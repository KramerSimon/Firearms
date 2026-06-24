package com.sio.firearms.item;

import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class BatteryItem extends Item {

    public static final int CAPACITY = 100_000;
    public static final int MAX_TRANSFER = 1_000;

    public BatteryItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        IEnergyStorage blockEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, face);
        if (blockEnergy != null && blockEnergy.canReceive()) {
            ItemStack stack = context.getItemInHand();
            ComponentEnergyStorage battery = new ComponentEnergyStorage(
                    stack, ModDataComponents.ENERGY.get(), CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
            int toTransfer = battery.extractEnergy(MAX_TRANSFER, true);
            if (toTransfer > 0) {
                int received = blockEnergy.receiveEnergy(toTransfer, false);
                battery.extractEnergy(received, false);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int stored = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
        tooltipComponents.add(Component.literal("Energy: " + stored + " / " + CAPACITY + " FE"));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int stored = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
        return Math.round(13.0f * stored / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF0000;
    }
}
