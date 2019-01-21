package me.prestige.bases.kothgame.tracker;

import me.prestige.bases.DateTimeFormats;
import me.prestige.bases.Bases;
import me.prestige.bases.kothgame.CaptureZone;
import me.prestige.bases.kothgame.EventTimer;
import me.prestige.bases.kothgame.EventType;
import me.prestige.bases.kothgame.faction.EventFaction;
import me.prestige.bases.kothgame.faction.KothFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Deprecated
public class KothTracker implements EventTracker {
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(8L);
    }

    private final Bases plugin;

    public KothTracker(final Bases plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    public String correctColor(CaptureZone captureZone){
        return ChatColor.GOLD.toString() + ChatColor.BOLD + captureZone.getDisplayName();
    }

    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if(remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        final int remainingSeconds = (int) (remainingMillis / 1000L);
        if(remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.YELLOW + "Someone is controlling " + correctColor(captureZone) + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }

    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + eventFaction.getName() + ChatColor.YELLOW + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }

    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        if(plugin.getTimerManager().waitingTimer.hasCooldown(player))
            return false;
        player.sendMessage(ChatColor.YELLOW + "You are now in control of " +correctColor(captureZone)+ ChatColor.YELLOW + '.');
        return true;
    }

    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        player.sendMessage(ChatColor.YELLOW + "You are no longer in control of " + correctColor(captureZone)+ ChatColor.YELLOW + '.');
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > KothTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + player.getName() + ChatColor.YELLOW + " has lost control of " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }


}
