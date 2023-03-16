package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.SnapshotUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerPermInfoCommand extends AdminCommand {
    public PlayerPermInfoCommand(SimpleVaultPerm plugin) {
        super(plugin, "playerperminfo", "Show the permissions info of the player", "/playerperminfo <player>", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];

        SnapshotUser snapshotUser = getSnapshotUser(player, false);

        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupInfoPermissionTitle());
        snapshotUser.getPermissions().forEach((k, v) -> MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGroupInfoPermissionFormat().replace("{permission}", k).replace("{state}", v ? "true" : "false")));

        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerInfoGroupTitle());
        snapshotUser.getGroups().forEach(group -> MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerInfoGroupFormat().replace("{group}", group)));

        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerInfoPrefix().replace("{prefix}", snapshotUser.getPrefix()));
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerInfoSuffix().replace("{suffix}", snapshotUser.getSuffix()));
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
