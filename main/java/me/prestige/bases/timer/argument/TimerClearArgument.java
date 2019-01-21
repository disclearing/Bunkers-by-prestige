package me.prestige.bases.timer.argument;

import com.customhcf.util.command.CommandArgument;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import me.prestige.bases.Bases;
import me.prestige.bases.timer.GlobalTimer;
import me.prestige.bases.timer.Timer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by TREHOME on 04/11/2016.
 */
public class TimerClearArgument extends CommandArgument {
    private static final Pattern WHITESPACE_TRIMMER;

    static {
        WHITESPACE_TRIMMER = Pattern.compile("\\s");
    }

    private final Bases plugin;

    public TimerClearArgument(final Bases plugin) {
        super("clear", "start timer time");
        this.plugin = plugin;
        this.permission = "command.timer." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <timerName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        GlobalTimer globalTimer = null;
        for (final Timer timer : this.plugin.getTimerManager().getTimers()) {
            if (timer instanceof GlobalTimer && WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("").equalsIgnoreCase(args[1])) {
                globalTimer = (GlobalTimer) timer;
                break;
            }
        }
        if (globalTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }

            globalTimer.setExpectedExpire((long) 0);

        sender.sendMessage(ChatColor.WHITE + "Cleared " + globalTimer.getDisplayName() + ChatColor.WHITE + " timer" + '.');

        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            return FluentIterable.from(plugin.getTimerManager().getTimers()).filter(new Predicate<Timer>() {
                public boolean apply(final Timer timer) {
                    return timer instanceof GlobalTimer;
                }
            }).transform(new Function<Timer, String>() {
                @Nullable
                public String apply(final Timer timer) {
                    return WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("");
                }
            }).toList();
        }
        return Collections.emptyList();
    }
}