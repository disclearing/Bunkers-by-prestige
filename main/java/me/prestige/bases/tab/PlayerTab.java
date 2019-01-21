package me.prestige.bases.tab;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerTab {
    private Player player;
    private boolean prepared = false;
    private TabEntry[][] tab = new TabEntry[21][3];


    public PlayerTab(Player player) {
        this.player = player;
    }

    public void setText(int x, int z, String input) {
        TabEntry tabEntry = tab[x][z];

        if (tabEntry.getText() != null && tabEntry.getText().equalsIgnoreCase(input)) {
            return;
        }
        tabEntry.setText(input);
        if (input.length() <= 16) {
            updateTeamClientSide(player, tabEntry.getCachedPacket(), "tab-entry-" + x + "-" + z, input, "");
        } else {
            String first = input.substring(0, 16);
            String second = input.substring(16, input.length());
            if (first.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                first = first.substring(0, first.length() - 1);
                second = ChatColor.COLOR_CHAR + second;
            }
            String lastColors = ChatColor.getLastColors(first);
            second = lastColors + second;
            updateTeamClientSide(player, tabEntry.getCachedPacket(), "tab-entry-" + x + "-" + z, first, StringUtils.left(second, 16));

        }
    }

    public void prepareOrReset() {
        if (!prepared) {
            prepared = true;
            for(Player other : Bukkit.getOnlinePlayers()){
                PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)other).getHandle());
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutPlayerInfo);
            }
            for (int x = 0; x < 21; x++) {
                for (int z = 0; z < 3; z++) {
                    tab[x][z] = new TabEntry(getNameForPosition(x, z));
                    createTeamClientSide(player, "tab-entry-" + x + "-" + z);
                    final PacketPlayOutScoreboardTeam textPacket = new PacketPlayOutScoreboardTeam();
                    textPacket.a = "tab-entry-" + x + "-" + z;
                    textPacket.e.add(tab[x][z].getName());
                    textPacket.f = 3;
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket( textPacket);
                }
            }
        } else {
            for (int x = 0; x < 21; x++) {
                for (int z = 0; z < 3; z++) {
                    TabEntry entry = tab[x][z];
                    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
                    packet.action = PacketPlayOutPlayerInfo.REMOVE_PLAYER;
                    packet.username = entry.getName();
                    entry.write(packet);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

                }
            }
        }
        for (int x = 0; x < 21; x++) {
            for (int z = 0; z < 3; z++) {
                TabEntry entry = tab[x][z];
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
                packet.action = PacketPlayOutPlayerInfo.ADD_PLAYER;
                packet.username = entry.getName();
                entry.write(packet);
                packet.ping = 1;
                packet.gamemode = 0;
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    private void updateTeamClientSide(final Player player, PacketPlayOutScoreboardTeam recycle, String name, String prefix, String suffix) {
        name = StringUtils.left(name, 16);
        final PacketPlayOutScoreboardTeam textPacket = recycle;
        textPacket.a = name;
        textPacket.c = prefix;
        textPacket.d = suffix;
        textPacket.f = 2;
        textPacket.g = 0;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(textPacket);
    }

    private void createTeamClientSide(final Player player, String name) {
        name = StringUtils.left(name, 16);
        final PacketPlayOutScoreboardTeam textPacket = new PacketPlayOutScoreboardTeam();
        textPacket.a = name;
        textPacket.c = "";
        textPacket.d = "";
        textPacket.f = 0;
        textPacket.g = 0;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(textPacket);
    }

    public String getNameForPosition(int x, int z) {
        if (z > 10) {
            return ChatColor.RESET.toString() + ChatColor.AQUA + ChatColor.values()[x] + ChatColor.values()[z - 10] + ChatColor.RESET;
        } else {
            return ChatColor.RESET.toString() + ChatColor.values()[x] + ChatColor.values()[z] + ChatColor.RESET;
        }
    }

    public void setPing(int x, int z,int ping) {
        TabEntry tabEntry = tab[x][z];
        tabEntry.setPing(ping);
        if(tabEntry.isPingDirty()){
            tabEntry.setPingDirty(false);
            PacketPlayOutPlayerInfo updatePing = new PacketPlayOutPlayerInfo();
            tabEntry.write(updatePing);
            updatePing.action = PacketPlayOutPlayerInfo.UPDATE_LATENCY;
            updatePing.username = tabEntry.getName();
            updatePing.ping = tabEntry.getPing();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(updatePing);
        }
    }
}
