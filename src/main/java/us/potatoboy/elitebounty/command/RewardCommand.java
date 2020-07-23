package us.potatoboy.elitebounty.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.potatoboy.elitebounty.Bounty;
import us.potatoboy.elitebounty.EliteBounty;
import us.potatoboy.elitebounty.Lang;
import us.potatoboy.elitebounty.RewardMenu;

public class RewardCommand extends AbstractCommand {
    public static final String NAME = "Reward";

    public static final String PERMISSION = "elitebounty.reward";

    public static final String USAGE = "/bounty reward <Bounty Target> <Bounty Setter>";

    public RewardCommand(CommandSender sender) {
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

        if (!isSenderPlayer()) {
            sender.sendMessage(Lang.CANT_CONSOLE.toString());
            sendUsage();
            return;
        }

        EliteBounty eliteBounty = EliteBounty.getInstance();

        eliteBounty.getOfflinePlayerAsync(args[1], target1 -> eliteBounty.getOfflinePlayerAsync(args[2], target2 -> Bukkit.getScheduler().runTask(eliteBounty, () -> {
            Bounty requestedBounty = EliteBounty.getInstance().getBountyFromIds(target1.getUniqueId(), target2.getUniqueId());

            if (requestedBounty == null) {
                sender.sendMessage(Lang.NO_BOUNTY_ON_PLAYER.toString());
                return;
            }

            if (requestedBounty.anonymousSetter) {
                if (args.length <= 3) {
                    sender.sendMessage(Lang.NO_BOUNTY_ON_PLAYER.toString());
                    return;
                }

                if (!args[3].equals(EliteBounty.hiddenArg)) {
                    sender.sendMessage(Lang.NO_BOUNTY_ON_PLAYER.toString());
                    return;
                }
            }

            RewardMenu menu = new RewardMenu(requestedBounty.bountyReward);
            menu.OpenInventory(((Player) sender).getPlayer());
        })));
    }
}
