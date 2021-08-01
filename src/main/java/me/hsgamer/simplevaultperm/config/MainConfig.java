package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class MainConfig extends PathableConfig {
    public static final StringConfigPath DEFAULT_GROUP = new StringConfigPath("default-group", "default");

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
