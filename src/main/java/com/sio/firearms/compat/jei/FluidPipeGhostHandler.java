package com.sio.firearms.compat.jei;

import com.sio.firearms.network.SetFluidPipeFilterPayload;
import com.sio.firearms.screen.FluidPipeConfigScreen;
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

public class FluidPipeGhostHandler implements IGhostIngredientHandler<FluidPipeConfigScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(FluidPipeConfigScreen screen,
                                                ITypedIngredient<I> ingredient, boolean doStart) {
        if (ingredient.getType() != NeoForgeTypes.FLUID_STACK) return List.of();

        List<Target<FluidStack>> targets = List.of(new Target<>() {
            @Override
            public Rect2i getArea() {
                return new Rect2i(
                        screen.getGuiLeft() + FluidPipeConfigScreen.GHOST_X,
                        screen.getGuiTop()  + FluidPipeConfigScreen.GHOST_Y,
                        FluidPipeConfigScreen.GHOST_SIZE,
                        FluidPipeConfigScreen.GHOST_SIZE
                );
            }

            @Override
            public void accept(FluidStack fluid) {
                ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
                PacketDistributor.sendToServer(
                        new SetFluidPipeFilterPayload(
                                screen.getMenu().pos,
                                screen.getMenu().face.ordinal(),
                                Optional.ofNullable(key)));
                // Update local menu state so label refreshes without reopening
                screen.getMenu().filterFluid = key;
            }
        });

        @SuppressWarnings("unchecked")
        List<Target<I>> result = (List<Target<I>>) (List<?>) targets;
        return result;
    }

    @Override
    public void onComplete() {}
}
