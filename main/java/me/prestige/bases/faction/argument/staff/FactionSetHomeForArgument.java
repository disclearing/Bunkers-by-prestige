package me.prestige.bases.faction.argument.staff;

import com.customhcf.util.command.CommandArgument;
import com.customhcf.util.cuboid.Cuboid;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.claim.Claim;
import me.prestige.bases.faction.type.ClaimableFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionSetHomeForArgument  extends CommandArgument {
    private static final int MIN_EVENT_CLAIM_AREA;


    private final Bases plugin;

    public FactionSetHomeForArgument(final Bases plugin) {
        super("sethomefor", "Sethome for a faction");
        this.plugin = plugin;
        this.permission = "command.faction." + this.getName();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factioName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Player player = (Player) sender;
        Faction faction = plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof PlayerFaction)){
            sender.sendMessage(ChatColor.RED + "This is not a claimable faction (cannot contain claims)");
            return true;
        }
        ((PlayerFaction) faction).setHome(player.getLocation());
        sender.sendMessage(ChatColor.WHITE + "Set home for " + ChatColor.GOLD + faction.getDisplayName(sender) + ChatColor.WHITE + " at your location"+ ChatColor.WHITE + '.');
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
            default: {
                return Collections.emptyList();
            }
        }
    }
    static {
        MIN_EVENT_CLAIM_AREA = 2;
    }

}
