package me.prestige.bases.faction.struct;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum ChatChannel {
    FACTION("Faction"),
    PUBLIC("Public");

    private final String name;

    private ChatChannel(final String name) {
        this.name = name;
    }

    public static ChatChannel parse(final String id) {
        return parse(id, ChatChannel.PUBLIC);
    }

    public static ChatChannel parse(String id, final ChatChannel def) {
        final String lowerCase;
        id = (lowerCase = id.toLowerCase(Locale.ENGLISH));
        switch(lowerCase) {
            case "f":
            case "faction":
            case "fc":
            case "fac":
            case "fact": {
                return ChatChannel.FACTION;
            }
            case "p":
            case "pc":
            case "g":
            case "gc":
            case "global":
            case "pub":
            case "publi":
            case "public": {
                return ChatChannel.PUBLIC;
            }
            default: {
                return (def == null) ? null : def.getRotation();
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        String prefix = null;
        switch(this) {
            case FACTION: {
                prefix = ChatColor.GREEN.toString();
                break;
            }
            default: {
                prefix = ChatColor.RED.toString();
                break;
            }
        }
        return prefix + this.name;
    }

    public String getShortName() {
        switch(this) {
            case FACTION: {
                return "FC";
            }
            default: {
                return "PC";
            }
        }
    }

    public ChatChannel getRotation() {
        switch(this) {
            case FACTION: {
                return ChatChannel.PUBLIC;
            }
            case PUBLIC: {
                return ChatChannel.FACTION;
            }
            default: {
                return ChatChannel.PUBLIC;
            }
        }
    }

    public String getRawFormat(final Player player) {
        switch(this) {
            case FACTION: {
                return ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "Team" + ChatColor.GRAY +"] " + ChatColor.LIGHT_PURPLE+ player.getName() + ChatColor.WHITE + " %2$s";
            }
            default: {
                throw new IllegalArgumentException("Cannot get the raw format for public chat channel");
            }
        }
    }
}
