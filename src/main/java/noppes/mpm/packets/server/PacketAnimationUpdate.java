/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package noppes.mpm.packets.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketAnimationStart;

public record PacketAnimationUpdate(EnumAnimation animation) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketAnimationUpdate> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("moreplayermodels", "animation_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAnimationUpdate> STREAM_CODEC = StreamCodec.of(PacketAnimationUpdate::encode, PacketAnimationUpdate::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void encode(RegistryFriendlyByteBuf buf, PacketAnimationUpdate msg) {
        buf.writeEnum((Enum)msg.animation);
    }

    public static PacketAnimationUpdate decode(RegistryFriendlyByteBuf buf) {
        return new PacketAnimationUpdate((EnumAnimation)buf.readEnum(EnumAnimation.class));
    }

    public static void handle(PacketAnimationUpdate msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> PacketAnimationUpdate.setAnimation((Player)ctx.player(), msg.animation));
    }

    public static void setAnimation(Player player, EnumAnimation animation) {
        if (MorePlayerModels.HasServerSide && player.level().isClientSide) {
            Packets.sendServer(new PacketAnimationUpdate(animation));
            return;
        }
        ModelData data = ModelData.get(player);
        if (animation == EnumAnimation.SLEEP) {
            data.sleepRotation = player.yBodyRot;
        }
        if (data.animation == animation) {
            animation = EnumAnimation.NONE;
        } else if (data.moveAnimation == animation) {
            animation = EnumAnimation.IDLE;
        }
        data.setAnimation(animation);
        Packets.sendNearby((Entity)player, new PacketAnimationStart(player.getUUID(), animation));
    }
}

