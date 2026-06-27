package com.sio.firearms.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CoilBlock extends Block {

    private final int temperature;

    public CoilBlock(int temperature, BlockBehaviour.Properties properties) {
        super(properties);
        this.temperature = temperature;
    }

    public int getTemperature() {
        return temperature;
    }
}
