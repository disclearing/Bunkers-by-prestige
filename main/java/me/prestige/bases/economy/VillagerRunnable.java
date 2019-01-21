package me.prestige.bases.economy;

import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.nms.Villager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VillagerRunnable extends BukkitRunnable {
    @Override
    public void run() {
        if(EconomyListener.respawners.isEmpty()) return;
        for(Map.Entry<Villager, Long> entry : EconomyListener.respawners.entrySet()){
            if(System.currentTimeMillis() - entry.getValue() >= TimeUnit.MINUTES.toMillis(5)){
                Location loc = entry.getKey().getBukkitEntity().getLocation();
                final WorldServer mcWorld = ((CraftWorld)loc.getWorld()).getHandle();
                final Villager customEnt = new Villager(mcWorld);
                customEnt.setCustomName(entry.getKey().getJob());
                customEnt.setJob(entry.getKey().getJob());
                customEnt.setCustomNameVisible(true);
                customEnt.setPosition(loc.getX(), loc.getY(), loc.getZ());
                mcWorld.addEntity(customEnt, CreatureSpawnEvent.SpawnReason.CUSTOM);
                ((ColorFaction) Bases.getPlugin().getFactionManager().getFactionAt(loc)).broadcast(ChatColor.WHITE + "Your " + entry.getKey().getJob() + ChatColor.WHITE + " villager respawned.");
                mcWorld.removeEntity(entry.getKey());
                EconomyListener.respawners.remove(entry.getKey(), entry.getValue());
                continue;
            }else{
                entry.getKey().setCustomName( ChatColor.GRAY + ChatColor.stripColor(entry.getKey().getJob()) +  " respawns in " + Bases.getRemaining( TimeUnit.MINUTES.toMillis(5) - (System.currentTimeMillis() - entry.getValue()), true));
            }
        }
    }
}
