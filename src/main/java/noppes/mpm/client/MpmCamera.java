/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import noppes.mpm.client.MpmKeys;
import noppes.mpm.mixin.MouseHelperMixin;

public class MpmCamera {
    public boolean enabled = false;
    public float cameraYaw = 0.0f;
    public float cameraPitch = 0.0f;
    public float playerYaw = 0.0f;
    public float playerPitch = 0.0f;
    public float cameraDistance = 4.0f;
    private double mouseX;
    private double mouseY;

    public void update(boolean start) {
        Minecraft mc = Minecraft.getInstance();
        Entity view = mc.getCameraEntity();
        if (!mc.gameRenderer.getMainCamera().isDetached()) {
            if (this.enabled) {
                this.reset();
            }
            return;
        }
        if (!this.enabled || view == null) {
            return;
        }
        this.updateCamera();
        if (start) {
            view.xRotO = this.cameraPitch;
            view.setXRot(view.xRotO);
            view.yRotO = this.cameraYaw;
            view.setYRot(view.yRotO);
        } else {
            view.setXRot(mc.player.getXRot() - this.playerPitch);
            view.xRotO = mc.player.xRotO - this.playerPitch;
            view.setYRot(this.playerYaw);
            view.yRotO = this.playerYaw;
        }
    }

    private void updateCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isWindowActive()) {
            return;
        }
        double f = (Double)mc.options.sensitivity().get() * 0.6 + 0.2;
        double f1 = f * f * f * 8.0;
        double dx = (mc.mouseHandler.xpos() - this.mouseX) * f1 * 0.15;
        double dy = (mc.mouseHandler.ypos() - this.mouseY) * f1 * 0.15;
        if (MpmKeys.Camera.isDown()) {
            this.cameraYaw = (float)((double)this.cameraYaw + dx);
            this.cameraPitch = (float)((double)this.cameraPitch + dy);
            this.cameraPitch = Mth.clamp((float)this.cameraPitch, (float)-90.0f, (float)90.0f);
        } else {
            this.playerYaw = (float)((double)this.playerYaw + dx);
            this.playerPitch = (float)((double)this.playerPitch + dy);
            this.playerPitch = Mth.clamp((float)this.playerPitch, (float)-90.0f, (float)90.0f);
        }
        this.mouseX = mc.mouseHandler.xpos();
        this.mouseY = mc.mouseHandler.ypos();
    }

    public void reset() {
        this.enabled = false;
        this.cameraYaw = 0.0f;
        this.cameraPitch = 0.0f;
        this.playerYaw = 0.0f;
        this.playerPitch = 0.0f;
        this.cameraDistance = 4.0f;
    }

    public void enable() {
        Minecraft mc = Minecraft.getInstance();
        if (!this.enabled) {
            this.cameraPitch = this.playerPitch = mc.player.getXRot();
            this.cameraYaw = this.playerYaw = mc.player.getYRot();
        }
        this.enabled = true;
        this.mouseX = ((MouseHelperMixin)mc.mouseHandler).getX();
        this.mouseY = ((MouseHelperMixin)mc.mouseHandler).getY();
    }
}

