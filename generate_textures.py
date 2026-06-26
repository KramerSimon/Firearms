"""
Firearms mod texture generator — requires: pip install Pillow
Run from the project root: python generate_textures.py
"""

from PIL import Image, ImageDraw
import os

ROOT = os.path.dirname(__file__)
BLOCK  = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/block")
ITEM   = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/item")
FLUID  = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/fluid")
GUI    = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/gui")

# ── palette ──────────────────────────────────────────────────────────────────
T  = (0,   0,   0,   0)    # transparent

# machine block
MB  = (52,  60,  80,  255)  # body dark
ML  = (80,  92, 118, 255)   # highlight
MS  = (28,  32,  48, 255)   # shadow
ME  = (195,200, 210, 255)   # electrode silver
MG  = ( 56,195, 240, 255)   # electrode glow blue
MP  = ( 68, 78, 100, 255)   # panel mid-tone
MR  = (255,  0,   0, 255)   # indicator red

# tungsten
TR  = ( 34, 34,  44, 255)   # rod dark
TH  = ( 72, 72,  90, 255)   # rod highlight
TS  = ( 18, 18,  24, 255)   # rod shadow
TE  = (110,110, 128, 255)   # rod edge

# AP bullet
BA  = (130,130, 145, 255)   # steel body
BB  = (168,138,  60, 255)   # brass case
BC  = (100, 75,  25, 255)   # brass shadow
BT  = ( 38, 38,  50, 255)   # tungsten tip

# nitrocellulose
NC  = (245,240, 215, 255)   # cream light
NS  = (210,204, 182, 255)   # shadow lines
ND  = (175,168, 148, 255)   # deep shadow

# fluid colours (RGBA, semi-transparent for fluid tiles)
H2  = (185,215, 255, 200)   # hydrogen – cool blue-white
O2  = (155,195, 255, 200)   # oxygen – slightly deeper blue
F2  = (210,255, 155, 200)   # fluorine – yellow-green
CL  = (155,230, 100, 200)   # chlorine – vivid yellow-green
NIT = (240,220, 110, 200)   # nitrate solution – pale amber
PVC = (225,220, 215, 210)   # pvc resin – milky off-white

# bucket content mask – use the vanilla bucket fluid window (pixels 3-12, rows 2-11)
BUCKET_ROWS = list(range(2, 12))
BUCKET_COLS = list(range(3, 13))


def px(w, h, bg=T):
    return Image.new("RGBA", (w, h), bg)


def save(img, path):
    img.save(path, "PNG")
    print(f"  wrote {os.path.relpath(path, ROOT)}")


# ── electrolysis_machine block (16×16) ───────────────────────────────────────
def make_electrolysis_machine():
    GRID = [
        [MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS],
        [MS,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,MS],
        [MS,ML,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,ML,MS],
        [MS,ML,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,ML,MS],
        [MS,ML,MB,ME,ME,MP,MP,MP,MP,MP,MP,ME,ME,MB,ML,MS],
        [MS,ML,MB,ME,MG,MP,MP,MP,MP,MP,MP,MG,ME,MB,ML,MS],
        [MS,ML,MB,ME,ME,MP,MR,MP,MP,MR,MP,ME,ME,MB,ML,MS],
        [MS,ML,MB,MB,MB,MP,MP,MP,MP,MP,MP,MB,MB,MB,ML,MS],
        [MS,ML,MB,ME,ME,MP,MP,MP,MP,MP,MP,ME,ME,MB,ML,MS],
        [MS,ML,MB,ME,MG,MP,MP,MP,MP,MP,MP,MG,ME,MB,ML,MS],
        [MS,ML,MB,ME,ME,MP,MP,MP,MP,MP,MP,ME,ME,MB,ML,MS],
        [MS,ML,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,MB,ML,MS],
        [MS,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,ML,MS],
        [MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS],
        [MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS],
        [MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS,MS],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "electrolysis_machine.png"))


# ── tungsten_rod item (16×16) ─────────────────────────────────────────────────
def make_tungsten_rod():
    img = px(16, 16)
    # Diagonal rod: upper-left to lower-right, 3px wide
    for i in range(13):
        # main pixel
        rx, ry = 2 + i, 1 + i
        if 0 <= rx < 16 and 0 <= ry < 16:
            img.putpixel((rx, ry), TH)
        # secondary (right)
        if 0 <= rx+1 < 16 and 0 <= ry < 16:
            img.putpixel((rx+1, ry), TR)
        # shadow below
        if 0 <= rx < 16 and 0 <= ry+1 < 16:
            img.putpixel((rx, ry+1), TS)
    save(img, os.path.join(ITEM, "tungsten_rod.png"))


