package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GroupConfig extends BukkitConfig {
    private final SimpleVaultPerm plugin;

    public GroupConfig(SimpleVaultPerm plugin) {
        super(plugin, "groups.yml");
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        this.getOriginal().options().pathSeparator('/');
        super.setup();
    }

    public Collection<String> getGroups() {
        return plugin.getGroupConfig().getKeys(false);
    }

    private String formatPath(String group, String permission) {
        return String.join("/", group, permission);
    }

    public boolean getPermissionState(String group, String permission) {
        String path = formatPath(group, permission);
        if (contains(path)) {
            return getInstance(path, false, Boolean.class);
        }
        return getInstance(formatPath(MainConfig.DEFAULT_GROUP.getValue(), permission), false, Boolean.class);
    }

    public Map<String, Boolean> getPermissionMap(String group) {
        Map<String, Boolean> map = new HashMap<>();
        getNormalizedValues(group, false).forEach((k, v) -> {
            if (v instanceof Boolean) {
                map.put(k, (Boolean) v);
            }
        });
        return map;
    }

    public boolean addPermission(String group, String permission, boolean state) {
        String path = formatPath(group, permission);
        if (contains(path) && getInstance(path, false, Boolean.class) == state) {
            return false;
        }
        set(path, state);
        this.save();
        plugin.getPermissionManager().reloadAllPermissions();
        return true;
    }

    public boolean removePermission(String group, String permission) {
        String path = formatPath(group, permission);
        if (!contains(path)) {
            return false;
        }
        remove(path);
        this.save();
        plugin.getPermissionManager().reloadAllPermissions();
        return true;
    }
}
