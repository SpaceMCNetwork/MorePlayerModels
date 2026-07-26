/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class GuiNpcLabel {
    public Component label;
    private int x;
    private int y;
    private int color = 0x404040;
    public boolean enabled = true;
    public int id;

    public GuiNpcLabel(int id, Component label, int x, int y, int color) {
        this.id = id;
        this.label = label;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public GuiNpcLabel(int id, String label, int x, int y, int color) {
        this(id, (Component)Component.translatable((String)label), x, y, color);
    }

    public GuiNpcLabel(int id, Component label, int x, int y) {
        this(id, label, x, y, 0x404040);
    }

    public GuiNpcLabel(int id, String label, int x, int y) {
        this(id, label, x, y, 0x404040);
    }

    public void drawLabel(GuiGraphics graphics, Screen gui, Font font) {
        if (this.enabled) {
            graphics.drawString(font, this.label, this.x, this.y, this.color, false);
        }
    }

    public void center(int width) {
        int size = Minecraft.getInstance().font.width((FormattedText)this.label);
        this.x += (width - size) / 2;
    }
}

