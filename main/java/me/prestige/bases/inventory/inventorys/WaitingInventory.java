package me.prestige.bases.inventory.inventorys;

import com.customhcf.util.ItemBuilder;
import me.prestige.bases.Bases;
import me.prestige.bases.inventory.InventorySnapshot;
import me.prestige.bases.scoreboard.provider.GameProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WaitingInventory extends InventorySnapshot {

    public static ItemStack blue;
    public static ItemStack red;
    public static ItemStack random;
    public static ItemStack yellow;
    public static ItemStack green;

    static {
        blue =  new ItemBuilder(Material.INK_SACK).data(DyeColor.BLUE.getDyeData()).displayName(ChatColor.AQUA + "Join Blue " + ChatColor.GRAY + "(Right Click)").build();
        red = new ItemBuilder(Material.INK_SACK).data(DyeColor.RED.getDyeData()).displayName(ChatColor.RED + "Join Red " + ChatColor.GRAY + "(Right Click)").build();
        random = new ItemBuilder(Material.NETHER_STAR).displayName(ChatColor.LIGHT_PURPLE + "Random Team " + ChatColor.GRAY + "(Right Click)").build();
        yellow = new ItemBuilder(Material.INK_SACK).data(DyeColor.YELLOW.getDyeData()).displayName(ChatColor.YELLOW + "Join Yellow " + ChatColor.GRAY + "(Right Click)").build();
        green = new ItemBuilder(Material.INK_SACK).data(DyeColor.GREEN.getDyeData()).displayName(ChatColor.GREEN + "Join Green " + ChatColor.GRAY + "(Right Click)").build();
    }


    Bases plugin;
    public WaitingInventory(Bases plugin) {
        super(plugin, null, Bukkit.getWorld("world").getSpawnLocation());
        this.plugin = plugin;
    }

    @Override
    public void applyTo(Player player, boolean teleport, boolean setItems) {
       super.applyTo(player, teleport, setItems);
        if(setItems){
            Inventory inventory = player.getInventory();
            inventory.setItem(2, blue);
            inventory.setItem(3, red);

            inventory.setItem(4, random);

            inventory.setItem(5, yellow);
            inventory.setItem(6, green);

        }
    }
}
