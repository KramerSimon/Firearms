package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.attachment.AttachmentType;
import com.sio.firearms.keybind.ModKeybinds;
import com.sio.firearms.registry.ModBlockEntities;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModEntities;
import com.sio.firearms.registry.ModItems;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.WEAPON_RACK.get(), WeaponRackRenderer::new);
        event.registerEntityRenderer(ModEntities.BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.GRENADE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.SMOKE_GRENADE.get(), ThrownItemRenderer::new);
        // SeaMineEntity extends Entity (not ThrowableItemProjectile) but implements ItemSupplier,
        // so raw-type cast is required to use ThrownItemRenderer here.
        event.registerEntityRenderer((EntityType) ModEntities.SEA_MINE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.TANK.get(), TankRenderer::new);
        event.registerEntityRenderer(ModEntities.MOLOTOV.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.NITROGLYCERIN.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.NAPALM_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.THERMITE_GRENADE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer((EntityType) ModEntities.NAPALM_FIRE_PATCH.get(),
                ctx -> new net.minecraft.client.renderer.entity.EntityRenderer<>(ctx) {
                    @Override public net.minecraft.resources.ResourceLocation getTextureLocation(
                            net.minecraft.world.entity.Entity e) {
                        return net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
                    }
                });
        event.registerEntityRenderer((EntityType) ModEntities.FLAME.get(),
                ctx -> new net.minecraft.client.renderer.entity.EntityRenderer<>(ctx) {
                    @Override public net.minecraft.resources.ResourceLocation getTextureLocation(
                            net.minecraft.world.entity.Entity e) {
                        return net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
                    }
                });
        event.registerEntityRenderer((EntityType) ModEntities.FIRE_PATCH.get(),
                ctx -> new net.minecraft.client.renderer.entity.EntityRenderer<>(ctx) {
                    @Override public net.minecraft.resources.ResourceLocation getTextureLocation(
                            net.minecraft.world.entity.Entity e) {
                        return net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
                    }
                });
        // Shell moves at 3 b/t — renders as invisible (no model); NoopRenderer equivalent via empty EntityRenderer
        event.registerEntityRenderer((EntityType) ModEntities.TANK_CANNON_SHELL.get(),
                ctx -> new net.minecraft.client.renderer.entity.EntityRenderer<>(ctx) {
                    @Override public net.minecraft.resources.ResourceLocation getTextureLocation(
                            net.minecraft.world.entity.Entity e) {
                        return net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
                    }
                });
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.RELOAD);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ResourceLocation attachmentId = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "attachment");
            ResourceLocation underbarrelId = ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "underbarrel");

            ItemProperties.register(ModItems.PISTOL.get(), attachmentId, (stack, level, entity, seed) -> {
                String attachment = stack.get(ModDataComponents.ATTACHMENT.get());
                if (attachment == null) return 0.0f;
                AttachmentType type = AttachmentType.fromName(attachment);
                return type != null ? type.getPredicateValue() : 0.0f;
            });

            ItemProperties.register(ModItems.RIFLE.get(), attachmentId, (stack, level, entity, seed) -> {
                String attachment = stack.get(ModDataComponents.ATTACHMENT.get());
                if (attachment == null) return 0.0f;
                AttachmentType type = AttachmentType.fromName(attachment);
                return type != null ? type.getPredicateValue() : 0.0f;
            });

            ItemProperties.register(ModItems.PISTOL.get(), underbarrelId, (stack, level, entity, seed) -> {
                String underbarrel = stack.get(ModDataComponents.UNDERBARREL_ATTACHMENT.get());
                if (underbarrel == null) return 0.0f;
                AttachmentType type = AttachmentType.fromName(underbarrel);
                return type != null ? type.getUnderbarrelValue() : 0.0f;
            });

            ItemProperties.register(ModItems.RIFLE.get(), underbarrelId, (stack, level, entity, seed) -> {
                String underbarrel = stack.get(ModDataComponents.UNDERBARREL_ATTACHMENT.get());
                if (underbarrel == null) return 0.0f;
                AttachmentType type = AttachmentType.fromName(underbarrel);
                return type != null ? type.getUnderbarrelValue() : 0.0f;
            });
        });
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        try {
            String[] pistolSights = {"red_dot", "holo_sight", "rubber_grip"};
            String[] rifleSights = {"red_dot", "holo_sight", "scope_4x", "scope_8x", "rubber_grip"};
            String[] underbarrels = {"laser", "flashlight"};

            // Pistol: sight-only models
            for (String sight : pistolSights) {
                registerModel(event, "pistol_" + sight);
            }
            // Pistol: underbarrel-only models
            for (String ub : underbarrels) {
                registerModel(event, "pistol_" + ub);
            }
            // Pistol: sight + underbarrel combinations
            for (String sight : pistolSights) {
                for (String ub : underbarrels) {
                    registerModel(event, "pistol_" + sight + "_" + ub);
                }
            }

            // Rifle: sight-only models
            for (String sight : rifleSights) {
                registerModel(event, "rifle_" + sight);
            }
            // Rifle: underbarrel-only models
            for (String ub : underbarrels) {
                registerModel(event, "rifle_" + ub);
            }
            // Rifle: sight + underbarrel combinations
            for (String sight : rifleSights) {
                for (String ub : underbarrels) {
                    registerModel(event, "rifle_" + sight + "_" + ub);
                }
            }
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error("Failed to register additional models for Firearms", e);
        }
    }

    private static void registerModel(ModelEvent.RegisterAdditional event, String name) {
        event.register(new ModelResourceLocation(
                ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "item/" + name), "inventory"));
    }
}
