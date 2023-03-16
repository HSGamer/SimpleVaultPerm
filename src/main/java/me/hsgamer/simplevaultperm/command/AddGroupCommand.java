package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.User;
import me.hsgamer.simplevaultperm.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddGroupCommand extends AdminCommand {
    public AddGroupCommand(SimpleVaultPerm plugin) {
        super(plugin, "addgroup", "Add a group to a player", "/addgroup <player> <group> [duration] [relative]", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];
        String group = args[1];
        long duration = -1;
        if (args.length > 2) {
            duration = TimeUtil.toMillis(args[2]);
        }
        boolean relative = false;
        if (args.length > 3) {
            relative = Boolean.parseBoolean(args[3]);
        }

        User user = getUser(player, true);

        if (duration > 0) {
            user.setTimedGroup(group, duration, relative);
        } else {
            user.addGroup(group);
        }
        user.setUpdateRequire(true);
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getSuccess());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return new ArrayList<>(plugin.getUserManager().getGroupNames());
        } else {
            return Collections.emptyList();
        }
    }
}
