package me.hsgamer.simplevaultperm.object;

import lombok.Data;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.simplevaultperm.util.ValueUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Data
public class User {
    private final UUID uuid;
    private final Object lock = new Object();

    // User data
    private List<String> groups;
    private Map<String, Long> timedGroups;
    private Map<String, Boolean> permissions;

    // Cached data
    private PermissionAttachment permissionAttachment;
    private Map<String, Boolean> cachedPermissions;
    private String prefix;
    private String suffix;

    public static User fromMap(UUID uuid, Map<String, Object> map) {
        User user = new User(uuid);
        Optional.ofNullable(map.get("groups")).map(CollectionUtils::createStringListFromObject).ifPresent(user::setGroups);
        Optional.ofNullable(map.get("permissions")).map(CollectionUtils::createStringListFromObject).map(ValueUtil::toBooleanMap).ifPresent(user::setPermissions);
        Optional.ofNullable(map.get("timed-groups")).map(ValueUtil::toLongMap).ifPresent(user::setTimedGroups);
        return user;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("groups", groups);
        map.put("permissions", ValueUtil.toStringList(permissions));
        map.put("timed-groups", timedGroups);
        return map;
    }

    public void applyPermissions() {
        synchronized (lock) {
            if (permissionAttachment != null) {
                permissionAttachment.remove();
            }

            Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
            Player player = plugin.getServer().getPlayer(uuid);
            if (player == null) {
                throw new IllegalStateException("Player is not online");
            }

            if (cachedPermissions != null) {
                PermissionAttachment attachment = player.addAttachment(plugin);
                cachedPermissions.forEach(attachment::setPermission);
                permissionAttachment = attachment;
            }
        }
    }

    public void removePermissions() {
        synchronized (lock) {
            if (permissionAttachment != null) {
                permissionAttachment.remove();
                permissionAttachment = null;
            }
            if (cachedPermissions != null) {
                cachedPermissions.clear();
                cachedPermissions = null;
            }
            prefix = null;
            suffix = null;
        }
    }

    public List<String> getFinalGroups() {
        List<String> finalGroups = new ArrayList<>(groups);
        timedGroups.forEach((group, time) -> {
            if (time > System.currentTimeMillis()) {
                finalGroups.add(group);
            }
        });
        return finalGroups;
    }

    public void setPermission(String permission, boolean value) {
        if (permissions == null) {
            permissions = new HashMap<>();
        }
        permissions.put(permission, value);
    }

    public void removePermission(String permission) {
        if (permissions != null) {
            permissions.remove(permission);
        }
    }

    public void setTimedGroup(String group, long time) {
        if (timedGroups == null) {
            timedGroups = new HashMap<>();
        }
        timedGroups.put(group, time);
    }

    public void removeTimedGroup(String group) {
        if (timedGroups != null) {
            timedGroups.remove(group);
        }
    }

    /**
     * Clear the expired timed groups
     *
     * @return the list of expired groups
     */
    public List<String> clearExpiredTimedGroups() {
        if (timedGroups == null) {
            return Collections.emptyList();
        }
        List<String> expiredGroups = new ArrayList<>();
        timedGroups.forEach((group, time) -> {
            if (time <= System.currentTimeMillis()) {
                expiredGroups.add(group);
            }
        });
        expiredGroups.forEach(timedGroups::remove);
        return expiredGroups;
    }
}
