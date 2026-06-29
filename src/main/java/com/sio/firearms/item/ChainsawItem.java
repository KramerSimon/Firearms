package com.sio.firearms.item;

import com.sio.firearms.registry.ModDataComponents;
import com.sio.firearms.registry.ModItems;
import com.sio.firearms.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ChainsawItem extends Item {

    public static final int MAX_FUEL = 2000;
    private static final int FUEL_PER_TICK = 5;

    public ChainsawItem(Properties props) {
        super(props);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("firearms", "chainsaw_attack_speed"),
                                -2.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public boolean isRunning(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(ModDataComponents.CHAINSAW_RUNNING.get()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getMainHandItem();

        // Offhand HGO bucket → refuel 500 mB
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == ModItems.HEAVY_GAS_OIL_BUCKET.get()) {
            if (!level.isClientSide()) {
                int current = stack.getOrDefault(ModDataComponents.CHAINSAW_FUEL.get(), 0);
                int canAdd = Math.min(500, MAX_FUEL - current);
                if (canAdd > 0) {
                    stack.set(ModDataComponents.CHAINSAW_FUEL.get(), current + canAdd);
                    offhand.shrink(1);
                    player.addItem(new ItemStack(Items.BUCKET));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            net.minecraft.sounds.SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // Toggle running
        if (!level.isClientSide()) {
            boolean wasRunning = isRunning(stack);
            int fuel = stack.getOrDefault(ModDataComponents.CHAINSAW_FUEL.get(), 0);
            if (!wasRunning && fuel <= 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.CHAINSAW_EMPTY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                return InteractionResultHolder.fail(stack);
            }
            boolean running = !wasRunning;
            stack.set(ModDataComponents.CHAINSAW_RUNNING.get(), running);
            if (running) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.CHAINSAW_START.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            } else {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.CHAINSAW_STOP.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!isRunning(stack) || !(entity instanceof Player player)) return;

        int fuel = stack.getOrDefault(ModDataComponents.CHAINSAW_FUEL.get(), 0);
        if (fuel <= 0) {
            stack.set(ModDataComponents.CHAINSAW_RUNNING.get(), false);
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.CHAINSAW_EMPTY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.CHAINSAW_STOP.get(), SoundSource.PLAYERS, 0.8f, 0.8f);
            }
            return;
        }

        if (!level.isClientSide()) {
            stack.set(ModDataComponents.CHAINSAW_FUEL.get(), Math.max(0, fuel - FUEL_PER_TICK));
            // Loop sound every 20 ticks
            if (level.getGameTime() % 20 == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.CHAINSAW_RUNNING.get(), SoundSource.PLAYERS, 0.6f, 1.0f);
            }
        } else {
            // Particles on client
            Vec3 look = player.getLookAngle();
            Vec3 tip = player.getEyePosition().add(look.scale(1.0));
            for (int i = 0; i < 2; i++) {
                double ox = (level.random.nextDouble() - 0.5) * 0.4;
                double oy = (level.random.nextDouble() - 0.5) * 0.4;
                double oz = (level.random.nextDouble() - 0.5) * 0.4;
                level.addParticle(ParticleTypes.LARGE_SMOKE, tip.x + ox, tip.y + oy, tip.z + oz,
                        look.x * 0.1, look.y * 0.1, look.z * 0.1);
            }
            if (level.random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.CRIT, tip.x, tip.y, tip.z,
                        (level.random.nextDouble() - 0.5) * 0.3,
                        (level.random.nextDouble() - 0.5) * 0.3,
                        (level.random.nextDouble() - 0.5) * 0.3);
            }
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isRunning(stack) && state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return 1000.0f;
        }
        return 1.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return isRunning(stack) && state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int fuel = stack.getOrDefault(ModDataComponents.CHAINSAW_FUEL.get(), 0);
        boolean running = isRunning(stack);
        ChatFormatting stateColor = running ? ChatFormatting.GREEN : ChatFormatting.RED;
        tooltip.add(Component.literal("State: " + (running ? "Running" : "Off")).withStyle(stateColor));
        ChatFormatting fuelColor = fuel > 600 ? ChatFormatting.GOLD : ChatFormatting.RED;
        tooltip.add(Component.literal("Fuel: " + fuel + " / " + MAX_FUEL + " mB").withStyle(fuelColor));
        tooltip.add(Component.literal("Right-click to toggle | HGO Bucket in offhand to refuel").withStyle(ChatFormatting.GRAY));
    }
}
