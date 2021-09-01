package me.hsgamer.simplevaultperm.listener;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final SimpleVaultPerm plugin;

    public PlayerListener(SimpleVaultPerm plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getTimedGroupConfig().addPlayer(event.getPlayer());
        plugin.getPermissionManager().addPermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTimedGroupConfig().removePlayer(event.getPlayer());
        plugin.getPermissionManager().removePermissions(event.getPlayer());
    }
}
