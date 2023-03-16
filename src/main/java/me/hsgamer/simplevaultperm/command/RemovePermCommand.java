package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RemovePermCommand extends AdminCommand {
    public RemovePermCommand(SimpleVaultPerm plugin) {
        super(plugin, "removeperm", "Remove a permission from a player", "/removeperm <player> <permission>", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];
        String permission = args[1];

        User user = getUser(player, false);
        if (user == null) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerNotFound());
            return false;
        }

        if (!user.removePermission(permission)) {
            MessageUtils.sendMessage(sender, "&cCannot remove permission from the player");
            return false;
        }

        user.setUpdateRequire(true);
        MessageUtils.sendMessage(sender, "&aSuccess");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return Optional.ofNullable(getUser(args[0], false)).map(User::getPermissions).map(Map::keySet).map(ArrayList::new).orElseGet(ArrayList::new);
        } else {
            return Collections.emptyList();
        }
    }
}
