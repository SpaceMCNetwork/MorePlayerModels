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
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;
import noppes.mpm.ModelEyeData;
import noppes.mpm.client.parts.MpmPartData;

public class PacketEyeBlink implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketEyeBlink> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "eye_blink"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketEyeBlink> STREAM_CODEC = StreamCodec.of(PacketEyeBlink::encode, PacketEyeBlink::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;

    public PacketEyeBlink(UUID playerId) {
        this.playerId = playerId;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketEyeBlink msg) {
        buf.writeUUID(msg.playerId);
    }

    public static PacketEyeBlink decode(RegistryFriendlyByteBuf buf) {
        return new PacketEyeBlink(buf.readUUID());
    }

    public static void handle(PacketEyeBlink msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = Minecraft.getInstance().level.getPlayerByUUID(msg.playerId);
            if (pl == null) {
                return;
            }
            ModelData data = ModelData.get(pl);
            for (MpmPartData pd : data.mpmParts) {
                if (!(pd instanceof ModelEyeData)) continue;
                ((ModelEyeData)pd).blinkStart = System.currentTimeMillis();
            }
        });
    }
}

