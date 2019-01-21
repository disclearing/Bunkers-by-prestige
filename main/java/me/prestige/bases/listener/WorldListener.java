package me.prestige.bases.listener;

import com.customhcf.base.event.TickEvent;
import com.customhcf.util.ParticleEffect;
import me.prestige.bases.Bases;
import me.prestige.bases.kothgame.EventTimer;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WorldListener implements Listener {

    private Bases plugin;

    public WorldListener(Bases plugin) {
        this.plugin = plugin;
    }




    @EventHandler
    public void onUnLoad(ChunkUnloadEvent e){
        for(Entity entity : e.getChunk().getEntities()){
            if(entity.getType() == EntityType.ENDER_PEARL || entity.getType() == EntityType.SPLASH_POTION ||entity.getType() == EntityType.VILLAGER || entity.getType() == EntityType.PLAYER || entity.getType() == EntityType.DROPPED_ITEM || entity.getType() == EntityType.ITEM_FRAME){
                e.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakBlock(BlockBreakEvent e){
        Block b = e.getBlock();
        final Location l = b.getLocation().clone();
        if(e.getBlock().getType().equals(Material.DIAMOND_ORE) && !e.getPlayer().getItemInHand().getType().equals(Material.STONE_PICKAXE)){
            e.setCancelled(true);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> l.getBlock().setType(Material.COBBLESTONE), 1);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ParticleEffect.CRITICAL_HIT.broadcast(l.clone(), 0, 1, 0, 5, 5);
                l.getBlock().setType(Material.DIAMOND_ORE);
            }, 7*20);
            if(!e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND, 1)).isEmpty()){
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(Material.DIAMOND, 1));
            }
        }
        if(e.getBlock().getType().equals(Material.GOLD_ORE) && !e.getPlayer().getItemInHand().getType().equals(Material.STONE_PICKAXE)){
            e.setCancelled(true);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> l.getBlock().setType(Material.COBBLESTONE), 1);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ParticleEffect.CRITICAL_HIT.broadcast(l.clone(), 0, 1, 0, 5, 5);
                l.getBlock().setType(Material.GOLD_ORE);
            }, 5*20);
            if(!e.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1)).isEmpty()){
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(Material.GOLD_INGOT, 1));
            }
        }
        if(e.getBlock().getType().equals(Material.IRON_ORE)){
            e.setCancelled(true);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> l.getBlock().setType(Material.COBBLESTONE), 1);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ParticleEffect.CRITICAL_HIT.broadcast(l.clone(), 0, 1, 0, 5, 5);
                l.getBlock().setType(Material.IRON_ORE);
            }, 5*20);
            if(!e.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1)).isEmpty()){
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(Material.IRON_INGOT, 1));
            }
        }
        if(e.getBlock().getType().equals(Material.COAL_ORE)){
            e.setCancelled(true);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> l.getBlock().setType(Material.COBBLESTONE), 1);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ParticleEffect.CRITICAL_HIT.broadcast(l.clone(), 0, 1, 0, 5, 5);
                l.getBlock().setType(Material.COAL_ORE);
            }, 3*20);
            if(!e.getPlayer().getInventory().addItem(new ItemStack(Material.COAL, 1)).isEmpty()){
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(Material.COAL, 1));
            }
        }
    }


}

