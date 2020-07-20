package us.potatoboy.elitebounty;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class Bounty {
    public UUID target;
    public ItemStack bountyReward;
    public UUID setBy;
    public String setDate;
    public Boolean anonymousSetter;

    public Bounty(UUID target, ItemStack bountyReward, UUID setBy, String setDate, Boolean anonymousSetter) {
        this.target = target;
        this.bountyReward = bountyReward;
        this.setBy = setBy;
        this.setDate = setDate;
        this.anonymousSetter = anonymousSetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounty bounty = (Bounty) o;
        return Objects.equals(target, bounty.target) &&
                Objects.equals(bountyReward, bounty.bountyReward) &&
                Objects.equals(setBy, bounty.setBy) &&
                Objects.equals(setDate, bounty.setDate) &&
                Objects.equals(anonymousSetter, bounty.anonymousSetter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, bountyReward, setBy, setDate, anonymousSetter);
    }
}
