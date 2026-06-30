package com.sio.firearms.compat.jei;

import com.sio.firearms.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LatheJeiRecipe {

    private final ItemStack primary;
    private final ItemStack secondary; // may be EMPTY
    private final ItemStack output;

    public LatheJeiRecipe(ItemStack primary, ItemStack secondary, ItemStack output) {
        this.primary   = primary;
        this.secondary = secondary;
        this.output    = output;
    }

    public ItemStack getPrimary()   { return primary; }
    public ItemStack getSecondary() { return secondary; }
    public ItemStack getOutput()    { return output; }

    public static List<LatheJeiRecipe> getAllRecipes() {
        return List.of(
            new LatheJeiRecipe(
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.STEEL_ROD.get(), 2)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                new ItemStack(ModItems.GUN_BARREL_BLANK.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.STEEL_INGOT.get()),
                new ItemStack(ModItems.FIRING_MECHANISM.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                new ItemStack(ModItems.CARBON_STEEL.get()),
                new ItemStack(ModItems.SPRING.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.CARBON_STEEL.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.FIRING_PIN.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.HARDENED_STEEL_INGOT.get()),
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.BOLT.get(), 2)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.RUBBER_SHEET.get()),
                new ItemStack(ModItems.BUFFER_TUBE.get())),
            new LatheJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_INGOT.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.TUNGSTEN_ROD.get(), 2)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.KANTHAL_ALLOY.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.KANTHAL_WIRE.get(), 4)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.NICHROME_ALLOY.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.NICHROME_WIRE.get(), 4)),
            new LatheJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_CARBIDE.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.TUNGSTEN_WIRE.get(), 4)),
            // ── Tank Production Chain ─────────────────────────────────────────
            new LatheJeiRecipe(
                new ItemStack(ModItems.TUNGSTEN_CARBIDE.get()),
                new ItemStack(ModItems.STEEL_ROD.get()),
                new ItemStack(ModItems.TANK_CANNON.get())),
            // pvc_pellets → pipe_fitting x2
            new LatheJeiRecipe(
                new ItemStack(ModItems.PVC_PELLETS.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.PIPE_FITTING.get(), 2)),
            // iridium_alloy → iridium_wire x4
            new LatheJeiRecipe(
                new ItemStack(ModItems.IRIDIUM_ALLOY.get()),
                ItemStack.EMPTY,
                new ItemStack(ModItems.IRIDIUM_WIRE.get(), 4)),
            // gold_ingot → gold_wire x2
            new LatheJeiRecipe(
                new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT),
                ItemStack.EMPTY,
                new ItemStack(ModItems.GOLD_WIRE.get(), 2))
        );
    }
}
