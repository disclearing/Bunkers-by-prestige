package me.prestige.bases.nms;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

import java.lang.reflect.Field;
import java.util.List;

public class Villager extends EntityVillager {

    private String job;

    public Villager(final World world) {
        super(world);
    }

    public void move(final double d0, final double d1, final double d2) {
    }





    public void collide(final Entity entity) {
    }

    public void g(final double d0, final double d1, final double d2) {
    }

    public EntityAgeable createChild(final EntityAgeable entityAgeable) {
        return null;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}