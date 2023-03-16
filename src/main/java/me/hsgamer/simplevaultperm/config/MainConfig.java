package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface MainConfig {
    @ConfigPath("default-group")
    default String getDefaultGroup() {
        return "default";
    }

    @ConfigPath("update-task.interval")
    default int getUpdateInterval() {
        return 60;
    }

    @ConfigPath("update-task.save-on-update")
    default boolean isSaveOnUpdate() {
        return false;
    }
}
