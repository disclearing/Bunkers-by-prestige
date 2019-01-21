package me.prestige.bases.timer;

import com.customhcf.util.command.ArgumentExecutor;
import me.prestige.bases.Bases;
import me.prestige.bases.timer.argument.TimerCheckArgument;
import me.prestige.bases.timer.argument.TimerClearArgument;
import me.prestige.bases.timer.argument.TimerSetArgument;
import me.prestige.bases.timer.argument.TimerStartArugment;

public class TimerExecutor extends ArgumentExecutor {
    public TimerExecutor(final Bases plugin) {
        super("timer");
        this.addArgument(new TimerCheckArgument(plugin));
        this.addArgument(new TimerClearArgument(plugin));
        this.addArgument(new TimerSetArgument(plugin));
        this.addArgument(new TimerStartArugment(plugin));
    }
}
