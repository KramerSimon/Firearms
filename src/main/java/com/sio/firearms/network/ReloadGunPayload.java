package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.registry.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReloadGunPayload() implements CustomPacketPayload {

    public static final Type<ReloadGunPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "reload_gun"));

    public static final StreamCodec<ByteBuf, ReloadGunPayload> STREAM_CODEC =
            StreamCodec.unit(new ReloadGunPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReloadGunPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack held = player.getMainHandItem();
                if (held.getItem() instanceof GunItem gunItem) {
                    int currentAmmo = gunItem.getAmmo(held);
                    int maxAmmo = gunItem.getMaxAmmo();
                    int needed = maxAmmo - currentAmmo;
                    if (needed <= 0) return;

                    int available = 0;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack slot = player.getInventory().getItem(i);
                        if (slot.is(ModItems.BULLET.get())) {
                            available += slot.getCount();
                        }
                    }

                    int toLoad = Math.min(needed, available);
                    if (toLoad <= 0) return;

                    int remaining = toLoad;
                    for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                        ItemStack slot = player.getInventory().getItem(i);
                        if (slot.is(ModItems.BULLET.get())) {
                            int take = Math.min(remaining, slot.getCount());
                            if (!player.isCreative()) {
                                slot.shrink(take);
                            }
                            remaining -= take;
                        }
                    }

                    gunItem.setAmmo(held, currentAmmo + toLoad);
                }
            }
        });
    }
}
