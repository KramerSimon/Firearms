package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.menu.ItemPipeUnifiedMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchItemPipeFacePayload(BlockPos pos, int newFaceOrdinal)
        implements CustomPacketPayload {

    public static final Type<SwitchItemPipeFacePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "switch_item_pipe_face"));

    public static final StreamCodec<ByteBuf, SwitchItemPipeFacePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SwitchItemPipeFacePayload::pos,
                    ByteBufCodecs.INT, SwitchItemPipeFacePayload::newFaceOrdinal,
                    SwitchItemPipeFacePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SwitchItemPipeFacePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (!(player.containerMenu instanceof ItemPipeUnifiedMenu menu)) return;
            ServerLevel level = player.serverLevel();
            if (level.getBlockEntity(payload.pos()) instanceof ItemPipeBlockEntity pipe) {
                menu.switchFace(payload.newFaceOrdinal(), pipe);
                level.sendBlockUpdated(payload.pos(), level.getBlockState(payload.pos()),
                        level.getBlockState(payload.pos()), 3);
            }
        });
    }
}
