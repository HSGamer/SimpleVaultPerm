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
    private final Object attachmentLock = new Object();
    private final Object groupLock = new Object();
    private final Object permissionLock = new Object();
    private final Object timedGroupLock = new Object();

    // User data
    private List<String> groups;
    private Map<String, Long> timedGroups;
    private Map<String, Boolean> permissions;
    private String prefix;
    private String suffix;

    // Cached data
    private boolean updateRequire;
    private PermissionAttachment permissionAttachment;
    private List<String> cachedGroups;
    private Map<String, Boolean> cachedPermissions;
    private String cachedPrefix;
    private String cachedSuffix;

    public static User fromMap(UUID uuid, Map<String, Object> map) {
        User user = new User(uuid);
        Optional.ofNullable(map.get("groups")).map(CollectionUtils::createStringListFromObject).ifPresent(user::setGroups);
        Optional.ofNullable(map.get("permissions")).map(CollectionUtils::createStringListFromObject).map(ValueUtil::toBooleanMap).ifPresent(user::setPermissions);
        Optional.ofNullable(map.get("timed-groups")).map(ValueUtil::toLongMap).ifPresent(user::setTimedGroups);
        Optional.ofNullable(map.get("prefix")).map(Objects::toString).ifPresent(user::setPrefix);
        Optional.ofNullable(map.get("suffix")).map(Objects::toString).ifPresent(user::setSuffix);
        return user;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Optional.ofNullable(groups).ifPresent(value -> map.put("groups", value));
        Optional.ofNullable(permissions).map(ValueUtil::toStringList).ifPresent(value -> map.put("permissions", value));
        Optional.ofNullable(timedGroups).ifPresent(value -> map.put("timed-groups", value));
        Optional.ofNullable(prefix).ifPresent(value -> map.put("prefix", value));
        Optional.ofNullable(suffix).ifPresent(value -> map.put("suffix", value));
        return map;
    }

    public void applyAttachment() {
        synchronized (attachmentLock) {
            if (permissionAttachment != null) {
                permissionAttachment.remove();
            }

            Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
            Player player = plugin.getServer().getPlayer(uuid);
            if (player == null) {
                return;
            }

            if (cachedPermissions != null) {
                PermissionAttachment attachment = player.addAttachment(plugin);
                cachedPermissions.forEach(attachment::setPermission);
                permissionAttachment = attachment;
            }
        }
    }

    public void removeAttachment() {
        synchronized (attachmentLock) {
            if (permissionAttachment != null) {
                permissionAttachment.remove();
                permissionAttachment = null;
            }
        }
    }

    public List<String> getFinalGroups() {
        List<String> finalGroups;
        synchronized (groupLock) {
            finalGroups = new ArrayList<>(groups);
        }
        synchronized (timedGroupLock) {
            if (timedGroups != null) {
                timedGroups.forEach((group, time) -> {
                    if (time > System.currentTimeMillis()) {
                        finalGroups.add(group);
                    }
                });
            }
        }
        return finalGroups;
    }

    public void setPermission(String permission, boolean value) {
        synchronized (permissionLock) {
            if (permissions == null) {
                permissions = new HashMap<>();
            }
            permissions.put(permission, value);
        }
    }

    public boolean removePermission(String permission) {
        synchronized (permissionLock) {
            if (permissions != null) {
                return permissions.remove(permission);
            }
            return false;
        }
    }

    public void addGroup(String group) {
        synchronized (groupLock) {
            if (groups == null) {
                groups = new ArrayList<>();
            }
            groups.add(group);
        }
    }

    public boolean removeGroup(String group) {
        synchronized (groupLock) {
            if (groups != null) {
                return groups.remove(group);
            }
            return false;
        }
    }

    public void setTimedGroup(String group, long duration) {
        synchronized (timedGroupLock) {
            if (timedGroups == null) {
                timedGroups = new HashMap<>();
            }
            timedGroups.put(group, System.currentTimeMillis() + duration);
        }
    }

    public boolean removeTimedGroup(String group) {
        synchronized (timedGroupLock) {
            if (timedGroups != null) {
                return timedGroups.remove(group) != null;
            }
            return false;
        }
    }

    /**
     * Clear the expired timed groups
     *
     * @return the list of expired groups
     */
    public List<String> clearExpiredTimedGroups() {
        synchronized (timedGroupLock) {
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
}
