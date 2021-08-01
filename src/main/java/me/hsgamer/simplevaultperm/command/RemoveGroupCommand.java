package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class RemoveGroupCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public RemoveGroupCommand(SimpleVaultPerm plugin) {
        super("removegroup", "Remove a group from a player", "/removegroup <player> <group>", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];
        String group = args[1];
        if (plugin.getUserConfig().removeGroup(player, group)) {
            MessageUtils.sendMessage(sender, "&aSuccess");
            return true;
        } else {
            MessageUtils.sendMessage(sender, "&cCannot remove group from the player");
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        } else if (args.length == 2) {
            return plugin.getUserConfig().getGroups(args[0]);
        } else {
            return Collections.emptyList();
        }
    }
}
