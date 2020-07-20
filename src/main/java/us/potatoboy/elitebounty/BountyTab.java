package us.potatoboy.elitebounty;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BountyTab implements TabCompleter {

    List<String> arguments = new ArrayList<>(Arrays.asList("set", "list", "info", "reward", "remove"));
    List<String> trueFalse = new ArrayList<>(Arrays.asList("true", "false"));

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1:
                for (String a : arguments) {
                    if (commandSender.isOp() || commandSender.hasPermission("elitebounty." + a)) {
                        if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                            result.add(a);
                        }
                    }
                }
                return result;

            case 2:
                switch (args[0].toLowerCase()) {
                    case "info":
                        return null;
                    case "remove":
                        return null;
                    case "reward":
                        return null;
                    case "set":
                        return null;
                }

            case 3:
                switch (args[0].toLowerCase()) {
                    case "info":
                        return null;
                    case "remove":
                        return null;
                    case "reward":
                        return null;
                    case "set":
                        for (String a : trueFalse) {
                            if (a.toLowerCase().startsWith(args[2].toLowerCase())) {
                                result.add(a);
                            }
                        }
                        return result;
                }
        }

        return Arrays.asList();
    }
}
