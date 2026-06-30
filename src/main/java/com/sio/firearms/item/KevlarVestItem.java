package com.sio.firearms.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class KevlarVestItem extends ArmorItem {

    public KevlarVestItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Damage Reduction: 70%").withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(Component.literal("Bleeding Protection: Yes").withStyle(ChatFormatting.GREEN));
    }
}
