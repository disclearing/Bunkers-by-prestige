package me.prestige.bases.tab;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class TabEntry {

    private static Class<?> GAME_PROFILE;
    private static Constructor CONSTRUCTOR;
    private static Method GET_NAME;
    private static Method GET_UUID;
    private static Field TARGET;

    static {
        try {
            GAME_PROFILE = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
        } catch (ClassNotFoundException e) {
            try {
                GAME_PROFILE = Class.forName("com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        try {
            CONSTRUCTOR = GAME_PROFILE.getConstructor(UUID.class, String.class);
            GET_NAME = GAME_PROFILE.getDeclaredMethod("getName");
            GET_UUID = GAME_PROFILE.getDeclaredMethod("getId");
            TARGET = PacketPlayOutPlayerInfo.class.getDeclaredField("player");
            CONSTRUCTOR.setAccessible(true);
            GET_NAME.setAccessible(true);
            GET_UUID.setAccessible(true);
            TARGET.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private Object gameProfile;
    private String text;
    private int ping;
    @Getter
    @Setter
    private boolean isPingDirty = true;
    private PacketPlayOutScoreboardTeam packetPlayOutPlayerInfo = new PacketPlayOutScoreboardTeam();

    @SneakyThrows
    public TabEntry(String name) {
        this.gameProfile = CONSTRUCTOR.newInstance(UUID.randomUUID(), name);
    }

    @SneakyThrows
    public void write(PacketPlayOutPlayerInfo packet) {
        TARGET.set(packet, gameProfile);
    }

    @SneakyThrows
    public UUID getUUID() {
        return (UUID) GET_UUID.invoke(gameProfile);
    }

    @SneakyThrows
    public String getName() {
        return (String) GET_NAME.invoke(gameProfile);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PacketPlayOutScoreboardTeam getCachedPacket() {
        return packetPlayOutPlayerInfo;
    }

    public int getPing(){
        return ping;
    }

    public void setPing(int ping){
        if(this.ping == ping){
            return;
        }
        this.ping = ping;
        isPingDirty = true;
    }

}
