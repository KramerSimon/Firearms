package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.AircraftEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AircraftInputPayload(
        boolean forward, boolean back, boolean left, boolean right,
        boolean up, boolean down, boolean fire)
        implements CustomPacketPayload {

    public static final Type<AircraftInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "aircraft_input"));

    public static final StreamCodec<ByteBuf, AircraftInputPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AircraftInputPayload::forward,
            ByteBufCodecs.BOOL, AircraftInputPayload::back,
            ByteBufCodecs.BOOL, AircraftInputPayload::left,
            ByteBufCodecs.BOOL, AircraftInputPayload::right,
            ByteBufCodecs.BOOL, AircraftInputPayload::up,
            ByteBufCodecs.BOOL, AircraftInputPayload::down,
            ByteBufCodecs.BOOL, AircraftInputPayload::fire,
            AircraftInputPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(AircraftInputPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.getVehicle() instanceof AircraftEntity aircraft) {
                aircraft.setInputState(
                        payload.forward(), payload.back(),
                        payload.left(),   payload.right(),
                        payload.up(),     payload.down(),
                        payload.fire());
            }
        });
    }
}
