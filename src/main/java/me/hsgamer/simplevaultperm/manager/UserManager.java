package me.hsgamer.simplevaultperm.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import me.hsgamer.simplevaultperm.object.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private final SimpleVaultPerm plugin;
    private final Config groupConfig;
    private final Config userConfig;
    private final Map<String, Group> groupMap = new LinkedHashMap<>();
    private final Map<UUID, User> userMap = new HashMap<>();

    public UserManager(SimpleVaultPerm plugin) {
        this.plugin = plugin;
        this.groupConfig = new BukkitConfig(plugin, "groups.yml");
        this.userConfig = new BukkitConfig(plugin, "users.yml");
    }

    public void setup() {
        groupConfig.setup();
        userConfig.setup();

        groupConfig.getKeys(false).forEach(groupName -> {
            Group group = Group.fromMap(groupName, groupConfig.getNormalizedValues(groupName, false));
            groupMap.put(groupName, group);
        });
        userConfig.getKeys(false).forEach(uuidKey -> {
            User user = User.fromMap(UUID.fromString(uuidKey), userConfig.getNormalizedValues(uuidKey, false));
            userMap.put(user.getUuid(), user);
        });
    }

    public void save() {
        groupConfig.getKeys(false).forEach(groupConfig::remove);
        groupMap.forEach((groupName, group) -> groupConfig.set(groupName, group.toMap()));
        groupConfig.save();

        userConfig.getKeys(false).forEach(userConfig::remove);
        userMap.forEach((uuid, user) -> userConfig.set(uuid.toString(), user.toMap()));
        userConfig.save();
    }
}
