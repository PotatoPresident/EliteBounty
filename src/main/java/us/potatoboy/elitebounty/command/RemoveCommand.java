package us.potatoboy.elitebounty.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import us.potatoboy.elitebounty.Bounty;
import us.potatoboy.elitebounty.EliteBounty;
import us.potatoboy.elitebounty.Lang;

import java.util.HashMap;

public class RemoveCommand extends AbstractCommand {
    public static final String NAME = "Remove";

    public static final String PERMISSION = "elitebounty.remove";

    public static HashMap<CommandSender, Bounty> confirming = new HashMap<>();

    public static final String USAGE = "/bounty remove <Bounty Target> <Bounty Setter>";

    public RemoveCommand(CommandSender sender) {
        super(sender, NAME, PERMISSION, USAGE);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPerm()) {
            sender.sendMessage(Lang.INVALID_PERMISSIONS.toString());
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            sendUsage();
            return;
        }

        EliteBounty eliteBounty = EliteBounty.getInstance();
        eliteBounty.getOfflinePlayerAsync(args[1], target1 -> eliteBounty.getOfflinePlayerAsync(args[2], target2 -> Bukkit.getScheduler().runTask(eliteBounty, () -> {

            Bounty requestedBounty = eliteBounty.getBountyFromIds(target1.getUniqueId(), target2.getUniqueId());

            if (requestedBounty == null) {
                sender.sendMessage(Lang.NO_BOUNTY_ON_PLAYER.toString());
                return;
            }

            //checks if player has confirmed bounty
            if (eliteBounty.getConfig().getBoolean("set.require-confirm")) {
                if (confirming.containsKey(sender) && confirming.get(sender).equals(requestedBounty)) {
                    confirming.remove(sender);
                } else {
                    confirming.put(sender, requestedBounty);
                    sender.sendMessage(String.format(Lang.CONFIRM_REMOVE.toString(),
                            args[1],
                            eliteBounty.getConfig().getInt("remove.confirm-delay")
                    ));

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(eliteBounty, new Runnable() {
                        public void run() {
                            confirming.remove(sender);
                        }
                    }, eliteBounty.getConfig().getInt("remove.confirm-delay") * 20);
                    return;
                }
            }

            if (EliteBounty.getInstance().removeBounty(requestedBounty.target, requestedBounty.setBy)) {
                sender.sendMessage(Lang.BOUNTY_REMOVED.toString());
            } else {
                sender.sendMessage(Lang.BOUNTY_REMOVE_ERROR.toString());
                return;
            }
        })));
    }
}