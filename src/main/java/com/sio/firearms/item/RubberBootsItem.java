package com.sio.firearms.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RubberBootsItem extends ArmorItem {

    public RubberBootsItem(DeferredHolder<ArmorMaterial, ArmorMaterial> material, Properties props) {
        super(material, Type.BOOTS, props);
    }
}
