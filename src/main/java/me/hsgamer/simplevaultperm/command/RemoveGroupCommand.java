package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RemoveGroupCommand extends AdminCommand {
    public RemoveGroupCommand(SimpleVaultPerm plugin) {
        super(plugin, "removegroup", "Remove a group from a player", "/removegroup <player> <group> [isTimed]", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];
        String group = args[1];
        boolean isTimed = false;
        if (args.length > 2) {
            isTimed = Boolean.parseBoolean(args[2]);
        }

        User user = getUser(player, false);
        if (user == null) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerNotFound());
            return false;
        }

        if (isTimed) {
            if (!user.removeTimedGroup(group)) {
                MessageUtils.sendMessage(sender, plugin.getMessageConfig().getCannotRemoveTimedGroup());
                return false;
            }
        } else if (!user.removeGroup(group)) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getCannotRemoveGroup());
            return false;
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
            return Optional.ofNullable(getUser(args[0], false)).map(User::getGroups).orElse(Collections.emptyList());
        } else if (args.length == 3) {
            return Arrays.asList("true", "false");
        } else {
            return Collections.emptyList();
        }
    }
}
