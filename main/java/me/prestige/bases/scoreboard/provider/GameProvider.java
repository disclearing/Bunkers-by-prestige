package me.prestige.bases.scoreboard.provider;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.customhcf.base.BasePlugin;
import com.customhcf.util.BukkitUtils;
import com.google.common.collect.Lists;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.game.GameState;
import me.prestige.bases.scoreboard.SidebarEntry;
import me.prestige.bases.scoreboard.SidebarProvider;
import me.prestige.bases.timer.GlobalTimer;
import me.prestige.bases.timer.PlayerTimer;
import me.prestige.bases.timer.Timer;
import me.prestige.bases.timer.type.TeleportTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GameProvider implements BoardAdapter {


    private final Bases plugin;

    public GameProvider(final Bases plugin) {
        this.plugin = plugin;
    }

    protected static final String STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 12);

    @Override
    public String getTitle(Player player) {
        return plugin.getScoreboardTitle();
    }


    public void add(List<String> list , SidebarEntry sidebarEntry){
        list.add(sidebarEntry.prefix + sidebarEntry.name + sidebarEntry.suffix);
    }
    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<SidebarEntry> lines = new ArrayList<>();
        if(plugin.getGameManager().getGameState().equals(GameState.WAITING)) {
            lines.add(new SidebarEntry(ChatColor.RED.toString(),  ChatColor.BOLD.toString()+ "Map" + ChatColor.GRAY + ": ", ChatColor.GRAY.toString() + plugin.getMapName()));
            String name = plugin.getFactionManager().getColorFaction(player.getUniqueId()) != null ? plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player) : ChatColor.DARK_RED + "None";
            lines.add(new SidebarEntry(ChatColor.RED + ChatColor.BOLD.toString(), "Team" + ChatColor.GRAY + ": ", name));
            lines.add(new SidebarEntry(ChatColor.RED + ChatColor.BOLD.toString(), "Players" + ChatColor.GRAY + ": ", ChatColor.GREEN.toString() + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + "/20"));
            lines.add(new SidebarEntry(ChatColor.RED + ChatColor.BOLD.toString(), "Needed"+ ChatColor.GRAY + ": ",  ChatColor.RED.toString() + (20 - Bukkit.getOnlinePlayers().size())));

        }else if(plugin.getGameManager().getGameState().equals(GameState.STARTING)){
            lines.add(new SidebarEntry(ChatColor.GOLD + ChatColor.BOLD.toString(), "Game Timer" + ChatColor.GRAY + ": ", ChatColor.WHITE + Bases.getRemaining(System.currentTimeMillis() - plugin.getGameManager().getGameTimer(), true)));
            if(plugin.getFactionManager().getColorFaction(player.getUniqueId()) != null){
                lines.add(new SidebarEntry(ChatColor.DARK_RED + ChatColor.BOLD.toString(), "DTR" + ChatColor.GRAY + ": ",ChatColor.WHITE.toString() +  plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDeathsUntilRaidable()));
            }
            lines.add(new SidebarEntry(ChatColor.GREEN + ChatColor.BOLD.toString(), "Balance" + ChatColor.GRAY + ": ", ChatColor.WHITE.toString() + "$" + plugin.getEconomyManager().getBalance(player.getUniqueId())));
        }else{
            if(!plugin.getGameManager().getGameState().equals(GameState.ENDING)){
                return null;
            }

        }
        final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
        for(final Timer timer : timers) {
            if(timer instanceof PlayerTimer && !(timer instanceof TeleportTimer)) {
                final PlayerTimer playerTimer = (PlayerTimer) timer;
                final long remaining3 = playerTimer.getRemaining(player);
                if(remaining3 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if(timerName.length() > 14) {
                    timerName = timerName.substring(0, timerName.length());
                }
                lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), ChatColor.BOLD + timerName + ChatColor.GRAY, ": " + ChatColor.WHITE + Bases.getRemaining(remaining3, true)));
            }
            if(timer instanceof GlobalTimer) {
                final GlobalTimer playerTimer = (GlobalTimer) timer;
                final long remaining3 = playerTimer.getRemaining();
                if(remaining3 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if(timerName.length() > 14) {
                    timerName = timerName.substring(0, timerName.length());
                }
                lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.WHITE + Bases.getRemaining(remaining3, true)));
            }
        }
        if(player.hasPermission("command.staffmode") && BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()) {
            lines.add(new SidebarEntry(ChatColor.BLUE.toString() + ChatColor.BOLD, "Staff Mode", ChatColor.GRAY + ": "));
            if (player.hasPermission("command.vanish")) {
                lines.add(new SidebarEntry(ChatColor.GRAY.toString() + " » " + ChatColor.BLUE.toString(), "Visibility" + ChatColor.DARK_GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished() ? ChatColor.GOLD + "Vanished" : ChatColor.WHITE + "Visible"));
            }
            if (player.hasPermission("command.gamemode")) {
                lines.add(new SidebarEntry(ChatColor.GRAY.toString() + " » " + ChatColor.BLUE.toString(), "Gamemode" + ChatColor.DARK_GRAY + ": ", player.getGameMode() == GameMode.CREATIVE ? ChatColor.GOLD + "Creative" : ChatColor.WHITE + "Survival"));
            } else if (player.hasPermission("command.fly")) {
                lines.add(new SidebarEntry(ChatColor.GRAY.toString() + " » " + ChatColor.BLUE.toString(), "Fly" + ChatColor.DARK_GRAY + ": ", player.getAllowFlight() ? ChatColor.GOLD + "True" : ChatColor.WHITE + "False"));
            }
            if (player.hasPermission("command.staffchat")) {
                lines.add(new SidebarEntry(ChatColor.GRAY.toString() + " » " + ChatColor.BLUE.toString(), "Chat mode" + ChatColor.DARK_GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isInStaffChat() ? ChatColor.GOLD + "Staff Chat" : ChatColor.WHITE + "Global Chat"));
            }
            lines.add(new SidebarEntry(ChatColor.GRAY.toString() + " » " + ChatColor.BLUE.toString(), "Timer" + ChatColor.DARK_GRAY + ": ", ((BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).getClicked()) == (long) 0) ? ChatColor.WHITE + "Off" : ChatColor.GOLD + Bases.getRemaining(System.currentTimeMillis() - BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).getClicked(), true)));
        }
        if (!lines.isEmpty()) {
            lines.add(0, new SidebarEntry(ChatColor.GRAY, STRAIGHT_LINE, STRAIGHT_LINE + "-"));
            lines.add(lines.size(),  new SidebarEntry(ChatColor.GRAY, STRAIGHT_LINE, STRAIGHT_LINE + "-"));
        }
        List<String> translated = Lists.newArrayList();
        for(SidebarEntry sidebarEntry : lines){
            add(translated , sidebarEntry);
        }
        return translated;
    }
}
