package me.prestige.bases.listener;

import me.prestige.bases.Bases;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerStatsListener implements Listener {

    private Bases plugin;

    public PlayerStatsListener(Bases plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e){
        if(e.getEntity().getKiller() != null){
            plugin.getUserManager().getUser(e.getEntity().getUniqueId()).setDeaths(plugin.getUserManager().getUser(e.getEntity().getUniqueId()).getDeaths() + 1);
            plugin.getUserManager().getUser(e.getEntity().getKiller().getUniqueId()).setKills(plugin.getUserManager().getUser(e.getEntity().getKiller().getUniqueId()).getKills() + 1);
        }
    }

}
