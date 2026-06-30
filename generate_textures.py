"""
Firearms mod texture generator — requires: pip install Pillow
Run from the project root: python generate_textures.py
"""

from PIL import Image, ImageDraw
import os

ROOT  = os.path.dirname(__file__)
BLOCK = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/block")
ITEM  = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/item")
FLUID = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/fluid")
GUI   = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/gui")
ARMOR = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/models/armor")

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

# titanium ore
TI_S  = (100, 100, 120, 255)   # stone shadow
TI_B  = (122, 122, 138, 255)   # stone base
TI_L  = (148, 148, 162, 255)   # stone light
TI_V  = (128, 155, 205, 255)   # titanium vein
TI_VH = (175, 198, 238, 255)   # titanium highlight
TI_VD = (82,  108, 168, 255)   # titanium shadow

# battlesuit (powered armor)
BS_S  = (12,  18,  42,  255)   # suit shadow
BS_D  = (22,  32,  62,  255)   # suit dark
BS_B  = (36,  52,  98,  255)   # suit body
BS_L  = (58,  80, 148,  255)   # suit light
BS_H  = (90, 120, 205,  255)   # suit highlight
BS_T  = (168, 185, 215, 255)   # titanium trim
BS_E  = (80, 200, 255, 255)    # energy blue glow

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


# ── titanium_ore block (16×16) ───────────────────────────────────────────────
def make_titanium_ore():
    # Stone background with scattered blue-silver titanium veins
    GRID = [
        [TI_S,TI_B,TI_B,TI_S,TI_B,TI_B,TI_L,TI_B,TI_B,TI_S,TI_B,TI_V ,TI_VH,TI_V ,TI_B,TI_S],
        [TI_B,TI_L,TI_B,TI_B,TI_V ,TI_VH,TI_V,TI_B,TI_B,TI_B,TI_B,TI_B ,TI_B ,TI_VD,TI_B,TI_B],
        [TI_B,TI_B,TI_S,TI_B,TI_VD,TI_V ,TI_B,TI_B,TI_L,TI_B,TI_B,TI_B ,TI_B ,TI_B ,TI_L,TI_B],
        [TI_S,TI_B,TI_B,TI_L,TI_B ,TI_B ,TI_B,TI_V,TI_VH,TI_V,TI_B,TI_S ,TI_B ,TI_B ,TI_B,TI_S],
        [TI_B,TI_V,TI_VH,TI_V,TI_B ,TI_B ,TI_S,TI_VD,TI_B,TI_B,TI_L,TI_B,TI_V ,TI_VH,TI_V,TI_B],
        [TI_B,TI_VD,TI_V,TI_B,TI_B ,TI_B ,TI_B,TI_B ,TI_B,TI_B,TI_B,TI_B,TI_VD,TI_V ,TI_B,TI_B],
        [TI_L,TI_B,TI_B,TI_B,TI_S ,TI_B ,TI_V,TI_VH,TI_V,TI_B,TI_S,TI_B,TI_B ,TI_B ,TI_B,TI_L],
        [TI_B,TI_B,TI_B,TI_L,TI_B ,TI_B ,TI_VD,TI_V,TI_B,TI_B,TI_B,TI_L,TI_B ,TI_S ,TI_B,TI_B],
        [TI_S,TI_B,TI_V,TI_VH,TI_V,TI_B ,TI_B,TI_B ,TI_L,TI_B,TI_B,TI_B,TI_V ,TI_VH,TI_B,TI_S],
        [TI_B,TI_B,TI_VD,TI_V,TI_B,TI_S ,TI_B,TI_B ,TI_B,TI_B,TI_V,TI_VH,TI_VD,TI_B ,TI_B,TI_B],
        [TI_B,TI_L,TI_B,TI_B,TI_B ,TI_B ,TI_L,TI_V ,TI_VH,TI_V,TI_B,TI_B,TI_B ,TI_B ,TI_L,TI_B],
        [TI_B,TI_B,TI_B,TI_S,TI_B ,TI_B ,TI_B,TI_VD,TI_V ,TI_B,TI_B,TI_S,TI_B ,TI_V ,TI_VH,TI_B],
        [TI_S,TI_V,TI_VH,TI_V,TI_B,TI_L ,TI_B,TI_B ,TI_B ,TI_L,TI_B,TI_B,TI_VD,TI_V ,TI_B,TI_S],
        [TI_B,TI_VD,TI_V,TI_B,TI_B,TI_B ,TI_S,TI_B ,TI_B ,TI_B,TI_B,TI_B,TI_B ,TI_B ,TI_B,TI_B],
        [TI_B,TI_B,TI_B,TI_B,TI_L ,TI_B ,TI_B,TI_V ,TI_VH,TI_V,TI_L,TI_B,TI_B ,TI_S ,TI_B,TI_B],
        [TI_S,TI_B,TI_B,TI_S,TI_B ,TI_B ,TI_B,TI_VD,TI_V ,TI_B,TI_B,TI_B,TI_B ,TI_B ,TI_B,TI_S],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "titanium_ore.png"))


