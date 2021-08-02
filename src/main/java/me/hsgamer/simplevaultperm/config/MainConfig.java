package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.LongConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class MainConfig extends PathableConfig {
    public static final StringConfigPath DEFAULT_GROUP = new StringConfigPath("default-group", "default");
    public static final BooleanConfigPath TIMED_GROUP_CHECK_ASYNC = new BooleanConfigPath("timed-group-check.async", true);
    public static final LongConfigPath TIMED_GROUP_CHECK_PERIOD = new LongConfigPath("timed-group-check.period", 10L);

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
