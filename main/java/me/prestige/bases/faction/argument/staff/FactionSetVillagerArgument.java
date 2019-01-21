package me.prestige.bases.faction.argument.staff;

import com.customhcf.util.PersistableLocation;
import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ClaimableFaction;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionSetVillagerArgument  extends CommandArgument {


    private final Bases plugin;

    public FactionSetVillagerArgument(final Bases plugin) {
        super("setvillager", "Sets villager at a location");
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
                ((ColorFaction) faction).setBuyVillager(new PersistableLocation(player.getLocation()));
                break;
            }
            case "SELL": {
                ((ColorFaction) faction).setSellVillager(new PersistableLocation(player.getLocation()));
                break;
            }
            case "ENCHANT": {
                ((ColorFaction) faction).setEnchantVillager(new PersistableLocation(player.getLocation()));
                break;
            }
            case "BLOCK": {
                ((ColorFaction) faction).setBlockVillager(new PersistableLocation(player.getLocation()));
                break;
            }
            default: {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
        }
        sender.sendMessage(ChatColor.WHITE + "Set villager location for " + ChatColor.GOLD + faction.getDisplayName(sender) + ChatColor.WHITE + " at your location"+ ChatColor.WHITE + '.');
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
                return result;
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

}
