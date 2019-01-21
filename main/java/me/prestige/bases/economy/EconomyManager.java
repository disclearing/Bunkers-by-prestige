package me.prestige.bases.economy;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;

import java.util.UUID;

public interface EconomyManager {

    TObjectIntMap<UUID> getBalanceMap();

    Integer getBalance(UUID p0);

    Integer setBalance(UUID p0, int p1);

    Integer addBalance(UUID p0, int p1);

    Integer subtractBalance(UUID p0, int p1);


}
