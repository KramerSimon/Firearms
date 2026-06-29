package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Firearms.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BULLETPROOF_VEST =
            ARMOR_MATERIALS.register("bulletproof_vest", () -> new ArmorMaterial(
                    Map.of(ArmorItem.Type.CHESTPLATE, 8),
                    5,
                    SoundEvents.ARMOR_EQUIP_CHAIN,
                    () -> Ingredient.of(ModItems.RUBBER_SHEET.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "bulletproof_vest"))),
                    2.0f,
                    0.0f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NIGHT_VISION_GOGGLES =
            ARMOR_MATERIALS.register("night_vision_goggles", () -> new ArmorMaterial(
                    Map.of(ArmorItem.Type.HELMET, 4),
                    10,
                    SoundEvents.ARMOR_EQUIP_CHAIN,
                    () -> Ingredient.of(ModItems.CIRCUIT_BOARD.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "night_vision_goggles"))),
                    0.0f,
                    0.0f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HAZMAT =
            ARMOR_MATERIALS.register("hazmat", () -> new ArmorMaterial(
                    Map.of(
                            ArmorItem.Type.HELMET,     4,
                            ArmorItem.Type.CHESTPLATE, 6,
                            ArmorItem.Type.LEGGINGS,   5,
                            ArmorItem.Type.BOOTS,      3
                    ),
                    5,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(ModItems.RUBBER_SHEET.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "hazmat"))),
                    0.5f,
                    0.0f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GAS_MASK =
            ARMOR_MATERIALS.register("gas_mask", () -> new ArmorMaterial(
                    Map.of(ArmorItem.Type.HELMET, 3),
                    8,
                    SoundEvents.ARMOR_EQUIP_CHAIN,
                    () -> Ingredient.of(ModItems.RUBBER_SHEET.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "gas_mask"))),
                    1.0f,
                    0.0f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBBER_BOOTS =
            ARMOR_MATERIALS.register("rubber_boots", () -> new ArmorMaterial(
                    Map.of(ArmorItem.Type.BOOTS, 3),
                    5,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(ModItems.RUBBER_SHEET.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "rubber_boots"))),
                    0.0f,
                    0.0f
            ));
}
