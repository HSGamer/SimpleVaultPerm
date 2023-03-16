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
            updateUser(user, Collections.emptyList());
        }
        return user;
    }

    public Collection<String> getGroupNames() {
        return Collections.unmodifiableCollection(groupMap.keySet());
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

    private boolean updateUser(User user, List<String> updatedGroups) {
        List<String> cachedGroups = user.getCachedGroups();
        if (cachedGroups != null && cachedGroups.stream().anyMatch(updatedGroups::contains)) {
            user.setUpdateRequire(true);
        }

        if (!user.isUpdateRequire()) {
            return false;
        }

        List<String> finalGroups = new ArrayList<>(user.getFinalGroups());
        String defaultGroup = plugin.getMainConfig().getDefaultGroup();
        if (!finalGroups.contains(defaultGroup)) {
            finalGroups.add(defaultGroup);
        }
        user.setCachedGroups(finalGroups);

        Map<String, Boolean> finalPermissions = new HashMap<>();
        String finalPrefix = "";
        String finalSuffix = "";

        List<String> userGroups = user.getCachedGroups();
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

        String userPrefix = user.getPrefix();
        if (userPrefix != null) {
            finalPrefix = userPrefix;
        }

        String userSuffix = user.getSuffix();
        if (userSuffix != null) {
            finalSuffix = userSuffix;
        }

        user.setCachedPermissions(finalPermissions);
        user.setCachedPrefix(finalPrefix);
        user.setCachedSuffix(finalSuffix);

        user.setUpdateRequire(false);
        return true;
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

            if (updateUser(user, updatedGroups)) {
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
