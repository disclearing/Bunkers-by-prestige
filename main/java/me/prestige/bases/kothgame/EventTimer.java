package me.prestige.bases.kothgame;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import me.prestige.bases.DateTimeFormats;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.event.CaptureZoneEnterEvent;
import me.prestige.bases.faction.event.CaptureZoneLeaveEvent;
import me.prestige.bases.kothgame.faction.EventFaction;
import me.prestige.bases.kothgame.faction.KothFaction;
import me.prestige.bases.timer.GlobalTimer;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class EventTimer extends GlobalTimer implements Listener {
    private static final long RESCHEDULE_FREEZE_MILLIS;
    private static final String RESCHEDULE_FREEZE_WORDS;

    static {
        RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(EventTimer.RESCHEDULE_FREEZE_MILLIS, true, true);
    }

    private final Bases plugin;
    private long startStamp;
    private long lastContestedEventMillis;
    private EventFaction eventFaction;

    public EventTimer(final Bases plugin) {
        super("Event", 0L);
        this.plugin = plugin;
        new BukkitRunnable() {
            public void run() {
                if(EventTimer.this.eventFaction != null) {
                    EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
                    return;
                }
                final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
                final int day = now.getDayOfYear();
                final int hour = now.getHour();
                final int minute = now.getMinute();


            }
        }.runTaskTimer((Plugin) plugin, 20L, 20L);
    }

    public EventFaction getEventFaction() {
        return this.eventFaction;
    }

    public String getScoreboardPrefix() {
          return ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
    }

    public String getName() {
        return (this.eventFaction == null) ? "Event" : this.eventFaction.getName();
    }

    @Override
    public boolean clearCooldown() {
        boolean result = super.clearCooldown();
        if(this.eventFaction != null) {
            for(final CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }
            this.eventFaction.setDeathban(true);
            this.eventFaction = null;
            this.startStamp = -1L;
            result = true;
        }
        return result;
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent e){
        if(plugin.getFactionManager().getFactionAt(e.getBlock()) != null){
            e.setCancelled(true);
        }
    }

    @Override
    public long getRemaining() {
        if(this.eventFaction == null) {
            return 0L;
        }
        if(this.eventFaction instanceof KothFaction) {
            return ((KothFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }

    public void handleWinner(final Player winner) {
        if(this.eventFaction == null) {
            return;
        }
        plugin.getGameManager().end(plugin.getFactionManager().getColorFaction(winner.getUniqueId()));
        //TODO add/loss win to player

        this.clearCooldown();
    }

    public boolean tryContesting(final EventFaction eventFaction, CommandSender sender) {
        if(sender == null){
            sender = Bukkit.getConsoleSender();
        }
        if(this.eventFaction != null) {
            sender.sendMessage(ChatColor.RED + "There is already an active event, use /game cancel to end it.");
            return false;
        }
        if(eventFaction instanceof KothFaction) {
            final KothFaction kothFaction = (KothFaction) eventFaction;
            if(kothFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        }
        final long millis = System.currentTimeMillis();
        if(this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L) {
            sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
            return false;
        }
        this.lastContestedEventMillis = millis;
        this.startStamp = millis;
        this.eventFaction = eventFaction;
        final Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for(final CaptureZone captureZone : captureZones) {
            if(captureZone.isActive()) {
                final Player player = (Player) Iterables.getFirst((Iterable) captureZone.getCuboid().getPlayers(), (Object) null);
                if(player == null || !eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
                    continue;
                }
                captureZone.setCappingPlayer(player);
            }
        }
        eventFaction.setDeathban(true);
        return true;
    }

    public long getUptime() {
        return System.currentTimeMillis() - this.startStamp;
    }

    public long getStartStamp() {
        return this.startStamp;
    }

    private void handleDisconnect(final Player player) {
        Preconditions.checkNotNull((Object) player);
        if(this.eventFaction == null) {
            return;
        }
        final Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
        for(final CaptureZone captureZone : captureZones) {
            if(player.getUniqueId().equals(captureZone.getCappingPlayer().getUniqueId())) {
                this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
                captureZone.setCappingPlayer(null);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.handleDisconnect(event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(final CaptureZoneEnterEvent event) {
        if(this.eventFaction == null) {
            return;
        }
        final CaptureZone captureZone = event.getCaptureZone();
        if(!this.eventFaction.getCaptureZones().contains(captureZone)) {
            return;
        }
        final Player player = event.getPlayer();
        if(captureZone.getCappingPlayer() == null && this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
            captureZone.setCappingPlayer(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event) {
        if(Objects.equal(event.getFaction(),  this.eventFaction)) {
            final Player player = event.getPlayer();
            final CaptureZone captureZone = event.getCaptureZone();
            if(Objects.equal((Object) player, (Object) captureZone.getCappingPlayer()) && this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)) {
                captureZone.setCappingPlayer(null);
                for(final Player target : captureZone.getCuboid().getPlayers()) {
                    if(target != null && !target.equals(player) && this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
}
