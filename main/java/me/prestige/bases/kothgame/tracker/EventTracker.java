package me.prestige.bases.kothgame.tracker;

import me.prestige.bases.kothgame.CaptureZone;
import me.prestige.bases.kothgame.EventTimer;
import me.prestige.bases.kothgame.EventType;
import me.prestige.bases.kothgame.faction.EventFaction;
import org.bukkit.entity.Player;

@Deprecated
public interface EventTracker {
    EventType getEventType();

    void tick(EventTimer p0, EventFaction p1);

    void onContest(EventFaction p0, EventTimer p1);

    boolean onControlTake(Player p0, CaptureZone p1);

    boolean onControlLoss(Player p0, CaptureZone p1, EventFaction p2);

}
