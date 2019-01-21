package me.prestige.bases.economy.gui;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.Menu;
import com.google.common.primitives.Ints;
import me.prestige.bases.Bases;
import me.prestige.bases.economy.EconomyItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BlockShop extends Menu {
    private static Player player;

    public BlockShop(Player player) {
        super(ChatColor.RED + ChatColor.BOLD.toString() + "Build Shop", 6);
        this.player = player;
        this.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName(" ").build());
        loadItems();
    }

    public static Map<Integer, ItemStack> createLoadMap() {
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        itemStackMap.put(0, new EconomyItem(player, Material.CHEST, "Chest", 250, 16).build());
        itemStackMap.put(1, new EconomyItem(player, Material.STONE, "Stone", 10, 16).build());
        itemStackMap.put(2, new EconomyItem(player, Material.COBBLESTONE, "Cobblestone", 10, 16).build());
        itemStackMap.put(3, new EconomyItem(player, Material.SMOOTH_BRICK, "Stone Bricks", 10, 16).build());
        itemStackMap.put(4, new EconomyItem(player, Material.STEP, "Stone Slab", 5, 16).build());
        itemStackMap.put(5, new EconomyItem(player, Material.FENCE_GATE, "Fence Gate", 30, 16).build());
        itemStackMap.put(6, new EconomyItem(player, Material.STONE_PLATE, "Pressure Plate", 30, 16).build());
        itemStackMap.put(7, new EconomyItem(player, Material.LADDER, "Ladder", 10, 16).build());
        itemStackMap.put(8, new EconomyItem(player, Material.STONE_BUTTON, "Button", 5, 16).build());

        itemStackMap.put(48, new EconomyItem(player, Material.DIAMOND_PICKAXE, "Diamond Pickaxe", 50, 1).build());
        itemStackMap.put(49, new EconomyItem(player, Material.DIAMOND_AXE, "Diamond Axe", 25, 1).build());
        itemStackMap.put(50, new EconomyItem(player, Material.DIAMOND_SPADE, "Diamond Shovel", 25, 1).build());
        return itemStackMap;
    }

    private void loadItems() {
        for (Map.Entry<Integer, ItemStack> items : createLoadMap().entrySet()) {
            this.setItem(items.getKey(), items.getValue());
        }
        loadClicks();
    }



    private Integer getPriceFromString(String string){
        return Ints.tryParse(string.replace(ChatColor.GRAY + "Price" + ChatColor.GRAY + ": ", "").replace(ChatColor.GREEN.toString(), "").replace("$", "").replace(ChatColor.RED.toString(), ""));
    }

    private void loadClicks() {
        this.setGlobalAction((player, inventory, itemStack, slot, action) -> {
            Integer price = getPriceFromString(itemStack.getItemMeta().getLore().get(3));
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "Your inventory is full.");
                return;
            }
            if (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) < price) {
                player.sendMessage(ChatColor.RED + "You do not have enough money for this!");
                return;
            }
            player.getInventory().addItem(new ItemStack(itemStack.getType(), itemStack.getAmount()));
            Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), price);
            return;
        });
    }
}