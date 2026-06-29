package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.TankEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TankInputPayload(boolean forward, boolean back, boolean left, boolean right, boolean fire)
        implements CustomPacketPayload {

    public static final Type<TankInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "tank_input"));

    public static final StreamCodec<ByteBuf, TankInputPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, TankInputPayload::forward,
            ByteBufCodecs.BOOL, TankInputPayload::back,
            ByteBufCodecs.BOOL, TankInputPayload::left,
            ByteBufCodecs.BOOL, TankInputPayload::right,
            ByteBufCodecs.BOOL, TankInputPayload::fire,
            TankInputPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(TankInputPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.getVehicle() instanceof TankEntity tank) {
                tank.setInputState(payload.forward(), payload.back(), payload.left(), payload.right(), payload.fire());
            }
        });
    }
}
