/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PathPackResources
 *  net.minecraft.server.packs.repository.PackRepository
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.mpm.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.PackRepository;
import noppes.mpm.MorePlayerModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PackRepository.class})
public class PackRepositoryMixin {
    @Inject(at={@At(value="TAIL")}, method={"openAllSelected"}, cancellable=true)
    private void reload(CallbackInfoReturnable<List<PackResources>> ci) {
        List<PackResources> resources = new ArrayList<>(ci.getReturnValue());
        if (MorePlayerModels.dir != null && MorePlayerModels.dir.isDirectory()) {
            PackLocationInfo location = new PackLocationInfo(
                MorePlayerModels.MODID + "_user",
                Component.literal("More Player Models user assets"),
                PackSource.BUILT_IN,
                Optional.empty()
            );
            resources.add(new PathPackResources(location, MorePlayerModels.dir.toPath()));
        }
        ci.setReturnValue(resources);
    }
}
