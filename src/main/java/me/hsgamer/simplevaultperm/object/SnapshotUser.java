package me.hsgamer.simplevaultperm.object;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

@Data
public class SnapshotUser {
    private final User user;
    private final List<String> groups;
    private final Map<String, Boolean> permissions;
    private final String prefix;
    private final String suffix;
    private final Object attachmentLock = new Object();
    private PermissionAttachment permissionAttachment;

    public void setup() {
        synchronized (attachmentLock) {
            if (permissionAttachment != null) {
                permissionAttachment.remove();
            }

            Plugin plugin = JavaPlugin.getProvidingPlugin(getClass());
            Player player = plugin.getServer().getPlayer(user.getUuid());
            if (player == null) {
                return;
            }

            if (!permissions.isEmpty()) {
                PermissionAttachment attachment = player.addAttachment(plugin);
                permissions.forEach(attachment::setPermission);
                permissionAttachment = attachment;
            }
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
