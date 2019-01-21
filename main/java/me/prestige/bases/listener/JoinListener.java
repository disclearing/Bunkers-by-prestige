package me.prestige.bases.listener;

import me.prestige.bases.Bases;
import me.prestige.bases.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.concurrent.TimeUnit;

public class JoinListener implements Listener {

    private Bases plugin;

    public JoinListener(Bases plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        if (plugin.getGameManager().getGameState().equals(GameState.WAITING)) {
            if(e.getResult() == PlayerLoginEvent.Result.ALLOWED) {
                e.allow();
            }

        }else{
            if(!e.getPlayer().isOp()) return;
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Game has already started. Please join another one.");
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            plugin.getInventoryHandler().waitingInventory.applyTo(e.getPlayer(), true, true);
        }
    }

}
