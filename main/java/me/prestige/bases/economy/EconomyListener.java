package me.prestige.bases.economy;

import me.prestige.bases.Bases;
import me.prestige.bases.economy.gui.BlockShop;
import me.prestige.bases.economy.gui.CombatShop;
import me.prestige.bases.economy.gui.EnchantShop;
import me.prestige.bases.economy.gui.SellShop;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.nms.*;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftVillager;
import org.bukkit.entity.*;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EconomyListener implements Listener {

    private Bases plugin;
    public static Map<me.prestige.bases.nms.Villager, Long> respawners;

    public EconomyListener(Bases plugin){
        this.plugin = plugin;
        respawners = new ConcurrentHashMap<>();
    }


    @EventHandler
    public void onDamageVillager(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Villager){
            if(e.getDamager() instanceof Player){
                if(((Villager) e.getEntity()).getCustomName().contains("respawns")){
                    e.setCancelled(true);
                    return;
                }
                Faction factionAt = plugin.getFactionManager().getFactionAt(e.getEntity().getLocation());
                ColorFaction colorFaction = plugin.getFactionManager().getColorFaction(e.getDamager().getUniqueId());
                if(factionAt.equals(colorFaction)){
                    ((Player) e.getDamager()).sendMessage(ChatColor.WHITE + "You " + ChatColor.RED + "cannot" + ChatColor.WHITE + " damage your own teams villager!");
                    e.setCancelled(true);
                }else{
                    e.setCancelled(true);
                    ((Villager) e.getEntity()).damage(e.getDamage());
                }
            }
        }
    }


    @EventHandler
    public void onIneract(PlayerInteractEntityEvent e){
        if(e.getRightClicked() instanceof Villager){
            e.setCancelled(true);
            Entity villager = e.getRightClicked();
            if(((CraftVillager)villager).getHandle().getName().contains("respawn")) {
                e.setCancelled(true);
                return;
            }
            Faction villagerFaction = plugin.getFactionManager().getFactionAt(villager.getLocation());
            ColorFaction colorFaction = plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId());
            if(villagerFaction.equals(colorFaction)){
                if(((CraftVillager)villager).getHandle().getName().contains("Combat")) {
                    new CombatShop(e.getPlayer()).showMenu(e.getPlayer());
                    return;
                }
                if(((CraftVillager)villager).getHandle().getName().contains("Build")) {
                    new BlockShop(e.getPlayer()).showMenu(e.getPlayer());
                    return;
                }
                if(((CraftVillager)villager).getHandle().getName().toLowerCase().contains("enchant")) {
                    new EnchantShop(e.getPlayer()).showMenu(e.getPlayer());
                    return;
                }
                if(((CraftVillager)villager).getHandle().getName().toLowerCase().contains("sell")) {
                    new SellShop(e.getPlayer()).showMenu(e.getPlayer());
                    return;
                }
            }else if(villagerFaction instanceof ColorFaction && ((ColorFaction) villagerFaction).getDeathsUntilRaidable() <= 0){

            }else{
                return;
            }
            return;
        }
    }




    @EventHandler
    public void onDeath(EntityDeathEvent e){
        if(e.getEntity() instanceof Villager){
            final WorldServer mcWorld = ((CraftWorld)e.getEntity().getLocation().getWorld()).getHandle();
            final me.prestige.bases.nms.Villager customEnt = new me.prestige.bases.nms.Villager(mcWorld);
            customEnt.setCustomName(ChatColor.GRAY + ChatColor.stripColor(e.getEntity().getCustomName()) + " respawns in " + Bases.getRemaining(TimeUnit.MINUTES.toMillis(5) - (System.currentTimeMillis()), true));
            customEnt.setCustomNameVisible(true);
            customEnt.setJob(e.getEntity().getCustomName());
            customEnt.setPosition(e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ());
            mcWorld.addEntity(customEnt, CreatureSpawnEvent.SpawnReason.CUSTOM);
            respawners.put(customEnt, System.currentTimeMillis());
        }
    }


    @EventHandler
    public void onPotion(PotionEffectAddEvent e){
        if(e.getEntity() instanceof Villager){
            e.setCancelled(true);
        }
    }

}
