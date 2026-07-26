/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package noppes.mpm.client;

import java.util.ArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityFakeLiving
extends LivingEntity {
    public EntityFakeLiving(Level level) {
        super(EntityType.SHEEP, level);
    }

    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<ItemStack>();
    }

    public ItemStack getItemBySlot(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    public void setItemSlot(EquipmentSlot equipmentSlotType, ItemStack itemStack) {
    }

    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }
}

