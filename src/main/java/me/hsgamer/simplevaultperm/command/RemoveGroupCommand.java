package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveGroupCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public RemoveGroupCommand(SimpleVaultPerm plugin) {
        super("removegroup", "Remove a group from a player", "/removegroup <player> <group> [isTimed]", Collections.emptyList());
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
        boolean isTimed = false;
        if (args.length > 2) {
            isTimed = Boolean.parseBoolean(args[2]);
        }
        if (isTimed) {
            if (plugin.getTimedGroupConfig().removeGroup(player, group)) {
                MessageUtils.sendMessage(sender, "&aSuccess");
                return true;
            } else {
                MessageUtils.sendMessage(sender, "&cCannot remove timed group from the player");
                return false;
            }
        } else if (plugin.getUserConfig().removeGroup(player, group)) {
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
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return plugin.getUserConfig().getGroups(args[0]);
        } else if (args.length == 3) {
            return Arrays.asList("true", "false");
        } else {
            return Collections.emptyList();
        }
    }
}
