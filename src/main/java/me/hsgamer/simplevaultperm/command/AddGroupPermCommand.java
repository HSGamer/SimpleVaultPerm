package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddGroupPermCommand extends AdminCommand {
    public AddGroupPermCommand(SimpleVaultPerm plugin) {
        super(plugin, "addgroupperm", "Add a permission to a group", "/addgroupperm <group> <permission> [state]", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String group = args[0];
        String permission = args[1];
        boolean state = true;
        if (args.length > 2) {
            state = Boolean.parseBoolean(args[2]);
        }
        Group groupObject = getGroup(group, true);
        groupObject.setPermission(permission, state);
        groupObject.setUpdateRequire(true);
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getSuccess());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getUserManager().getGroupNames());
        } else if (args.length == 3) {
            return Arrays.asList("true", "false");
        } else {
            return Collections.emptyList();
        }
    }
}
