/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.resources.DefaultPlayerSkin
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 */
package noppes.mpm.client;

import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import noppes.mpm.ModelData;
import noppes.mpm.shared.client.ImageDownloadAlt;
import noppes.mpm.shared.client.ResourceDownloader;

public class SkinUtil {
    public static long lastSkinTick = -30L;
    public static final int MaxUrlTicks = 6;

    public static void loadPlayerResource(Player pl, ModelData data) {
        Minecraft minecraft = Minecraft.getInstance();
        data.textureObject = null;
        AbstractClientPlayer player = (AbstractClientPlayer)pl;
        if (data.url != null && !data.url.isEmpty()) {
            if (!data.url.startsWith("http://") && !data.url.startsWith("https://")) {
                ResourceLocation location;
                try {
                    location = ResourceLocation.parse(data.url);
                    data.textureObject = minecraft.getTextureManager().getTexture(location);
                }
                catch (Exception e) {
                    location = DefaultPlayerSkin.getDefaultTexture();
                }
                data.resourceLocation = location;
                data.resourceLoaded = true;
            } else {
                boolean noEntity = data.getEntity(pl) == null;
                ResourceLocation location = ResourceDownloader.getUrlResourceLocation(data.url, noEntity);
                File file = ResourceDownloader.getUrlFile(data.url, noEntity);
                if (file.exists()) {
                    file.delete();
                }
                data.textureObject = SkinUtil.loadTexture(data, file, location, DefaultPlayerSkin.getDefaultTexture(), data.url, noEntity);
            }
        }
    }

    private static AbstractTexture loadTexture(ModelData data, File file, ResourceLocation resource, ResourceLocation def, String par1Str, boolean fix64) {
        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        AbstractTexture object = texturemanager.getTexture(resource, null);
        if (object == null) {
            data.resourceLoading = resource;
            ResourceDownloader.load(new ImageDownloadAlt(file, par1Str, resource, def, fix64, () -> {
                if (data.resourceLoading == resource) {
                    data.resourceLoaded = true;
                    data.resourceLocation = resource;
                }
            }));
        } else {
            data.resourceLoaded = true;
            data.resourceLocation = resource;
        }
        return object;
    }

    public static synchronized void load(ModelData data, Player player) {
        if (!data.resourceInit && lastSkinTick > 6L) {
            data.resourceInit = true;
            SkinUtil.loadPlayerResource(player, data);
            lastSkinTick = 0L;
        }
    }

    /** Returns MPM's downloaded skin when one is ready, otherwise vanilla's skin. */
    public static ResourceLocation getTexture(AbstractClientPlayer player) {
        ModelData data = ModelData.get((Player)player);
        SkinUtil.load(data, (Player)player);
        if (data.resourceLoaded && data.resourceLocation != null) {
            return data.resourceLocation;
        }
        return player.getSkin().texture();
    }

    public static void reloadSkins() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        for (net.minecraft.world.entity.player.Player rawPlayer : mc.level.players()) {
            if (!(rawPlayer instanceof AbstractClientPlayer player)) continue;
            ModelData data = ModelData.get((Player)player);
            data.resourceLoaded = false;
            data.resourceInit = false;
            data.webapiInit = false;
            data.webapiActive = false;
            if (data.resourceLocation == null) continue;
            mc.getTextureManager().release(data.resourceLocation);
            data.resourceLocation = null;
        }
    }
}
