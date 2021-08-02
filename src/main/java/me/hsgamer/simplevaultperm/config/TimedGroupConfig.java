package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TimedGroupConfig {
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

    public Map<String, Long> getTimedGroups(String player) {
        return Collections.emptyMap();
    }

    public List<String> getGroups(String player) {
        return Collections.emptyList();
    }

    public void addGroup(String player, String group, long duration) {

    }

    public void removeGroup(String player, String group) {

    }
}
