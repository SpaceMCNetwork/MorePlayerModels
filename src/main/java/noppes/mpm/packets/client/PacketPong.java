/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.gui.GuiCreationScreenInterface;

public class PacketPong implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketPong> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "pong"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPong> STREAM_CODEC = StreamCodec.of(PacketPong::encode, PacketPong::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final int version;

    public PacketPong(int version) {
        this.version = version;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketPong msg) {
        buf.writeInt(msg.version);
    }

    public static PacketPong decode(RegistryFriendlyByteBuf buf) {
        return new PacketPong(buf.readInt());
    }

    public static void handle(PacketPong msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (msg.version == MorePlayerModels.Version) {
                MorePlayerModels.HasServerSide = true;
                GuiCreationScreenInterface.Message = "";
            } else if (msg.version < MorePlayerModels.Version) {
                MorePlayerModels.HasServerSide = false;
                GuiCreationScreenInterface.Message = "message.lowerversion";
            } else if (msg.version > MorePlayerModels.Version) {
                MorePlayerModels.HasServerSide = false;
                GuiCreationScreenInterface.Message = "message.higherversion";
            }
        });
    }
}

