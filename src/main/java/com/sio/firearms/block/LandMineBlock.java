package com.sio.firearms.block;

import com.sio.firearms.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LandMineBlock extends Block implements EntityBlock {

    public static final BooleanProperty ARMED = BooleanProperty.create("armed");
    public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);
    // Thinner than the visible mine's outline — used only while camouflaged (rendering is
    // skipped entirely via RenderShape.INVISIBLE, so this only affects the selection outline).
    private static final VoxelShape HIDDEN_SHAPE = Block.box(0, 0, 0, 16, 0.0625, 16);

    public LandMineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ARMED, false).setValue(HIDDEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ARMED, HIDDEN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HIDDEN) ? HIDDEN_SHAPE : SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Camouflaged mines have no collision at all — players walk over them like normal ground,
        // while entityInside() (position-based, not shape-based) still fires the trigger below.
        return state.getValue(HIDDEN) ? Shapes.empty() : SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(HIDDEN) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LandMineBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof LandMineBlockEntity mine) {
                mine.serverTick();
            }
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && placer instanceof Player player) {
            if (level.getBlockEntity(pos) instanceof LandMineBlockEntity be) {
                be.setPlacer(player.getUUID());
            }
        }
    }

    // Right-click with dirt/grass camouflages the mine (HIDDEN=true); the wrench reverses this
    // in WrenchItem, giving the camo item back via LandMineBlockEntity#takeCamoItem().
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(HIDDEN)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!(stack.is(Items.DIRT) || stack.is(Items.GRASS_BLOCK))) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        Item camoItem = stack.getItem();
        level.setBlock(pos, state.setValue(HIDDEN, true), 3);
        if (level.getBlockEntity(pos) instanceof LandMineBlockEntity mine) {
            mine.setCamoItem(camoItem);
        }
        if (camoItem instanceof BlockItem blockItem) {
            SoundType soundType = blockItem.getBlock().defaultBlockState().getSoundType();
            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        }
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide()) return;
        if (!state.getValue(ARMED)) return;
        if (!level.getBlockState(pos).is(this)) return; // guard against double-detonation same tick

        if (level.getBlockEntity(pos) instanceof LandMineBlockEntity be) {
            if (be.isPlacerImmune(entity)) return;
        }

        triggerExplosion(level, pos);
    }

    private static void triggerExplosion(Level level, BlockPos pos) {
        level.removeBlock(pos, false);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        level.explode(null, cx, cy, cz, 3.5f, Level.ExplosionInteraction.NONE);

        AABB blastArea = new AABB(cx - 3.5, cy - 3.5, cz - 3.5, cx + 3.5, cy + 3.5, cz + 3.5);
        level.getEntitiesOfClass(Player.class, blastArea)
                .forEach(player -> player.addEffect(
                        new MobEffectInstance(ModEffects.BLEEDING, 100, 1)));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }
}
