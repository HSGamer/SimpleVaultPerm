package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GroupInfoCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public GroupInfoCommand(SimpleVaultPerm plugin) {
        super("groupinfo", "Show the info of the group", "/groupinfo <group>", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String group = args[0];
        Map<String, Boolean> permissionMap = plugin.getGroupConfig().getPermissionMap(group);

        MessageUtils.sendMessage(sender, "&ePermissions");
        permissionMap.forEach((k, v) -> MessageUtils.sendMessage(sender, "  &f" + k + "&7: &b" + v));

        MessageUtils.sendMessage(sender, "&ePrefix: &f" + plugin.getChatConfig().getGroupPrefix(group));
        MessageUtils.sendMessage(sender, "&eSuffix: &f" + plugin.getChatConfig().getGroupSuffix(group));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getGroupConfig().getGroups());
        } else {
            return Collections.emptyList();
        }
    }
}
