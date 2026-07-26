package noppes.mpm.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.packets.client.*;
import noppes.mpm.packets.server.*;
import noppes.mpm.util.MPMScheduler;

/** Typed NeoForge payload registration and distribution helpers. */
public final class Packets {
    private Packets() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mpm");
        registrar.playToClient(PacketAnimationStart.TYPE, PacketAnimationStart.STREAM_CODEC, PacketAnimationStart::handle)
                .playToClient(PacketBackItemUpdate.TYPE, PacketBackItemUpdate.STREAM_CODEC, PacketBackItemUpdate::handle)
                .playToClient(PacketChatEvent.TYPE, PacketChatEvent.STREAM_CODEC, PacketChatEvent::handle)
                .playToClient(PacketEyeBlink.TYPE, PacketEyeBlink.STREAM_CODEC, PacketEyeBlink::handle)
                .playToClient(PacketParticleAngry.TYPE, PacketParticleAngry.STREAM_CODEC, PacketParticleAngry::handle)
                .playToClient(PacketParticleLove.TYPE, PacketParticleLove.STREAM_CODEC, PacketParticleLove::handle)
                .playToClient(PacketParticleNote.TYPE, PacketParticleNote.STREAM_CODEC, PacketParticleNote::handle)
                .playToClient(PacketPlayerDataSend.TYPE, PacketPlayerDataSend.STREAM_CODEC, PacketPlayerDataSend::handle)
                .playToClient(PacketPong.TYPE, PacketPong.STREAM_CODEC, PacketPong::handle)
                .playToServer(PacketAnimationUpdate.TYPE, PacketAnimationUpdate.STREAM_CODEC, PacketAnimationUpdate::handle)
                .playToServer(PacketPing.TYPE, PacketPing.STREAM_CODEC, PacketPing::handle)
                .playToServer(PacketPlayerDataUpdate.TYPE, PacketPlayerDataUpdate.STREAM_CODEC, PacketPlayerDataUpdate::handle);
    }

    public static void send(ServerPlayer player, CustomPacketPayload msg) {
        if (MorePlayerModels.HasServerSide) PacketDistributor.sendToPlayer(player, msg);
    }

    public static void sendDelayed(ServerPlayer player, CustomPacketPayload msg, int delay) {
        if (MorePlayerModels.HasServerSide) MPMScheduler.runTack(() -> PacketDistributor.sendToPlayer(player, msg), delay);
    }

    public static void sendNearby(Entity entity, CustomPacketPayload msg) {
        if (MorePlayerModels.HasServerSide) PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, msg);
    }

    public static void sendAll(CustomPacketPayload msg) {
        if (MorePlayerModels.HasServerSide) PacketDistributor.sendToAllPlayers(msg);
    }

    public static void sendServer(CustomPacketPayload msg) {
        if (MorePlayerModels.HasServerSide) PacketDistributor.sendToServer(msg);
    }
}
