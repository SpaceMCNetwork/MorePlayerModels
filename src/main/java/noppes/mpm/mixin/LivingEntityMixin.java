/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.mpm.mixin;

import net.minecraft.world.entity.LivingEntity;
import noppes.mpm.mixin.EntityMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={LivingEntity.class})
public interface LivingEntityMixin
extends EntityMixin {
    @Accessor(value="dead")
    public boolean isDead();

    @Accessor(value="jumping")
    public boolean isJumping();

    @Accessor(value="animStep")
    public float getAnimStep();

    @Accessor(value="animStep")
    public void setAnimStep(float var1);

    @Accessor(value="animStepO")
    public float getAnimStepO();

    @Accessor(value="animStepO")
    public void setAnimStepO(float var1);
}

