/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 */
package noppes.mpm.client.parts;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import noppes.mpm.client.parts.IMpmPartDataClient;
import noppes.mpm.client.parts.MpmPart;
import noppes.mpm.client.parts.MpmPartReader;
import noppes.mpm.shared.client.ImageDownloadAlt;
import noppes.mpm.shared.client.ResourceDownloader;
import noppes.mpm.shared.util.NopVector3f;
import noppes.mpm.util.NoppesStringUtils;

public class MpmPartData {
    private static final int MAX_URL_LENGTH = 2048;
    public static final NopVector3f WHITE = new NopVector3f(1.0f, 1.0f, 1.0f);
    public ResourceLocation partId;
    public boolean usePlayerSkin = false;
    public NopVector3f color = WHITE;
    public ResourceLocation texture = null;
    private ResourceLocation textureUrl = null;
    public String url = "";
    public IMpmPartDataClient clientData;

    public MpmPart getPart() {
        return MpmPartReader.PARTS.get(this.partId);
    }

    public ResourceLocation getTexture() {
        ResourceLocation urlTexture = this.getUrlTexture();
        if (urlTexture != null) {
            return urlTexture;
        }
        if (this.texture != null) {
            return this.texture;
        }
        MpmPart part = this.getPart();
        if (part != null && part.texture != null) {
            return this.getPart().texture;
        }
        return MissingTextureAtlasSprite.getLocation();
    }

    public ResourceLocation getUrlTexture() {
        if (this.textureUrl != null) {
            return this.textureUrl;
        }
        if (!this.url.isEmpty()) {
            try {
                ResourceLocation resource = ResourceDownloader.getUrlResourceLocation(this.url, false);
                File file = ResourceDownloader.getUrlFile(this.url, false);
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                AbstractTexture object = texturemanager.getTexture(resource, null);
                if (object == null) {
                    this.textureUrl = this.getDefaultTexture();
                    ResourceDownloader.load(new ImageDownloadAlt(file, this.url, resource, this.getDefaultTexture(), false, () -> {
                        this.textureUrl = resource;
                    }));
                } else {
                    this.textureUrl = resource;
                }
            } catch (RuntimeException ignored) {
                this.textureUrl = this.getDefaultTexture();
            }
        }
        return this.textureUrl;
    }

    public void setTexture(String s) {
        this.texture = s == null || s.isEmpty() ? null : ResourceLocation.tryParse(s);
    }

    public void setUrl(String url) {
        url = url == null ? "" : url;
        if (url.length() > MAX_URL_LENGTH) {
            url = url.substring(0, MAX_URL_LENGTH);
        }
        if (NoppesStringUtils.areEqual(this.url, url)) {
            return;
        }
        this.url = url;
        this.textureUrl = null;
    }

    public ResourceLocation getDefaultTexture() {
        if (this.texture != null) {
            return this.texture;
        }
        MpmPart part = this.getPart();
        return part != null && part.texture != null ? part.texture : MissingTextureAtlasSprite.getLocation();
    }

    public int getColor() {
        int r = (int)(this.color.x * 255.0f) << 16;
        int g = (int)(this.color.y * 255.0f) << 8;
        int b = (int)(this.color.z * 255.0f);
        return r + g + b;
    }

    public void setColor(int color) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        this.color = new NopVector3f(r, g, b);
    }

    public CompoundTag getNbt() {
        CompoundTag item = new CompoundTag();
        item.putString("Id", this.partId == null ? "" : this.partId.toString());
        item.putBoolean("UsePlayerSkin", this.usePlayerSkin);
        item.putString("Url", this.url);
        item.putString("Texture", this.texture == null ? "" : this.texture.toString());
        item.putFloat("ColorR", this.color.x);
        item.putFloat("ColorG", this.color.y);
        item.putFloat("ColorB", this.color.z);
        return item;
    }

    public void setNbt(CompoundTag compound) {
        this.partId = ResourceLocation.tryParse(compound.getString("Id"));
        this.usePlayerSkin = compound.getBoolean("UsePlayerSkin");
        this.setUrl(compound.getString("Url"));
        this.setTexture(compound.getString("Texture"));
        this.color = new NopVector3f(
                colorComponent(compound.getFloat("ColorR")),
                colorComponent(compound.getFloat("ColorG")),
                colorComponent(compound.getFloat("ColorB"))
        );
    }

    private static float colorComponent(float value) {
        if (!Float.isFinite(value)) {
            return 1.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
