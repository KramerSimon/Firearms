package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GarageDoorBlock extends Block {

    public static final BooleanProperty   OPEN   = BlockStateProperties.OPEN;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SOLID = Block.box(0, 0, 0, 16, 16, 16);
    // Thin 2px hitbox on the door's face side so it remains clickable when open (invisible)
    private static final Map<Direction, VoxelShape> OPEN_SHAPES = Map.of(
        Direction.NORTH, Block.box(0, 0,  0, 16, 16,  2),
        Direction.SOUTH, Block.box(0, 0, 14, 16, 16, 16),
        Direction.EAST,  Block.box(14, 0, 0, 16, 16, 16),
        Direction.WEST,  Block.box(0,  0, 0,  2, 16, 16)
    );

    public GarageDoorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(OPEN, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // FACING = direction player is looking — all doors in one wall face the same way
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(OPEN, false);
    }

    // ── Per-state rendering & collision ──────────────────────────────────────

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(OPEN) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (!state.getValue(OPEN)) return SOLID;
        return OPEN_SHAPES.getOrDefault(state.getValue(FACING), Shapes.empty());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(OPEN) ? Shapes.empty() : SOLID;
    }

    public VoxelShape getOcclusionShape(BlockState state) {
        return state.getValue(OPEN) ? Shapes.empty() : SOLID;
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(OPEN);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        boolean nowOpen = !state.getValue(OPEN);
        Direction facing = state.getValue(FACING);
        org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GarageDoorBlock.class);
        log.info("[GarageDoor] clicked at {} FACING={} -> toggling to open={}", pos, facing, nowOpen);
        toggleConnectedDoors(level, pos, nowOpen, log);
        return InteractionResult.SUCCESS;
    }

    /**
     * BFS from the clicked block in all 6 directions, toggling every connected
     * GarageDoorBlock regardless of its FACING value.
     */
    private static void toggleConnectedDoors(Level level, BlockPos start,
                                             boolean open, org.slf4j.Logger log) {
        Set<BlockPos>   visited = new HashSet<>();
        Queue<BlockPos> queue   = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            BlockState bs = level.getBlockState(current);
            if (!(bs.getBlock() instanceof GarageDoorBlock)) continue;

            log.info("[GarageDoor] BFS toggling {} FACING={}", current, bs.getValue(FACING));
            level.setBlock(current, bs.setValue(OPEN, open), Block.UPDATE_ALL);

            for (Direction dir : Direction.values()) {
                BlockPos nb = current.relative(dir);
                if (visited.add(nb)) {
                    BlockState nbs = level.getBlockState(nb);
                    if (nbs.getBlock() instanceof GarageDoorBlock) {
                        log.info("[GarageDoor] BFS enqueuing neighbor {} FACING={} via {}",
                                nb, nbs.getValue(FACING), dir);
                        queue.add(nb);
                    }
                }
            }
        }
    }
}
