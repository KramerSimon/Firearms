package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.item.SMGItem;
import com.sio.firearms.keybind.ModKeybinds;
import com.sio.firearms.network.ReloadGunPayload;
import com.sio.firearms.network.ShootGunPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean holdingFire = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ModKeybinds.RELOAD.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            ItemStack held = player.getMainHandItem();
            if (held.getItem() instanceof GunItem) {
                PacketDistributor.sendToServer(new ReloadGunPayload());
            }
        }
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) return;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem)) return;

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                event.setCanceled(true);
                holdingFire = true;
                PacketDistributor.sendToServer(new ShootGunPayload());
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                holdingFire = false;
            }
        }
    }

    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof GunItem && event.isAttack()) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!holdingFire) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) {
            holdingFire = false;
            return;
        }

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof SMGItem)) {
            holdingFire = false;
            return;
        }

        if (!player.getCooldowns().isOnCooldown(held.getItem())) {
            PacketDistributor.sendToServer(new ShootGunPayload());
        }
    }
}
