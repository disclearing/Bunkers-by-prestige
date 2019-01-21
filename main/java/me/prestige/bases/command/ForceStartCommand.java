package me.prestige.bases.command;

import com.customhcf.util.ItemBuilder;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ForceStartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    	Player player = (Player) commandSender;
    	player.sendMessage(ChatColor.YELLOW + "You have successfully forced the game to start");
        Bases.getPlugin().getGameManager().startGame();
        return false;
    }

}
