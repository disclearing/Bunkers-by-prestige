package me.prestige.bases.kothgame.argument;

import com.customhcf.util.command.CommandArgument;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.FactionManager;
import me.prestige.bases.faction.claim.Claim;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.kothgame.CaptureZone;
import me.prestige.bases.kothgame.faction.CapturableFaction;
import me.prestige.bases.kothgame.faction.EventFaction;
import me.prestige.bases.kothgame.faction.KothFaction;
import me.prestige.bases.kothgame.tracker.KothTracker;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameSetCapzoneArgument extends CommandArgument {
    private final Bases plugin;

    public GameSetCapzoneArgument(final Bases plugin) {
        super("setcapzone", "Sets the capture zone of an event");
        this.plugin = plugin;
        this.aliases = new String[]{"setcapturezone", "setcap", "setcappoint", "setcapturepoint", "setcappoint"};
        this.permission = "command.game." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <eventName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set KOTH arena capture points");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final WorldEditPlugin worldEdit = this.plugin.getWorldEditPlugin();
        if(worldEdit == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set KOTH capture points.");
            return true;
        }
        final Selection selection = worldEdit.getSelection((Player) sender);
        if(selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if(selection.getWidth() < 2 || selection.getLength() < 2) {
            sender.sendMessage(ChatColor.RED + "Capture zones must be at least " + 2 + 'x' + 2 + '.');
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof CapturableFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not a capturable faction named '" + args[1] + "'.");
            return true;
        }
        final CapturableFaction capturableFaction = (CapturableFaction) faction;
        final Collection<Claim> claims = capturableFaction.getClaims();
        if(claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
            return true;
        }
        final Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
        final World world = claim.getWorld();
        final int minimumX = claim.getMinimumX();
        final int maximumX = claim.getMaximumX();
        final int minimumZ = claim.getMinimumZ();
        final int maximumZ = claim.getMaximumZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for(int x = minimumX; x <= maximumX; ++x) {
            for(int z = minimumZ; z <= maximumZ; ++z) {
                final Faction factionAt = factionManager.getFactionAt(world, x, z);
                if(!factionAt.equals(capturableFaction)) {
                    sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
                    return true;
                }
            }
        }
        CaptureZone captureZone;
            ((KothFaction) capturableFaction).setCaptureZone(captureZone = new CaptureZone(capturableFaction.getName(), claim, KothTracker.DEFAULT_CAP_MILLIS));
        sender.sendMessage(ChatColor.WHITE + "Set capture zone " + ChatColor.GOLD + captureZone.getDisplayName() + ChatColor.WHITE + " for faction " + ChatColor.GOLD +faction.getName() + ChatColor.WHITE + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        switch(args.length) {
            case 2: {
                return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
            }
            case 3: {
                final Faction faction2 = this.plugin.getFactionManager().getFaction(args[1]);
                return Collections.emptyList();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
}
