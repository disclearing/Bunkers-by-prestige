package me.prestige.bases.economy.gui;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.Menu;
import com.google.common.primitives.Ints;
import me.prestige.bases.Bases;
import me.prestige.bases.economy.EconomyItem;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.ChatMessage;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CombatShop extends Menu {

    private static Player player;

    public CombatShop(Player player) {
        super(ChatColor.RED + ChatColor.BOLD.toString() + "Combat Shop", 6);
        this.player = player;
        this.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName(" ").build());
        loadItems();
        loadClicks();
    }

    public static Map<Integer, ItemStack> createLoadMap(){
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        itemStackMap.put(10, new EconomyItem(player, Material.DIAMOND_HELMET, "Diamond Helmet", 75, 1).build());
        itemStackMap.put(19, new EconomyItem(player, Material.DIAMOND_CHESTPLATE, "Diamond Chestplate", 200, 1).build());
        itemStackMap.put(28, new EconomyItem(player, Material.DIAMOND_LEGGINGS, "Diamond Leggings", 150, 1).build());
        itemStackMap.put(37, new EconomyItem(player, Material.DIAMOND_BOOTS, "Diamond Boots", 75, 1).build());
        itemStackMap.put(18, new EconomyItem(player, Material.DIAMOND_SWORD, "Diamond Sword", 100, 1).build());
        itemStackMap.put(20, new ItemBuilder(Material.DIAMOND).displayName(getName("Full Armor", 500))
                .lore(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 25),
                        ChatColor.GRAY + "1 x Diamond Helmet",
                        ChatColor.GRAY + "1 x Diamond Chestplate",
                        ChatColor.GRAY + "1 x Diamond Leggings",
                        ChatColor.GRAY + "1 x Diamond Boots",
                        ChatColor.GRAY + "1 x Diamond Sword",
                        ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 25),
                        ChatColor.WHITE + "Price" + ChatColor.GRAY + ": " + (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= 500 ? ChatColor.GREEN : ChatColor.RED) + "$500")
                .build());
        itemStackMap.put(21, new ItemBuilder(Material.ENDER_PEARL, 16).displayName(getName("Ender Pearl", 25))
                .lore(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 31),
                        ChatColor.GRAY + "Buy 1 x Ender Pearl", ChatColor.GRAY + "Right click to buy 16 x Ender Pearl",
                        ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 31),
                        ChatColor.GRAY + "Price" + ChatColor.GRAY + ": "  + (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= 25 ? ChatColor.GREEN : ChatColor.RED) + "$25" + ChatColor.GRAY + " or " + (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= 400 ? ChatColor.GREEN : ChatColor.RED) + "$400" + ChatColor.GRAY + " for 16.").build());
        itemStackMap.put(41, new EconomyItem(player, Material.COOKED_BEEF, "Steak", 75, 16).build());
        itemStackMap.put(14, new ItemBuilder(new EconomyItem(player, Material.POTION, "Speed II (1:30)", 10, 1).build()).data((short) 8226).build());
        itemStackMap.put(15, new ItemBuilder(new EconomyItem(player, Material.POTION, "Fire Resistance (8:00)", 0, 1).build()).data((short) 8259).build());
        itemStackMap.put(16, new ItemBuilder(new EconomyItem(player, Material.POTION, "Team Invis (6:00)", 1250, 1).build()).data((short) 16462).build());
        itemStackMap.put(23, new ItemBuilder(new EconomyItem(player, Material.POTION, "Lesser Invis (3:00)", 100, 1).build()).data((short) 8238).build());
        itemStackMap.put(24, new ItemBuilder(new EconomyItem(player, Material.POTION, "Health Potion II", 20, 1).build()).lore(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 30), ChatColor.GRAY + "1 x Health Potion II", ChatColor.GRAY + "Right click to fill your inventory", ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 30), ChatColor.GRAY + "Price" + ChatColor.GRAY + ": " +  (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= 20 ? ChatColor.GREEN : ChatColor.RED) + "$20" + ChatColor.GRAY + " or " + (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= (getEmptySlots(player.getInventory()) * 20) ? ChatColor.GREEN : ChatColor.RED) + "$" + (getEmptySlots(player.getInventory()) * 20) + ChatColor.GRAY + " for " + getEmptySlots(player.getInventory()) + ".").data((short) 16421).build());
        itemStackMap.put(34, new ItemBuilder(new EconomyItem(player, Material.POTION, "Slowness Potion (1:07)", 50, 1).build()).data((short) 16426).build());
        itemStackMap.put(25, new ItemBuilder(new EconomyItem(player, Material.POTION, "Poison Potion (0:33)", 50, 1).build()).data((short) 16388).build());
        return itemStackMap;
    }

    private  static int getEmptySlots(Inventory inventory) {
        int i = 0;
        for (ItemStack is : inventory.getContents()) {
            if (is == null || is.getType() == Material.AIR) {
                i++;
            }
        }
        return i;
    }

    private void loadItems() {
        for(Map.Entry<Integer, ItemStack> items : createLoadMap().entrySet()){
            this.setItem(items.getKey(), items.getValue());
        }
    }

    private static String getName(String name, Integer price){
        String newName;
        if(Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= price){
            newName = ChatColor.GREEN + name;
        }else{
            newName = ChatColor.RED + name;
        }
        return newName;
    }


    private Integer getPriceFromString(String string){
        return Ints.tryParse(string.replace(ChatColor.GRAY + "Price" + ChatColor.GRAY + ": ", "").replace(ChatColor.GREEN.toString(), "").replace("$", "").replace(ChatColor.RED.toString(), ""));
    }


    private void loadClicks() {
        this.setGlobalAction((player, inventory, itemStack, slot, action) -> {
            if(itemStack.getType() == Material.STAINED_GLASS_PANE){
                return;
            }
            Integer price = getPriceFromString(itemStack.getItemMeta().getLore().get(3));
            if (!itemStack.getType().equals(Material.ENDER_PEARL) &&player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "Your inventory is full.");
                return;
            }
            if (price == null) {
                if (itemStack.getType().equals(Material.ENDER_PEARL)) {
                    price = 25;
                    if (action == InventoryAction.PICKUP_HALF) {
                        price = 400;
                    }
                    if (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) < price) {
                        player.sendMessage(ChatColor.RED + "You do not have enough money for this!");
                        return;
                    }
                    Map<Integer , ItemStack> tooFull = player.getInventory().addItem(price == 25 ? new ItemStack(Material.ENDER_PEARL) : new ItemStack(Material.ENDER_PEARL, 16));
                    Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), price);
                    if(!tooFull.isEmpty()){
                        player.closeInventory();
                        Inventory takeIt = Bukkit.createInventory(null , 9 );
                        takeIt.addItem(tooFull.values().toArray(new ItemStack[0]));
                        player.openInventory(takeIt);
                    }
                    return;
                }
                if (itemStack.getType().equals(Material.DIAMOND)) {
                    price = 500;
                    if (player.getInventory().getHelmet() != null || player.getInventory().getChestplate() != null || player.getInventory().getLeggings() != null || player.getInventory().getBoots() != null) {
                        player.sendMessage(ChatColor.RED + "You have armor on already!");
                        return;
                    }
                    if (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) < price) {
                        player.sendMessage(ChatColor.RED + "You do not have enough money for this!");
                        return;
                    }
                    player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                    Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), price);
                    return;
                }
                if(itemStack.getType().equals(Material.POTION) && itemStack.getDurability() == (short) 16421){
                    if (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) < 20) {
                        player.sendMessage(ChatColor.RED + "You do not have enough money for this!");
                        return;
                    }
                    if (action == InventoryAction.PICKUP_HALF) {
                        int emptySlot = getEmptySlots(player.getInventory());
                        for(int i = 0; i < emptySlot ; i++){
                            player.getInventory().addItem(new ItemBuilder(Material.POTION).data((short)16421).build());
                            Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 20);
                        }
                        return;
                    }else{
                        player.getInventory().addItem(new ItemBuilder(Material.POTION).data((short) 16421).build());
                        Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), 20);
                        return;
                    }
                }
                return;
            }
            if (Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) < price) {
                player.sendMessage(ChatColor.RED + "You do not have enough money for this!");
                return;
            }
            if(EnchantmentTarget.ARMOR.includes(itemStack)){
                if(EnchantmentTarget.ARMOR_HEAD.includes(itemStack)){
                    if (player.getInventory().getHelmet() != null) {
                        player.sendMessage(ChatColor.RED + "You have a helmet on already!");
                        return;
                    }
                    player.getInventory().setHelmet(new ItemStack(itemStack.getType(), itemStack.getAmount(), itemStack.getDurability()));
                }
                if(EnchantmentTarget.ARMOR_TORSO.includes(itemStack)){
                    if (player.getInventory().getChestplate() != null) {
                        player.sendMessage(ChatColor.RED + "You have chestplate on already!");
                        return;
                    }
                    player.getInventory().setChestplate(new ItemStack(itemStack.getType(), itemStack.getAmount(), itemStack.getDurability()));
                }
                if(EnchantmentTarget.ARMOR_LEGS.includes(itemStack)){
                    if (player.getInventory().getLeggings() != null) {
                        player.sendMessage(ChatColor.RED + "You have leggings on already!");
                        return;
                    }
                    player.getInventory().setLeggings(new ItemStack(itemStack.getType(), itemStack.getAmount(), itemStack.getDurability()));
                }
                if(EnchantmentTarget.ARMOR_FEET.includes(itemStack)){
                    if (player.getInventory().getBoots() != null) {
                        player.sendMessage(ChatColor.RED + "You have boots on already!");
                        return;
                    }
                    player.getInventory().setBoots(new ItemStack(itemStack.getType(), itemStack.getAmount(), itemStack.getDurability()));
                }
            }else{
                player.getInventory().addItem(new ItemStack(itemStack.getType(), itemStack.getAmount(), itemStack.getDurability()));
            }
            Bases.getPlugin().getEconomyManager().subtractBalance(player.getUniqueId(), price);
            return;
        });
    }



}
