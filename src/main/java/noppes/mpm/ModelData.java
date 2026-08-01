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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    public static ExecutorService saveExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "MorePlayerModels profile IO");
        thread.setDaemon(true);
        return thread;
    });
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
    private long dataRevision;
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
        this.soundType = (short)Math.max(0, Math.min(3, compound.getShort("SoundType")));
        this.lastEdited = compound.getLong("LastEdited");
        this.modelType = Math.max(0, Math.min(2, compound.getInt("ModelType")));
        this.presetName = limitPresetName(compound.getString("PresetName"));
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
        ++this.dataRevision;
    }

    private static String limitPresetName(String name) {
        return name.length() > 128 ? name.substring(0, 128) : name;
    }

    public void setMoveAnimation(int i) {
        if (i >= 0 && i < EnumAnimation.values().length) {
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
        if (i >= 0 && i < EnumAnimation.values().length) {
            this.setAnimation(EnumAnimation.values()[i]);
        } else {
            this.setAnimation(EnumAnimation.NONE);
        }
    }

    public boolean isMovementAnimation(EnumAnimation ani) {
        return ani == EnumAnimation.SLEEP || ani == EnumAnimation.CRAWL || ani == EnumAnimation.CROUCH || ani == EnumAnimation.SIT || ani == EnumAnimation.DEATH || ani == EnumAnimation.WALK || ani == EnumAnimation.IDLE || ani == EnumAnimation.FLY_IDLE || ani == EnumAnimation.FLY;
    }

    public void setAnimation(EnumAnimation ani) {
        if (ani == null) {
            ani = EnumAnimation.NONE;
        }
        if (this.isMovementAnimation(ani)) {
            this.setMoveAnimation(ani);
            return;
        }
        this.animationTime = -1;
        this.startAnimation = this.animation != ani;
        this.animation = ani;
        this.lastEdited = System.currentTimeMillis();
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
        CompoundTag snapshot = this.writeToNBT();
        String filename = this.player.getUUID().toString().toLowerCase();
        if (filename.isEmpty()) {
            filename = "noplayername";
        }
        String profileFilename = filename + ".dat";
        saveExecutor.submit(() -> {
            try {
                saveProfile(snapshot, profileFilename);
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
            data.backItem = player.getInventory().getItem(0).copy();
            data.loadPlayerData(player.getUUID());
        }
        return data;
    }

    private void loadPlayerData(UUID id) {
        final long expectedRevision = this.dataRevision;
        final Player profilePlayer = this.player;
        saveExecutor.submit(() -> {
            String filename = id.toString();
            if (filename.isEmpty()) {
                filename = "noplayername";
            }
            filename = filename + ".dat";
            CompoundTag compound = null;
            try {
                Path current = new File(MorePlayerModels.dir, filename).toPath();
                Path backup = new File(MorePlayerModels.dir, filename + "_old").toPath();
                if (Files.exists(current)) {
                    compound = readProfile(current);
                } else if (Files.exists(backup)) {
                    compound = readProfile(backup);
                }
            }
            catch (Exception e) {
                LogWriter.except(e);
                try {
                    Path backup = new File(MorePlayerModels.dir, filename + "_old").toPath();
                    if (Files.exists(backup)) {
                        compound = readProfile(backup);
                    }
                } catch (Exception backupException) {
                    LogWriter.except(backupException);
                }
            }
            if (compound == null || profilePlayer == null) {
                return;
            }
            CompoundTag profile = compound;
            MorePlayerModels.proxy.executor(profilePlayer, () -> {
                synchronized (ModelData.this) {
                    // Never allow a late disk read to overwrite a profile that
                    // was already updated through a client packet or GUI.
                    if (ModelData.this.dataRevision == expectedRevision) {
                        ModelData.this.readFromNBT(profile);
                    }
                }
            });
        });
    }

    private static CompoundTag readProfile(Path profile) throws Exception {
        try (InputStream input = Files.newInputStream(profile)) {
            return NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
    }

    private static void saveProfile(CompoundTag profile, String filename) throws Exception {
        Path temporary = new File(MorePlayerModels.dir, filename + "_new").toPath();
        Path backup = new File(MorePlayerModels.dir, filename + "_old").toPath();
        Path current = new File(MorePlayerModels.dir, filename).toPath();
        try (OutputStream output = new FileOutputStream(temporary.toFile())) {
            NbtIo.writeCompressed(profile, output);
        }
        if (Files.exists(current)) {
            Files.move(current, backup, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            Files.move(temporary, current, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, current, StandardCopyOption.REPLACE_EXISTING);
        }
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
