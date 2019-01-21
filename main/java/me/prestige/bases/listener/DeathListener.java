package me.prestige.bases.listener;

import com.customhcf.base.event.PlayerMoveByBlockEvent;
import com.customhcf.util.ItemBuilder;
import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.event.FactionDtrChangeEvent;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import me.prestige.bases.game.GameState;
import me.prestige.bases.inventory.inventorys.WaitingInventory;
import me.prestige.bases.timer.event.TimerExpireEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class DeathListener implements Listener {

    private Bases plugin;
    public static Map<UUID, Location> spectators;
    private Set<UUID> spamPrevention = Sets.newHashSet();
    public DeathListener(Bases plugin) {
        this.plugin = plugin;
        spectators = new HashMap<>();
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(spectators.containsKey(player.getUniqueId())) {
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            if(other.canSee(player)) {
                                System.err.println("Unhidden Spectator Detected: " + player.getName() + "->" + other.getName());
                                other.hidePlayer(player);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin , 20 , 20);
    }


    @EventHandler
    public void onInteract(InventoryClickEvent e) {
        if (spectators.containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        if (spectators.containsKey(e.getPlayer().getUniqueId())) {
            e.getItem().remove();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (spectators.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onIteract(PlayerInteractEvent e) {
        if (spectators.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onTeleport(PlayerTeleportEvent e) {
        if (spectators.containsKey(e.getPlayer().getUniqueId()) && e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (spectators.containsKey(e.getDamager().getUniqueId()) || spectators.containsKey(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (spectators.containsKey(e.getPlayer().getUniqueId())) {
            if (e.getTo().distance(spectators.get(e.getPlayer().getUniqueId())) >= 40) {
                e.setTo(spectators.get(e.getPlayer().getUniqueId()));
                e.getPlayer().teleport(spectators.get(e.getPlayer().getUniqueId()) , PlayerTeleportEvent.TeleportCause.UNKNOWN);

                if(!spamPrevention.contains(e.getPlayer().getUniqueId())) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You cannot move more than 40 blocks away from your death location");
                    spamPrevention.add(e.getPlayer().getUniqueId());
                    UUID uuid = e.getPlayer().getUniqueId();
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            spamPrevention.remove(uuid);
                        }
                    }.runTaskLater(Bases.getPlugin() , 100);
                }
                return;
            }
        }
    }


    @EventHandler
    public void onIteract(PlayerInteractEntityEvent e) {
        if (spectators.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(spectators.containsKey(event.getPlayer().getUniqueId())) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(event.getPlayer());
            }

            event.getPlayer().getInventory().setItem(0, teleport.clone());
            event.getPlayer().getInventory().setItem(1 ,  hub.clone());

        }
    }
    @EventHandler
    public void onTimerExpire(TimerExpireEvent e) {
        if (e.getTimer().equals(plugin.getTimerManager().waitingTimer)) {
            spectators.remove(e.getUserUUID().get());
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!spectators.containsKey(player.getUniqueId())) {
                    player.showPlayer( Bukkit.getPlayer(e.getUserUUID().get()));
                }
            }
            Bukkit.getPlayer(e.getUserUUID().get()).getInventory().addItem(new ItemBuilder(Material.STONE_PICKAXE).displayName(ChatColor.RED + "Starter Pickaxe").build());
            Bukkit.getPlayer(e.getUserUUID().get()).getInventory().addItem(new ItemBuilder(Material.COOKED_BEEF, 16).build());
            Bukkit.getPlayer(e.getUserUUID().get()).getInventory().addItem(new ItemBuilder(Material.STONE_AXE).displayName(ChatColor.RED + "Starter Axe").build());

            Bukkit.getPlayer(e.getUserUUID().get()).setFlying(false);
            Bukkit.getPlayer(e.getUserUUID().get()).setAllowFlight(false);
            Bukkit.getPlayer(e.getUserUUID().get()).teleport(plugin.getFactionManager().getColorFaction(e.getUserUUID().get()).getHome());
        }
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.getFactionManager().getColorFaction(event.getEntity().getUniqueId()) != null) {
            ColorFaction faction = plugin.getFactionManager().getColorFaction(event.getEntity().getUniqueId());
            FactionDtrChangeEvent dtrChangeEvent = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.MEMBER_DEATH, faction, faction.getDeathsUntilRaidable(), faction.getDeathsUntilRaidable() - 1);
            Bukkit.getPluginManager().callEvent(dtrChangeEvent);
            if (!dtrChangeEvent.isCancelled()) {
                faction.broadcast(ChatColor.YELLOW + "Since a member died, you lost 1 DTR bring you to a total of " + faction.getDtrColour() + dtrChangeEvent.getNewDtr() + ChatColor.YELLOW + " DTR.");
                faction.setDeathsUntilRaidable(faction.getDeathsUntilRaidable() - 1);
            }
            if (event.getEntity().getKiller() != null && plugin.getFactionManager().getColorFaction(event.getEntity().getKiller().getUniqueId()) != null) {
                event.setDeathMessage(faction.getColor() + event.getEntity().getName() + ChatColor.GRAY + "[" + plugin.getUserManager().getUser(event.getEntity().getUniqueId()).getKills() + "]" + ChatColor.YELLOW + " was slain by " + plugin.getFactionManager().getColorFaction(event.getEntity().getKiller().getUniqueId()).getColor() + event.getEntity().getKiller().getName() + ChatColor.GRAY + "[" + plugin.getUserManager().getUser(event.getEntity().getKiller().getUniqueId()).getKills() + ChatColor.GRAY + "]");
            } else {
                event.setDeathMessage(faction.getColor() + event.getEntity().getName() + ChatColor.YELLOW + " has died.");
            }
            plugin.getTimerManager().enderPearlTimer.clearCooldown(event.getEntity().getUniqueId());
            plugin.getTimerManager().spawnTagTimer.clearCooldown(event.getEntity().getUniqueId());
            if (dtrChangeEvent.getNewDtr() > 0) {
                plugin.getTimerManager().waitingTimer.setCooldown(event.getEntity(), event.getEntity().getUniqueId());
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(!spectators.containsKey(player.getUniqueId())) {
                        player.hidePlayer(event.getEntity());
                    }
                }

            }
            spectators.put(event.getEntity().getUniqueId(), event.getEntity().getLocation().clone());
            if(event.getEntity().getKiller() != null) {
                plugin.getEconomyManager().addBalance(event.getEntity().getKiller().getUniqueId(), 50);
            }
            if(dtrChangeEvent.getNewDtr() <= 0){
                for(Player player : faction.getOnlinePlayers()){
                    if(!spectators.containsKey(player.getUniqueId()))
                        return;
                }
                Bases.getPlugin().getGameManager().getDeadFactions().add(faction);
            }
            if(Bases.getPlugin().getGameManager().getDeadFactions().size() == (Bases.getPlugin().getFactionManager().getPlayerFactions().size() - 1)){
                for(PlayerFaction factions : Bases.getPlugin().getFactionManager().getPlayerFactions()){
                    if(factions instanceof ColorFaction) {
                        if (Bases.getPlugin().getGameManager().getDeadFactions().contains(((ColorFaction)factions)))
                            continue;
                        plugin.getGameManager().end((ColorFaction) factions);
                    }
                }
            }
        }
    }
    private ItemStack teleport = new ItemBuilder(Material.COMPASS).displayName(ChatColor.RESET + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Teleport").build();
    private ItemStack hub = new ItemBuilder(Material.WATCH).displayName(ChatColor.RESET + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Back to hub").build();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if(spectators.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getItem().isSimilar(hub)) {
                String to = "warzone-hub";
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(to);
                event.getPlayer().sendMessage(org.bukkit.ChatColor.AQUA + "Connecting you to " + to);
                event.getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            }
        }
    }
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!spectators.containsKey(event.getPlayer().getUniqueId())) return;
        if (!plugin.getTimerManager().waitingTimer.hasCooldown(event.getPlayer())) {
            event.getPlayer().getInventory().setItem(0, teleport.clone());
            event.getPlayer().getInventory().setItem(1 ,  hub.clone());

        }
        event.getPlayer().setAllowFlight(true);
        event.getPlayer().setFlying(true);
        event.setRespawnLocation(spectators.get(event.getPlayer().getUniqueId()).clone().add(0.5D, 0.0D, 0.5D));
    }
}
