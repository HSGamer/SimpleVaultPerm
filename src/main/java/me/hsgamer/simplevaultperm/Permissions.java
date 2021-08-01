package me.hsgamer.simplevaultperm;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {
    public static final Permission ADMIN = new Permission("simplevaultperm.admin", PermissionDefault.OP);

    private Permissions() {
        // EMPTY
    }
}
