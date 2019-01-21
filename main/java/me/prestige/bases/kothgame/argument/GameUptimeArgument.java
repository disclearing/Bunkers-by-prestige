package me.prestige.bases.kothgame.argument;

import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.DateTimeFormats;
import me.prestige.bases.Bases;
import me.prestige.bases.kothgame.EventTimer;
import me.prestige.bases.kothgame.faction.EventFaction;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GameUptimeArgument extends CommandArgument {
    private final Bases plugin;

    public GameUptimeArgument(final Bases plugin) {
        super("uptime", "Check the uptime of an event");
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        if(eventTimer.getRemaining() <= 0L) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }
        final EventFaction eventFaction = eventTimer.getEventFaction();
      //  sender.sendMessage(ChatColor.WHITE + "Up-time of " + ChatColor.GOLD + eventTimer.getName() + ChatColor.WHITE + " timer" + ((eventFaction == null) ? "" : (": " + ChatColor.BLUE + '(' + eventFaction.getDisplayName(sender) + ChatColor.BLUE + ')')) + ChatColor.WHITE + " is " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.WHITE + ", started at " + ChatColor.GOLD + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.WHITE + '.');
        return true;
    }
}
