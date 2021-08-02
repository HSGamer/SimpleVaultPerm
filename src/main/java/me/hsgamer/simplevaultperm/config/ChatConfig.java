package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatConfig extends BukkitConfig {
    private final SimpleVaultPerm plugin;

    public ChatConfig(SimpleVaultPerm plugin) {
        super(plugin, "chat.yml");
        this.plugin = plugin;
    }

    private String formatPlayerPrefix(String player) {
        return "prefix.player." + player;
    }

    private String formatGroupPrefix(String group) {
        return "prefix.group." + group;
    }

    private String formatPlayerSuffix(String player) {
        return "suffix.player." + player;
    }

    private String formatGroupSuffix(String group) {
        return "suffix.group." + group;
    }

    public String getPlayerPrefix(String player) {
        String playerPath = formatPlayerPrefix(player);
        if (contains(playerPath)) {
            return getInstance(playerPath, "", String.class);
        }
        List<String> groups = new ArrayList<>(plugin.getUserConfig().getGroups(player));
        groups.addAll(plugin.getTimedGroupConfig().getGroups(player));
        Collections.reverse(groups);
        for (String group : groups) {
            String groupPath = formatGroupPrefix(group);
            if (contains(groupPath)) {
                return getInstance(groupPath, "", String.class);
            }
        }
        return getInstance(formatGroupPrefix(MainConfig.DEFAULT_GROUP.getValue()), "", String.class);
    }

    public void setPlayerPrefix(String player, String prefix) {
        set(formatPlayerPrefix(player), prefix.equalsIgnoreCase("null") ? null : prefix);
        save();
    }

    public String getPlayerSuffix(String player) {
        String playerPath = formatPlayerSuffix(player);
        if (contains(playerPath)) {
            return getInstance(playerPath, "", String.class);
        }
        List<String> groups = new ArrayList<>(plugin.getUserConfig().getGroups(player));
        groups.addAll(plugin.getTimedGroupConfig().getGroups(player));
        Collections.reverse(groups);
        for (String group : groups) {
            String groupPath = formatGroupSuffix(group);
            if (contains(groupPath)) {
                return getInstance(groupPath, "", String.class);
            }
        }
        return getInstance(formatGroupSuffix(MainConfig.DEFAULT_GROUP.getValue()), "", String.class);
    }

    public void setPlayerSuffix(String player, String suffix) {
        set(formatPlayerSuffix(player), suffix.equalsIgnoreCase("null") ? null : suffix);
        save();
    }

    public String getGroupPrefix(String group) {
        String path = formatGroupPrefix(group);
        if (contains(path)) {
            return getInstance(path, "", String.class);
        }
        return getInstance(formatGroupPrefix(MainConfig.DEFAULT_GROUP.getValue()), "", String.class);
    }

    public void setGroupPrefix(String group, String prefix) {
        set(formatGroupPrefix(group), prefix.equalsIgnoreCase("null") ? null : prefix);
        save();
    }

    public String getGroupSuffix(String group) {
        String path = formatGroupSuffix(group);
        if (contains(path)) {
            return getInstance(path, "", String.class);
        }
        return getInstance(formatGroupSuffix(MainConfig.DEFAULT_GROUP.getValue()), "", String.class);
    }

    public void setGroupSuffix(String group, String suffix) {
        set(formatGroupSuffix(group), suffix.equalsIgnoreCase("null") ? null : suffix);
        save();
    }
}
