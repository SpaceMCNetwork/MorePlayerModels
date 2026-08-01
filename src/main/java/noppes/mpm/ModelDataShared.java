/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 */
package noppes.mpm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import noppes.mpm.ModelEyeData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.parts.MpmPart;
import noppes.mpm.client.parts.MpmPartData;
import noppes.mpm.constants.BodyPart;
import noppes.mpm.constants.EnumParts;

public abstract class ModelDataShared {
    private static final int MAX_PARTS = 256;
    private static final int MAX_RESOURCE_LOCATION_LENGTH = 256;
    private static final int MAX_URL_LENGTH = 2048;
    private static final int MAX_DISPLAY_NAME_LENGTH = 256;
    public ModelPartConfig arm1 = new ModelPartConfig();
    public ModelPartConfig arm2 = new ModelPartConfig();
    public ModelPartConfig body = new ModelPartConfig();
    public ModelPartConfig leg1 = new ModelPartConfig();
    public ModelPartConfig leg2 = new ModelPartConfig();
    public ModelPartConfig head = new ModelPartConfig();
    protected ResourceLocation entityName = null;
    protected LivingEntity entity;
    public CompoundTag extra = new CompoundTag();
    public ListTag oldPartData = new ListTag();
    public List<MpmPartData> mpmParts = new ArrayList<MpmPartData>();
    public List<BodyPart> hiddenParts = new ArrayList<BodyPart>();
    public int wingMode = 0;
    public String url = "";
    public String displayName = "";

    public CompoundTag writeToNBT() {
        CompoundTag compound = new CompoundTag();
        if (this.entityName != null) {
            compound.putString("EntityName", this.entityName.toString());
        }
        compound.put("ArmsConfig", (Tag)this.arm1.writeToNBT());
        compound.put("Arms2Config", (Tag)this.arm2.writeToNBT());
        compound.put("BodyConfig", (Tag)this.body.writeToNBT());
        compound.put("LegsConfig", (Tag)this.leg1.writeToNBT());
        compound.put("Legs2Config", (Tag)this.leg2.writeToNBT());
        compound.put("HeadConfig", (Tag)this.head.writeToNBT());
        compound.put("ExtraData", (Tag)this.extra);
        compound.putInt("WingMode", this.wingMode);
        compound.putString("CustomSkinUrl", this.url);
        compound.putString("DisplayName", this.displayName);
        compound.put("Parts", (Tag)this.oldPartData);
        ListTag list = new ListTag();
        for (MpmPartData e : this.mpmParts) {
            if (e != null && e.partId != null) {
                list.add(e.getNbt());
            }
        }
        compound.put("NewParts", (Tag)list);
        return compound;
    }

    public void readFromNBT(CompoundTag compound) {
        String rl = limit(compound.getString("EntityName"), MAX_RESOURCE_LOCATION_LENGTH);
        this.setEntity(rl.isEmpty() ? null : ResourceLocation.tryParse(rl));
        this.arm1.readFromNBT(compound.getCompound("ArmsConfig"));
        this.arm2.readFromNBT(compound.getCompound("Arms2Config"));
        this.body.readFromNBT(compound.getCompound("BodyConfig"));
        this.leg1.readFromNBT(compound.getCompound("LegsConfig"));
        this.leg2.readFromNBT(compound.getCompound("Legs2Config"));
        this.head.readFromNBT(compound.getCompound("HeadConfig"));
        this.extra = compound.getCompound("ExtraData");
        this.wingMode = compound.getInt("WingMode") == 1 ? 1 : 0;
        this.url = limit(compound.getString("CustomSkinUrl"), MAX_URL_LENGTH);
        this.displayName = limit(compound.getString("DisplayName"), MAX_DISPLAY_NAME_LENGTH);
        ArrayList<MpmPartData> parts = new ArrayList<MpmPartData>();
        ListTag newParts = compound.getList("NewParts", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(newParts.size(), MAX_PARTS); ++i) {
            MpmPartData part = readPart(newParts.getCompound(i));
            if (part != null) {
                parts.add(part);
            }
        }
        this.mpmParts = parts;
        this.oldPartData = copyParts(compound.getList("Parts", Tag.TAG_COMPOUND));
        if (this.mpmParts.isEmpty()) {
            for (int i = 0; i < this.oldPartData.size(); ++i) {
                MpmPartData part = EnumParts.convertOldPart(this.oldPartData.getCompound(i));
                if (part != null && part.partId != null) {
                    this.mpmParts.add(part);
                }
            }
        }
        this.refreshParts();
        this.updateTransate();
    }

    private static String limit(String value, int maximumLength) {
        return value.length() > maximumLength ? value.substring(0, maximumLength) : value;
    }

    private static ListTag copyParts(ListTag source) {
        ListTag copy = new ListTag();
        for (int i = 0; i < Math.min(source.size(), MAX_PARTS); ++i) {
            copy.add(source.getCompound(i).copy());
        }
        return copy;
    }

