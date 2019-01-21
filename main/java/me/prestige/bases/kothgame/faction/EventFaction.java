package me.prestige.bases.kothgame.faction;

import com.customhcf.util.cuboid.Cuboid;
import me.prestige.bases.faction.claim.Claim;
import me.prestige.bases.faction.type.ClaimableFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.kothgame.CaptureZone;
import me.prestige.bases.kothgame.EventType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public abstract class  EventFaction extends ClaimableFaction {
    public EventFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }

    public EventFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
    }

    @Override
    public String getDisplayName(final Faction faction) {
        if(this.getEventType() == EventType.KOTH){
            if(getName().equalsIgnoreCase("Palace")){
                return ChatColor.LIGHT_PURPLE + "Palace";
            }
            return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() +  this.getName();
        }
        return ChatColor.DARK_RED + this.getEventType().getDisplayName();
    }

    @Override
    public String getDisplayName(final CommandSender sender) {
        if(this.getEventType() == EventType.KOTH){
            if(getName().equalsIgnoreCase("Palace")){
                return ChatColor.LIGHT_PURPLE + "Palace";
            }
            return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() +  this.getName();
        }
        return ChatColor.DARK_RED + this.getEventType().getDisplayName();
    }

    @Override
    public void setClaim(final Cuboid cuboid, final CommandSender sender) {
        this.removeClaims(this.getClaims(), sender);
        final Location min = cuboid.getMinimumPoint();
        min.setY(0);
        final Location max = cuboid.getMaximumPoint();
        max.setY(256);
        this.addClaim(new Claim(this, min, max), sender);
    }

    public abstract EventType getEventType();

    public abstract List<CaptureZone> getCaptureZones();
}
