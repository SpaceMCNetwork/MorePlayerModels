/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.world.entity.Entity
 */
package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import noppes.mpm.client.model.animation.AnimationBase;

public class AniBow
implements AnimationBase {
    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
        float ticks2;
        float ticks = (float)(entity.tickCount - animationStart) / 10.0f;
        if (ticks > 1.0f) {
            ticks = 1.0f;
        }
        if ((ticks2 = (float)(entity.tickCount + 1 - animationStart) / 10.0f) > 1.0f) {
            ticks2 = 1.0f;
        }
        ticks += (ticks2 - ticks) * Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        model.body.xRot = ticks;
        model.head.xRot = ticks;
        model.leftArm.xRot = ticks;
        model.rightArm.xRot = ticks;
        model.body.z = -ticks * 10.0f;
        model.body.y = ticks * 6.0f;
        model.head.z = -ticks * 10.0f;
        model.head.y = ticks * 6.0f;
        model.leftArm.z = -ticks * 10.0f;
        model.leftArm.y += ticks * 6.0f;
        model.rightArm.z = -ticks * 10.0f;
        model.rightArm.y += ticks * 6.0f;
    }
}

