package me.hsgamer.simplevaultperm.object;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.simplevaultperm.util.ValueUtil;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class User {
    private final UUID uuid;
    private final @Getter(AccessLevel.PACKAGE) List<String> expiredGroups = new ArrayList<>();
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
        Optional.ofNullable(timedGroups).map(timeMap -> {
            Map<String, Object> newMap = new HashMap<>();
            long currentTime = System.currentTimeMillis();
            timeMap.forEach((group, time) -> {
                if (time >= currentTime) {
                    newMap.put(group, time);
                }
            });
            return newMap;
        }).ifPresent(value -> map.put("timed-groups", value));
        Optional.ofNullable(prefix).ifPresent(value -> map.put("prefix", value));
        Optional.ofNullable(suffix).ifPresent(value -> map.put("suffix", value));
        return map;
    }

    public List<String> getFinalGroups() {
        List<String> finalGroups = new ArrayList<>(groups);
        if (timedGroups != null) {
            long currentTime = System.currentTimeMillis();
            timedGroups.forEach((group, time) -> {
                if (time >= currentTime) {
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
        expiredGroups.remove(group);
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
        long currentTime = System.currentTimeMillis();
        List<String> lastExpiredGroups = timedGroups.entrySet().stream()
                .filter(entry -> entry.getValue() < currentTime)
                .map(Map.Entry::getKey)
                .filter(group -> !expiredGroups.contains(group))
                .collect(Collectors.toList());
        expiredGroups.addAll(lastExpiredGroups);
        return expiredGroups;
    }
}
