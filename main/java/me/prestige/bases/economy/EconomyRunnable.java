package me.prestige.bases.economy;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.ItemBuilder;
import me.prestige.bases.Bases;
import me.prestige.bases.economy.gui.BlockShop;
import me.prestige.bases.economy.gui.CombatShop;
import me.prestige.bases.economy.gui.EnchantShop;
import me.prestige.bases.economy.gui.SellShop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class EconomyRunnable extends BukkitRunnable {

    private Bases plugin;

    public EconomyRunnable(Bases plugin){
        this.plugin = plugin;
    }


    @Override
    public void run() {
        for(Player on : Bukkit.getOnlinePlayers()){
            plugin.getEconomyManager().addBalance(on.getUniqueId(), 3);
            if(on.getOpenInventory().getTitle().contains("Combat")){
                for(Map.Entry<Integer, ItemStack> itemStack : CombatShop.createLoadMap().entrySet()){
                    on.getOpenInventory().setItem(itemStack.getKey(), null);
                    on.getOpenInventory().setItem(itemStack.getKey(), itemStack.getValue());
                }
            }
            if(on.getOpenInventory().getTitle().contains("Blcok")){
                for(Map.Entry<Integer, ItemStack> itemStack : BlockShop.createLoadMap().entrySet()){
                    on.getOpenInventory().setItem(itemStack.getKey(), null);
                    on.getOpenInventory().setItem(itemStack.getKey(), itemStack.getValue());
                }
            }
            if(on.getOpenInventory().getTitle().contains("Sell")){
                for(Map.Entry<Integer, ItemStack> itemStack : SellShop.createLoadMap().entrySet()){
                    on.getOpenInventory().setItem(itemStack.getKey(), null);
                    on.getOpenInventory().setItem(itemStack.getKey(), itemStack.getValue());
                }
            }
            if(on.getOpenInventory().getTitle().contains("enchant")){
                for(Map.Entry<Integer, ItemStack> itemStack : EnchantShop.createLoadMap().entrySet()){
                    on.getOpenInventory().setItem(itemStack.getKey(), null);
                    on.getOpenInventory().setItem(itemStack.getKey(), itemStack.getValue());
                }
            }
        }
    }
}