# ── titanium_ore_raw item (16×16) ────────────────────────────────────────────
def make_titanium_ore_raw():
    img = px(16, 16)
    d = ImageDraw.Draw(img)
    # Irregular chunk shape with blue-silver highlights
    d.polygon([(3,1),(12,1),(14,4),(15,9),(13,14),(8,15),(3,14),(1,10),(1,5)], fill=TI_B)
    d.polygon([(4,2),(11,2),(13,5),(14,9),(12,13),(8,14),(4,13),(2,9),(2,5)], fill=TI_V)
    d.polygon([(5,3),(9,3),(11,6),(11,10),(9,12),(6,12),(4,9),(4,5)],          fill=TI_VH)
    # shadow facets
    d.polygon([(3,1),(1,5),(2,9),(4,13),(8,14),(8,14),(7,12),(3,9),(3,5)],     fill=TI_VD)
    # edge dark
    d.polygon([(3,1),(12,1),(12,2),(3,2)], fill=TI_VH)
    save(img, os.path.join(ITEM, "titanium_ore_raw.png"))


# ── titanium_ingot item (16×16) ──────────────────────────────────────────────
def make_titanium_ingot():
    img = px(16, 16)
    d = ImageDraw.Draw(img)
    # Ingot body
    d.rectangle([2, 5, 13, 12], fill=TI_V)
    # Top face (lighter)
    d.rectangle([2, 5, 13, 7],  fill=TI_VH)
    # Left shadow
    d.rectangle([2, 5, 3, 12],  fill=TI_VD)
    # Bottom shadow
    d.rectangle([2, 11, 13, 12], fill=TI_S)
    # Notch (ingot shape)
    d.rectangle([4, 3, 11, 5],   fill=TI_V)
    d.rectangle([4, 3, 11, 4],   fill=TI_VH)
    d.rectangle([4, 4, 5, 5],    fill=TI_VD)
    save(img, os.path.join(ITEM, "titanium_ingot.png"))


# ── battlesuit item icons (16×16) ────────────────────────────────────────────
def make_battlesuit_helmet():
    img = px(16, 16)
    d = ImageDraw.Draw(img)
    # Dome shape
    d.ellipse([2, 1, 13, 10], fill=BS_B)
    # Visor
    d.rectangle([3, 5, 12, 9], fill=BS_E)
    d.rectangle([3, 5, 12, 6], fill=(120, 220, 255, 255))
    # Neck guard
    d.rectangle([4, 10, 11, 12], fill=BS_D)
    # Trim
    d.rectangle([2, 1, 13, 2], fill=BS_T)
    d.rectangle([2, 9, 13, 10], fill=BS_T)
    # Chin guards
    d.rectangle([3, 11, 5, 13], fill=BS_B)
    d.rectangle([10, 11, 12, 13], fill=BS_B)
    # Shadow side
    d.rectangle([2, 2, 3, 9], fill=BS_S)
    d.rectangle([12, 2, 13, 9], fill=BS_L)
    save(img, os.path.join(ITEM, "battlesuit_helmet.png"))


def make_battlesuit_chestplate():
    img = px(16, 16)
    d = ImageDraw.Draw(img)
    # Main plate
    d.rectangle([2, 1, 13, 13], fill=BS_B)
    # Shoulder pauldrons
    d.rectangle([0, 1, 2, 8],   fill=BS_L)
    d.rectangle([13, 1, 15, 8], fill=BS_D)
    # Central energy cell
    d.rectangle([6, 4, 9, 9],   fill=BS_E)
    d.rectangle([6, 4, 9, 5],   fill=(120, 220, 255, 255))
    # Chest ridge lines
    d.rectangle([3, 2, 5, 12],  fill=BS_D)
    d.rectangle([10, 2, 12, 12],fill=BS_L)
    # Collar
    d.rectangle([4, 1, 11, 2],  fill=BS_T)
    # Bottom trim
    d.rectangle([2, 12, 13, 13],fill=BS_T)
    # Shadow
    d.rectangle([2, 1, 3, 13],  fill=BS_S)
    save(img, os.path.join(ITEM, "battlesuit_chestplate.png"))


