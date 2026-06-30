package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.FluidPortBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetFluidPortModePayload(BlockPos pos, boolean outputMode) implements CustomPacketPayload {

    public static final Type<SetFluidPortModePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "set_fluid_port_mode"));

    public static final StreamCodec<ByteBuf, SetFluidPortModePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetFluidPortModePayload::pos,
                    ByteBufCodecs.BOOL, SetFluidPortModePayload::outputMode,
                    SetFluidPortModePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SetFluidPortModePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            if (level.getBlockEntity(payload.pos()) instanceof FluidPortBlockEntity port) {
                port.setMode(payload.outputMode()
                        ? FluidPortBlockEntity.Mode.OUTPUT
                        : FluidPortBlockEntity.Mode.INPUT);
            }
        });
    }
}