# ── armor_piercing_bullet item (16×16) ───────────────────────────────────────
def make_ap_bullet():
    img = px(16, 16)
    #  x centre = 7..9  (3px wide body, 1px tip)
    # rows: tip at top, case at bottom
    layout = [
        # (y, x_start, x_end, color)
        (2,  8,  8,  BT),  # tip 1px
        (3,  7,  9,  BT),  # tip 3px
        (4,  7,  9,  BA),  # transition
        (5,  6, 10,  BA),  # body
        (6,  6, 10,  BA),
        (7,  6, 10,  BA),
        (8,  6, 10,  BB),  # case start (brass)
        (9,  6, 10,  BB),
        (10, 6, 10,  BB),
        (11, 6, 10,  BC),  # case rim
        (12, 6, 10,  BC),
    ]
    for (y, xs, xe, col) in layout:
        for x in range(xs, xe + 1):
            img.putpixel((x, y), col)
    save(img, os.path.join(ITEM, "armor_piercing_bullet.png"))


# ── nitrocellulose item (16×16) ──────────────────────────────────────────────
def make_nitrocellulose():
    # Cotton-ball pattern
    GRID = [
        [T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T],
        [T, T, T,NC,NC,NS, T, T, T,NC,NC,NS, T, T, T, T],
        [T, T,NC,NC,NC,NC,NS,NS,NC,NC,NC,NC,NS, T, T, T],
        [T,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NS, T, T],
        [T,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NS, T, T],
        [T,NC,NC,ND,NC,NC,ND,NC,NC,ND,NC,NC,NC,NS, T, T],
        [T, T,NS,NS,NC,NC,NC,NC,NC,NC,NC,NS,NS, T, T, T],
        [T, T, T, T,NC,NC,NC,NC,NC,NC,NC, T, T, T, T, T],
        [T, T,NC,NC,NC,NC,ND,NC,NC,NC,NC,NC, T, T, T, T],
        [T,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC, T, T, T],
        [T,NC,NC,ND,NC,NC,NC,ND,NC,NC,ND,NC,NC, T, T, T],
        [T,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC,NC, T, T, T],
        [T, T,NS,NC,NC,NC,NC,NC,NC,NC,NC,NS, T, T, T, T],
        [T, T, T,NS,NS,NS,NS,NS,NS,NS,NS, T, T, T, T, T],
        [T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T],
        [T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "nitrocellulose.png"))


# ── bucket content textures (16×16, transparent outside fluid window) ─────────
def make_bucket_content(color, name):
    img = px(16, 16)
    # Fill the vanilla bucket fluid window area with the given colour
    for y in BUCKET_ROWS:
        width = 10 if y < 10 else 8  # taper at the very bottom
        for x in range(3, 3 + width):
            if x < 13:
                img.putpixel((x, y), color)
    save(img, os.path.join(ITEM, f"{name}_bucket_content.png"))


# ── fluid still/flowing (16×16, solid tint) ──────────────────────────────────
def make_fluid_tiles(color, name):
    still = px(16, 16, color)
    save(still, os.path.join(FLUID, f"{name}_still.png"))
    # Flowing: slightly more transparent / darker
    r, g, b, a = color
    flow_col = (max(r - 20, 0), max(g - 20, 0), max(b - 20, 0), max(a - 30, 0))
    flowing = px(16, 16, flow_col)
    save(flowing, os.path.join(FLUID, f"{name}_flowing.png"))


