package me.prestige.bases.scoreboard;

import com.customhcf.base.BasePlugin;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ColorFaction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Collections;

public class PlayerBoard {
    public final BufferedObjective bufferedObjective;
    private final Team green;
    private final Team blue;
    private final Team yellow;
    private final Team vanish;
    private final Team red;
    private final Team focused;
    private final Scoreboard scoreboard;
    private final Player player;
    private final Bases plugin;
    private boolean sidebarVisible;
    private boolean removed;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;

    public PlayerBoard(final Bases plugin, final Player player) {
        this.sidebarVisible = false;
        this.removed = false;
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        (this.vanish = this.scoreboard.registerNewTeam("vanish")).setPrefix(ChatColor.AQUA.toString());
        this.vanish.setCanSeeFriendlyInvisibles(true);
        (this.blue = this.scoreboard.registerNewTeam("blue")).setPrefix(ChatColor.BLUE.toString());
        this.blue.setCanSeeFriendlyInvisibles(true);
        (this.red = this.scoreboard.registerNewTeam("red")).setPrefix(ChatColor.RED.toString());
        this.red.setCanSeeFriendlyInvisibles(true);
        (this.yellow = this.scoreboard.registerNewTeam("yellow")).setPrefix(ChatColor.YELLOW.toString());
        this.yellow.setCanSeeFriendlyInvisibles(true);
        (this.focused = this.scoreboard.registerNewTeam("focused")).setPrefix(ChatColor.DARK_PURPLE.toString());
        (this.green = this.scoreboard.registerNewTeam("green")).setPrefix(ChatColor.GREEN.toString());
        this.green.setCanSeeFriendlyInvisibles(true);
        player.setScoreboard(this.scoreboard);
    }

    public void remove() {
        this.removed = true;
        if(this.scoreboard != null) {
            synchronized(this.scoreboard) {
                for(final Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for(final Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }


    public void setSidebarVisible(final boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final SidebarProvider provider, final long updateInterval) {
        if(provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        this.defaultProvider = provider;
        if(this.runnable != null) {
            this.runnable.cancel();
        }
        if(provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        (this.runnable = new BukkitRunnable() {
            public void run() {
                if(PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if(provider.equals(PlayerBoard.this.defaultProvider)) {
                    PlayerBoard.this.updateObjective();
                }
            }
        }).runTaskTimerAsynchronously((Plugin) this.plugin, updateInterval, updateInterval);
    }


    private void updateObjective() {
        final SidebarProvider provider = (this.temporaryProvider != null) ? this.temporaryProvider : this.defaultProvider;
        if(provider == null) {
            this.bufferedObjective.setVisible(false);
        } else {
            this.bufferedObjective.setTitle(provider.getTitle());
            this.bufferedObjective.setAllLines(provider.getLines(this.player));
            this.bufferedObjective.flip();
        }
    }

    public void addUpdate(final Player target) {
        this.addUpdates(Collections.singleton(target));
    }

    public void addUpdates(final Collection<? extends Player> updates) {
        if(this.removed) {
            return;
        }
        this.player.setScoreboard(scoreboard);
        new BukkitRunnable() {

            public void run() {
                for(final Player update : updates) {
                    if (PlayerBoard.this.player.equals(update)) {
                        if(BasePlugin.getPlugin().getUserManager().getUser(update.getUniqueId()).isVanished()){
                            PlayerBoard.this.vanish.addPlayer(update);
                            continue;
                        }
                        if(plugin.getFactionManager().getColorFaction(update.getUniqueId()) == null){
                            continue;
                        }
                        switch (plugin.getFactionManager().getColorFaction(update.getUniqueId()).getName()){
                            case "Blue": {
                                PlayerBoard.this.blue.addPlayer(update);
                                break;
                            }
                            case "Green": {
                                PlayerBoard.this.green.addPlayer(update);
                                break;
                            }
                            case "Yellow": {
                                PlayerBoard.this.yellow.addPlayer(update);
                                break;
                            }case "Red": {
                                PlayerBoard.this.red.addPlayer(update);
                                break;
                            }
                            default:{
                                continue;
                            }
                        }

                    } else {
                        if(BasePlugin.getPlugin().getUserManager().getUser(update.getUniqueId()).isVanished()){
                            PlayerBoard.this.vanish.addPlayer(update);
                            continue;
                        }
                        if(plugin.getFactionManager().getColorFaction(update.getUniqueId()) == null){
                            continue;
                        }
                        switch (plugin.getFactionManager().getColorFaction(update.getUniqueId()).getName()){
                            case "Blue": {
                                PlayerBoard.this.blue.addPlayer(update);
                                break;
                            }
                            case "Green": {
                                PlayerBoard.this.green.addPlayer(update);
                                break;
                            }
                            case "Yellow": {
                                PlayerBoard.this.yellow.addPlayer(update);
                                break;
                            }case "Red": {
                                PlayerBoard.this.red.addPlayer(update);
                                break;
                            }
                            default:{
                                continue;
                            }
                        }

                    }
                }
                    }
        }.runTask(this.plugin);
    }
}
