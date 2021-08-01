package me.hsgamer.simplevaultperm.hook;

import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

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

    @Override
    public String getPlayerPrefix(String world, String player) {
        return colorize(plugin.getChatConfig().getPlayerPrefix(player));
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        plugin.getChatConfig().setPlayerPrefix(player, prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return colorize(plugin.getChatConfig().getPlayerSuffix(player));
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        plugin.getChatConfig().setPlayerSuffix(player, suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return colorize(plugin.getChatConfig().getGroupPrefix(group));
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        plugin.getChatConfig().setGroupPrefix(group, prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return colorize(plugin.getChatConfig().getGroupSuffix(group));
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        plugin.getChatConfig().setGroupSuffix(group, suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        throw new UnsupportedOperationException();
    }
}
