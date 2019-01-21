package me.prestige.bases.faction;

import me.prestige.bases.faction.claim.Claim;
import me.prestige.bases.faction.type.ClaimableFaction;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface FactionManager {
    long MAX_DTR_REGEN_MILLIS = TimeUnit.HOURS.toMillis(3L);
    String MAX_DTR_REGEN_WORDS = DurationFormatUtils.formatDurationWords(FactionManager.MAX_DTR_REGEN_MILLIS, true, true);
    Map<String, ?> getFactionNameMap();


    Collection<Faction> getFactions();

    Collection<ClaimableFaction> getClaimableFactions();

    Collection<PlayerFaction> getPlayerFactions();



    Claim getClaimAt(Location p0);

    Claim getClaimAt(World p0, int p1, int p2);

    Faction getFactionAt(Location p0);

    Faction getFactionAt(Block p0);

    Faction getFactionAt(World p0, int p1, int p2);

    Faction getFaction(String p0);

    Faction getFaction(UUID p0);

    PlayerFaction getContainingPlayerFaction(String p0);

    PlayerFaction getPlayerFaction(UUID p0);

    ColorFaction getColorFaction(UUID p0);

    ColorFaction getColorFaction(String p0);


    Faction getContainingFaction(String p0);



    boolean createFaction(Faction p0, CommandSender p1);

    boolean removeFaction(Faction p0, CommandSender p1);

    void reloadFactionData();

    void saveFactionData();
}
