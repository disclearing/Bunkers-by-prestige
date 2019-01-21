package me.prestige.bases.kothgame;

import com.customhcf.util.command.ArgumentExecutor;
import me.prestige.bases.Bases;
import me.prestige.bases.kothgame.argument.*;

public class EventExecutor extends ArgumentExecutor {
    public EventExecutor(final Bases plugin) {
        super("game");
        addArgument(new GameCancelArgument(plugin));
        addArgument(new GameCreateArgument(plugin));
        addArgument(new GameDeleteArgument(plugin));
        addArgument(new GameRenameArgument(plugin));
        addArgument(new GameSetAreaArgument(plugin));
        addArgument(new GameSetCapzoneArgument(plugin));
        addArgument(new GameStartArgument(plugin));
        addArgument(new GameScheduleArgument(plugin));
        addArgument(new GameUptimeArgument(plugin));
    }
}
