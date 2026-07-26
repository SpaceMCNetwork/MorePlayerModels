/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 */
package noppes.mpm.shared.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class AssetsFinder {
    public static List<ResourceLocation> find(String root, String type) {
        return new ArrayList<ResourceLocation>(Minecraft.getInstance().getResourceManager().listResources(root, r -> r.getPath().endsWith(type)).keySet());
    }
}

