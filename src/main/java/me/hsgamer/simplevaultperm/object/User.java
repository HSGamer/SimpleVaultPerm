package me.hsgamer.simplevaultperm.object;

import lombok.Data;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.simplevaultperm.util.ValueUtil;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@Data
public class User {
    private final UUID uuid;
    private final List<PermissionAttachment> permissionAttachments = new ArrayList<>();
    private final Object lock = new Object();

    // User data
    private List<String> groups;
    private Map<String, Long> timedGroups;
    private Map<String, Boolean> permissions;

    // Cached data
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
}
