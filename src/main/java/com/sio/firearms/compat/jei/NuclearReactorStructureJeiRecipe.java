package com.sio.firearms.compat.jei;

import java.util.List;

public class NuclearReactorStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int BASE_COUNT       = 97;  // layers 0-1: 2×49 − 1 controller
    public static final int WALL_COUNT       = 144; // layers 2-7: 6 × 24 border blocks
    public static final int TOP_COUNT        = 49;  // layer 8: full 7×7

    public static List<NuclearReactorStructureJeiRecipe> getAllRecipes() {
        return List.of(new NuclearReactorStructureJeiRecipe());
    }
}
