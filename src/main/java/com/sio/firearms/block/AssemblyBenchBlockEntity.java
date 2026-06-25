package com.sio.firearms.block;

import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.AssemblyBenchMenu;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class AssemblyBenchBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final int CAPACITY = 50_000;
    private static final int MAX_RECEIVE = 500;
    private static final int FE_PER_TICK = 100;
    private static final int PROCESS_TIME = 300;

    private final ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> PROCESS_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 2) progress = value;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AssemblyBenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ASSEMBLY_BENCH.get(), pos, state, CAPACITY, MAX_RECEIVE, 0);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.firearms.assembly_bench");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AssemblyBenchMenu(containerId, playerInventory, inventory, data);
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        ItemStack output = inventory.getStackInSlot(6);
        RecipeMatch recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK && canOutput(output, recipe.result)) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                for (int i = 0; i < recipe.consume.length; i++) {
                    if (recipe.consume[i] > 0) {
                        inventory.getStackInSlot(i).shrink(recipe.consume[i]);
                    }
                }
                if (output.isEmpty()) {
                    inventory.setStackInSlot(6, recipe.result.copy());
                } else {
                    output.grow(recipe.result.getCount());
                }
                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
    }

    private record RecipeMatch(ItemStack result, int[] consume) {}

    private boolean slotIs(int slot, net.neoforged.neoforge.registries.DeferredItem<?> item) {
        return inventory.getStackInSlot(slot).is(item.get());
    }

    private boolean slotIs(int slot, net.neoforged.neoforge.registries.DeferredItem<?> item, int minCount) {
        ItemStack stack = inventory.getStackInSlot(slot);
        return stack.is(item.get()) && stack.getCount() >= minCount;
    }

    private boolean slotEmpty(int slot) {
        return inventory.getStackInSlot(slot).isEmpty();
    }

    private RecipeMatch findRecipe() {
        // gun_barrel_blank + steel_rod x2 → gun_barrel
        if (slotIs(0, ModItems.GUN_BARREL_BLANK) && slotIs(1, ModItems.STEEL_ROD, 2)
                && slotEmpty(2) && slotEmpty(3) && slotEmpty(4) && slotEmpty(5)) {
            return new RecipeMatch(new ItemStack(ModItems.GUN_BARREL.get()), new int[]{1, 2, 0, 0, 0, 0});
        }

        // firing_mechanism + firing_pin + spring → trigger_assembly
        if (slotIs(0, ModItems.FIRING_MECHANISM) && slotIs(1, ModItems.FIRING_PIN) && slotIs(2, ModItems.SPRING)
                && slotEmpty(3) && slotEmpty(4) && slotEmpty(5)) {
            return new RecipeMatch(new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()), new int[]{1, 1, 1, 0, 0, 0});
        }

        // gun_barrel + trigger_assembly + gun_grip + magazine + spring → pistol
        if (slotIs(0, ModItems.GUN_BARREL) && slotIs(1, ModItems.TRIGGER_ASSEMBLY)
                && slotIs(2, ModItems.GUN_GRIP) && slotIs(3, ModItems.MAGAZINE)
                && slotIs(4, ModItems.SPRING) && slotEmpty(5)) {
            return new RecipeMatch(new ItemStack(ModItems.PISTOL.get()), new int[]{1, 1, 1, 1, 1, 0});
        }

        // gun_barrel + trigger_assembly + gun_grip + magazine + bolt + buffer_tube → rifle
        if (slotIs(0, ModItems.GUN_BARREL) && slotIs(1, ModItems.TRIGGER_ASSEMBLY)
                && slotIs(2, ModItems.GUN_GRIP) && slotIs(3, ModItems.MAGAZINE)
                && slotIs(4, ModItems.BOLT) && slotIs(5, ModItems.BUFFER_TUBE)) {
            return new RecipeMatch(new ItemStack(ModItems.RIFLE.get()), new int[]{1, 1, 1, 1, 1, 1});
        }

        // gun_barrel x2 + trigger_assembly + gun_grip + magazine → shotgun
        if (slotIs(0, ModItems.GUN_BARREL, 2) && slotIs(1, ModItems.TRIGGER_ASSEMBLY)
                && slotIs(2, ModItems.GUN_GRIP) && slotIs(3, ModItems.MAGAZINE)
                && slotEmpty(4) && slotEmpty(5)) {
            return new RecipeMatch(new ItemStack(ModItems.SHOTGUN.get()), new int[]{2, 1, 1, 1, 0, 0});
        }

        // gun_barrel + trigger_assembly + gun_grip + magazine + steel_rod x2 + firing_pin → sniper_rifle
        if (slotIs(0, ModItems.GUN_BARREL) && slotIs(1, ModItems.TRIGGER_ASSEMBLY)
                && slotIs(2, ModItems.GUN_GRIP) && slotIs(3, ModItems.MAGAZINE)
                && slotIs(4, ModItems.STEEL_ROD, 2) && slotIs(5, ModItems.FIRING_PIN)) {
            return new RecipeMatch(new ItemStack(ModItems.SNIPER_RIFLE.get()), new int[]{1, 1, 1, 1, 2, 1});
        }

        // gun_barrel + electronic_trigger + gun_grip + magazine + circuit_board + buffer_tube → smg
        if (slotIs(0, ModItems.GUN_BARREL) && slotIs(1, ModItems.ELECTRONIC_TRIGGER)
                && slotIs(2, ModItems.GUN_GRIP) && slotIs(3, ModItems.MAGAZINE)
                && slotIs(4, ModItems.CIRCUIT_BOARD) && slotIs(5, ModItems.BUFFER_TUBE)) {
            return new RecipeMatch(new ItemStack(ModItems.SMG.get()), new int[]{1, 1, 1, 1, 1, 1});
        }

        return null;
    }

    private boolean canOutput(ItemStack current, ItemStack result) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, result)) return false;
        return current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
    }
}
