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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SellShop extends Menu {
    private static Player player;

    public SellShop(Player player) {
        super(ChatColor.RED + ChatColor.BOLD.toString() + "Sell Items", 1);
        this.player = player;
        this.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName(" ").build());
        loadItems();
    }

    public static Map<Integer, ItemStack> createLoadMap() {
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        itemStackMap.put(1, new ItemBuilder(Material.DIAMOND).displayName((hasItem(Material.DIAMOND) ? ChatColor.AQUA : ChatColor.RED) + "Sell Diamond").lore((hasItem(Material.DIAMOND) ? getLore(Material.DIAMOND, "Diamond", 100) : new String[]{ChatColor.GRAY + "You can sell Diamond here.", ChatColor.GRAY + "They sell for $100 each."})).build());
        itemStackMap.put(3, new ItemBuilder(Material.IRON_INGOT).displayName((hasItem(Material.IRON_INGOT) ? ChatColor.AQUA : ChatColor.RED) + "Sell Iron").lore((hasItem(Material.IRON_INGOT) ? getLore(Material.IRON_INGOT, "Iron Ingot", 25) : new String[]{ChatColor.GRAY + "You can sell Iron Ingot here.", ChatColor.GRAY + "They sell for $25 each."})).build());
        itemStackMap.put(5, new ItemBuilder(Material.COAL).displayName((hasItem(Material.COAL) ? ChatColor.AQUA : ChatColor.RED) + "Sell Coal").lore((hasItem(Material.COAL) ? getLore(Material.COAL, "Coal", 15) : new String[]{ChatColor.GRAY + "You can sell Coal here.", ChatColor.GRAY + "They sell for $15 each"})).build());
        itemStackMap.put(7, new ItemBuilder(Material.GOLD_INGOT).displayName((hasItem(Material.GOLD_INGOT) ? ChatColor.AQUA : ChatColor.RED) + "Sell Gold").lore((hasItem(Material.GOLD_INGOT) ? getLore(Material.GOLD_INGOT, "Gold Ingot", 50) : new String[]{ChatColor.GRAY + "You can sell Gold here.", ChatColor.GRAY + "They sell for $50 each"})).build());
        return itemStackMap;
    }

    private void loadItems() {
        for (Map.Entry<Integer, ItemStack> items : createLoadMap().entrySet()) {
            this.setItem(items.getKey(), items.getValue());
        }
        loadClicks();
    }

    private static String[] getLore(Material material, String name, Integer price){
       return new String[]{
                ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 38),
                ChatColor.GRAY + "Left click to sell 1 x " + name + " for " + ChatColor.GREEN + '$' +price + ChatColor.GRAY + '.',
                ChatColor.GRAY + "Right click to sell " + getAmount(player.getInventory(), material) + " x " + name +" for " + ChatColor.GREEN + '$' + (price * getAmount(player.getInventory(), material)) + ChatColor.GRAY + '.',
               ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 38)
        };
    }

    private static int getAmount(Inventory inventory, Material material) {
        int i = 0;
        for (ItemStack is : inventory.getContents()) {
            if (is == null || !is.getType().equals(material)) {
                continue;
            }
            i += is.getAmount();
        }
        return i;
    }


    private static boolean hasItem(Material material){
        for(ItemStack itemStack1 : player.getInventory().getContents()){
            if(itemStack1 == null) continue;
            if(itemStack1.getType().equals(material)){
                return true;
            }
        }
        return false;
    }




    private void loadClicks() {
        this.setGlobalAction((player, inventory, itemStack, slot, action) -> {
                        if(action == InventoryAction.PICKUP_HALF){
                            if(slot == 1){
                                int amount = 0;
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack == null) continue;
                                    if(stack.getType().equals(Material.DIAMOND)){
                                        amount += stack.getAmount();
                                        player.getInventory().remove(stack);
                                    }
                                }
                                Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), amount * 100);
                            }else if(slot == 3){
                                int amount = 0;
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack == null) continue;
                                    if(stack.getType().equals(Material.IRON_INGOT)){
                                        amount += stack.getAmount();
                                        player.getInventory().remove(stack);
                                    }
                                }
                                Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), amount * 25);
                            }else  if (slot == 5){
                                int amount = 0;
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack == null) continue;
                                    if(stack.getType().equals(Material.COAL)){
                                        amount += stack.getAmount();
                                        player.getInventory().remove(stack);
                                    }
                                }
                                Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), amount * 15);
                            }else {
                                if(slot != 7){
                                    return;
                                }
                                int amount = 0;
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack == null) continue;
                                    if(stack.getType().equals(Material.GOLD_INGOT)){
                                        amount += stack.getAmount();
                                        player.getInventory().remove(stack);
                                    }
                                }
                                Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), amount * 50);

                            }
                            return;
                        }else {
                            if(slot == 1){
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack != null && stack.getType().equals(Material.DIAMOND)) {
                                        if(stack.getAmount() <= 1){
                                            player.getInventory().remove(stack);
                                        }else {
                                            stack.setAmount(stack.getAmount() - 1);
                                        }
                                        Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), 100);
                                        return;
                                    }
                                }
                            }else if(slot == 3){
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack != null && stack.getType().equals(Material.IRON_INGOT)){
                                        if(stack.getAmount() <= 1){
                                            player.getInventory().remove(stack);
                                        }else {
                                            stack.setAmount(stack.getAmount() - 1);
                                        }
                                        Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), 25);
                                        return;
                                    }
                                }
                            }else  if (slot == 5){
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack != null && stack.getType().equals(Material.COAL)){
                                        if(stack.getAmount() <= 1){
                                            player.getInventory().remove(stack);
                                        }else {
                                            stack.setAmount(stack.getAmount() - 1);
                                        }
                                        Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), 15);
                                        return;
                                    }
                                }
                            }else {
                                if(slot != 7){
                                    return;
                                }
                                for(ItemStack stack : player.getInventory().getContents()){
                                    if(stack != null && stack.getType().equals(Material.GOLD_INGOT)){
                                        if(stack.getAmount() <= 1){
                                            player.getInventory().remove(stack);
                                        }else {
                                            stack.setAmount(stack.getAmount() - 1);
                                        }
                                        Bases.getPlugin().getEconomyManager().addBalance(player.getUniqueId(), 50);
                                        return;
                                    }
                                }
                            }
                        }
        });
    }
}