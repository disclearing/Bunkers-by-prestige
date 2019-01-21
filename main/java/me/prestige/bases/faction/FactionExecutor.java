package me.prestige.bases.faction;

import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.argument.*;
import me.prestige.bases.faction.argument.staff.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionExecutor extends ArgumentExecutor {
    private final CommandArgument helpArgument;

    public FactionExecutor(final Bases plugin) {
        super("faction");
        addArgument(new FactionPunishArgument(plugin));
        addArgument(new FactionLockArgument(plugin));
        addArgument(new FactionChatArgument(plugin));
        addArgument(new FactionClearClaimsArgument(plugin));
        addArgument(new FactionForceJoinArgument(plugin));
        addArgument(this.helpArgument = new FactionHelpArgument(this));
        addArgument(new FactionHomeArgument(this, plugin));
        addArgument(new FactionSetHomeForArgument(plugin));
        addArgument(new FactionSetVillagerArgument(plugin));
        addArgument(new FactionSpawnVillagerArgument(plugin));
        addArgument(new FactionClaimForArgument(plugin));
        addArgument(new FactionMessageArgument(plugin));
        addArgument(new FactionVersionArgument());
        addArgument(new FactionRemoveArgument(plugin));
        addArgument(new FactionSetDtrArgument(plugin));
        addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        addArgument(new FactionShowArgument(plugin));
        addArgument(new FactionStuckArgument(plugin));
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }
        final CommandArgument argument = this.getArgument(args[0]);
        if(argument != null) {
            final String permission = argument.getPermission();
            if(permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }


}
