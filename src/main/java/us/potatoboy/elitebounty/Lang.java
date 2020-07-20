package us.potatoboy.elitebounty;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
    TITLE("title-name", "&c[EliteBounty]", false),
    DIVIDER("divider", "&c------------%s-----------", false),
    BOUNTY_LIST("bounty-list", "%s - Bounty: &a%s x%s", false),
    NO_BOUNTIES("no-bounties", "No bounties to display", true),
    BOUNTY_TARGET("bounty-target", "&7TARGET: &r%s", false),
    BOUNTY_OWNER("bounty-owner", "&7SET BY: &r%s", false),
    BOUNTY_DATE("bounty-date", "&7SET DATE: &r%s", false),
    BOUNTY_REWARD("bounty-reward", "&7REWARD: &a%s x%s", false),
    NO_BOUNTY_ON_PLAYER("no-bounty-on-player", "No Bounties On That Player", true),
    INVENTORY_FULL("inventory-full", "Inventory Full. Dropping Reward", true),
    BOUNTY_REMOVE_ERROR("bounty-remove-error", "Something went wrong removing bounty", true),
    INVALID_PLAYER("invalid-player", "Invalid Player", true),
    TARGET_HAS_BOUNTY("target-has-bounty", "Target Has Maximum Bounties (%s)", true),
    NO_REWARD("no-reward", "Bounty Reward Must Be In Main Hand", true),
    BOUNTY_SUCCESSFUL("bounty-successful", "Successfully set bounty on &c%s", true),
    BOUNTY_ERROR("bounty-error", "Something Went Wrong", true),
    TARGET_KILLED("target-killed", "&c%s &rEliminated &c%s", false),
    BOUNTY_SET_ON("bounty-set-on", "&c%s &rSet Bounty On &c%s", false),
    INVALID_PERMISSIONS("invalid-permissions", "Invalid Permissions", true),
    CANT_CONSOLE("cant-console", "Can't Run This Command From Console", true),
    BOUNTY_REMOVED("bounty-removed", "Bounty Removed", true),
    MAX_BOUNTIES_SET("max-bounties-set", "You Have Set The Maximum Bounties", true),
    CANT_PAY_FEE("cant-pay-fee", "Can't Pay Fee &a(%s x%s)", true),
    WHITELIST("whitelist", "Reward Not Allowed. Allowed Items: &a%s", true),
    BLACKLIST("blacklist", "Reward Not Allowed. Banned Items: &a%s", true),
    CONFIRM_BOUNTY("confirm-bounty", "Run Command Again To Confirm Bounty Of &a%s x%s&r On &7%s&r for &a%s x%s&r Fee (%ss)", true),
    CONFIRM_REMOVE("confirm-remove", "Run Command Again To Remove Bounty On &7%s&r (%ss)", true),
    BOUNTIES_ON_PLAYER("bounties-on-player", "Listing All Bounties On &7%s", false),
    TRUE_FALSE("true-false", "You must type \"true\" or \"false\"", true);


    private String path;
    private String def;
    private boolean title;
    private static YamlConfiguration LANG;

    Lang(String path, String start, boolean title) {
        this.path = path;
        this.def = start;
        this.title = title;
    }

    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }


    @Override
    public String toString() {
        if (this.title) {
            String title = ChatColor.translateAlternateColorCodes('&', LANG.getString(TITLE.path, TITLE.def) + " " + ChatColor.RESET);
            return title + ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
        } else {
            return  ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
        }
    }

    public String getPath() {
        return this.path;
    }
}
