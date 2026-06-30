package com.sio.firearms.compat.jei;

import java.util.List;

public class CoolingTowerStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int BASE_COUNT       = 24;  // layer 0: 24 base + 1 controller
    public static final int WALL_COUNT       = 96;  // layers 1-6: 6 × 16 border blocks
    public static final int VENT_COUNT       = 25;  // layer 7: full 5×5

    public static List<CoolingTowerStructureJeiRecipe> getAllRecipes() {
        return List.of(new CoolingTowerStructureJeiRecipe());
    }
}
