#!/usr/bin/env python3
"""
Generate all Firearms mod GUI textures (256x256 RGBA PNG) using Pillow.
Run: pip install Pillow && python generate_gui_textures.py
No text is drawn — all labels are rendered by the Java screen classes.
"""

from PIL import Image, ImageDraw
import os

OUT = "src/main/resources/assets/firearms/textures/gui"
os.makedirs(OUT, exist_ok=True)

# Colors (RGBA)
BG         = (198, 185, 154, 255)
SLOT_BG    = (139, 139, 139, 255)
SLOT_DARK  = ( 85,  85,  85, 255)
SLOT_LIGHT = (255, 255, 255, 255)
FLAME_A    = (255, 200,   0, 255)
FLAME_B    = (255, 100,   0, 255)
FLAME_C    = (255, 240, 120, 255)
ENERGY_CLR = (200,  20,  20, 255)
FUEL_CLR   = (255, 140,   0, 255)
OIL_CLR    = ( 15,  15,  15, 255)
STATUS_BG  = ( 70,  70,  70, 255)
BAR_EMPTY  = ( 25,  25,  25, 255)
TRANSP     = (  0,   0,   0,   0)


def new_gui():
    img = Image.new("RGBA", (256, 256), TRANSP)
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, 175, 165], fill=BG)
    return img, draw


def save_gui(img, name):
    path = os.path.join(OUT, name)
    img.save(path)
    print(f"  saved  {name}")


def draw_slot(draw, x, y):
    draw.rectangle([x, y, x + 17, y + 17], fill=SLOT_BG)
    draw.line([(x,      y),      (x + 17, y)     ], fill=SLOT_DARK)
    draw.line([(x,      y),      (x,      y + 17)], fill=SLOT_DARK)
    draw.line([(x,      y + 17), (x + 17, y + 17)], fill=SLOT_LIGHT)
    draw.line([(x + 17, y),      (x + 17, y + 17)], fill=SLOT_LIGHT)


def draw_grid(draw, x, y, cols, rows):
    for r in range(rows):
        for c in range(cols):
            draw_slot(draw, x + c * 18, y + r * 18)


def draw_bar(draw, x, y, w=12, h=52, color=ENERGY_CLR):
    """Draws an empty bar container. Java fills it dynamically at runtime."""
    draw.rectangle([x - 1, y - 1, x + w, y + h], fill=SLOT_DARK)
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=BAR_EMPTY)


def draw_arrow(draw, x, y):
    """Static arrow drawn into the background (non-animated screens)."""
    cy = y + 7
    draw.rectangle([x, cy - 2, x + 13, cy + 2], fill=SLOT_DARK)
    for i in range(8):
        half = 7 - i
        draw.line([(x + 14 + i, cy - half), (x + 14 + i, cy + half)], fill=SLOT_DARK)


def draw_flame_sprite(draw, ux=176, uy=0, w=14, h=14):
    """Flame sprite at UV (ux, uy) for animated blit (coal/fuel generator)."""
    draw.rectangle([ux + 3, uy + 5, ux + w - 5, uy + h - 2], fill=FLAME_B)
    draw.rectangle([ux + 4, uy + 2, ux + w - 6, uy + 9    ], fill=FLAME_A)
    draw.rectangle([ux + 5, uy,     ux + w - 7, uy + 4    ], fill=FLAME_C)
    draw.rectangle([ux + 2, uy + 7, ux + 4,     uy + h - 2], fill=FLAME_B)
    draw.rectangle([ux + w - 5, uy + 7, ux + w - 3, uy + h - 2], fill=FLAME_B)


def draw_flame_slot(draw, x, y, w=14, h=14):
    """Empty flame slot drawn in the main GUI area — flame sprite overlays this at runtime."""
    draw.rectangle([x - 1, y - 1, x + w, y + h], fill=SLOT_DARK)
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=BAR_EMPTY)


