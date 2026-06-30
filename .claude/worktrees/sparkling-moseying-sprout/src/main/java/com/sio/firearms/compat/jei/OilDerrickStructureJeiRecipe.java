package com.sio.firearms.compat.jei;

import java.util.List;

public class OilDerrickStructureJeiRecipe {

    public static final int CONTROLLER_COUNT = 1;
    public static final int BASE_COUNT       = 8;  // layer 0: 8 base + 1 controller
    public static final int PILLAR_COUNT     = 5;  // layers 1-5: 1 pillar per layer (centre)

    public static List<OilDerrickStructureJeiRecipe> getAllRecipes() {
        return List.of(new OilDerrickStructureJeiRecipe());
    }
}
