package me.prestige.bases.scoreboard;

import com.customhcf.base.event.PlayerVanishEvent;
import com.google.common.base.Optional;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.event.*;
import me.prestige.bases.scoreboard.provider.GameProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ScoreboardHandler implements Listener {
    private final Map<UUID, PlayerBoard> playerBoards;
    private final Bases plugin;

    public ScoreboardHandler(final Bases plugin) {
        this.playerBoards = new HashMap<>();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents( this,  plugin);
        for(Player players : Bukkit.getOnlinePlayers()) {
            final PlayerBoard playerBoard;
            this.setPlayerBoard(players.getUniqueId(), playerBoard = new PlayerBoard(plugin, players));
                    playerBoard.addUpdates(Bukkit.getOnlinePlayers());
        }
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent e){
        for(Player on: Bukkit.getOnlinePlayers()){
            if(!on.canSee(e.getPlayer()))
                continue;

            this.getPlayerBoard(on.getUniqueId()).addUpdate(e.getPlayer());
        }
    }

    @EventHandler
    public void onFocus(FactionFocusChangeEvent e){
        final HashSet<Player> updates = new HashSet<>(e.getSenderFaction().getOnlinePlayers());
        if(e.getPlayer() != null) {
            updates.add(e.getPlayer());
        }
        if(e.getOldFocus() != null && Bukkit.getPlayer(e.getOldFocus()) != null) {
            updates.add(Bukkit.getPlayer(e.getOldFocus()));

        }
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }

    }

    @EventHandler
    public void onJOinFaction(PlayerJoinFactionEvent e){
        if(!e.getPlayer().isPresent()) {
            return;
        }
        for(Player on: Bukkit.getOnlinePlayers()) {
            if (this.getPlayerBoard(on.getUniqueId()) != null) {
                this.getPlayerBoard(on.getUniqueId()).addUpdate(e.getPlayer().get());
            }else{
                System.err.println("Warning Playerbaord is null for " + on.getName());
            }
        }
    }




    @EventHandler( priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player);
        }
        final PlayerBoard board2 = new PlayerBoard(this.plugin, player);
        board2.addUpdates(Bukkit.getOnlinePlayers());
        setPlayerBoard(uuid, board2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()) {
            final Player player =  optional.get();
            if(plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null){
                System.err.println("Warning: faction is null for playerjoinedfactionevent");
                return;
            }
            final Collection<Player> players = plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for(final Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()) {
            final Player player =  optional.get();
            final Collection<Player> players = event.getFaction().getOnlinePlayers();
            if(this.getPlayerBoard(event.getUniqueID()) != null) {
                this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            }
            for(final Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }



    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }

    public void setPlayerBoard(final UUID uuid, final PlayerBoard board) {
        this.playerBoards.put(uuid, board);
        board.setSidebarVisible(true);
        //board.setDefaultSidebar(this.gameProvider, 2L);
    }

    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while(iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}
