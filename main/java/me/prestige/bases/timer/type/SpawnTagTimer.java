package me.prestige.bases.timer.type;

import com.customhcf.base.kit.event.KitApplyEvent;
import com.customhcf.util.BukkitUtils;
import com.google.common.base.MoreObjects;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.event.PlayerClaimEnterEvent;
import me.prestige.bases.faction.event.PlayerJoinFactionEvent;
import me.prestige.bases.faction.event.PlayerLeaveFactionEvent;
import me.prestige.bases.timer.PlayerTimer;
import me.prestige.bases.timer.event.TimerClearEvent;
import me.prestige.bases.timer.event.TimerStartEvent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnTagTimer extends PlayerTimer implements Listener {
    private static final long NON_WEAPON_TAG = 5000L;
    private final Bases plugin;

    public SpawnTagTimer(final Bases plugin) {
        super("Spawn Tag", TimeUnit.SECONDS.toMillis(30L));
        this.plugin = plugin;
    }

    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(final TimerClearEvent event) {
        if(event.getTimer().equals(this)) {
            final com.google.common.base.Optional<UUID> optionalUserUUID = event.getUserUUID();
            if(optionalUserUUID.isPresent()) {
                this.onExpire((UUID) optionalUserUUID.get());
            }
        }
    }





    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent) event, true);
        final Entity entity;
        if(attacker != null && (entity = event.getEntity()) instanceof Player) {
            final Player attacked = (Player) entity;
            boolean weapon = event.getDamager() instanceof Arrow;
            if(!weapon) {
                final ItemStack stack = attacker.getItemInHand();
                weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
            }
            final long duration = weapon ? this.defaultCooldown : NON_WEAPON_TAG;
            this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
            this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(final TimerStartEvent event) {
        if(event.getTimer().equals(this)) {
            final com.google.common.base.Optional<Player> optional = event.getPlayer();
            if(optional.isPresent()) {
                final Player player = (Player) optional.get();
                player.sendMessage(ChatColor.WHITE + "You are now spawn-tagged for " + ChatColor.GOLD + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.GOLD + '.');
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        this.clearCooldown(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()) {
            this.clearCooldown(event.getPlayer());
        }
    }
}
