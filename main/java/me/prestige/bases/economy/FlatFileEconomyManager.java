package me.prestige.bases.economy;

import com.customhcf.util.Config;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import me.prestige.bases.Bases;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

import java.util.UUID;

public class FlatFileEconomyManager implements EconomyManager {
    private TObjectIntMap<UUID> balanceMap;

    public FlatFileEconomyManager(final Bases plugin) {
        this.balanceMap = new TObjectIntHashMap<>();
    }

    @Override
    public TObjectIntMap<UUID> getBalanceMap() {
        return this.balanceMap;
    }

    @Override
    public Integer getBalance(final UUID uuid) {
        balanceMap.putIfAbsent(uuid, 0);
        return this.balanceMap.get(uuid);
    }

    @Override
    public Integer setBalance(final UUID uuid, final int amount) {
        this.balanceMap.put(uuid, amount);
        return amount;
    }

    @Override
    public Integer addBalance(final UUID uuid, final int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) + amount);
    }

    @Override
    public Integer subtractBalance(final UUID uuid, final int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) - amount);
    }



}
