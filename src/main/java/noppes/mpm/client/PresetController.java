/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.player.Player
 */
package noppes.mpm.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.client.Preset;

public class PresetController {
    public HashMap<String, Preset> presets = new HashMap();
    private File dir;
    public static PresetController instance;

    public PresetController(File dir) {
        instance = this;
        this.dir = dir;
    }

    public Preset getPreset(String username) {
        if (this.presets.isEmpty()) {
            this.load();
        }
        if (username == null || username.isEmpty()) {
            return null;
        }
        return this.presets.get(username.toLowerCase());
    }

    public void load() {
        CompoundTag compound = this.loadPreset();
        HashMap<String, Preset> presets = new HashMap<String, Preset>();
        if (compound != null) {
            ListTag list = compound.getList("Presets", 10);
            for (int i = 0; i < list.size(); ++i) {
                CompoundTag comp = list.getCompound(i);
                Preset preset = new Preset();
                preset.readFromNBT(comp);
                presets.put(preset.name.toLowerCase(), preset);
            }
            if (compound.contains("PresetSelected")) {
                ModelData data = ModelData.get((Player)Minecraft.getInstance().player);
                if (data.presetName.isEmpty()) {
                    data.presetName = compound.getString("PresetSelected");
                }
            }
        }
        if (presets.isEmpty()) {
            Preset preset = new Preset();
            preset.data = ModelData.get((Player)Minecraft.getInstance().player);
            preset.name = "Default";
            preset.menu = true;
            presets.put("default", preset);
            ModelData data = new ModelData();
            preset = new Preset();
            preset.name = "Normal";
            preset.data = data;
            preset.menu = true;
            presets.put("normal", preset);
        }
        this.presets = presets;
    }

    private CompoundTag loadPreset() {
        String filename = "presets.dat";
        try {
            File file = new File(this.dir, filename);
            if (!file.exists()) {
                return null;
            }
            return NbtIo.readCompressed(new FileInputStream(file), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
        catch (Exception e) {
            LogWriter.except(e);
            try {
                File file = new File(this.dir, filename + "_old");
                if (!file.exists()) {
                    return null;
                }
                return NbtIo.readCompressed(new FileInputStream(file), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
            }
            catch (Exception e2) {
                LogWriter.except(e2);
                return null;
            }
        }
    }

    public void save() {
        CompoundTag compound = new CompoundTag();
        ListTag list = new ListTag();
        for (Preset preset : this.presets.values()) {
            list.add(preset.writeToNBT());
        }
        compound.put("Presets", (Tag)list);
        this.savePreset(compound);
    }

    private void savePreset(CompoundTag compound) {
        String filename = "presets.dat";
        try {
            File file = new File(this.dir, filename + "_new");
            File file1 = new File(this.dir, filename + "_old");
            File file2 = new File(this.dir, filename);
            NbtIo.writeCompressed((CompoundTag)compound, (OutputStream)new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
            e.printStackTrace();
        }
    }

    public void addPreset(Preset preset) {
        this.presets.put(preset.name.toLowerCase(), preset);
        this.save();
    }

    public void removePreset(String preset) {
        if (preset == null) {
            return;
        }
        this.presets.remove(preset.toLowerCase());
        this.save();
    }
}
