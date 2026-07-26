/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 */
package noppes.mpm;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ModelPartData {
    private static Map<String, ResourceLocation> resources = new HashMap<String, ResourceLocation>();
    public int color = 0xFFFFFF;
    public int colorPattern = 0xFFFFFF;
    public byte type = 0;
    public byte pattern = 0;
    public boolean playerTexture = false;
    public String name;
    private ResourceLocation location;

    public ModelPartData(String name) {
        this.name = name;
    }

    public CompoundTag writeToNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putByte("Type", this.type);
        compound.putInt("Color", this.color);
        compound.putBoolean("PlayerTexture", this.playerTexture);
        compound.putByte("Pattern", this.pattern);
        return compound;
    }

    public void readFromNBT(CompoundTag compound) {
        this.type = compound.getByte("Type");
        this.color = compound.getInt("Color");
        this.playerTexture = compound.getBoolean("PlayerTexture");
        this.pattern = compound.getByte("Pattern");
        this.location = null;
    }

    public ResourceLocation getResource() {
        if (this.location != null) {
            return this.location;
        }
        String texture = this.name + "/" + this.type;
        this.location = resources.get(texture);
        if (this.location != null) {
            return this.location;
        }
        this.location = ResourceLocation.parse("moreplayermodels:textures/" + texture + ".png");
        resources.put(texture, this.location);
        return this.location;
    }

    public void setType(int type) {
        this.type = (byte)type;
        this.location = null;
    }

    public String toString() {
        return "Color: " + this.color + " Type: " + this.type;
    }

    public String getColor() {
        String str = Integer.toHexString(this.color);
        while (((String)str).length() < 6) {
            str = "0" + str;
        }
        return str;
    }
}
