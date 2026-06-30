package com.sio.firearms.compat.jei;

import java.util.List;

public class VehicleGarageStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int FLOOR_COUNT      = 161; // layers 0-1: 2×81 − 1 controller
    public static final int WALL_COUNT       = 96;  // layers 2-4: 3 × 32 border blocks (wall or door)
    public static final int ROOF_COUNT       = 81;  // layer 5: full 9×9

    public static List<VehicleGarageStructureJeiRecipe> getAllRecipes() {
        return List.of(new VehicleGarageStructureJeiRecipe());
    }
}