def make_battlesuit_leggings():
    img = px(16, 16)
    d = ImageDraw.Draw(img)
    # Waist band
    d.rectangle([2, 1, 13, 4],  fill=BS_B)
    d.rectangle([2, 1, 13, 2],  fill=BS_T)
    # Left leg
    d.rectangle([2, 4, 7, 15],  fill=BS_B)
    d.rectangle([3, 5, 6, 14],  fill=BS_L)
    d.rectangle([2, 4, 3, 15],  fill=BS_S)
    d.rectangle([6, 10, 7, 15], fill=BS_D)
    # Right leg
    d.rectangle([8, 4, 13, 15], fill=BS_B)
    d.rectangle([8, 5, 11, 14], fill=BS_D)
    d.rectangle([12, 4, 13, 15],fill=BS_L)
    d.rectangle([8, 10, 9, 15], fill=BS_S)
    # Knee pads
    d.rectangle([3, 8, 7, 11],  fill=BS_T)
    d.rectangle([8, 8, 12, 11], fill=BS_T)
    save(img, os.path.join(ITEM, "battlesuit_leggings.png"))


def make_battlesuit_boots():
    img = px(16, 16)
    d = ImageDraw.Draw(img)
    # Shin
    d.rectangle([3, 1, 7, 10],  fill=BS_B)
    d.rectangle([4, 2, 6, 9],   fill=BS_L)
    d.rectangle([3, 1, 4, 10],  fill=BS_S)
    # Ankle/boot
    d.rectangle([2, 10, 8, 15], fill=BS_B)
    d.rectangle([2, 10, 8, 11], fill=BS_T)
    d.rectangle([2, 14, 8, 15], fill=BS_D)
    # Toe cap
    d.rectangle([1, 13, 9, 15], fill=BS_D)
    d.rectangle([1, 13, 9, 14], fill=BS_S)
    # Knee guard
    d.rectangle([3, 7, 7, 10],  fill=BS_T)
    # Shadow
    d.rectangle([7, 10, 8, 15], fill=BS_L)
    save(img, os.path.join(ITEM, "battlesuit_boots.png"))


# ── battlesuit armor layer textures (64×32) ──────────────────────────────────
def make_battlesuit_armor():
    # Layer 1 – helmet, chestplate, boots (standard Minecraft UV layout)
    img1 = px(64, 32, bg=T)
    d1 = ImageDraw.Draw(img1)

    # Fill all armor regions with body color
    d1.rectangle([0, 0, 63, 31], fill=BS_B)

    # Top highlight row
    d1.line([(0, 0), (63, 0)], fill=BS_L)
    # Bottom shadow row
    d1.line([(0, 31), (63, 31)], fill=BS_S)
    # Left shadow column
    d1.line([(0, 0), (0, 31)], fill=BS_S)
    # Right highlight
    d1.line([(63, 0), (63, 31)], fill=BS_L)

    # Energy cells (scattered blue glows based on armor UV regions)
    for cx, cy in [(8, 8), (20, 14), (44, 8), (52, 20)]:
        d1.rectangle([cx-1, cy-1, cx+1, cy+1], fill=BS_E)

    # Titanium trim lines
    for x in range(0, 64, 16):
        d1.line([(x, 0), (x, 31)], fill=BS_T)
    for y in range(0, 32, 8):
        d1.line([(0, y), (63, y)], fill=BS_T)

    # Darken seam regions
    for region in [(0,0,7,7),(24,0,39,7),(56,0,63,7),(0,16,15,31),(48,16,63,31)]:
        d1.rectangle(region, fill=BS_D)

    save(img1, os.path.join(ARMOR, "battlesuit_layer_1.png"))

    # Layer 2 – leggings
    img2 = px(64, 32, bg=T)
    d2 = ImageDraw.Draw(img2)
    d2.rectangle([0, 0, 63, 31], fill=BS_D)
    d2.line([(0, 0), (63, 0)], fill=BS_L)
    d2.line([(0, 31), (63, 31)], fill=BS_S)
    for x in range(0, 64, 16):
        d2.line([(x, 0), (x, 31)], fill=BS_T)
    for y in range(0, 32, 8):
        d2.line([(0, y), (63, y)], fill=BS_B)
    # Knee pad highlights
    for cx, cy in [(4, 20), (20, 20)]:
        d2.rectangle([cx, cy, cx+5, cy+3], fill=BS_T)
    save(img2, os.path.join(ARMOR, "battlesuit_layer_2.png"))


