package me.prestige.bases.faction.argument.staff;

import com.customhcf.util.PersistableLocation;
import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.Bases;
import me.prestige.bases.economy.EconomyListener;
import me.prestige.bases.economy.VillagerRunnable;
import me.prestige.bases.faction.type.ClaimableFaction;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.nms.Villager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionSpawnVillagerArgument  extends CommandArgument {


    private final Bases plugin;

    public FactionSpawnVillagerArgument(final Bases plugin) {
        super("spawnvillager", "Spawns a vilager for a faction at its location");
        this.plugin = plugin;
        this.permission = "command.faction." + this.getName();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factioName> <villagerType>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player) sender;
        Faction faction = plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof ColorFaction)){
            sender.sendMessage(ChatColor.RED + "This is not a claimable faction (cannot contain claims)");
            return true;
        }
        final String upperCase = args[2].toUpperCase();
        switch(upperCase) {
            case "BUY": {
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getBuyVillager(), ((ColorFaction) faction).getColor() + "Combat Shop");
                break;
            }
            case "SELL": {
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getSellVillager(), ((ColorFaction) faction).getColor() + "Sell Items");
                break;
            }
            case "ENCHANT": {
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getEnchantVillager(), ((ColorFaction) faction).getColor() + "Tim the Enchanter");
                break;
            }
            case "BLOCK": {
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getBlockVillager(), ((ColorFaction) faction).getColor() + "Build Shop");
                break;
            }
            case "ALL": {
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getBlockVillager(), ((ColorFaction) faction).getColor() + "Build Shop");
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getBuyVillager(), ((ColorFaction) faction).getColor() + "Combat Shop");
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getSellVillager(), ((ColorFaction) faction).getColor() + "Sell Items");
                plugin.getGameManager().spawnVillager(((ColorFaction) faction).getEnchantVillager(), ((ColorFaction) faction).getColor() + "Tim the Enchanter");
                break;
            }
            default: {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
        }
        sender.sendMessage(ChatColor.WHITE + "You should have spawned a "+ args[2] + " for the faction " + faction.getDisplayName(sender) );
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        switch (args.length){
            case 2 :{
                final List<String> results = new ArrayList<>(plugin.getFactionManager().getClaimableFactions().size());
                for(ClaimableFaction claimableFaction : plugin.getFactionManager().getClaimableFactions()){
                    results.add(claimableFaction.getName());
                }
                return results;
            }
            case 3 :{
                List<String> result = new ArrayList<>();
                result.add("BUY");
                result.add("SELL");
                result.add("BLOCK");
                result.add("ENCHANT");
                result.add("ALL");
                return result;
            }
            default: {
                return Collections.emptyList();
            }
        }
    }



}
