package me.prestige.bases.kothgame.argument;


import com.customhcf.util.command.CommandArgument;
import com.customhcf.util.cuboid.Cuboid;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.kothgame.faction.EventFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameSetAreaArgument extends CommandArgument {
    private static final int MIN_EVENT_CLAIM_AREA = 8;
    private final Bases plugin;

    public GameSetAreaArgument(final Bases plugin) {
        super("setarea", "Sets the area of an event");
        this.plugin = plugin;
        this.aliases = new String[]{"setclaim", "setclaimarea", "setland"};
        this.permission = "command.game." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kothName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set event claim areas");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final WorldEditPlugin worldEditPlugin = this.plugin.getWorldEditPlugin();
        if(worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set event claim areas.");
            return true;
        }
        final Player player = (Player) sender;
        final Selection selection = worldEditPlugin.getSelection(player);
        if(selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if(selection.getWidth() < MIN_EVENT_CLAIM_AREA || selection.getLength() < MIN_EVENT_CLAIM_AREA) {
            sender.sendMessage(ChatColor.RED + "Game claim areas must be at least " +MIN_EVENT_CLAIM_AREA + 'x' + MIN_EVENT_CLAIM_AREA + '.');
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
            ((EventFaction) faction).setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), (CommandSender) player);
        sender.sendMessage(ChatColor.WHITE + "Updated the claim for event " + ChatColor.GOLD  + faction.getName() + ChatColor.WHITE + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
