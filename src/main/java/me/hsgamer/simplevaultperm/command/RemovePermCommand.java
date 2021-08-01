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

public class RemovePermCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public RemovePermCommand(SimpleVaultPerm plugin) {
        super("removeperm", "Remove a permission from a player", "/removeperm <player> <permission>", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];
        String permission = args[1];
        if (plugin.getUserConfig().removePermission(player, permission)) {
            MessageUtils.sendMessage(sender, "&aSuccess");
            return true;
        } else {
            MessageUtils.sendMessage(sender, "&cCannot remove permission from the player");
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return new ArrayList<>(plugin.getUserConfig().getPermissionMap(args[0]).keySet());
        } else {
            return Collections.emptyList();
        }
    }
}
