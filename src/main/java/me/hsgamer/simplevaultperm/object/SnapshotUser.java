package me.hsgamer.simplevaultperm.object;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Data
public class SnapshotUser {
    private static final Field PERMISSIONS_FIELD;

    static {
        try {
            PERMISSIONS_FIELD = PermissionAttachment.class.getDeclaredField("permissions");
            PERMISSIONS_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final User user;
    private final List<String> groups;
    private final Map<String, Boolean> permissions;
    private final String prefix;
    private final String suffix;
    private final Object attachmentLock = new Object();
    private PermissionAttachment permissionAttachment;

    public void setup() {
        synchronized (attachmentLock) {
            if (permissionAttachment == null) {
                Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
                Player player = plugin.getServer().getPlayer(user.getUuid());
                if (player == null) {
                    return;
                }
                permissionAttachment = player.addAttachment(plugin);
            }

            Map<String, Boolean> attachmentPermissions;
            try {
                // noinspection unchecked
                attachmentPermissions = (Map<String, Boolean>) PERMISSIONS_FIELD.get(permissionAttachment);
            } catch (Exception e) {
                JavaPlugin.getProvidingPlugin(getClass()).getLogger().severe("Cannot get the permissions of the attachment");
                return;
            }

            attachmentPermissions.clear();
            attachmentPermissions.putAll(permissions);

            permissionAttachment.getPermissible().recalculatePermissions();
        }
    }

    public void clear() {
        synchronized (attachmentLock) {
            if (permissionAttachment != null) {
                permissionAttachment.remove();
                permissionAttachment = null;
            }
        }
    }
}
