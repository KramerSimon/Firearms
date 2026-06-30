package com.sio.firearms.screen;

import com.sio.firearms.block.ChemicalMixerControllerBlockEntity;
import com.sio.firearms.menu.ChemicalMixerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class ChemicalMixerScreen extends AbstractContainerScreen<ChemicalMixerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/gui/chemical_mixer.png");

    public ChemicalMixerScreen(ChemicalMixerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    private static String fluidName(int fluidId) {
        if (fluidId <= 0) return null;
        var fluid = BuiltInRegistries.FLUID.byId(fluidId);
        if (fluid == null || fluid == Fluids.EMPTY) return null;
        return new FluidStack(fluid, 1).getHoverName().getString();
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int maxP = menu.getMaxProgress();
        if (maxP > 0 && menu.getProgress() > 0) {
            int w = menu.getProgress() * 24 / maxP;
            gui.blit(TEXTURE, leftPos + 75, topPos + 35, 176, 0, w, 16);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int h = menu.getEnergyStored() * 52 / maxE;
            gui.fill(leftPos + 8, topPos + 66 - h, leftPos + 20, topPos + 66, 0xFFCC0000);
        }

        int maxFI = menu.getFluidInMax();
        if (maxFI > 0 && menu.getFluidInAmount() > 0) {
            int h = menu.getFluidInAmount() * 52 / maxFI;
            gui.fill(leftPos + 27, topPos + 66 - h, leftPos + 39, topPos + 66, 0xFF4080FF);
        }

        int maxFO = menu.getFluidOutMax();
        if (maxFO > 0 && menu.getFluidOutAmount() > 0) {
            int h = menu.getFluidOutAmount() * 52 / maxFO;
            gui.fill(leftPos + 141, topPos + 66 - h, leftPos + 153, topPos + 66, 0xFFCCCC00);
        }

        int maxFI2 = menu.getFluidIn2Max();
        if (maxFI2 > 0 && menu.getFluidIn2Amount() > 0) {
            int h = menu.getFluidIn2Amount() * 52 / maxFI2;
            gui.fill(leftPos + 154, topPos + 66 - h, leftPos + 166, topPos + 66, 0xFFFF8800);
        }

        if (!menu.isStructureValid()) {
            gui.drawString(font, "Structure: Invalid", leftPos + 8, topPos + 68, 0xFF5555, false);
        }

        int recipeIdx = menu.getSelectedRecipeIndex();
        String recipeName = recipeIdx < 0 ? "Auto"
                : ChemicalMixerControllerBlockEntity.RECIPE_NAMES[recipeIdx];
        String recipeLabel = "Recipe: " + recipeName;
        gui.drawString(font, "<", leftPos + 8,  topPos + 76, 0xCCCCCC, false);
        gui.drawString(font, ">", leftPos + 162, topPos + 76, 0xCCCCCC, false);
        int tx = leftPos + 22 + (138 - font.width(recipeLabel)) / 2;
        gui.drawString(font, recipeLabel, tx, topPos + 76, 0xFFFFFF, false);

        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int rx = (int) mouseX - leftPos;
            int ry = (int) mouseY - topPos;
            if (ry >= 74 && ry <= 84) {
                if (rx >= 6 && rx <= 16) {
                    Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, 0);
                    return true;
                }
                if (rx >= 160 && rx <= 174) {
                    Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, 1);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        // Progress arrow (75–99, 35–51)
        if (mx >= leftPos + 75 && mx < leftPos + 99 && my >= topPos + 35 && my < topPos + 51) {
            gui.renderTooltip(font,
                    Component.literal("Progress: " + menu.getProgress() + " / " + menu.getMaxProgress() + " ticks"),
                    mx, my);
            return;
        }
        // Energy bar (8–20, top 14, bottom 66)
        if (mx >= leftPos + 8 && mx < leftPos + 20 && my >= topPos + 14 && my <= topPos + 66) {
            gui.renderTooltip(font,
                    Component.literal("Energy: " + menu.getEnergyStored() + " / " + menu.getMaxEnergy() + " FE"),
                    mx, my);
            return;
        }
        // Fluid input 1 bar (27–39, top 14, bottom 66)
        if (mx >= leftPos + 27 && mx < leftPos + 39 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidInAmount();
            if (amt == 0) {
                text = "Input 1: Empty";
            } else {
                String name = fluidName(menu.getFluidInTypeId());
                text = "Input 1: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidInMax() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        // Fluid output bar (141–153, top 14, bottom 66)
        if (mx >= leftPos + 141 && mx < leftPos + 153 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidOutAmount();
            if (amt == 0) {
                text = "Output: Empty";
            } else {
                String name = fluidName(menu.getFluidOutTypeId());
                text = "Output: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidOutMax() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        // Fluid input 2 bar (154–166, top 14, bottom 66)
        if (mx >= leftPos + 154 && mx < leftPos + 166 && my >= topPos + 14 && my <= topPos + 66) {
            String text;
            int amt = menu.getFluidIn2Amount();
            if (amt == 0) {
                text = "Input 2: Empty";
            } else {
                String name = fluidName(menu.getFluidIn2TypeId());
                text = "Input 2: " + (name != null ? name + " " : "") + amt + "/" + menu.getFluidIn2Max() + " mB";
            }
            gui.renderTooltip(font, Component.literal(text), mx, my);
            return;
        }
        super.renderTooltip(gui, mx, my);
    }
}
