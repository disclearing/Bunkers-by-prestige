package me.prestige.bases.faction.argument;

import com.customhcf.util.command.CommandArgument;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionVersionArgument extends CommandArgument {
    public FactionVersionArgument() {
        super("version", "Gets the faction version information.");
    }

    @Override
    public String getUsage(final String label) {
        return "";
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChatColor.YELLOW + "This plugin was made by " + ChatColor.RED + "Addons & Squirted" + ChatColor.GOLD + "\nLicensed for Kirai.rip ");
        return false;
    }
}
