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

public class AniDancing
implements AnimationBase {
    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
        float dancing = (float)entity.tickCount / 4.0f;
        float dancing2 = (float)(entity.tickCount + 1) / 4.0f;
        dancing += (dancing2 - dancing) * Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float x = (float)Math.sin(dancing);
        float y = (float)Math.abs(Math.cos(dancing));
        model.hat.x = model.head.x = x * 0.75f;
        model.hat.y = model.head.y = y * 1.25f - 0.02f + (float)(entity.isCrouching() ? 4 : 0);
        model.hat.z = model.head.z = -y * 0.75f;
        model.leftArm.x += x * 0.25f;
        model.leftArm.y += y * 1.25f;
        model.rightArm.x += x * 0.25f;
        model.rightArm.y += y * 1.25f;
        model.body.x = x * 0.25f;
    }

    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel model, int animationStart) {
    }
}

