package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerPermInfoCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public PlayerPermInfoCommand(SimpleVaultPerm plugin) {
        super("playerperminfo", "Show the permissions info of the player", "/playerperminfo <player>", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];

        MessageUtils.sendMessage(sender, "&ePermissions");
        Map<String, Boolean> permissionMap = plugin.getUserConfig().getPermissionMap(player);
        permissionMap.forEach((k, v) -> MessageUtils.sendMessage(sender, "  &f" + k + "&7: &b" + v));

        MessageUtils.sendMessage(sender, "&eGroups");
        plugin.getUserConfig().getGroups(player).forEach(group -> MessageUtils.sendMessage(sender, "  &f" + group));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
