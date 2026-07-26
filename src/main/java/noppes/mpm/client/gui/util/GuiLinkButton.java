/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.FormattedCharSequence
 */
package noppes.mpm.client.gui.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

public class GuiLinkButton
extends Button {
    private int color;

    public GuiLinkButton(int x, int y, Component title, Button.OnPress action) {
        super(x, y, Minecraft.getInstance().font.width((FormattedText)title), 9, title, action, Button.DEFAULT_NARRATION);
        this.color = 238;
    }

    public GuiLinkButton(int x, int y, int color, Component title, Button.OnPress action) {
        super(x, y, Minecraft.getInstance().font.width((FormattedText)title), 9, title, action, Button.DEFAULT_NARRATION);
        this.color = 238;
        this.color = color;
    }

    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        MutableComponent text = this.getMessage().plainCopy().withStyle(new ChatFormatting[0]);
        if (this.isHoveredOrFocused()) {
            text.withStyle(new ChatFormatting[]{ChatFormatting.UNDERLINE});
        }
        FormattedCharSequence ireorderingprocessor = text.getVisualOrderText();
        graphics.drawString(minecraft.font, ireorderingprocessor, this.getX(), this.getY(), this.color);
    }
}
