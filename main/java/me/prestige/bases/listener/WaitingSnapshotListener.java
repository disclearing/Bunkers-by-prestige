package me.prestige.bases.listener;

import com.customhcf.base.BasePlugin;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.FactionMember;
import me.prestige.bases.faction.event.PlayerJoinFactionEvent;
import me.prestige.bases.faction.struct.ChatChannel;
import me.prestige.bases.faction.struct.Role;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import me.prestige.bases.game.GameState;
import me.prestige.bases.inventory.inventorys.WaitingInventory;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WaitingSnapshotListener implements Listener {

    private Bases plugin;
    private TObjectLongHashMap<UUID> throttle = new TObjectLongHashMap<>();
    public WaitingSnapshotListener(Bases plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event){
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.setJoinMessage(null);
        Bukkit.broadcastMessage(org.bukkit.ChatColor.GREEN + event.getPlayer().getName() +  ChatColor.YELLOW + " has joined. " + ChatColor.GREEN + "(" + Bukkit.getOnlinePlayers().size()  +"/20)");

        new BukkitRunnable(){
            @Override
            public void run() {
                if(event.getPlayer().isOnline() && plugin.getGameManager().getGameState() == GameState.WAITING && plugin.getFactionManager().getColorFaction(event.getPlayer().getUniqueId()) == null){
                    event.getPlayer().sendMessage("Automatically putting you in a team");
                    randomJoin(event.getPlayer());
                }
            }
        }.runTaskLater(plugin , 200);
    }
    @EventHandler
    public void onInteract(EntityDamageEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteract(EntityDamageByEntityEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteract(FoodLevelChangeEvent event){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onInteract(InventoryClickEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.getItem().remove();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        throttle.remove(event.getPlayer().getUniqueId());
        if (plugin.getGameManager().getGameState().equals(GameState.WAITING)) {
            Player player = event.getPlayer();
            ColorFaction green =  plugin.getFactionManager().getColorFaction("Green");
            ColorFaction blue =   plugin.getFactionManager().getColorFaction("Blue");
            ColorFaction yellow =   plugin.getFactionManager().getColorFaction("Yellow");
            ColorFaction red =   plugin.getFactionManager().getColorFaction("Red");
            green.setMember(player , null , true);
            blue.setMember(player , null , true);
            yellow.setMember(player , null , true);
            red.setMember(player , null , true);

        }
    }
    public boolean isThrottled(UUID uuid){
        if(throttle.containsKey(uuid)){
            return System.currentTimeMillis() - throttle.get(uuid) < 0;
        }
        return false;
    }
    public void throttle(UUID uuid){
        throttle.put(uuid , System.currentTimeMillis() + 2500);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getItem() == null) return;
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)){
            if(e.getItem().isSimilar(WaitingInventory.blue)){
                if(!isThrottled(e.getPlayer().getUniqueId())) {
                    plugin.ensureLeaveFromOthers(e.getPlayer().getUniqueId() , plugin.getFactionManager().getColorFaction("Blue"));
                    ColorFaction faction = plugin.getFactionManager().getColorFaction("Blue");
                    if(plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()) == faction){
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                        return;
                    }
                    if (faction.setMember(e.getPlayer(), new FactionMember(e.getPlayer(), ChatChannel.FACTION, Role.MEMBER))) {
                        Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(e.getPlayer().getUniqueId(), faction, plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId())));
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                    }
                }else{
                    e.getPlayer().sendMessage(ChatColor.RED + "You can not spam team join.");
                }
                e.setCancelled(true);
            }else if(e.getItem().isSimilar(WaitingInventory.green)){
                if(!isThrottled(e.getPlayer().getUniqueId())) {
                    plugin.ensureLeaveFromOthers(e.getPlayer().getUniqueId() , plugin.getFactionManager().getColorFaction("Green"));
                    ColorFaction faction = plugin.getFactionManager().getColorFaction("Green");
                    if(plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()) == faction){
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                        return;
                    }
                    if (faction.setMember(e.getPlayer(), new FactionMember(e.getPlayer(), ChatChannel.FACTION, Role.MEMBER))) {
                        Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(e.getPlayer().getUniqueId(), faction, plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId())));
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                    }
                }else{
                    e.getPlayer().sendMessage(ChatColor.RED + "You can not spam team join.");
                }
                e.setCancelled(true);
            }else if(e.getItem().isSimilar(WaitingInventory.random)){
                if(!isThrottled(e.getPlayer().getUniqueId())) {
                    randomJoin(e.getPlayer());
                    e.setCancelled(true);
                }else{
                    e.getPlayer().sendMessage(ChatColor.RED + "You can not spam team join.");
                    e.setCancelled(true);
                    return;
                }

            }else if(e.getItem().isSimilar(WaitingInventory.red)){
                if(!isThrottled(e.getPlayer().getUniqueId())) {
                    plugin.ensureLeaveFromOthers(e.getPlayer().getUniqueId() , plugin.getFactionManager().getColorFaction("Red"));

                    ColorFaction faction = plugin.getFactionManager().getColorFaction("Red");
                    if(plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()) == faction){
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                        return;
                    }

                    if (faction.setMember(e.getPlayer(), new FactionMember(e.getPlayer(), ChatChannel.FACTION, Role.MEMBER))) {
                        Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(e.getPlayer().getUniqueId(), faction, plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId())));
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                    }
                }else{
                    e.getPlayer().sendMessage(ChatColor.RED + "You can not spam team join.");
                }
                e.setCancelled(true);
            }else {
                if(!e.getPlayer().getItemInHand().isSimilar(WaitingInventory.yellow)){
                    return;
                }
                if(!isThrottled(e.getPlayer().getUniqueId())) {
                    plugin.ensureLeaveFromOthers(e.getPlayer().getUniqueId() , plugin.getFactionManager().getColorFaction("Yellow"));

                    ColorFaction faction = plugin.getFactionManager().getColorFaction("Yellow");
                    if(plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()) == faction){
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                        return;
                    }
                    if (faction.setMember(e.getPlayer(), new FactionMember(e.getPlayer(), ChatChannel.FACTION, Role.MEMBER))) {
                        Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(e.getPlayer().getUniqueId(), faction, plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId())));
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(e.getPlayer().getUniqueId()).getDisplayName(e.getPlayer()));
                        throttle(e.getPlayer().getUniqueId());
                    }
                }else{
                    e.getPlayer().sendMessage(ChatColor.RED + "You can not spam team join.");
                }
                e.setCancelled(true);
            }
        }
    }


    public void randomJoin(Player player){
        if(plugin.getFactionManager().getColorFaction(player.getUniqueId()) != null){
            player.sendMessage(ChatColor.RED + "You already have a team");
            return;
        }
        int count1 = plugin.getFactionManager().getColorFaction("Green").getMembers().size();
        int count2 = plugin.getFactionManager().getColorFaction("Blue").getMembers().size();
        int count3 = plugin.getFactionManager().getColorFaction("Yellow").getMembers().size();
        int count4 = plugin.getFactionManager().getColorFaction("Red").getMembers().size();
        int lowest2 = Math.min(count3, count4);
        int lowest1 = Math.min(count1, count2);
        int lowest3 = Math.min(lowest2, lowest1);
        if (lowest3 == count1) {
            ColorFaction faction = plugin.getFactionManager().getColorFaction("Green");
            if(plugin.getFactionManager().getColorFaction(player.getUniqueId()) == faction){
                player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
                throttle(player.getUniqueId());
                return;
            }
            plugin.ensureLeaveFromOthers(player.getUniqueId() , plugin.getFactionManager().getColorFaction("Green"));
            if (plugin.getFactionManager().getColorFaction("Green").setMember(player, new FactionMember(player, ChatChannel.FACTION, Role.MEMBER))) {
                Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(player.getUniqueId(), plugin.getFactionManager().getColorFaction("Green"), plugin.getFactionManager().getColorFaction(player.getUniqueId())));
            }
            throttle(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
            return;
        } else if (lowest3 == count2) {
            ColorFaction faction = plugin.getFactionManager().getColorFaction("Blue");
            if(plugin.getFactionManager().getColorFaction(player.getUniqueId()) == faction){
                player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
                throttle(player.getUniqueId());
                return;
            }
            plugin.ensureLeaveFromOthers(player.getUniqueId() , plugin.getFactionManager().getColorFaction("Blue"));
            if (plugin.getFactionManager().getColorFaction("Blue").setMember(player, new FactionMember(player, ChatChannel.FACTION, Role.MEMBER))) {
                Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(player.getUniqueId(), plugin.getFactionManager().getColorFaction("Blue"), plugin.getFactionManager().getColorFaction(player.getUniqueId())));
            }
            throttle(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
            return;
        } else if (lowest3 == count3) {
            ColorFaction faction = plugin.getFactionManager().getColorFaction("Yellow");
            if(plugin.getFactionManager().getColorFaction(player.getUniqueId()) == faction){
                player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
                throttle(player.getUniqueId());
                return;
            }
            plugin.ensureLeaveFromOthers(player.getUniqueId() , plugin.getFactionManager().getColorFaction("Yellow"));
            if (plugin.getFactionManager().getColorFaction("Yellow").setMember(player, new FactionMember(player, ChatChannel.FACTION, Role.MEMBER))) {
                Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(player.getUniqueId(), plugin.getFactionManager().getColorFaction("Yellow"), plugin.getFactionManager().getColorFaction(player.getUniqueId())));
            }
            throttle(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
            return;
        } else {
            ColorFaction faction = plugin.getFactionManager().getColorFaction("Red");
            if(plugin.getFactionManager().getColorFaction(player.getUniqueId()) == faction){
                player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
                throttle(player.getUniqueId());
                return;
            }
            plugin.ensureLeaveFromOthers(player.getUniqueId() , plugin.getFactionManager().getColorFaction("Red"));
            if (plugin.getFactionManager().getColorFaction("Red").setMember(player, new FactionMember(player, ChatChannel.FACTION, Role.MEMBER))) {
                Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(player.getUniqueId(), plugin.getFactionManager().getColorFaction("Red"), plugin.getFactionManager().getColorFaction(player.getUniqueId())));
            }
            throttle(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
            return;
        }
    }
}
