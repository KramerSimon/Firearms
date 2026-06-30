package com.sio.firearms.client;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.AircraftEntity;
import com.sio.firearms.network.AircraftInputPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class AircraftClientHandler {

    private static boolean prevForward, prevBack, prevLeft, prevRight;
    private static boolean prevUp, prevDown, prevFire;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isPassenger()
                || !(mc.player.getVehicle() instanceof AircraftEntity aircraft)) {
            prevForward = prevBack = prevLeft = prevRight = false;
            prevUp = prevDown = prevFire = false;
            return;
        }

        boolean forward = mc.options.keyUp.isDown();
        boolean back    = mc.options.keyDown.isDown();
        boolean left    = mc.options.keyLeft.isDown();
        boolean right   = mc.options.keyRight.isDown();
        boolean up      = mc.options.keyJump.isDown();
        boolean down    = mc.options.keyShift.isDown();
        boolean fire    = mc.options.keyAttack.isDown();

        if (forward != prevForward || back != prevBack || left != prevLeft || right != prevRight
                || up != prevUp || down != prevDown || fire != prevFire) {
            PacketDistributor.sendToServer(
                    new AircraftInputPayload(forward, back, left, right, up, down, fire));
            prevForward = forward; prevBack  = back;
            prevLeft    = left;   prevRight = right;
            prevUp      = up;     prevDown  = down;
            prevFire    = fire;
        }

        // HUD: fuel and speed
        int fuel  = aircraft.getFuel();
        float spd = aircraft.getCurrentSpeed();
        mc.player.displayClientMessage(
                Component.literal(String.format("Fuel: %d / %d mB  |  Speed: %.2f b/t",
                        fuel, AircraftEntity.MAX_FUEL, spd)), true);
    }
}
