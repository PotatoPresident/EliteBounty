package us.potatoboy.elitebounty;

import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class RewardMenu implements  InventoryHolder {
    private final Inventory inv;

    public RewardMenu(ItemStack reward) {
        inv = Bukkit.createInventory(this, 27, "REWARD");

        if (reward.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta im = (BlockStateMeta)reward.getItemMeta();
            if (im.getBlockState() instanceof ShulkerBox) {
                ShulkerBox shulkerBox = (ShulkerBox)im.getBlockState();
                inv.setContents(shulkerBox.getInventory().getContents());
            } else {
                inv.setItem(13, reward);
            }
        } else {
            inv.setItem(13, reward);
        }
    }

    public void OpenInventory(final HumanEntity entity) {
        entity.openInventory(inv);
    }


    @Override
    public Inventory getInventory() {
        return null;
    }
}
