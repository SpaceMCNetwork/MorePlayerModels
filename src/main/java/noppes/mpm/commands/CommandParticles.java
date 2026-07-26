/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 */
package noppes.mpm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketParticleAngry;
import noppes.mpm.packets.client.PacketParticleLove;
import noppes.mpm.packets.client.PacketParticleNote;

public class CommandParticles {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"angry").requires(source -> source.hasPermission(0))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            Packets.sendNearby((Entity)player, new PacketParticleAngry(player.getUUID()));
            return 1;
        }));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"love").requires(source -> source.hasPermission(0))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            Packets.sendNearby((Entity)player, new PacketParticleLove(player.getUUID()));
            return 1;
        }));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"sing").requires(source -> source.hasPermission(0))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            CommandParticles.playNote(player, player.getRandom().nextInt(25));
            return 1;
        })).then(Commands.argument((String)"note", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            CommandParticles.playNote(player, IntegerArgumentType.getInteger((CommandContext)context, (String)"note"));
            return 1;
        })));
    }

    private static void playNote(ServerPlayer player, int note) {
        float pitch = (float)Math.pow(2.0, (double)(note - 12) / 12.0);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.PLAYERS, 3.0f, pitch);
        Packets.sendNearby((Entity)player, new PacketParticleNote(player.getUUID(), note));
    }
}

