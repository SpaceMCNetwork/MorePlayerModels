/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import noppes.mpm.client.model.animation.AnimationBase;

public class AniWaving
implements AnimationBase {
    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
        float f = Mth.sin((float)((float)entity.tickCount * 0.27f));
        float f2 = Mth.sin((float)((float)(entity.tickCount + 1) * 0.27f));
        f += (f2 - f) * Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        model.rightArm.xRot = -0.1f;
        model.rightArm.yRot = 0.0f;
        model.rightArm.zRot = (float)(2.141592653589793 - (double)(f * 0.5f));
    }
}

