package us.potatoboy.elitebounty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EliteBounty extends JavaPlugin {
    private static EliteBounty instance;

    private File bountyFile;
    private FileConfiguration bountyConfig;

    public static String hiddenArg = String.valueOf(new Random().nextInt(10000));

    @Override
    public void onEnable() {
        instance = this;

        this.getCommand("bounty").setExecutor(new BountyCommand());
        this.getCommand("bounty").setTabCompleter(new BountyTab());

        getServer().getPluginManager().registerEvents(new BountyListener(), this);

        loadLang();
        loadBounties();
        saveDefaultConfig();
    }

    public void loadLang() {

        File file = new File(getDataFolder(), "lang.yml");
        if (!file.exists()) {
            saveResource("lang.yml", false);
        }

        if (file == null) {
            getLogger().severe("Failed to load custom lang settings. Loading defaults");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Lang.setFile(config);
        getLogger().info("Loaded lang");
    }

    public void loadBounties() {
        bountyFile = new File(getDataFolder(), "bounties.yml");
        if (!bountyFile.exists()) {
            saveResource("bounties.yml", false);
        }

        if (bountyFile == null) {
            getLogger().severe("Failed to load bounties. Disabling plugin");

            getPluginLoader().disablePlugin(this);
            return;
        }

        bountyConfig = YamlConfiguration.loadConfiguration(bountyFile);
        getLogger().info("Loaded bounties");
    }

    public static EliteBounty getInstance() {
        return instance;
    }

    public FileConfiguration getBountyConfig() {
        return bountyConfig;
    }

    public File getBountyFile() {
        return bountyFile;
    }

    public ArrayList<Bounty> getBounties() {
        ArrayList<Bounty> bounties = new ArrayList<>();
        Set<String> bountyTargetList;
        try {
            bountyTargetList = bountyConfig.getConfigurationSection("Bounties").getKeys(false);
        } catch (Exception e) {
            return null;
        }
        for (String bountyTarget : bountyTargetList) {
            Set<String> bountyList;
            try {
                bountyList = bountyConfig.getConfigurationSection("Bounties." + bountyTarget).getKeys(false);
            } catch (Exception e) {
                return null;
            }

            for (String bounty: bountyList) {
                try {
                    Bounty currentBounty = new Bounty(
                            UUID.fromString(bountyTarget),
                            bountyConfig.getItemStack("Bounties." + bountyTarget + "." + bounty + ".BountyReward"),
                            UUID.fromString(bountyConfig.getString("Bounties." + bountyTarget + "."  + bounty + ".SetBy")),
                            bountyConfig.getString("Bounties." + bountyTarget + "."  + bounty + ".SetDate"),
                            bountyConfig.getBoolean("Bounties." + bountyTarget + "." + bounty + ".AnonymousSetter")
                    );
                    bounties.add(currentBounty);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return  bounties;
    }

    public HashSet<Bounty> getBountiesOnTarget(UUID id) {
        if (bountyConfig.getString("Bounties." + id) == null) return null;

        getLogger().info("here");
        HashSet<Bounty> bounties = new HashSet<>();
        Set<String> bountyList;

        try {
            bountyList = bountyConfig.getConfigurationSection("Bounties." + id).getKeys(false);
        } catch (Exception e) {
            return null;
        }
        getLogger().info("here");

        for (String bounty: bountyList) {
            try {
                Bounty b = new Bounty(
                        id,
                        bountyConfig.getItemStack("Bounties." + id + "." + bounty + ".BountyReward"),
                        UUID.fromString(bountyConfig.getString("Bounties." + id  + "." + bounty + ".SetBy")),
                        bountyConfig.getString("Bounties." + id + "." + bounty + ".SetDate"),
                        bountyConfig.getBoolean("Bounties." + id + "." + bounty + ".AnonymousSetter")
                );

                bounties.add(b);
            } catch (Exception e) {
                return null;
            }
        }
        getLogger().info("here");

        return bounties;
    }

    public Bounty getBountyFromIds(UUID target, UUID setter) {
        if (bountyConfig.getString("Bounties." + target + "." + setter) == null) return null;

        Bounty bounty = null;

        try {
            bounty = new Bounty(
                    target,
                    bountyConfig.getItemStack("Bounties." + target + "." + setter + ".BountyReward"),
                    setter,
                    bountyConfig.getString("Bounties." + target + "." + setter + ".SetDate"),
                    bountyConfig.getBoolean("Bounties." + target + "." + setter + ".AnonymousSetter")
            );
        } catch (Exception e) {
            return null;
        }

        return bounty;
    }

    public boolean saveBounty(Bounty bounty) {
        bountyConfig.set("Bounties." + bounty.target + "." + bounty.setBy, null);
        bountyConfig.set("Bounties." + bounty.target + "." + bounty.setBy + ".BountyReward", bounty.bountyReward);
        bountyConfig.set("Bounties." + bounty.target + "." + bounty.setBy + ".SetBy", bounty.setBy.toString());
        bountyConfig.set("Bounties." + bounty.target + "." + bounty.setBy + ".SetDate", bounty.setDate);
        bountyConfig.set("Bounties." + bounty.target + "." + bounty.setBy + ".AnonymousSetter", bounty.anonymousSetter);

        try {
            bountyConfig.save(bountyFile);

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    public boolean removeBounty(UUID targetId, UUID setterId) {
        bountyConfig.getConfigurationSection("Bounties." + targetId).set(setterId.toString(), null);

        if (bountyConfig.getConfigurationSection("Bounties." + targetId).getKeys(false).isEmpty()) {
            bountyConfig.getConfigurationSection("Bounties").set(targetId.toString(), null);
        }

        try {
            bountyConfig.save(bountyFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}