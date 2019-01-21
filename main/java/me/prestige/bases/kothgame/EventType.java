package me.prestige.bases.kothgame;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import me.prestige.bases.Bases;
import me.prestige.bases.kothgame.tracker.EventTracker;
import me.prestige.bases.kothgame.tracker.KothTracker;

public enum EventType {
    KOTH("KOTH",  new KothTracker(Bases.getPlugin())), ;


    private static final ImmutableMap<String, EventType> byDisplayName;

    static {
        final ImmutableMap.Builder<String, EventType> builder = (ImmutableMap.Builder<String, EventType>) new ImmutableBiMap.Builder();
        for(final EventType eventType : values()) {
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }
        byDisplayName = builder.build();
    }

    private final EventTracker eventTracker;
    private final String displayName;

    EventType(final String displayName, final EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }

    @Deprecated
    public static EventType getByDisplayName(final String name) {
        return (EventType) EventType.byDisplayName.get((Object) name.toLowerCase());
    }

    public EventTracker getEventTracker() {
        return this.eventTracker;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
