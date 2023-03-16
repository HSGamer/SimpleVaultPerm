package me.hsgamer.simplevaultperm.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import me.hsgamer.simplevaultperm.object.User;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserManager {
    private final SimpleVaultPerm plugin;
    private final Config groupConfig;
    private final Config userConfig;
    private final AtomicBoolean isSaving = new AtomicBoolean(false);
    private final AtomicBoolean needSaving = new AtomicBoolean(false);
    private final Map<String, Group> groupMap = new LinkedHashMap<>();
    private final Map<UUID, User> userMap = new HashMap<>();
    private BukkitTask updateTask;

    public UserManager(SimpleVaultPerm plugin) {
        this.plugin = plugin;
        this.groupConfig = new BukkitConfig(plugin, "groups.yml");
        this.userConfig = new BukkitConfig(plugin, "users.yml");
    }

    public Group getGroup(String groupName, boolean createIfNotExist) {
        Group group = groupMap.get(groupName);
        if (group == null && createIfNotExist) {
            group = new Group();
            groupMap.put(groupName, group);
        }
        return group;
    }

    public User getUser(UUID uuid, boolean forceUpdate) {
        User user = userMap.computeIfAbsent(uuid, User::new);
        if (forceUpdate) {
            updateUser(user);
        }
        return user;
    }

    public void setup() {
        groupConfig.setup();
        userConfig.setup();

        groupConfig.getKeys(false).forEach(groupName -> {
            Group group = Group.fromMap(groupConfig.getNormalizedValues(groupName, false));
            groupMap.put(groupName, group);
        });
        userConfig.getKeys(false).forEach(uuidKey -> {
            User user = User.fromMap(UUID.fromString(uuidKey), userConfig.getNormalizedValues(uuidKey, false));
            userMap.put(user.getUuid(), user);
        });

        long updateInterval = plugin.getMainConfig().getUpdateInterval();
        updateTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::onUpdateTick, updateInterval, updateInterval);
    }

    private boolean save() {
        if (isSaving.get()) {
            return false;
        }

        groupConfig.getKeys(false).forEach(groupConfig::remove);
        groupMap.forEach((groupName, group) -> groupConfig.set(groupName, group.toMap()));
        groupConfig.save();

        userConfig.getKeys(false).forEach(userConfig::remove);
        userMap.forEach((uuid, user) -> userConfig.set(uuid.toString(), user.toMap()));
        userConfig.save();

        isSaving.set(false);
        return true;
    }

    public void clear() {
        Optional.ofNullable(updateTask).ifPresent(BukkitTask::cancel);
        save();
    }

    private void updateUser(User user) {
        Map<String, Boolean> finalPermissions = new HashMap<>();
        String finalPrefix = "";
        String finalSuffix = "";

        String defaultGroupName = plugin.getMainConfig().getDefaultGroup();
        Group defaultGroup = groupMap.get(defaultGroupName);
        if (defaultGroup != null) {
            finalPermissions.putAll(defaultGroup.getPermissions());
            finalPrefix = defaultGroup.getPrefix();
            finalSuffix = defaultGroup.getSuffix();
        }

        List<String> userGroups = user.getFinalGroups();
        for (Map.Entry<String, Group> entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            Group group = entry.getValue();
            if (userGroups.contains(groupName)) {
                finalPermissions.putAll(group.getPermissions());

                String prefix = group.getPrefix();
                if (prefix != null) {
                    finalPrefix = prefix;
                }

                String suffix = group.getSuffix();
                if (suffix != null) {
                    finalSuffix = suffix;
                }
            }
        }

        user.setCachedPermissions(finalPermissions);
        user.setPrefix(finalPrefix);
        user.setSuffix(finalSuffix);
    }

    private void onUpdateTick() {
        List<String> updatedGroups = new ArrayList<>();
        groupMap.forEach((groupName, group) -> {
            if (group.isUpdateRequire()) {
                group.setUpdateRequire(false);
                updatedGroups.add(groupName);
            }
        });

        boolean updated = false;
        for (User user : userMap.values()) {
            List<String> expiredGroups = user.clearExpiredTimedGroups();
            if (!expiredGroups.isEmpty()) {
                user.setUpdateRequire(true);
            }

            if (user.getFinalGroups().stream().anyMatch(updatedGroups::contains)) {
                user.setUpdateRequire(true);
            }

            if (user.isUpdateRequire()) {
                user.setUpdateRequire(false);
                updateUser(user);
                user.applyAttachment();
                updated = true;
            }
        }

        if (updated && plugin.getMainConfig().isSaveOnUpdate()) {
            needSaving.set(true);
        }

        if (needSaving.get()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (save()) {
                    needSaving.set(false);
                }
            });
        }
    }
}
