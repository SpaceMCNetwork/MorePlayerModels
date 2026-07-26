/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.registries.ForgeRegistries
 */
package noppes.mpm.util;

import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import noppes.mpm.mixin.LivingEntityMixin;
import noppes.mpm.mixin.WalkAnimationStateMixin;

public class MPMEntityUtil {
    private static final float CUSTOM_ENTITY_WALK_AMPLITUDE = 0.5f;

    public static void copy(LivingEntity copied, LivingEntity entity) {
        LivingEntityMixin copiedm = (LivingEntityMixin)copied;
        LivingEntityMixin entitym = (LivingEntityMixin)entity;
        entity.deathTime = copied.deathTime;
        entity.walkDist = copied.walkDist;
        entity.walkDistO = copied.walkDist;
        entity.moveDist = copied.moveDist;
        entity.zza = copied.zza;
        entity.xxa = copied.xxa;
        entity.setOnGround(copied.onGround());
        entity.fallDistance = copied.fallDistance;
        entity.setJumping(copiedm.isJumping());
        entity.xo = copied.xo;
        entity.yo = copied.yo;
        entity.zo = copied.zo;
        entity.setPos(copied.getX(), copied.getY(), copied.getZ());
        entity.xOld = copied.xOld;
        entity.yOld = copied.yOld;
        entity.zOld = copied.zOld;
        entity.setDeltaMovement(copied.getDeltaMovement());
        entity.setXRot(copied.getXRot());
        entity.setYRot(copied.getYRot());
        entity.xRotO = copied.xRotO;
        entity.yRotO = copied.yRotO;
        entity.yHeadRot = copied.yHeadRot;
        entity.yHeadRotO = copied.yHeadRotO;
        entity.yBodyRot = copied.yBodyRot;
        entity.yBodyRotO = copied.yBodyRotO;
        WalkAnimationStateMixin sourceWalk = (WalkAnimationStateMixin)(Object)copied.walkAnimation;
        WalkAnimationStateMixin targetWalk = (WalkAnimationStateMixin)(Object)entity.walkAnimation;
        entity.walkAnimation.setSpeed(copied.walkAnimation.speed() * CUSTOM_ENTITY_WALK_AMPLITUDE);
        targetWalk.mpm$setSpeedOld(sourceWalk.mpm$getSpeedOld() * CUSTOM_ENTITY_WALK_AMPLITUDE);
        targetWalk.mpm$setPosition(sourceWalk.mpm$getPosition());
        entitym.setAnimStep(copiedm.getAnimStep());
        entitym.setAnimStepO(copiedm.getAnimStepO());
        entity.attackAnim = copied.attackAnim;
        entity.oAttackAnim = copied.oAttackAnim;
        entity.tickCount = copied.tickCount;
        entity.setHealth(Math.min(copied.getHealth(), entity.getMaxHealth()));
        entity.getPersistentData().merge(copied.getPersistentData());
        if (entity.getVehicle() != copied.getVehicle()) {
            entitym.setVehicle(copiedm.getVehicle());
        }
        if (entity instanceof Player && copied instanceof Player) {
            Player ePlayer = (Player)entity;
            Player cPlayer = (Player)copied;
            ePlayer.bob = cPlayer.bob;
            ePlayer.oBob = cPlayer.oBob;
            ePlayer.xCloakO = cPlayer.xCloakO;
            ePlayer.yCloakO = cPlayer.yCloakO;
            ePlayer.zCloakO = cPlayer.zCloakO;
            ePlayer.xCloak = cPlayer.xCloak;
            ePlayer.yCloak = cPlayer.yCloak;
            ePlayer.zCloak = cPlayer.zCloak;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            entity.setItemSlot(slot, copied.getItemBySlot(slot));
        }
        if (entity instanceof EnderDragon) {
            entity.setXRot(entity.getXRot() + 180.0f);
        }
    }

    public static HashMap<String, ResourceLocation> getAllEntities(Level level) {
        HashMap<String, ResourceLocation> data = new HashMap<String, ResourceLocation>();
        for (EntityType ent : BuiltInRegistries.ENTITY_TYPE) {
            try {
                Entity e = ent.create(level);
                if (e == null) continue;
                if (LivingEntity.class.isAssignableFrom(e.getClass())) {
                    data.put(ent.getDescriptionId(), BuiltInRegistries.ENTITY_TYPE.getKey(ent));
                }
                e.remove(Entity.RemovalReason.DISCARDED);
            }
            catch (Exception exception) {}
        }
        return data;
    }
}
