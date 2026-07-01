package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.TankEntity;
import com.sio.firearms.network.TankInputPayload;
import com.sio.firearms.network.TankTurretPayload;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TankClientHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isPassenger()
                || !(mc.player.getVehicle() instanceof TankEntity tank)) {
            return;
        }

        boolean forward = mc.options.keyUp.isDown();
        boolean back    = mc.options.keyDown.isDown();
        boolean left    = mc.options.keyLeft.isDown();
        boolean right   = mc.options.keyRight.isDown();
        boolean fire    = mc.options.keyAttack.isDown();

        // Sent every tick while riding — not just on change — so the server always
        // has fresh input state even across remounts or brief packet handling gaps.
        PacketDistributor.sendToServer(new TankInputPayload(forward, back, left, right, fire));

        // Turret sent every tick for smooth tracking
        float turretYaw   = mc.player.getYRot() - tank.getYRot();
        float turretPitch = mc.player.getXRot();
        PacketDistributor.sendToServer(new TankTurretPayload(turretYaw, turretPitch));
    }

    /**
     * In first person, lock the camera's horizontal angle to the hull's yaw rather than the
     * player's own look yaw — the player's mouse aims the turret independently (see
     * {@link #onClientTick}), so the "driver's seat" view should stay fixed to the body while the
     * crosshair (drawn in {@link #onRenderGuiPost}) shows where the turret is actually pointed.
     * Third person is left untouched so the detached camera still orbits normally.
     */
    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (!(mc.player.getVehicle() instanceof TankEntity tank)) return;

        event.setYaw(tank.getBodyYawInterpolated((float) event.getPartialTick()));
    }

    /** Pull the third-person camera back further so the hull doesn't block the forward view. */
    @SubscribeEvent
    public static void onCalculateDetachedCameraDistance(CalculateDetachedCameraDistanceEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof TankEntity) {
            event.setDistance(event.getDistance() + 3.0f);
        }
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiLayerEvent.Post event) {
        if (!VanillaGuiLayers.HOTBAR.equals(event.getName())) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isPassenger()
                || !(mc.player.getVehicle() instanceof TankEntity tank)) return;
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;

        GuiGraphics gg = event.getGuiGraphics();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        int barW = 102;
        int barH = 8;
        int x    = (sw - barW) / 2;
        int y    = sh - 56;

        // Fuel bar
        int fuel = tank.getFuel();
        gg.fill(x - 1, y - 1, x + barW + 1, y + barH + 1, 0xAA000000);
        int filledFuel = barW * fuel / 10000;
        int fuelCol = fuel > 3000 ? 0xFF22BB22 : fuel > 1000 ? 0xFFFFAA00 : 0xFFBB2222;
        if (filledFuel > 0) gg.fill(x, y, x + filledFuel, y + barH, fuelCol);
        gg.drawString(mc.font, "Fuel: " + fuel + " / 10000 mB", x, y - 11, 0xFFFFFFFF, true);

        // Speed indicator (blocks/tick × 20 ticks/second = blocks/second)
        double speed = tank.getDeltaMovement().horizontalDistance() * 20.0;
        String speedLabel = String.format("Speed: %.1f blocks/s", speed);
        gg.drawString(mc.font, speedLabel, x, y - 22, 0xFFFFFFFF, true);

        // Ammo count
        String ammoLabel = "Ammo: " + tank.getAmmo() + " / " + TankEntity.MAX_AMMO;
        gg.drawString(mc.font, ammoLabel, x, y + barH + 3, 0xFFFFFFFF, true);
    }

    /** Hide the vanilla crosshair while driving in first person — a turret-aim reticle replaces it. */
    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.CROSSHAIR.equals(event.getName())) return;
        if (isDrivingInFirstPerson()) event.setCanceled(true);
    }

    /** Draws a crosshair offset horizontally to show where the independently-aimed turret points. */
    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!isDrivingInFirstPerson()) return;
        TankEntity tank = (TankEntity) mc.player.getVehicle();

        float turretYaw = Mth.wrapDegrees(tank.getTurretYaw());
        // Turret is behind the camera — nothing sensible to draw on screen
        if (Math.abs(turretYaw) >= 90f) return;

        GuiGraphics gg = event.getGuiGraphics();
        int sw = gg.guiWidth();
        int sh = gg.guiHeight();
        int cx = sw / 2;
        int cy = sh / 2;

        double fovDegrees = mc.options.fov().get();
        int offsetX = (int) (turretYaw / (fovDegrees * 0.5) * (sw * 0.5));

        int white = 0xFFFFFFFF;
        gg.fill(cx + offsetX - 6, cy - 1, cx + offsetX + 6, cy + 1, white);
        gg.fill(cx + offsetX - 1, cy - 6, cx + offsetX + 1, cy + 6, white);
    }

    private static boolean isDrivingInFirstPerson() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getVehicle() instanceof TankEntity
                && mc.options.getCameraType() == CameraType.FIRST_PERSON;
    }
}
