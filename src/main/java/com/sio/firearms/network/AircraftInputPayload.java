package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.AircraftEntity;
import io.netty.buffer.ByteBuf;
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

    // Pack 7 booleans into one byte (StreamCodec.composite max is 6)
    public static final StreamCodec<ByteBuf, AircraftInputPayload> STREAM_CODEC = StreamCodec.of(
        (buf, p) -> {
            byte flags = 0;
            if (p.forward()) flags |= 0x01;
            if (p.back())    flags |= 0x02;
            if (p.left())    flags |= 0x04;
            if (p.right())   flags |= 0x08;
            if (p.up())      flags |= 0x10;
            if (p.down())    flags |= 0x20;
            if (p.fire())    flags |= 0x40;
            buf.writeByte(flags);
        },
        buf -> {
            byte flags = buf.readByte();
            return new AircraftInputPayload(
                (flags & 0x01) != 0, (flags & 0x02) != 0,
                (flags & 0x04) != 0, (flags & 0x08) != 0,
                (flags & 0x10) != 0, (flags & 0x20) != 0,
                (flags & 0x40) != 0);
        }
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
