/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package noppes.mpm.client.gui;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;

public class GuiEditEmotes
extends GuiNPCInterface {
    private final String[] animations = new String[]{"gui.none", "animation.sleep", "animation.crawl", "animation.hug", "animation.sit", "animation.dance", "animation.wave", "animation.wag", "animation.bow", "animation.cry", "animation.yes", "animation.no", "animation.point", "animation.death"};

    public GuiEditEmotes() {
        this.xSize = 366;
        this.ySize = 226;
        this.setBackground("menubg.png");
        this.closeOnEsc = true;
    }

    @Override
    public void init() {
        super.init();
        int y = this.guiTop + 4;
        this.addLabel(new GuiNpcLabel(0, "message.animationmessage1", this.guiLeft + 4, y));
        this.addLabel(new GuiNpcLabel(6, "message.animationmessage2", this.guiLeft + 4, y + 11));
        this.addButton(1, y += 32, "MPM 1", MorePlayerModels.button1);
        this.addButton(2, y += 22, "MPM 2", MorePlayerModels.button2);
        this.addButton(3, y += 22, "MPM 3", MorePlayerModels.button3);
        this.addButton(4, y += 22, "MPM 4", MorePlayerModels.button4);
        this.addButton(5, y += 22, "MPM 5", MorePlayerModels.button5);
        this.addButton(new GuiNpcButton(this, 66, this.guiLeft + this.xSize - 24, this.guiTop + 4, 20, 20, "X"));
        y = this.guiTop + 32;
        this.addLabel(new GuiNpcLabel(10, (Component)Component.translatable((String)"gui.commands").append(":"), this.guiLeft + 240, y));
        this.addLabel(new GuiNpcLabel(11, "/bow", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(12, "/crawl", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(13, "/cry", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(14, "/dance", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(15, "/death", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(16, "/hug", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(17, "/point", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(18, "/sit", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(19, "/sleep", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(20, "/wag", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(21, "/wave", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(22, "/yes", this.guiLeft + 250, y += 11));
        this.addLabel(new GuiNpcLabel(23, "/no", this.guiLeft + 250, y += 11));
    }

    private void addButton(int id, int y, String title, int value) {
        MutableComponent comp = Component.empty();
        for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if (!key.getName().equals(title)) continue;
            comp = Component.literal((String)" (").append(key.getKey().getDisplayName()).append(")");
            break;
        }
        this.addButton(new GuiButtonBiDirectional(this, id, this.guiLeft + 80, y, 100, 20, this.animations, value));
        this.addLabel(new GuiNpcLabel(id, (Component)Component.literal((String)title).append((Component)comp), this.guiLeft + 4, y + 5));
    }

    @Override
    public void buttonEvent(GuiNpcButton button) {
        if (button.id == 1) {
            MorePlayerModels.button1 = button.getValue();
            MorePlayerModels.instance.configLoader.updateConfig();
        }
        if (button.id == 2) {
            MorePlayerModels.button2 = button.getValue();
            MorePlayerModels.instance.configLoader.updateConfig();
        }
        if (button.id == 3) {
            MorePlayerModels.button3 = button.getValue();
            MorePlayerModels.instance.configLoader.updateConfig();
        }
        if (button.id == 4) {
            MorePlayerModels.button4 = button.getValue();
            MorePlayerModels.instance.configLoader.updateConfig();
        }
        if (button.id == 5) {
            MorePlayerModels.button5 = button.getValue();
            MorePlayerModels.instance.configLoader.updateConfig();
        }
        if (button.id == 66) {
            this.close();
        }
    }

    @Override
    public void save() {
    }
}

