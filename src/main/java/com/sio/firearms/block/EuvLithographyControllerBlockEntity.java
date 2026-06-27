package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.EuvLithographyMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EuvLithographyControllerBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int CAPACITY = 5_000_000;
    public static final int MAX_RECEIVE = 10_000;
    public static final int FE_PER_TICK = 50_000;
    public static final int PROCESS_TIME = 1200;

    private int progress = 0;
    private boolean structureValid = false;
    private boolean cleanRoom = false;

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank photoresistTank = new FluidTank(10_000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            if (stack.isEmpty()) return false;
            return BuiltInRegistries.FLUID.getKey(stack.getFluid())
                    .toString().equals("firearms:photoresist_still");
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                case 4 -> structureValid ? 1 : 0;
                case 5 -> cleanRoom ? 1 : 0;
                case 6 -> photoresistTank.getFluidAmount();
                case 7 -> 10_000;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) { if (index == 2) progress = value; }
        @Override
        public int getCount() { return 8; }
    };

    public EuvLithographyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EUV_LITHOGRAPHY_CONTROLLER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() { return inventory; }
    public FluidTank getPhotoresistTank()  { return photoresistTank; }
    public boolean isStructureValid()      { return structureValid; }
    public boolean isCleanRoom()           { return cleanRoom; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.euv_lithography_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EuvLithographyMenu(id, inv, inventory, data);
    }

    private boolean stackIs(ItemStack stack, String id) {
        if (stack.isEmpty()) return false;
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id);
    }

    private boolean canOutput(ItemStack result, ItemStack outputSlot) {
        if (outputSlot.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    public void serverTick() {
        if (level == null) return;
        if (level.getGameTime() % 40 == 0) checkStructure();
        if (!structureValid || !cleanRoom) {
            if (progress > 0) { progress = 0; setChanged(); }
            return;
        }

        ItemStack in0  = inventory.getStackInSlot(0);
        ItemStack mask = inventory.getStackInSlot(1);
        ItemStack out  = inventory.getStackInSlot(2);
        ItemStack result = new ItemStack(ModItems.PATTERNED_WAFER.get());

        boolean hasMask = !mask.isEmpty() && stackIs(mask, "firearms:photomask")
                && mask.getDamageValue() < mask.getMaxDamage();
        boolean hasResist = photoresistTank.getFluidAmount() >= 500;
        boolean canProcess = stackIs(in0, "firearms:coated_wafer") && hasMask && hasResist
                && canOutput(result, out) && energy.getEnergyStored() >= FE_PER_TICK;

        if (canProcess) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            if (progress >= PROCESS_TIME) {
                in0.shrink(1);
                photoresistTank.drain(500, IFluidHandler.FluidAction.EXECUTE);
                mask.setDamageValue(mask.getDamageValue() + 1);
                if (mask.getDamageValue() >= mask.getMaxDamage()) inventory.setStackInSlot(1, ItemStack.EMPTY);
                if (out.isEmpty()) inventory.setStackInSlot(2, result.copy());
                else out.grow(1);
                progress = 0;
            }
            setChanged();

            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 5 == 0) {
                double cx = worldPosition.getX() + 0.5;
                double cy = worldPosition.getY() + 2.5;
                double cz = worldPosition.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.WITCH, cx, cy, cz, 3, 0.5, 0.5, 0.5, 0.05);
            }
        } else if (progress > 0) { progress = 0; setChanged(); }
    }

    public boolean checkStructure() {
        if (level == null) return false;
        LOGGER.info("[EUV] checkStructure() called, controller at {}", worldPosition);
        for (int ox = 0; ox <= 4; ox++) {
            for (int oz = 0; oz <= 4; oz++) {
                if (ox > 0 && ox < 4 && oz > 0 && oz < 4) continue;
                BlockPos origin = worldPosition.offset(-ox, 0, -oz);
                LOGGER.info("[EUV] Trying origin {} (controller offset ox={} oz={})", origin, ox, oz);
                if (isValidAt(origin)) {
                    LOGGER.info("[EUV] Structure VALID at origin {}", origin);
                    structureValid = true;
                    checkCleanRoom(origin);
                    return true;
                }
            }
        }
        LOGGER.info("[EUV] Structure INVALID — no valid origin found");
        structureValid = false;
        cleanRoom = false;
        return false;
    }

    private boolean isValidAt(BlockPos origin) {
        // Layer 0: base
        for (int x = 0; x < 5; x++) for (int z = 0; z < 5; z++) {
            Block b = level.getBlockState(origin.offset(x, 0, z)).getBlock();
            if (b != ModBlocks.EUV_BASE.get() && b != ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()
                    && b != ModBlocks.ENERGY_PORT.get()) {
                LOGGER.info("[EUV]   FAIL base layer y=0 x={} z={}: found {}", x, z,
                        BuiltInRegistries.BLOCK.getKey(b));
                return false;
            }
        }
        // Layers 1-2: wall
        for (int y = 1; y <= 2; y++) for (int x = 0; x < 5; x++) for (int z = 0; z < 5; z++) {
            Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
            if (b != ModBlocks.EUV_WALL.get() && b != ModBlocks.ENERGY_PORT.get()) {
                LOGGER.info("[EUV]   FAIL wall layer y={} x={} z={}: found {}", y, x, z,
                        BuiltInRegistries.BLOCK.getKey(b));
                return false;
            }
        }
        // Layers 3-4: wall + mirror corners
        for (int y = 3; y <= 4; y++) for (int x = 0; x < 5; x++) for (int z = 0; z < 5; z++) {
            Block b = level.getBlockState(origin.offset(x, y, z)).getBlock();
            boolean isCorner = (x == 0 || x == 4) && (z == 0 || z == 4);
            if (isCorner) {
                if (b != ModBlocks.EUV_MIRROR_ARRAY.get()) {
                    LOGGER.info("[EUV]   FAIL mirror corner y={} x={} z={}: found {}", y, x, z,
                            BuiltInRegistries.BLOCK.getKey(b));
                    return false;
                }
            } else {
                if (b != ModBlocks.EUV_WALL.get() && b != ModBlocks.ENERGY_PORT.get()) {
                    LOGGER.info("[EUV]   FAIL mirror layer (non-corner) y={} x={} z={}: found {}", y, x, z,
                            BuiltInRegistries.BLOCK.getKey(b));
                    return false;
                }
            }
        }
        // Layer 5: lens center + wall
        for (int x = 0; x < 5; x++) for (int z = 0; z < 5; z++) {
            Block b = level.getBlockState(origin.offset(x, 5, z)).getBlock();
            if (x == 2 && z == 2) {
                if (b != ModBlocks.EUV_LENS_HOUSING.get()) {
                    LOGGER.info("[EUV]   FAIL lens center y=5 x=2 z=2: found {}",
                            BuiltInRegistries.BLOCK.getKey(b));
                    return false;
                }
            } else {
                if (b != ModBlocks.EUV_WALL.get() && b != ModBlocks.ENERGY_PORT.get()) {
                    LOGGER.info("[EUV]   FAIL lens layer (non-center) y=5 x={} z={}: found {}", x, z,
                            BuiltInRegistries.BLOCK.getKey(b));
                    return false;
                }
            }
        }
        // Layer 6: emitter
        for (int x = 0; x < 5; x++) for (int z = 0; z < 5; z++) {
            Block b = level.getBlockState(origin.offset(x, 6, z)).getBlock();
            if (b != ModBlocks.EUV_EMITTER_HOUSING.get() && b != ModBlocks.ENERGY_PORT.get()) {
                LOGGER.info("[EUV]   FAIL emitter layer y=6 x={} z={}: found {}", x, z,
                        BuiltInRegistries.BLOCK.getKey(b));
                return false;
            }
        }
        return true;
    }

    private void checkCleanRoom(BlockPos origin) {
        cleanRoom = true;
        for (int x = -2; x < 7; x++) {
            for (int z = -2; z < 7; z++) {
                for (int y = 1; y <= 6; y++) {
                    if (x >= 0 && x < 5 && z >= 0 && z < 5) continue;
                    if (x < -2 || x > 6 || z < -2 || z > 6) continue;
                    BlockPos check = origin.offset(x, y, z);
                    Block b = level.getBlockState(check).getBlock();
                    if (!level.getBlockState(check).isAir() && b != ModBlocks.EUV_WALL.get()) {
                        cleanRoom = false;
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
        tag.putBoolean("CleanRoom", cleanRoom);
        tag.put("PhotoresistTank", photoresistTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
        cleanRoom = tag.getBoolean("CleanRoom");
        if (tag.contains("PhotoresistTank")) photoresistTank.readFromNBT(registries, tag.getCompound("PhotoresistTank"));
    }
}
