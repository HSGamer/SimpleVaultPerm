package me.hsgamer.simplevaultperm.listener;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final SimpleVaultPerm plugin;

    public PlayerListener(SimpleVaultPerm plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            plugin.getTimedGroupConfig().addPlayer(event.getPlayer());
            plugin.getPermissionManager().addPermissions(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTimedGroupConfig().removePlayer(event.getPlayer());
        plugin.getPermissionManager().removePermissions(event.getPlayer());
    }
}
