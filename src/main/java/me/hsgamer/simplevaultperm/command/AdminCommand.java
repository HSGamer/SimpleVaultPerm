package me.hsgamer.simplevaultperm.command;

import me.hsgamer.simplevaultperm.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AdminCommand extends Command {
    public AdminCommand(String name) {
        super(name);
        setPermission(Permissions.ADMIN.getName());
    }

    public AdminCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        setPermission(Permissions.ADMIN.getName());
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        return execute(sender, args);
    }
}
