package me.prestige.bases.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;

public interface SidebarProvider {
    String getTitle();

    List<SidebarEntry> getLines(Player p0);
}
