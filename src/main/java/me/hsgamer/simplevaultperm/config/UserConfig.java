package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;

import java.util.*;

public class UserConfig extends BukkitConfig {
    private final SimpleVaultPerm plugin;

    public UserConfig(SimpleVaultPerm plugin) {
        super(plugin, "users.yml");
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.getOriginal().options().pathSeparator('/');
        super.setup();
    }

    private String formatGroupPath(String player) {
        return player + "/group";
    }

    private String formatPermissionsPath(String player) {
        return player + "/permission";
    }

    private String formatPermissionPath(String player, String permission) {
        return String.join("/", formatPermissionsPath(player), permission);
    }

    public boolean getPermissionState(String player, String permission) {
        String permissionPath = formatPermissionPath(player, permission);
        if (contains(permissionPath)) {
            return getInstance(permissionPath, false, Boolean.class);
        }
        List<String> groups = getGroups(player);
        if (!groups.isEmpty() && groups.parallelStream().anyMatch(group -> plugin.getGroupConfig().getPermissionState(group, permission))) {
            return true;
        }
        return plugin.getGroupConfig().getPermissionState(MainConfig.DEFAULT_GROUP.getValue(), permission);
    }

    public Map<String, Boolean> getPermissionMap(String player) {
        String path = formatPermissionsPath(player);
        Map<String, Boolean> map = new HashMap<>();
        getNormalizedValues(path, false).forEach((k, v) -> {
            if (v instanceof Boolean) {
                map.put(k, (Boolean) v);
            }
        });
        return map;
    }

    public List<String> getGroups(String player) {
        String path = formatGroupPath(player);
        if (contains(path)) {
            return CollectionUtils.createStringListFromObject(getNormalized(path), true);
        } else {
            return Collections.emptyList();
        }
    }

    public boolean addPermission(String player, String permission, boolean state) {
        String path = formatPermissionPath(player, permission);
        if (contains(path) && getInstance(path, false, Boolean.class) == state) {
            return false;
        }
        set(path, state);
        this.save();
        plugin.getPermissionManager().reloadPermissions(player);
        return true;
    }

    public boolean removePermission(String player, String permission) {
        String path = formatPermissionPath(player, permission);
        if (!contains(path)) {
            return false;
        }
        remove(path);
        this.save();
        plugin.getPermissionManager().reloadPermissions(player);
        return true;
    }

    public boolean addGroup(String player, String group) {
        List<String> groups = new ArrayList<>(getGroups(player));
        if (groups.contains(group)) {
            return false;
        }
        groups.add(group);
        set(formatGroupPath(player), groups);
        this.save();
        plugin.getPermissionManager().reloadPermissions(player);
        return true;
    }

    public boolean removeGroup(String player, String group) {
        List<String> groups = new ArrayList<>(getGroups(player));
        if (!groups.contains(group)) {
            return false;
        }
        groups.remove(group);
        set(formatGroupPath(player), groups);
        this.save();
        plugin.getPermissionManager().reloadPermissions(player);
        return true;
    }
}
