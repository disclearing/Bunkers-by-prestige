package me.prestige.bases.faction.struct;

import com.customhcf.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Relation {
    MEMBER(3),
    ENEMY(1);

    private final int value;

    private Relation(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isAtLeast(final Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(final Relation relation) {
        return this.value <= relation.value;
    }

    public boolean isMember() {
        return this == Relation.MEMBER;
    }


    public boolean isEnemy() {
        return this == Relation.ENEMY;
    }

    public String getDisplayName() {
        switch(this) {
            default: {
                return this.toChatColour() + this.name().toLowerCase();
            }
        }
    }

    public ChatColor toChatColour() {
        switch(this) {
            case MEMBER: {
                return ChatColor.GREEN;
            }
            default: {
                return ChatColor.DARK_AQUA;
            }
        }
    }

    public DyeColor toDyeColour() {
        return BukkitUtils.toDyeColor(this.toChatColour());
    }
}
