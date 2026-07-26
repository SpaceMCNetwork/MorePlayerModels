/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleRenderType
 */
package noppes.mpm.client.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;

public class EntityRainbowFX
extends Particle {
    private float quadSize;
    public static float[][] colorTable = new float[][]{{1.0f, 0.0f, 0.0f}, {1.0f, 0.5f, 0.0f}, {1.0f, 1.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 4375.0f, 0.0f, 1.0f}, {0.5625f, 0.0f, 1.0f}};
    float reddustParticleScale;

    public EntityRainbowFX(ClientLevel world, double d, double d1, double d2, double f, double f1, double f2) {
        this(world, d, d1, d2, 1.0f, f, f1, f2);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.2f + 0.5f);
    }

    public EntityRainbowFX(ClientLevel world, double d, double d1, double d2, float f, double f1, double f2, double f3) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.xd *= (double)0.1f;
        this.yd *= (double)0.1f;
        this.zd *= (double)0.1f;
        if (f1 == 0.0) {
            f1 = 1.0;
        }
        int i = world.random.nextInt(colorTable.length);
        this.rCol = colorTable[i][0];
        this.gCol = colorTable[i][1];
        this.bCol = colorTable[i][2];
        this.quadSize *= 0.75f;
        this.quadSize *= f;
        this.reddustParticleScale = this.quadSize;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime = (int)((float)this.lifetime * f);
    }

    public void render(VertexConsumer renderer, Camera info, float partialTicks) {
        float f6 = ((float)this.age + partialTicks) / (float)this.lifetime * 32.0f;
        if (f6 < 0.0f) {
            f6 = 0.0f;
        } else if (f6 > 1.0f) {
            f6 = 1.0f;
        }
        this.quadSize = this.reddustParticleScale * f6;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= (double)0.96f;
        this.yd *= (double)0.96f;
        this.zd *= (double)0.96f;
        if (this.onGround) {
            this.xd *= (double)0.7f;
            this.zd *= (double)0.7f;
        }
    }
}

