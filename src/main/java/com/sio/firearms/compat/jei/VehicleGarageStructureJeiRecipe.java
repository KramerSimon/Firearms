package com.sio.firearms.compat.jei;

import java.util.List;

public class VehicleGarageStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int FLOOR_COUNT      = 120; // layer 0: 121 − 1 controller
    public static final int WALL_COUNT       = 120; // layers 1-3: 3 × 40 border blocks (wall or door)
    public static final int ROOF_COUNT       = 121; // layer 4: full 11×11

    public static List<VehicleGarageStructureJeiRecipe> getAllRecipes() {
        return List.of(new VehicleGarageStructureJeiRecipe());
    }
}
