/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.common.capabilities.ICapabilityProvider
 *  net.neoforged.neoforge.event.AttachCapabilitiesEvent
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.event.ServerChatEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$NameFormat
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$StartTracking
 *  net.neoforged.bus.api.SubscribeEvent
 */
package noppes.mpm;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import noppes.mpm.ModelData;
import noppes.mpm.commands.CommandAnimations;
import noppes.mpm.commands.CommandMPM;
import noppes.mpm.commands.CommandParticles;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketBackItemUpdate;
import noppes.mpm.packets.client.PacketChatEvent;
import noppes.mpm.packets.client.PacketPlayerDataSend;

public class ServerEventHandler {
    private static final ResourceLocation key = ResourceLocation.fromNamespaceAndPath("moreplayermodels", "modeldata");

    @SubscribeEvent
    public void chat(ServerChatEvent event) {
        Packets.sendAll(new PacketChatEvent(event.getPlayer().getUUID(), event.getMessage()));
    }

    @SubscribeEvent
    public void playerTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        Player target = (Player)event.getTarget();
        ServerPlayer player = (ServerPlayer)event.getEntity();
        ModelData data = ModelData.get(target);
        Packets.sendDelayed(player, new PacketPlayerDataSend(target.getUUID(), data.writeToNBT()), 100);
        Packets.sendDelayed(player, new PacketBackItemUpdate(target.getUUID(), target.getInventory().getItem(0).copy()), 100);
    }

    @SubscribeEvent
    public void onNameSet(PlayerEvent.NameFormat event) {
        ModelData data = ModelData.get(event.getEntity());
        if (!data.displayName.isEmpty()) {
            event.setDisplayname((Component)Component.translatable((String)data.displayName.replace('&', Character.toChars(167)[0])));
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e) {
        CommandParticles.register((CommandDispatcher<CommandSourceStack>)e.getDispatcher());
        CommandAnimations.register((CommandDispatcher<CommandSourceStack>)e.getDispatcher());
        CommandMPM.register((CommandDispatcher<CommandSourceStack>)e.getDispatcher(), e.getBuildContext());
    }
}
