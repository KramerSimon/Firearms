package com.sio.firearms.compat.jei;

import java.util.List;

/**
 * A single "recipe" describing how to assemble the Chemical Mixer 3x3x4 multiblock.
 * All visual layout lives in {@link ChemicalMixerStructureCategory}.
 */
public class ChemicalMixerStructureJeiRecipe {

    // Block counts in the assembled structure.
    public static final int CONTROLLER_COUNT = 1;
    public static final int BASE_COUNT       = 8;   // layer 0: 8 base + 1 controller = 9
    public static final int WALL_COUNT       = 25;  // 8+8 hollow layers + 9 solid cap

    public static List<ChemicalMixerStructureJeiRecipe> getAllRecipes() {
        return List.of(new ChemicalMixerStructureJeiRecipe());
    }
}
