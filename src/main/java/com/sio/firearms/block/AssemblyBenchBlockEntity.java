package com.sio.firearms.block;

import com.mojang.logging.LogUtils;
import com.sio.firearms.energy.EnergyStorageBlock;
import com.sio.firearms.menu.AssemblyBenchMenu;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AssemblyBenchBlockEntity extends EnergyStorageBlock implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int CAPACITY = 50_000;
    private static final int MAX_RECEIVE = 500;
    private static final int FE_PER_TICK = 100;
    private static final int PROCESS_TIME = 300;

    // slots 0-8: input grid (3x3), slot 9: output
    private final ItemStackHandler inventory = new ItemStackHandler(10) {
        @Override
        public void setSize(int size) {
            // Never resize — old world saves may have "Size":7; keep fixed at 10.
            super.setSize(10);
        }

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

    // ── Recipe system ────────────────────────────────────────────────────────

    private Map<String, Integer> getInputMap() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            map.merge(key, stack.getCount(), Integer::sum);
        }
        return map;
    }

    private boolean containsItem(Map<String, Integer> inputs, String id, int count) {
        return inputs.getOrDefault(id, 0) >= count;
    }

    private record RecipeMatch(ItemStack result, Map<String, Integer> consume) {}

    private RecipeMatch findRecipe() {
        Map<String, Integer> in = getInputMap();
        int total = in.values().stream().mapToInt(Integer::intValue).sum();

        if (LOGGER.isDebugEnabled() && !in.isEmpty()) {
            LOGGER.debug("[AssemblyBench] findRecipe input={} total={}", in, total);
        }

        // Gun Barrel: gun_barrel_blank + steel_rod x2
        if (total == 3
                && containsItem(in, "firearms:gun_barrel_blank", 1)
                && containsItem(in, "firearms:steel_rod", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.GUN_BARREL.get()),
                    Map.of("firearms:gun_barrel_blank", 1, "firearms:steel_rod", 2));
        }

        // Trigger Assembly: firing_mechanism + firing_pin + spring
        if (total == 3
                && containsItem(in, "firearms:firing_mechanism", 1)
                && containsItem(in, "firearms:firing_pin", 1)
                && containsItem(in, "firearms:spring", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()),
                    Map.of("firearms:firing_mechanism", 1, "firearms:firing_pin", 1, "firearms:spring", 1));
        }

        // Pistol: gun_barrel + trigger_assembly + gun_grip + magazine + spring
        if (total == 5
                && containsItem(in, "firearms:gun_barrel", 1)
                && containsItem(in, "firearms:trigger_assembly", 1)
                && containsItem(in, "firearms:gun_grip", 1)
                && containsItem(in, "firearms:magazine", 1)
                && containsItem(in, "firearms:spring", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.PISTOL.get()),
                    Map.of("firearms:gun_barrel", 1, "firearms:trigger_assembly", 1,
                            "firearms:gun_grip", 1, "firearms:magazine", 1, "firearms:spring", 1));
        }

        // Shotgun: gun_barrel x2 + trigger_assembly + gun_grip + magazine
        if (total == 5
                && containsItem(in, "firearms:gun_barrel", 2)
                && containsItem(in, "firearms:trigger_assembly", 1)
                && containsItem(in, "firearms:gun_grip", 1)
                && containsItem(in, "firearms:magazine", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SHOTGUN.get()),
                    Map.of("firearms:gun_barrel", 2, "firearms:trigger_assembly", 1,
                            "firearms:gun_grip", 1, "firearms:magazine", 1));
        }

        // Rifle: gun_barrel + trigger_assembly + gun_grip + magazine + bolt + buffer_tube
        if (total == 6
                && containsItem(in, "firearms:gun_barrel", 1)
                && containsItem(in, "firearms:trigger_assembly", 1)
                && containsItem(in, "firearms:gun_grip", 1)
                && containsItem(in, "firearms:magazine", 1)
                && containsItem(in, "firearms:bolt", 1)
                && containsItem(in, "firearms:buffer_tube", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.RIFLE.get()),
                    Map.of("firearms:gun_barrel", 1, "firearms:trigger_assembly", 1,
                            "firearms:gun_grip", 1, "firearms:magazine", 1,
                            "firearms:bolt", 1, "firearms:buffer_tube", 1));
        }

        // SMG: gun_barrel + electronic_trigger + gun_grip + magazine + circuit_board + buffer_tube
        if (total == 6
                && containsItem(in, "firearms:gun_barrel", 1)
                && containsItem(in, "firearms:electronic_trigger", 1)
                && containsItem(in, "firearms:gun_grip", 1)
                && containsItem(in, "firearms:magazine", 1)
                && containsItem(in, "firearms:circuit_board", 1)
                && containsItem(in, "firearms:buffer_tube", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SMG.get()),
                    Map.of("firearms:gun_barrel", 1, "firearms:electronic_trigger", 1,
                            "firearms:gun_grip", 1, "firearms:magazine", 1,
                            "firearms:circuit_board", 1, "firearms:buffer_tube", 1));
        }

        // Sniper Rifle: gun_barrel + trigger_assembly + gun_grip + magazine + steel_rod x2 + firing_pin
        if (total == 7
                && containsItem(in, "firearms:gun_barrel", 1)
                && containsItem(in, "firearms:trigger_assembly", 1)
                && containsItem(in, "firearms:gun_grip", 1)
                && containsItem(in, "firearms:magazine", 1)
                && containsItem(in, "firearms:steel_rod", 2)
                && containsItem(in, "firearms:firing_pin", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SNIPER_RIFLE.get()),
                    Map.of("firearms:gun_barrel", 1, "firearms:trigger_assembly", 1,
                            "firearms:gun_grip", 1, "firearms:magazine", 1,
                            "firearms:steel_rod", 2, "firearms:firing_pin", 1));
        }

        // Refined Bullet: bullet_casing + refined_gunpowder → 8x refined_bullet
        // Uses in.size()==2 (exactly 2 distinct item types) instead of total==2 so that stacks
        // larger than 1 (e.g. 4x refined_gunpowder from the Chemical Mixer) still match.
        // Item IDs: "firearms:bullet_casing", "firearms:refined_gunpowder" — must match exactly.
        if (in.size() == 2
                && containsItem(in, "firearms:bullet_casing", 1)
                && containsItem(in, "firearms:refined_gunpowder", 1)) {
            LOGGER.debug("[AssemblyBench] refined_bullet recipe matched: input={}", in);
            return new RecipeMatch(new ItemStack(ModItems.REFINED_BULLET.get(), 8),
                    Map.of("firearms:bullet_casing", 1, "firearms:refined_gunpowder", 1));
        }

        // AP Bullet: tungsten_rod + bullet_casing + propellant_powder → 4x armor_piercing_bullet
        if (total == 3
                && containsItem(in, "firearms:tungsten_rod", 1)
                && containsItem(in, "firearms:bullet_casing", 1)
                && containsItem(in, "firearms:propellant_powder", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.ARMOR_PIERCING_BULLET.get(), 4),
                    Map.of("firearms:tungsten_rod", 1, "firearms:bullet_casing", 1,
                            "firearms:propellant_powder", 1));
        }

        // Kanthal Alloy: chromium_ingot + iron_ingot + aluminum_ingot → 4x kanthal_alloy
        if (total == 3 && in.size() == 3
                && containsItem(in, "firearms:chromium_ingot", 1)
                && containsItem(in, "minecraft:iron_ingot", 1)
                && containsItem(in, "firearms:aluminum_ingot", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.KANTHAL_ALLOY.get(), 4),
                    Map.of("firearms:chromium_ingot", 1, "minecraft:iron_ingot", 1,
                            "firearms:aluminum_ingot", 1));
        }

        // Kanthal Coil: 4x kanthal_wire → kanthal_coil x1
        if (in.size() == 1 && containsItem(in, "firearms:kanthal_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.KANTHAL_COIL.get().asItem()),
                    Map.of("firearms:kanthal_wire", 4));
        }

        // Nichrome Coil: 4x nichrome_wire → nichrome_coil x1
        if (in.size() == 1 && containsItem(in, "firearms:nichrome_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.NICHROME_COIL.get().asItem()),
                    Map.of("firearms:nichrome_wire", 4));
        }

        // Tungsten Coil: 4x tungsten_wire → tungsten_coil x1
        if (in.size() == 1 && containsItem(in, "firearms:tungsten_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.TUNGSTEN_COIL.get().asItem()),
                    Map.of("firearms:tungsten_wire", 4));
        }

        // ── Nuclear Reactor Stage 1 recipes ───────────────────────────────────

        // Fuel Rod: 8x uranium_dioxide_pellet + fuel_rod_cladding → fuel_rod
        if (total == 9
                && containsItem(in, "firearms:uranium_dioxide_pellet", 8)
                && containsItem(in, "firearms:fuel_rod_cladding", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.FUEL_ROD.get()),
                    Map.of("firearms:uranium_dioxide_pellet", 8, "firearms:fuel_rod_cladding", 1));
        }

        // Fuel Rod Assembly: 4x fuel_rod → fuel_rod_assembly
        if (in.size() == 1 && containsItem(in, "firearms:fuel_rod", 4)) {
            return new RecipeMatch(new ItemStack(ModItems.FUEL_ROD_ASSEMBLY.get()),
                    Map.of("firearms:fuel_rod", 4));
        }

        // Control Rod: boron_carbide + zirconium_ingot + steel_rod → control_rod
        if (total == 3
                && containsItem(in, "firearms:boron_carbide", 1)
                && containsItem(in, "firearms:zirconium_ingot", 1)
                && containsItem(in, "firearms:steel_rod", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.CONTROL_ROD.get()),
                    Map.of("firearms:boron_carbide", 1, "firearms:zirconium_ingot", 1, "firearms:steel_rod", 1));
        }

        return null;
    }

    private void consumeIngredients(Map<String, Integer> toConsume) {
        Map<String, Integer> remaining = new HashMap<>(toConsume);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            int need = remaining.getOrDefault(key, 0);
            if (need <= 0) continue;
            int take = Math.min(need, stack.getCount());
            stack.shrink(take);
            remaining.put(key, need - take);
        }
    }

    private boolean canOutput(ItemStack current, ItemStack result) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, result)) return false;
        return current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    public void serverTick() {
        if (level == null) return;
        boolean changed = false;

        ItemStack output = inventory.getStackInSlot(9);
        RecipeMatch recipe = findRecipe();

        if (recipe != null && energy.getEnergyStored() >= FE_PER_TICK && canOutput(output, recipe.result())) {
            energy.extractEnergy(FE_PER_TICK, false);
            progress++;
            changed = true;

            if (progress >= PROCESS_TIME) {
                consumeIngredients(recipe.consume());
                if (output.isEmpty()) {
                    inventory.setStackInSlot(9, recipe.result().copy());
                } else {
                    output.grow(recipe.result().getCount());
                }
                progress = 0;
            }
        } else if (progress > 0) {
            progress = 0;
            changed = true;
        }

        if (changed) setChanged();
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
