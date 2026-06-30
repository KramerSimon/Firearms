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
    private int logCooldown = 0;

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

    // Returns true when inputs contains at least `amount` of `item`.
    private boolean hasAtLeast(Map<String, Integer> inputs, String item, int amount) {
        return inputs.getOrDefault(item, 0) >= amount;
    }

    private record RecipeMatch(ItemStack result, Map<String, Integer> consume) {}

    private RecipeMatch findRecipe() {
        Map<String, Integer> in = getInputMap();

        if (!in.isEmpty()) {
            if (++logCooldown >= 100) {
                logCooldown = 0;
                LOGGER.info("[AssemblyBench]@{} inputs: {}", worldPosition.toShortString(), in);
            }
        } else {
            logCooldown = 0;
        }

        // ── Weapons ──────────────────────────────────────────────────────────
        // Checked most-specific first to prevent partial-overlap false matches.

        // Rifle: steel×6 + hardened×2 + copper×3 + circuit×1 + bullet_casing×16
        // Must come before Pistol — if the bench also has propellant, Pistol would fire first otherwise.
        if (hasAtLeast(in, "firearms:steel_ingot", 6)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:copper_wire", 3)
                && hasAtLeast(in, "firearms:circuit_board", 1)
                && hasAtLeast(in, "firearms:bullet_casing", 16)) {
            return new RecipeMatch(new ItemStack(ModItems.RIFLE.get()),
                    Map.of("firearms:steel_ingot", 6, "firearms:hardened_steel_ingot", 2,
                            "firearms:copper_wire", 3, "firearms:circuit_board", 1,
                            "firearms:bullet_casing", 16));
        }

        // Pistol: steel×4 + copper×2 + circuit×1 + bullet_casing×8 + propellant×4
        if (hasAtLeast(in, "firearms:steel_ingot", 4)
                && hasAtLeast(in, "firearms:copper_wire", 2)
                && hasAtLeast(in, "firearms:circuit_board", 1)
                && hasAtLeast(in, "firearms:bullet_casing", 8)
                && hasAtLeast(in, "firearms:propellant_powder", 4)) {
            return new RecipeMatch(new ItemStack(ModItems.PISTOL.get()),
                    Map.of("firearms:steel_ingot", 4, "firearms:copper_wire", 2,
                            "firearms:circuit_board", 1, "firearms:bullet_casing", 8,
                            "firearms:propellant_powder", 4));
        }

        // Minigun: steel×6 + hardened×4 + advanced_microchip×2 + circuit×2 + copper×4
        // Must come before Sniper Rifle — Sniper satisfies steel≥6, hardened≥4, copper≥3, adv≥1,
        // which would all match when the bench holds minigun ingredients.
        if (hasAtLeast(in, "firearms:steel_ingot", 6)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 4)
                && hasAtLeast(in, "firearms:advanced_microchip", 2)
                && hasAtLeast(in, "firearms:circuit_board", 2)
                && hasAtLeast(in, "firearms:copper_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModItems.MINIGUN.get()),
                    Map.of("firearms:steel_ingot", 6, "firearms:hardened_steel_ingot", 4,
                            "firearms:advanced_microchip", 2, "firearms:circuit_board", 2,
                            "firearms:copper_wire", 4));
        }

        // Sniper Rifle: steel×6 + hardened×4 + copper×3 + advanced_microchip×1
        // advanced_microchip is a unique ingredient — no overlap risk with other weapons.
        if (hasAtLeast(in, "firearms:steel_ingot", 6)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 4)
                && hasAtLeast(in, "firearms:copper_wire", 3)
                && hasAtLeast(in, "firearms:advanced_microchip", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SNIPER_RIFLE.get()),
                    Map.of("firearms:steel_ingot", 6, "firearms:hardened_steel_ingot", 4,
                            "firearms:copper_wire", 3, "firearms:advanced_microchip", 1));
        }

        // Shotgun: steel×5 + hardened×2 + copper×2 + circuit×1
        // Must come before SMG — SMG needs only steel×4, so 5 steel would also satisfy SMG.
        if (hasAtLeast(in, "firearms:steel_ingot", 5)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:copper_wire", 2)
                && hasAtLeast(in, "firearms:circuit_board", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SHOTGUN.get()),
                    Map.of("firearms:steel_ingot", 5, "firearms:hardened_steel_ingot", 2,
                            "firearms:copper_wire", 2, "firearms:circuit_board", 1));
        }

        // Chainsaw: steel×4 + hardened×2 + circuit×1 + copper×2 + chain×2
        // Must come before SMG — SMG matches steel≥4, hardened≥2, copper≥2, circuit≥1 (no chain check),
        // so chainsaw ingredients would false-match SMG without the chain gate.
        if (hasAtLeast(in, "firearms:steel_ingot", 4)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:circuit_board", 1)
                && hasAtLeast(in, "firearms:copper_wire", 2)
                && hasAtLeast(in, "minecraft:chain", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.CHAINSAW.get()),
                    Map.of("firearms:steel_ingot", 4, "firearms:hardened_steel_ingot", 2,
                            "firearms:circuit_board", 1, "firearms:copper_wire", 2,
                            "minecraft:chain", 2));
        }

        // SMG: steel×4 + hardened×2 + copper×2 + circuit×1
        if (hasAtLeast(in, "firearms:steel_ingot", 4)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:copper_wire", 2)
                && hasAtLeast(in, "firearms:circuit_board", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SMG.get()),
                    Map.of("firearms:steel_ingot", 4, "firearms:hardened_steel_ingot", 2,
                            "firearms:copper_wire", 2, "firearms:circuit_board", 1));
        }

        // ── Machines ─────────────────────────────────────────────────────────
        // Ordered from highest material requirements to lowest to prevent superset matches.

        // Refinery Wall ×4: hardened×12 + circuit×4 + copper×6 + steel×8
        // Must come before Assembly Bench machine (steel×8 + hardened≥4 would also match Wall materials).
        if (hasAtLeast(in, "firearms:hardened_steel_ingot", 12)
                && hasAtLeast(in, "firearms:circuit_board", 4)
                && hasAtLeast(in, "firearms:copper_wire", 6)
                && hasAtLeast(in, "firearms:steel_ingot", 8)) {
            return new RecipeMatch(new ItemStack(ModBlocks.REFINERY_WALL.get().asItem(), 4),
                    Map.of("firearms:hardened_steel_ingot", 12, "firearms:circuit_board", 4,
                            "firearms:copper_wire", 6, "firearms:steel_ingot", 8));
        }

        // Refinery Controller: hardened×8 + advanced_microchip×2 + circuit×4 + copper×4
        // advanced_microchip uniquely identifies this; hardened×8 gates it from EBF confusion.
        if (hasAtLeast(in, "firearms:hardened_steel_ingot", 8)
                && hasAtLeast(in, "firearms:advanced_microchip", 2)
                && hasAtLeast(in, "firearms:circuit_board", 4)
                && hasAtLeast(in, "firearms:copper_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.REFINERY_CONTROLLER.get().asItem()),
                    Map.of("firearms:hardened_steel_ingot", 8, "firearms:advanced_microchip", 2,
                            "firearms:circuit_board", 4, "firearms:copper_wire", 4));
        }

        // EBF Controller: hardened×8 + circuit×2 + copper×4 + kanthal_coil×4
        // kanthal_coil uniquely identifies this recipe.
        if (hasAtLeast(in, "firearms:hardened_steel_ingot", 8)
                && hasAtLeast(in, "firearms:circuit_board", 2)
                && hasAtLeast(in, "firearms:copper_wire", 4)
                && hasAtLeast(in, "firearms:kanthal_coil", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.EBF_CONTROLLER.get().asItem()),
                    Map.of("firearms:hardened_steel_ingot", 8, "firearms:circuit_board", 2,
                            "firearms:copper_wire", 4, "firearms:kanthal_coil", 4));
        }

        // Assembly Bench (machine): steel×8 + hardened×4 + circuit×2 + copper×4
        if (hasAtLeast(in, "firearms:steel_ingot", 8)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 4)
                && hasAtLeast(in, "firearms:circuit_board", 2)
                && hasAtLeast(in, "firearms:copper_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.ASSEMBLY_BENCH.get().asItem()),
                    Map.of("firearms:steel_ingot", 8, "firearms:hardened_steel_ingot", 4,
                            "firearms:circuit_board", 2, "firearms:copper_wire", 4));
        }

        // Fuel Generator: steel×6 + hardened×2 + copper×4 + circuit×2
        // Must come before Lathe — Lathe needs only copper×3, so copper×4 satisfies Lathe too.
        if (hasAtLeast(in, "firearms:steel_ingot", 6)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:copper_wire", 4)
                && hasAtLeast(in, "firearms:circuit_board", 2)) {
            return new RecipeMatch(new ItemStack(ModBlocks.FUEL_GENERATOR.get().asItem()),
                    Map.of("firearms:steel_ingot", 6, "firearms:hardened_steel_ingot", 2,
                            "firearms:copper_wire", 4, "firearms:circuit_board", 2));
        }

        // Coal Generator: steel×6 + copper×4 + furnace×1 + circuit×1
        // furnace uniquely identifies this recipe; no overlap risk with Lathe.
        if (hasAtLeast(in, "firearms:steel_ingot", 6)
                && hasAtLeast(in, "firearms:copper_wire", 4)
                && hasAtLeast(in, "minecraft:furnace", 1)
                && hasAtLeast(in, "firearms:circuit_board", 1)) {
            return new RecipeMatch(new ItemStack(ModBlocks.COAL_GENERATOR.get().asItem()),
                    Map.of("firearms:steel_ingot", 6, "firearms:copper_wire", 4,
                            "minecraft:furnace", 1, "firearms:circuit_board", 1));
        }

        // Lathe: steel×6 + hardened×2 + circuit×2 + copper×3
        if (hasAtLeast(in, "firearms:steel_ingot", 6)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:circuit_board", 2)
                && hasAtLeast(in, "firearms:copper_wire", 3)) {
            return new RecipeMatch(new ItemStack(ModBlocks.LATHE.get().asItem()),
                    Map.of("firearms:steel_ingot", 6, "firearms:hardened_steel_ingot", 2,
                            "firearms:circuit_board", 2, "firearms:copper_wire", 3));
        }

        // Chemical Mixer (machine): steel×4 + hardened×2 + circuit×2 + copper×3 + glass×2
        // glass uniquely distinguishes this from Lathe even at the same minimum counts.
        if (hasAtLeast(in, "firearms:steel_ingot", 4)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)
                && hasAtLeast(in, "firearms:circuit_board", 2)
                && hasAtLeast(in, "firearms:copper_wire", 3)
                && hasAtLeast(in, "minecraft:glass", 2)) {
            return new RecipeMatch(new ItemStack(ModBlocks.CHEMICAL_MIXER.get().asItem()),
                    Map.of("firearms:steel_ingot", 4, "firearms:hardened_steel_ingot", 2,
                            "firearms:circuit_board", 2, "firearms:copper_wire", 3,
                            "minecraft:glass", 2));
        }

        // ── Intermediate parts ────────────────────────────────────────────────

        // Circuit Board: copper×4 + gold_ingot×1 + redstone×4 + silicon_die×2
        if (hasAtLeast(in, "firearms:copper_wire", 4)
                && hasAtLeast(in, "minecraft:gold_ingot", 1)
                && hasAtLeast(in, "minecraft:redstone", 4)
                && hasAtLeast(in, "firearms:silicon_die", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                    Map.of("firearms:copper_wire", 4, "minecraft:gold_ingot", 1,
                            "minecraft:redstone", 4, "firearms:silicon_die", 2));
        }

        // Explosive Bullet: bullet_casing×4 + refined_gunpowder×2 + tnt×1 → 4x explosive_bullet
        // Must come before Refined Bullet — that recipe also needs bullet_casing + refined_gunpowder.
        if (hasAtLeast(in, "firearms:bullet_casing", 4)
                && hasAtLeast(in, "firearms:refined_gunpowder", 2)
                && hasAtLeast(in, "minecraft:tnt", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.EXPLOSIVE_BULLET.get(), 4),
                    Map.of("firearms:bullet_casing", 4, "firearms:refined_gunpowder", 2,
                            "minecraft:tnt", 1));
        }

        // Refined Bullet: bullet_casing×1 + refined_gunpowder×1 → 8x refined_bullet
        // Consumes exactly 1 of each per craft; extra stacks stay in the bench.
        if (hasAtLeast(in, "firearms:bullet_casing", 1)
                && hasAtLeast(in, "firearms:refined_gunpowder", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.REFINED_BULLET.get(), 8),
                    Map.of("firearms:bullet_casing", 1, "firearms:refined_gunpowder", 1));
        }

        // AP Bullet: tungsten_rod×1 + bullet_casing×1 + propellant_powder×1 → 4x armor_piercing_bullet
        // tungsten_rod distinguishes this from Refined Bullet (no overlap).
        if (hasAtLeast(in, "firearms:tungsten_rod", 1)
                && hasAtLeast(in, "firearms:bullet_casing", 1)
                && hasAtLeast(in, "firearms:propellant_powder", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.ARMOR_PIERCING_BULLET.get(), 4),
                    Map.of("firearms:tungsten_rod", 1, "firearms:bullet_casing", 1,
                            "firearms:propellant_powder", 1));
        }

        // Kanthal Alloy: chromium×1 + iron_ingot×1 + aluminum×1 → 4x kanthal_alloy
        if (hasAtLeast(in, "firearms:chromium_ingot", 1)
                && hasAtLeast(in, "minecraft:iron_ingot", 1)
                && hasAtLeast(in, "firearms:aluminum_ingot", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.KANTHAL_ALLOY.get(), 4),
                    Map.of("firearms:chromium_ingot", 1, "minecraft:iron_ingot", 1,
                            "firearms:aluminum_ingot", 1));
        }

        // ── Nuclear Reactor Stage 1 ───────────────────────────────────────────

        // Fuel Rod: uranium_dioxide_pellet×8 + fuel_rod_cladding×1
        if (hasAtLeast(in, "firearms:uranium_dioxide_pellet", 8)
                && hasAtLeast(in, "firearms:fuel_rod_cladding", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.FUEL_ROD.get()),
                    Map.of("firearms:uranium_dioxide_pellet", 8, "firearms:fuel_rod_cladding", 1));
        }

        // Control Rod: boron_carbide×1 + zirconium_ingot×1 + steel_rod×1
        if (hasAtLeast(in, "firearms:boron_carbide", 1)
                && hasAtLeast(in, "firearms:zirconium_ingot", 1)
                && hasAtLeast(in, "firearms:steel_rod", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.CONTROL_ROD.get()),
                    Map.of("firearms:boron_carbide", 1, "firearms:zirconium_ingot", 1,
                            "firearms:steel_rod", 1));
        }

        // ── Component crafting ────────────────────────────────────────────────
        // Single or dual-ingredient recipes checked last — unique ingredients mean no conflicts above.

        // Gun Barrel: gun_barrel_blank×1 + steel_rod×2
        if (hasAtLeast(in, "firearms:gun_barrel_blank", 1)
                && hasAtLeast(in, "firearms:steel_rod", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.GUN_BARREL.get()),
                    Map.of("firearms:gun_barrel_blank", 1, "firearms:steel_rod", 2));
        }

        // Trigger Assembly: firing_mechanism×1 + firing_pin×1 + spring×1
        if (hasAtLeast(in, "firearms:firing_mechanism", 1)
                && hasAtLeast(in, "firearms:firing_pin", 1)
                && hasAtLeast(in, "firearms:spring", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()),
                    Map.of("firearms:firing_mechanism", 1, "firearms:firing_pin", 1,
                            "firearms:spring", 1));
        }

        // Kanthal Coil: kanthal_wire×4 → kanthal_coil×1
        if (hasAtLeast(in, "firearms:kanthal_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.KANTHAL_COIL.get().asItem()),
                    Map.of("firearms:kanthal_wire", 4));
        }

        // Nichrome Coil: nichrome_wire×4 → nichrome_coil×1
        if (hasAtLeast(in, "firearms:nichrome_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.NICHROME_COIL.get().asItem()),
                    Map.of("firearms:nichrome_wire", 4));
        }

        // Iridium Coil: iridium_wire×4 → iridium_coil×1
        // Checked before Tungsten Coil — iridium_wire is a distinct item, no overlap risk.
        if (hasAtLeast(in, "firearms:iridium_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.IRIDIUM_COIL.get().asItem()),
                    Map.of("firearms:iridium_wire", 4));
        }

        // Tungsten Coil: tungsten_wire×4 → tungsten_coil×1
        if (hasAtLeast(in, "firearms:tungsten_wire", 4)) {
            return new RecipeMatch(new ItemStack(ModBlocks.TUNGSTEN_COIL.get().asItem()),
                    Map.of("firearms:tungsten_wire", 4));
        }

        // Fuel Rod Assembly: fuel_rod×4 → fuel_rod_assembly×1
        if (hasAtLeast(in, "firearms:fuel_rod", 4)) {
            return new RecipeMatch(new ItemStack(ModItems.FUEL_ROD_ASSEMBLY.get()),
                    Map.of("firearms:fuel_rod", 4));
        }

        // ── Containers & utility ──────────────────────────────────────────────
        // AmmoBox: steel×2 + iron×2 + chest×1
        if (hasAtLeast(in, "firearms:steel_ingot", 2)
                && hasAtLeast(in, "minecraft:iron_ingot", 2)
                && hasAtLeast(in, "minecraft:chest", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.AMMO_BOX.get()),
                    Map.of("firearms:steel_ingot", 2, "minecraft:iron_ingot", 2,
                            "minecraft:chest", 1));
        }

        // GunCase: steel×4 + leather×2 + iron×2
        if (hasAtLeast(in, "firearms:steel_ingot", 4)
                && hasAtLeast(in, "minecraft:leather", 2)
                && hasAtLeast(in, "minecraft:iron_ingot", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.GUN_CASE.get()),
                    Map.of("firearms:steel_ingot", 4, "minecraft:leather", 2,
                            "minecraft:iron_ingot", 2));
        }

        // ── Equipment ─────────────────────────────────────────────────────────

        // Riot Shield: steel_plate×4 + rubber_sheet×2 + hardened_steel_ingot×2 → riot_shield
        // steel_plate is a unique ingredient in Assembly Bench; no conflict risk.
        if (hasAtLeast(in, "firearms:steel_plate", 4)
                && hasAtLeast(in, "firearms:rubber_sheet", 2)
                && hasAtLeast(in, "firearms:hardened_steel_ingot", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.RIOT_SHIELD.get()),
                    Map.of("firearms:steel_plate", 4, "firearms:rubber_sheet", 2,
                            "firearms:hardened_steel_ingot", 2));
        }

        // Rubber Boots: rubber_sheet×4 + steel_ingot×2 → rubber_boots
        // rubber_sheet uniquely identifies this recipe; no overlap with existing ammo/machines.
        if (hasAtLeast(in, "firearms:rubber_sheet", 4)
                && hasAtLeast(in, "firearms:steel_ingot", 2)) {
            return new RecipeMatch(new ItemStack(ModItems.RUBBER_BOOTS.get()),
                    Map.of("firearms:rubber_sheet", 4, "firearms:steel_ingot", 2));
        }

        // Cordite Bullet: bullet_casing×4 + cordite×4 → 8x cordite_bullet
        // cordite is unique in Assembly Bench; no conflict.
        if (hasAtLeast(in, "firearms:bullet_casing", 4)
                && hasAtLeast(in, "firearms:cordite", 4)) {
            return new RecipeMatch(new ItemStack(ModItems.CORDITE_BULLET.get(), 8),
                    Map.of("firearms:bullet_casing", 4, "firearms:cordite", 4));
        }

        // Spent Fuel Storage Base: lead_ingot×4 + stone×4 + iron_bars×1 → 4x base
        if (hasAtLeast(in, "firearms:lead_ingot", 4)
                && hasAtLeast(in, "minecraft:stone", 4)
                && hasAtLeast(in, "minecraft:iron_bars", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.SPENT_FUEL_STORAGE_BASE.get(), 4),
                    Map.of("firearms:lead_ingot", 4, "minecraft:stone", 4,
                            "minecraft:iron_bars", 1));
        }

        // ── Pharmaceutical syringes ───────────────────────────────────────────
        // morphine_syringe: syringe×1 + morphine×1
        if (hasAtLeast(in, "firearms:syringe", 1) && hasAtLeast(in, "firearms:morphine", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.MORPHINE_SYRINGE.get()),
                    Map.of("firearms:syringe", 1, "firearms:morphine", 1));
        }

        // adrenaline_syringe: syringe×1 + adrenaline×1
        if (hasAtLeast(in, "firearms:syringe", 1) && hasAtLeast(in, "firearms:adrenaline", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.ADRENALINE_SYRINGE.get()),
                    Map.of("firearms:syringe", 1, "firearms:adrenaline", 1));
        }

        // coagulant_syringe: syringe×1 + coagulant×1
        if (hasAtLeast(in, "firearms:syringe", 1) && hasAtLeast(in, "firearms:coagulant", 1)) {
            return new RecipeMatch(new ItemStack(ModItems.COAGULANT_SYRINGE.get()),
                    Map.of("firearms:syringe", 1, "firearms:coagulant", 1));
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
            if (progress == 0) {
                LOGGER.info("[AssemblyBench]@{} matched: {}", worldPosition.toShortString(),
                        BuiltInRegistries.ITEM.getKey(recipe.result().getItem()));
            }
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
