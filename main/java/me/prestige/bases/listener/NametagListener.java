package me.prestige.bases.listener;

import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

/**
 * Created by admin on 7/27/2017.
 */
public class NametagListener implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(getVersion(event.getPlayer()) >= 47){
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            packet.a = "invis-group";
            packet.nameTagVisibility = NameTagVisibility.NEVER;
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!isOnSameTeam(player , event.getPlayer())) {
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        packet.e.add(player.getName());
                    }
                }
            }
            sendPacket(event.getPlayer(), packet);
        }
    }

    @EventHandler
    public void onPotion(PotionEffectAddEvent event){
        if(event.getEffect().getType().getId() == PotionEffectType.INVISIBILITY.getId() && event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            packet.a = "invis-group";
            packet.f = 3;
            packet.e.add(player.getName());
            for(Player other : Bukkit.getOnlinePlayers()){
                if(!isOnSameTeam(player, other) && getVersion(other) >= 47){
                    sendPacket(other, packet);
                }
            }
        }
    }

    @EventHandler
    public void onPotion(PotionEffectRemoveEvent event){
        if(event.getEffect().getType().getId() == PotionEffectType.INVISIBILITY.getId() && event.getEntity() instanceof Player){
            final Player player = (Player) event.getEntity();
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            packet.a = "invis-group";
            packet.f = 4;
            packet.e.add(player.getName());
            for(Player other : Bukkit.getOnlinePlayers()){
                if(!isOnSameTeam(player, other) && getVersion(other) >= 47){
                    sendPacket(other, packet);
                    final Team team = other.getScoreboard().getPlayerTeam(player);
                    if(team != null){
                        PacketPlayOutScoreboardTeam addback = new PacketPlayOutScoreboardTeam();
                        addback.a = team.getName();
                        addback.f = 3;
                        addback.e.add(player.getName());
                        sendPacket(other, addback);
                    }
                }
            }
        }

    }

    public boolean isOnSameTeam(Player player , Player other){
        final ColorFaction me = Bases.getPlugin().getFactionManager().getColorFaction(player.getUniqueId());
        final ColorFaction them = Bases.getPlugin().getFactionManager().getColorFaction(other.getUniqueId());
        return me != null && me == them;

    }
    public int getVersion(Player player){
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion();
    }
    private void sendPacket(Player player , Packet packet){
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

}
