package us.potatoboy.elitebounty.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import us.potatoboy.elitebounty.Bounty;
import us.potatoboy.elitebounty.EliteBounty;
import us.potatoboy.elitebounty.Lang;

import java.util.HashSet;

public class InfoCommand extends AbstractCommand {
    public static final String NAME = "Info";

    public static final String PERMISSION = "elitebounty.info";

    public static final String USAGE = "/bounty info <Bounty Target> [Bounty Setter]";

    public InfoCommand(CommandSender sender) {
        super(sender, NAME, PERMISSION, USAGE);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPerm()) {
            sender.sendMessage(Lang.INVALID_PERMISSIONS.toString());
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            sendUsage();
            return;
        }

        if(args.length == 2) {
            HashSet<Bounty> requestedBounties = EliteBounty.getInstance().getBountiesOnTarget(Bukkit.getOfflinePlayer(args[1]).getUniqueId());

            if (requestedBounties != null) {

                sender.sendMessage(String.format(Lang.DIVIDER.toString(), "BOUNTY LIST"));
                sender.sendMessage(String.format(Lang.BOUNTIES_ON_PLAYER.toString(), args[1].toUpperCase()));
                for (Bounty bounty : requestedBounties) {
                    TextComponent listItem = new TextComponent(String.format(Lang.BOUNTY_LIST.toString(),
                            bounty.anonymousSetter ? "Anonymous" : Bukkit.getOfflinePlayer(bounty.setBy).getName(),
                            bounty.getFriendlyRewardName(),
                            bounty.bountyReward.getAmount()));
                    listItem.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND,
                            "/bounty info " +
                                    Bukkit.getOfflinePlayer(bounty.target).getName() + " " +
                                    Bukkit.getOfflinePlayer(bounty.setBy).getName() + " " +
                                    EliteBounty.hiddenArg
                    ));
                    listItem.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To View Bounty").create()));

                    sender.spigot().sendMessage(listItem);
                }
            } else {
                sender.sendMessage(Lang.NO_BOUNTY_ON_PLAYER.toString());
            }
        } else {
            Bounty requestedBounty = EliteBounty.getInstance().getBountyFromIds(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), Bukkit.getOfflinePlayer(args[2]).getUniqueId());

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

            TextComponent rewardText = new TextComponent(String.format(Lang.BOUNTY_REWARD.toString(),
                    requestedBounty.getFriendlyRewardName(),
                    requestedBounty.bountyReward.getAmount()));
            rewardText.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/bounty reward " + args[1] + " " + args[2] + " " + EliteBounty.hiddenArg));
            rewardText.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To View Reward").create()));

            sender.sendMessage(String.format(Lang.DIVIDER.toString(),
                    "Bounty"));

            sender.sendMessage(String.format(Lang.BOUNTY_TARGET.toString(),
                    Bukkit.getOfflinePlayer(requestedBounty.target).getName()));

            sender.sendMessage(String.format(Lang.BOUNTY_OWNER.toString(),
                    requestedBounty.anonymousSetter ? "Anonymous" : Bukkit.getOfflinePlayer(requestedBounty.setBy).getName()));

            sender.sendMessage(String.format(Lang.BOUNTY_DATE.toString(),
                    requestedBounty.setDate));

            sender.spigot().sendMessage(rewardText);
            return;
        }
    }
}
