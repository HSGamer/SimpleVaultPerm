package me.hsgamer.simplevaultperm.hook;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import me.hsgamer.simplevaultperm.object.Group;
import me.hsgamer.simplevaultperm.object.User;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.colorize;

public class VaultChatHook extends Chat {
    private final SimpleVaultPerm plugin;

    public VaultChatHook(SimpleVaultPerm plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "SimpleVaultPerm";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @SuppressWarnings("deprecation")
    private User getUser(String player) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(player);
        return plugin.getUserManager().getUser(offlinePlayer.getUniqueId(), false);
    }

    private Group getGroup(String group, boolean createIfNotExist) {
        return plugin.getUserManager().getGroup(group, createIfNotExist);
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        User user = getUser(player);
        if (user != null) {
            String prefix = user.getPrefix();
            if (prefix != null) {
                return colorize(prefix);
            }
        }
        return "";
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        User user = getUser(player);
        if (user != null) {
            user.setPrefix(prefix);
            user.setUpdateRequire(true);
        }
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        User user = getUser(player);
        if (user != null) {
            String suffix = user.getSuffix();
            if (suffix != null) {
                return colorize(suffix);
            }
        }
        return "";
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        User user = getUser(player);
        if (user != null) {
            user.setSuffix(suffix);
            user.setUpdateRequire(true);
        }
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        Group groupObject = getGroup(group, false);
        if (groupObject != null) {
            String prefix = groupObject.getPrefix();
            if (prefix != null) {
                return colorize(prefix);
            }
        }
        return "";
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        Group groupObject = getGroup(group, true);
        groupObject.setPrefix(prefix);
        groupObject.setUpdateRequire(true);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        Group groupObject = getGroup(group, false);
        if (groupObject != null) {
            String suffix = groupObject.getSuffix();
            if (suffix != null) {
                return colorize(suffix);
            }
        }
        return "";
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        Group groupObject = getGroup(group, true);
        groupObject.setSuffix(suffix);
        groupObject.setUpdateRequire(true);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        throw new UnsupportedOperationException();
    }
}
