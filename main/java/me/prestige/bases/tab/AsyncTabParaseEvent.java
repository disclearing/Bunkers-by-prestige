package me.prestige.bases.tab;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncTabParaseEvent extends Event {
    private static final HandlerList handlers;
    private String input;
    private int ping = -1;
    private Player player;

    public AsyncTabParaseEvent(Player player, final String input) {
        super();
        this.input = input;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return AsyncTabParaseEvent.handlers;
    }

    public HandlerList getHandlers() {
        return AsyncTabParaseEvent.handlers;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    static {
        handlers = new HandlerList();
    }
}
