package me.hsgamer.simplevaultperm.hook;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import me.hsgamer.simplevaultperm.object.SnapshotUser;
import me.hsgamer.simplevaultperm.object.User;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class VaultPermissionHook extends Permission {
    private final SimpleVaultPerm plugin;

    public VaultPermissionHook(SimpleVaultPerm plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "SimpleVaultPerm";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @SuppressWarnings("deprecation")
    private SnapshotUser getUser(String player) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player);
        return plugin.getUserManager().getSnapshotUser(offlinePlayer.getUniqueId(), false);
    }

    private Group getGroup(String group, boolean createIfNotExist) {
        return plugin.getUserManager().getGroup(group, createIfNotExist);
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return getUser(player).getPermissions().getOrDefault(permission, false);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        User user = getUser(player).getUser();
        user.setPermission(permission, true);
        user.setUpdateRequire(true);
        return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        User user = getUser(player).getUser();
        if (user != null && user.removePermission(permission)) {
            user.setUpdateRequire(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return Optional.ofNullable(getGroup(group, false)).map(Group::getPermissions).map(map -> map.get(permission)).orElse(false);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        Group groupObject = getGroup(group, true);
        groupObject.setPermission(permission, true);
        groupObject.setUpdateRequire(true);
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        Group groupObject = getGroup(group, false);
        if (groupObject != null && groupObject.removePermission(permission)) {
            groupObject.setUpdateRequire(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return getUser(player).getGroups().contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        User user = getUser(player).getUser();
        user.addGroup(group);
        user.setUpdateRequire(true);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        User user = getUser(player).getUser();
        if (user != null && user.removeGroup(group)) {
            user.setUpdateRequire(true);
            return true;
        }
        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return getUser(player).getGroups().toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        String[] groups = getPlayerGroups(world, player);
        return groups.length > 0 ? groups[groups.length - 1] : plugin.getMainConfig().getDefaultGroup();
    }

    @Override
    public String[] getGroups() {
        return plugin.getUserManager().getGroupNames().toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
