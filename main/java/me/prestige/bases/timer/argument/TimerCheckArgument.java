package me.prestige.bases.timer.argument;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.Bases;
import me.prestige.bases.timer.PlayerTimer;
import me.prestige.bases.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimerCheckArgument extends CommandArgument {
    private final Bases plugin;

    public TimerCheckArgument(final Bases plugin) {
        super("check", "Check remaining timer time");
        this.plugin = plugin;
        this.permission  = "command.timer." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Set<PlayerTimer> timerNames = new HashSet<>();
        if(Bukkit.getPlayer(args[1]) != null){
            for(Timer timers : plugin.getTimerManager().getTimers()){
                if(timers instanceof PlayerTimer) {
                    if (((PlayerTimer) timers).hasCooldown(Bukkit.getPlayer(args[1]))) {
                        timerNames.add((PlayerTimer) timers);
                    }
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        if(timerNames.isEmpty()){
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(args[1]).getName() + "'s "+ ChatColor.WHITE + "active timers");
            sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.GOLD + "None!");
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(args[1]).getName() + "'s "+ ChatColor.WHITE + "active timers");
        for(PlayerTimer timers : timerNames){
            sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.GOLD + timers.getDisplayName() + ChatColor.GRAY + " \u00BB " + ChatColor.WHITE + Bases.getRemaining(timers.getRemaining(Bukkit.getPlayer(args[1]).getUniqueId()), true));
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
