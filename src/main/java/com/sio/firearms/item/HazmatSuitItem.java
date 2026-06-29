package com.sio.firearms.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class HazmatSuitItem extends ArmorItem {

    public HazmatSuitItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    public static boolean hasFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem()  instanceof HazmatSuitItem
            && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof HazmatSuitItem
            && player.getItemBySlot(EquipmentSlot.LEGS).getItem()  instanceof HazmatSuitItem
            && player.getItemBySlot(EquipmentSlot.FEET).getItem()  instanceof HazmatSuitItem;
    }
}
