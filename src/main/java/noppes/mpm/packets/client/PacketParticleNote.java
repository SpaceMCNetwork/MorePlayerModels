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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketParticleNote implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketParticleNote> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "particle_note"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleNote> STREAM_CODEC = StreamCodec.of(PacketParticleNote::encode, PacketParticleNote::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;
    public final int note;

    public PacketParticleNote(UUID playerId, int note) {
        this.playerId = playerId;
        this.note = note;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketParticleNote msg) {
        buf.writeUUID(msg.playerId);
        buf.writeInt(msg.note);
    }

    public static PacketParticleNote decode(RegistryFriendlyByteBuf buf) {
        return new PacketParticleNote(buf.readUUID(), buf.readInt());
    }

    public static void handle(PacketParticleNote msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = ClientPacketHelper.getPlayer(msg.playerId);
            if (pl == null) {
                return;
            }
            pl.level().addParticle((ParticleOptions)ParticleTypes.NOTE, pl.getX(), pl.getY() + 2.0, pl.getZ(), (double)msg.note / 24.0, 0.0, 0.0);
        });
    }
}
