package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimedGroupConfig extends BukkitConfig {
    private final Map<String, TimedPlayer> timedPlayerMap = new ConcurrentHashMap<>();
    private final SimpleVaultPerm plugin;

    public TimedGroupConfig(SimpleVaultPerm plugin) {
        super(plugin, "timed-groups.yml");
        this.plugin = plugin;
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String displayDuration(long time) {
        return DurationFormatUtils.formatDuration(time, "HH:mm:ss", true);
    }

    @Override
    public void reload() {
        clearAllPlayers();
        super.reload();
        Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::addPlayer);
    }

    public void addPlayer(String player) {
        timedPlayerMap.put(player, new TimedPlayer(player));
    }

    public void removePlayer(String player) {
        Optional.ofNullable(timedPlayerMap.remove(player))
                .filter(timedPlayer -> !timedPlayer.isCancelled())
                .ifPresent(TimedPlayer::cancelAndSave);
    }

    public void clearAllPlayers() {
        timedPlayerMap.values().forEach(TimedPlayer::cancel);
        timedPlayerMap.clear();
        save();
    }

    public List<String> getGroups(String player) {
        return new ArrayList<>(getTimedGroups(player).keySet());
    }

    public Map<String, Long> getTimedGroups(String player) {
        Map<String, Long> map = new LinkedHashMap<>();
        getNormalizedValues(player, false).forEach((key, value) -> map.put(key, Long.parseUnsignedLong(String.valueOf(value))));
        return map;
    }

    private String formatGroupPath(String player, String group) {
        return player + "." + group;
    }

    public long getTimeLeft(String player, String group) {
        long current = getCurrentTime();
        long time = getInstance(formatGroupPath(player, group), current, Number.class).longValue();
        return time < current ? 0 : time - current;
    }

    public boolean addGroup(String player, String group, long duration, boolean override) {
        if (duration < 0) {
            return false;
        }
        Optional<TimedPlayer> optional = Optional.ofNullable(timedPlayerMap.get(player));
        if (optional.isPresent()) {
            TimedPlayer timedPlayer = optional.get();
            long time = duration;
            time += override ? getCurrentTime() : timedPlayer.timedGroupMap.getOrDefault(group, getCurrentTime());
            timedPlayer.timedGroupMap.put(group, time);
            timedPlayer.needUpdate.set(true);
        } else {
            String path = formatGroupPath(player, group);
            long time = duration;
            time += override ? getCurrentTime() : getInstance(path, getCurrentTime(), Number.class).longValue();
            set(path, time);
            save();
        }
        return true;
    }

    public boolean removeGroup(String player, String group) {
        Optional<TimedPlayer> optional = Optional.ofNullable(timedPlayerMap.get(player));
        if (optional.isPresent()) {
            TimedPlayer timedPlayer = optional.get();
            if (timedPlayer.timedGroupMap.containsKey(group)) {
                timedPlayer.timedGroupMap.remove(group);
                timedPlayer.needUpdate.set(true);
                return true;
            }
        } else {
            String path = formatGroupPath(player, group);
            if (contains(path)) {
                remove(path);
                save();
                return true;
            }
        }
        return false;
    }

    private class TimedPlayer extends BukkitRunnable {
        private final Map<String, Long> timedGroupMap = new HashMap<>();
        private final AtomicBoolean needUpdate = new AtomicBoolean();
        private final String player;

        private TimedPlayer(String player) {
            this.player = player;
            timedGroupMap.putAll(getTimedGroups(player));
            boolean async = MainConfig.TIMED_GROUP_CHECK_ASYNC.getValue();
            long update = MainConfig.TIMED_GROUP_CHECK_PERIOD.getValue();
            if (async) {
                runTaskTimerAsynchronously(plugin, update, update);
            } else {
                runTaskTimer(plugin, update, update);
            }
        }

        @Override
        public void run() {
            long currentTime = getCurrentTime();
            if (timedGroupMap.entrySet().removeIf(entry -> entry.getValue() < currentTime)) {
                needUpdate.set(true);
            }
            if (needUpdate.get()) {
                remove(player);
                if (!timedGroupMap.isEmpty()) {
                    timedGroupMap.forEach((key, value) -> set(formatGroupPath(player, key), value));
                }
                save();
                plugin.getPermissionManager().reloadPermissions(player);
                needUpdate.set(false);
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            remove(player);
            if (!timedGroupMap.isEmpty()) {
                timedGroupMap.forEach((key, value) -> set(formatGroupPath(player, key), value));
            }
        }

        public void cancelAndSave() {
            cancel();
            save();
        }
    }
}
