package com.sio.firearms.item;

import com.mojang.logging.LogUtils;
import com.sio.firearms.block.FluidPipeBlock;
import com.sio.firearms.block.FluidPipeBlockEntity;
import com.sio.firearms.block.FluidPortBlock;
import com.sio.firearms.block.FluidPortBlockEntity;
import com.sio.firearms.block.ItemPipeBlock;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.block.WireBlock;
import com.sio.firearms.menu.FluidPipeConfigMenu;
import com.sio.firearms.menu.ItemPipeFilterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

public class WrenchItem extends Item {

    private static final Logger LOGGER = LogUtils.getLogger();

    public WrenchItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Direction face = ctx.getClickedFace();
        Block block = level.getBlockState(pos).getBlock();

        // Log on BOTH sides so we can confirm the method is being called and detect the block correctly
        LOGGER.info("[Wrench] useOn: pos={} face={} block={} isClient={}",
                pos, face.getSerializedName(), block.getClass().getSimpleName(), level.isClientSide());

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockState state = level.getBlockState(pos);

        // ── Item Pipe ──────────────────────────────────────────────────────────
        if (block instanceof ItemPipeBlock) {
            LOGGER.info("[Wrench] Detected ItemPipeBlock at {} — looking up BE and player", pos);
            if (level.getBlockEntity(pos) instanceof ItemPipeBlockEntity pipe
                    && ctx.getPlayer() instanceof ServerPlayer sp) {
                LOGGER.info("[Wrench] Opening ItemPipe filter GUI for face {} at {}", face.getSerializedName(), pos);
                String dirName = capitalize(face.getSerializedName());
                sp.openMenu(
                        new SimpleMenuProvider(
                                (id, inv, pl) -> ItemPipeFilterMenu.openFor(id, inv, pos, face, pipe),
                                Component.literal("Item Pipe — " + dirName)
                        ),
                        buf -> {
                            buf.writeBlockPos(pos);
                            buf.writeByte(face.ordinal());
                        }
                );
            } else {
                LOGGER.warn("[Wrench] ItemPipeBlock at {} — BE={} player={}",
                        pos,
                        level.getBlockEntity(pos) == null ? "null" : level.getBlockEntity(pos).getClass().getSimpleName(),
                        ctx.getPlayer() == null ? "null" : ctx.getPlayer().getClass().getSimpleName());
            }
            return InteractionResult.SUCCESS;
        }

        // ── Fluid Pipe ─────────────────────────────────────────────────────────
        if (block instanceof FluidPipeBlock) {
            LOGGER.info("[Wrench] Detected FluidPipeBlock at {} — face={}", pos, face.getSerializedName());
            if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity pipe
                    && ctx.getPlayer() instanceof ServerPlayer sp) {

                if (sp.isShiftKeyDown()) {
                    // Shift+right-click → toggle face blocked
                    BooleanProperty blockedProp = FluidPipeBlock.blockedPropFor(face);
                    boolean wasBlocked = state.getValue(blockedProp);
                    BlockState withToggle = state.setValue(blockedProp, !wasBlocked);
                    level.setBlock(pos, withToggle, 3);
                    BlockState recomputed = withToggle.updateShape(face,
                            level.getBlockState(pos.relative(face)), level, pos, pos.relative(face));
                    if (recomputed != withToggle) level.setBlock(pos, recomputed, 3);
                    String msg = !wasBlocked
                            ? "Pipe " + face.getSerializedName() + ": blocked"
                            : "Pipe " + face.getSerializedName() + ": open";
                    sp.displayClientMessage(Component.literal(msg), true);
                } else {
                    // Normal right-click → open per-face filter GUI
                    LOGGER.info("[Wrench] Opening FluidPipe filter GUI for face {} at {}", face.getSerializedName(), pos);
                    String dirName = capitalize(face.getSerializedName());
                    ResourceLocation filter = pipe.getFilterFluid(face);
                    sp.openMenu(
                            new SimpleMenuProvider(
                                    (id, inv, pl) -> new FluidPipeConfigMenu(id, inv, pos, face, filter),
                                    Component.literal("Fluid Pipe — " + dirName)
                            ),
                            buf -> {
                                buf.writeBlockPos(pos);
                                buf.writeByte(face.ordinal());
                                ResourceLocation f = pipe.getFilterFluid(face);
                                buf.writeBoolean(f != null);
                                if (f != null) buf.writeResourceLocation(f);
                            }
                    );
                }
            }
            return InteractionResult.SUCCESS;
        }

        // ── Wire ───────────────────────────────────────────────────────────────
        if (block instanceof WireBlock) {
            BooleanProperty blockedProp = WireBlock.blockedPropFor(face);
            boolean wasBlocked = state.getValue(blockedProp);
            BlockState withToggle = state.setValue(blockedProp, !wasBlocked);
            level.setBlock(pos, withToggle, 3);
            BlockState recomputed = withToggle.updateShape(face,
                    level.getBlockState(pos.relative(face)), level, pos, pos.relative(face));
            if (recomputed != withToggle) level.setBlock(pos, recomputed, 3);
            String msg = !wasBlocked
                    ? "Wire " + face.getSerializedName() + ": blocked"
                    : "Wire " + face.getSerializedName() + ": open";
            ctx.getPlayer().displayClientMessage(Component.literal(msg), true);
            return InteractionResult.SUCCESS;
        }

        // ── Fluid Port ─────────────────────────────────────────────────────────
        if (block instanceof FluidPortBlock && ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
            if (level.getBlockEntity(pos) instanceof FluidPortBlockEntity port) {
                port.cycleTargetFluid();
                ctx.getPlayer().displayClientMessage(
                        Component.literal("Fluid Port: Targeting " + port.getTargetFluidDisplayName()), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
