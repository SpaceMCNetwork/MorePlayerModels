/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 */
package noppes.mpm.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import noppes.mpm.client.gui.util.GuiNPCInterface;

public class GuiNpcButton
extends Button {
    public boolean shown = true;
    public GuiNPCInterface gui;
    protected String[] display;
    private int displayValue = 0;
    public int id;
    private static final Button.OnPress clicked = button -> {
        GuiNpcButton b = (GuiNpcButton)button;
        b.gui.buttonEvent(b);
    };

    public GuiNpcButton(GuiNPCInterface gui, int i, int j, int k, String s) {
        super(j, k, 200, 20, (Component)Component.translatable((String)s), clicked, Button.DEFAULT_NARRATION);
        this.id = i;
        this.gui = gui;
    }

    public GuiNpcButton(GuiNPCInterface gui, int i, int j, int k, String[] display, int val) {
        this(gui, i, j, k, display[val]);
        this.display = display;
        this.displayValue = val;
    }

    public GuiNpcButton(GuiNPCInterface gui, int i, int j, int k, int l, int m, String string) {
        super(j, k, l, m, (Component)Component.translatable((String)string), clicked, Button.DEFAULT_NARRATION);
        this.id = i;
        this.gui = gui;
    }

    public GuiNpcButton(int i, int j, int k, int l, int m, String string, Button.OnPress clicked) {
        super(j, k, l, m, (Component)Component.translatable((String)string), clicked, Button.DEFAULT_NARRATION);
        this.id = i;
    }

    public GuiNpcButton(GuiNPCInterface gui, int i, int j, int k, int l, int m, String[] display, int val) {
        this(gui, i, j, k, l, m, display[val % display.length]);
        this.display = display;
        this.displayValue = val % display.length;
    }

    public GuiNpcButton(int i, int j, int k, int l, int m, String[] display, int val, Button.OnPress clicked) {
        this(i, j, k, l, m, display[val % display.length], clicked);
        this.display = display;
        this.displayValue = val % display.length;
    }

    public void setDisplayText(String text) {
        this.setMessage((Component)Component.translatable((String)text));
    }

    public int getValue() {
        return this.displayValue;
    }

    public void clicked() {
    }

    public void renderWidget(GuiGraphics graphics, int i, int j, float partialTicks) {
        if (!this.shown) {
            return;
        }
        super.renderWidget(graphics, i, j, partialTicks);
    }

    public void onClick(double x, double y) {
        if (this.display != null) {
            this.setDisplay((this.displayValue + 1) % this.display.length);
        }
        super.onClick(x, y);
    }

    public void setDisplay(int value) {
        this.displayValue = value;
        this.setDisplayText(this.display[value]);
    }
}

