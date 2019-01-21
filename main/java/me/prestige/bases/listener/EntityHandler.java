package me.prestige.bases.listener;

import me.prestige.bases.Bases;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityHandler implements Listener {

    private Bases plugin;

    public EntityHandler(Bases plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getEntity().getType().equals(EntityType.VILLAGER)) {
            e.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getEntity().getType().equals(EntityType.VILLAGER)) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(EntitySpawnEvent e){
        if(e.getEntityType() != EntityType.VILLAGER){
            if(e.getEntityType() != EntityType.PLAYER){
                if(e.getEntityType() != EntityType.ARROW) {
                    if(e.getEntityType() != EntityType.SPLASH_POTION) {
                        if(e.getEntityType() != EntityType.DROPPED_ITEM) {
                            e.setCancelled(true);
                        }
                }
            }
        }
    }


}
}
