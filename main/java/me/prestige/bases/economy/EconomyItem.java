package me.prestige.bases.economy;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.ItemBuilder;
import me.prestige.bases.Bases;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EconomyItem {

    private ItemStack stack;


    public EconomyItem(Player player, Material material, String name, Integer price, Integer amount){
        stack = new ItemBuilder(material, amount).displayName((Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= price ? ChatColor.GREEN : ChatColor.RED) +name).lore(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 25), ChatColor.GRAY.toString() + amount + " x " + name, ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 25), ChatColor.GRAY + "Price" + ChatColor.GRAY +": "  +(Bases.getPlugin().getEconomyManager().getBalance(player.getUniqueId()) >= price ? ChatColor.GREEN : ChatColor.RED) + "$"  +price).build();
    }

    public ItemStack build(){
    return stack;
    }
}
