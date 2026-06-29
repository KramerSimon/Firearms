package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.TankEntity;
import com.sio.firearms.network.TankInputPayload;
import com.sio.firearms.network.TankTurretPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TankClientHandler {

    private static boolean prevForward, prevBack, prevLeft, prevRight, prevFire;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isPassenger()
                || !(mc.player.getVehicle() instanceof TankEntity tank)) {
            prevForward = prevBack = prevLeft = prevRight = prevFire = false;
            return;
        }

        boolean forward = mc.options.keyUp.isDown();
        boolean back    = mc.options.keyDown.isDown();
        boolean left    = mc.options.keyLeft.isDown();
        boolean right   = mc.options.keyRight.isDown();
        boolean fire    = mc.options.keyAttack.isDown();

        if (forward != prevForward || back != prevBack
                || left != prevLeft || right != prevRight || fire != prevFire) {
            PacketDistributor.sendToServer(new TankInputPayload(forward, back, left, right, fire));
            prevForward = forward; prevBack  = back;
            prevLeft    = left;    prevRight = right;
            prevFire    = fire;
        }

        // Turret sent every tick for smooth tracking
        float turretYaw   = mc.player.getYRot() - tank.getYRot();
        float turretPitch = mc.player.getXRot();
        PacketDistributor.sendToServer(new TankTurretPayload(turretYaw, turretPitch));

        // Action bar fuel text
        mc.player.displayClientMessage(
                Component.literal("Fuel: " + tank.getFuel() + " / 10000 mB"), true);
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiLayerEvent.Post event) {
        if (!VanillaGuiLayers.HOTBAR.equals(event.getName())) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isPassenger()
                || !(mc.player.getVehicle() instanceof TankEntity tank)) return;

        GuiGraphics gg = event.getGuiGraphics();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        int fuel   = tank.getFuel();
        int barW   = 102;
        int barH   = 8;
        int x      = (sw - barW) / 2;
        int y      = sh - 56;

        // Border
        gg.fill(x - 1, y - 1, x + barW + 1, y + barH + 1, 0xAA000000);
        // Fill
        int filled = barW * fuel / 10000;
        int col = fuel > 3000 ? 0xFF22BB22 : fuel > 1000 ? 0xFFFFAA00 : 0xFFBB2222;
        if (filled > 0) gg.fill(x, y, x + filled, y + barH, col);
        // Label
        String label = "Fuel: " + fuel + " / 10000 mB";
        gg.drawString(mc.font, label, x, y - 11, 0xFFFFFFFF, true);
    }
}
