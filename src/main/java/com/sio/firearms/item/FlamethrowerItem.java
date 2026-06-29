package com.sio.firearms.item;

import com.sio.firearms.entity.FlameEntity;
import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FlamethrowerItem extends Item {

    public static final int MAX_FUEL     = 5000;
    public static final int FUEL_PER_TICK = 10;

    public FlamethrowerItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getMainHandItem();

        // Offhand butane bucket → refuel 1000 mB
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == ModItems.BUTANE_BUCKET.get()) {
            if (!level.isClientSide()) {
                int current = stack.getOrDefault(ModDataComponents.FLAMETHROWER_FUEL.get(), 0);
                if (current < MAX_FUEL) {
                    int added = Math.min(1000, MAX_FUEL - current);
                    stack.set(ModDataComponents.FLAMETHROWER_FUEL.get(), current + added);
                    if (!player.isCreative()) {
                        offhand.shrink(1);
                        player.addItem(new ItemStack(Items.BUCKET));
                    }
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // Out of fuel → click sound and fail
        int fuel = stack.getOrDefault(ModDataComponents.FLAMETHROWER_FUEL.get(), 0);
        if (fuel <= 0) {
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.2f);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide() || !(entity instanceof Player player)) return;

        int fuel = stack.getOrDefault(ModDataComponents.FLAMETHROWER_FUEL.get(), 0);
        if (fuel <= 0) {
            player.releaseUsingItem();
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8f, 1.2f);
            return;
        }

        // Spawn 3 flame entities in a spread cone each tick
        Vec3 look  = player.getLookAngle();
        Vec3 start = player.getEyePosition().add(look.scale(1.2));
        double spread = 0.12;

        for (int i = 0; i < 3; i++) {
            double dx = look.x + (level.random.nextDouble() - 0.5) * spread;
            double dy = look.y + (level.random.nextDouble() - 0.5) * spread;
            double dz = look.z + (level.random.nextDouble() - 0.5) * spread;
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

            FlameEntity flame = new FlameEntity(level, player);
            flame.setPos(start.x, start.y, start.z);
            flame.setDeltaMovement(dx / len * 0.5, dy / len * 0.5, dz / len * 0.5);
            level.addFreshEntity(flame);
        }

        stack.set(ModDataComponents.FLAMETHROWER_FUEL.get(), Math.max(0, fuel - FUEL_PER_TICK));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int fuel = stack.getOrDefault(ModDataComponents.FLAMETHROWER_FUEL.get(), 0);
        ChatFormatting fuelColor = fuel > 1500 ? ChatFormatting.GOLD : ChatFormatting.RED;
        tooltip.add(Component.literal("Fuel: " + fuel + " / " + MAX_FUEL + " mB").withStyle(fuelColor));
        tooltip.add(Component.literal("Hold right-click to fire").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Butane Bucket in offhand to refuel").withStyle(ChatFormatting.GRAY));
    }
}
