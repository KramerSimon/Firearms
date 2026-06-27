package com.sio.firearms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class WireBlock extends Block implements EntityBlock {

    // Connection properties (whether wire currently connects that direction)
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;

    // Wrench-set block properties — prevent connection on that face
    public static final BooleanProperty BLOCKED_NORTH = BooleanProperty.create("blocked_north");
    public static final BooleanProperty BLOCKED_SOUTH = BooleanProperty.create("blocked_south");
    public static final BooleanProperty BLOCKED_EAST  = BooleanProperty.create("blocked_east");
    public static final BooleanProperty BLOCKED_WEST  = BooleanProperty.create("blocked_west");
    public static final BooleanProperty BLOCKED_UP    = BooleanProperty.create("blocked_up");
    public static final BooleanProperty BLOCKED_DOWN  = BooleanProperty.create("blocked_down");

    private static final VoxelShape CENTER      = Block.box(6, 6,  6, 10, 10, 10);
    private static final VoxelShape NORTH_SHAPE = Block.box(6, 6,  0, 10, 10,  6);
    private static final VoxelShape SOUTH_SHAPE = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape EAST_SHAPE  = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape WEST_SHAPE  = Block.box( 0, 6, 6,  6, 10, 10);
    private static final VoxelShape UP_SHAPE    = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape DOWN_SHAPE  = Block.box(6,  0, 6, 10,  6, 10);

    public WireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST,  false).setValue(WEST,  false)
                .setValue(UP,    false).setValue(DOWN,  false)
                .setValue(BLOCKED_NORTH, false).setValue(BLOCKED_SOUTH, false)
                .setValue(BLOCKED_EAST,  false).setValue(BLOCKED_WEST,  false)
                .setValue(BLOCKED_UP,    false).setValue(BLOCKED_DOWN,  false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN,
                    BLOCKED_NORTH, BLOCKED_SOUTH, BLOCKED_EAST, BLOCKED_WEST, BLOCKED_UP, BLOCKED_DOWN);
    }

    /** Returns the BLOCKED_* property for the given direction. */
    public static BooleanProperty blockedPropFor(Direction dir) {
        return switch (dir) {
            case NORTH -> BLOCKED_NORTH;
            case SOUTH -> BLOCKED_SOUTH;
            case EAST  -> BLOCKED_EAST;
            case WEST  -> BLOCKED_WEST;
            case UP    -> BLOCKED_UP;
            case DOWN  -> BLOCKED_DOWN;
        };
    }

    /** Returns the connection property for the given direction. */
    public static BooleanProperty connPropFor(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case UP    -> UP;
            case DOWN  -> DOWN;
        };
    }

    private boolean canConnect(LevelAccessor level, BlockPos pos, Direction dir, BlockState ownState) {
        if (ownState.getValue(blockedPropFor(dir))) return false;
        BlockPos neighborPos = pos.relative(dir);
        if (level.getBlockState(neighborPos).getBlock() instanceof WireBlock) return true;
        if (level.getBlockEntity(neighborPos) != null) {
            if (level instanceof Level realLevel) {
                var cap = realLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
                return cap != null;
            }
            return true;
        }
        return false;
    }

    /** Recomputes connection properties from the given state (preserving BLOCKED values). */
    private BlockState getConnectionState(LevelAccessor level, BlockPos pos, BlockState existing) {
        return existing
                .setValue(NORTH, canConnect(level, pos, Direction.NORTH, existing))
                .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH, existing))
                .setValue(EAST,  canConnect(level, pos, Direction.EAST,  existing))
                .setValue(WEST,  canConnect(level, pos, Direction.WEST,  existing))
                .setValue(UP,    canConnect(level, pos, Direction.UP,    existing))
                .setValue(DOWN,  canConnect(level, pos, Direction.DOWN,  existing));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getConnectionState(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return getConnectionState(level, pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CENTER;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(EAST))  shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(WEST))  shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(UP))    shape = Shapes.or(shape, UP_SHAPE);
        if (state.getValue(DOWN))  shape = Shapes.or(shape, DOWN_SHAPE);
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof WireBlockEntity wire) {
                wire.serverTick();
            }
        };
    }
}
