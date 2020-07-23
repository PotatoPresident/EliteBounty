package us.potatoboy.elitebounty.command;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.potatoboy.elitebounty.Bounty;
import us.potatoboy.elitebounty.EliteBounty;
import us.potatoboy.elitebounty.Lang;

import java.util.ArrayList;
import java.util.HashSet;

public class CompassCommand extends AbstractCommand{

    public static final String NAME = "Compass";

    public static final String PERMISSION = "elitebounty.compass";

    public static final String USAGE = "/bounty compass [reset]";

    public static HashSet<Player> confirming = new HashSet<>();

    public CompassCommand(CommandSender sender) {
        super(sender, NAME, PERMISSION, USAGE);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPerm()) {
            sender.sendMessage(Lang.INVALID_PERMISSIONS.toString());
            return;
        }

        if (!isSenderPlayer()) {
            sender.sendMessage(Lang.CANT_CONSOLE.toString());
            sendUsage();
            return;
        }
        Player playerSender = (Player) sender;

        if (args.length > 1 && args[1].equals("reset")) {
            playerSender.setCompassTarget(playerSender.getWorld().getSpawnLocation());
            sender.sendMessage(Lang.COMPASS_RESET.toString());
            return;
        }

        EliteBounty eliteBounty = EliteBounty.getInstance();

        ArrayList<Bounty> bounties = eliteBounty.getBounties();
        if (bounties == null || bounties.isEmpty()) {
            sender.sendMessage(Lang.NO_BOUNTIES.toString());
            return;
        }

        ItemStack fee = new ItemStack(
                Material.getMaterial(eliteBounty.getConfig().getString("compass.fee.type")),
                eliteBounty.getConfig().getInt("compass.fee.amount")
        );

        //check if sender can afford fee
        if (!playerSender.getInventory().contains(fee.getType(), fee.getAmount())) {
            sender.sendMessage(String.format(Lang.CANT_PAY_FEE.toString(), fee.getType(), fee.getAmount()));
            return;
        }

        if (eliteBounty.getConfig().getBoolean("compass.require-confirm")) {
            if (confirming.contains(playerSender)) {
                confirming.remove(playerSender);
            } else {
                confirming.add(playerSender);
                sender.sendMessage(String.format(Lang.CONFIRM_COMPASS.toString(),
                        WordUtils.capitalize(fee.getType().name().toLowerCase().replaceAll("_", " ")),
                        fee.getAmount(),
                        eliteBounty.getConfig().getInt("compass.confirm-delay")
                ));

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(eliteBounty, new Runnable() {
                    public void run() {
                        confirming.remove(playerSender);
                    }
                }, eliteBounty.getConfig().getInt("compass.confirm-delay") * 20);
                return;
            }
        }

        Location result = null;
        double lastDis = Double.MAX_VALUE;
        for (Player player : playerSender.getWorld().getPlayers()) {
            if (player == playerSender) continue;

            double dis = playerSender.getLocation().distance(player.getLocation());
            if (dis < lastDis) {
                lastDis = dis;
                result = player.getLocation();
            }
        }

        if (result != null) {
            playerSender.setCompassTarget(result);
            sender.sendMessage(Lang.COMPASS_SET.toString());
            playerSender.getInventory().removeItem(fee);
        } else {
            sender.sendMessage(Lang.NO_BOUNTIES_WORLD.toString());
            return;
        }
    }
}
