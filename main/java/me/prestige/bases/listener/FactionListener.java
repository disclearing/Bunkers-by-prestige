package me.prestige.bases.listener;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.google.common.base.Optional;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.event.PlayerClaimEnterEvent;
import me.prestige.bases.faction.event.PlayerJoinFactionEvent;
import me.prestige.bases.faction.event.PlayerJoinedFactionEvent;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import me.prestige.bases.game.GameState;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FactionListener implements Listener {
    private static final long FACTION_JOIN_WAIT_MILLIS;
    private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
    private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;

    static {
        FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    }

    private final Bases plugin;

    public FactionListener(Bases plugin) {
        this.plugin = plugin;
    }

    private long getLastLandChangedMeta(Player player) {
        MetadataValue value = player.getMetadata(LAND_CHANGED_META_KEY).isEmpty() ? null : player.getMetadata(LAND_CHANGED_META_KEY).get(0);
        long millis = System.currentTimeMillis();
        long remaining = value == null ? 0L : value.asLong() - millis;
        if(remaining <= 0L) {
            player.setMetadata(LAND_CHANGED_META_KEY, new FixedMetadataValue(this.plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
        }

        return remaining;
    }


    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Faction toFaction = event.getToFaction();
        Player player;

        player = event.getPlayer();
        if(this.getLastLandChangedMeta(player) <= 0L) {
            Faction fromFaction = event.getFromFaction();
            Text text1 = new Text("Now leaving: ").setColor(ChatColor.YELLOW);
            text1.append(new Text(fromFaction.getDisplayName(player)).setClick(ClickAction.RUN_COMMAND, "/faction who " + fromFaction.getName()).setHoverText(ChatColor.GRAY + "Click to view information.")).setColor(ChatColor.YELLOW);
            text1.append(new Text(" (" + (fromFaction.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ')')).setColor(ChatColor.YELLOW).send(player);
            Text text = new Text("Now entering: ").setColor(ChatColor.YELLOW);
            text.append(new Text(toFaction.getDisplayName(player)).setClick(ClickAction.RUN_COMMAND, "/faction who " + toFaction.getName()).setHoverText(ChatColor.GRAY + "Click to view information.")).setColor(ChatColor.YELLOW);
            text.append(" (" + (toFaction.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ')').setColor(ChatColor.YELLOW).send(player);
        }
    }


    @EventHandler
    public void onJoinFaction(PlayerJoinedFactionEvent e){
        Player player = e.getPlayer().isPresent() ? e.getPlayer().get() : null;
        if(player == null) return;
        ColorFaction oldFaction = plugin.getFactionManager().getColorFaction(player.getUniqueId());
        if(oldFaction == null) return;
        oldFaction.setMember(player, null);
    }





    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction != null && plugin.getGameManager().getGameState() == GameState.STARTING && !playerFaction.isRaidable()) {
            playerFaction.broadcast(ChatColor.RED + "Deserter: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.RED + " left the game, and will be punished." + '.');
            new BukkitRunnable(){
                @Override
                public void run() {
                    try(Jedis jedis = BasePlugin.getPlugin().getRedis().getResource()){
                        jedis.setex("deserter-flag-" + player.getName() , 60 , "true");
                    }
                }
            }.runTaskAsynchronously(plugin);

        }

    }
}
