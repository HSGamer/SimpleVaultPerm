package me.hsgamer.simplevaultperm.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.User;
import me.hsgamer.simplevaultperm.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlaceholderHook extends PlaceholderExpansion {
    private final SimpleVaultPerm plugin;

    public PlaceholderHook(SimpleVaultPerm plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "simplevaultperm";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean register() {
        boolean result = super.register();
        if (result) {
            plugin.addDisableFunction(this::unregister);
        }
        return result;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        if (params.startsWith("grouptime_")) {
            String group = params.substring("grouptime_".length());
            boolean format = group.endsWith("_formatted");
            if (format) {
                group = group.substring(0, group.indexOf("_formatted"));
            }
            final String finalGroup = group;
            long timeLeft = Optional.ofNullable(plugin.getUserManager().getUser(player.getUniqueId(), false))
                    .map(User::getTimedGroups)
                    .map(timedGroups -> timedGroups.get(finalGroup))
                    .map(endTime -> endTime - System.currentTimeMillis())
                    .filter(duration -> duration > 0)
                    .orElse(0L);
            if (format) {
                return TimeUtil.displayDuration(timeLeft);
            } else {
                return Long.toString(timeLeft);
            }
        }
        return null;
    }
}
