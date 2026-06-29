package com.sio.firearms.block;

import com.sio.firearms.config.FirearmsConfig;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.menu.AutoTurretMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class AutoTurretBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final int CAPACITY = 10_000;
    private static final int MAX_RECEIVE = 500;
    private static final int ENERGY_PER_SHOT = 20;
    private static final int SCAN_RANGE = 16;
    private static final int FIRE_COOLDOWN = 20;
    private static final float BULLET_DAMAGE = 8.0f;
    private static final float BULLET_SPEED = 3.0f;

    private int cooldown = 0;
    private boolean active = false;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.BULLET.get());
        }

        @Override
        public int getSlotLimit(int slot) {
            return 256;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> inventory.getStackInSlot(0).getCount();
                case 3 -> active ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 3) active = value == 1;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AutoTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_TURRET.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.auto_turret");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoTurretMenu(containerId, playerInventory, inventory, data);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        if (cooldown > 0) {
            cooldown--;
            changed = true;
        }

        boolean wasActive = active;
        active = hasAmmo() && energy.getEnergyStored() >= ENERGY_PER_SHOT;

        if (active && cooldown <= 0) {
            Entity target = findTarget();
            if (target != null) {
                shootAt(target);
                cooldown = FIRE_COOLDOWN;
                changed = true;
            }
        }

        if (wasActive != active) changed = true;
        if (changed) setChanged();
    }

    private boolean hasAmmo() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    private Entity findTarget() {
        double range = FirearmsConfig.AUTO_TURRET_RANGE.get();
        AABB scanBox = new AABB(worldPosition).inflate(range);
        List<Mob> hostiles = level.getEntitiesOfClass(Mob.class, scanBox,
                entity -> entity instanceof Enemy && entity.isAlive());

        if (hostiles.isEmpty()) return null;

        Vec3 turretPos = Vec3.atCenterOf(worldPosition);
        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Mob mob : hostiles) {
            double dist = mob.distanceToSqr(turretPos);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = mob;
            }
        }
        return nearest;
    }

    private void shootAt(Entity target) {
        Vec3 turretPos = Vec3.atCenterOf(worldPosition).add(0, 0.5, 0);
        Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2.0, 0);
        Vec3 direction = targetPos.subtract(turretPos).normalize();

        BulletEntity bullet = new BulletEntity(level, null, BULLET_DAMAGE);
        bullet.setPos(turretPos.x, turretPos.y, turretPos.z);
        bullet.setDeltaMovement(direction.scale(BULLET_SPEED));
        level.addFreshEntity(bullet);

        energy.extractEnergy(ENERGY_PER_SHOT, false);
        inventory.getStackInSlot(0).shrink(1);

        level.playSound(null, worldPosition, ModSounds.RIFLE_SHOOT.get(), SoundSource.BLOCKS, 0.5f, 1.2f);

        Direction facing = Direction.getNearest(direction.x, 0, direction.z);
        BlockState state = getBlockState().setValue(AutoTurretBlock.FACING, facing);
        level.setBlock(worldPosition, state, 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        cooldown = tag.getInt("Cooldown");
    }
}
