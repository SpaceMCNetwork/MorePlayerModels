/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.client;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;

public record PacketBackItemUpdate(UUID playerId, ItemStack item) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketBackItemUpdate> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "back_item_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketBackItemUpdate> STREAM_CODEC = StreamCodec.of(PacketBackItemUpdate::encode, PacketBackItemUpdate::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void encode(RegistryFriendlyByteBuf buf, PacketBackItemUpdate msg) {
        buf.writeUUID(msg.playerId);
        // The back slot is allowed to be empty.  STREAM_CODEC intentionally
        // rejects ItemStack.EMPTY, which made clearing the first hotbar slot
        // disconnect the receiver with "Empty ItemStack not allowed".
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, msg.item);
    }

    public static PacketBackItemUpdate decode(RegistryFriendlyByteBuf buf) {
        return new PacketBackItemUpdate(buf.readUUID(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static void handle(PacketBackItemUpdate msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = ClientPacketHelper.getPlayer(msg.playerId);
            if (pl == null) {
                return;
            }
            ModelData data = ModelData.get(pl);
            data.backItem = msg.item.copy();
        });
    }
}
