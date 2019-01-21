package me.prestige.bases.faction.type;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.GenericUtils;
import com.customhcf.util.JavaUtils;
import com.customhcf.util.PersistableLocation;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.FactionMember;
import me.prestige.bases.faction.event.FactionDtrChangeEvent;
import me.prestige.bases.faction.event.PlayerJoinedFactionEvent;
import me.prestige.bases.faction.event.PlayerLeaveFactionEvent;
import me.prestige.bases.faction.event.PlayerLeftFactionEvent;
import me.prestige.bases.faction.event.cause.FactionLeaveCause;
import me.prestige.bases.faction.struct.Raidable;
import me.prestige.bases.faction.struct.RegenStatus;
import me.prestige.bases.faction.struct.Relation;
import me.prestige.bases.faction.struct.Role;
import me.prestige.bases.listener.DeathListener;
import me.prestige.bases.timer.type.TeleportTimer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerFaction extends ClaimableFaction implements Raidable {
    private static final UUID[] EMPTY_UUID_ARRAY;

    static {
        EMPTY_UUID_ARRAY = new UUID[0];
    }

    protected final Map members = new HashMap();
    protected UUID focus;
    protected PersistableLocation home;
    protected double deathsUntilRaidable;

    public PlayerFaction(final String name) {
        super(name);
        this.deathsUntilRaidable = 5.0D;
    }

    public PlayerFaction(Map map) {
        super(map);
        this.deathsUntilRaidable = 5.0D;
        Object object1 = map.get("home");
        if(object1 != null) {
            this.home = (PersistableLocation) object1;
        }
    }

    public Map serialize() {
        Map map = super.serialize();
        if(this.home != null) {
            map.put("home", this.home);
        }
        return map;
    }


    public boolean setMember(final UUID playerUUID, final FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }

    public boolean setMember(final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }

    public boolean setMember(final Player player, final FactionMember factionMember) {
        return this.setMember(player, player.getUniqueId(), factionMember, false);
    }

    public boolean setMember(final Player player, final FactionMember factionMember, final boolean force) {
        return this.setMember(player, player.getUniqueId(), factionMember, force);
    }

    private boolean setMember( final Player player, final UUID playerUUID, final FactionMember factionMember, final boolean force) {

        if(factionMember == null) {
            if(!force) {
                final PlayerLeaveFactionEvent event = (player == null) ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
                Bukkit.getPluginManager().callEvent( event);
                if(event.isCancelled()) {
                    return false;
                }
            }
            this.members.remove(playerUUID);
            final PlayerLeftFactionEvent event2 = (player == null) ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
            Bukkit.getPluginManager().callEvent(event2);
            return true;
        }
        if(members.size() >= 5){
            player.sendMessage(ChatColor.RED + "Team Full!");
            return false;
        }
        this.members.put(playerUUID, factionMember);
        final PlayerJoinedFactionEvent eventPre = (player == null) ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
        Bukkit.getPluginManager().callEvent(eventPre);
        return true;
    }







    public Map<UUID, FactionMember> getMembers() {
        return ImmutableMap.copyOf(members);
    }

    public Set<Player> getOnlinePlayers() {
        return getOnlinePlayers(null);
    }

    public Set getOnlinePlayers(CommandSender sender) {
        Set entrySet = this.getOnlineMembers(sender).entrySet();
        HashSet results = new HashSet(entrySet.size());
        Iterator var4 = entrySet.iterator();

        while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry) var4.next();
            results.add(Bukkit.getPlayer((UUID) entry.getKey()));
        }

        return results;
    }

    public Map getOnlineMembers() {
        return this.getOnlineMembers(null);
    }

    public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender) {
        Player senderPlayer = sender instanceof Player ? (Player)sender : null;
        HashMap<UUID, FactionMember> results = new HashMap<UUID, FactionMember>();
        Iterator<Map.Entry<UUID, FactionMember>> iterator = this.members.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FactionMember> entry = iterator.next();
            Player target = Bukkit.getPlayer((UUID)entry.getKey());
            if (target == null) continue;
            if (senderPlayer != null && !senderPlayer.canSee(target)) continue;
            results.put(entry.getKey(), entry.getValue());
        }
        return results;
    }


    @Deprecated
    public FactionMember getMember(String memberName) {
        UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
        if (uuid == null) {
            return null;
        }
        FactionMember factionMember = (FactionMember) this.members.get(uuid);
        return factionMember;
    }

    public FactionMember getMember(Player player) {
        return this.getMember(player.getUniqueId());
    }

    public FactionMember getMember(UUID memberUUID) {
        return (FactionMember) this.members.get(memberUUID);
    }


    public Location getHome() {
        return (this.home == null) ? null : this.home.getLocation();
    }

    public void setHome( Location home) {
        if(home == null && this.home != null) {
            TeleportTimer timer = Bases.getPlugin().getTimerManager().teleportTimer;
            Iterator var3 = this.getOnlinePlayers().iterator();

            while(var3.hasNext()) {
                Player player = (Player) var3.next();
                Location destination = (Location) timer.getDestination(player);
                if(Objects.equal(destination, this.home.getLocation())) {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Your home was unset, so your " + timer.getDisplayName() + ChatColor.RED + " timer has been cancelled");
                }
            }
        }

        this.home = home == null ? null : new PersistableLocation(home);
    }


    @Override
    public boolean isRaidable() {
        return this.deathsUntilRaidable <= 0.0;
    }

    @Override
    public double getDeathsUntilRaidable() {
        return deathsUntilRaidable;
    }

    @Override
    public double getMaximumDeathsUntilRaidable() {
        return 5;
    }



    public ChatColor getDtrColour() {
        if(this.deathsUntilRaidable < 0.0) {
            return ChatColor.RED;
        }
        if(this.deathsUntilRaidable < 3.0) {
            return ChatColor.YELLOW;
        }
        return ChatColor.GREEN;
    }


    @Override
    public double setDeathsUntilRaidable(final double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }

    private double setDeathsUntilRaidable(double deathsUntilRaidable, final boolean limit) {
        deathsUntilRaidable = deathsUntilRaidable * 100.0 / 100.0;
        if(limit) {
            deathsUntilRaidable = Math.min(deathsUntilRaidable, this.getMaximumDeathsUntilRaidable());
        }
        if(deathsUntilRaidable - this.deathsUntilRaidable != 0.0) {
            final FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, Bases.getPlugin().getFactionManager().getColorFaction(this.getName()), this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                deathsUntilRaidable = event.getNewDtr();
                if(deathsUntilRaidable > 0.0 && deathsUntilRaidable <= 0.0) {
                    Bases.getPlugin().getLogger().info("Faction " + this.getName() + " is now raidable.");
                }
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }
        return this.deathsUntilRaidable;
    }

    @Override
    public RegenStatus getRegenStatus() {
        return RegenStatus.FULL;
    }


    public void printDetails(CommandSender sender) {
        PlayerFaction playerFaction;
        HashSet memberNames = new HashSet();
        Iterator playerFaction1 = this.members.entrySet().iterator();
        while(playerFaction1.hasNext()) {
            final Map.Entry entry = (Map.Entry) playerFaction1.next();
            final FactionMember factionMember = (FactionMember) entry.getValue();
            final Player target = factionMember.toOnlinePlayer();
            ChatColor colour;
            if (DeathListener.spectators.containsKey(factionMember.getUniqueId())) {
                colour = ChatColor.RED;
            }else
             if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
                colour = ChatColor.GRAY;
            }
            else {
                colour = ChatColor.GREEN;
            }
            final String memberName =  colour + factionMember.getName() +  ChatColor.GRAY + '[' +  ChatColor.GREEN + Bases.getPlugin().getUserManager().getUser(factionMember.getUniqueId()).getKills() +ChatColor.GRAY + ']';
            switch (factionMember.getRole()) {
                case MEMBER: {
                    memberNames.add(memberName);
                }
            }
        }

        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + this.getDisplayName(sender)+ ChatColor.GRAY+" ["+this.getOnlineMembers().size()+"/"+this.getMembers().size()+"] "+ ChatColor.GREEN + " - " + ChatColor.YELLOW + "Home: " + ChatColor.WHITE + (this.home == null ? "None" : ChatColor.WHITE.toString() + this.home.getLocation().getBlockX() + " | " + this.home.getLocation().getBlockZ()));

        if(!memberNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.RED + StringUtils.join(memberNames, ChatColor.GRAY + ", "));
        }



        sender.sendMessage(ChatColor.YELLOW + "Deaths until Raidable: "+ this.getRegenStatus().getSymbol() + this.getDtrColour() + JavaUtils.format(getDeathsUntilRaidable()));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }

    public UUID getFocus() {
        return focus;
    }

    public void setFocus(UUID uuid){
       this.focus = uuid;
    }

    public void broadcast(final String message) {
        this.broadcast(message, PlayerFaction.EMPTY_UUID_ARRAY);
    }

    public void broadcast(final String[] messages) {
        this.broadcast(messages, PlayerFaction.EMPTY_UUID_ARRAY);
    }

    public void broadcast(final String message, @Nullable final UUID... ignore) {
        this.broadcast(new String[]{message}, ignore);
    }

    public void broadcast(final String[] messages, final UUID... ignore) {
        Preconditions.checkNotNull((Object) messages, "Messages cannot be null");
        Preconditions.checkArgument(messages.length > 0, "Message array cannot be empty");
        final Collection<Player> players = this.getOnlinePlayers();
        final Collection<UUID> ignores = ((ignore.length == 0) ? Collections.emptySet() : Sets.newHashSet(ignore));
        for(final Player player : players) {
            if(!ignores.contains(player.getUniqueId())) {
                player.sendMessage(messages);
            }
        }
    }



}
