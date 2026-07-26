/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.mpm.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Entity.class})
public interface EntityMixin {
    @Accessor(value="vehicle")
    public Entity getVehicle();

    @Accessor(value="vehicle")
    public void setVehicle(Entity var1);

    @Accessor(value="dimensions")
    public EntityDimensions getDimensions();

    @Accessor(value="dimensions")
    public void setDimensions(EntityDimensions var1);

    @Accessor(value="eyeHeight")
    public void setEyeHeight(float var1);
}

