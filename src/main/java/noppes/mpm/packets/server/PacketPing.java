/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketBackItemUpdate;
import noppes.mpm.packets.client.PacketPlayerDataSend;
import noppes.mpm.packets.client.PacketPong;

public class PacketPing implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketPing> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "ping"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPing> STREAM_CODEC = StreamCodec.of(PacketPing::encode, PacketPing::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final int version;
    public final CompoundTag data;

    public PacketPing(int version, CompoundTag data) {
        this.version = version;
        this.data = data;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketPing msg) {
        buf.writeInt(msg.version);
        ByteBufCodecs.COMPOUND_TAG.encode(buf, msg.data);
    }

    public static PacketPing decode(RegistryFriendlyByteBuf buf) {
        return new PacketPing(buf.readInt(), ByteBufCodecs.COMPOUND_TAG.decode(buf));
    }

    public static void handle(PacketPing msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ItemStack back;
            ServerPlayer player = (ServerPlayer)ctx.player();
            if (msg.version == MorePlayerModels.Version) {
                ModelData data = ModelData.get((Player)player);
                data.readFromNBT(msg.data);
                if (!player.level().getGameRules().getBoolean(MorePlayerModels.ALLOW_ENTITY_MODELS)) {
                    data.setEntity(null);
                }
                data.save();
                Packets.sendNearby((Entity)player, new PacketPlayerDataSend(player.getUUID(), data.writeToNBT()));
            }
            if (!(back = (ItemStack)player.getInventory().items.get(0)).isEmpty()) {
                Packets.sendNearby((Entity)player, new PacketBackItemUpdate(player.getUUID(), back));
            }
            MorePlayerModels.HasServerSide = true;
            Packets.send(player, new PacketPong(MorePlayerModels.Version));
        });
    }
}

