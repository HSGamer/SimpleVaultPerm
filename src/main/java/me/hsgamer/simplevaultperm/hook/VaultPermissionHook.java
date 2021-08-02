package me.hsgamer.simplevaultperm.hook;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.config.MainConfig;
import net.milkbowl.vault.permission.Permission;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return plugin.getUserConfig().getPermissionState(player, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return plugin.getUserConfig().addPermission(player, permission, true);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return plugin.getUserConfig().removePermission(player, permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return plugin.getGroupConfig().getPermissionState(group, permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return plugin.getGroupConfig().addPermission(group, permission, true);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return plugin.getGroupConfig().removePermission(group, permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        if (group.equals(MainConfig.DEFAULT_GROUP.getValue())) {
            return true;
        }
        return plugin.getUserConfig().getGroups(player).contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return plugin.getUserConfig().addGroup(player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return plugin.getUserConfig().removeGroup(player, group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Set<String> set = new LinkedHashSet<>();
        set.add(MainConfig.DEFAULT_GROUP.getValue());
        set.addAll(plugin.getUserConfig().getGroups(player));
        set.addAll(plugin.getTimedGroupConfig().getGroups(player));
        return set.toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        String[] groups = getPlayerGroups(world, player);
        return groups.length > 0 ? groups[groups.length - 1] : MainConfig.DEFAULT_GROUP.getValue();
    }

    @Override
    public String[] getGroups() {
        return plugin.getGroupConfig().getGroups().toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
