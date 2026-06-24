package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.attachment.AttachmentType;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class OverlayRenderer {

    private static float aimProgress = 0.0f;
    private static float prevAimProgress = 0.0f;
    private static final float AIM_SPEED = 0.25f;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        prevAimProgress = aimProgress;

        if (player == null) {
            aimProgress = 0.0f;
            return;
        }

        boolean shouldAim = false;
        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof GunItem gunItem && gunItem.isAiming(held)) {
            String attachmentName = held.get(ModDataComponents.ATTACHMENT.get());
            AttachmentType attachment = attachmentName != null ? AttachmentType.fromName(attachmentName) : null;
            boolean hasScope = attachment == AttachmentType.SCOPE_4X || attachment == AttachmentType.SCOPE_8X;
            if (!hasScope) {
                shouldAim = true;
            }
        }

        float target = shouldAim ? 1.0f : 0.0f;
        if (aimProgress < target) {
            aimProgress = Math.min(target, aimProgress + AIM_SPEED);
        } else if (aimProgress > target) {
            aimProgress = Math.max(target, aimProgress - AIM_SPEED);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (aimProgress <= 0.0f && prevAimProgress <= 0.0f) return;

        float partialTick = event.getPartialTick();
        float progress = prevAimProgress + (aimProgress - prevAimProgress) * partialTick;
        if (progress <= 0.001f) return;

        float adsY = 0.1f;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack held = mc.player.getMainHandItem();
            String attachmentName = held.get(ModDataComponents.ATTACHMENT.get());
            if (attachmentName != null) {
                AttachmentType attachment = AttachmentType.fromName(attachmentName);
                if (attachment == AttachmentType.RED_DOT || attachment == AttachmentType.HOLO_SIGHT) {
                    adsY = 0.02f;
                }
            }
        }

        event.getPoseStack().translate(progress * -0.58f, progress * adsY, progress * -0.2f);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem gunItem)) return;
        if (!gunItem.isAiming(held)) return;

        double fov = event.getFOV();
        float multiplier = 0.7f;

        String attachmentName = held.get(ModDataComponents.ATTACHMENT.get());
        if (attachmentName != null) {
            AttachmentType attachment = AttachmentType.fromName(attachmentName);
            if (attachment != null) {
                multiplier = attachment.getFovMultiplier();
            }
        }

        event.setFOV(fov * multiplier);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (getActiveScopeType() == null) return;

        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        AttachmentType scopeType = getActiveScopeType();
        if (scopeType == null) return;

        GuiGraphics gui = event.getGuiGraphics();
        int screenWidth = gui.guiWidth();
        int screenHeight = gui.guiHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int radius = scopeType == AttachmentType.SCOPE_8X ? 60 : 80;
        int black = 0xFF000000;

        // Top
        gui.fill(0, 0, screenWidth, centerY - radius, black);
        // Bottom
        gui.fill(0, centerY + radius, screenWidth, screenHeight, black);
        // Left
        gui.fill(0, centerY - radius, centerX - radius, centerY + radius, black);
        // Right
        gui.fill(centerX + radius, centerY - radius, screenWidth, centerY + radius, black);

        int white = 0xFFFFFFFF;

        // Horizontal crosshair
        gui.fill(centerX - radius, centerY - 1, centerX + radius, centerY + 1, white);
        // Vertical crosshair
        gui.fill(centerX - 1, centerY - radius, centerX + 1, centerY + radius, white);
    }

    private static AttachmentType getActiveScopeType() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return null;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem gunItem)) return null;
        if (!gunItem.isAiming(held)) return null;

        String attachmentName = held.get(ModDataComponents.ATTACHMENT.get());
        if (attachmentName == null) return null;

        AttachmentType type = AttachmentType.fromName(attachmentName);
        if (type == AttachmentType.SCOPE_4X || type == AttachmentType.SCOPE_8X) {
            return type;
        }
        return null;
    }
}
