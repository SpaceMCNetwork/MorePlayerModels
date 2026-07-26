/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.particle.TextureSheetParticle
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package noppes.mpm.client.fx;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import noppes.mpm.ModelPartData;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class EntityEnderFX
extends TextureSheetParticle {
    public static SpriteSet portalSprite;
    private int particleNumber;
    private AbstractClientPlayer player;
    private final ResourceLocation location;
    private boolean move = true;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private float startZ = 0.0f;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    public EntityEnderFX(AbstractClientPlayer player, double x, double y, double z, double par8, double par10, double par12, ModelPartData data) {
        super(player.clientLevel, x, y, z);
        this.pickSprite(portalSprite);
        this.xd = par8;
        this.yd = par10;
        this.zd = par12;
        this.player = player;
        this.particleNumber = player.getRandom().nextInt(2);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.2f + 0.5f);
        this.rCol = (float)(data.color >> 16 & 0xFF) / 255.0f;
        this.gCol = (float)(data.color >> 8 & 0xFF) / 255.0f;
        this.bCol = (float)(data.color & 0xFF) / 255.0f;
        this.x = this.portalPosX = x;
        this.y = this.portalPosY = y;
        this.z = this.portalPosZ = z;
        this.location = data.playerTexture ? player.getSkin().texture() : data.getResource();
        this.lifetime = (int)(Math.random() * 10.0) + 40;
    }

    public float getQuadSize(float p_217561_1_) {
        float scale = ((float)this.age + p_217561_1_) / (float)this.lifetime;
        scale = 1.0f - scale;
        scale *= scale;
        scale = 1.0f - scale;
        return this.quadSize * scale;
    }

    public int getLightColor(float p_189214_1_) {
        int lvt_2_1_ = super.getLightColor(p_189214_1_);
        float lvt_3_1_ = (float)this.age / (float)this.lifetime;
        lvt_3_1_ *= lvt_3_1_;
        lvt_3_1_ *= lvt_3_1_;
        int lvt_4_1_ = lvt_2_1_ & 0xFF;
        int lvt_5_1_ = lvt_2_1_ >> 16 & 0xFF;
        if ((lvt_5_1_ += (int)(lvt_3_1_ * 15.0f * 16.0f)) > 240) {
            lvt_5_1_ = 240;
        }
        return lvt_4_1_ | lvt_5_1_ << 16;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float lvt_1_1_;
            float lvt_2_1_ = lvt_1_1_ = (float)this.age / (float)this.lifetime;
            lvt_1_1_ = -lvt_1_1_ + lvt_1_1_ * lvt_1_1_ * 2.0f;
            lvt_1_1_ = 1.0f - lvt_1_1_;
            this.x = this.portalPosX + this.xd * (double)lvt_1_1_;
            this.y = this.portalPosY + this.yd * (double)lvt_1_1_ + (double)(1.0f - lvt_2_1_);
            this.z = this.portalPosZ + this.zd * (double)lvt_1_1_;
        }
    }

    public void render(VertexConsumer renderer, Camera info, float partialTicks) {
        Quaternionf quaternion;
        if (this.move) {
            this.startX = (float)(this.player.xo + (this.player.getX() - this.player.xo) * (double)partialTicks);
            this.startY = (float)(this.player.yo + (this.player.getY() - this.player.yo) * (double)partialTicks);
            this.startZ = (float)(this.player.zo + (this.player.getZ() - this.player.zo) * (double)partialTicks);
        }
        Vec3 vector3d = info.getPosition();
        float f = (float)(Mth.lerp((double)partialTicks, (double)this.xo, (double)this.x) - vector3d.x());
        float f1 = (float)(Mth.lerp((double)partialTicks, (double)this.yo, (double)this.y) - vector3d.y());
        float f2 = (float)(Mth.lerp((double)partialTicks, (double)this.zo, (double)this.z) - vector3d.z());
        if (this.roll == 0.0f) {
            quaternion = info.rotation();
        } else {
            quaternion = new Quaternionf((Quaternionfc)info.rotation());
            float f3 = Mth.lerp((float)partialTicks, (float)this.oRoll, (float)this.roll);
            quaternion.mul((Quaternionfc)Axis.ZP.rotation(f3));
        }
        Vector3f vector3f1 = quaternion.transform(new Vector3f(-1.0f, -1.0f, 0.0f));
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float f4 = this.getQuadSize(partialTicks);
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = quaternion.transform(avector3f[i]);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0.875f;
        float f8 = f7 + 0.125f;
        float f5 = 0.75f - (float)this.particleNumber * 0.25f;
        float f6 = f5 + 0.25f;
        int j = this.getLightColor(partialTicks);
        renderer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).setUv(f8, f6).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        renderer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).setUv(f8, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        renderer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).setUv(f7, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        renderer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).setUv(f7, f6).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
    }

    public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
        this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
        this.setLocationFromBoundingbox();
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
