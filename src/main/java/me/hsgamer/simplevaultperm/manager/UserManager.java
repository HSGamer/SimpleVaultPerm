package me.hsgamer.simplevaultperm.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.event.GroupExpiredEvent;
import me.hsgamer.simplevaultperm.object.Group;
import me.hsgamer.simplevaultperm.object.SnapshotUser;
import me.hsgamer.simplevaultperm.object.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserManager {
    private final SimpleVaultPerm plugin;
    private final Config groupConfig;
    private final Config userConfig;
    private final AtomicBoolean isSaving = new AtomicBoolean(false);
    private final AtomicBoolean needSaving = new AtomicBoolean(false);
    private final Map<String, Group> groupMap = new LinkedHashMap<>();
    private final Map<UUID, User> userMap = new HashMap<>();
    private final Map<UUID, SnapshotUser> snapshotUserMap = new ConcurrentHashMap<>();
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

    public User getUser(UUID uuid, boolean createIfNotExist) {
        User user = userMap.get(uuid);
        if (user == null && createIfNotExist) {
            user = new User(uuid);
            userMap.put(uuid, user);
        }
        return user;
    }

    public SnapshotUser getSnapshotUser(UUID uuid, boolean forceUpdate) {
        return snapshotUserMap.compute(uuid, (key, value) -> {
            if (value != null && !forceUpdate) {
                return value;
            }
            User user = getUser(uuid, false);
            if (user == null) {
                user = new User(uuid);
            }
            return makeSnapshot(user);
        });
    }

    public Collection<String> getGroupNames() {
        return Collections.unmodifiableCollection(groupMap.keySet());
    }

    public void setup() {
        groupConfig.setup();
        userConfig.setup();

        groupConfig.getKeys(false).forEach(groupPath -> {
            String groupName = PathString.toPath(groupPath);
            Group group = Group.fromMap(PathString.toPathMap(groupConfig.getNormalizedValues(groupPath, false)));
            groupMap.put(groupName, group);
        });
        userConfig.getKeys(false).forEach(uuidPath -> {
            User user = User.fromMap(UUID.fromString(PathString.toPath(uuidPath)), PathString.toPathMap(userConfig.getNormalizedValues(uuidPath, false)));
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
        groupMap.forEach((groupName, group) -> {
            Map<String, Object> map = group.toMap();
            if (!map.isEmpty()) {
                groupConfig.set(new PathString(groupName), map);
            }
        });
        groupConfig.save();

        userConfig.getKeys(false).forEach(userConfig::remove);
        userMap.forEach((uuid, user) -> {
            Map<String, Object> map = user.toMap();
            if (!map.isEmpty()) {
                userConfig.set(new PathString(uuid.toString()), map);
            }
        });
        userConfig.save();

        isSaving.set(false);
        return true;
    }

    public void clear() {
        Optional.ofNullable(updateTask).ifPresent(BukkitTask::cancel);
        save();
        clearSnapshot();
    }

    public void clearSnapshot() {
        snapshotUserMap.forEach((uuid, snapshotUser) -> snapshotUser.clear());
        snapshotUserMap.clear();
    }

    private SnapshotUser makeSnapshot(User user) {
        List<String> groups = new ArrayList<>(user.getFinalGroups());
        String defaultGroup = plugin.getMainConfig().getDefaultGroup();
        if (!groups.contains(defaultGroup)) {
            groups.add(defaultGroup);
        }

        List<String> finalGroups = new ArrayList<>();
        Map<String, Boolean> finalPermissions = new HashMap<>();
        String finalPrefix = "";
        String finalSuffix = "";

        for (Map.Entry<String, Group> entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            Group group = entry.getValue();
            if (groups.contains(groupName)) {
                finalGroups.add(groupName);

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

        Map<String, Boolean> userPermissions = user.getPermissions();
        if (userPermissions != null) {
            finalPermissions.putAll(userPermissions);
        }

        String userPrefix = user.getPrefix();
        if (userPrefix != null) {
            finalPrefix = userPrefix;
        }

        String userSuffix = user.getSuffix();
        if (userSuffix != null) {
            finalSuffix = userSuffix;
        }

        return new SnapshotUser(user, finalGroups, finalPermissions, finalPrefix, finalSuffix);
    }

    private void updateSnapshot(User user) {
        SnapshotUser newSnapshot = makeSnapshot(user);
        SnapshotUser oldSnapshot = snapshotUserMap.put(user.getUuid(), newSnapshot);
        if (oldSnapshot != null) {
            oldSnapshot.clear();
        }
        newSnapshot.setup();
    }

    private void onUpdateTick() {
        List<String> updatedGroups = new ArrayList<>();
        groupMap.forEach((groupName, group) -> {
            if (group.isUpdateRequire()) {
                group.setUpdateRequire(false);
                updatedGroups.add(groupName);
            }
        });
        boolean updated = !updatedGroups.isEmpty();

        for (User user : userMap.values()) {
            List<String> expiredGroups = user.clearExpiredTimedGroups();
            if (!expiredGroups.isEmpty()) {
                expiredGroups.forEach(group -> {
                    if (groupMap.containsKey(group)) {
                        Bukkit.getPluginManager().callEvent(new GroupExpiredEvent(user, group));
                    }
                });
                user.setUpdateRequire(true);
            }
            if (user.isUpdateRequire()) {
                updated = true;
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            User user = Optional.ofNullable(getUser(uuid, false)).orElseGet(() -> new User(uuid));

            if (
                    updatedGroups.contains(plugin.getMainConfig().getDefaultGroup())
                            || (user.getGroups() != null && updatedGroups.stream().anyMatch(user.getGroups()::contains))
                            || (user.getTimedGroups() != null && updatedGroups.stream().anyMatch(user.getTimedGroups()::containsKey))
            ) {
                user.setUpdateRequire(true);
            }

            if (user.isUpdateRequire()) {
                updateSnapshot(user);
                user.setUpdateRequire(false);
                updated = true;
            }
        }

        if (updated && plugin.getMainConfig().isSaveOnUpdate()) {
            needSaving.set(true);
        }

        if (needSaving.get() && save()) {
            needSaving.set(false);
        }
    }
}