# ── Electrolysis Machine GUI (256×256, content at 0,0 → 176×166) ─────────────
def make_electrolysis_gui():
    GUI_W, GUI_H = 256, 256
    CONTENT_W, CONTENT_H = 176, 166

    BG   = (198, 198, 198, 255)   # standard MC panel grey
    DARK = ( 85,  85,  85, 255)   # slot shadow / borders
    LITE = (255, 255, 255, 255)   # slot highlight
    SLOT = (139, 139, 139, 255)   # slot fill
    BLK  = (  0,   0,   0, 255)   # bar/tank empty fill
    ENRG = (100,   0,   0, 255)   # energy bar label tint
    H2O  = ( 20,  80, 200, 255)   # water label tint
    OUT1 = ( 80, 170, 230, 255)   # output 1 tint
    OUT2 = ( 90, 195, 240, 255)   # output 2 tint
    ARRW = (160, 160, 160, 255)   # arrow fill

    img = px(GUI_W, GUI_H)
    d = ImageDraw.Draw(img)

    # ── main panel ───────────────────────────────────────────────────────────
    d.rectangle([0, 0, CONTENT_W - 1, CONTENT_H - 1], fill=BG)

    # ── outer bevel ──────────────────────────────────────────────────────────
    # top-left highlight, bottom-right shadow
    d.line([(0,0),(CONTENT_W-1,0)], fill=LITE)
    d.line([(0,0),(0,CONTENT_H-1)], fill=LITE)
    d.line([(CONTENT_W-1,0),(CONTENT_W-1,CONTENT_H-1)], fill=DARK)
    d.line([(0,CONTENT_H-1),(CONTENT_W-1,CONTENT_H-1)], fill=DARK)

    def slot(x, y):
        """Draw a standard MC item slot at (x,y), 18×18."""
        # outer shadow (top-left dark, bottom-right light)
        d.rectangle([x, y, x+17, y+17], fill=BLK)
        d.rectangle([x+1, y+1, x+16, y+16], fill=SLOT)
        # inner bevel
        d.line([(x,y),(x+17,y)], fill=DARK)
        d.line([(x,y),(x,y+17)], fill=DARK)
        d.line([(x+17,y),(x+17,y+17)], fill=LITE)
        d.line([(x,y+17),(x+17,y+17)], fill=LITE)

    def tank(x, top_y, bot_y, w, tint):
        """Draw a fluid tank indicator bar (empty/outlined)."""
        d.rectangle([x, top_y, x+w-1, bot_y], fill=BLK)
        d.rectangle([x+1, top_y+1, x+w-2, bot_y-1], fill=SLOT)
        # tick marks every 13px of height
        tank_h = bot_y - top_y
        step = tank_h // 4
        for i in range(1, 4):
            ty = top_y + i * step
            d.line([(x+2, ty), (x+w-3, ty)], fill=DARK)

    # ── energy bar (x=8, bottom=y66, height=52) ───────────────────────────
    tank(8, 14, 66, 12, ENRG)
    # label: tiny "E" indicator at top
    d.rectangle([10, 8, 16, 12], fill=ENRG)

    # ── water input tank (x=25, same height) ────────────────────────────────
    tank(25, 14, 66, 12, H2O)
    d.rectangle([27, 8, 33, 12], fill=H2O)

    # ── item input slot (80,35) ──────────────────────────────────────────────
    slot(79, 34)

    # ── separator line above inventory ──────────────────────────────────────
    d.line([(7, 79), (168, 79)], fill=DARK)
    d.line([(7, 80), (168, 80)], fill=LITE)

    # ── output tank 1 (x=130) ───────────────────────────────────────────────
    tank(130, 14, 66, 12, OUT1)
    d.rectangle([132, 8, 138, 12], fill=OUT1)

    # ── output tank 2 (x=148) ───────────────────────────────────────────────
    tank(148, 14, 66, 12, OUT2)
    d.rectangle([150, 8, 156, 12], fill=OUT2)

    # ── progress arrow  (x=102→124, y=35→50) ───────────────────────────────
    arrow_x, arrow_y = 100, 33
    # Arrow body
    d.rectangle([arrow_x, arrow_y+6, arrow_x+16, arrow_y+12], fill=DARK)
    # Arrow head (triangle pointing right)
    for i in range(8):
        d.line([(arrow_x+16+i, arrow_y+i), (arrow_x+16+i, arrow_y+18-i)], fill=DARK)
    # Inner lighter arrow (fills when processing)
    d.rectangle([arrow_x+1, arrow_y+7, arrow_x+14, arrow_y+11], fill=ARRW)

    # ── machine area top label region ────────────────────────────────────────
    d.rectangle([0, 0, CONTENT_W-1, 7], fill=(198,198,198,255))

    # ── player inventory (3 rows + hotbar) ──────────────────────────────────
    # Rows: y = 84, 102, 120
    for row in range(3):
        for col in range(9):
            slot(8 + col*18, 84 + row*18)
    # Hotbar (with slight gap): y = 142
    for col in range(9):
        slot(8 + col*18, 142)

    save(img, os.path.join(GUI, "electrolysis_machine.png"))


# ── main ─────────────────────────────────────────────────────────────────────
if __name__ == "__main__":
    print("Generating Firearms mod textures...")

    make_electrolysis_machine()
    make_tungsten_rod()
    make_ap_bullet()
    make_nitrocellulose()

    for name, col in [
        ("hydrogen_gas",     H2),
        ("oxygen_gas",       O2),
        ("fluorine_gas",     F2),
        ("chlorine_gas",     CL),
        ("nitrate_solution", NIT),
        ("pvc_resin",        PVC),
    ]:
        make_bucket_content(col, name)
        make_fluid_tiles(col, name)

    make_electrolysis_gui()

    print("Done.")
