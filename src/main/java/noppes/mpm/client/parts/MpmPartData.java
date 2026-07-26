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
        if (this.getUrlTexture() != null) {
            return this.getUrlTexture();
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
        }
        return this.textureUrl;
    }

    public void setTexture(String s) {
        this.texture = s == null || s.isEmpty() ? null : ResourceLocation.parse(s);
    }

    public void setUrl(String url) {
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
        return this.getPart().texture;
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
        item.putString("Id", this.partId.toString());
        item.putBoolean("UsePlayerSkin", this.usePlayerSkin);
        item.putString("Url", this.url);
        item.putString("Texture", this.texture == null ? "" : this.texture.toString());
        item.putFloat("ColorR", this.color.x);
        item.putFloat("ColorG", this.color.y);
        item.putFloat("ColorB", this.color.z);
        return item;
    }

    public void setNbt(CompoundTag compound) {
        this.partId = ResourceLocation.parse(compound.getString("Id"));
        this.usePlayerSkin = compound.getBoolean("UsePlayerSkin");
        this.setUrl(compound.getString("Url"));
        this.setTexture(compound.getString("Texture"));
        this.color = new NopVector3f(compound.getFloat("ColorR"), compound.getFloat("ColorG"), compound.getFloat("ColorB"));
    }
}

