/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.language.I18n
 */
package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.resources.language.I18n;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.gui.GuiCreationExtra;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.client.gui.GuiModelColor;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcButtonYesNo;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationParts
extends GuiCreationScreenInterface
implements ITextfieldListener,
ICustomScrollListener {
    private GuiCustomScroll scroll;
    private GuiPart[] parts = new GuiPart[0];
    private static int selected = 0;

    public GuiCreationParts() {
        this.active = 2;
        Arrays.sort(this.parts, (o1, o2) -> {
            String s1 = I18n.get((String)("part." + o1.part.name), (Object[])new Object[0]);
            String s2 = I18n.get((String)("part." + o2.part.name), (Object[])new Object[0]);
            return s1.compareToIgnoreCase(s2);
        });
    }

    @Override
    public void init() {
        super.init();
        if (this.entity != null) {
            this.openGui(new GuiCreationExtra());
            return;
        }
        if (this.scroll == null) {
            ArrayList<String> list = new ArrayList<String>();
            for (GuiPart part : this.parts) {
                list.add(I18n.get((String)("part." + part.part.name), (Object[])new Object[0]));
            }
            this.scroll = new GuiCustomScroll(this, 0);
            this.scroll.setUnsortedList(list);
        }
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 46;
        this.scroll.setSize(100, this.ySize - 50);
        this.addScroll(this.scroll);
    }

    @Override
    public void buttonEvent(GuiNpcButton btn) {
        if (this.parts[selected] != null) {
            this.parts[selected].buttonEvent(btn);
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 23) {
            // empty if block
        }
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
        if (scroll.getSelectedIndex() >= 0) {
            selected = scroll.getSelectedIndex();
            this.init();
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

    class GuiPart {
        EnumParts part;
        private int paterns = 0;
        protected String[] types = new String[]{"gui.none"};
        protected ModelPartData data;
        protected boolean hasPlayerOption = true;
        protected boolean noPlayerTypes = false;
        protected boolean canBeDeleted = true;

        public GuiPart(EnumParts part) {
            this.part = part;
        }

        public int init() {
            int y = GuiCreationParts.this.guiTop + 50;
            if (this.data == null || !this.data.playerTexture || !this.noPlayerTypes) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(20, "gui.type", GuiCreationParts.this.guiLeft + 102, y + 5, 0xFFFFFF));
                GuiCreationParts.this.addButton(new GuiButtonBiDirectional(GuiCreationParts.this, 20, GuiCreationParts.this.guiLeft + 145, y, 100, 20, this.types, this.data == null ? 0 : this.data.type + 1));
                y += 25;
            }
            if (this.data != null && this.hasPlayerOption) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(21, "gui.playerskin", GuiCreationParts.this.guiLeft + 102, y + 5, 0xFFFFFF));
                GuiCreationParts.this.addButton(new GuiNpcButtonYesNo((GuiNPCInterface)GuiCreationParts.this, 21, GuiCreationParts.this.guiLeft + 170, y, this.data.playerTexture));
                y += 25;
            }
            if (this.data != null && !this.hasPlayerOption) {
                this.data.playerTexture = false;
            }
            if (this.data != null && !this.data.playerTexture) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(23, "gui.color", GuiCreationParts.this.guiLeft + 102, y + 5, 0xFFFFFF));
                y += 25;
            }
            return y;
        }

        public void buttonEvent(GuiNpcButton btn) {
            if (btn.id == 20) {
                int i = btn.getValue();
                if (i != 0 || this.canBeDeleted) {
                    // empty if block
                }
                GuiCreationParts.this.init();
            }
            if (btn.id == 22) {
                this.data.pattern = (byte)btn.getValue();
            }
            if (btn.id == 21) {
                this.data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
                GuiCreationParts.this.init();
            }
            if (btn.id == 23) {
                GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.data.color, color -> {
                    this.data.color = color;
                }));
            }
        }

        public GuiPart noPlayerOptions() {
            this.hasPlayerOption = false;
            return this;
        }

        public GuiPart noPlayerTypes() {
            this.noPlayerTypes = true;
            return this;
        }

        public GuiPart setTypes(String[] types) {
            this.types = types;
            return this;
        }
    }

    class GuiPartParticles
    extends GuiPart {
        public GuiPartParticles() {
            super(EnumParts.PARTICLES);
            this.types = new String[]{"gui.none", "1", "2"};
        }

        @Override
        public int init() {
            int y = super.init();
            if (this.data == null) {
                return y;
            }
            return y;
        }
    }
}

