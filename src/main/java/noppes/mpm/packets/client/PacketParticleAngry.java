/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.client;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;

public class PacketParticleAngry implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketParticleAngry> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "particle_angry"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleAngry> STREAM_CODEC = StreamCodec.of(PacketParticleAngry::encode, PacketParticleAngry::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;

    public PacketParticleAngry(UUID playerId) {
        this.playerId = playerId;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketParticleAngry msg) {
        buf.writeUUID(msg.playerId);
    }

    public static PacketParticleAngry decode(RegistryFriendlyByteBuf buf) {
        return new PacketParticleAngry(buf.readUUID());
    }

    public static void handle(PacketParticleAngry msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(msg.playerId);
            if (player == null) {
                return;
            }
            ModelData data = ModelData.get(player);
            for (int i = 0; i < 5; ++i) {
                double d0 = player.getRandom().nextGaussian() * 0.02;
                double d1 = player.getRandom().nextGaussian() * 0.02;
                double d2 = player.getRandom().nextGaussian() * 0.02;
                double x = player.getX() + (double)((player.getRandom().nextFloat() - 0.5f) * player.getBbWidth() * 2.0f);
                double z = player.getZ() + (double)((player.getRandom().nextFloat() - 0.5f) * player.getBbWidth() * 2.0f);
                player.level().addParticle((ParticleOptions)ParticleTypes.ANGRY_VILLAGER, x, player.getY() + (double)0.8f + (double)(player.getRandom().nextFloat() * player.getBbHeight() / 2.0f) - 0.0 - (double)data.getBodyY(), z, d0, d1, d2);
            }
        });
    }
}

