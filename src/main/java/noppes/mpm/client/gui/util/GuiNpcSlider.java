/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 */
package noppes.mpm.client.gui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import noppes.mpm.client.gui.util.ISliderListener;

public class GuiNpcSlider
extends AbstractWidget {
    public static final ResourceLocation SLIDER_LOCATION = ResourceLocation.withDefaultNamespace("widget/slider");
    public static final ResourceLocation SLIDER_HIGHLIGHTED_LOCATION = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
    public static final ResourceLocation SLIDER_HANDLE_LOCATION = ResourceLocation.withDefaultNamespace("widget/slider_handle");
    public static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_LOCATION = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");
    private ISliderListener listener;
    public int id;
    public float sliderValue = 1.0f;

    public GuiNpcSlider(Screen parent, int id, int xPos, int yPos, String displayString, float sliderValue) {
        super(xPos, yPos, 150, 20, (Component)Component.translatable((String)displayString));
        this.id = id;
        this.sliderValue = sliderValue;
        if (parent instanceof ISliderListener) {
            this.listener = (ISliderListener)parent;
        }
    }

    public GuiNpcSlider(Screen parent, int id, int xPos, int yPos, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        if (this.listener != null) {
            this.listener.mouseDragged(this);
        }
    }

    public GuiNpcSlider(Screen parent, int id, int xPos, int yPos, int width, int height, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        this.width = width;
        this.height = height;
        if (this.listener != null) {
            this.listener.mouseDragged(this);
        }
    }

    public void setListener(ISliderListener listener) {
        this.listener = listener;
        listener.mouseDragged(this);
    }

    public void playDownSound(SoundManager soundHandler) {
    }

    public void setString(String str) {
        this.setMessage((Component)Component.literal((String)str));
    }

    private void setSliderValue(float value) {
        if ((value = Mth.clamp((float)value, (float)0.0f, (float)1.0f)) == this.sliderValue) {
            return;
        }
        this.sliderValue = value;
        this.listener.mouseDragged(this);
    }

    public void onClick(double x, double y) {
        if (!this.visible || !this.active) {
            return;
        }
        this.setSliderValue((float)(x - (double)(this.getX() + 4)) / (float)(this.width - 8));
    }

    protected void onDrag(double x, double y, double dragX, double dragY) {
        this.setSliderValue((float)(x - (double)(this.getX() + 4)) / (float)(this.width - 8));
        super.onDrag(x, y, dragX, dragY);
    }

    public void onRelease(double x, double y) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float f) {
        if (!this.visible) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        ResourceLocation background = this.isHoveredOrFocused() ? SLIDER_HIGHLIGHTED_LOCATION : SLIDER_LOCATION;
        ResourceLocation handle = this.isHoveredOrFocused() ? SLIDER_HANDLE_HIGHLIGHTED_LOCATION : SLIDER_HANDLE_LOCATION;
        graphics.blitSprite(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        graphics.blitSprite(handle, this.getX() + (int)(this.sliderValue * (float)(this.getWidth() - 8)), this.getY(), 8, this.getHeight());
        int i = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderScrollingString(graphics, Minecraft.getInstance().font, 2, i | Mth.ceil((float)(this.alpha * 255.0f)) << 24);
    }

    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
    }
}
