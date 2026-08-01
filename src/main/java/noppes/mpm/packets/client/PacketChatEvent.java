/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.client;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.client.ChatMessages;

public class PacketChatEvent implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketChatEvent> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "chat_event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketChatEvent> STREAM_CODEC = StreamCodec.of(PacketChatEvent::encode, PacketChatEvent::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;
    public final Component message;

    public PacketChatEvent(UUID playerId, Component message) {
        this.playerId = playerId;
        this.message = message;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketChatEvent msg) {
        buf.writeUUID(msg.playerId);
        net.minecraft.network.chat.ComponentSerialization.STREAM_CODEC.encode(buf, msg.message);
    }

    public static PacketChatEvent decode(RegistryFriendlyByteBuf buf) {
        return new PacketChatEvent(buf.readUUID(), net.minecraft.network.chat.ComponentSerialization.STREAM_CODEC.decode(buf));
    }

    public static void handle(PacketChatEvent msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = ClientPacketHelper.getPlayer(msg.playerId);
            if (pl == null) {
                return;
            }
            ChatMessages.getChatMessages(pl.getName().getString()).addMessage(msg.message);
        });
    }
}
