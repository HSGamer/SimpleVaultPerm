package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddGroupCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public AddGroupCommand(SimpleVaultPerm plugin) {
        super("addgroup", "Add a group to a player", "/addgroup <player> <group> [duration] [override]", Collections.emptyList());
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
        long duration = -1;
        if (args.length > 2) {
            try {
                duration = Long.parseUnsignedLong(args[2]);
            } catch (Exception e) {
                // IGNORED
            }
        }
        boolean override = true;
        if (duration > 0) {
            if (plugin.getTimedGroupConfig().addGroup(player, group, duration, override)) {
                MessageUtils.sendMessage(sender, "&aSuccess");
                return true;
            } else {
                MessageUtils.sendMessage(sender, "&cCannot add timed group to the player");
                return false;
            }
        } else if (plugin.getUserConfig().addGroup(player, group)) {
            MessageUtils.sendMessage(sender, "&aSuccess");
            return true;
        } else {
            MessageUtils.sendMessage(sender, "&cCannot add group to the player");
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return new ArrayList<>(plugin.getGroupConfig().getGroups());
        } else {
            return Collections.emptyList();
        }
    }
}
