package com.sio.firearms.network;

import com.sio.firearms.Firearms;
import com.sio.firearms.item.GunItem;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredHolder;

public record ReloadGunPayload() implements CustomPacketPayload {

    public static final Type<ReloadGunPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "reload_gun"));

    public static final StreamCodec<ByteBuf, ReloadGunPayload> STREAM_CODEC =
            StreamCodec.unit(new ReloadGunPayload());

    // Priority order: AP -> Explosive -> Cordite -> Match Grade -> Refined -> Normal.
    // Earlier entries take priority when the player is carrying multiple ammo types.
    private static final DeferredHolder<Item, ? extends Item>[] AMMO_ITEMS_IN_PRIORITY = ammoPriorityArray();
    private static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>[] AMMO_FLAGS_IN_PRIORITY = ammoFlagPriorityArray();

    @SuppressWarnings("unchecked")
    private static DeferredHolder<Item, ? extends Item>[] ammoPriorityArray() {
        return new DeferredHolder[] {
                ModItems.ARMOR_PIERCING_BULLET,
                ModItems.EXPLOSIVE_BULLET,
                ModItems.CORDITE_BULLET,
                ModItems.MATCH_GRADE_BULLET,
                ModItems.REFINED_BULLET
        };
    }

    @SuppressWarnings("unchecked")
    private static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>[] ammoFlagPriorityArray() {
        return new DeferredHolder[] {
                ModDataComponents.ARMOR_PIERCING,
                ModDataComponents.USING_EXPLOSIVE_AMMO,
                ModDataComponents.USING_CORDITE_AMMO,
                ModDataComponents.USING_MATCH_GRADE_AMMO,
                ModDataComponents.USING_REFINED_AMMO
        };
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReloadGunPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ItemStack held = player.getMainHandItem();
            if (!(held.getItem() instanceof GunItem gunItem)) return;

            int currentAmmo = gunItem.getAmmo(held);
            int maxAmmo = gunItem.getMaxAmmo();
            int needed = maxAmmo - currentAmmo;
            if (needed <= 0) return;

            for (int priority = 0; priority < AMMO_ITEMS_IN_PRIORITY.length; priority++) {
                Item ammoItem = AMMO_ITEMS_IN_PRIORITY[priority].get();

                int available = 0;
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    if (player.getInventory().getItem(i).is(ammoItem))
                        available += player.getInventory().getItem(i).getCount();
                }
                if (available <= 0) continue;

                int toLoad = Math.min(needed, available);
                int remaining = toLoad;
                for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                    ItemStack slot = player.getInventory().getItem(i);
                    if (slot.is(ammoItem)) {
                        int take = Math.min(remaining, slot.getCount());
                        if (!player.isCreative()) slot.shrink(take);
                        remaining -= take;
                    }
                }

                gunItem.setAmmo(held, currentAmmo + toLoad);
                for (int i = 0; i < AMMO_FLAGS_IN_PRIORITY.length; i++) {
                    held.set(AMMO_FLAGS_IN_PRIORITY[i].get(), i == priority);
                }
                return;
            }

            // Fall back to standard bullets
            int available = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (player.getInventory().getItem(i).is(ModItems.BULLET.get()))
                    available += player.getInventory().getItem(i).getCount();
            }

            int toLoad = Math.min(needed, available);
            if (toLoad <= 0) return;

            int remaining = toLoad;
            for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                ItemStack slot = player.getInventory().getItem(i);
                if (slot.is(ModItems.BULLET.get())) {
                    int take = Math.min(remaining, slot.getCount());
                    if (!player.isCreative()) slot.shrink(take);
                    remaining -= take;
                }
            }
            gunItem.setAmmo(held, currentAmmo + toLoad);
            for (DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> flag : AMMO_FLAGS_IN_PRIORITY) {
                held.set(flag.get(), false);
            }
        });
    }
}
