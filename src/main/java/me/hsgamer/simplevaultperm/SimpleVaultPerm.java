package me.hsgamer.simplevaultperm;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.command.*;
import me.hsgamer.simplevaultperm.config.GroupConfig;
import me.hsgamer.simplevaultperm.config.MainConfig;
import me.hsgamer.simplevaultperm.config.UserConfig;
import me.hsgamer.simplevaultperm.hook.VaultPermissionHook;
import me.hsgamer.simplevaultperm.listener.PlayerListener;
import me.hsgamer.simplevaultperm.manager.PermissionManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public final class SimpleVaultPerm extends BasePlugin {
    private final MainConfig mainConfig = new MainConfig(this);
    private final GroupConfig groupConfig = new GroupConfig(this);
    private final UserConfig userConfig = new UserConfig(this);
    private final PermissionManager permissionManager = new PermissionManager(this);

    @Override
    public void load() {
        MessageUtils.setPrefix("&f[&6SimpleVaultPerm&f] &7");
    }

    @Override
    public void enable() {
        mainConfig.setup();
        groupConfig.setup();
        userConfig.setup();
        registerListener(new PlayerListener(this));

        registerCommand(new AddGroupCommand(this));
        registerCommand(new AddGroupPermCommand(this));
        registerCommand(new AddPermCommand(this));
        registerCommand(new ReloadCommand(this));
        registerCommand(new RemoveGroupCommand(this));
        registerCommand(new RemoveGroupPermCommand(this));
        registerCommand(new RemovePermCommand(this));
        registerCommand(new GroupInfoCommand(this));
        registerCommand(new PlayerPermInfoCommand(this));

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getServer().getServicesManager().register(
                    Permission.class,
                    new VaultPermissionHook(this),
                    this,
                    ServicePriority.High
            );
        }
    }

    @Override
    public void postEnable() {
        getServer().getOnlinePlayers().forEach(permissionManager::addPermissions);
    }

    @Override
    public void disable() {
        permissionManager.clearAllPermissions();
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public GroupConfig getGroupConfig() {
        return groupConfig;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
}
