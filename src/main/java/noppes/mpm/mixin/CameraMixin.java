/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.mpm.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import noppes.mpm.client.ClientEventHandler;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Camera.class})
public class CameraMixin {
    @Inject(at={@At(value="HEAD")}, method={"getMaxZoom"}, cancellable=true)
    private void getMaxZoom(float zoom, CallbackInfoReturnable<Float> cir) {
        if (ClientEventHandler.camera.enabled) {
            Camera info = (Camera)(Object)this;
            Vec3 position = info.getPosition();
            Vector3f forwards = info.getLookVector();
            ClientLevel level = Minecraft.getInstance().level;
            zoom = ClientEventHandler.camera.cameraDistance;
            for (int i = 0; i < 8; ++i) {
                double d0;
                Vec3 vector3d1;
                BlockHitResult raytraceresult;
                float f = (i & 1) * 2 - 1;
                float f1 = (i >> 1 & 1) * 2 - 1;
                float f2 = (i >> 2 & 1) * 2 - 1;
                Vec3 vector3d = position.add((double)(f *= 0.1f), (double)(f1 *= 0.1f), (double)(f2 *= 0.1f));
                if ((raytraceresult = level.clip(new ClipContext(vector3d, vector3d1 = new Vec3(position.x - (double)forwards.x() * zoom + (double)f + (double)f2, position.y - (double)forwards.y() * zoom + (double)f1, position.z - (double)forwards.z() * zoom + (double)f2), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, info.getEntity()))).getType() == HitResult.Type.MISS || !((d0 = raytraceresult.getLocation().distanceTo(position)) < zoom)) continue;
                zoom = (float)d0;
            }
            cir.setReturnValue((float)zoom);
            cir.cancel();
        }
    }
}
