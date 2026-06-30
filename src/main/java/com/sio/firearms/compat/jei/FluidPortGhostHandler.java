package com.sio.firearms.compat.jei;

import com.sio.firearms.network.SetFluidPortTargetPayload;
import com.sio.firearms.screen.FluidPortConfigScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class FluidPortGhostHandler implements IGhostIngredientHandler<FluidPortConfigScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(FluidPortConfigScreen screen,
                                                ITypedIngredient<I> ingredient, boolean doStart) {
        if (ingredient.getType() != NeoForgeTypes.FLUID_STACK) return List.of();

        List<Target<FluidStack>> targets = List.of(new Target<>() {
            @Override
            public Rect2i getArea() {
                return new Rect2i(
                        screen.getGuiLeft() + FluidPortConfigScreen.GHOST_X,
                        screen.getGuiTop()  + FluidPortConfigScreen.GHOST_Y,
                        FluidPortConfigScreen.GHOST_SIZE,
                        FluidPortConfigScreen.GHOST_SIZE
                );
            }

            @Override
            public void accept(FluidStack fluid) {
                ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
                PacketDistributor.sendToServer(
                        new SetFluidPortTargetPayload(screen.getMenu().pos, Optional.ofNullable(key)));
                // Update local menu state so label refreshes without reopening
                screen.getMenu().targetFluid = key != null ? key.getPath() : "any";
            }
        });

        @SuppressWarnings("unchecked")
        List<Target<I>> result = (List<Target<I>>) (List<?>) targets;
        return result;
    }

    @Override
    public void onComplete() {}
}