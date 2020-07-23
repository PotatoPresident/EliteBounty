package us.potatoboy.elitebounty;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import us.potatoboy.elitebounty.command.*;

public class BountyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length == 0) {
            return false;
        }

        AbstractCommand cmd = null;

        switch (args[0].toLowerCase()) {
            case "list":
                cmd = new ListCommand(commandSender);
                break;
            case "set":
                cmd = new SetCommand(commandSender);
                break;
            case "info":
                cmd = new InfoCommand(commandSender);
                break;
            case "reward":
                cmd = new RewardCommand(commandSender);
                break;
            case "remove":
                cmd = new RemoveCommand(commandSender);
                break;
            case "compass":
                cmd = new CompassCommand(commandSender);
                break;
        }

        if (cmd != null) {
            cmd.execute(commandSender, command, label, args);
            return true;
        } else {
            return false;
        }
    }
}
