/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.common.capabilities.Capability
 *  net.neoforged.neoforge.common.capabilities.CapabilityManager
 *  net.neoforged.neoforge.common.capabilities.CapabilityToken
 *  net.neoforged.neoforge.common.capabilities.ICapabilityProvider
 *  net.neoforged.neoforge.common.util.LazyOptional
 *  net.neoforged.neoforge.registries.ForgeRegistries
 */
package noppes.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.registries.BuiltInRegistries;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelDataShared;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.util.PixelmonHelper;

public class ModelData
extends ModelDataShared
implements INBTSerializable<CompoundTag> {
    public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);
    public boolean resourceInit = false;
    public boolean resourceLoaded = false;
    public ResourceLocation resourceLocation = null;
    public ResourceLocation resourceLoading = null;
    public boolean webapiActive = false;
    public boolean webapiInit = false;
    public Object textureObject = null;
    public ItemStack backItem = ItemStack.EMPTY;
    public int inLove = 0;
    public int animationTime = -1;
    public int modelType = 0;
    public EnumAnimation moveAnimation = EnumAnimation.IDLE;
    public EnumAnimation prevMoveAnimation = EnumAnimation.IDLE;
    public boolean startMoveAnimation = false;
    public EnumAnimation animation = EnumAnimation.NONE;
    public EnumAnimation prevAnimation = EnumAnimation.NONE;
    public boolean startAnimation = false;
    public int animationStart = 0;
    public float sleepRotation;
    public short soundType = 0;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    public Player player = null;
    public long lastEdited = System.currentTimeMillis();
    public UUID analyticsUUID = UUID.randomUUID();
    public String presetName = "Default";
    private static ModelData backup = new ModelData();

    @Override
    public synchronized CompoundTag writeToNBT() {
        CompoundTag compound = super.writeToNBT();
        compound.putShort("SoundType", this.soundType);
        compound.putInt("Animation", this.animation.ordinal());
        compound.putInt("MoveAnimation", this.moveAnimation.ordinal());
        compound.putLong("LastEdited", this.lastEdited);
        compound.putLong("ModelType", (long)this.modelType);
        compound.putString("PresetName", this.presetName);
        return compound;
    }

    @Override
    public synchronized void readFromNBT(CompoundTag compound) {
        String prevUrl = this.url;
        String prevName = this.displayName;
        super.readFromNBT(compound);
        this.soundType = compound.getShort("SoundType");
        this.lastEdited = compound.getLong("LastEdited");
        this.modelType = compound.getInt("ModelType");
        this.presetName = compound.getString("PresetName");
        if (this.player != null) {
            if (!this.hasEntity()) {
                this.player.getPersistentData().remove("MPMModel");
            } else {
                this.player.getPersistentData().putString("MPMModel", this.getEntityName().toString());
            }
        }
        this.setAnimation(compound.getInt("Animation"));
        this.setMoveAnimation(compound.getInt("MoveAnimation"));
        if (!prevUrl.equals(this.url)) {
            this.resourceInit = false;
            this.resourceLoaded = false;
            this.resourceLocation = null;
        }
        if (!prevName.equals(this.displayName) && this.player != null) {
            this.player.refreshDisplayName();
        }
    }

    public void setMoveAnimation(int i) {
        if (i < EnumAnimation.values().length) {
            this.setMoveAnimation(EnumAnimation.values()[i]);
        } else {
            this.setMoveAnimation(EnumAnimation.IDLE);
        }
    }

    public void setMoveAnimation(EnumAnimation ani) {
        this.startMoveAnimation = this.moveAnimation != ani;
        this.moveAnimation = ani;
    }

    public EnumAnimation getMoveAnimtion(AbstractClientPlayer player) {
        if (player.isPassenger()) {
            return EnumAnimation.SIT;
        }
        if (player.isSleeping()) {
            return EnumAnimation.SLEEP;
        }
        if (this.moveAnimation == EnumAnimation.IDLE && player.isCrouching()) {
            return EnumAnimation.CROUCH;
        }
        return this.moveAnimation;
    }

    public void setAnimation(int i) {
        if (i < EnumAnimation.values().length) {
            this.setAnimation(EnumAnimation.values()[i]);
        } else {
            this.setAnimation(EnumAnimation.NONE);
        }
    }

    public boolean isMovementAnimation(EnumAnimation ani) {
        return ani == EnumAnimation.SLEEP || ani == EnumAnimation.CRAWL || ani == EnumAnimation.CROUCH || ani == EnumAnimation.SIT || ani == EnumAnimation.DEATH || ani == EnumAnimation.WALK || ani == EnumAnimation.IDLE || ani == EnumAnimation.FLY_IDLE || ani == EnumAnimation.FLY;
    }

    public void setAnimation(EnumAnimation ani) {
        if (this.isMovementAnimation(ani)) {
            this.setMoveAnimation(ani);
            return;
        }
        this.animationTime = -1;
        this.animation = ani;
        this.lastEdited = System.currentTimeMillis();
        boolean bl = this.startAnimation = this.animation != ani;
        if (this.animation == EnumAnimation.WAVE) {
            this.animationTime = 80;
        }
        if (this.animation == EnumAnimation.YES || this.animation == EnumAnimation.NO) {
            this.animationTime = 60;
        }
        this.animationStart = this.player == null || ani == EnumAnimation.NONE ? -1 : this.player.tickCount;
    }

    public LivingEntity getEntity(Player player) {
        if (!this.hasEntity()) {
            return null;
        }
        if (this.entity == null) {
            try {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.getEntityName());
                this.entity = type == null ? null : (LivingEntity)type.create(player.level());
                if (this.entity == null) {
                    return null;
                }
                CompoundTag comp = new CompoundTag();
                this.entity.addAdditionalSaveData(comp);
                if (PixelmonHelper.isPixelmon((Entity)this.entity) && player.level().isClientSide && !this.extra.contains("Name")) {
                    this.extra.putString("Name", "Abra");
                }
                comp = comp.merge(this.extra);
                this.entity.readAdditionalSaveData(comp);
                this.entity.setInvulnerable(true);
                this.entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)player.getMaxHealth());
                this.entity.setItemSlot(EquipmentSlot.MAINHAND, player.getMainHandItem());
                this.entity.setItemSlot(EquipmentSlot.OFFHAND, player.getOffhandItem());
                this.entity.setItemSlot(EquipmentSlot.HEAD, player.getInventory().getItem(3));
                this.entity.setItemSlot(EquipmentSlot.CHEST, player.getInventory().getItem(2));
                this.entity.setItemSlot(EquipmentSlot.LEGS, player.getInventory().getItem(1));
                this.entity.setItemSlot(EquipmentSlot.FEET, player.getInventory().getItem(0));
                this.resourceInit = false;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.entity;
    }

    public ModelData copy() {
        ModelData data = new ModelData();
        data.readFromNBT(this.writeToNBT());
        data.resourceLoaded = this.resourceLoaded;
        data.player = this.player;
        return data;
    }

    @Override
    public void clearEntity() {
        super.clearEntity();
        this.resourceInit = false;
    }

    public float getOffsetCamera(Player player) {
        if (!MorePlayerModels.EnablePOV) {
            return 0.0f;
        }
        float offset = -this.offsetY();
        if (this.entity == null) {
            if (this.moveAnimation == EnumAnimation.SIT) {
                offset += 0.5f - this.getLegsY();
            }
            if (this.moveAnimation == EnumAnimation.SLEEP) {
                offset = 1.18f;
            }
            if (this.moveAnimation == EnumAnimation.CRAWL) {
                offset = 0.8f;
            }
        }
        if (offset < -0.2f && this.isBlocked(player)) {
            offset = -0.2f;
        }
        return offset;
    }

    private boolean isBlocked(Player player) {
        return !player.level().isEmptyBlock(new BlockPos((Vec3i)BlockPos.containing((Position)player.position())).above(2));
    }

    public void save() {
        if (this.player == null) {
            return;
        }
        Player player = this.player;
        saveExecutor.submit(() -> {
            try {
                Object filename = player.getUUID().toString().toLowerCase();
                if (((String)filename).isEmpty()) {
                    filename = "noplayername";
                }
                filename = (String)filename + ".dat";
                File file = new File(MorePlayerModels.dir, (String)filename + "_new");
                File file1 = new File(MorePlayerModels.dir, (String)filename + "_old");
                File file2 = new File(MorePlayerModels.dir, (String)filename);
                NbtIo.writeCompressed((CompoundTag)this.writeToNBT(), (OutputStream)new FileOutputStream(file));
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
            }
        });
    }

    public static ModelData get(Player player) {
        ModelData data = player.getData(MPMRegistries.MODEL_DATA);
        if (data.player == null) {
            data.player = player;
            data.backItem = player.getInventory().getItem(0);
            data.loadPlayerData(player.getUUID());
        }
        return data;
    }

    private void loadPlayerData(UUID id) {
        saveExecutor.submit(() -> {
            Object filename = id.toString();
            if (((String)filename).isEmpty()) {
                filename = "noplayername";
            }
            filename = (String)filename + ".dat";
            try {
                File file = new File(MorePlayerModels.dir, (String)filename);
                if (!file.exists()) {
                    return;
                }
                CompoundTag compound = NbtIo.readCompressed(new FileInputStream(file), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
                this.readFromNBT(compound);
                return;
            }
            catch (Exception e) {
                try {
                    LogWriter.except(e);
                    try {
                        File file = new File(MorePlayerModels.dir, (String)filename + "_old");
                        if (file.exists()) {
                            return;
                        }
                        CompoundTag compound = NbtIo.readCompressed(new FileInputStream(file), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
                        this.readFromNBT(compound);
                    }
                    catch (Exception e2) {
                        LogWriter.except(e2);
                    }
                }
                catch (Exception e3) {
                    LogWriter.except(e3);
                }
            }
        });
    }

    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        return writeToNBT();
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag tag) {
        readFromNBT(tag);
    }
}
