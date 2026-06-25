package com.sio.firearms.block;

import com.sio.firearms.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class LandMineBlockEntity extends BlockEntity {

    private UUID placerUUID = null;
    private int placerImmuneTicks = 60; // 3-second immunity after placement
    private int armingTicks = 0;

    public LandMineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAND_MINE.get(), pos, state);
    }

    public void setPlacer(UUID uuid) {
        this.placerUUID = uuid;
        setChanged();
    }

    public boolean isPlacerImmune(Entity entity) {
        if (placerUUID == null || placerImmuneTicks <= 0) return false;
        return entity instanceof Player player && player.getUUID().equals(placerUUID);
    }

    public void serverTick() {
        if (placerImmuneTicks > 0) placerImmuneTicks--;

        if (armingTicks < 40) {
            armingTicks++;
            if (armingTicks == 40) {
                level.setBlock(worldPosition, getBlockState().setValue(LandMineBlock.ARMED, true), 3);
                level.playSound(null, worldPosition, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5f, 1.5f);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (placerUUID != null) tag.putUUID("placer", placerUUID);
        tag.putInt("placerImmuneTicks", placerImmuneTicks);
        tag.putInt("armingTicks", armingTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("placer")) placerUUID = tag.getUUID("placer");
        placerImmuneTicks = tag.getInt("placerImmuneTicks");
        armingTicks = tag.getInt("armingTicks");
    }
}
