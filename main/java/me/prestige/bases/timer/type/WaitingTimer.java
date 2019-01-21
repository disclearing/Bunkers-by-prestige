package me.prestige.bases.timer.type;

import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import me.prestige.bases.listener.DeathListener;
import me.prestige.bases.timer.GlobalTimer;
import me.prestige.bases.timer.PlayerTimer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WaitingTimer extends PlayerTimer {

    public WaitingTimer() {
        super("Reviving", TimeUnit.SECONDS.toMillis(30));
    }

    @Override
    public void onExpire(UUID userUUID) {
        super.onExpire(userUUID);
        if(Bukkit.getPlayer(userUUID) == null){
            return;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player)) return;
        if(hasCooldown((Player) e.getDamager())){
            e.setCancelled(true);
        }
    }


    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GOLD + "";
    }
}
