package me.prestige.bases.faction.struct;

public interface Raidable {
    boolean isRaidable();

    double getDeathsUntilRaidable();

    double getMaximumDeathsUntilRaidable();

    double setDeathsUntilRaidable(double p0);


    RegenStatus getRegenStatus();
}