def draw_arrow_sprite(draw, ux=176, uy=0, w=24, h=16):
    """Arrow sprite at UV (ux, uy) for animated blit (refinery progress arrow)."""
    cy = uy + h // 2
    draw.rectangle([ux, cy - 2, ux + w - 10, cy + 2], fill=SLOT_DARK)
    for i in range(9):
        half = max(0, (h // 2) - 1 - i)
        draw.line([(ux + w - 9 + i, cy - half), (ux + w - 9 + i, cy + half)], fill=SLOT_DARK)


def draw_arrow_slot(draw, x, y):
    """Empty arrow area in the main GUI — arrow sprite overlays at runtime."""
    draw.rectangle([x - 1, y + 5, x + 25, y + 11], fill=SLOT_DARK)
    draw.rectangle([x, y + 6, x + 24, y + 10], fill=BAR_EMPTY)


def draw_status_box(draw, x, y, w=56, h=40):
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=STATUS_BG)
    draw.line([(x,         y),         (x + w - 1, y)        ], fill=SLOT_DARK)
    draw.line([(x,         y),         (x,         y + h - 1)], fill=SLOT_DARK)
    draw.line([(x + w - 1, y),         (x + w - 1, y + h - 1)], fill=SLOT_LIGHT)
    draw.line([(x,         y + h - 1), (x + w - 1, y + h - 1)], fill=SLOT_LIGHT)


def draw_player_inv(draw, ix=7, iy=83, hx=7, hy=141):
    """Player inventory and hotbar slots — no text."""
    draw_grid(draw, ix, iy, 9, 3)
    draw_grid(draw, hx, hy, 9, 1)


print(f"Generating GUI textures -> {OUT}\n")

# 1. Gunsmith Table
img, draw = new_gui()
draw_grid(draw, 29, 16, 3, 3)
draw_arrow(draw, 97, 34)
draw_slot(draw, 123, 34)
draw_player_inv(draw)
save_gui(img, "gunsmith_table.png")

# 2. Metal Press
img, draw = new_gui()
draw_slot(draw, 56, 17)
draw_slot(draw, 56, 35)
draw_arrow(draw, 88, 34)
draw_slot(draw, 116, 34)
draw_player_inv(draw)
save_gui(img, "metal_press.png")

# 3. Coal Generator
img, draw = new_gui()
draw_slot(draw, 56, 35)
draw_slot(draw, 116, 35)
draw_flame_slot(draw, 79, 34)
draw_bar(draw, 150, 14)
draw_player_inv(draw)
draw_flame_sprite(draw, 176, 0)
save_gui(img, "coal_generator.png")

# 4. Heat Treatment Furnace
img, draw = new_gui()
draw_slot(draw, 56, 26)
draw_slot(draw, 56, 44)
draw_arrow(draw, 88, 34)
draw_slot(draw, 116, 34)
draw_bar(draw, 150, 14)
draw_player_inv(draw)
save_gui(img, "heat_treatment_furnace.png")

# 5. Lathe
img, draw = new_gui()
draw_slot(draw, 56, 26)
draw_slot(draw, 56, 44)
draw_arrow(draw, 88, 34)
draw_slot(draw, 116, 34)
draw_bar(draw, 150, 14)
draw_player_inv(draw)
save_gui(img, "lathe.png")

# 6. Assembly Bench
img, draw = new_gui()
draw_grid(draw, 29, 16, 2, 3)
draw_arrow(draw, 88, 34)
draw_slot(draw, 116, 34)
draw_bar(draw, 150, 14)
draw_player_inv(draw)
save_gui(img, "assembly_bench.png")

# 7. Fuel Generator
img, draw = new_gui()
draw_slot(draw, 56, 26)
draw_slot(draw, 56, 44)
draw_flame_slot(draw, 79, 34)
draw_bar(draw, 7,   14, color=FUEL_CLR)
draw_bar(draw, 150, 14, color=ENERGY_CLR)
draw_player_inv(draw)
draw_flame_sprite(draw, 176, 0)
save_gui(img, "fuel_generator.png")

# 8. Oil Derrick
img, draw = new_gui()
draw_bar(draw, 7,   14, color=ENERGY_CLR)
draw_bar(draw, 150, 14, color=OIL_CLR)
draw_status_box(draw, 60, 20, 56, 40)
draw_player_inv(draw)
save_gui(img, "oil_derrick.png")

# 9. Refinery — 3 output slots: fuel (130,14), gun_oil (130,32), rubber (130,50)
img, draw = new_gui()
draw_bar(draw, 7,   14, color=ENERGY_CLR)
draw_bar(draw, 40,  14, color=OIL_CLR)
draw_arrow_slot(draw, 70, 34)
draw_bar(draw, 100, 14, color=FUEL_CLR)
draw_slot(draw, 130, 14)
draw_slot(draw, 130, 32)
draw_slot(draw, 130, 50)
draw_player_inv(draw)
draw_arrow_sprite(draw, 176, 0)
save_gui(img, "refinery.png")

# 10. Auto Turret
img, draw = new_gui()
draw_slot(draw, 80, 26)
draw_bar(draw, 150, 14, color=ENERGY_CLR)
draw_status_box(draw, 40, 34, 56, 40)
draw_player_inv(draw)
save_gui(img, "auto_turret.png")

# 11. Gun Modification Table
img, draw = new_gui()
draw_slot(draw, 26, 26)
draw_slot(draw, 62, 17)
draw_slot(draw, 62, 53)
draw_arrow(draw, 88, 35)
draw_slot(draw, 124, 35)
draw_player_inv(draw)
save_gui(img, "gun_modification_table.png")

print("\nDone — 11 textures generated.")
