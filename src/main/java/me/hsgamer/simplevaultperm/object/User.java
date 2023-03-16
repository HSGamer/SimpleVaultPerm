package me.hsgamer.simplevaultperm.object;

import lombok.Data;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.simplevaultperm.util.ValueUtil;

import java.util.*;

@Data
public class User {
    private final UUID uuid;
    private List<String> groups;
    private Map<String, Long> timedGroups;
    private Map<String, Boolean> permissions;
    private String prefix;
    private String suffix;
    private boolean updateRequire;

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

    public List<String> getFinalGroups() {
        List<String> finalGroups = new ArrayList<>(groups);
        if (timedGroups != null) {
            timedGroups.forEach((group, time) -> {
                if (time > System.currentTimeMillis()) {
                    finalGroups.add(group);
                }
            });
        }
        return finalGroups;
    }

    public void setPermission(String permission, boolean value) {
        if (permissions == null) {
            permissions = new HashMap<>();
        }
        permissions.put(permission, value);
    }

    public boolean removePermission(String permission) {
        if (permissions != null) {
            return permissions.remove(permission);
        }
        return false;
    }

    public void addGroup(String group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    public boolean removeGroup(String group) {
        if (groups != null) {
            return groups.remove(group);
        }
        return false;
    }

    public void setTimedGroup(String group, long duration) {
        if (timedGroups == null) {
            timedGroups = new HashMap<>();
        }
        timedGroups.put(group, System.currentTimeMillis() + duration);
    }

    public boolean removeTimedGroup(String group) {
        if (timedGroups != null) {
            return timedGroups.remove(group) != null;
        }
        return false;
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
