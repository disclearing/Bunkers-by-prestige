package me.prestige.bases.faction.argument;

import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.Bases;
import me.prestige.bases.timer.type.StuckTimer;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionStuckArgument extends CommandArgument {
    private final Bases plugin;

    public FactionStuckArgument(final Bases plugin) {
        super("stuck", "Teleport to a safe position.", new String[]{"trap", "trapped"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        if(player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return true;
        }
        final StuckTimer stuckTimer = this.plugin.getTimerManager().stuckTimer;
        if(!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.WHITE + "Your " + stuckTimer.getDisplayName() + ChatColor.WHITE + " timer has a remaining " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(stuckTimer.getRemaining(player), true, true)+ChatColor.WHITE + '.');
            return true;
        }
        sender.sendMessage(ChatColor.WHITE + stuckTimer.getDisplayName() + ChatColor.WHITE + " timer has started. " + "\nTeleportation will commence in " + ChatColor.LIGHT_PURPLE + Bases.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.WHITE + ". " + "\nThis will cancel if you move more than " + 5 + " blocks.");
        return true;
    }
}
