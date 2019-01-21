package me.prestige.bases.listener;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.event.TickEvent;
import com.customhcf.util.BukkitUtils;
import me.prestige.bases.Bases;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CoreListener implements Listener {
    private final Bases plugin;
    private Set<UUID> afk;

    public CoreListener(final Bases plugin) {
        this.plugin = plugin;
        this.afk = new HashSet<>();
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpawn(CreatureSpawnEvent event){
        CraftLivingEntity craftLivingEntity = (CraftLivingEntity) event.getEntity();
        if(craftLivingEntity.getHandle().getClass() == me.prestige.bases.nms.Villager.class){
            System.out.println("Un-cancelling Creature Spawn Event");
            event.setCancelled(false);
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
            }
        }
                .runTask(this.plugin);
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(event.getSpawnLocation().clone().add(0.5D, 0.0D, 0.5D));
    }







    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerKickEvent event) {
        event.setLeaveMessage((String) null);
    }

}
