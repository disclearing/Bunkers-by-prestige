package me.prestige.bases.listener;

import me.prestige.bases.Bases;
import me.prestige.bases.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class LobbyListener implements Listener {

    private Bases plugin;

    public LobbyListener(Bases plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
            e.setFoodLevel(20);
            e.getEntity().setHealth(20);
        }
    }
}
