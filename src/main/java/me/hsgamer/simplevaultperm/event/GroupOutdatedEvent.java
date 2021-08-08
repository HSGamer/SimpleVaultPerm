package me.hsgamer.simplevaultperm.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GroupOutdatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String group;

    public GroupOutdatedEvent(Player player, String group) {
        this.player = player;
        this.group = group;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public Player getPlayer() {
        return player;
    }

    public String getGroup() {
        return group;
    }
}
