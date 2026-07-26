/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 */
package noppes.mpm;

import net.minecraft.nbt.CompoundTag;
import noppes.mpm.MorePlayerModels;

public class ModelPartConfig {
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    public float transX = 0.0f;
    public float transY = 0.0f;
    public float transZ = 0.0f;
    public boolean notShared = false;

    public CompoundTag writeToNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putFloat("ScaleX", this.scaleX);
        compound.putFloat("ScaleY", this.scaleY);
        compound.putFloat("ScaleZ", this.scaleZ);
        compound.putFloat("TransX", this.transX);
        compound.putFloat("TransY", this.transY);
        compound.putFloat("TransZ", this.transZ);
        compound.putBoolean("NotShared", this.notShared);
        return compound;
    }

    public void readFromNBT(CompoundTag compound) {
        this.scaleX = this.checkValue(compound.getFloat("ScaleX"), MorePlayerModels.ScaleSizeMin, MorePlayerModels.ScaleSizeMax);
        this.scaleY = this.checkValue(compound.getFloat("ScaleY"), MorePlayerModels.ScaleSizeMin, MorePlayerModels.ScaleSizeMax);
        this.scaleZ = this.checkValue(compound.getFloat("ScaleZ"), MorePlayerModels.ScaleSizeMin, MorePlayerModels.ScaleSizeMax);
        this.transX = this.checkValue(compound.getFloat("TransX"), -1.0f, 1.0f);
        this.transY = this.checkValue(compound.getFloat("TransY"), -1.0f, 1.0f);
        this.transZ = this.checkValue(compound.getFloat("TransZ"), -1.0f, 1.0f);
        this.notShared = compound.getBoolean("NotShared");
    }

    public String toString() {
        return "ScaleX: " + this.scaleX + " - ScaleY: " + this.scaleY + " - ScaleZ: " + this.scaleZ;
    }

    public void setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
    }

    public void setScale(float x, float y) {
        this.scaleZ = this.scaleX = x;
        this.scaleY = y;
    }

    public float checkValue(float given, float min, float max) {
        if (!Float.isFinite(min) || min <= 0.0f) {
            min = 0.2f;
        }
        if (!Float.isFinite(max) || max < min) {
            max = min;
        }
        if (!Float.isFinite(given)) {
            return min;
        }
        if (given < min) {
            return min;
        }
        if (given > max) {
            return max;
        }
        return given;
    }

    public void setTranslate(float transX, float transY, float transZ) {
        this.transX = transX;
        this.transY = transY;
        this.transZ = transZ;
    }

    public void copyValues(ModelPartConfig config) {
        this.scaleX = config.scaleX;
        this.scaleY = config.scaleY;
        this.scaleZ = config.scaleZ;
        this.transX = config.transX;
        this.transY = config.transY;
        this.transZ = config.transZ;
    }
}
