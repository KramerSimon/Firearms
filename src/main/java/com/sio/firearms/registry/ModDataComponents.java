package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Firearms.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AMMO =
            DATA_COMPONENTS.register("ammo", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_AIMING =
            DATA_COMPONENTS.register("is_aiming", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ATTACHMENT =
            DATA_COMPONENTS.register("attachment", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            DATA_COMPONENTS.register("energy", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> UNDERBARREL_ATTACHMENT =
            DATA_COMPONENTS.register("underbarrel_attachment", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> KILL_COUNT =
            DATA_COMPONENTS.register("kill_count", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ARMOR_PIERCING =
            DATA_COMPONENTS.register("armor_piercing", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> USING_REFINED_AMMO =
            DATA_COMPONENTS.register("using_refined_ammo", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLAMETHROWER_FUEL =
            DATA_COMPONENTS.register("flamethrower_fuel", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    // ── Chainsaw ──────────────────────────────────────────────────────────────
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHAINSAW_FUEL =
            DATA_COMPONENTS.register("chainsaw_fuel", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHAINSAW_RUNNING =
            DATA_COMPONENTS.register("chainsaw_running", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    // ── Minigun ───────────────────────────────────────────────────────────────
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINIGUN_ENERGY =
            DATA_COMPONENTS.register("minigun_energy", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINIGUN_BULLETS =
            DATA_COMPONENTS.register("minigun_bullets", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINIGUN_SPIN =
            DATA_COMPONENTS.register("minigun_spin", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    // ── Riot Shield ───────────────────────────────────────────────────────────
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHIELD_BLOCKING =
            DATA_COMPONENTS.register("shield_blocking", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    // ── Ammo types ────────────────────────────────────────────────────────────
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> USING_CORDITE_AMMO =
            DATA_COMPONENTS.register("using_cordite_ammo", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> USING_MATCH_GRADE_AMMO =
            DATA_COMPONENTS.register("using_match_grade_ammo", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> USING_EXPLOSIVE_AMMO =
            DATA_COMPONENTS.register("using_explosive_ammo", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    // ── Battlesuit ────────────────────────────────────────────────────────────
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BATTLESUIT_ENERGY =
            DATA_COMPONENTS.register("battlesuit_energy", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    // ── Containers ────────────────────────────────────────────────────────────
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> AMMO_BOX_ITEMS =
            DATA_COMPONENTS.register("ammo_box_items", () -> DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> GUN_CASE_ITEMS =
            DATA_COMPONENTS.register("gun_case_items", () -> DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build());
}
