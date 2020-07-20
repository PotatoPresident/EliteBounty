package us.potatoboy.elitebounty.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand {

    private final CommandSender sender;

    private final String name;

    private final String permission;

    private final String usage;

    public AbstractCommand(CommandSender sender, String name, String permission, String usage) {
        this.sender = sender;
        this.name = name;
        this.permission = permission;
        this.usage = usage;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String getUsage() {
        return usage;
    }

    public boolean hasPerm() {
        return sender.hasPermission(permission) || isSenderConsole() || isSenderRemoteConsole();
    }

    public boolean isSenderPlayer() {
        return (sender instanceof Player);
    }

    public boolean isSenderConsole() {
        return (sender instanceof ConsoleCommandSender);
    }

    public boolean isSenderRemoteConsole() {
        return (sender instanceof RemoteConsoleCommandSender);
    }

    public abstract void execute(CommandSender sender, Command command,String label, String[] args);

    public void sendUsage() {
        sender.sendMessage(usage);
    }
}
