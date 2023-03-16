package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GroupInfoCommand extends AdminCommand {
    public GroupInfoCommand(SimpleVaultPerm plugin) {
        super(plugin, "groupinfo", "Show the info of the group", "/groupinfo <group>", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String group = args[0];
        Group groupObject = getGroup(group, false);
        if (groupObject == null) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupNotFound());
            return false;
        }

        Map<String, Boolean> permissionMap = groupObject.getPermissions();
        String prefix = groupObject.getPrefix();
        String suffix = groupObject.getSuffix();

        if (permissionMap == null) {
            permissionMap = Collections.emptyMap();
        }
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }

        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupInfoPermissionTitle());
        permissionMap.forEach((k, v) -> MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupInfoPermissionFormat().replace("{permission}", k).replace("{state}", Boolean.toString(v))));
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupInfoPrefix().replace("{prefix}", prefix));
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupInfoSuffix().replace("{suffix}", suffix));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getUserManager().getGroupNames());
        } else {
            return Collections.emptyList();
        }
    }
}
