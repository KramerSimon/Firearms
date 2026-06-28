package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FluidClientSetup {

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/oil_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/oil_flowing");
            }

            @Override
            public int getTintColor() {
                return 0xFF1A1A1A;
            }
        }, ModFluids.OIL_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/fuel_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/fuel_flowing");
            }

            @Override
            public int getTintColor() {
                return 0xFFFF8C00;
            }
        }, ModFluids.FUEL_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/creosote_oil_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/creosote_oil_flowing");
            }

            @Override
            public int getTintColor() {
                return 0xFF4A2800;
            }
        }, ModFluids.CREOSOTE_OIL_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/sulfuric_acid_still");
            }
            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/sulfuric_acid_flowing");
            }
            @Override
            public int getTintColor() { return 0xFF9DB500; }
        }, ModFluids.SULFURIC_ACID_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/nitric_acid_still");
            }
            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/nitric_acid_flowing");
            }
            @Override
            public int getTintColor() { return 0xFFE8E800; }
        }, ModFluids.NITRIC_ACID_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/synthetic_rubber_still");
            }
            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/synthetic_rubber_flowing");
            }
            @Override
            public int getTintColor() { return 0xFF2A2A2A; }
        }, ModFluids.SYNTHETIC_RUBBER_TYPE.get());

        // ── Distillation products ─────────────────────────────────────────────
        registerSimpleFluid(event, "butane",           0xFFB8D4E8, ModFluids.BUTANE_TYPE.get());
        registerSimpleFluid(event, "gasoline",         0xFFFFE680, ModFluids.GASOLINE_TYPE.get());
        registerSimpleFluid(event, "naphtha",          0xFFFFCC44, ModFluids.NAPHTHA_TYPE.get());
        registerSimpleFluid(event, "kerosene",         0xFFFF9900, ModFluids.KEROSENE_TYPE.get());
        registerSimpleFluid(event, "diesel",           0xFFCC7700, ModFluids.DIESEL_TYPE.get());
        registerSimpleFluid(event, "heavy_gas_oil",    0xFF8B4513, ModFluids.HEAVY_GAS_OIL_TYPE.get());
        registerSimpleFluid(event, "residual_fuel_oil",0xFF3D1C00, ModFluids.RESIDUAL_FUEL_OIL_TYPE.get());
        registerSimpleFluid(event, "photoresist",          0xFFCC88FF, ModFluids.PHOTORESIST_TYPE.get());
        // ── Nuclear fluids ────────────────────────────────────────────────────
        registerSimpleFluid(event, "uranium_hexafluoride", 0xFF90EE90, ModFluids.URANIUM_HEXAFLUORIDE_TYPE.get());
        registerSimpleFluid(event, "enriched_uf6",         0xFF00FF7F, ModFluids.ENRICHED_UF6_TYPE.get());
        registerSimpleFluid(event, "depleted_uf6",         0xFF808080, ModFluids.DEPLETED_UF6_TYPE.get());
        registerSimpleFluid(event, "heavy_water",          0xFF9999FF, ModFluids.HEAVY_WATER_TYPE.get());
        registerSimpleFluid(event, "steam",                0xAAFFFFFF, ModFluids.STEAM_TYPE.get());
    }

    private static void registerSimpleFluid(RegisterClientExtensionsEvent event,
                                            String name, int tint,
                                            net.neoforged.neoforge.fluids.FluidType type) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/" + name + "_still");
            }
            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "fluid/" + name + "_flowing");
            }
            @Override
            public int getTintColor() { return tint; }
        }, type);
    }
}
