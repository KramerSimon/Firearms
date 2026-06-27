package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.ChemicalMixerMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModFluids;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ChemicalMixerBlockEntity extends EnergyStorageBlock implements MenuProvider {

    public static final int CAPACITY     = 30_000;
    public static final int MAX_RECEIVE  = 500;
    public static final int FE_PER_TICK  = 80;
    public static final int PROCESS_TIME = 200;
    public static final int TANK_SIZE    = 5_000;

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
                || key.equals("firearms:nitric_acid_still");
        }
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    public final FluidTank fluidOutputTank = new FluidTank(TANK_SIZE) {
        @Override
        protected void onContentsChanged() { setChanged(); }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                case 4 -> fluidInputTank.getFluidAmount();
                case 5 -> TANK_SIZE;
                case 6 -> fluidOutputTank.getFluidAmount();
                case 7 -> TANK_SIZE;
                default -> 0;
            };
        }
        @Override
        public void set(int i, int v) { if (i == 2) progress = v; }
        @Override
        public int getCount() { return 8; }
    };

    public ChemicalMixerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEMICAL_MIXER.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory()  { return inventory; }
    public FluidTank getFluidInputTank()    { return fluidInputTank; }
    public FluidTank getFluidOutputTank()   { return fluidOutputTank; }

    // Fill-only wrapper — pipes can push fluid in, never pull out.
    public final IFluidHandler fluidInputHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int tank) { return fluidInputTank.getFluidInTank(0); }
        @Override public int getTankCapacity(int tank) { return fluidInputTank.getTankCapacity(0); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return fluidInputTank.isFluidValid(0, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return fluidInputTank.fill(resource, action); }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.chemical_mixer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChemicalMixerMenu(id, inv, inventory, data);
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

    private boolean fluidIs(String id, int mb) {
        return fluidKey(fluidInputTank.getFluid()).equals(id)
            && fluidInputTank.getFluidAmount() >= mb;
    }

    // ── Bucket draining ───────────────────────────────────────────────────────

    private void tryDrainBucket() {
        ItemStack bucketStack = inventory.getStackInSlot(2);
        if (bucketStack.isEmpty()) return;

        // Simulate to check feasibility
        FluidActionResult sim = FluidUtil.tryEmptyContainer(
            bucketStack, fluidInputTank, Integer.MAX_VALUE, null, false);
        if (!sim.isSuccess()) return;

        // Check the empty-bucket output slot can accept the result
        ItemStack emptyOut   = inventory.getStackInSlot(3);
        ItemStack emptyResult = sim.getResult();
        if (!emptyOut.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(emptyOut, emptyResult)) return;
            if (emptyOut.getCount() >= emptyOut.getMaxStackSize()) return;
        }

        // Execute
        FluidActionResult result = FluidUtil.tryEmptyContainer(
            bucketStack, fluidInputTank, Integer.MAX_VALUE, null, true);
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
        ItemStack itemOutput,
        FluidStack fluidOutput
    ) {}

    private RecipeResult findRecipe() {
        ItemStack slotA   = inventory.getStackInSlot(0);
        ItemStack slotB   = inventory.getStackInSlot(1);
        ItemStack outSlot = inventory.getStackInSlot(4);   // slot 4 = item output

        // 0. sulfur + saltpeter + water 250mB → refined_gunpowder x4 (checked first — more specific than recipe 1)
        if (itemIs(slotA, "firearms:sulfur", 1) && itemIs(slotB, "firearms:saltpeter", 1)
                && fluidIs("minecraft:water", 250)
                && canOutputItem(outSlot, new ItemStack(ModItems.REFINED_GUNPOWDER.get(), 4))) {
            return new RecipeResult("firearms:sulfur", 1, "firearms:saltpeter", 1, 250,
                new ItemStack(ModItems.REFINED_GUNPOWDER.get(), 4), FluidStack.EMPTY);
        }

        // 1. sulfur + water 500mB → sulfuric_acid 500mB
        if (itemIs(slotA, "firearms:sulfur", 1) && fluidIs("minecraft:water", 500)
                && fluidOutputTank.getSpace() >= 500) {
            return new RecipeResult("firearms:sulfur", 1, null, 0, 500,
                ItemStack.EMPTY, new FluidStack(ModFluids.SULFURIC_ACID_STILL.get(), 500));
        }

        // 2. rubber_sheet + fuel 500mB → synthetic_rubber 500mB
        if (itemIs(slotA, "firearms:rubber_sheet", 1) && fluidIs("firearms:fuel_still", 500)
                && fluidOutputTank.getSpace() >= 500) {
            return new RecipeResult("firearms:rubber_sheet", 1, null, 0, 500,
                ItemStack.EMPTY, new FluidStack(ModFluids.SYNTHETIC_RUBBER_STILL.get(), 500));
        }

        // 3. sand + quartz → quartz_sand×2  (no fluid)
        if (itemIs(slotA, "minecraft:sand", 1) && itemIs(slotB, "minecraft:quartz", 1)
                && fluidInputTank.isEmpty()
                && canOutputItem(outSlot, new ItemStack(ModItems.QUARTZ_SAND.get(), 2))) {
            return new RecipeResult("minecraft:sand", 1, "minecraft:quartz", 1, 0,
                new ItemStack(ModItems.QUARTZ_SAND.get(), 2), FluidStack.EMPTY);
        }

        // 4. saltpeter + sulfuric_acid 250mB → nitric_acid 250mB
        if (itemIs(slotA, "firearms:saltpeter", 1) && fluidIs("firearms:sulfuric_acid_still", 250)
                && fluidOutputTank.getSpace() >= 250) {
            return new RecipeResult("firearms:saltpeter", 1, null, 0, 250,
                ItemStack.EMPTY, new FluidStack(ModFluids.NITRIC_ACID_STILL.get(), 250));
        }

        // 5. paper + nitric_acid 250mB → nitrocellulose×1
        if (itemIs(slotA, "minecraft:paper", 1) && fluidIs("firearms:nitric_acid_still", 250)
                && canOutputItem(outSlot, new ItemStack(ModItems.NITROCELLULOSE.get(), 1))) {
            return new RecipeResult("minecraft:paper", 1, null, 0, 250,
                new ItemStack(ModItems.NITROCELLULOSE.get(), 1), FluidStack.EMPTY);
        }

        // 6. chlorine_gas_bucket + fuel 500mB → pvc_resin 500mB
        if (itemIs(slotA, "firearms:chlorine_gas_bucket", 1) && fluidIs("firearms:fuel_still", 500)
                && fluidOutputTank.getSpace() >= 500) {
            return new RecipeResult("firearms:chlorine_gas_bucket", 1, null, 0, 500,
                ItemStack.EMPTY, new FluidStack(ModFluids.PVC_RESIN_STILL.get(), 500));
        }

        // 7. bauxite_dust + sulfuric_acid 500mB → aluminum_ingot x2
        if (itemIs(slotA, "firearms:bauxite_dust", 1) && fluidIs("firearms:sulfuric_acid_still", 500)
                && canOutputItem(outSlot, new ItemStack(ModItems.ALUMINUM_INGOT.get(), 2))) {
            return new RecipeResult("firearms:bauxite_dust", 1, null, 0, 500,
                new ItemStack(ModItems.ALUMINUM_INGOT.get(), 2), FluidStack.EMPTY);
        }

        // 8. nickel_ingot + chromium_ingot → nichrome_alloy x2 (no fluid)
        if (itemIs(slotA, "firearms:nickel_ingot", 1) && itemIs(slotB, "firearms:chromium_ingot", 1)
                && fluidInputTank.isEmpty()
                && canOutputItem(outSlot, new ItemStack(ModItems.NICHROME_ALLOY.get(), 2))) {
            return new RecipeResult("firearms:nickel_ingot", 1, "firearms:chromium_ingot", 1, 0,
                new ItemStack(ModItems.NICHROME_ALLOY.get(), 2), FluidStack.EMPTY);
        }

        return null;
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

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        tryDrainBucket();

        RecipeResult recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                consumeItem(recipe.consumeAId(), recipe.consumeAQty(), 0);
                consumeItem(recipe.consumeBId(), recipe.consumeBQty(), 1);
                if (recipe.consumeFluidMb() > 0)
                    fluidInputTank.drain(recipe.consumeFluidMb(), IFluidHandler.FluidAction.EXECUTE);

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

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory",   inventory.serializeNBT(registries));
        tag.put("FluidIn",     fluidInputTank.writeToNBT(registries, new CompoundTag()));
        tag.put("FluidOut",    fluidOutputTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("FluidIn"))   fluidInputTank.readFromNBT(registries, tag.getCompound("FluidIn"));
        if (tag.contains("FluidOut"))  fluidOutputTank.readFromNBT(registries, tag.getCompound("FluidOut"));
        progress = tag.getInt("Progress");
    }
}
