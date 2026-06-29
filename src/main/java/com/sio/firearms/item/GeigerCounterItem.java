package com.sio.firearms.item;

import com.sio.firearms.block.ReactorControllerBlockEntity;
import com.sio.firearms.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GeigerCounterItem extends Item {

    private static final int RIGHT_CLICK_RADIUS = 12;
    private static final int PASSIVE_RADIUS      = 8;

    public GeigerCounterItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);

        double radiation = scanRadiation(level, player.blockPosition(), RIGHT_CLICK_RADIUS);
        String label;
        if      (radiation > 80) label = "§4EXTREME";
        else if (radiation > 40) label = "§cHIGH";
        else if (radiation > 10) label = "§6MODERATE";
        else if (radiation > 0)  label = "§eLOW";
        else                     label = "§aNONE";

        player.displayClientMessage(
                Component.literal("☢ Radiation: " + label + " §7(" + String.format("%.1f", radiation) + " rads)"),
                true);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!isSelected || !(entity instanceof Player player)) return;
        // Passive tick sound on client only, every 40 ticks
        if (!level.isClientSide() || level.getGameTime() % 40 != 0) return;

        double radiation = scanRadiation(level, player.blockPosition(), PASSIVE_RADIUS);
        if (radiation > 5) {
            int interval = (int) Math.max(2, 40 - radiation / 2);
            if (level.getGameTime() % interval == 0) {
                level.playSound(player, player.blockPosition(),
                        net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS, 0.4f, 2.0f + (float)(radiation * 0.01));
            }
        }
    }

    private double scanRadiation(Level level, BlockPos center, int radius) {
        double total = 0;
        int hy = Math.max(1, radius / 2);
        for (BlockPos bp : BlockPos.betweenClosed(
                center.offset(-radius, -hy, -radius),
                center.offset( radius,  hy,  radius))) {
            if (level.getBlockEntity(bp) instanceof ReactorControllerBlockEntity reactor) {
                if (reactor.isStructureValid() && reactor.getControlRodCount() == 0) {
                    double distSq = Math.max(1, center.distSqr(bp));
                    total += 1000.0 / distSq;
                }
            }
            if (level.getBlockState(bp).is(ModBlocks.URANIUM_ORE.get())) {
                double distSq = Math.max(1, center.distSqr(bp));
                total += 10.0 / distSq;
            }
        }
        return total;
    }
}
