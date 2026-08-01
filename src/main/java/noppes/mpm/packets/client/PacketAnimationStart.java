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
import noppes.mpm.constants.EnumAnimation;

public class PacketAnimationStart implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketAnimationStart> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "animation_start"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAnimationStart> STREAM_CODEC = StreamCodec.of(PacketAnimationStart::encode, PacketAnimationStart::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public final UUID playerId;
    public final EnumAnimation animation;

    public PacketAnimationStart(UUID playerId, EnumAnimation animation) {
        this.playerId = playerId;
        this.animation = animation;
    }

    public static void encode(RegistryFriendlyByteBuf buf, PacketAnimationStart msg) {
        buf.writeUUID(msg.playerId);
        buf.writeEnum((Enum)msg.animation);
    }

    public static PacketAnimationStart decode(RegistryFriendlyByteBuf buf) {
        return new PacketAnimationStart(buf.readUUID(), (EnumAnimation)buf.readEnum(EnumAnimation.class));
    }

    public static void handle(PacketAnimationStart msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player pl = ClientPacketHelper.getPlayer(msg.playerId);
            if (pl == null) {
                return;
            }
            ModelData data = ModelData.get(pl);
            data.setAnimation(msg.animation);
            data.animationStart = pl.tickCount;
            if (msg.animation == EnumAnimation.SLEEP) {
                data.sleepRotation = pl.yBodyRot;
            }
        });
    }
}
