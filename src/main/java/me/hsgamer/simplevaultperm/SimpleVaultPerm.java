package me.hsgamer.simplevaultperm;

import lombok.Getter;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.simplevaultperm.config.MainConfig;
import me.hsgamer.simplevaultperm.config.MessageConfig;
import me.hsgamer.simplevaultperm.manager.UserManager;

@Getter
public final class SimpleVaultPerm extends BasePlugin {
    private final MainConfig mainConfig = ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this, "config.yml"));
    private final MessageConfig messageConfig = ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "message.yml"));
    private final UserManager userManager = new UserManager(this);

    @Override
    public void enable() {
        userManager.setup();
    }

    @Override
    public void disable() {
        userManager.clear();
    }
}
