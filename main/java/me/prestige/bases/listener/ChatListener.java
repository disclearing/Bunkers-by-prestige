package me.prestige.bases.listener;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.event.FactionChatEvent;
import me.prestige.bases.faction.struct.ChatChannel;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
    private static final Pattern PATTERN;

    static {
        final ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
        PATTERN = Pattern.compile("\\W");
    }

    private final ConcurrentMap<Object, Object> messageHistory;
    private final ConcurrentMap<Object, Object> lastMessage;
    private final Bases plugin;

    public ChatListener(Bases plugin) {
        this.plugin = plugin;
        this.lastMessage = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
        this.messageHistory = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        final Player player = event.getPlayer();
        messageHistory.putIfAbsent(player.getUniqueId(), 0);
        messageHistory.put(player.getUniqueId(), (int)messageHistory.get(player.getUniqueId()) +1);
        lastMessage.put(player.getUniqueId(), System.currentTimeMillis());
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        final ColorFaction colorFaction = (ColorFaction) playerFaction;
        final ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        final Set<Player> recipients = event.getRecipients();
        if(chatChannel == ChatChannel.FACTION) {
            if(!this.isGlobalChannel(message)) {
                final Collection<Player> online = playerFaction.getOnlinePlayers();
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent((new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage())));
                return;
            }
            message = message.substring(1, message.length()).trim();
            event.setMessage(message);
        }
        ChatColor color = ChatColor.WHITE;
        if(colorFaction != null){
            color = colorFaction.getColor();
        }
        event.setCancelled(true);
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(color + player.getDisplayName() + " "+ ChatColor.GRAY + message);
        for(final Player recipient : event.getRecipients()) {
            recipient.sendMessage(color + player.getDisplayName()   + ChatColor.GRAY +" \u00BB "+ ChatColor.WHITE + message);
        }
    }




    private boolean isGlobalChannel(final String input) {
        final int length = input.length();
        if(length <= 1 || !input.startsWith("!")) {
            return false;
        }
        int i = 1;
        while(i < length) {
            final char character = input.charAt(i);
            if(character == ' ') {
                ++i;
            } else {
                if(character == '/') {
                    return false;
                }
                break;
            }
        }
        return true;
    }
}
