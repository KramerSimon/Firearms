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
    }
}
