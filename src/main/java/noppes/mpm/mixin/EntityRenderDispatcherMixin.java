package noppes.mpm.mixin;

import java.util.Map;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import noppes.mpm.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Selects MPM's forced Steve/Alex renderer before vanilla reads PlayerSkin.model(). */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow
    private Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers;

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void mpm$getModelTypeRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T>> callbackInfo) {
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        ModelData data = ModelData.get((Player)player);
        if (data.modelType == 0) {
            return;
        }
        PlayerSkin.Model model = data.modelType == 1 ? PlayerSkin.Model.WIDE : PlayerSkin.Model.SLIM;
        EntityRenderer<? extends Player> renderer = this.playerRenderers.get(model);
        if (renderer != null) {
            callbackInfo.setReturnValue((EntityRenderer<? super T>)renderer);
        }
    }
}
