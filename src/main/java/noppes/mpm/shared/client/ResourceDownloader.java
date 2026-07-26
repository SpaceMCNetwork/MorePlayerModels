/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.resources.ResourceLocation
 */
package noppes.mpm.shared.client;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.shared.client.ImageDownloadAlt;

public class ResourceDownloader {
    private static final Set<ResourceLocation> active = Collections.synchronizedSet(new HashSet());
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void load(ImageDownloadAlt resource) {
        if (active.contains(resource.location)) {
            return;
        }
        active.add(resource.location);
        executor.execute(() -> {
            if (!resource.loadTextureFromServer()) {
                active.remove(resource.location);
                return;
            }
            Minecraft.getInstance().submit(() -> {
                Minecraft.getInstance().getTextureManager().register(resource.location, (AbstractTexture)resource);
                active.remove(resource.location);
            });
            try {
                Thread.sleep(400L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static ResourceLocation getUrlResourceLocation(String url, boolean fixSkin) {
        return ResourceLocation.fromNamespaceAndPath("moreplayermodels", "skins/" + (url + fixSkin).hashCode() + (fixSkin ? "" : "32"));
    }

    public static File getUrlFile(String url, boolean fixSkin) {
        return new File(MorePlayerModels.skinCache, "" + (url + fixSkin).hashCode());
    }

    public static boolean contains(ResourceLocation location) {
        return active.contains(location);
    }
}
