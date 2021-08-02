package me.hsgamer.simplevaultperm.listener;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final SimpleVaultPerm plugin;

    public PlayerListener(SimpleVaultPerm plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getTimedGroupConfig().addPlayer(event.getPlayer().getName());
        plugin.getPermissionManager().addPermissions(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTimedGroupConfig().removePlayer(event.getPlayer().getName());
        plugin.getPermissionManager().removePermissions(event.getPlayer());
    }
}
