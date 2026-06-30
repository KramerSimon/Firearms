#!/usr/bin/env python3
"""
Generate pipe_unified.png — GUI background texture for the unified pipe config screen.
Run from the repo root:  python generate_pipe_gui.py
Requires Pillow:         pip install Pillow
Output: src/main/resources/assets/firearms/textures/gui/pipe_unified.png
"""
from pathlib import Path
from PIL import Image, ImageDraw

W, H = 256, 204
OUT = Path("src/main/resources/assets/firearms/textures/gui/pipe_unified.png")

img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
d = ImageDraw.Draw(img)

# ── Panel background ────────────────────────────────────────────────────────
d.rectangle([0, 0, W-1, H-1], fill=(16, 16, 24, 208))
d.rectangle([1, 1, W-2, H-2], fill=(26, 26, 46, 255))

# ── Face-net button slots (same positions as FACE_NET_POS in the Java screens)
# Direction.ordinals: DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5
FACE_NET = {
    "Down":  (90, 44),
    "Up":    (90, 12),
    "North": (90, 28),
    "South": (174, 28),
    "West":  (48, 28),
    "East":  (132, 28),
}
BTN_W, BTN_H = 40, 14
for label, (bx, by) in FACE_NET.items():
    # Button well
    d.rectangle([bx-1, by-1, bx+BTN_W, by+BTN_H], fill=(36, 36, 60, 255), outline=(74, 74, 106, 255))
    # Mode-dot placeholder (top-right corner of button)
    d.rectangle([bx+BTN_W-5, by+2, bx+BTN_W-2, by+5], fill=(51, 51, 85, 255))

# ── Separator between face-net section and detail section ───────────────────
d.rectangle([4, 62, W-5, 63], fill=(74, 74, 106, 255))

# ── JEI drop zone (fluid-pipe variant, x=6 y=76, 32×32) ───────────────────
JX, JY, JS = 6, 76, 32
d.rectangle([JX-2, JY-2, JX+JS+1, JY+JS+1], outline=(74, 74, 106, 255))
d.rectangle([JX,   JY,   JX+JS-1, JY+JS-1], fill=(13, 13, 31, 255))

# ── Filter slot grid (3×3, item-pipe variant, x=90 y=62) ───────────────────
GX, GY = 90, 62
d.rectangle([GX-2, GY-2, GX+3*18+2, GY+3*18+2], fill=(74, 74, 106, 255))
for row in range(3):
    for col in range(3):
        sx = GX + col * 18
        sy = GY + row * 18
        d.rectangle([sx-1, sy-1, sx+16, sy+16], fill=(13, 13, 31, 255))

# ── Player inventory separator ──────────────────────────────────────────────
d.rectangle([8, 118, W-9, 119], fill=(74, 74, 106, 255))

# ── Player inventory slots (3 rows × 9 + hotbar, x=47) ─────────────────────
IX = 47
for row in range(3):
    for col in range(9):
        sx = IX + col * 18
        sy = 122 + row * 18
        d.rectangle([sx-1, sy-1, sx+16, sy+16], fill=(13, 13, 31, 255))
for col in range(9):
    sx = IX + col * 18
    d.rectangle([sx-1, 179, sx+16, 196], fill=(13, 13, 31, 255))

OUT.parent.mkdir(parents=True, exist_ok=True)
img.save(OUT)
print(f"Saved {OUT} ({W}×{H})")
