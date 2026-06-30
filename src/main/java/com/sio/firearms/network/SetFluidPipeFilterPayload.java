package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.FluidPipeBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SetFluidPipeFilterPayload(BlockPos pos, int faceOrdinal, Optional<ResourceLocation> filterFluid)
        implements CustomPacketPayload {

    public static final Type<SetFluidPipeFilterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "set_fluid_pipe_filter"));

    public static final StreamCodec<ByteBuf, SetFluidPipeFilterPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetFluidPipeFilterPayload::pos,
                    ByteBufCodecs.INT, SetFluidPipeFilterPayload::faceOrdinal,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SetFluidPipeFilterPayload::filterFluid,
                    SetFluidPipeFilterPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SetFluidPipeFilterPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            if (level.getBlockEntity(payload.pos()) instanceof FluidPipeBlockEntity pipe) {
                Direction face = Direction.values()[payload.faceOrdinal()];
                pipe.setFilterFluid(face, payload.filterFluid().orElse(null));
                pipe.setChanged();
                level.sendBlockUpdated(payload.pos(),
                        level.getBlockState(payload.pos()),
                        level.getBlockState(payload.pos()), 3);
            }
        });
    }
}
