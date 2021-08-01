package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ReloadCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public ReloadCommand(SimpleVaultPerm plugin) {
        super("reloadperms", "Reload the plugin", "/reloadperms", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.getMainConfig().reload();
        plugin.getGroupConfig().reload();
        plugin.getUserConfig().reload();
        plugin.getPermissionManager().reloadAllPermissions();
        MessageUtils.sendMessage(sender, "&aSuccess");
        return true;
    }
}
