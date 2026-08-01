/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 */
package noppes.mpm;

import java.util.Random;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import noppes.mpm.client.parts.MpmPartData;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketEyeBlink;
import noppes.mpm.shared.util.ColorUtil;
import noppes.mpm.shared.util.NopVector2i;
import noppes.mpm.shared.util.NopVector3f;

public class ModelEyeData
extends MpmPartData {
    public static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath("moreplayermodels", "eyes");
    public static final ResourceLocation RESOURCE_LEFT = ResourceLocation.fromNamespaceAndPath("moreplayermodels", "eyes_left");
    public static final ResourceLocation RESOURCE_RIGHT = ResourceLocation.fromNamespaceAndPath("moreplayermodels", "eyes_right");
    private final Random r = new Random();
    public boolean glint = true;
    public NopVector3f browThickness = new NopVector3f(1.0f, 0.4f, 1.0f);
    public NopVector2i eyePos = NopVector2i.ZERO;
    public boolean mirror = false;
    public int eyeSize = 0;
    public int skinType = 0;
    public boolean useLidTexture = false;
    public NopVector3f lidColor = ColorUtil.colorToRgb(11830381);
    public NopVector3f browColor = ColorUtil.colorToRgb(5982516);
    public long blinkStart = 0L;
    public boolean disableBlink = false;

    public ModelEyeData() {
        this.color = (new NopVector3f[]{ColorUtil.colorToRgb(8368696), ColorUtil.colorToRgb(16247203), ColorUtil.colorToRgb(0xA0A0FF), ColorUtil.colorToRgb(0xA7A7A7), ColorUtil.colorToRgb(10791096), ColorUtil.colorToRgb(0x4040FF), ColorUtil.colorToRgb(14188339), ColorUtil.colorToRgb(11685080), ColorUtil.colorToRgb(6724056), ColorUtil.colorToRgb(0xE5E533), ColorUtil.colorToRgb(55610), ColorUtil.colorToRgb(8375321), ColorUtil.colorToRgb(15892389), ColorUtil.colorToRgb(0x999999), ColorUtil.colorToRgb(5013401), ColorUtil.colorToRgb(8339378), ColorUtil.colorToRgb(3361970), ColorUtil.colorToRgb(6704179), ColorUtil.colorToRgb(6717235), ColorUtil.colorToRgb(0x993333), ColorUtil.colorToRgb(16445005), ColorUtil.colorToRgb(6085589), ColorUtil.colorToRgb(4882687)})[this.r.nextInt(23)];
    }

    @Override
    public CompoundTag getNbt() {
        CompoundTag compound = super.getNbt();
        compound.putBoolean("Glint", this.glint);
        compound.putBoolean("UseLidTexture", this.useLidTexture);
        compound.putBoolean("Mirror", this.mirror);
        compound.putBoolean("DisableBlink", this.disableBlink);
        compound.putInt("SkinType", this.skinType);
        compound.putInt("EyeSize", this.eyeSize);
        compound.putInt("SkinColor", ColorUtil.rgbToColor(this.lidColor));
        compound.putInt("BrowColor", ColorUtil.rgbToColor(this.browColor));
        compound.putInt("PositionX", this.eyePos.x);
        compound.putInt("PositionY", this.eyePos.y);
        compound.putInt("BrowThickness", (int)(this.browThickness.y * 10.0f));
        return compound;
    }

    @Override
    public void setNbt(CompoundTag compound) {
        super.setNbt(compound);
        this.glint = compound.getBoolean("Glint");
        this.useLidTexture = compound.getBoolean("UseLidTexture");
        this.mirror = compound.getBoolean("Mirror");
        this.disableBlink = compound.getBoolean("DisableBlink");
        this.skinType = clamp(compound.getInt("SkinType"), 0, 2);
        this.eyeSize = clamp(compound.getInt("EyeSize"), 0, 1);
        this.lidColor = ColorUtil.colorToRgb(compound.getInt("SkinColor"));
        this.browColor = ColorUtil.colorToRgb(compound.getInt("BrowColor"));
        this.eyePos = new NopVector2i(clamp(compound.getInt("PositionX"), -1, 1), clamp(compound.getInt("PositionY"), -2, 2));
        this.browThickness = new NopVector3f(1.0f, (float)clamp(compound.getInt("BrowThickness"), 0, 8) / 10.0f, 1.0f);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public void update(Player player) {
        if (!player.isAlive() || this.disableBlink) {
            return;
        }
        if (this.blinkStart < 0L) {
            ++this.blinkStart;
        } else if (this.blinkStart == 0L) {
            if (this.r.nextInt(140) == 1) {
                this.blinkStart = System.currentTimeMillis();
                if (!player.isLocalPlayer()) {
                    Packets.sendNearby((Entity)player, new PacketEyeBlink(player.getUUID()));
                }
            }
        } else if (System.currentTimeMillis() - this.blinkStart > 300L) {
            this.blinkStart = -20L;
        }
    }

    @Override
    public ResourceLocation getUrlTexture() {
        ResourceLocation url = super.getUrlTexture();
        if (url == null) {
            return MissingTextureAtlasSprite.getLocation();
        }
        return url;
    }
}
