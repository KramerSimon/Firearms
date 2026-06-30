package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModBlocks;
import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AssemblyBenchJeiRecipe {

    private final NonNullList<ItemStack> inputs;
    private final ItemStack output;

    public AssemblyBenchJeiRecipe(NonNullList<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public NonNullList<ItemStack> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public static List<AssemblyBenchJeiRecipe> getAllRecipes() {
        return List.of(
                createGunBarrelRecipe(),
                createTriggerAssemblyRecipe(),
                // ── Weapons (resource-intensive) ──────────────────────────────
                createPistolRecipe(),
                createShotgunRecipe(),
                createRifleRecipe(),
                createSMGRecipe(),
                createSniperRifleRecipe(),
                createChainsawRecipe(),
                createMinigunRecipe(),
                createAmmoBoxRecipe(),
                createGunCaseRecipe(),
                createAPBulletRecipe(),
                createRefinedBulletRecipe(),
                // ── Intermediate parts ────────────────────────────────────────
                createCircuitBoardRecipe(),
                // ── Machines ─────────────────────────────────────────────────
                createCoalGeneratorRecipe(),
                createFuelGeneratorRecipe(),
                createLatheRecipe(),
                createChemicalMixerMachineRecipe(),
                createAssemblyBenchRecipe(),
                createEBFRecipe(),
                createRefineryWallRecipe(),
                createRefineryControllerRecipe(),
                createKanthalAlloyRecipe(),
                createKanthalCoilRecipe(),
                createNichromeCoilRecipe(),
                createTungstenCoilRecipe(),
                createPhotomaskRecipe(),
                // ── Nuclear Reactor Stage 1 ───────────────────────────────────
                createFuelRodRecipe(),
                createFuelRodAssemblyRecipe(),
                createControlRodRecipe(),
                // ── Tank Production Chain ─────────────────────────────────────
                createTankHullRecipe(),
                createTankTracksRecipe(),
                createTankTurretRecipe(),
                createDieselEngineRecipe(),
                // ── Incendiary / NBC ──────────────────────────────────────────
                createFlamethrowerRecipe(),
                createGasMaskRecipe(),
                createNapalmBombRecipe(),
                createThermiteGrenadeRecipe(),
                // ── Equipment ─────────────────────────────────────────────────
                createRubberBootsRecipe(),
                createRiotShieldRecipe(),
                // ── Military Ammo ─────────────────────────────────────────────
                createCorditeBulletRecipe(),
                createExplosiveBulletRecipe(),
                // ── Spent Fuel Storage ────────────────────────────────────────
                createSpentFuelStorageBaseRecipe(),
                // ── Pharmaceutical syringes ───────────────────────────────────
                createMorphineSyringeRecipe(),
                createAdrenalineSyringeRecipe(),
                createCoagulantSyringeRecipe(),
                // ── Iridium chain ─────────────────────────────────────────────
                createIridiumCoilRecipe(),
                // ── Match Grade Ammunition ────────────────────────────────────
                createMatchGradeBulletRecipe()
        );
    }

    /** Fill a 9-slot list; extra slots remain empty. */
    private static AssemblyBenchJeiRecipe of(ItemStack[] items, ItemStack output) {
        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < items.length && i < 9; i++) {
            inputs.set(i, items[i]);
        }
        return new AssemblyBenchJeiRecipe(inputs, output);
    }

    private static AssemblyBenchJeiRecipe createGunBarrelRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.GUN_BARREL_BLANK.get()),
                new ItemStack(ModItems.STEEL_ROD.get(), 2)
        }, new ItemStack(ModItems.GUN_BARREL.get()));
    }

    private static AssemblyBenchJeiRecipe createTriggerAssemblyRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.FIRING_MECHANISM.get()),
                new ItemStack(ModItems.FIRING_PIN.get()),
                new ItemStack(ModItems.SPRING.get())
        }, new ItemStack(ModItems.TRIGGER_ASSEMBLY.get()));
    }

    private static AssemblyBenchJeiRecipe createPistolRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                new ItemStack(ModItems.BULLET_CASING.get(), 8),
                new ItemStack(ModItems.PROPELLANT_POWDER.get(), 4)
        }, new ItemStack(ModItems.PISTOL.get()));
    }

    private static AssemblyBenchJeiRecipe createShotgunRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 5),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get())
        }, new ItemStack(ModItems.SHOTGUN.get()));
    }

    private static AssemblyBenchJeiRecipe createRifleRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 6),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 3),
                new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                new ItemStack(ModItems.BULLET_CASING.get(), 16)
        }, new ItemStack(ModItems.RIFLE.get()));
    }

    private static AssemblyBenchJeiRecipe createSMGRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get())
        }, new ItemStack(ModItems.SMG.get()));
    }

    private static AssemblyBenchJeiRecipe createSniperRifleRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 6),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.COPPER_WIRE.get(), 3),
                new ItemStack(ModItems.ADVANCED_MICROCHIP.get())
        }, new ItemStack(ModItems.SNIPER_RIFLE.get()));
    }

    private static AssemblyBenchJeiRecipe createCircuitBoardRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.COPPER_WIRE.get(), 4),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.REDSTONE, 4),
                new ItemStack(ModItems.SILICON_DIE.get(), 2)
        }, new ItemStack(ModItems.CIRCUIT_BOARD.get()));
    }

    private static AssemblyBenchJeiRecipe createCoalGeneratorRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 6),
                new ItemStack(ModItems.COPPER_WIRE.get(), 4),
                new ItemStack(Items.FURNACE),
                new ItemStack(ModItems.CIRCUIT_BOARD.get())
        }, new ItemStack(ModBlocks.COAL_GENERATOR.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createFuelGeneratorRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 6),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 4),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2)
        }, new ItemStack(ModBlocks.FUEL_GENERATOR.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createLatheRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 6),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 3)
        }, new ItemStack(ModBlocks.LATHE.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createChemicalMixerMachineRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 3),
                new ItemStack(Items.GLASS, 2)
        }, new ItemStack(ModBlocks.CHEMICAL_MIXER.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createAssemblyBenchRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 8),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 4)
        }, new ItemStack(ModBlocks.ASSEMBLY_BENCH.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createEBFRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 8),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 4),
                new ItemStack(ModBlocks.KANTHAL_COIL.get().asItem(), 4)
        }, new ItemStack(ModBlocks.EBF_CONTROLLER.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createRefineryWallRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 12),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 4),
                new ItemStack(ModItems.COPPER_WIRE.get(), 6),
                new ItemStack(ModItems.STEEL_INGOT.get(), 8)
        }, new ItemStack(ModBlocks.REFINERY_WALL.get().asItem(), 4));
    }

    private static AssemblyBenchJeiRecipe createRefineryControllerRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 8),
                new ItemStack(ModItems.ADVANCED_MICROCHIP.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 4),
                new ItemStack(ModItems.COPPER_WIRE.get(), 4)
        }, new ItemStack(ModBlocks.REFINERY_CONTROLLER.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createAPBulletRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.TUNGSTEN_ROD.get()),
                new ItemStack(ModItems.BULLET_CASING.get()),
                new ItemStack(ModItems.PROPELLANT_POWDER.get())
        }, new ItemStack(ModItems.ARMOR_PIERCING_BULLET.get(), 4));
    }

    private static AssemblyBenchJeiRecipe createRefinedBulletRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.BULLET_CASING.get()),
                new ItemStack(ModItems.REFINED_GUNPOWDER.get())
        }, new ItemStack(ModItems.REFINED_BULLET.get(), 8));
    }

    private static AssemblyBenchJeiRecipe createKanthalAlloyRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.CHROMIUM_INGOT.get()),
                new ItemStack(net.minecraft.world.item.Items.IRON_INGOT),
                new ItemStack(ModItems.ALUMINUM_INGOT.get())
        }, new ItemStack(ModItems.KANTHAL_ALLOY.get(), 4));
    }

    private static AssemblyBenchJeiRecipe createKanthalCoilRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.KANTHAL_WIRE.get()),
                new ItemStack(ModItems.KANTHAL_WIRE.get()),
                new ItemStack(ModItems.KANTHAL_WIRE.get()),
                new ItemStack(ModItems.KANTHAL_WIRE.get())
        }, new ItemStack(ModBlocks.KANTHAL_COIL.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createNichromeCoilRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.NICHROME_WIRE.get()),
                new ItemStack(ModItems.NICHROME_WIRE.get()),
                new ItemStack(ModItems.NICHROME_WIRE.get()),
                new ItemStack(ModItems.NICHROME_WIRE.get())
        }, new ItemStack(ModBlocks.NICHROME_COIL.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createTungstenCoilRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.TUNGSTEN_WIRE.get()),
                new ItemStack(ModItems.TUNGSTEN_WIRE.get()),
                new ItemStack(ModItems.TUNGSTEN_WIRE.get()),
                new ItemStack(ModItems.TUNGSTEN_WIRE.get())
        }, new ItemStack(ModBlocks.TUNGSTEN_COIL.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createPhotomaskRecipe() {
        return of(new ItemStack[]{
                new ItemStack(net.minecraft.world.item.Items.QUARTZ),
                new ItemStack(ModItems.GOLD_FOIL.get()),
                new ItemStack(ModItems.BASIC_MICROCHIP.get())
        }, new ItemStack(ModItems.PHOTOMASK.get()));
    }

    private static AssemblyBenchJeiRecipe createFuelRodRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.URANIUM_DIOXIDE_PELLET.get()),
                new ItemStack(ModItems.FUEL_ROD_CLADDING.get())
        }, new ItemStack(ModItems.FUEL_ROD.get()));
    }

    private static AssemblyBenchJeiRecipe createFuelRodAssemblyRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.FUEL_ROD.get()),
                new ItemStack(ModItems.FUEL_ROD.get()),
                new ItemStack(ModItems.FUEL_ROD.get()),
                new ItemStack(ModItems.FUEL_ROD.get())
        }, new ItemStack(ModItems.FUEL_ROD_ASSEMBLY.get()));
    }

    private static AssemblyBenchJeiRecipe createControlRodRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.BORON_CARBIDE.get()),
                new ItemStack(ModItems.ZIRCONIUM_INGOT.get()),
                new ItemStack(ModItems.STEEL_ROD.get())
        }, new ItemStack(ModItems.CONTROL_ROD.get()));
    }

    // ── Tank Production Chain ─────────────────────────────────────────────────

    private static AssemblyBenchJeiRecipe createTankHullRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_PLATE.get(), 4),
                new ItemStack(ModItems.STEEL_PLATE.get(), 4),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 4)
        }, new ItemStack(ModItems.TANK_HULL.get()));
    }

    private static AssemblyBenchJeiRecipe createTankTracksRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_PLATE.get(), 4),
                new ItemStack(ModItems.STEEL_PLATE.get(), 2),
                new ItemStack(ModItems.RUBBER_SHEET.get(), 4)
        }, new ItemStack(ModItems.TANK_TRACKS.get(), 2));
    }

    private static AssemblyBenchJeiRecipe createTankTurretRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_PLATE.get(), 4),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.ADVANCED_MICROCHIP.get())
        }, new ItemStack(ModItems.TANK_TURRET.get()));
    }

    private static AssemblyBenchJeiRecipe createDieselEngineRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2),
                new ItemStack(ModItems.ADVANCED_MICROCHIP.get())
        }, new ItemStack(ModItems.DIESEL_ENGINE.get()));
    }

    // ── Incendiary / NBC ──────────────────────────────────────────────────────

    private static AssemblyBenchJeiRecipe createFlamethrowerRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2),
                new ItemStack(ModItems.RUBBER_SHEET.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get())
        }, new ItemStack(ModItems.FLAMETHROWER.get()));
    }

    private static AssemblyBenchJeiRecipe createGasMaskRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.RUBBER_SHEET.get(), 3),
                new ItemStack(net.minecraft.world.item.Items.GLASS_PANE),
                new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2)
        }, new ItemStack(ModItems.GAS_MASK.get()));
    }

    private static AssemblyBenchJeiRecipe createChainsawRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                new ItemStack(ModItems.COPPER_WIRE.get(), 2),
                new ItemStack(net.minecraft.world.item.Items.CHAIN, 2)
        }, new ItemStack(ModItems.CHAINSAW.get()));
    }

    private static AssemblyBenchJeiRecipe createMinigunRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 6),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 4),
                new ItemStack(ModItems.ADVANCED_MICROCHIP.get(), 2),
                new ItemStack(ModItems.CIRCUIT_BOARD.get(), 2),
                new ItemStack(ModItems.COPPER_WIRE.get(), 4)
        }, new ItemStack(ModItems.MINIGUN.get()));
    }

    private static AssemblyBenchJeiRecipe createAmmoBoxRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 2),
                new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 2),
                new ItemStack(net.minecraft.world.item.Items.CHEST)
        }, new ItemStack(ModItems.AMMO_BOX.get()));
    }

    private static AssemblyBenchJeiRecipe createGunCaseRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_INGOT.get(), 4),
                new ItemStack(net.minecraft.world.item.Items.LEATHER, 2),
                new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 2)
        }, new ItemStack(ModItems.GUN_CASE.get()));
    }

    private static AssemblyBenchJeiRecipe createRubberBootsRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.RUBBER_SHEET.get(), 4),
                new ItemStack(ModItems.STEEL_INGOT.get(), 2)
        }, new ItemStack(ModItems.RUBBER_BOOTS.get()));
    }

    private static AssemblyBenchJeiRecipe createRiotShieldRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.STEEL_PLATE.get(), 4),
                new ItemStack(ModItems.RUBBER_SHEET.get(), 2),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get(), 2)
        }, new ItemStack(ModItems.RIOT_SHIELD.get()));
    }

    private static AssemblyBenchJeiRecipe createCorditeBulletRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.BULLET_CASING.get(), 4),
                new ItemStack(ModItems.CORDITE.get(), 4)
        }, new ItemStack(ModItems.CORDITE_BULLET.get(), 8));
    }

    private static AssemblyBenchJeiRecipe createExplosiveBulletRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.BULLET_CASING.get(), 4),
                new ItemStack(ModItems.REFINED_GUNPOWDER.get(), 2),
                new ItemStack(Items.TNT)
        }, new ItemStack(ModItems.EXPLOSIVE_BULLET.get(), 4));
    }

    private static AssemblyBenchJeiRecipe createSpentFuelStorageBaseRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.LEAD_INGOT.get(), 4),
                new ItemStack(Items.STONE, 4),
                new ItemStack(Items.IRON_BARS)
        }, new ItemStack(ModItems.SPENT_FUEL_STORAGE_BASE.get(), 4));
    }

    private static AssemblyBenchJeiRecipe createMorphineSyringeRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.SYRINGE.get()),
                new ItemStack(ModItems.MORPHINE.get())
        }, new ItemStack(ModItems.MORPHINE_SYRINGE.get()));
    }

    private static AssemblyBenchJeiRecipe createAdrenalineSyringeRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.SYRINGE.get()),
                new ItemStack(ModItems.ADRENALINE.get())
        }, new ItemStack(ModItems.ADRENALINE_SYRINGE.get()));
    }

    private static AssemblyBenchJeiRecipe createCoagulantSyringeRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.SYRINGE.get()),
                new ItemStack(ModItems.COAGULANT.get())
        }, new ItemStack(ModItems.COAGULANT_SYRINGE.get()));
    }

    private static AssemblyBenchJeiRecipe createIridiumCoilRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.IRIDIUM_WIRE.get(), 4)
        }, new ItemStack(ModBlocks.IRIDIUM_COIL.get().asItem()));
    }

    private static AssemblyBenchJeiRecipe createNapalmBombRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.RESIDUAL_FUEL_OIL_BUCKET.get()),
                new ItemStack(ModItems.GASOLINE_BUCKET.get()),
                new ItemStack(Items.TNT),
                new ItemStack(Items.GLASS_BOTTLE)
        }, new ItemStack(ModItems.NAPALM_BOMB.get(), 2));
    }

    private static AssemblyBenchJeiRecipe createThermiteGrenadeRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.ALUMINUM_INGOT.get(), 2),
                new ItemStack(Items.RAW_IRON),
                new ItemStack(Items.GUNPOWDER, 2)
        }, new ItemStack(ModItems.THERMITE_GRENADE.get(), 2));
    }

    private static AssemblyBenchJeiRecipe createMatchGradeBulletRecipe() {
        return of(new ItemStack[]{
                new ItemStack(ModItems.BULLET_CASING.get(), 4),
                new ItemStack(ModItems.MATCH_GRADE_POWDER.get(), 4),
                new ItemStack(ModItems.TUNGSTEN_ROD.get())
        }, new ItemStack(ModItems.MATCH_GRADE_BULLET.get(), 8));
    }
}
