/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package noppes.mpm.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.mpm.client.MpmKeys;

public class VersionChecker
extends Thread {
    @Override
    public void run() {
        LocalPlayer player;
        try {
            player = Minecraft.getInstance().player;
        }
        catch (NoSuchMethodError e) {
            return;
        }
        while ((player = Minecraft.getInstance().player) == null) {
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MutableComponent s = MpmKeys.Screen.getKey().getDisplayName().plainCopy().withStyle(ChatFormatting.RED);
        MutableComponent message = Component.literal((String)"\u00a72MorePlayerModels\u00a7f ").append((Component)Component.translatable((String)"message.startup", (Object[])new Object[]{s}));
        player.sendSystemMessage((Component)message);
    }
}

