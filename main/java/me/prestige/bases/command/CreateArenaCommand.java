package me.prestige.bases.command;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.prestige.bases.Bases;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by TREHOME on 02/14/2017.
 */
public class CreateArenaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set event claim areas");
            return true;
        }
        final WorldEditPlugin worldEditPlugin = Bases.getPlugin().getWorldEditPlugin();
        if(worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set event claim areas.");
            return true;
        }
        final Player player = (Player) sender;
        final Selection selection = worldEditPlugin.getSelection(player);
        if(selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        Bases.getPlugin().getGameManager().saveArena(selection.getMaximumPoint(), selection.getMinimumPoint());
        player.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "successfully " + ChatColor.YELLOW + "created the arena. Every game from now on will load on this save." );
        return false;
    }
}
