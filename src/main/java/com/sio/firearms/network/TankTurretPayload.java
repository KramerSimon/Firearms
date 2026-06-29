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

public record TankTurretPayload(float turretYaw, float turretPitch)
        implements CustomPacketPayload {

    public static final Type<TankTurretPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "tank_turret"));

    public static final StreamCodec<ByteBuf, TankTurretPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, TankTurretPayload::turretYaw,
            ByteBufCodecs.FLOAT, TankTurretPayload::turretPitch,
            TankTurretPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(TankTurretPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.getVehicle() instanceof TankEntity tank) {
                tank.setTurretYaw(payload.turretYaw());
                tank.setTurretPitch(payload.turretPitch());
            }
        });
    }
}
