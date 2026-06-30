package com.sio.firearms.item;

import com.sio.firearms.registry.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class BattlesuitItem extends ArmorItem {

    public static final int CAPACITY      = 20_000;
    public static final int CHARGE_AMOUNT = 10_000;
    public static final int MAX_TRANSFER  = 1_000;

    public BattlesuitItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return super.use(level, player, hand);

        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.success(stack);

        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof BatteryItem) {
            int current     = stack.getOrDefault(ModDataComponents.BATTLESUIT_ENERGY.get(), 0);
            int headroom    = CAPACITY - current;
            if (headroom > 0) {
                int want   = Math.min(CHARGE_AMOUNT, headroom);
                int stored = offhand.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                int actual = Math.min(want, stored);
                if (actual > 0) {
                    stack.set(ModDataComponents.BATTLESUIT_ENERGY.get(), current + actual);
                    offhand.set(ModDataComponents.ENERGY.get(), stored - actual);
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> lines, TooltipFlag flag) {
        int energy = stack.getOrDefault(ModDataComponents.BATTLESUIT_ENERGY.get(), 0);
        lines.add(Component.literal("Energy: " + energy + " / " + CAPACITY + " FE"));
        lines.add(Component.literal("Right-click with Battery in offhand to charge (+10,000 FE)"));
        if (energy > 0) {
            lines.add(Component.literal("Full set: 50% damage reduction, fall immunity, speed +20%, jump +30%, night vision"));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) { return true; }

    @Override
    public int getBarWidth(ItemStack stack) {
        int stored = stack.getOrDefault(ModDataComponents.BATTLESUIT_ENERGY.get(), 0);
        return Math.round(13.0f * stored / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) { return 0x00AAFF; }

    // ── Static helpers used by events ─────────────────────────────────────────

    public static boolean hasFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem()  instanceof BattlesuitItem
            && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BattlesuitItem
            && player.getItemBySlot(EquipmentSlot.LEGS).getItem()  instanceof BattlesuitItem
            && player.getItemBySlot(EquipmentSlot.FEET).getItem()  instanceof BattlesuitItem;
    }

    public static int getTotalEnergy(Player player) {
        int total = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack s = player.getItemBySlot(slot);
            if (s.getItem() instanceof BattlesuitItem)
                total += s.getOrDefault(ModDataComponents.BATTLESUIT_ENERGY.get(), 0);
        }
        return total;
    }

    /** Drains {@code amount} FE from the first piece that has energy. Returns amount actually drained. */
    public static int drainEnergy(Player player, int amount) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack s = player.getItemBySlot(slot);
            if (!(s.getItem() instanceof BattlesuitItem)) continue;
            int cur = s.getOrDefault(ModDataComponents.BATTLESUIT_ENERGY.get(), 0);
            if (cur <= 0) continue;
            int drain = Math.min(amount, cur);
            s.set(ModDataComponents.BATTLESUIT_ENERGY.get(), cur - drain);
            return drain;
        }
        return 0;
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
}
