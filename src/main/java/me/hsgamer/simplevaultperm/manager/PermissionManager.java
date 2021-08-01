package me.hsgamer.simplevaultperm.manager;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PermissionManager implements Listener {
    private final SimpleVaultPerm plugin;
    private final Map<String, PermissionAttachment> attachmentMap = new HashMap<>();

    public PermissionManager(SimpleVaultPerm plugin) {
        this.plugin = plugin;
    }

    public void addPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        addPermissions(player.getName(), attachment);
        attachmentMap.put(player.getName(), attachment);
    }

    private void addPermissions(String player, PermissionAttachment attachment) {
        String defaultGroup = MainConfig.DEFAULT_GROUP.getValue();
        plugin.getGroupConfig().getPermissionMap(defaultGroup).forEach(attachment::setPermission);
        List<String> groups = plugin.getUserConfig().getGroups(player);
        groups.stream().map(plugin.getGroupConfig()::getPermissionMap).forEach(map -> map.forEach(attachment::setPermission));
        plugin.getUserConfig().getPermissionMap(player).forEach(attachment::setPermission);
    }

    public void removePermissions(Player player) {
        Optional.ofNullable(attachmentMap.remove(player.getName())).ifPresent(PermissionAttachment::remove);
    }

    public void reloadPermissions(String player) {
        if (!attachmentMap.containsKey(player)) {
            return;
        }
        PermissionAttachment oldAttachment = attachmentMap.get(player);
        Permissible permissible = oldAttachment.getPermissible();
        oldAttachment.remove();
        PermissionAttachment newAttachment = permissible.addAttachment(plugin);
        addPermissions(player, newAttachment);
        attachmentMap.replace(player, oldAttachment, newAttachment);
    }

    public void reloadAllPermissions() {
        attachmentMap.replaceAll((player, oldAttachment) -> {
            Permissible permissible = oldAttachment.getPermissible();
            oldAttachment.remove();
            PermissionAttachment newAttachment = permissible.addAttachment(plugin);
            addPermissions(player, newAttachment);
            return newAttachment;
        });
    }

    public void clearAllPermissions() {
        attachmentMap.values().forEach(PermissionAttachment::remove);
        attachmentMap.clear();
    }
}
