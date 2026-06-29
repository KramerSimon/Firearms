package com.sio.firearms.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;


import java.util.List;

public class GasMaskItem extends ArmorItem {

    public GasMaskItem(Holder<ArmorMaterial> material, Properties props) {
        super(material, Type.HELMET, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("Protects against: Smoke, Poison, Radiation")
                .withStyle(ChatFormatting.GREEN));
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        ChatFormatting durColor = remaining < 100 ? ChatFormatting.RED : ChatFormatting.GRAY;
        tooltip.add(Component.literal("Filter: " + remaining + " / " + stack.getMaxDamage() + " uses")
                .withStyle(durColor));
    }
}
