/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.event.TickEvent$Phase
 *  net.neoforged.neoforge.event.TickEvent$PlayerTickEvent
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.LogicalSide
 */
package noppes.mpm;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import noppes.mpm.ModelData;
import noppes.mpm.ModelEyeData;
import noppes.mpm.client.parts.MpmPartData;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketBackItemUpdate;

public class ServerTickHandler {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        ServerPlayer player = (ServerPlayer)event.getEntity();
        ModelData data = ModelData.get((Player)player);
        ItemStack item = (ItemStack)player.getInventory().items.get(0);
        if (data.backItem != item) {
            Packets.send(player, new PacketBackItemUpdate(player.getUUID(), item));
            data.backItem = item;
        }
        for (MpmPartData pd : data.mpmParts) {
            if (!(pd instanceof ModelEyeData)) continue;
            ((ModelEyeData)pd).update((Player)player);
        }
        ServerTickHandler.checkMovementAnimation((Player)player, data);
        if (data.animation != EnumAnimation.NONE) {
            ServerTickHandler.checkAnimation((Player)player, data);
        }
        data.prevPosX = player.getX();
        data.prevPosY = player.getY();
        data.prevPosZ = player.getZ();
    }

    public static void checkMovementAnimation(Player player, ModelData data) {
        boolean isFlying;
        double motionX = data.prevPosX - player.getX();
        double motionY = data.prevPosY - player.getY();
        double motionZ = data.prevPosZ - player.getZ();
        double speed = motionX * motionX + motionZ * motionZ;
        boolean isMoving = speed > 0.006 && !player.isShiftKeyDown();
        boolean isJumping = motionY * motionY > 0.08;
        boolean bl = isFlying = player.level().isEmptyBlock(player.blockPosition()) && player.level().isEmptyBlock(player.blockPosition().below());
        if (player.isVisuallySwimming()) {
            data.setMoveAnimation(EnumAnimation.SWIM);
            return;
        }
        if (isFlying) {
            data.setMoveAnimation(isMoving ? EnumAnimation.FLY : EnumAnimation.FLY_IDLE);
            return;
        }
        if (speed < 0.001 && !isJumping && (data.moveAnimation == EnumAnimation.DEATH || data.moveAnimation == EnumAnimation.SLEEP)) {
            return;
        }
        if (data.moveAnimation == EnumAnimation.CRAWL || data.moveAnimation == EnumAnimation.SIT) {
            if (isMoving || isJumping) {
                data.setMoveAnimation(EnumAnimation.WALK);
            }
            return;
        }
        if (data.moveAnimation == EnumAnimation.CROUCH && !isMoving) {
            return;
        }
        data.setMoveAnimation(isMoving ? EnumAnimation.WALK : EnumAnimation.IDLE);
    }

    public static void checkAnimation(Player player, ModelData data) {
        boolean isJumping;
        if (data.prevPosY <= 0.0 || player.tickCount < 40) {
            return;
        }
        double motionX = data.prevPosX - player.getX();
        double motionY = data.prevPosY - player.getY();
        double motionZ = data.prevPosZ - player.getZ();
        double speed = motionX * motionX + motionZ * motionZ;
        boolean bl = isJumping = motionY * motionY > 0.08;
        if (data.animationTime > 0) {
            --data.animationTime;
        }
        if (player.isSleeping() || player.isPassenger() || data.animationTime == 0 || data.animation == EnumAnimation.BOW && player.isShiftKeyDown()) {
            data.setAnimation(EnumAnimation.NONE);
        }
        if (!isJumping && player.isShiftKeyDown() && (data.animation == EnumAnimation.HUG || data.animation == EnumAnimation.DANCE)) {
            return;
        }
        if (speed > 0.01 || isJumping || player.isSleeping() || data.animation == EnumAnimation.SLEEP && speed > 0.001) {
            data.setAnimation(EnumAnimation.NONE);
        }
    }
}
