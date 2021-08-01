package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveGroupPermCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public RemoveGroupPermCommand(SimpleVaultPerm plugin) {
        super("removegroupperm", "Remove a permission from a group", "/removegroupperm <group> <permission>", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String group = args[0];
        String permission = args[1];
        if (plugin.getGroupConfig().removePermission(group, permission)) {
            MessageUtils.sendMessage(sender, "&aSuccess");
            return true;
        } else {
            MessageUtils.sendMessage(sender, "&cCannot remove permission from the group");
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getGroupConfig().getGroups());
        } else if (args.length == 2) {
            return new ArrayList<>(plugin.getGroupConfig().getPermissionMap(args[0]).keySet());
        } else {
            return Collections.emptyList();
        }
    }
}
