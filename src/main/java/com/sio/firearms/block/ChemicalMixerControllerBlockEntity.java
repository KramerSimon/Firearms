package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.ChemicalMixerMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModFluids;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChemicalMixerControllerBlockEntity extends EnergyStorageBlock implements MenuProvider, IMultiblockPreview {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String[] RECIPE_NAMES = {
        "Nitroglycerin",
        "Propellant Powder",
        "Refined Gunpowder",
        "Sulfuric Acid",
        "Synthetic Rubber",
        "Quartz Sand",
        "Nitric Acid",
        "Nitrocellulose",
        "PVC Resin",
        "Aluminum Ingot",
        "Nichrome Alloy",
        "Photoresist A",
        "Photoresist B",
        "Uranium Hexafluor.",
        "Uranium Dioxide",
        "Boron Carbide",
        "Heavy Water",
        "Cordite",
        "PVC Pellets",
        "Refined Opium",
        "Morphine",
        "Adrenaline",
        "Coagulant",
    };
    public static final int RECIPE_COUNT = RECIPE_NAMES.length;

    public static final int CAPACITY     = 30_000;
    public static final int MAX_RECEIVE  = 500;
    public static final int FE_PER_TICK  = 80;
    public static final int PROCESS_TIME = 200;
    public static final int TANK_SIZE    = 5_000;

    private boolean structureValid = false;
    private int tickCounter = 0;
    private int selectedRecipeIndex = -1;

    // slots: 0=inputA, 1=inputB, 2=bucket input, 3=empty bucket output, 4=item output
    public final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        public void setSize(int size) { super.setSize(5); }
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final FluidTank fluidInputTank = new FluidTank(TANK_SIZE) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            if (stack.isEmpty()) return false;
            String key = fluidKey(stack);
            return key.equals("minecraft:water")
                || key.equals("firearms:fuel_still")
                || key.equals("firearms:sulfuric_acid_still")
                || key.equals("firearms:nitric_acid_still")
                || key.equals("firearms:uranium_hexafluoride_still")
                || key.equals("firearms:enriched_uf6_still")
                || key.equals("firearms:pvc_resin_still");
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank fluidInputTank2 = new FluidTank(TANK_SIZE) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            if (stack.isEmpty()) return false;
            String key = fluidKey(stack);
            return key.equals("firearms:naphtha_still")
                || key.equals("firearms:sulfuric_acid_still");
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank fluidOutputTank = new FluidTank(TANK_SIZE) {
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    private int progress = 0;
    private int currentProcessTime = PROCESS_TIME;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> currentProcessTime;
                case 4 -> fluidInputTank.getFluidAmount();
                case 5 -> TANK_SIZE;
                case 6 -> fluidOutputTank.getFluidAmount();
                case 7 -> TANK_SIZE;
                case 8 -> fluidInputTank2.getFluidAmount();
                case 9 -> TANK_SIZE;
                case 10 -> fluidInputTank.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidInputTank.getFluid().getFluid());
                case 11 -> fluidOutputTank.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidOutputTank.getFluid().getFluid());
                case 12 -> fluidInputTank2.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluidInputTank2.getFluid().getFluid());
                case 13 -> structureValid ? 1 : 0;
                case 14 -> selectedRecipeIndex;
                default -> 0;
            };
        }
        @Override
        public void set(int i, int v) {
            if (i == 2) progress = v;
            else if (i == 14) selectedRecipeIndex = v;
        }
        @Override
        public int getCount() { return 15; }
    };

    public ChemicalMixerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEMICAL_MIXER_CONTROLLER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory()   { return inventory; }
    public FluidTank getFluidInputTank()    { return fluidInputTank; }
    public FluidTank getFluidInputTank2()   { return fluidInputTank2; }
    public FluidTank getFluidOutputTank()   { return fluidOutputTank; }
    public boolean isStructureValid()       { return structureValid; }

    // Fill-only wrapper for tank1
    public final IFluidHandler fluidInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int tank) { return fluidInputTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int tank) { return fluidInputTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return fluidInputTank.isFluidValid(0, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return fluidInputTank.fill(resource, action); }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    // Fill-only wrapper for tank2
    public final IFluidHandler fluidInputHandler2 = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int tank) { return fluidInputTank2.getFluidInTank(0); }
        @Override public int getTankCapacity(int tank) { return fluidInputTank2.getTankCapacity(0); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return fluidInputTank2.isFluidValid(0, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return fluidInputTank2.fill(resource, action); }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    // Drain-only wrapper for fluidOutputTank
    public final IFluidHandler fluidOutputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int tank) { return fluidOutputTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int tank) { return fluidOutputTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return false; }
        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return fluidOutputTank.drain(resource, action); }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return fluidOutputTank.drain(maxDrain, action); }
    };

    public IFluidHandler getFluidOutputHandler() { return fluidOutputHandler; }

    // Full-access handler: fill() routes to input tanks, drain() routes to output tank.
    public final IFluidHandler fullAccessHandler = new IFluidHandler() {
        @Override public int getTanks() { return 3; }
        @Override public FluidStack getFluidInTank(int tank) {
            return switch (tank) {
                case 0 -> fluidInputTank.getFluidInTank(0);
                case 1 -> fluidInputTank2.getFluidInTank(0);
                default -> fluidOutputTank.getFluidInTank(0);
            };
        }
        @Override public int getTankCapacity(int tank) {
            return switch (tank) {
                case 0 -> fluidInputTank.getTankCapacity(0);
                case 1 -> fluidInputTank2.getTankCapacity(0);
                default -> fluidOutputTank.getTankCapacity(0);
            };
        }
        @Override public boolean isFluidValid(int tank, FluidStack stack) {
            return switch (tank) {
                case 0 -> fluidInputTank.isFluidValid(0, stack);
                case 1 -> fluidInputTank2.isFluidValid(0, stack);
                default -> false;
            };
        }
        @Override public int fill(FluidStack resource, FluidAction action) {
            return combinedFluidInputHandler.fill(resource, action);
        }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) {
            return fluidOutputHandler.drain(resource, action);
        }
        @Override public FluidStack drain(int maxDrain, FluidAction action) {
            return fluidOutputHandler.drain(maxDrain, action);
        }
    };

    // Combined fill-only handler — routes to tank1 or tank2 based on fluid type
    public final IFluidHandler combinedFluidInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 2; }
        @Override public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? fluidInputTank.getFluidInTank(0) : fluidInputTank2.getFluidInTank(0);
        }
        @Override public int getTankCapacity(int tank) {
            return tank == 0 ? fluidInputTank.getTankCapacity(0) : fluidInputTank2.getTankCapacity(0);
        }
        @Override public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 ? fluidInputTank.isFluidValid(0, stack) : fluidInputTank2.isFluidValid(0, stack);
        }
        @Override public int fill(FluidStack resource, FluidAction action) {
            int filled = fluidInputTank.fill(resource, action);
            if (filled == 0) filled = fluidInputTank2.fill(resource, action);
            return filled;
        }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.chemical_mixer_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChemicalMixerMenu(id, inv, inventory, data);
    }

    // ── Structure validation ──────────────────────────────────────────────────

    // Positions currently rendered with CONNECTED=true, so we can un-connect exactly the
    // right blocks if the structure re-forms at a different corner (or stops forming).
    private Set<BlockPos> connectedBlocks = new HashSet<>();

    private void checkStructure() {
        if (level == null || level.isClientSide()) return;
        LOGGER.info("[ChemicalMixerController] checkStructure() called, controller at {}", worldPosition);
        boolean valid = false;
        BlockPos validCorner = null;
        for (int offX = -2; offX <= 0 && !valid; offX++) {
            for (int offZ = -2; offZ <= 0 && !valid; offZ++) {
                BlockPos corner = worldPosition.offset(offX, 0, offZ);
                LOGGER.info("[ChemicalMixerController]   Trying origin {} (controller at local dx={} dz={})", corner, -offX, -offZ);
                if (validateAt(corner)) {
                    LOGGER.info("[ChemicalMixerController]   PASS corner {}", corner);
                    valid = true;
                    validCorner = corner;
                }
            }
        }
        if (!valid) {
            LOGGER.info("[ChemicalMixerController] Structure INVALID — no valid corner found");
            connectedBlocks = ConnectedStructureHelper.clear(level, connectedBlocks);
        } else {
            LOGGER.info("[ChemicalMixerController] Structure VALID");
            connectedBlocks = ConnectedStructureHelper.apply(level, connectedBlocks, collectStructurePositions(validCorner));
        }
        if (valid != structureValid) {
            structureValid = valid;
            setChanged();
        }
    }

    /** Every base/wall/cap position of the 3×3×4 shell for a given corner. */
    private Set<BlockPos> collectStructurePositions(BlockPos corner) {
        Set<BlockPos> positions = new HashSet<>();
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++)
                positions.add(corner.offset(dx, 0, dz));
        for (int dy = 1; dy <= 2; dy++)
            for (int dx = 0; dx <= 2; dx++)
                for (int dz = 0; dz <= 2; dz++) {
                    if (dx == 1 && dz == 1) continue;
                    positions.add(corner.offset(dx, dy, dz));
                }
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++)
                positions.add(corner.offset(dx, 3, dz));
        return positions;
    }

    private boolean validateAt(BlockPos corner) {
        // Layer 0: base blocks (this controller occupies its own position)
        for (int dx = 0; dx <= 2; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                BlockPos p = corner.offset(dx, 0, dz);
                if (p.equals(worldPosition)) continue;
                BlockState bs0 = level.getBlockState(p);
                if (!bs0.is(ModBlocks.CHEMICAL_MIXER_BASE.get())
                        && !bs0.is(ModBlocks.ENERGY_PORT.get())
                        && !bs0.is(ModBlocks.FLUID_PORT.get())) {
                    LOGGER.info("[ChemicalMixerController]   FAIL layer=0 dx={} dz={} expected chemical_mixer_base, found {}",
                            dx, dz, BuiltInRegistries.BLOCK.getKey(bs0.getBlock()));
                    return false;
                }
            }
        }
        LOGGER.info("[ChemicalMixerController]   PASS layer=0 floor");
        // Layers 1-2: walls with hollow 1x1 center column
        for (int dy = 1; dy <= 2; dy++) {
            for (int dx = 0; dx <= 2; dx++) {
                for (int dz = 0; dz <= 2; dz++) {
                    if (dx == 1 && dz == 1) continue;
                    BlockPos p = corner.offset(dx, dy, dz);
                    if (!isValidWallBlock(level.getBlockState(p))) {
                        Block found = level.getBlockState(p).getBlock();
                        LOGGER.info("[ChemicalMixerController]   FAIL layer={} dx={} dz={} expected wall, found {}",
                                dy, dx, dz, BuiltInRegistries.BLOCK.getKey(found));
                        return false;
                    }
                }
            }
            LOGGER.info("[ChemicalMixerController]   PASS layer={} wall ring", dy);
        }
        // Layer 3: solid cap
        for (int dx = 0; dx <= 2; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                BlockPos p = corner.offset(dx, 3, dz);
                if (!isValidWallBlock(level.getBlockState(p))) {
                    Block found = level.getBlockState(p).getBlock();
                    LOGGER.info("[ChemicalMixerController]   FAIL layer=3 dx={} dz={} expected wall (cap), found {}",
                            dx, dz, BuiltInRegistries.BLOCK.getKey(found));
                    return false;
                }
            }
        }
        LOGGER.info("[ChemicalMixerController]   PASS layer=3 cap");
        return true;
    }

    private boolean isValidWallBlock(BlockState st) {
        return st.is(ModBlocks.CHEMICAL_MIXER_WALL.get())
                || st.is(ModBlocks.ENERGY_PORT.get())
                || st.is(ModBlocks.FLUID_PORT.get());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String itemKey(ItemStack stack) {
        if (stack.isEmpty()) return "";
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static String fluidKey(FluidStack stack) {
        if (stack.isEmpty()) return "";
        return BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString();
    }

    private boolean itemIs(ItemStack stack, String id, int qty) {
        return stack.getCount() >= qty && itemKey(stack).equals(id);
    }

    private boolean fluidIs(Fluid fluid, int mb) {
        return fluidInputTank.getFluid().getFluid().isSame(fluid)
            && fluidInputTank.getFluidAmount() >= mb;
    }

    private boolean fluid2Is(Fluid fluid, int mb) {
        return fluidInputTank2.getFluid().getFluid().isSame(fluid)
            && fluidInputTank2.getFluidAmount() >= mb;
    }

    // ── Bucket draining ───────────────────────────────────────────────────────

    private void tryDrainBucket() {
        ItemStack bucketStack = inventory.getStackInSlot(2);
        if (bucketStack.isEmpty()) return;

        FluidActionResult sim = FluidUtil.tryEmptyContainer(
            bucketStack, combinedFluidInputHandler, Integer.MAX_VALUE, null, false);
        if (!sim.isSuccess()) return;

        ItemStack emptyOut    = inventory.getStackInSlot(3);
        ItemStack emptyResult = sim.getResult();
        if (!emptyOut.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(emptyOut, emptyResult)) return;
            if (emptyOut.getCount() >= emptyOut.getMaxStackSize()) return;
        }

        FluidActionResult result = FluidUtil.tryEmptyContainer(
            bucketStack, combinedFluidInputHandler, Integer.MAX_VALUE, null, true);
        if (result.isSuccess()) {
            inventory.setStackInSlot(2, ItemStack.EMPTY);
            if (emptyOut.isEmpty()) {
                inventory.setStackInSlot(3, result.getResult().copy());
            } else {
                emptyOut.grow(1);
            }
            setChanged();
        }
    }

    // ── Recipe result ─────────────────────────────────────────────────────────

    private record RecipeResult(
        String consumeAId, int consumeAQty,
        String consumeBId, int consumeBQty,
        int consumeFluidMb,
        int consumeFluid2Mb,
        ItemStack itemOutput,
        FluidStack fluidOutput,
        int processTime
    ) {}

    private RecipeResult findRecipe() {
        if (selectedRecipeIndex >= 0) {
            return checkRecipe(selectedRecipeIndex);
        }
        for (int i = 0; i < RECIPE_COUNT; i++) {
            RecipeResult r = checkRecipe(i);
            if (r != null) return r;
        }
        return null;
    }

    private RecipeResult checkRecipe(int index) {
        ItemStack slotA   = inventory.getStackInSlot(0);
        ItemStack slotB   = inventory.getStackInSlot(1);
        ItemStack outSlot = inventory.getStackInSlot(4);
        return switch (index) {
            case 0 -> // Nitroglycerin
                itemIs(slotA, "minecraft:sugar", 1)
                    && fluidIs(ModFluids.NITRIC_ACID_STILL.get(), 500)
                    && fluid2Is(ModFluids.SULFURIC_ACID_STILL.get(), 250)
                    && canOutputItem(outSlot, new ItemStack(ModItems.NITROGLYCERIN.get(), 2))
                ? new RecipeResult("minecraft:sugar", 1, null, 0, 500, 250,
                    new ItemStack(ModItems.NITROGLYCERIN.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 1 -> // Propellant Powder (water 100–249 mB)
                itemIs(slotA, "firearms:sulfur", 1) && itemIs(slotB, "firearms:saltpeter", 1)
                    && fluidIs(Fluids.WATER, 100) && fluidInputTank.getFluidAmount() < 250
                    && canOutputItem(outSlot, new ItemStack(ModItems.PROPELLANT_POWDER.get(), 8))
                ? new RecipeResult("firearms:sulfur", 1, "firearms:saltpeter", 1, 100, 0,
                    new ItemStack(ModItems.PROPELLANT_POWDER.get(), 8), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 2 -> // Refined Gunpowder (water ≥250 mB)
                itemIs(slotA, "firearms:sulfur", 1) && itemIs(slotB, "firearms:saltpeter", 1)
                    && fluidIs(Fluids.WATER, 250)
                    && canOutputItem(outSlot, new ItemStack(ModItems.REFINED_GUNPOWDER.get(), 4))
                ? new RecipeResult("firearms:sulfur", 1, "firearms:saltpeter", 1, 250, 0,
                    new ItemStack(ModItems.REFINED_GUNPOWDER.get(), 4), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 3 -> // Sulfuric Acid
                itemIs(slotA, "firearms:sulfur", 1) && fluidIs(Fluids.WATER, 500)
                    && fluidOutputTank.getSpace() >= 500
                ? new RecipeResult("firearms:sulfur", 1, null, 0, 500, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 500), PROCESS_TIME)
                : null;
            case 4 -> // Synthetic Rubber
                itemIs(slotA, "firearms:rubber_sheet", 1) && fluidIs(ModFluids.FUEL_STILL.get(), 500)
                    && fluidOutputTank.getSpace() >= 500
                ? new RecipeResult("firearms:rubber_sheet", 1, null, 0, 500, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.SYNTHETIC_RUBBER_STILL.get(), 500), PROCESS_TIME)
                : null;
            case 5 -> // Quartz Sand
                itemIs(slotA, "minecraft:sand", 1) && itemIs(slotB, "minecraft:quartz", 1)
                    && fluidInputTank.isEmpty()
                    && canOutputItem(outSlot, new ItemStack(ModItems.QUARTZ_SAND.get(), 2))
                ? new RecipeResult("minecraft:sand", 1, "minecraft:quartz", 1, 0, 0,
                    new ItemStack(ModItems.QUARTZ_SAND.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 6 -> // Nitric Acid
                itemIs(slotA, "firearms:saltpeter", 1) && fluidIs(ModFluids.SULFURIC_ACID_STILL.get(), 250)
                    && fluidOutputTank.getSpace() >= 250
                ? new RecipeResult("firearms:saltpeter", 1, null, 0, 250, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 250), PROCESS_TIME)
                : null;
            case 7 -> // Nitrocellulose
                itemIs(slotA, "minecraft:paper", 1) && fluidIs(ModFluids.NITRIC_ACID_STILL.get(), 250)
                    && canOutputItem(outSlot, new ItemStack(ModItems.NITROCELLULOSE.get(), 1))
                ? new RecipeResult("minecraft:paper", 1, null, 0, 250, 0,
                    new ItemStack(ModItems.NITROCELLULOSE.get(), 1), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 8 -> // PVC Resin
                itemIs(slotA, "firearms:chlorine_gas_bucket", 1) && fluidIs(ModFluids.FUEL_STILL.get(), 500)
                    && fluidOutputTank.getSpace() >= 500
                ? new RecipeResult("firearms:chlorine_gas_bucket", 1, null, 0, 500, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.PVC_RESIN_STILL.get(), 500), PROCESS_TIME)
                : null;
            case 9 -> // Aluminum Ingot
                itemIs(slotA, "firearms:bauxite_dust", 1) && fluidIs(ModFluids.SULFURIC_ACID_STILL.get(), 500)
                    && canOutputItem(outSlot, new ItemStack(ModItems.ALUMINUM_INGOT.get(), 2))
                ? new RecipeResult("firearms:bauxite_dust", 1, null, 0, 500, 0,
                    new ItemStack(ModItems.ALUMINUM_INGOT.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 10 -> // Nichrome Alloy
                itemIs(slotA, "firearms:nickel_ingot", 1) && itemIs(slotB, "firearms:chromium_ingot", 1)
                    && fluidInputTank.isEmpty()
                    && canOutputItem(outSlot, new ItemStack(ModItems.NICHROME_ALLOY.get(), 2))
                ? new RecipeResult("firearms:nickel_ingot", 1, "firearms:chromium_ingot", 1, 0, 0,
                    new ItemStack(ModItems.NICHROME_ALLOY.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 11 -> // Photoresist A (naphtha + nitric acid, two-fluid)
                fluid2Is(ModFluids.NAPHTHA_STILL.get(), 500) && fluidIs(ModFluids.NITRIC_ACID_STILL.get(), 500)
                    && fluidOutputTank.getSpace() >= 1000
                ? new RecipeResult(null, 0, null, 0, 500, 500,
                    ItemStack.EMPTY, new FluidStack(ModFluids.PHOTORESIST_STILL.get(), 1000), 300)
                : null;
            case 12 -> // Photoresist B (synthetic rubber + nitric acid)
                itemIs(slotA, "firearms:synthetic_rubber", 1) && fluidIs(ModFluids.NITRIC_ACID_STILL.get(), 500)
                    && fluidOutputTank.getSpace() >= 1000
                ? new RecipeResult("firearms:synthetic_rubber", 1, null, 0, 500, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.PHOTORESIST_STILL.get(), 1000), 300)
                : null;
            case 13 -> // Uranium Hexafluoride
                itemIs(slotA, "firearms:uranium_ingot", 1) && itemIs(slotB, "firearms:fluorine_gas_bucket", 1)
                    && fluidOutputTank.getSpace() >= 1000
                ? new RecipeResult("firearms:uranium_ingot", 1, "firearms:fluorine_gas_bucket", 1, 0, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.URANIUM_HEXAFLUORIDE_STILL.get(), 1000), PROCESS_TIME)
                : null;
            case 14 -> // Uranium Dioxide Powder
                fluidIs(ModFluids.ENRICHED_UF6_STILL.get(), 500)
                    && canOutputItem(outSlot, new ItemStack(ModItems.URANIUM_DIOXIDE_POWDER.get(), 4))
                ? new RecipeResult(null, 0, null, 0, 500, 0,
                    new ItemStack(ModItems.URANIUM_DIOXIDE_POWDER.get(), 4), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 15 -> // Boron Carbide
                itemIs(slotA, "firearms:boron", 1) && itemIs(slotB, "minecraft:coal", 1)
                    && fluidInputTank.isEmpty()
                    && canOutputItem(outSlot, new ItemStack(ModItems.BORON_CARBIDE.get(), 2))
                ? new RecipeResult("firearms:boron", 1, "minecraft:coal", 1, 0, 0,
                    new ItemStack(ModItems.BORON_CARBIDE.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 16 -> // Heavy Water
                fluidIs(Fluids.WATER, 1000) && fluidOutputTank.getSpace() >= 500
                    && slotA.isEmpty() && slotB.isEmpty()
                ? new RecipeResult(null, 0, null, 0, 1000, 0,
                    ItemStack.EMPTY, new FluidStack(ModFluids.HEAVY_WATER_STILL.get(), 500), 400)
                : null;
            case 17 -> // Cordite
                itemIs(slotA, "firearms:nitrocellulose", 1) && itemIs(slotB, "firearms:nitroglycerin", 1)
                    && fluidIs(Fluids.WATER, 250)
                    && canOutputItem(outSlot, new ItemStack(ModItems.CORDITE.get(), 4))
                ? new RecipeResult("firearms:nitrocellulose", 1, "firearms:nitroglycerin", 1, 250, 0,
                    new ItemStack(ModItems.CORDITE.get(), 4), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 18 -> // PVC Pellets (cooling/solidification)
                fluidIs(ModFluids.PVC_RESIN_STILL.get(), 1000)
                    && canOutputItem(outSlot, new ItemStack(ModItems.PVC_PELLETS.get(), 4))
                    && slotA.isEmpty() && slotB.isEmpty()
                ? new RecipeResult(null, 0, null, 0, 1000, 0,
                    new ItemStack(ModItems.PVC_PELLETS.get(), 4), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 19 -> // Refined Opium
                itemIs(slotA, "firearms:raw_opium", 1) && fluidIs(Fluids.WATER, 250) && slotB.isEmpty()
                    && canOutputItem(outSlot, new ItemStack(ModItems.REFINED_OPIUM.get()))
                ? new RecipeResult("firearms:raw_opium", 1, null, 0, 250, 0,
                    new ItemStack(ModItems.REFINED_OPIUM.get()), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 20 -> // Morphine
                itemIs(slotA, "firearms:refined_opium", 1) && fluidIs(ModFluids.SULFURIC_ACID_STILL.get(), 100)
                    && slotB.isEmpty() && canOutputItem(outSlot, new ItemStack(ModItems.MORPHINE.get(), 2))
                ? new RecipeResult("firearms:refined_opium", 1, null, 0, 100, 0,
                    new ItemStack(ModItems.MORPHINE.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 21 -> // Adrenaline
                itemIs(slotA, "minecraft:sugar", 1) && itemIs(slotB, "minecraft:glass_bottle", 1)
                    && fluidIs(ModFluids.NITRIC_ACID_STILL.get(), 100)
                    && canOutputItem(outSlot, new ItemStack(ModItems.ADRENALINE.get()))
                ? new RecipeResult("minecraft:sugar", 1, "minecraft:glass_bottle", 1, 100, 0,
                    new ItemStack(ModItems.ADRENALINE.get()), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            case 22 -> // Coagulant
                itemIs(slotA, "minecraft:spider_eye", 1) && itemIs(slotB, "firearms:saltpeter", 1)
                    && fluidIs(Fluids.WATER, 100)
                    && canOutputItem(outSlot, new ItemStack(ModItems.COAGULANT.get(), 2))
                ? new RecipeResult("minecraft:spider_eye", 1, "firearms:saltpeter", 1, 100, 0,
                    new ItemStack(ModItems.COAGULANT.get(), 2), FluidStack.EMPTY, PROCESS_TIME)
                : null;
            default -> null;
        };
    }

    private boolean canOutputItem(ItemStack current, ItemStack result) {
        if (current.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(current, result)
            && current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    private void consumeItem(String id, int qty, int slot) {
        if (id == null || qty == 0) return;
        ItemStack stack = inventory.getStackInSlot(slot);
        if (!stack.isEmpty() && itemKey(stack).equals(id)) stack.shrink(qty);
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChemicalMixerControllerBlockEntity be) {
        if (be.tickCounter++ % 100 == 0) {
            be.checkStructure();
        }
        if (be.structureValid) {
            be.serverTickInstance();
        }
    }

    private void serverTickInstance() {
        if (level == null) return;
        boolean changed = false;

        tryDrainBucket();

        RecipeResult recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK) {
            currentProcessTime = recipe.processTime();
            if (progress == 0) {
                LOGGER.info("[ChemicalMixerController]@{} recipe started", worldPosition.toShortString());
            }
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= currentProcessTime) {
                consumeItem(recipe.consumeAId(), recipe.consumeAQty(), 0);
                consumeItem(recipe.consumeBId(), recipe.consumeBQty(), 1);
                if (recipe.consumeFluidMb() > 0)
                    fluidInputTank.drain(recipe.consumeFluidMb(), IFluidHandler.FluidAction.EXECUTE);
                if (recipe.consumeFluid2Mb() > 0)
                    fluidInputTank2.drain(recipe.consumeFluid2Mb(), IFluidHandler.FluidAction.EXECUTE);

                if (!recipe.itemOutput().isEmpty()) {
                    ItemStack outSlot = inventory.getStackInSlot(4);
                    if (outSlot.isEmpty()) inventory.setStackInSlot(4, recipe.itemOutput().copy());
                    else outSlot.grow(recipe.itemOutput().getCount());
                }
                if (!recipe.fluidOutput().isEmpty())
                    fluidOutputTank.fill(recipe.fluidOutput(), IFluidHandler.FluidAction.EXECUTE);

                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    // ── Multiblock preview ghost ────────────────────────────────────────────────
    private boolean previewActive = false;

    @Override
    public boolean isPreviewActive() { return previewActive; }

    @Override
    public void setPreviewActive(boolean active) {
        previewActive = active;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // Canonical layout: origin is the front-center cell at ground level (the 3×3 base),
    // with the structure extending back (+dz) and to the sides.
    @Override
    public Map<BlockPos, Block> getPreviewPositions(BlockPos origin) {
        Map<BlockPos, Block> map = new HashMap<>();
        Block base = ModBlocks.CHEMICAL_MIXER_BASE.get();
        Block wall = ModBlocks.CHEMICAL_MIXER_WALL.get();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                BlockPos p = origin.offset(dx, 0, dz);
                if (!p.equals(origin)) map.put(p, base);
            }
        }
        for (int dy = 1; dy <= 2; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = 0; dz <= 2; dz++) {
                    if (dx == 0 && dz == 1) continue;
                    map.put(origin.offset(dx, dy, dz), wall);
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                map.put(origin.offset(dx, 3, dz), wall);
            }
        }
        return map;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory",   inventory.serializeNBT(registries));
        tag.put("FluidIn",     fluidInputTank.writeToNBT(registries, new CompoundTag()));
        tag.put("FluidIn2",    fluidInputTank2.writeToNBT(registries, new CompoundTag()));
        tag.put("FluidOut",    fluidOutputTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
        tag.putInt("SelectedRecipe", selectedRecipeIndex);
        tag.putBoolean("PreviewActive", previewActive);
        ConnectedStructureHelper.writePositions(tag, "ConnectedBlocks", connectedBlocks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("FluidIn"))   fluidInputTank.readFromNBT(registries, tag.getCompound("FluidIn"));
        if (tag.contains("FluidIn2"))  fluidInputTank2.readFromNBT(registries, tag.getCompound("FluidIn2"));
        if (tag.contains("FluidOut"))  fluidOutputTank.readFromNBT(registries, tag.getCompound("FluidOut"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
        selectedRecipeIndex = tag.contains("SelectedRecipe") ? tag.getInt("SelectedRecipe") : -1;
        previewActive = tag.getBoolean("PreviewActive");
        connectedBlocks = ConnectedStructureHelper.readPositions(tag, "ConnectedBlocks");
    }
}
