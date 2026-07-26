/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 */
package noppes.mpm.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiModelColor
extends GuiNPCInterface
implements ITextfieldListener {
    private Screen parent;
    private static final ResourceLocation colorPicker = ResourceLocation.parse("moreplayermodels:textures/gui/color.png");
    private static final ResourceLocation colorgui = ResourceLocation.parse("moreplayermodels:textures/gui/color_gui.png");
    private int colorX;
    private int colorY;
    private GuiNpcTextField textfield;
    public int color;
    private ColorCallback callback;

    public GuiModelColor(Screen parent, int color, ColorCallback callback) {
        this.parent = parent;
        this.callback = callback;
        this.ySize = 230;
        this.closeOnEsc = false;
        this.background = colorgui;
        this.color = color;
    }

    @Override
    public void init() {
        super.init();
        this.colorX = this.guiLeft + 4;
        this.colorY = this.guiTop + 50;
        this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 35, this.guiTop + 25, 60, 20, this.getColor());
        this.addTextField(this.textfield);
        this.addButton(new GuiNpcButton(this, 66, this.guiLeft + 107, this.guiTop + 8, 20, 20, "X"));
        this.textfield.setTextColor(this.color);
    }

    @Override
    public void buttonEvent(GuiNpcButton guibutton) {
        if (guibutton.id == 66) {
            this.close();
        }
    }

    @Override
    public boolean charTyped(char c, int i) {
        String prev = this.textfield.getValue();
        boolean bo = super.charTyped(c, i);
        String newText = this.textfield.getValue();
        if (newText.equals(prev)) {
            return bo;
        }
        try {
            this.color = Integer.parseInt(this.textfield.getValue(), 16);
            this.callback.color(this.color);
            this.textfield.setTextColor(this.color);
        }
        catch (NumberFormatException e) {
            this.textfield.setValue(prev);
        }
        return bo;
    }

    @Override
    public void render(GuiGraphics graphics, int par1, int par2, float par3) {
        super.render(graphics, par1, par2, par3);
        graphics.blit(colorPicker, this.colorX, this.colorY, 0, 0, 120, 120);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean mouseClicked(double i, double j, int k) {
        boolean bo = super.mouseClicked(i, j, k);
        if (i < (double)this.colorX || i > (double)(this.colorX + 120) || j < (double)this.colorY || j > (double)(this.colorY + 120)) {
            return bo;
        }
        InputStream stream = null;
        try {
            Resource resource = (Resource)this.minecraft.getResourceManager().getResource(colorPicker).get();
            stream = resource.open();
            BufferedImage bufferedimage = ImageIO.read(stream);
            int color = bufferedimage.getRGB((int)(i - (double)this.guiLeft - 4.0) * 4, (int)(j - (double)this.guiTop - 50.0) * 4) & 0xFFFFFF;
            if (color != 0) {
                this.color = color;
                this.callback.color(color);
                this.textfield.setTextColor(color);
                this.textfield.setValue(this.getColor());
            }
        }
        catch (IOException iOException) {
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        return true;
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        try {
            this.color = Integer.parseInt(textfield.getValue(), 16);
        }
        catch (NumberFormatException e) {
            this.color = 0;
        }
        this.callback.color(this.color);
        textfield.setTextColor(this.color);
    }

    public String getColor() {
        String str = Integer.toHexString(this.color);
        while (((String)str).length() < 6) {
            str = "0" + str;
        }
        return str;
    }

    @Override
    public void save() {
    }

    public static interface ColorCallback {
        public void color(int var1);
    }
}
