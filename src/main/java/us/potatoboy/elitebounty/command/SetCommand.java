package us.potatoboy.elitebounty.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.potatoboy.elitebounty.Bounty;
import us.potatoboy.elitebounty.EliteBounty;
import us.potatoboy.elitebounty.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SetCommand extends AbstractCommand{
    public static final String NAME = "Set";

    public static final String PERMISSION = "elitebounty.set";

    public static HashMap<Player, Bounty> confirming = new HashMap<>();

    public static final String USAGE = "/bounty set <Bounty Target> [Anonymous]";

    public SetCommand(CommandSender sender) {
        super(sender, NAME, PERMISSION, USAGE);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        //check perms
        if (!hasPerm()) {
            sender.sendMessage(Lang.INVALID_PERMISSIONS.toString());
            return;
        }

        //check if target is provided
        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            sendUsage();
            return;
        }

        //check if sender is player
        if (!isSenderPlayer()) {
            sender.sendMessage(Lang.CANT_CONSOLE.toString());
            sendUsage();
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        Player playerSender = (Player) sender;
        EliteBounty eliteBounty = EliteBounty.getInstance();

        //check if target has played before and isn't themselves
        if (!target.hasPlayedBefore() || target.getUniqueId() == playerSender.getUniqueId()) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            sendUsage();
            return;
        }

        HashSet<Bounty> bountyHashSet = eliteBounty.getBountiesOnTarget(target.getUniqueId());
        int maxHave = eliteBounty.getConfig().getInt("set.max-have");

        //check if target already has a bounty
        if (bountyHashSet != null && bountyHashSet.size() > maxHave) {
            sender.sendMessage(String.format(Lang.TARGET_HAS_BOUNTY.toString(), maxHave));
            return;
        }

        //check if sender has set max bounties
        ArrayList<Bounty> bounties = eliteBounty.getBounties();
        int maxSet = eliteBounty.getConfig().getInt("set.max-set");
        if (maxSet != -1 && bounties != null) {
            int bountiesSet = 0;
            for (Bounty b : bounties) {
                if (b.setBy.equals(playerSender.getUniqueId())) {
                    bountiesSet += 1;
                }

                if (bountiesSet >= maxSet) {
                    sender.sendMessage(Lang.MAX_BOUNTIES_SET.toString());
                    return;
                }
            }
        }


        boolean anonymousSetter = false;
        //check if anonymous setter
        if (args.length > 2) {
            anonymousSetter = Boolean.parseBoolean(args[2]);

            if (anonymousSetter && !hasPerm()) {
                sender.sendMessage(Lang.INVALID_PERMISSIONS.toString());
                return;
            }
        }

        ItemStack fee = new ItemStack(
                Material.getMaterial(eliteBounty.getConfig().getString("set.fee.type")),
                eliteBounty.getConfig().getInt(anonymousSetter ? "set.fee.anonymous-amount" : "set.fee.amount")
        );

        //check if sender can afford fee
        if (!playerSender.getInventory().contains(fee.getType(), fee.getAmount())) {
            sender.sendMessage(String.format(Lang.CANT_PAY_FEE.toString(), fee.getType(), fee.getAmount()));
            return;
        }

        ItemStack reward = new ItemStack(playerSender.getInventory().getItemInMainHand());

        //if reward is the same as fee. subtract fee from reward
        if (playerSender.getInventory().getItemInMainHand().getType() == fee.getType()) {
            reward.setAmount(reward.getAmount() - fee.getAmount());
        }

        Bounty bounty = new Bounty(
                target.getUniqueId(),
                reward,
                playerSender.getUniqueId(),
                java.time.LocalDate.now().toString(),
                anonymousSetter

        );

        //check if sender has reward
        if (bounty.bountyReward.getType() == Material.AIR || bounty.bountyReward.getAmount() <= 0) {
            sender.sendMessage(Lang.NO_REWARD.toString());
            return;
        }

        List<String> stringListItems = eliteBounty.getConfig().getStringList("set.list");
        List<Material> listItems = new ArrayList<>();
        Boolean listType = eliteBounty.getConfig().getBoolean("set.list-type");
        for (String material : stringListItems) {
            listItems.add(Material.getMaterial(material));
        }

        //check reward against white/blacklist
        if(listType == true) {
            //Blacklist
            if (listItems.contains(bounty.bountyReward.getType())) {
                sender.sendMessage(String.format(Lang.BLACKLIST.toString(), String.join(", ", stringListItems)));
                return;
            }
        } else {
            //Whitelist
            if (!listItems.contains(bounty.bountyReward.getType())) {
                sender.sendMessage(String.format(Lang.WHITELIST.toString(), String.join(", ", stringListItems)));
                return;
            }
        }

        //checks if player has confirmed bounty
        if (eliteBounty.getConfig().getBoolean("set.require-confirm")) {
            if (confirming.containsKey(playerSender) && confirming.get(playerSender).equals(bounty)) {
                confirming.remove(playerSender);
            } else {
                confirming.put(playerSender, bounty);
                sender.sendMessage(String.format(Lang.CONFIRM_BOUNTY.toString(),
                        bounty.getFriendlyRewardName(),
                        bounty.bountyReward.getAmount(),
                        target.getName(),
                        WordUtils.capitalize(fee.getType().name().toLowerCase().replaceAll("_", " ")),
                        fee.getAmount(),
                        eliteBounty.getConfig().getInt("set.confirm-delay")
                ));

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(eliteBounty, new Runnable() {
                    public void run() {
                        confirming.remove(playerSender);
                    }
                }, (long) (eliteBounty.getConfig().getInt("set.confirm-delay") * 20));
                return;
            }
        }

        if (eliteBounty.saveBounty(bounty)) {
            sender.sendMessage(String.format(Lang.BOUNTY_SUCCESSFUL.toString(), target.getName()));

            if (eliteBounty.getConfig().getBoolean("set.message")) {
                TextComponent rewardText = new TextComponent(String.format(Lang.BOUNTY_REWARD.toString(),
                        bounty.getFriendlyRewardName(),
                        bounty.bountyReward.getAmount()));
                rewardText.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/bounty reward " + target.getName() + " " + sender.getName() + " " + EliteBounty.hiddenArg));
                rewardText.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To View Reward").create()));

                TextComponent bountyText = new TextComponent(String.format(Lang.BOUNTY_SET_ON.toString(),
                        bounty.anonymousSetter ? "Anonymous" : sender.getName(),
                        target.getName()
                ));
                bountyText.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/bounty info " + target.getName() + " " + sender.getName() + " " + EliteBounty.hiddenArg));
                bountyText.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click To View Bounty").create()));

                Bukkit.broadcastMessage(String.format(Lang.DIVIDER.toString(), "BOUNTY SET"));
                eliteBounty.getServer().spigot().broadcast(bountyText);
                eliteBounty.getServer().spigot().broadcast(rewardText);
            }

            playerSender.getInventory().removeItem(fee);
            playerSender.getInventory().setItemInMainHand(null);
        } else {
            sender.sendMessage(Lang.BOUNTY_ERROR.toString());
        }
    }
}
