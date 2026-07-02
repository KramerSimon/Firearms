package com.sio.firearms.item;

import com.mojang.logging.LogUtils;
import com.sio.firearms.block.FluidPipeBlock;
import com.sio.firearms.block.FluidPipeBlockEntity;
import com.sio.firearms.block.FluidPortBlock;
import com.sio.firearms.block.FluidPortBlockEntity;
import com.sio.firearms.block.IMultiblockPreview;
import com.sio.firearms.block.ItemPipeBlock;
import com.sio.firearms.block.ItemPipeBlockEntity;
import com.sio.firearms.block.LandMineBlock;
import com.sio.firearms.block.LandMineBlockEntity;
import com.sio.firearms.block.WireBlock;
import com.sio.firearms.menu.FluidPipeUnifiedMenu;
import com.sio.firearms.menu.ItemPipeUnifiedMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.items.ItemHandlerHelper;
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

        LOGGER.info("[Wrench] useOn: pos={} face={} block={} isClient={}",
                pos, face.getSerializedName(), block.getClass().getSimpleName(), level.isClientSide());

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockState state = level.getBlockState(pos);

        // ── Item Pipe ──────────────────────────────────────────────────────────
        if (block instanceof ItemPipeBlock) {
            LOGGER.info("[Wrench] ItemPipeBlock at {} → opening unified GUI", pos);
            if (level.getBlockEntity(pos) instanceof ItemPipeBlockEntity pipe
                    && ctx.getPlayer() instanceof ServerPlayer sp) {
                sp.openMenu(
                        new SimpleMenuProvider(
                                (id, inv, pl) -> ItemPipeUnifiedMenu.openFor(id, inv, pos, pipe),
                                Component.literal("Item Pipe")
                        ),
                        buf -> buf.writeBlockPos(pos)
                );
            } else {
                LOGGER.warn("[Wrench] ItemPipeBlock at {} — BE={} player={}",
                        pos,
                        level.getBlockEntity(pos) == null ? "null"
                                : level.getBlockEntity(pos).getClass().getSimpleName(),
                        ctx.getPlayer() == null ? "null"
                                : ctx.getPlayer().getClass().getSimpleName());
            }
            return InteractionResult.SUCCESS;
        }

        // ── Fluid Pipe ─────────────────────────────────────────────────────────
        if (block instanceof FluidPipeBlock) {
            if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity pipe
                    && ctx.getPlayer() instanceof ServerPlayer sp) {

                if (sp.isShiftKeyDown()) {
                    // Shift+right-click → toggle this face's blocked state
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
                    // Normal right-click → open unified GUI for all 6 faces
                    LOGGER.info("[Wrench] FluidPipeBlock at {} → opening unified GUI", pos);
                    sp.openMenu(
                            new SimpleMenuProvider(
                                    (id, inv, pl) -> FluidPipeUnifiedMenu.openFor(id, inv, pos, pipe),
                                    Component.literal("Fluid Pipe")
                            ),
                            buf -> {
                                buf.writeBlockPos(pos);
                                for (Direction d : Direction.values()) {
                                    ResourceLocation f = pipe.getFilterFluid(d);
                                    buf.writeBoolean(f != null);
                                    if (f != null) buf.writeResourceLocation(f);
                                }
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

        // ── Land Mine — reveal a camouflaged mine, returning the dirt/grass used to hide it ──
        if (block instanceof LandMineBlock) {
            if (state.getValue(LandMineBlock.HIDDEN)) {
                level.setBlock(pos, state.setValue(LandMineBlock.HIDDEN, false), 3);
                if (level.getBlockEntity(pos) instanceof LandMineBlockEntity mine) {
                    Item camoItem = mine.takeCamoItem();
                    if (camoItem != null && ctx.getPlayer() != null) {
                        ItemHandlerHelper.giveItemToPlayer(ctx.getPlayer(), new ItemStack(camoItem));
                    }
                }
                level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5f, 1.5f);
                if (ctx.getPlayer() != null) {
                    ctx.getPlayer().displayClientMessage(Component.literal("Land mine revealed"), true);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // ── Multiblock controllers — toggle the ghost structure preview ─────────
        if (level.getBlockEntity(pos) instanceof IMultiblockPreview preview) {
            boolean nowActive = !preview.isPreviewActive();
            preview.setPreviewActive(nowActive);
            if (ctx.getPlayer() != null) {
                String msg = nowActive ? "Structure preview: ON" : "Structure preview: OFF";
                ctx.getPlayer().displayClientMessage(Component.literal(msg), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
