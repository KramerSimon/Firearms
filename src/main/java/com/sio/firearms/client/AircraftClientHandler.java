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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isPassenger()
                || !(mc.player.getVehicle() instanceof AircraftEntity aircraft)) {
            return;
        }

        boolean forward = mc.options.keyUp.isDown();
        boolean back    = mc.options.keyDown.isDown();
        boolean left    = mc.options.keyLeft.isDown();
        boolean right   = mc.options.keyRight.isDown();
        boolean up      = mc.options.keyJump.isDown();
        boolean down    = mc.options.keyShift.isDown();
        boolean fire    = mc.options.keyAttack.isDown();

        // Sent every tick while riding — not just on change — so the server always
        // has fresh input state even across remounts or brief packet handling gaps.
        PacketDistributor.sendToServer(
                new AircraftInputPayload(forward, back, left, right, up, down, fire));

        // HUD: fuel and speed
        int fuel  = aircraft.getFuel();
        float spd = aircraft.getCurrentSpeed();
        mc.player.displayClientMessage(
                Component.literal(String.format("Fuel: %d / %d mB  |  Speed: %.2f b/t",
                        fuel, AircraftEntity.MAX_FUEL, spd)), true);
    }
}
