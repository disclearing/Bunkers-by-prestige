package me.prestige.bases.nms;

import net.minecraft.server.v1_7_R4.BiomeBase;
import net.minecraft.server.v1_7_R4.BiomeMeta;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Nms {
    public static void registerEntity(final String name, final int id, final Class<? extends EntityInsentient> nmsClass, final Class<? extends EntityInsentient> customClass) {
        try {
            final List<Map<?, ?>> dataMap = new ArrayList<Map<?, ?>>();
            for (final Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>)f.get(null));
                }
            }
            if (dataMap.get(2).containsKey(id)) {
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }
            final Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, Integer.TYPE);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
