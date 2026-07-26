/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.SimpleTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package noppes.mpm.shared.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.util.NoppesStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(value=Dist.CLIENT)
public class ImageDownloadAlt
extends SimpleTexture {
    private static final Logger logger = LogManager.getLogger();
    public final File cacheFile;
    private final String imageUrl;
    private boolean fix64;
    private Runnable r;
    public final ResourceLocation location;
    public boolean uploaded = false;

    public ImageDownloadAlt(File file, String url, ResourceLocation location, ResourceLocation defaultLocation, boolean fix64, Runnable r) {
        super(defaultLocation);
        this.location = location;
        this.cacheFile = file;
        this.imageUrl = url;
        this.fix64 = fix64;
        this.r = r;
    }

    public void setImage(NativeImage image) {
        Minecraft.getInstance().execute(() -> {
            this.uploaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.upload(image));
            } else {
                this.upload(image);
            }
            this.r.run();
        });
    }

    private void upload(NativeImage imageIn) {
        TextureUtil.prepareImage((int)this.getId(), (int)imageIn.getWidth(), (int)imageIn.getHeight());
        imageIn.upload(0, 0, 0, true);
    }

    public void load(ResourceManager resourceManager) throws IOException {
        if (this.cacheFile != null && this.cacheFile.isFile()) {
            logger.debug("Loading http texture from local cache ({})", new Object[]{this.cacheFile});
            NativeImage image = null;
            try {
                image = NativeImage.read((InputStream)new FileInputStream(this.cacheFile));
                this.setImage(this.parseUserSkin(image));
                return;
            }
            catch (IOException ioexception) {
                super.load(resourceManager);
                logger.error("Couldn't load skin " + this.cacheFile, (Throwable)ioexception);
            }
        }
        if (!this.uploaded) {
            try {
                this.uploaded = true;
                super.load(resourceManager);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public boolean loadTextureFromServer() {
        if (this.cacheFile != null && this.cacheFile.isFile()) {
            return true;
        }
        return this.load(this.imageUrl, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean load(String url, int redirectCount) {
        HttpURLConnection connection = null;
        logger.debug("Downloading http texture from {} to {}", new Object[]{url, this.cacheFile});
        try {
            connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
            connection.setRequestProperty("Content-Type", "image/png");
            connection.setRequestProperty("Accept", "image/png");
            connection.setRequestProperty("Expect", "100-continue");
            connection.connect();
            String type = connection.getContentType();
            long size = connection.getContentLengthLong();
            int statusCode = connection.getResponseCode();
            if (statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307 || statusCode == 308) {
                String newUrl = connection.getHeaderField("Location");
                if (!NoppesStringUtils.empty(newUrl) && redirectCount < 5) {
                    return this.load(new URL(new URL(url), newUrl).toString(), redirectCount + 1);
                }
                logger.warn("Couldn't download skin: redirect from {} had no usable destination", url);
                return false;
            }
            if (statusCode / 100 != 2 || size > 2000000L && !Minecraft.getInstance().hasSingleplayerServer()) {
                logger.warn("Couldn't download skin {}: HTTP {}, {} bytes", url, statusCode, size);
                return false;
            }
            // Several CDNs use image/png with parameters or octet-stream for
            // a PNG. NativeImage validates the bytes before they are uploaded.
            if (type != null && !type.toLowerCase(Locale.ROOT).startsWith("image/") && !type.equalsIgnoreCase("application/octet-stream")) {
                logger.warn("Couldn't download skin {}: unexpected content type {}", url, type);
                return false;
            }
            FileUtils.copyInputStreamToFile((InputStream)connection.getInputStream(), (File)this.cacheFile);
            return true;
        }
        catch (Exception exception) {
            logger.error("Couldn't download http texture", (Throwable)exception);
            return false;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public NativeImage parseUserSkin(NativeImage image) {
        boolean lvt_2_1_;
        if (image.getHeight() != image.getWidth() && image.getWidth() / 2 != image.getHeight()) {
            throw new IllegalArgumentException("Invalid texture size: " + image.getWidth() + "x" + image.getHeight());
        }
        int scale = image.getWidth() / 64;
        boolean bl = lvt_2_1_ = image.getHeight() != image.getWidth();
        if (lvt_2_1_ && this.fix64) {
            NativeImage nativeImage = new NativeImage(64 * scale, 64 * scale, true);
            nativeImage.copyFrom(image);
            image.close();
            image = nativeImage;
            nativeImage.fillRect(0, 32 * scale, 64 * scale, 32 * scale, 0);
            nativeImage.copyRect(4 * scale, 16 * scale, 16 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(8 * scale, 16 * scale, 16 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(0, 20 * scale, 24 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(4 * scale, 20 * scale, 16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(8 * scale, 20 * scale, 8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(12 * scale, 20 * scale, 16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(44 * scale, 16 * scale, -8 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(48 * scale, 16 * scale, -8 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(40 * scale, 20 * scale, 0, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(44 * scale, 20 * scale, -8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(48 * scale, 20 * scale, -16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(52 * scale, 20 * scale, -8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
        }
        if (!MorePlayerModels.AllowFullyInvisibleSkins) {
            ImageDownloadAlt.setAreaOpaque(image, 0, 0, 32 * scale, 16 * scale);
        }
        if (lvt_2_1_ && this.fix64) {
            ImageDownloadAlt.setAreaTransparent(image, 32 * scale, 0, 64 * scale, 32 * scale);
        }
        return image;
    }

    private static void setAreaTransparent(NativeImage image, int x, int y, int width, int height) {
        for (int i = x; i < width; ++i) {
            for (int j = y; j < height; ++j) {
                int k = image.getPixelRGBA(i, j);
                if ((k >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int l = x; l < width; ++l) {
            for (int i1 = y; i1 < height; ++i1) {
                image.setPixelRGBA(l, i1, image.getPixelRGBA(l, i1) & 0xFFFFFF);
            }
        }
    }

    private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height) {
        for (int i = x; i < width; ++i) {
            for (int j = y; j < height; ++j) {
                image.setPixelRGBA(i, j, image.getPixelRGBA(i, j) | 0xFF000000);
            }
        }
    }
}
