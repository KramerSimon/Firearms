package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.EBFMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EBFControllerBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int MAX_PROCESS_TIME = 400;
    public static final int FE_PER_TICK = 200;
    public static final int CAPACITY = 100_000;
    public static final int MAX_RECEIVE = 1_000;

    private int progress = 0;
    private boolean structureValid = false;

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
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
                case 2 -> progress;
                case 3 -> MAX_PROCESS_TIME;
                case 4 -> structureValid ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 2) progress = value;
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public EBFControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EBF_CONTROLLER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.ebf_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EBFMenu(containerId, playerInventory, inventory, data);
    }

    private boolean stackIs(ItemStack stack, String id) {
        if (stack.isEmpty()) return false;
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id);
    }

    private ItemStack getRecipeOutput(ItemStack in0, ItemStack in1) {
        if (in0.isEmpty() || in1.isEmpty()) return ItemStack.EMPTY;
        if (!stackIs(in1, "firearms:coal_coke")) return ItemStack.EMPTY;
        if (stackIs(in0, "minecraft:raw_iron"))     return new ItemStack(ModItems.STEEL_INGOT.get(), 2);
        if (stackIs(in0, "minecraft:iron_ingot"))   return new ItemStack(ModItems.STEEL_INGOT.get(), 3);
        if (stackIs(in0, "firearms:steel_ingot"))   return new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 1);
        if (stackIs(in0, "minecraft:raw_gold"))     return new ItemStack(Items.GOLD_INGOT, 2);
        if (stackIs(in0, "minecraft:copper_ingot"))      return new ItemStack(ModItems.CARBON_STEEL.get(), 1);
        if (stackIs(in0, "firearms:tungsten_ore_raw"))   return new ItemStack(ModItems.TUNGSTEN_INGOT.get(), 1);
        return ItemStack.EMPTY;
    }

    private boolean canOutput(ItemStack result, ItemStack outputSlot) {
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    public void serverTick() {
        if (level == null) return;

        if (level.getGameTime() % 40 == 0) checkStructure();

        if (!structureValid) {
            if (progress > 0) { progress = 0; setChanged(); }
            return;
        }

        ItemStack in0 = inventory.getStackInSlot(0);
        ItemStack in1 = inventory.getStackInSlot(1);
        ItemStack out = inventory.getStackInSlot(2);
        ItemStack result = getRecipeOutput(in0, in1);

        boolean canProcess = !result.isEmpty()
                && canOutput(result, out)
                && energy.getEnergyStored() >= FE_PER_TICK;

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= MAX_PROCESS_TIME) {
                in0.shrink(1);
                in1.shrink(1);
                if (out.isEmpty()) {
                    inventory.setStackInSlot(2, result.copy());
                } else {
                    out.grow(result.getCount());
                }
                progress = 0;
            }
            setChanged();
        } else if (progress > 0) {
            progress = 0;
            setChanged();
        }
    }

    public boolean checkStructure() {
        if (level == null) return false;
        for (int dx = 0; dx <= 2; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                if (dx == 1 && dz == 1) continue; // center is not a valid controller position
                BlockPos origin = worldPosition.offset(-dx, 0, -dz);
                if (isValidStructureAt(origin)) {
                    structureValid = true;
                    return true;
                }
            }
        }
        structureValid = false;
        return false;
    }

    private boolean isValidStructureAt(BlockPos origin) {
        Block base  = ModBlocks.EBF_BASE.get();
        Block wall  = ModBlocks.EBF_WALL.get();
        Block top   = ModBlocks.EBF_TOP.get();
        Block ctrl  = ModBlocks.EBF_CONTROLLER.get();
        Block ePort = ModBlocks.ENERGY_PORT.get();
        Block fPort = ModBlocks.FLUID_PORT.get();

        for (int x = 0; x < 3; x++)
            for (int z = 0; z < 3; z++) {
                if (!isAnyOf(origin.offset(x, 0, z), base, ctrl, ePort, fPort)) return false;
            }

        for (int y = 1; y <= 2; y++)
            for (int x = 0; x < 3; x++)
                for (int z = 0; z < 3; z++) {
                    if (x == 1 && z == 1) continue; // hollow interior
                    if (!isAnyOf(origin.offset(x, y, z), wall, ePort, fPort)) return false;
                }

        for (int x = 0; x < 3; x++)
            for (int z = 0; z < 3; z++) {
                if (!isAnyOf(origin.offset(x, 3, z), top, ePort, fPort)) return false;
            }

        return true;
    }

    private boolean isAnyOf(BlockPos pos, Block... valid) {
        Block b = level.getBlockState(pos).getBlock();
        for (Block v : valid) if (b == v) return true;
        return false;
    }

    public boolean isStructureValid() { return structureValid; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
    }
}
