/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 */
package noppes.mpm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import noppes.mpm.ModelData;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.packets.Packets;
import noppes.mpm.packets.client.PacketAnimationStart;

public class CommandAnimations {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandAnimations.animationCommand(dispatcher, "bow", EnumAnimation.BOW);
        CommandAnimations.animationCommand(dispatcher, "crawl", EnumAnimation.CRAWL);
        CommandAnimations.animationCommand(dispatcher, "cry", EnumAnimation.CRY);
        CommandAnimations.animationCommand(dispatcher, "dance", EnumAnimation.DANCE);
        CommandAnimations.animationCommand(dispatcher, "death", EnumAnimation.DEATH);
        CommandAnimations.animationCommand(dispatcher, "hug", EnumAnimation.HUG);
        CommandAnimations.animationCommand(dispatcher, "no", EnumAnimation.NO);
        CommandAnimations.animationCommand(dispatcher, "point", EnumAnimation.POINT);
        CommandAnimations.animationCommand(dispatcher, "sit", EnumAnimation.SIT);
        CommandAnimations.animationCommand(dispatcher, "sleep", EnumAnimation.SLEEP);
        CommandAnimations.animationCommand(dispatcher, "wag", EnumAnimation.WAG);
        CommandAnimations.animationCommand(dispatcher, "wave", EnumAnimation.WAVE);
        CommandAnimations.animationCommand(dispatcher, "yes", EnumAnimation.YES);
    }

    private static void animationCommand(CommandDispatcher<CommandSourceStack> dispatcher, String command, EnumAnimation animation) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)command).requires(source -> source.hasPermission(0))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            ModelData data = ModelData.get((Player)player);
            EnumAnimation ani = animation;
            if (data.animation == ani) {
                ani = EnumAnimation.NONE;
            } else if (data.moveAnimation == ani) {
                ani = EnumAnimation.IDLE;
            }
            data.setAnimation(ani);
            Packets.sendNearby((Entity)player, new PacketAnimationStart(player.getUUID(), ani));
            return 1;
        }));
    }
}

