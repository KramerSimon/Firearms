package com.sio.firearms.entity;

import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MolotovEntity extends ThrowableItemProjectile {

    public MolotovEntity(EntityType<? extends MolotovEntity> type, Level level) {
        super(type, level);
    }

    public MolotovEntity(Level level, LivingEntity thrower) {
        super(ModEntities.MOLOTOV.get(), thrower, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MOLOTOV_COCKTAIL.get();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide()) {
            Vec3 pos = position();
            BlockPos center = BlockPos.containing(pos);

            // Glass break + fire ignition sounds
            level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.5f, 0.9f + level().random.nextFloat() * 0.2f);
            level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, 0.7f + level().random.nextFloat() * 0.3f);

            // Ignite a 3×3 footprint
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos fp = center.offset(dx, 0, dz);
                    if (level().getBlockState(fp).isAir()
                            && !level().getBlockState(fp.below()).isAir()
                            && BaseFireBlock.canBePlacedAt(level(), fp, Direction.UP)) {
                        level().setBlock(fp, BaseFireBlock.getState(level(), fp), 11);
                    }
                }
            }

            // Spawn persistent fire patch entity
            FirePatchEntity patch = new FirePatchEntity(level(), pos.x, pos.y + 0.1, pos.z);
            level().addFreshEntity(patch);

            discard();
        }
    }
}
