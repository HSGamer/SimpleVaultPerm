package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import org.bukkit.command.CommandSender;

import java.util.*;

public class RemoveGroupPermCommand extends AdminCommand {
    public RemoveGroupPermCommand(SimpleVaultPerm plugin) {
        super(plugin, "removegroupperm", "Remove a permission from a group", "/removegroupperm <group> <permission>", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String group = args[0];
        String permission = args[1];

        Group groupObject = getGroup(group, false);
        if (groupObject == null) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupNotFound());
            return false;
        }

        if (!groupObject.removePermission(permission)) {
            MessageUtils.sendMessage(sender, "&cCannot remove permission from the group");
            return false;
        }

        groupObject.setUpdateRequire(true);
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getSuccess());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getUserManager().getGroupNames());
        } else if (args.length == 2) {
            return Optional.ofNullable(getGroup(args[0], false)).map(Group::getPermissions).map(Map::keySet).map(ArrayList::new).orElseGet(ArrayList::new);
        } else {
            return Collections.emptyList();
        }
    }
}
