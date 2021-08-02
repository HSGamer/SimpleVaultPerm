package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public void reload() {
        config.reload();
    }

    public void addPlayer(Player player) {
        timedPlayerMap.put(player.getName(), new TimedPlayer(player.getName()));
    }

    public void removePlayer(Player player) {
        Optional.ofNullable(timedPlayerMap.remove(player.getName()))
                .filter(timedPlayer -> !timedPlayer.isCancelled())
                .ifPresent(TimedPlayer::cancel);
    }

    public void clearAllPlayers() {
        timedPlayerMap.values().forEach(TimedPlayer::cancel);
        timedPlayerMap.clear();
    }

    public Map<String, Long> getTimedGroups(String player) {
        return Collections.emptyMap();
    }

    public List<String> getGroups(String player) {
        return new ArrayList<>(getTimedGroups(player).keySet());
    }

    public boolean addGroup(String player, String group, long duration) {

        plugin.getPermissionManager().reloadPermissions(player);
        return true;
    }

    public boolean removeGroup(String player, String group) {

        plugin.getPermissionManager().reloadPermissions(player);
        return true;
    }

    private class TimedPlayer extends BukkitRunnable {
        private final Map<String, Long> timedGroupMap = new HashMap<>();
        private final String player;

        private TimedPlayer(String player) {
            this.player = player;
        }

        @Override
        public void run() {

            plugin.getPermissionManager().reloadPermissions(player);
        }

        public Map<String, Long> getTimedGroupMap() {
            return timedGroupMap;
        }
    }
}
