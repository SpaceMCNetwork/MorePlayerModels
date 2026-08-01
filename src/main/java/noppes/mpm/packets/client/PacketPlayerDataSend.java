/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.client;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;

public class PacketPlayerDataSend implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketPlayerDataSend> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "player_data_send"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPlayerDataSend> STREAM_CODEC = StreamCodec.of(PacketPlayerDataSend::encode, PacketPlayerDataSend::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;
    public final CompoundTag compound;

    public PacketPlayerDataSend(UUID playerId, CompoundTag compound) {
        this.playerId = playerId;
        this.compound = compound;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketPlayerDataSend msg) {
        buf.writeUUID(msg.playerId);
        ByteBufCodecs.COMPOUND_TAG.encode(buf, msg.compound);
    }

    public static PacketPlayerDataSend decode(RegistryFriendlyByteBuf buf) {
        return new PacketPlayerDataSend(buf.readUUID(), ByteBufCodecs.COMPOUND_TAG.decode(buf));
    }

    public static void handle(PacketPlayerDataSend msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = ClientPacketHelper.getPlayer(msg.playerId);
            if (pl == null) {
                return;
            }
            ModelData data = ModelData.get(pl);
            data.readFromNBT(msg.compound);
            if (pl == Minecraft.getInstance().player) {
                // Remote profiles are display-only on this client.  Persisting
                // each tracking update caused unnecessary disk writes and left
                // stale remote profiles in the local MPM directory.
                data.lastEdited = System.currentTimeMillis();
                data.save();
            }
        });
    }
}
