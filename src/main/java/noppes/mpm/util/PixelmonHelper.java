/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.neoforged.fml.ModList
 *  org.apache.logging.log4j.LogManager
 */
package noppes.mpm.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import noppes.mpm.LogWriter;
import org.apache.logging.log4j.LogManager;

public class PixelmonHelper {
    public static boolean Enabled = false;
    private static Method getPixelmonModel = null;
    private static Class modelSetupClass;
    private static Method modelSetupMethod;

    public static void load() {
        Enabled = ModList.get().isLoaded("pixelmon");
        if (!Enabled) {
            return;
        }
        try {
            Class<?> c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2Client");
            getPixelmonModel = c.getMethod("getModel", new Class[0]);
            modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
            modelSetupMethod = modelSetupClass.getMethod("setupForRender", c);
        }
        catch (Exception e) {
            LogWriter.except(e);
            Enabled = false;
        }
    }

    public static List<String> getPixelmonList() {
        ArrayList<String> list = new ArrayList<String>();
        if (!Enabled) {
            return list;
        }
        try {
            Object[] array;
            Class<?> c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemonModel");
            for (Object ob : array = c.getEnumConstants()) {
                list.add(ob.toString());
            }
        }
        catch (Exception e) {
            LogManager.getLogger().error("getPixelmonList", (Throwable)e);
        }
        return list;
    }

    public static boolean isPixelmon(Entity entity) {
        if (!Enabled) {
            return false;
        }
        return entity.getType().getDescriptionId().toLowerCase().contains("pixelmon");
    }

    public static Object getModel(LivingEntity entity) {
        try {
            return getPixelmonModel.invoke(entity, new Object[0]);
        }
        catch (Exception e) {
            LogManager.getLogger().error("getModel", (Throwable)e);
            return null;
        }
    }

    public static void setupModel(LivingEntity entity, Object model) {
        try {
            if (modelSetupClass.isAssignableFrom(model.getClass())) {
                modelSetupMethod.invoke(model, entity);
            }
        }
        catch (Exception e) {
            LogManager.getLogger().error("setupModel", (Throwable)e);
        }
    }

    public static String getName(LivingEntity entity) {
        if (!Enabled || !PixelmonHelper.isPixelmon((Entity)entity)) {
            return "";
        }
        try {
            Method m = entity.getClass().getMethod("getName", new Class[0]);
            return m.invoke(entity, new Object[0]).toString();
        }
        catch (Exception e) {
            LogManager.getLogger().error("getName", (Throwable)e);
            return "";
        }
    }

    public static void debug(LivingEntity entity) {
        if (!Enabled || !PixelmonHelper.isPixelmon((Entity)entity)) {
            return;
        }
        try {
            Method m = entity.getClass().getMethod("getModel", new Class[0]);
            LocalPlayer player = Minecraft.getInstance().player;
            player.sendSystemMessage((Component)Component.literal((String)((String)m.invoke(entity, new Object[0]))));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
