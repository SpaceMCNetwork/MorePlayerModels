/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.resources.ResourceLocation
 */
package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;

public class GuiButtonBiDirectional
extends GuiNpcButton {
    public static final ResourceLocation resource = ResourceLocation.parse("moreplayermodels:textures/gui/arrowbuttons.png");

    public GuiButtonBiDirectional(GuiNPCInterface gui, int id, int x, int y, int width, int height, String[] arr, int current) {
        super(gui, id, x, y, width, height, arr, current);
    }

    public GuiButtonBiDirectional(int id, int x, int y, int width, int height, String[] arr, int current, Button.OnPress clicked) {
        super(id, x, y, width, height, arr, current, clicked);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        boolean disabled = !this.active || this.display.length <= 1;
        boolean hover = !disabled && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        boolean hoverL = !disabled && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + 11 && mouseY < this.getY() + this.height;
        boolean hoverR = !disabled && !hoverL && mouseX >= this.getX() + this.width - 11 && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        graphics.blit(resource, this.getX(), this.getY(), 0, disabled ? 20 : (hoverL ? 40 : 0), 11, 20);
        graphics.blit(resource, this.getX() + this.width - 11, this.getY(), 11, disabled ? 20 : (hoverR ? 40 : 0), 11, 20);
        int l = 0xFFFFFF;
        if (this.packedFGColor != 0) {
            l = this.packedFGColor;
        } else if (!this.active || disabled) {
            l = 0xA0A0A0;
        } else if (hover) {
            l = 0xFFFFA0;
        }
        Object text = "";
        float maxWidth = this.width - 36;
        String displayString = this.getMessage().getString();
        if ((float)mc.font.width(displayString) > maxWidth) {
            char c;
            for (int h = 0; h < displayString.length() && !((float)mc.font.width((String)(text = (String)text + (c = displayString.charAt(h)))) > maxWidth); ++h) {
            }
            text = (String)text + "...";
        } else {
            text = displayString;
        }
        if (hover) {
            text = "\u00a7n" + (String)text;
        }
        graphics.drawCenteredString(mc.font, (String)text, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, l);
    }

    @Override
    public void onClick(double x, double y) {
        if (this.display != null && this.display.length != 0) {
            boolean hoverR;
            int value = this.getValue();
            boolean hoverL = x >= (double)this.getX() && y >= (double)this.getY() && x < (double)(this.getX() + 11) && y < (double)(this.getY() + this.height);
            boolean bl = hoverR = !hoverL && x >= (double)(this.getX() + 11) && y >= (double)this.getY() && x < (double)(this.getX() + this.width) && y < (double)(this.getY() + this.height);
            if (hoverR) {
                value = (value + 1) % this.display.length;
            }
            if (hoverL) {
                if (value <= 0) {
                    value = this.display.length;
                }
                --value;
            }
            this.setDisplay(value);
        }
        this.onPress.onPress((Button)this);
    }
}

