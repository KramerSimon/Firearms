package com.sio.firearms.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Shared blockstate properties reused across unrelated block classes. */
public final class ModBlockStateProperties {

    /** Set true on every shell block of a multiblock once its controller validates the
     *  structure, so the shell can swap to a seamless "connected" texture that reads as
     *  one cohesive machine instead of a grid of individual blocks. */
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private ModBlockStateProperties() {}
}
