package me.hsgamer.simplevaultperm.command;

import me.hsgamer.simplevaultperm.Permissions;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import me.hsgamer.simplevaultperm.object.SnapshotUser;
import me.hsgamer.simplevaultperm.object.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AdminCommand extends Command {
    protected final SimpleVaultPerm plugin;

    protected AdminCommand(SimpleVaultPerm plugin, String name) {
        super(name);
        this.plugin = plugin;
        setPermission(Permissions.ADMIN.getName());
    }

    protected AdminCommand(SimpleVaultPerm plugin, String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
        setPermission(Permissions.ADMIN.getName());
    }

    @SuppressWarnings("deprecation")
    protected User getUser(String player, boolean createIfNotExist) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player);
        return plugin.getUserManager().getUser(offlinePlayer.getUniqueId(), createIfNotExist);
    }

    @SuppressWarnings("deprecation")
    protected SnapshotUser getSnapshotUser(String player, boolean forceUpdate) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player);
        return plugin.getUserManager().getSnapshotUser(offlinePlayer.getUniqueId(), forceUpdate);
    }

    protected Group getGroup(String group, boolean createIfNotExist) {
        return plugin.getUserManager().getGroup(group, createIfNotExist);
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        return execute(sender, args);
    }
}
