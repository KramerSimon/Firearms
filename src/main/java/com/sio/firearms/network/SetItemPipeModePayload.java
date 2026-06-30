package com.sio.firearms.network;

import com.mojang.logging.LogUtils;
import com.sio.firearms.Firearms;
import com.sio.firearms.block.ItemPipeBlockEntity;
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
import org.slf4j.Logger;

public record SetItemPipeModePayload(BlockPos pos, int faceOrdinal)
        implements CustomPacketPayload {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<SetItemPipeModePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "set_item_pipe_mode"));

    public static final StreamCodec<ByteBuf, SetItemPipeModePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetItemPipeModePayload::pos,
                    ByteBufCodecs.INT, SetItemPipeModePayload::faceOrdinal,
                    SetItemPipeModePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SetItemPipeModePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            Direction face = Direction.values()[payload.faceOrdinal()];
            if (level.getBlockEntity(payload.pos()) instanceof ItemPipeBlockEntity pipe) {
                ItemPipeBlockEntity.SideMode newMode = pipe.cycleSideMode(face);
                pipe.setChanged();
                level.sendBlockUpdated(payload.pos(),
                        level.getBlockState(payload.pos()),
                        level.getBlockState(payload.pos()), 3);
                LOGGER.info("[ItemPipe] SetItemPipeModePayload: cycled {} face {} -> {}",
                        payload.pos(), face.getSerializedName(), newMode.name());
            }
        });
    }
}
