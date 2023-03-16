package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ReloadCommand extends AdminCommand {
    public ReloadCommand(SimpleVaultPerm plugin) {
        super(plugin, "reloadperms", "Reload the plugin", "/reloadperms", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.getMainConfig().reloadConfig();
        plugin.getMessageConfig().reloadConfig();
        plugin.getUserManager().clearSnapshot();
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getSuccess());
        return true;
    }
}
