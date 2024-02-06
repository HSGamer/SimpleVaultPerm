package me.hsgamer.simplevaultperm.event;

import lombok.Getter;
import me.hsgamer.simplevaultperm.object.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GroupExpiredEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final User user;
    private final String group;

    public GroupExpiredEvent(User user, String group) {
        super(true);
        this.user = user;
        this.group = group;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

}
