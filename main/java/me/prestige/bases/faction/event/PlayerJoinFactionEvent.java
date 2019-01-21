package me.prestige.bases.faction.event;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import me.prestige.bases.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerJoinFactionEvent extends FactionEvent implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final UUID uniqueID;
    private boolean cancelled;
    private Optional<Player> player;
    private Optional<PlayerFaction> from;


    public PlayerJoinFactionEvent(final Player player, final PlayerFaction playerFaction, PlayerFaction faction) {
        super(playerFaction);
        Preconditions.checkNotNull((Object) player, (Object) "Player cannot be null");
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
        if(faction == null){
            Optional.absent();
        }else {
            this.from = Optional.of(faction);
        }
    }

    public PlayerJoinFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction, PlayerFaction faction) {
        super(playerFaction);
        Preconditions.checkNotNull((Object) playerUUID, (Object) "Player UUID cannot be null");
        this.uniqueID = playerUUID;
        if(faction == null){
            Optional.absent();
        }else {
            this.from = Optional.of(faction);
        }
    }

    public static HandlerList getHandlerList() {
        return PlayerJoinFactionEvent.handlers;
    }

    public Optional<Player> getPlayer() {
        if(this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerJoinFactionEvent.handlers;
    }

    public Optional<PlayerFaction> getFrom() {
        return from;
    }

    public void setFrom(Optional<PlayerFaction> from) {
        this.from = from;
    }
}
