package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Firearms.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Firearms.MOD_ID);
        registrar.playToServer(ReloadGunPayload.TYPE, ReloadGunPayload.STREAM_CODEC, ReloadGunPayload::handle);
        registrar.playToServer(ShootGunPayload.TYPE, ShootGunPayload.STREAM_CODEC, ShootGunPayload::handle);
        registrar.playToServer(TankInputPayload.TYPE, TankInputPayload.STREAM_CODEC, TankInputPayload::handle);
        registrar.playToServer(TankTurretPayload.TYPE, TankTurretPayload.STREAM_CODEC, TankTurretPayload::handle);
        registrar.playToServer(AircraftInputPayload.TYPE, AircraftInputPayload.STREAM_CODEC, AircraftInputPayload::handle);
        registrar.playToServer(SetFluidPortTargetPayload.TYPE, SetFluidPortTargetPayload.STREAM_CODEC, SetFluidPortTargetPayload::handle);
        registrar.playToServer(SetFluidPortModePayload.TYPE, SetFluidPortModePayload.STREAM_CODEC, SetFluidPortModePayload::handle);
        registrar.playToServer(SetFluidPipeFilterPayload.TYPE, SetFluidPipeFilterPayload.STREAM_CODEC, SetFluidPipeFilterPayload::handle);
        registrar.playToServer(SetItemPipeModePayload.TYPE, SetItemPipeModePayload.STREAM_CODEC, SetItemPipeModePayload::handle);
    }
}