# ── Incendiary weapon textures ───────────────────────────────────────────────

def make_napalm_bomb():
    """Orange incendiary bomb with dark metal caps and red warning band."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    p = img.load()

    ORANGE   = (210,  88,  18, 255)  # main body
    ORANGE_H = (238, 126,  48, 255)  # highlight side
    ORANGE_D = (138,  52,   8, 255)  # shadow side
    METAL    = ( 72,  72,  82, 255)  # dark cap
    METAL_H  = ( 96,  96, 108, 255)  # cap highlight
    RED      = (188,  20,  20, 255)  # warning band
    RED_D    = (140,  10,  10, 255)  # warning shadow edge
    FUSE     = ( 52,  36,  16, 255)  # brown cord fuse

    # Fuse tip (row 0, offset left of center for slight angle)
    p[7, 0] = FUSE
    # Top cap (rows 1–3, cols 4–11)
    for x in range(5, 11):
        p[x, 1] = METAL_H
    for x in range(4, 12):
        p[x, 2] = METAL
        p[x, 3] = METAL
    # Body (rows 4–12, cols 4–11)
    for y in range(4, 12):
        for x in range(4, 12):
            if y in (7, 8):
                p[x, y] = RED_D if (x == 4 or x == 11) else RED
            elif x == 4 or x == 11:
                p[x, y] = ORANGE_D
            elif x in (5, 6):
                p[x, y] = ORANGE_H
            else:
                p[x, y] = ORANGE
    # Bottom cap (rows 12–14, cols 4–11)
    for x in range(4, 12):
        p[x, 12] = METAL
        p[x, 13] = METAL
    for x in range(5, 11):
        p[x, 14] = METAL_H

    img.save(os.path.join(ITEM, "napalm_bomb.png"))


def make_thermite_grenade():
    """Silver metallic grenade with a bright orange thermite band."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    p = img.load()

    SILVER   = (178, 184, 190, 255)  # body
    SILVER_H = (212, 218, 224, 255)  # highlight
    SILVER_D = (118, 124, 130, 255)  # shadow
    CAP      = ( 88,  90, 100, 255)  # metal end-cap
    CAP_H    = (112, 114, 124, 255)  # cap highlight
    ORANGE   = (220, 108,   0, 255)  # thermite band
    ORANGE_H = (248, 148,  20, 255)  # band highlight
    ORANGE_D = (160,  72,   0, 255)  # band shadow
    SPARK    = (255, 248, 160, 255)  # detonator tip

    # Detonator pin (rows 0–1, col 8)
    p[8, 0] = SPARK
    p[8, 1] = CAP
    # Top cap (rows 2–3, cols 4–11)
    for x in range(5, 11):
        p[x, 2] = CAP_H
    for x in range(4, 12):
        p[x, 3] = CAP
    # Body (rows 4–12, cols 4–11)
    for y in range(4, 12):
        for x in range(4, 12):
            if y in (7, 8):
                if x == 4 or x == 11:
                    p[x, y] = ORANGE_D
                elif x in (5, 6):
                    p[x, y] = ORANGE_H
                else:
                    p[x, y] = ORANGE
            elif x == 4 or x == 11:
                p[x, y] = SILVER_D
            elif x in (5, 6):
                p[x, y] = SILVER_H
            else:
                p[x, y] = SILVER
    # Bottom cap (rows 12–14, cols 4–11)
    for x in range(4, 12):
        p[x, 12] = CAP
        p[x, 13] = CAP
    for x in range(5, 11):
        p[x, 14] = CAP_H

    img.save(os.path.join(ITEM, "thermite_grenade.png"))


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

    # ── Titanium & Battlesuit ─────────────────────────────────────────────────
    make_titanium_ore()
    make_titanium_ore_raw()
    make_titanium_ingot()
    make_battlesuit_helmet()
    make_battlesuit_chestplate()
    make_battlesuit_leggings()
    make_battlesuit_boots()
    make_battlesuit_armor()

    # ── Incendiary Weapons ────────────────────────────────────────────────────
    make_napalm_bomb()
    make_thermite_grenade()

    print("Done.")
