package me.prestige.bases.inventory;

import me.prestige.bases.Bases;
import me.prestige.bases.inventory.inventorys.WaitingInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class InventoryHandler {

    public WaitingInventory waitingInventory;

    public InventoryHandler(Bases plugin){
        waitingInventory = new WaitingInventory(plugin);

    }

}