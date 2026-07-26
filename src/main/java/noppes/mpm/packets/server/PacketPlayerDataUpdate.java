/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketPlayerDataSend;

public class PacketPlayerDataUpdate implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketPlayerDataUpdate> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "player_data_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPlayerDataUpdate> STREAM_CODEC = StreamCodec.of(PacketPlayerDataUpdate::encode, PacketPlayerDataUpdate::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final CompoundTag data;

    public PacketPlayerDataUpdate(CompoundTag data) {
        this.data = data;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketPlayerDataUpdate msg) {
        ByteBufCodecs.COMPOUND_TAG.encode(buf, msg.data);
    }

    public static PacketPlayerDataUpdate decode(RegistryFriendlyByteBuf buf) {
        return new PacketPlayerDataUpdate(ByteBufCodecs.COMPOUND_TAG.decode(buf));
    }

    public static void handle(PacketPlayerDataUpdate msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer)ctx.player();
            ModelData data = ModelData.get((Player)player);
            data.readFromNBT(msg.data);
            if (!player.level().getGameRules().getBoolean(MorePlayerModels.ALLOW_ENTITY_MODELS)) {
                data.setEntity(null);
            }
            data.save();
            Packets.sendNearby((Entity)player, new PacketPlayerDataSend(player.getUUID(), data.writeToNBT()));
        });
    }
}

