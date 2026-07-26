/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.ByteTag
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 */
package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import noppes.mpm.client.EntityFakeLiving;
import noppes.mpm.client.gui.GuiCreationEntities;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcButtonYesNo;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.util.PixelmonHelper;

public class GuiCreationExtra
extends GuiCreationScreenInterface
implements ICustomScrollListener {
    private final String[] ignoredTags = new String[]{"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
    private final String[] booleanTags = new String[0];
    private GuiCustomScroll scroll;
    private Map<String, GuiType> data = new HashMap<String, GuiType>();
    private GuiType selected;

    public GuiCreationExtra() {
        this.active = 2;
    }

    @Override
    public void init() {
        super.init();
        if (this.entity == null) {
            this.openGui(new GuiCreationEntities());
            return;
        }
        if (this.scroll == null) {
            this.data = this.getData(this.entity);
            this.scroll = new GuiCustomScroll(this, 0);
            ArrayList<String> list = new ArrayList<String>(this.data.keySet());
            this.scroll.setList(list);
            if (list.isEmpty()) {
                return;
            }
            this.scroll.setSelected((String)list.get(0));
        }
        this.selected = this.data.get(this.scroll.getSelected());
        if (this.selected == null) {
            return;
        }
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 46;
        this.scroll.setSize(100, this.ySize - 74);
        this.addScroll(this.scroll);
        this.selected.init();
    }

    public Map<String, GuiType> getData(LivingEntity entity) {
        HashMap<String, GuiType> data = new HashMap<String, GuiType>();
        CompoundTag compound = this.getExtras(entity);
        Set<String> keys = compound.getAllKeys();
        for (String name : keys) {
            byte b;
            if (this.isIgnored(name)) continue;
            Tag base = compound.get(name);
            if (name.equals("Age")) {
                data.put("Child", new GuiTypeBoolean("Child", entity.isBaby()));
                continue;
            }
            if (name.equals("Color") && base.getId() == 1) {
                data.put("Color", new GuiTypeByte("Color", compound.getByte("Color")));
                continue;
            }
            if (base.getId() != 1 || (b = ((ByteTag)base).getAsByte()) != 0 && b != 1) continue;
            if (this.playerdata.extra.contains(name)) {
                b = this.playerdata.extra.getByte(name);
            }
            data.put(name, new GuiTypeBoolean(name, b == 1));
        }
        if (PixelmonHelper.isPixelmon((Entity)entity)) {
            data.put("Model", new GuiTypePixelmon("Model"));
        }
        return data;
    }

    private boolean isIgnored(String tag) {
        for (String s : this.ignoredTags) {
            if (!s.equals(tag)) continue;
            return true;
        }
        return false;
    }

    private CompoundTag getExtras(LivingEntity entity) {
        CompoundTag fake = new CompoundTag();
        new EntityFakeLiving(entity.level()).addAdditionalSaveData(fake);
        CompoundTag compound = new CompoundTag();
        try {
            entity.addAdditionalSaveData(compound);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        Set<String> keys = fake.getAllKeys();
        for (String name : keys) {
            compound.remove(name);
        }
        return compound;
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
        if (scroll.id == 0) {
            this.init();
        } else if (this.selected != null) {
            this.selected.scrollClicked(i, j, k, scroll);
        }
    }

    @Override
    public void buttonEvent(GuiNpcButton btn) {
        if (this.selected != null) {
            this.selected.buttonEvent(btn);
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

    abstract class GuiType {
        public String name;

        public GuiType(String name) {
            this.name = name;
        }

        public void init() {
        }

        public void buttonEvent(GuiNpcButton button) {
        }

        public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
        }
    }

    class GuiTypeBoolean
    extends GuiType {
        private boolean bo;

        public GuiTypeBoolean(String name, boolean bo) {
            super(name);
            this.bo = bo;
        }

        @Override
        public void init() {
            GuiCreationExtra.this.addButton(new GuiNpcButtonYesNo((GuiNPCInterface)GuiCreationExtra.this, 11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 50, 60, 20, this.bo));
        }

        @Override
        public void buttonEvent(GuiNpcButton button) {
            if (button.id != 11) {
                return;
            }
            this.bo = ((GuiNpcButtonYesNo)button).getBoolean();
            if (this.name.equals("Child")) {
                GuiCreationExtra.this.playerdata.extra.putInt("Age", this.bo ? -24000 : 0);
                GuiCreationExtra.this.playerdata.clearEntity();
            } else {
                GuiCreationExtra.this.playerdata.extra.putBoolean(this.name, this.bo);
                GuiCreationExtra.this.playerdata.clearEntity();
            }
        }
    }

    class GuiTypeByte
    extends GuiType {
        private byte b;

        public GuiTypeByte(String name, byte b) {
            super(name);
            this.b = b;
        }

        @Override
        public void init() {
            GuiCreationExtra.this.addButton(new GuiButtonBiDirectional(GuiCreationExtra.this, 11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}, this.b));
        }

        @Override
        public void buttonEvent(GuiNpcButton button) {
            if (button.id != 11) {
                return;
            }
            GuiCreationExtra.this.playerdata.extra.putByte(this.name, (byte)button.getValue());
            GuiCreationExtra.this.playerdata.clearEntity();
        }
    }

    class GuiTypePixelmon
    extends GuiType {
        public GuiTypePixelmon(String name) {
            super(name);
        }

        @Override
        public void init() {
            GuiCustomScroll scroll = new GuiCustomScroll(GuiCreationExtra.this, 1);
            scroll.setSize(120, 200);
            scroll.guiLeft = GuiCreationExtra.this.guiLeft + 120;
            scroll.guiTop = GuiCreationExtra.this.guiTop + 50;
            GuiCreationExtra.this.addScroll(scroll);
            scroll.setList(PixelmonHelper.getPixelmonList());
            scroll.setSelected(PixelmonHelper.getName(GuiCreationExtra.this.entity));
        }

        @Override
        public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
            String name = scroll.getSelected();
            GuiCreationExtra.this.playerdata.clearEntity();
            GuiCreationExtra.this.playerdata.extra.putString("Name", name);
        }
    }
}
