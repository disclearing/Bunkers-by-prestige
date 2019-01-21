package me.prestige.bases.faction.argument;

import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.FactionExecutor;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import me.prestige.bases.kothgame.faction.EventFaction;
import me.prestige.bases.timer.PlayerTimer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class FactionHomeArgument extends CommandArgument {
    private final FactionExecutor factionExecutor;
    private final Bases plugin;

    public FactionHomeArgument(final FactionExecutor factionExecutor, final Bases plugin) {
        super("home", "Teleport to the faction home.");
        this.factionExecutor = factionExecutor;
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        final UUID uuid = player.getUniqueId();
        PlayerTimer timer = this.plugin.getTimerManager().enderPearlTimer;
        long remaining = timer.getRemaining(player);
        if(remaining > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + Bases.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
            return true;
        }
        if((remaining = (timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player)) > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + Bases.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
            return true;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final Location home = playerFaction.getHome();
        if(home == null) {
            sender.sendMessage(ChatColor.RED + "Your faction does not have a home set.");
            return true;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if(factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
            return true;
        }
        long millis;

            switch(player.getWorld().getEnvironment()) {
                case THE_END: {
                    millis = 30000L;
                    break;
                }
                case NETHER: {
                    millis = 30000L;
                    break;
                }
                default: {
                    millis = 10000L;
                    break;
                }
            }
        if(!factionAt.equals(playerFaction) && factionAt instanceof PlayerFaction) {
            if(!((PlayerFaction) factionAt).isRaidable()) {
                player.sendMessage(ChatColor.RED + "You are in a claim, if your stuck use /f stuck");
                return true;
            } else {
                millis *= 2l;
            }
        }
            this.plugin.getTimerManager().teleportTimer.teleport(player, home, 1, ChatColor.WHITE + "Teleported to your faction home "+ChatColor.GOLD +"instantly "+ChatColor.WHITE +"since there is no enemies nearby.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
    }
}
