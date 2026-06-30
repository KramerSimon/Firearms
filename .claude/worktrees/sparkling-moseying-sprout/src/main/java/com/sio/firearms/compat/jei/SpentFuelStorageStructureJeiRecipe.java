package com.sio.firearms.compat.jei;

import java.util.List;

public class SpentFuelStorageStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int BASE_COUNT       = 49; // layers 0-1: 25 + 24 (controller in layer 1)
    public static final int WALL_COUNT       = 32; // layers 2-3: 2 × 16 border blocks

    public static List<SpentFuelStorageStructureJeiRecipe> getAllRecipes() {
        return List.of(new SpentFuelStorageStructureJeiRecipe());
    }
}
