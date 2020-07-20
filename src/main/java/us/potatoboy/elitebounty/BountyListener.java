package us.potatoboy.elitebounty;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.UUID;

public class BountyListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player && event.getEntity().getKiller() != event.getEntity()) {
            UUID killedId = event.getEntity().getUniqueId();
            Player killer = event.getEntity().getKiller();
            EliteBounty eliteBounty = EliteBounty.getInstance();
            FileConfiguration config = eliteBounty.getBountyConfig();

            if (config.getString("Bounties." + killedId) != null) {
                HashSet<Bounty> completedBounties = eliteBounty.getBountiesOnTarget(killedId);

                if(!killer.hasPermission("elitebounty.claim")) return;

                for (Bounty bounty : completedBounties) {
                    if (killer.getUniqueId().equals(bounty.setBy)) return;

                    if (eliteBounty.getConfig().getBoolean("claim.claim-message")) {
                        Bukkit.broadcastMessage(String.format(Lang.DIVIDER.toString(), "BOUNTY COMPLETE"));
                        Bukkit.broadcastMessage(String.format(Lang.TARGET_KILLED.toString(), killer.getName(), event.getEntity().getName()));
                        Bukkit.broadcastMessage(String.format(Lang.BOUNTY_REWARD.toString(),
                                bounty.bountyReward.getType().name(),
                                bounty.bountyReward.getAmount()));
                    }

                    if (!eliteBounty.getConfig().getBoolean("claim.death-message")) {
                        event.setDeathMessage(null);
                    }

                    if (killer.getInventory().firstEmpty() == -1) {
                        killer.getWorld().dropItem(killer.getLocation(), bounty.bountyReward);
                        killer.sendMessage(Lang.INVENTORY_FULL.toString());
                    } else {
                        event.getEntity().getKiller().getInventory().addItem(bounty.bountyReward);
                    }

                    if (!eliteBounty.removeBounty(bounty.target, bounty.setBy)) {
                        killer.sendMessage(Lang.BOUNTY_REMOVE_ERROR.toString());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RewardMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RewardMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerCommandPreprocces(final PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args[0] == "/bountysecretinfoRandomLettersKJHSFEVF") {

        }
    }
}