    private static MpmPartData readPart(CompoundTag compound) {
        String id = limit(compound.getString("Id"), MAX_RESOURCE_LOCATION_LENGTH);
        ResourceLocation partId = ResourceLocation.tryParse(id);
        if (partId == null) {
            return null;
        }
        try {
            MpmPartData part = isEyePart(partId) ? new ModelEyeData() : new MpmPartData();
            part.setNbt(compound);
            if (part.partId == null) {
                return null;
            }
            MorePlayerModels.proxy.createMpmPartData(part);
            return part;
        } catch (RuntimeException ignored) {
            // A profile is user-controlled network data.  Drop one invalid
            // accessory instead of allowing it to take down a server/client.
            return null;
        }
    }

    private static boolean isEyePart(ResourceLocation id) {
        return id.equals(ModelEyeData.RESOURCE)
                || id.equals(ModelEyeData.RESOURCE_RIGHT)
                || id.equals(ModelEyeData.RESOURCE_LEFT);
    }

    public void updateTransate() {
        for (EnumParts part : EnumParts.values()) {
            float y;
            float x;
            ModelPartConfig body;
            ModelPartConfig config = this.getPartConfig(part);
            if (config == null) continue;
            if (part == EnumParts.HEAD) {
                config.setTranslate(0.0f, this.getBodyY(), 0.0f);
                continue;
            }
            if (part == EnumParts.ARM_LEFT) {
                body = this.getPartConfig(EnumParts.BODY);
                x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.0625f;
                y = this.getBodyY() + (1.0f - config.scaleY) * -0.125f;
                config.setTranslate(-x, y, 0.0f);
                if (config.notShared) continue;
                ModelPartConfig arm = this.getPartConfig(EnumParts.ARM_RIGHT);
                arm.copyValues(config);
                continue;
            }
            if (part == EnumParts.ARM_RIGHT) {
                body = this.getPartConfig(EnumParts.BODY);
                x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.0625f;
                y = this.getBodyY() + (1.0f - config.scaleY) * -0.125f;
                config.setTranslate(x, y, 0.0f);
                continue;
            }
            if (part == EnumParts.LEG_LEFT) {
                config.setTranslate(-(1.0f - config.scaleX) * 0.118f, this.getLegsY(), -(1.0f - config.scaleZ) * 0.00625f);
                if (config.notShared) continue;
                ModelPartConfig leg = this.getPartConfig(EnumParts.LEG_RIGHT);
                leg.copyValues(config);
                continue;
            }
            if (part == EnumParts.LEG_RIGHT) {
                config.setTranslate((1.0f - config.scaleX) * 0.118f, this.getLegsY(), -(1.0f - config.scaleZ) * 0.00625f);
                continue;
            }
            if (part != EnumParts.BODY) continue;
            config.setTranslate(0.0f, this.getBodyY(), 0.0f);
        }
    }

    public void setEntity(ResourceLocation resourceLocation) {
        this.entityName = resourceLocation;
        this.clearEntity();
        this.extra = new CompoundTag();
    }

    public ResourceLocation getEntityName() {
        return this.entityName;
    }

    public boolean hasEntity() {
        return this.entityName != null;
    }

    public float offsetY() {
        if (this.entity == null) {
            return -this.getBodyY();
        }
        return this.entity.getBbHeight() - 1.8f;
    }

    public void clearEntity() {
        this.entity = null;
    }

    public ModelPartConfig getPartConfig(EnumParts type) {
        if (type == EnumParts.BODY) {
            return this.body;
        }
        if (type == EnumParts.ARM_LEFT) {
            return this.arm1;
        }
        if (type == EnumParts.ARM_RIGHT) {
            return this.arm2;
        }
        if (type == EnumParts.LEG_LEFT) {
            return this.leg1;
        }
        if (type == EnumParts.LEG_RIGHT) {
            return this.leg2;
        }
        return this.head;
    }

    public float getBodyY() {
        if (this.entity != null) {
            return this.entity.getBbHeight();
        }
        return (1.0f - this.body.scaleY) * 0.75f + this.getLegsY();
    }

    public float getLegsY() {
        ModelPartConfig legs = this.leg1;
        if (this.leg1.notShared && this.leg2.scaleY > this.leg1.scaleY) {
            legs = this.leg2;
        }
        return (1.0f - legs.scaleY) * 0.75f;
    }

    public void refreshParts() {
        this.hiddenParts = this.mpmParts.stream().flatMap(part -> {
            if (part == null) {
                return Stream.empty();
            }
            MpmPart p = part.getPart();
            if (p != null) {
                return p.hiddenParts.stream();
            }
            return Stream.empty();
        }).distinct().collect(Collectors.toList());
    }
}
