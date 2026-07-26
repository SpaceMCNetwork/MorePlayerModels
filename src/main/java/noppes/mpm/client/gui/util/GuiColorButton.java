/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button$OnPress
 */
package noppes.mpm.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import noppes.mpm.client.gui.util.GuiNpcButton;

public class GuiColorButton
extends GuiNpcButton {
    public int color;

    public GuiColorButton(int id, int x, int y, int color, Button.OnPress clicked) {
        super(id, x, y, 50, 20, "", clicked);
        this.color = color;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        graphics.fill(this.getX(), this.getY(), this.getX() + 50, this.getY() + 20, -16777216 + this.color);
    }
}

