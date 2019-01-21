package me.prestige.bases.faction.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class WarzoneFaction extends Faction {
    public WarzoneFaction() {
        super("Warzone");
    }

    public WarzoneFaction(final Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(final CommandSender sender) {
        return ChatColor.GRAY + this.getName();
    }
}
