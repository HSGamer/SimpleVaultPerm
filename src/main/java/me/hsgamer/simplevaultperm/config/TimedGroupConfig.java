package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimedGroupConfig {
    private final Map<String, TimedPlayer> timedPlayerMap = new ConcurrentHashMap<>();
    private final BukkitConfig config;
    private final SimpleVaultPerm plugin;

    public TimedGroupConfig(SimpleVaultPerm plugin) {
        this.plugin = plugin;
        this.config = new BukkitConfig(plugin, "timed-groups.yml");
    }

    public void setup() {
        config.setup();
    }

    private static long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public void reload() {
        clearAllPlayers();
        config.reload();
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
        config.save();
    }

    public List<String> getGroups(String player) {
        return new ArrayList<>(getTimedGroups(player).keySet());
    }

    public Map<String, Long> getTimedGroups(String player) {
        Map<String, Long> map = new LinkedHashMap<>();
        config.getNormalizedValues(player, false).forEach((key, value) -> map.put(key, Long.parseUnsignedLong(String.valueOf(value))));
        return map;
    }

    private String formatGroupPath(String player, String group) {
        return player + "." + group;
    }

    public boolean addGroup(String player, String group, long duration, boolean override) {
        if (duration < 0) {
            return false;
        }
        Optional<TimedPlayer> optional = Optional.ofNullable(timedPlayerMap.get(player));
        if (optional.isPresent()) {
            TimedPlayer timedPlayer = optional.get();
            long time = duration;
            time += override ? getCurrentMillis() : timedPlayer.timedGroupMap.getOrDefault(group, getCurrentMillis());
            timedPlayer.timedGroupMap.put(group, time);
            timedPlayer.needUpdate.set(true);
        } else {
            String path = formatGroupPath(player, group);
            long time = duration;
            time += override ? getCurrentMillis() : config.getInstance(path, getCurrentMillis(), Number.class).longValue();
            config.set(path, time);
            config.save();
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
            if (config.contains(path)) {
                config.remove(path);
                config.save();
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
            long currentTime = getCurrentMillis();
            if (timedGroupMap.entrySet().removeIf(entry -> entry.getValue() < currentTime)) {
                needUpdate.set(true);
            }
            if (needUpdate.get()) {
                config.remove(player);
                if (!timedGroupMap.isEmpty()) {
                    timedGroupMap.forEach((key, value) -> config.set(formatGroupPath(player, key), value));
                }
                config.save();
                plugin.getPermissionManager().reloadPermissions(player);
                needUpdate.set(false);
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            config.remove(player);
            if (!timedGroupMap.isEmpty()) {
                timedGroupMap.forEach((key, value) -> config.set(formatGroupPath(player, key), value));
            }
        }

        public void cancelAndSave() {
            cancel();
            config.save();
        }
    }
}
