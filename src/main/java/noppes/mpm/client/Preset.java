/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 */
package noppes.mpm.client;

import java.util.HashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import noppes.mpm.ModelData;
import noppes.mpm.constants.EnumParts;

public class Preset {
    public ModelData data = new ModelData();
    public String name;
    public boolean menu = false;

    public CompoundTag writeToNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putString("PresetName", this.name);
        compound.put("PresetData", (Tag)this.data.writeToNBT());
        compound.putBoolean("PresetMenu", this.menu);
        return compound;
    }

    public void readFromNBT(CompoundTag compound) {
        this.name = compound.getString("PresetName");
        this.data.readFromNBT(compound.getCompound("PresetData"));
        this.menu = compound.getBoolean("PresetMenu");
    }

    public static HashMap<String, Preset> GetDefault() {
        HashMap<String, Preset> presets = new HashMap<String, Preset>();
        Preset preset = new Preset();
        ModelData data = new ModelData();
        preset = new Preset();
        preset.name = "Normal";
        preset.data = data;
        preset.menu = true;
        presets.put("normal", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Elf Male";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.85f, 1.15f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.85f, 1.15f);
        data.getPartConfig(EnumParts.BODY).setScale(0.85f, 1.15f);
        data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.95f);
        presets.put("elf male", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Elf Female";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8f, 1.05f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 1.05f);
        data.getPartConfig(EnumParts.BODY).setScale(0.8f, 1.05f);
        data.getPartConfig(EnumParts.HEAD).setScale(0.8f, 0.85f);
        presets.put("elf female", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Dwarf Male";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.1f, 0.7f, 0.9f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.9f, 0.7f);
        data.getPartConfig(EnumParts.BODY).setScale(1.2f, 0.7f, 1.5f);
        data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.85f);
        presets.put("dwarf male", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Dwarf Female";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.9f, 0.65f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.9f, 0.65f);
        data.getPartConfig(EnumParts.BODY).setScale(1.0f, 0.65f, 1.1f);
        data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.85f);
        presets.put("dwarf female", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Orc Male";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.2f, 1.05f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.2f, 1.05f);
        data.getPartConfig(EnumParts.BODY).setScale(1.4f, 1.1f, 1.5f);
        data.getPartConfig(EnumParts.HEAD).setScale(1.2f, 1.1f);
        presets.put("orc male", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Orc Female";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.1f, 1.0f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.1f, 1.0f);
        data.getPartConfig(EnumParts.BODY).setScale(1.1f, 1.0f, 1.25f);
        presets.put("orc female", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Human Male";
        preset.data = data;
        presets.put("human male", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Human Female";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92f, 0.92f);
        data.getPartConfig(EnumParts.HEAD).setScale(0.95f, 0.95f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.92f);
        data.getPartConfig(EnumParts.BODY).setScale(0.92f, 0.92f);
        presets.put("human female", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Cat Male";
        preset.data = data;
        presets.put("cat male", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Cat Female";
        preset.data = data;
        data.getPartConfig(EnumParts.HEAD).setScale(0.95f, 0.95f);
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92f, 0.92f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.92f);
        data.getPartConfig(EnumParts.BODY).setScale(0.92f, 0.92f);
        presets.put("cat female", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Wolf Male";
        preset.data = data;
        presets.put("wolf male", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Wolf Female";
        preset.data = data;
        data.getPartConfig(EnumParts.HEAD).setScale(0.95f, 0.95f);
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92f, 0.92f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.92f);
        data.getPartConfig(EnumParts.BODY).setScale(0.92f, 0.92f);
        presets.put("wolf female", preset);
        data = new ModelData();
        preset = new Preset();
        preset.name = "Enderchibi";
        preset.data = data;
        data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.65f, 0.75f);
        data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5f, 1.45f);
        presets.put("enderchibi", preset);
        return presets;
    }
}

