package me.hsgamer.simplevaultperm.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.config.TimedGroupConfig;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
            long timeLeft = plugin.getTimedGroupConfig().getTimeLeft(player.getName(), group);
            if (format) {
                return TimedGroupConfig.displayDuration(timeLeft);
            } else {
                return Long.toString(timeLeft);
            }
        }
        return null;
    }
}
