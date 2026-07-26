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

public class AniCrawling
implements AnimationBase {
    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
        model.head.zRot = -netHeadYaw / 57.295776f;
        model.head.yRot = 0.0f;
        model.hat.xRot = model.head.xRot = -0.95993114f;
        model.hat.yRot = model.head.yRot;
        model.hat.zRot = model.head.zRot;
        if ((double)limbSwingAmount > 0.25) {
            limbSwingAmount = 0.25f;
        }
        float movement = Mth.cos((float)(limbSwing * 0.8f + (float)Math.PI)) * limbSwingAmount;
        model.leftArm.xRot = (float)Math.PI - movement * 0.25f;
        model.leftArm.yRot = movement * -0.46f;
        model.leftArm.zRot = movement * -0.2f;
        model.leftArm.y = 2.0f - movement * 9.0f;
        model.rightArm.xRot = (float)Math.PI + movement * 0.25f;
        model.rightArm.yRot = movement * -0.4f;
        model.rightArm.zRot = movement * -0.2f;
        model.rightArm.y = 2.0f + movement * 9.0f;
        model.body.yRot = movement * 0.1f;
        model.body.xRot = 0.0f;
        model.body.zRot = movement * 0.1f;
        model.leftLeg.xRot = movement * 0.1f;
        model.leftLeg.yRot = movement * 0.1f;
        model.leftLeg.zRot = -0.122173056f - movement * 0.25f;
        model.leftLeg.y = 10.4f + movement * 9.0f;
        model.leftLeg.z = movement * 0.6f;
        model.rightLeg.xRot = movement * -0.1f;
        model.rightLeg.yRot = movement * 0.1f;
        model.rightLeg.zRot = 0.122173056f - movement * 0.25f;
        model.rightLeg.y = 10.4f - movement * 9.0f;
        model.rightLeg.z = movement * -0.6f;
    }
}

