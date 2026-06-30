package com.sio.firearms.compat.jei;

import java.util.List;

public class RefineryStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int BASE_COUNT       = 24;  // layer 0: 24 base + 1 controller
    public static final int WALL_COUNT       = 64;  // layers 1-4: 4 × 16 border blocks
    public static final int TOP_COUNT        = 25;  // layer 5: full 5×5

    public static List<RefineryStructureJeiRecipe> getAllRecipes() {
        return List.of(new RefineryStructureJeiRecipe());
    }
}
