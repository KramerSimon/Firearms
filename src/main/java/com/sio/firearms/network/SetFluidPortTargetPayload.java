package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.FluidPortBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SetFluidPortTargetPayload(BlockPos pos, Optional<ResourceLocation> fluidKey)
        implements CustomPacketPayload {

    public static final Type<SetFluidPortTargetPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "set_fluid_port_target"));

    public static final StreamCodec<ByteBuf, SetFluidPortTargetPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetFluidPortTargetPayload::pos,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SetFluidPortTargetPayload::fluidKey,
                    SetFluidPortTargetPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SetFluidPortTargetPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            if (level.getBlockEntity(payload.pos()) instanceof FluidPortBlockEntity port) {
                if (payload.fluidKey().isEmpty()) {
                    port.setTargetFluid("any");
                } else {
                    var fluid = BuiltInRegistries.FLUID.getOptional(payload.fluidKey().get()).orElse(null);
                    port.setTargetFluid(port.findCycleKeyForFluid(fluid));
                }
            }
        });
    }
}
