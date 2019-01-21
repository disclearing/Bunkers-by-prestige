package me.prestige.bases.economy.gui;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.Menu;
import com.google.common.primitives.Ints;
import me.prestige.bases.Bases;
import me.prestige.bases.economy.EconomyItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantShop extends Menu {
    private static Player player;

    public EnchantShop(Player player) {
        super(ChatColor.GREEN + ChatColor.BOLD.toString() + "Tim the enchanter", 1);
        this.player = player;
        this.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName(" ").build());
        loadItems();
    }

    public static Map<Integer, ItemStack> createLoadMap() {
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        itemStackMap.put(0, new EconomyItem(player, Material.ENCHANTED_BOOK, "Protection I", 1200, 1).build());
        itemStackMap.put(1, new EconomyItem(player, Material.ENCHANTED_BOOK, "Sharpness I", 300, 1).build());
        itemStackMap.put(2, new EconomyItem(player, Material.ENCHANTED_BOOK, "Feather Falling IV", 200, 1).build());
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
            if (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) < price) {
                player.sendMessage(ChatColor.RED + "You do not have enough money for this!");
                return;
            }
            Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), price);
            if(price == 1200){
                int count = 0;
                for(ItemStack armor : player.getInventory().getArmorContents()){
                    if(armor == null) continue;
                    count++;
                    armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                }
                if(count == 0){
                    player.sendMessage(ChatColor.RED + "You do not have any armor on!");
                    Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), price);
                    return;
                }
            }
            if(price == 300){
                for(ItemStack sword : player.getInventory().getContents()){
                    if(sword == null || !sword.getType().equals(Material.DIAMOND_SWORD)) continue;
                    sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    return;
                }
            }
            if(price == 200){
                if(player.getInventory().getBoots() == null){
                    player.sendMessage(ChatColor.RED + "You do not have any boots on!");
                    Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), price);
                    return;
                }
                player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_FALL, 4);
                return;
            }
        });
    }
}