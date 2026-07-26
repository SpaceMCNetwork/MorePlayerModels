/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiNpcTextField
extends EditBox {
    public boolean enabled = true;
    public boolean inMenu = true;
    public boolean numbersOnly = false;
    private ITextfieldListener listener;
    public int id;
    public int min = 0;
    public int max = Integer.MAX_VALUE;
    public int def = 0;
    private static GuiNpcTextField activeTextfield = null;
    private final int[] allowedSpecialChars = new int[]{14, 211, 203, 205};

    public GuiNpcTextField(int id, Screen parent, int i, int j, int k, int l, String s) {
        super(Minecraft.getInstance().font, i, j, k, l, (Component)Component.literal((String)s));
        this.setMaxLength(500);
        this.setValue(s);
        this.id = id;
        if (parent instanceof ITextfieldListener) {
            this.listener = (ITextfieldListener)parent;
        }
    }

    public GuiNpcTextField(Screen parent, int id, int i, int j, int k, int l, String s, ITextfieldListener listener) {
        super(Minecraft.getInstance().font, i, j, k, l, (Component)Component.literal((String)s));
        this.setMaxLength(500);
        this.setValue(s);
        this.id = id;
        this.listener = listener;
    }

    public static boolean hasActive() {
        return activeTextfield != null;
    }

    private boolean charAllowed(char c, int i) {
        if (!this.numbersOnly || Character.isDigit(c)) {
            return true;
        }
        for (int j : this.allowedSpecialChars) {
            if (j != i) continue;
            return true;
        }
        return false;
    }

    public boolean charTyped(char c, int i) {
        if (!this.charAllowed(c, i)) {
            return false;
        }
        return super.charTyped(c, i);
    }

    public boolean isEmpty() {
        return this.getValue().trim().length() == 0;
    }

    public int getInteger() {
        return Integer.parseInt(this.getValue());
    }

    public boolean isInteger() {
        try {
            Integer.parseInt(this.getValue());
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public void unFocused() {
        if (this.numbersOnly) {
            if (this.isEmpty() || !this.isInteger()) {
                this.setValue("" + this.def);
            } else if (this.getInteger() < this.min) {
                this.setValue("" + this.min);
            } else if (this.getInteger() > this.max) {
                this.setValue("" + this.max);
            }
        }
        if (this.listener != null) {
            this.listener.unFocused(this);
        }
        if (this == activeTextfield) {
            activeTextfield = null;
        }
    }

    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.enabled) {
            super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        }
    }

    public void setMinMaxDefault(int i, int j, int k) {
        this.min = i;
        this.max = j;
        this.def = k;
    }

    public static void unfocus() {
        if (activeTextfield != null) {
            activeTextfield.unFocused();
        }
        activeTextfield = null;
    }

    public static void handleFocus(Screen screen, boolean clickResult) {
        GuiEventListener focused = screen.getFocused();
        if (focused instanceof GuiNpcTextField) {
            GuiNpcTextField tf = (GuiNpcTextField)focused;
            if (!clickResult) {
                screen.setFocused(null);
                GuiNpcTextField.unfocus();
                return;
            }
            if (activeTextfield != null && activeTextfield != tf) {
                activeTextfield.unFocused();
            }
            activeTextfield = tf;
        } else if (activeTextfield != null) {
            GuiNpcTextField.unfocus();
        }
    }
}

