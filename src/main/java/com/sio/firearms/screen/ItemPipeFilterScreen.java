package com.sio.firearms.screen;

import com.sio.firearms.block.ItemPipeBlockEntity.SideMode;
import com.sio.firearms.menu.ItemPipeFilterMenu;
import com.sio.firearms.network.SetItemPipeModePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ItemPipeFilterScreen extends AbstractContainerScreen<ItemPipeFilterMenu> {

    private Button modeButton;

    public ItemPipeFilterScreen(ItemPipeFilterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 174;
    }

    @Override
    protected void init() {
        super.init();

        modeButton = addRenderableWidget(Button.builder(
                buildModeLabel(),
                btn -> {
                    PacketDistributor.sendToServer(
                            new SetItemPipeModePayload(menu.pos, menu.face.ordinal()));
                }
        ).bounds(leftPos + 116, topPos + 22, 52, 18).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        super.render(g, mx, my, partial);
        // Keep the mode button label in sync with the ContainerData
        if (modeButton != null) {
            modeButton.setMessage(buildModeLabel());
        }
        renderTooltip(g, mx, my);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        // Dark panel
        g.fill(leftPos,     topPos,     leftPos + imageWidth,     topPos + imageHeight,     0xD0101018);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF1A1A2E);

        // Filter slot grid background (3×3)
        int gx = leftPos  + 44 - 2;
        int gy = topPos   + 18 - 2;
        g.fill(gx, gy, gx + 3 * 18 + 4, gy + 3 * 18 + 4, 0xFF4A4A6A);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = leftPos + 44 + col * 18;
                int sy = topPos  + 18 + row * 18;
                g.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF0D0D1F);
            }
        }

        // Player inventory separator
        g.fill(leftPos + 8, topPos + 86, leftPos + imageWidth - 8, topPos + 87, 0xFF4A4A6A);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        String dirName = capitalize(menu.face.getSerializedName());
        g.drawString(font, "Item Pipe — " + dirName, 8, 6, 0xFFCCCCDD, false);
        g.drawString(font, "Filter:", 8, 22, 0xFF999999, false);
        g.drawString(font, "Mode:", 116, 14, 0xFF999999, false);
        g.drawString(font, "Inventory", 8, 78, 0xFF999999, false);
    }

    private Component buildModeLabel() {
        SideMode mode = menu.getMode();
        int color = switch (mode) {
            case EXTRACT -> 0xFFFFAA00;
            case INSERT  -> 0xFF00AAFF;
            case NONE    -> 0xFFAAAAAA;
        };
        return Component.literal(mode.displayName());
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
