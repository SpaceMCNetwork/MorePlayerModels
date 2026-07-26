/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package noppes.mpm.client.model.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import noppes.mpm.client.model.animation.AnimationBase;

public class AniHug
implements AnimationBase {
    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
        float f6 = Mth.sin((float)(model.attackTime * 3.141593f));
        float f7 = Mth.sin((float)((1.0f - (1.0f - model.attackTime) * (1.0f - model.attackTime)) * 3.141593f));
        model.rightArm.zRot = 0.0f;
        model.leftArm.zRot = 0.0f;
        model.rightArm.yRot = -(0.1f - f6 * 0.6f);
        model.leftArm.yRot = 0.1f;
        model.rightArm.xRot = -1.570796f;
        model.leftArm.xRot = -1.570796f;
        model.rightArm.xRot -= f6 * 1.2f - f7 * 0.4f;
        model.rightArm.zRot += Mth.cos((float)(ageInTicks * 0.09f)) * 0.05f + 0.05f;
        model.leftArm.zRot -= Mth.cos((float)(ageInTicks * 0.09f)) * 0.05f + 0.05f;
        model.rightArm.xRot += Mth.sin((float)(ageInTicks * 0.067f)) * 0.05f;
        model.leftArm.xRot -= Mth.sin((float)(ageInTicks * 0.067f)) * 0.05f;
    }

    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
    }
}

