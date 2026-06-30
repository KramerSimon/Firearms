package com.sio.firearms.compat.jei;

import java.util.List;

public class AircraftHangarStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int FLOOR_COUNT      = 120; // layer 0: 11×11 − 1 controller
    public static final int WALL_COUNT       = 160; // layers 1-4: 4 × 40 border blocks (wall or door)
    public static final int ROOF_COUNT       = 121; // layer 5: full 11×11

    public static List<AircraftHangarStructureJeiRecipe> getAllRecipes() {
        return List.of(new AircraftHangarStructureJeiRecipe());
    }
}
