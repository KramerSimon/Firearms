package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.item.GunItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShootGunPayload() implements CustomPacketPayload {

    public static final Type<ShootGunPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "shoot_gun"));

    public static final StreamCodec<ByteBuf, ShootGunPayload> STREAM_CODEC =
            StreamCodec.unit(new ShootGunPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShootGunPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack held = player.getMainHandItem();
                if (held.getItem() instanceof GunItem gunItem) {
                    gunItem.shoot(player, player.level());
                }
            }
        });
    }
}
