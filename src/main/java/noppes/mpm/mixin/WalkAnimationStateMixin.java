package noppes.mpm.mixin;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Accesses the complete 1.21 gait state used by living-entity renderers. */
@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateMixin {
    @Accessor("speedOld")
    float mpm$getSpeedOld();

    @Accessor("speedOld")
    void mpm$setSpeedOld(float value);

    @Accessor("position")
    float mpm$getPosition();

    @Accessor("position")
    void mpm$setPosition(float value);
}
