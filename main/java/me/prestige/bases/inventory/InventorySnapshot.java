package me.prestige.bases.inventory;

import me.prestige.bases.Bases;
import me.prestige.bases.scoreboard.PlayerBoard;
import me.prestige.bases.scoreboard.SidebarProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public abstract class InventorySnapshot {
    public final SidebarProvider sidebarProvider;
    private final Bases plugin;
    private final Location location;


    public InventorySnapshot(final Bases plugin, final SidebarProvider sidebarProvider, final Location location) {
        this.plugin = plugin;
        this.sidebarProvider = sidebarProvider;
        this.location = location;
    }

    public void applyTo(final Player player, final boolean teleport, final boolean setItems) {
        if (this.sidebarProvider != null) {
            /*
            final PlayerBoard playerBoard = this.plugin.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
            if (playerBoard != null) {
                playerBoard.setDefaultSidebar(this.sidebarProvider, 2L);
            }
            */
        }
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (setItems) {
            final PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents(new ItemStack[inventory.getArmorContents().length]);
        }
        if (teleport && this.location != null && !player.isDead()) {
            player.teleport(this.location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }
}