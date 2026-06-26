package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Firearms.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FIREARMS_TAB =
            CREATIVE_MODE_TABS.register("firearms", () -> CreativeModeTab.builder()
                    .title(Component.literal("Firearms"))
                    .icon(() -> ModItems.STEEL_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STEEL_INGOT.get());
                        output.accept(ModItems.GUN_BARREL.get());
                        output.accept(ModItems.GUN_GRIP.get());
                        output.accept(ModItems.TRIGGER_ASSEMBLY.get());
                        output.accept(ModItems.MAGAZINE.get());
                        output.accept(ModItems.BULLET.get());
                        output.accept(ModItems.REFINED_GUNPOWDER.get());
                        output.accept(ModItems.REFINED_BULLET.get());
                        output.accept(ModItems.HARDENED_STEEL_INGOT.get());
                        output.accept(ModItems.PROPELLANT_POWDER.get());
                        output.accept(ModItems.STEEL_ROD.get());
                        output.accept(ModItems.GUN_BARREL_BLANK.get());
                        output.accept(ModItems.FIRING_MECHANISM.get());
                        output.accept(ModItems.BULLET_CASING.get());
                        output.accept(ModItems.LEAD_INGOT.get());
                        output.accept(ModItems.PISTOL.get());
                        output.accept(ModItems.RIFLE.get());
                        output.accept(ModItems.SHOTGUN.get());
                        output.accept(ModItems.SNIPER_RIFLE.get());
                        output.accept(ModItems.SMG.get());
                        output.accept(ModItems.RED_DOT.get());
                        output.accept(ModItems.HOLO_SIGHT.get());
                        output.accept(ModItems.LASER.get());
                        output.accept(ModItems.FLASHLIGHT.get());
                        output.accept(ModItems.SCOPE_4X.get());
                        output.accept(ModItems.SCOPE_8X.get());
                        output.accept(ModItems.METAL_PRESS.get());
                        output.accept(ModItems.GUN_MODIFICATION_TABLE.get());
                        output.accept(ModItems.CARBON_STEEL.get());
                        output.accept(ModItems.SPRING.get());
                        output.accept(ModItems.FIRING_PIN.get());
                        output.accept(ModItems.COAL_GENERATOR.get());
                        output.accept(ModItems.FUEL_GENERATOR.get());
                        output.accept(ModItems.HEAT_TREATMENT_FURNACE.get());
                        output.accept(ModItems.LATHE.get());
                        output.accept(ModItems.ASSEMBLY_BENCH.get());
                        output.accept(ModItems.ENERGY_PYLON.get());
                        output.accept(ModItems.COPPER_WIRE.get());
                        output.accept(ModItems.BATTERY.get());
                        output.accept(ModItems.RUBBER_SHEET.get());
                        output.accept(ModItems.GUN_OIL.get());
                        output.accept(ModItems.BOLT.get());
                        output.accept(ModItems.BUFFER_TUBE.get());
                        output.accept(ModItems.RUBBER_GRIP.get());
                        output.accept(ModItems.CLEANING_KIT.get());
                        output.accept(ModItems.INDUSTRIAL_LUBRICANT.get());
                        output.accept(ModItems.OIL_BUCKET.get());
                        output.accept(ModItems.FUEL_BUCKET.get());
                        output.accept(ModItems.REFINERY_BASE.get());
                        output.accept(ModItems.REFINERY_WALL.get());
                        output.accept(ModItems.REFINERY_TOP.get());
                        output.accept(ModItems.REFINERY_CONTROLLER.get());
                        output.accept(ModItems.ENERGY_PORT.get());
                        output.accept(ModItems.FLUID_PORT.get());
                        output.accept(ModItems.FLUID_PIPE.get());
                        output.accept(ModItems.ITEM_PIPE.get());
                        output.accept(ModItems.WEAPON_RACK.get());
                        output.accept(ModItems.OIL_DERRICK_BASE.get());
                        output.accept(ModItems.OIL_DERRICK_PILLAR.get());
                        output.accept(ModItems.OIL_DERRICK_CONTROLLER.get());
                        output.accept(ModItems.BANDAGE.get());
                        output.accept(ModItems.MEDIKIT.get());
                        output.accept(ModItems.KEVLAR_PLATE.get());
                        output.accept(ModItems.BULLETPROOF_VEST.get());
                        output.accept(ModItems.NIGHT_VISION_GOGGLES.get());
                        output.accept(ModItems.CIRCUIT_BOARD.get());
                        output.accept(ModItems.ELECTRONIC_TRIGGER.get());
                        output.accept(ModItems.AUTO_TURRET.get());
                        output.accept(ModItems.EXPLOSIVE_COMPOUND.get());
                        output.accept(ModItems.GRENADE.get());
                        output.accept(ModItems.SMOKE_GRENADE.get());
                        output.accept(ModItems.LAND_MINE.get());
                        output.accept(ModItems.SEA_MINE.get());
                        output.accept(ModItems.COAL_COKE.get());
                        output.accept(ModItems.CREOSOTE_OIL_BUCKET.get());
                        output.accept(ModItems.COKE_OVEN_BRICK.get());
                        output.accept(ModItems.COKE_OVEN_CONTROLLER.get());
                        output.accept(ModItems.EBF_BASE.get());
                        output.accept(ModItems.EBF_WALL.get());
                        output.accept(ModItems.EBF_TOP.get());
                        output.accept(ModItems.EBF_CONTROLLER.get());
                        output.accept(ModItems.CHEMICAL_MIXER.get());
                        output.accept(ModItems.ACID_BATH.get());
                        output.accept(ModItems.QUARTZ_SAND.get());
                        output.accept(ModItems.ETCHED_STEEL.get());
                        output.accept(ModItems.ETCHED_COPPER.get());
                        output.accept(ModItems.ETCHED_IRON.get());
                        output.accept(ModItems.SYNTHETIC_RUBBER.get());
                        output.accept(ModItems.SULFURIC_ACID_BUCKET.get());
                        output.accept(ModItems.NITRIC_ACID_BUCKET.get());
                        output.accept(ModItems.SYNTHETIC_RUBBER_BUCKET.get());
                        output.accept(ModItems.WATER_PUMP.get());
                        output.accept(ModItems.SULFUR_ORE.get());
                        output.accept(ModItems.SALTPETER_ORE.get());
                        output.accept(ModItems.TUNGSTEN_ORE.get());
                        output.accept(ModItems.FLUORITE_ORE.get());
                        output.accept(ModItems.URANIUM_ORE.get());
                        output.accept(ModItems.SULFUR.get());
                        output.accept(ModItems.SALTPETER.get());
                        output.accept(ModItems.TUNGSTEN_ORE_RAW.get());
                        output.accept(ModItems.FLUORITE_CRYSTAL.get());
                        output.accept(ModItems.URANIUM_ORE_RAW.get());
                        output.accept(ModItems.TUNGSTEN_INGOT.get());
                        output.accept(ModItems.URANIUM_INGOT.get());
                        output.accept(ModItems.TUNGSTEN_ROD.get());
                        output.accept(ModItems.ARMOR_PIERCING_BULLET.get());
                        output.accept(ModItems.NITROCELLULOSE.get());
                        output.accept(ModItems.ELECTROLYSIS_MACHINE.get());
                        output.accept(ModItems.HYDROGEN_GAS_BUCKET.get());
                        output.accept(ModItems.OXYGEN_GAS_BUCKET.get());
                        output.accept(ModItems.FLUORINE_GAS_BUCKET.get());
                        output.accept(ModItems.CHLORINE_GAS_BUCKET.get());
                        output.accept(ModItems.NITRATE_SOLUTION_BUCKET.get());
                        output.accept(ModItems.PVC_RESIN_BUCKET.get());
                    })
                    .build());
}
