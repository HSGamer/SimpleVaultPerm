package me.hsgamer.simplevaultperm;

import lombok.Getter;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.simplevaultperm.command.*;
import me.hsgamer.simplevaultperm.config.MainConfig;
import me.hsgamer.simplevaultperm.config.MessageConfig;
import me.hsgamer.simplevaultperm.hook.PlaceholderHook;
import me.hsgamer.simplevaultperm.hook.VaultChatHook;
import me.hsgamer.simplevaultperm.hook.VaultPermissionHook;
import me.hsgamer.simplevaultperm.listener.PlayerListener;
import me.hsgamer.simplevaultperm.manager.UserManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.ServicePriority;

import java.util.Collections;
import java.util.List;

@Getter
public final class SimpleVaultPerm extends BasePlugin {
    private final MainConfig mainConfig = ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this, "config.yml"));
    private final MessageConfig messageConfig = ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "message.yml"));
    private final UserManager userManager = new UserManager(this);

    @Override
    public void load() {
        MessageUtils.setPrefix(messageConfig::getPrefix);
    }

    @Override
    public void enable() {
        userManager.setup();

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

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            VaultPermissionHook permissionHook = new VaultPermissionHook(this);
            VaultChatHook chatHook = new VaultChatHook(this, permissionHook);
            registerProvider(Permission.class, permissionHook, ServicePriority.High);
            registerProvider(Chat.class, chatHook, ServicePriority.High);
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderHook placeholderHook = new PlaceholderHook(this);
            placeholderHook.register();
            addDisableFunction(placeholderHook::unregister);
        }
    }

    @Override
    public void disable() {
        userManager.clear();
    }

    @Override
    protected List<Class<?>> getPermissionClasses() {
        return Collections.singletonList(Permissions.class);
    }
}
