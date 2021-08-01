package me.hsgamer.simplevaultperm.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.simplevaultperm.SimpleVaultPerm;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddPermCommand extends AdminCommand {
    private final SimpleVaultPerm plugin;

    public AddPermCommand(SimpleVaultPerm plugin) {
        super("addperm", "Add a permission to a player", "/addperm <player> <permission> [state]", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        String player = args[0];
        String permission = args[1];
        boolean state = true;
        if (args.length > 2) {
            state = Boolean.parseBoolean(args[2]);
        }
        if (plugin.getUserConfig().addPermission(player, permission, state)) {
            MessageUtils.sendMessage(sender, "&aSuccess");
            return true;
        } else {
            MessageUtils.sendMessage(sender, "&cCannot add permission to the player");
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        } else if (args.length == 3) {
            return Arrays.asList("true", "false");
        } else {
            return Collections.emptyList();
        }
    }
}
