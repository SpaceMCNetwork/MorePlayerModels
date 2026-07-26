package noppes.mpm;

import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** NeoForge registries owned by More Player Models. */
public final class MPMRegistries {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MorePlayerModels.MODID);
    public static final Supplier<AttachmentType<ModelData>> MODEL_DATA = ATTACHMENTS.register(
            "modeldata", () -> AttachmentType.serializable(ModelData::new).copyOnDeath().build());

    private MPMRegistries() {
    }
}
