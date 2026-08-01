/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.client;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;

public class PacketParticleLove implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketParticleLove> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "particle_love"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleLove> STREAM_CODEC = StreamCodec.of(PacketParticleLove::encode, PacketParticleLove::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;

    public PacketParticleLove(UUID playerId) {
        this.playerId = playerId;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketParticleLove msg) {
        buf.writeUUID(msg.playerId);
    }

    public static PacketParticleLove decode(RegistryFriendlyByteBuf buf) {
        return new PacketParticleLove(buf.readUUID());
    }

    public static void handle(PacketParticleLove msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = ClientPacketHelper.getPlayer(msg.playerId);
            if (pl == null) {
                return;
            }
            ModelData data = ModelData.get(pl);
            data.inLove = 40;
        });
    }
}
