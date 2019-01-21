package me.prestige.bases;

import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.kothgame.faction.EventFaction;
import me.prestige.bases.kothgame.faction.KothFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class KothStartTimer extends BukkitRunnable {
    @Override
    public void run() {
        try {
            Bases.getPlugin().getTimerManager().eventTimer.tryContesting(((EventFaction) Bases.getPlugin().getFactionManager().getFaction(Bases.getPlugin().getConfig().getString("koth_faction_name"))), null);
            return;
        }catch (NullPointerException e){
            Bases.getPlugin().getServer().getLogger().log(Level.WARNING, "The koth configured in the 'config.yml' in 'bases' folder is not found.");
            Bases.getPlugin().getServer().getLogger().log(Level.WARNING, "No koth will start, you must stop the game and setup the koth!");
            Bases.getPlugin().getGameManager().end(null);
        }
    }
}
