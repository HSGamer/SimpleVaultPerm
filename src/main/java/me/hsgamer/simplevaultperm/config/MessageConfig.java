package me.hsgamer.simplevaultperm.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface MessageConfig {
    @ConfigPath("prefix")
    default String getPrefix() {
        return "&6[SimpleVaultPerm] ";
    }

    @ConfigPath("success")
    default String getSuccess() {
        return "&aSuccess";
    }

    @ConfigPath("group-not-found")
    default String getGroupNotFound() {
        return "&cGroup not found";
    }

    @ConfigPath("player-not-found")
    default String getPlayerNotFound() {
        return "&cPlayer not found";
    }

    @ConfigPath("group-info.permission-title")
    default String getGroupInfoPermissionTitle() {
        return "&ePermissions";
    }

    @ConfigPath("group-info.permission-format")
    default String getGroupInfoPermissionFormat() {
        return "  &f{permission}&7: &b{state}";
    }

    @ConfigPath("group-info.prefix")
    default String getGroupInfoPrefix() {
        return "&ePrefix: &f{prefix}";
    }

    @ConfigPath("group-info.suffix")
    default String getGroupInfoSuffix() {
        return "&eSuffix: &f{suffix}";
    }

    @ConfigPath("player-info.permission-title")
    default String getPlayerInfoPermissionTitle() {
        return "&ePermissions";
    }

    @ConfigPath("player-info.permission-format")
    default String getPlayerInfoPermissionFormat() {
        return "  &f{permission}&7: &b{state}";
    }

    @ConfigPath("player-info.prefix")
    default String getPlayerInfoPrefix() {
        return "&ePrefix: &f{prefix}";
    }

    @ConfigPath("player-info.suffix")
    default String getPlayerInfoSuffix() {
        return "&eSuffix: &f{suffix}";
    }

    @ConfigPath("player-info.group-title")
    default String getPlayerInfoGroupTitle() {
        return "&eGroups";
    }

    @ConfigPath("player-info.group-format")
    default String getPlayerInfoGroupFormat() {
        return "  &f{group}";
    }

    @ConfigPath("cannot-remove-timed-group")
    default String getCannotRemoveTimedGroup() {
        return "&cCannot remove timed group from the player";
    }

    @ConfigPath("cannot-remove-group")
    default String getCannotRemoveGroup() {
        return "&cCannot remove group from the player";
    }

    @ConfigPath("cannot-remove-group-permission")
    default String getCannotRemoveGroupPermission() {
        return "&cCannot remove permission from the group";
    }

    void reloadConfig();
}
