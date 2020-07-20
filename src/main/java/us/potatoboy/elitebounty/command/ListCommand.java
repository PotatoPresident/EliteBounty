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

import java.util.ArrayList;

public class ListCommand extends AbstractCommand {

    public static final String NAME = "List";

    public static final String PERMISSION = "elitebounty.list";

    public static final String USAGE = "/bounty list";

    public ListCommand(CommandSender sender) {
        super(sender, NAME, PERMISSION, USAGE);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPerm()) {
            sender.sendMessage(Lang.INVALID_PERMISSIONS.toString());
            return;
        }

        EliteBounty main = EliteBounty.getInstance();

        ArrayList<Bounty> bounties = main.getBounties();
        if (bounties == null || bounties.isEmpty()) {
            sender.sendMessage(Lang.NO_BOUNTIES.toString());
            return;
        }

        sender.sendMessage(String.format(Lang.DIVIDER.toString(), "Bounty List"));

        for (Bounty bounty: bounties) {
            TextComponent listItem = new TextComponent(String.format(Lang.BOUNTY_LIST.toString(), Bukkit.getOfflinePlayer(bounty.target).getName(),
                    bounty.bountyReward.getType().name(),
                    bounty.bountyReward.getAmount()));
            listItem.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND,
                    "/bounty info " + Bukkit.getOfflinePlayer(bounty.target).getName() + " " + Bukkit.getOfflinePlayer(bounty.setBy).getName() + " " + EliteBounty.hiddenArg));
            listItem.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To View Bounty").create()));
            sender.spigot().sendMessage(listItem);
        }
    }
}
