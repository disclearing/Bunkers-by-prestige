package me.prestige.bases.user;


import com.customhcf.util.GenericUtils;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FactionUser  implements ConfigurationSerializable{
    private final UUID userUUID;
    private int kills;
    private int deaths;

    public FactionUser(final UUID userUUID) {
        this.userUUID = userUUID;
    }
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("userUUID", this.userUUID.toString());
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        return map;
    }

    public FactionUser(final Map<String, Object> map) {
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.kills = (int) map.get("kills");
        this.deaths = (int) map.get("deaths");
    }


    public Integer getKills() {
        return this.kills;
    }

    public void setKills(final int kills) {
        this.kills = kills;
    }

    public void setDeaths(Integer deaths){
        this.deaths = deaths;
    }
    public Integer getDeaths(){
        return  deaths;
    }





    public UUID getUserUUID() {
        return this.userUUID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.userUUID);
    }


}
