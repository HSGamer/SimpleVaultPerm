package me.hsgamer.simplevaultperm.object;

import lombok.Data;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.simplevaultperm.util.ValueUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
public class Group {
    private String prefix;
    private String suffix;
    private Map<String, Boolean> permissions;
    private boolean updateRequire;

    public static Group fromMap(Map<String, Object> map) {
        Group group = new Group();
        Optional.ofNullable(map.get("prefix")).map(Objects::toString).ifPresent(group::setPrefix);
        Optional.ofNullable(map.get("suffix")).map(Objects::toString).ifPresent(group::setSuffix);
        Optional.ofNullable(map.get("permissions")).map(CollectionUtils::createStringListFromObject).map(ValueUtil::toBooleanMap).ifPresent(group::setPermissions);
        return group;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Optional.ofNullable(prefix).ifPresent(value -> map.put("prefix", value));
        Optional.ofNullable(suffix).ifPresent(value -> map.put("suffix", value));
        Optional.ofNullable(permissions).map(ValueUtil::toStringList).filter(v -> !v.isEmpty()).ifPresent(value -> map.put("permissions", value));
        return map;
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
}
