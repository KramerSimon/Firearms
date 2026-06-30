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


# ── chemical_mixer_base block (16×16) ────────────────────────────────────────
def make_chemical_mixer_base():
    # Acid-resistant tiled floor: 4x4 grid of tiles with grout lines
    TILE  = (58,  68, 58, 255)  # dark green-gray tile body
    GROUT = (28,  33, 28, 255)  # grout lines
    HI    = (80,  90, 80, 255)  # tile highlight corner
    img = px(16, 16)
    grout_cols = {4, 8, 12}
    grout_rows = {4, 8, 12}
    for y in range(16):
        for x in range(16):
            if x in grout_cols or y in grout_rows:
                img.putpixel((x, y), GROUT)
            elif (x % 4 == 1 and y % 4 == 1):
                img.putpixel((x, y), HI)
            else:
                img.putpixel((x, y), TILE)
    save(img, os.path.join(BLOCK, "chemical_mixer_base.png"))


# ── chemical_mixer_wall block (16×16) ────────────────────────────────────────
def make_chemical_mixer_wall():
    # Reinforced glass/steel composite: steel border with glass interior
    FRAME = (60,  70,  80, 255)  # steel frame
    EDGE  = (40,  48,  58, 255)  # frame inner edge
    GLASS = (180, 210, 230, 200) # glass panel (semi-transparent)
    REFL  = (210, 235, 255, 220) # glass highlight
    img = px(16, 16)
    for y in range(16):
        for x in range(16):
            border = (x < 2 or x > 13 or y < 2 or y > 13)
            inner_border = (x == 2 or x == 13 or y == 2 or y == 13)
            if border:
                img.putpixel((x, y), FRAME)
            elif inner_border:
                img.putpixel((x, y), EDGE)
            elif x == 4 and y == 4:
                img.putpixel((x, y), REFL)
            else:
                img.putpixel((x, y), GLASS)
    save(img, os.path.join(BLOCK, "chemical_mixer_wall.png"))


# ── chemical_mixer_controller block (16×16) ───────────────────────────────────
def make_chemical_mixer_controller():
    # Control panel: dark steel body with orange/green indicators
    BODY  = (52,  60,  80, 255)  # MB — dark steel body
    HI    = (80,  92, 118, 255)  # ML — highlight
    SH    = (28,  32,  48, 255)  # MS — shadow
    PNLB  = (38,  44,  62, 255)  # panel recess background
    PNLF  = (68,  78, 100, 255)  # MP — panel frame
    OR    = (255, 140,   0, 255) # orange active indicator
    GN    = (  0, 200,  80, 255) # green ready light
    SI    = (195, 200, 210, 255) # ME — silver accent
    GRID = [
        [SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH],
        [SH, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, SH],
        [SH, HI, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, HI, SH],
        [SH, HI, BODY, SI, SI, SI, SI, SI, SI, SI, SI, SI, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, PNLB, PNLB, PNLB, PNLB, PNLB, PNLB, PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, OR,   PNLB, PNLB, PNLB, PNLB, OR,   PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, OR,   PNLB, PNLF, PNLF, PNLB, OR,   PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, PNLB, PNLF, GN,   GN,   PNLF, PNLB, PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, PNLB, PNLF, GN,   GN,   PNLF, PNLB, PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, OR,   PNLB, PNLF, PNLF, PNLB, OR,   PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, OR,   PNLB, PNLB, PNLB, PNLB, OR,   PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, PNLB, PNLB, PNLB, PNLB, PNLB, PNLB, PNLB, PNLB, SI, BODY, HI, SH],
        [SH, HI, BODY, SI, SI, SI, SI, SI, SI, SI, SI, SI, SI, BODY, HI, SH],
        [SH, HI, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, BODY, HI, SH],
        [SH, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, HI, SH],
        [SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH, SH],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "chemical_mixer_controller.png"))


# ── PVC pellets (16×16) ──────────────────────────────────────────────────────
def make_pvc_pellets():
    # Colours
    PL  = (235, 232, 228, 255)  # pellet light
    PM  = (200, 196, 190, 255)  # pellet mid
    PS  = (160, 155, 148, 255)  # pellet shadow
    PD  = (120, 116, 110, 255)  # pellet dark edge
    img = px(16, 16)
    # Four oval pellets arranged in a 2×2 grid pattern
    pellets = [(2, 2, 6, 5), (9, 2, 13, 5), (2, 9, 6, 12), (9, 9, 13, 12)]
    draw = ImageDraw.Draw(img)
    for (x0, y0, x1, y1) in pellets:
        draw.ellipse([x0, y0, x1, y1], fill=PM, outline=PD)
        # highlight
        img.putpixel((x0 + 1, y0 + 1), PL)
        # shadow
        img.putpixel((x1 - 1, y1 - 1), PS)
    save(img, os.path.join(ITEM, "pvc_pellets.png"))


# ── plastic sheet (16×16) ─────────────────────────────────────────────────────
def make_plastic_sheet():
    SL  = (230, 228, 225, 255)  # surface light
    SM  = (195, 192, 188, 255)  # surface mid
    SS  = (155, 152, 148, 255)  # surface shadow
    SE  = (110, 108, 104, 255)  # edge dark
    GRID = [
        [SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE],
        [SE,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SE],
        [SE,SL,SM,SM,SM,SM,SM,SM,SM,SM,SM,SM,SM,SM,SL,SE],
        [SE,SL,SM,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SM,SM,SM,SM,SM,SM,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SL,SL,SL,SL,SL,SL,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SL,SM,SM,SM,SM,SL,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SL,SM,SS,SS,SM,SL,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SL,SM,SS,SS,SM,SL,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SL,SM,SM,SM,SM,SL,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SL,SL,SL,SL,SL,SL,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SM,SM,SM,SM,SM,SM,SM,SM,SL,SM,SL,SE],
        [SE,SL,SM,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SM,SL,SE],
        [SE,SL,SM,SM,SM,SM,SM,SM,SM,SM,SM,SM,SM,SM,SL,SE],
        [SE,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SL,SE],
        [SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE,SE],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "plastic_sheet.png"))


# ── pipe fitting (16×16) ─────────────────────────────────────────────────────
def make_pipe_fitting():
    FL  = (140, 148, 158, 255)  # fitting light
    FM  = (100, 108, 118, 255)  # fitting mid
    FS  = ( 62,  68,  78, 255)  # fitting shadow
    FD  = ( 36,  40,  50, 255)  # fitting dark edge
    FH  = (180, 188, 200, 255)  # highlight rim
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  FD, FD, FD, FD, FD, FD, FD, FD, FD, FD, T,  T,  T ],
        [T,  T,  FD, FH, FH, FH, FH, FH, FH, FH, FH, FH, FH, FD, T,  T ],
        [T,  FD, FH, FL, FL, FL, FL, FL, FL, FL, FL, FL, FL, FH, FD, T ],
        [T,  FD, FL, FM, FM, FS, FS, FS, FS, FS, FS, FM, FM, FL, FD, T ],
        [T,  FD, FL, FM, FD, T,  T,  T,  T,  T,  T,  FD, FM, FL, FD, T ],
        [T,  FD, FL, FS, T,  T,  T,  T,  T,  T,  T,  T,  FS, FL, FD, T ],
        [T,  FD, FL, FS, T,  T,  T,  T,  T,  T,  T,  T,  FS, FL, FD, T ],
        [T,  FD, FL, FS, T,  T,  T,  T,  T,  T,  T,  T,  FS, FL, FD, T ],
        [T,  FD, FL, FS, T,  T,  T,  T,  T,  T,  T,  T,  FS, FL, FD, T ],
        [T,  FD, FL, FM, FD, T,  T,  T,  T,  T,  T,  FD, FM, FL, FD, T ],
        [T,  FD, FL, FM, FM, FS, FS, FS, FS, FS, FS, FM, FM, FL, FD, T ],
        [T,  FD, FH, FL, FL, FL, FL, FL, FL, FL, FL, FL, FL, FH, FD, T ],
        [T,  T,  FD, FH, FH, FH, FH, FH, FH, FH, FH, FH, FH, FD, T,  T ],
        [T,  T,  T,  FD, FD, FD, FD, FD, FD, FD, FD, FD, FD, T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "pipe_fitting.png"))


# ── Poppy plant growth stages (16×16 block textures) ─────────────────────────
def make_poppy_plant_stages():
    G0 = (34, 120, 40, 255)   # dark green stem
    G1 = (60, 160, 60, 255)   # mid green leaf
    G2 = (90, 190, 80, 255)   # light green
    BD = (180, 30, 30, 255)   # bud dark red
    BR = (220, 50, 50, 255)   # bud red
    PL = (230, 60, 60, 255)   # petal light red
    PD = (200, 30, 30, 255)   # petal dark
    YC = (250, 230, 50, 255)  # yellow centre

    # Stage 0 — tiny seedling
    s0 = [
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G2,G2,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,G1,G0,G0,G1,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
    ]

    # Stage 1 — small plant with leaves
    s1 = [
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G2,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,G1,G0,G2,T,T,T,T,T,T,T],
        [T,T,T,T,T,G2,G1,G0,G0,G1,T,T,T,T,T,T],
        [T,T,T,T,T,T,G2,G0,G0,G2,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,G1,G2,G0,G0,G2,G1,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
    ]

    # Stage 2 — taller with visible bud
    s2 = [
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,BD,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,BD,BR,BD,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,G1,G0,G2,T,T,T,T,T,T,T],
        [T,T,T,T,T,G2,G1,G0,G0,G1,T,T,T,T,T,T],
        [T,T,T,T,T,T,G2,G0,G0,G2,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,G1,G2,G0,G0,G2,G1,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
    ]

    # Stage 3 — full bloom (red poppy flower)
    s3 = [
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,PL,PL,PL,T,T,T,T,T,T,T],
        [T,T,T,T,T,PL,PD,PL,PD,PL,T,T,T,T,T,T],
        [T,T,T,T,T,PL,PL,YC,YC,PL,PL,T,T,T,T,T],
        [T,T,T,T,T,PL,PD,YC,YC,PD,PL,T,T,T,T,T],
        [T,T,T,T,T,PL,PL,PD,PD,PL,PL,T,T,T,T,T],
        [T,T,T,T,T,T,PL,G0,G0,PL,T,T,T,T,T,T],
        [T,T,T,T,T,T,G2,G0,G0,G2,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,G1,G2,G0,G0,G2,G1,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,G0,G0,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
    ]

    for i, grid in enumerate([s0, s1, s2, s3]):
        img = px(16, 16)
        for y, row in enumerate(grid):
            for x, c in enumerate(row):
                img.putpixel((x, y), c)
        save(img, os.path.join(BLOCK, f"poppy_plant_stage{i}.png"))


# ── Poppy seeds (16×16 item) ──────────────────────────────────────────────────
def make_poppy_seeds():
    SL = (200, 185, 120, 255)
    SM = (155, 140, 80, 255)
    SD = (110, 95, 50, 255)
    GL = (60, 160, 60, 255)
    GRID = [
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,GL,GL,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,GL,SM,SM,GL,T,T,T,T],
        [T,T,T,T,T,T,T,GL,SL,SM,SD,SM,T,T,T,T],
        [T,T,T,T,GL,GL,GL,SL,SM,SL,SM,SD,GL,T,T,T],
        [T,T,T,GL,SM,SL,SL,SM,SD,SM,SL,SM,SD,GL,T,T],
        [T,T,T,T,GL,SM,SL,SM,SM,SD,SM,SD,GL,T,T,T],
        [T,T,T,T,T,GL,GL,SM,SM,SM,GL,GL,T,T,T,T],
        [T,T,T,T,T,T,T,GL,GL,GL,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
        [T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "poppy_seeds.png"))


# ── Raw opium (16×16) — dark brown sticky lump ────────────────────────────────
def make_raw_opium():
    OL = (100, 70, 35, 255)
    OM = (70, 45, 18, 255)
    OD = (45, 28, 8, 255)
    OH = (130, 95, 50, 255)
    img = px(16, 16)
    draw = ImageDraw.Draw(img)
    draw.ellipse([3, 4, 13, 12], fill=OM, outline=OD)
    for px2, py2, c in [(5,6,OH),(8,5,OH),(11,7,OL),(6,9,OL),(10,10,OD)]:
        img.putpixel((px2, py2), c)
    save(img, os.path.join(ITEM, "raw_opium.png"))


# ── Refined opium (16×16) — cream-colored powder in small pile ───────────────
def make_refined_opium():
    RL = (240, 235, 210, 255)
    RM = (210, 200, 170, 255)
    RD = (175, 162, 130, 255)
    img = px(16, 16)
    draw = ImageDraw.Draw(img)
    draw.ellipse([2, 8, 14, 14], fill=RM, outline=RD)
    draw.ellipse([4, 6, 12, 11], fill=RL, outline=RM)
    for px2, py2, c in [(7,7,RL),(9,7,RL),(8,9,RD),(6,10,RM),(11,9,RM)]:
        img.putpixel((px2, py2), c)
    save(img, os.path.join(ITEM, "refined_opium.png"))


# ── Morphine (16×16) — small glass vial with white liquid ────────────────────
def make_morphine():
    GL2 = (210, 235, 245, 200)
    GE  = (155, 185, 200, 255)
    LIQ = (245, 245, 250, 230)
    CAP = (180, 180, 190, 255)
    img = px(16, 16)
    draw = ImageDraw.Draw(img)
    # cap
    draw.rectangle([6, 3, 9, 5], fill=CAP)
    # vial outline
    draw.rectangle([5, 5, 10, 13], fill=GL2, outline=GE)
    # liquid fill
    draw.rectangle([6, 8, 9, 12], fill=LIQ)
    # label line
    img.putpixel((7, 7), GE)
    img.putpixel((8, 7), GE)
    save(img, os.path.join(ITEM, "morphine.png"))


# ── Adrenaline (16×16) — amber vial ──────────────────────────────────────────
def make_adrenaline():
    GL2 = (210, 235, 245, 200)
    GE  = (155, 185, 200, 255)
    LIQ = (240, 160, 30, 230)
    CAP = (200, 60, 60, 255)
    img = px(16, 16)
    draw = ImageDraw.Draw(img)
    draw.rectangle([6, 3, 9, 5], fill=CAP)
    draw.rectangle([5, 5, 10, 13], fill=GL2, outline=GE)
    draw.rectangle([6, 7, 9, 12], fill=LIQ)
    save(img, os.path.join(ITEM, "adrenaline.png"))


# ── Coagulant (16×16) — reddish-brown vial ───────────────────────────────────
def make_coagulant():
    GL2 = (210, 235, 245, 200)
    GE  = (155, 185, 200, 255)
    LIQ = (160, 60, 40, 220)
    CAP = (60, 100, 60, 255)
    img = px(16, 16)
    draw = ImageDraw.Draw(img)
    draw.rectangle([6, 3, 9, 5], fill=CAP)
    draw.rectangle([5, 5, 10, 13], fill=GL2, outline=GE)
    draw.rectangle([6, 7, 9, 12], fill=LIQ)
    save(img, os.path.join(ITEM, "coagulant.png"))


# ── Syringe (16×16) — plain glass syringe ────────────────────────────────────
def make_syringe():
    SB  = (200, 210, 220, 255)  # barrel body
    SG  = (230, 240, 245, 200)  # glass clear
    SN  = (160, 165, 170, 255)  # needle
    SP  = (180, 190, 200, 255)  # plunger
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, SG, SB, T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, SG, SG, SG, SB, T,  T ],
        [T,  T,  T,  T,  T,  T,  SN, SB, SG, SG, SG, SG, SG, SB, T,  T ],
        [T,  T,  T,  T,  T,  SP, SB, SG, SG, SG, SG, SG, SB, T,  T,  T ],
        [T,  T,  T,  T,  SP, SP, SG, SG, SG, SG, SG, SB, T,  T,  T,  T ],
        [T,  T,  T,  SP, SP, SG, SG, SG, SG, SG, SB, T,  T,  T,  T,  T ],
        [T,  T,  SP, SP, SG, SG, SG, SG, SG, SB, T,  T,  T,  T,  T,  T ],
        [T,  SP, SP, SB, SB, SB, SB, SB, SB, T,  T,  T,  T,  T,  T,  T ],
        [SP, SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "syringe.png"))


# ── Morphine syringe (syringe with white fill) ───────────────────────────────
def make_morphine_syringe():
    SB  = (200, 210, 220, 255)
    SG  = (245, 248, 252, 220)
    SN  = (160, 165, 170, 255)
    SP  = (180, 190, 200, 255)
    LIQ = (235, 240, 248, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, LIQ,SB, T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, LIQ,LIQ,LIQ,SB, T,  T ],
        [T,  T,  T,  T,  T,  T,  SN, SB, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T ],
        [T,  T,  T,  T,  T,  SP, SB, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T ],
        [T,  T,  T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T ],
        [T,  T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T,  T ],
        [T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T,  T,  T ],
        [T,  SP, SP, SB, SB, SB, SB, SB, SB, T,  T,  T,  T,  T,  T,  T ],
        [SP, SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "morphine_syringe.png"))


# ── Adrenaline syringe (amber fill) ──────────────────────────────────────────
def make_adrenaline_syringe():
    SB  = (200, 210, 220, 255)
    SN  = (160, 165, 170, 255)
    SP  = (200, 60, 60, 255)
    LIQ = (240, 160, 30, 240)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, LIQ,SB, T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, LIQ,LIQ,LIQ,SB, T,  T ],
        [T,  T,  T,  T,  T,  T,  SN, SB, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T ],
        [T,  T,  T,  T,  T,  SP, SB, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T ],
        [T,  T,  T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T ],
        [T,  T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T,  T ],
        [T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T,  T,  T ],
        [T,  SP, SP, SB, SB, SB, SB, SB, SB, T,  T,  T,  T,  T,  T,  T ],
        [SP, SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "adrenaline_syringe.png"))


# ── Coagulant syringe (dark red fill) ────────────────────────────────────────
def make_coagulant_syringe():
    SB  = (200, 210, 220, 255)
    SN  = (160, 165, 170, 255)
    SP  = (60, 100, 60, 255)
    LIQ = (160, 60, 40, 240)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, LIQ,SB, T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  SN, SN, SB, LIQ,LIQ,LIQ,SB, T,  T ],
        [T,  T,  T,  T,  T,  T,  SN, SB, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T ],
        [T,  T,  T,  T,  T,  SP, SB, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T ],
        [T,  T,  T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T ],
        [T,  T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T,  T ],
        [T,  T,  SP, SP, LIQ,LIQ,LIQ,LIQ,LIQ,SB, T,  T,  T,  T,  T,  T ],
        [T,  SP, SP, SB, SB, SB, SB, SB, SB, T,  T,  T,  T,  T,  T,  T ],
        [SP, SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [SP, SB, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "coagulant_syringe.png"))


# ── Titanium ore block (16×16) ───────────────────────────────────────────────
def make_titanium_ore():
    ST = (80, 80, 82, 255)    # stone base
    SH = (100, 100, 105, 255) # stone highlight
    SD = (55, 55, 58, 255)    # stone shadow
    TL = (140, 195, 220, 255) # titanium vein light
    TM = (90, 150, 185, 255)  # titanium vein mid
    TD = (55, 105, 145, 255)  # titanium vein dark
    GRID = [
        [ST,SH,ST,ST,SD,ST,SH,ST,ST,SD,ST,SH,ST,ST,SD,ST],
        [SH,TL,TM,ST,ST,SD,ST,ST,TL,TM,ST,ST,SD,ST,ST,SH],
        [ST,TM,TD,TL,ST,ST,ST,TM,TD,TL,ST,ST,ST,TM,TD,ST],
        [ST,TL,TM,TD,TM,ST,TL,TM,TD,TM,ST,TL,TM,TD,TM,ST],
        [SD,ST,TM,TD,TL,TM,TD,ST,ST,TL,TM,TD,ST,TL,TM,SD],
        [ST,ST,ST,TM,TD,TL,ST,ST,ST,ST,TL,ST,ST,ST,TL,ST],
        [SH,ST,ST,ST,TM,ST,ST,SH,ST,ST,ST,ST,SH,ST,ST,SH],
        [ST,SD,ST,ST,ST,SD,ST,ST,SD,ST,ST,SD,ST,ST,SD,ST],
        [ST,ST,SH,TL,TM,ST,SH,ST,ST,TL,TM,ST,SH,TL,TM,ST],
        [SD,ST,TM,TD,TL,ST,ST,SD,TM,TD,TL,ST,ST,TM,TD,SD],
        [ST,ST,TL,TM,ST,ST,ST,TL,TM,ST,ST,ST,TL,TM,ST,ST],
        [SH,ST,ST,TL,ST,SH,TL,TM,TD,ST,SH,TL,TM,TD,ST,SH],
        [ST,SD,ST,ST,ST,TM,TD,ST,ST,SD,TM,TD,ST,ST,SD,ST],
        [ST,ST,SH,ST,TL,TM,ST,ST,SH,TL,TM,ST,ST,SH,ST,ST],
        [SD,ST,ST,ST,TM,TD,TL,SD,ST,TM,TD,TL,SD,ST,ST,SD],
        [ST,SH,ST,ST,ST,TL,ST,ST,SH,ST,TL,ST,ST,SH,ST,ST],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "titanium_ore.png"))


# ── Iridium ore block (16×16) ─────────────────────────────────────────────────
def make_iridium_ore():
    ST = (80, 80, 82, 255)
    SH = (100, 100, 105, 255)
    SD = (55, 55, 58, 255)
    IL = (220, 215, 240, 255)  # iridium light (silvery purple)
    IM = (170, 155, 200, 255)  # iridium mid
    ID = (110, 95, 150, 255)   # iridium dark
    GRID = [
        [ST,SH,ST,ST,SD,ST,SH,ST,ST,SD,ST,SH,ST,ST,SD,ST],
        [SH,IL,IM,ST,ST,SD,ST,ST,IL,IM,ST,ST,SD,ST,ST,SH],
        [ST,IM,ID,IL,ST,ST,ST,IM,ID,IL,ST,ST,ST,IM,ID,ST],
        [ST,IL,IM,ID,IM,ST,IL,IM,ID,IM,ST,IL,IM,ID,IM,ST],
        [SD,ST,IM,ID,IL,IM,ID,ST,ST,IL,IM,ID,ST,IL,IM,SD],
        [ST,ST,ST,IM,ID,IL,ST,ST,ST,ST,IL,ST,ST,ST,IL,ST],
        [SH,ST,ST,ST,IM,ST,ST,SH,ST,ST,ST,ST,SH,ST,ST,SH],
        [ST,SD,ST,ST,ST,SD,ST,ST,SD,ST,ST,SD,ST,ST,SD,ST],
        [ST,ST,SH,IL,IM,ST,SH,ST,ST,IL,IM,ST,SH,IL,IM,ST],
        [SD,ST,IM,ID,IL,ST,ST,SD,IM,ID,IL,ST,ST,IM,ID,SD],
        [ST,ST,IL,IM,ST,ST,ST,IL,IM,ST,ST,ST,IL,IM,ST,ST],
        [SH,ST,ST,IL,ST,SH,IL,IM,ID,ST,SH,IL,IM,ID,ST,SH],
        [ST,SD,ST,ST,ST,IM,ID,ST,ST,SD,IM,ID,ST,ST,SD,ST],
        [ST,ST,SH,ST,IL,IM,ST,ST,SH,IL,IM,ST,ST,SH,ST,ST],
        [SD,ST,ST,ST,IM,ID,IL,SD,ST,IM,ID,IL,SD,ST,ST,SD],
        [ST,SH,ST,ST,ST,IL,ST,ST,SH,ST,IL,ST,ST,SH,ST,ST],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "iridium_ore.png"))


# ── Osmium ore block (16×16) ──────────────────────────────────────────────────
def make_osmium_ore():
    ST = (80, 80, 82, 255)
    SH = (100, 100, 105, 255)
    SD = (55, 55, 58, 255)
    OL = (160, 215, 195, 255)  # osmium light (blue-green)
    OM = (100, 165, 145, 255)  # osmium mid
    OD = (55, 110, 95, 255)    # osmium dark
    GRID = [
        [ST,SH,ST,ST,SD,ST,SH,ST,ST,SD,ST,SH,ST,ST,SD,ST],
        [SH,OL,OM,ST,ST,SD,ST,ST,OL,OM,ST,ST,SD,ST,ST,SH],
        [ST,OM,OD,OL,ST,ST,ST,OM,OD,OL,ST,ST,ST,OM,OD,ST],
        [ST,OL,OM,OD,OM,ST,OL,OM,OD,OM,ST,OL,OM,OD,OM,ST],
        [SD,ST,OM,OD,OL,OM,OD,ST,ST,OL,OM,OD,ST,OL,OM,SD],
        [ST,ST,ST,OM,OD,OL,ST,ST,ST,ST,OL,ST,ST,ST,OL,ST],
        [SH,ST,ST,ST,OM,ST,ST,SH,ST,ST,ST,ST,SH,ST,ST,SH],
        [ST,SD,ST,ST,ST,SD,ST,ST,SD,ST,ST,SD,ST,ST,SD,ST],
        [ST,ST,SH,OL,OM,ST,SH,ST,ST,OL,OM,ST,SH,OL,OM,ST],
        [SD,ST,OM,OD,OL,ST,ST,SD,OM,OD,OL,ST,ST,OM,OD,SD],
        [ST,ST,OL,OM,ST,ST,ST,OL,OM,ST,ST,ST,OL,OM,ST,ST],
        [SH,ST,ST,OL,ST,SH,OL,OM,OD,ST,SH,OL,OM,OD,ST,SH],
        [ST,SD,ST,ST,ST,OM,OD,ST,ST,SD,OM,OD,ST,ST,SD,ST],
        [ST,ST,SH,ST,OL,OM,ST,ST,SH,OL,OM,ST,ST,SH,ST,ST],
        [SD,ST,ST,ST,OM,OD,OL,SD,ST,OM,OD,OL,SD,ST,ST,SD],
        [ST,SH,ST,ST,ST,OL,ST,ST,SH,ST,OL,ST,ST,SH,ST,ST],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "osmium_ore.png"))


# ── Iridium coil block (16×16) ────────────────────────────────────────────────
def make_iridium_coil():
    CB  = (40, 30, 60, 255)   # dark purple base
    CM  = (80, 60, 120, 255)  # mid purple
    CL  = (140, 110, 200, 255)# light purple / wire
    CG  = (200, 180, 255, 255)# bright glow
    CS  = (20, 15, 35, 255)   # shadow
    GRID = [
        [CB,CB,CL,CM,CM,CL,CB,CB,CB,CL,CM,CM,CL,CB,CB,CB],
        [CB,CL,CG,CL,CM,CM,CL,CB,CL,CG,CL,CM,CM,CL,CB,CB],
        [CL,CG,CM,CB,CB,CM,CG,CL,CG,CM,CB,CB,CM,CG,CL,CB],
        [CM,CM,CB,CS,CS,CB,CM,CM,CM,CB,CS,CS,CB,CM,CM,CB],
        [CM,CM,CB,CS,CS,CB,CM,CM,CM,CB,CS,CS,CB,CM,CM,CB],
        [CL,CG,CM,CB,CB,CM,CG,CL,CG,CM,CB,CB,CM,CG,CL,CB],
        [CB,CL,CG,CL,CM,CM,CL,CB,CL,CG,CL,CM,CM,CL,CB,CB],
        [CB,CB,CL,CM,CM,CL,CB,CB,CB,CL,CM,CM,CL,CB,CB,CB],
        [CB,CB,CL,CM,CM,CL,CB,CB,CB,CL,CM,CM,CL,CB,CB,CB],
        [CB,CL,CG,CL,CM,CM,CL,CB,CL,CG,CL,CM,CM,CL,CB,CB],
        [CL,CG,CM,CB,CB,CM,CG,CL,CG,CM,CB,CB,CM,CG,CL,CB],
        [CM,CM,CB,CS,CS,CB,CM,CM,CM,CB,CS,CS,CB,CM,CM,CB],
        [CM,CM,CB,CS,CS,CB,CM,CM,CM,CB,CS,CS,CB,CM,CM,CB],
        [CL,CG,CM,CB,CB,CM,CG,CL,CG,CM,CB,CB,CM,CG,CL,CB],
        [CB,CL,CG,CL,CM,CM,CL,CB,CL,CG,CL,CM,CM,CL,CB,CB],
        [CB,CB,CL,CM,CM,CL,CB,CB,CB,CL,CM,CM,CL,CB,CB,CB],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(BLOCK, "iridium_coil.png"))


# ── Raw ore items (16×16) ─────────────────────────────────────────────────────
def make_titanium_ore_raw():
    BL = (80, 80, 82, 255)   # stone fleck
    TL = (140, 195, 220, 255)
    TM = (90, 150, 185, 255)
    TD = (55, 105, 145, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  TL, TM, TD, TL, T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  TL, TM, TD, BL, TM, TD, TL, T,  T,  T,  T,  T,  T],
        [T,  T,  TL, TM, BL, TD, TM, BL, TD, TM, TL, T,  T,  T,  T,  T],
        [T,  T,  TM, TD, TM, TL, BL, TM, TL, TD, TM, T,  T,  T,  T,  T],
        [T,  T,  TD, BL, TD, TM, TL, TD, BL, TL, TD, T,  T,  T,  T,  T],
        [T,  T,  TL, TM, BL, TD, TM, BL, TM, TD, TM, T,  T,  T,  T,  T],
        [T,  T,  T,  TL, TM, TD, BL, TM, TD, TL, T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  TL, TM, TD, TL, T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  TM, T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "titanium_ore_raw.png"))


def make_iridium_ore_raw():
    BL = (80, 80, 82, 255)
    IL = (220, 215, 240, 255)
    IM = (170, 155, 200, 255)
    ID = (110, 95, 150, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  IL, IM, ID, IL, T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  IL, IM, ID, BL, IM, ID, IL, T,  T,  T,  T,  T,  T],
        [T,  T,  IL, IM, BL, ID, IM, BL, ID, IM, IL, T,  T,  T,  T,  T],
        [T,  T,  IM, ID, IM, IL, BL, IM, IL, ID, IM, T,  T,  T,  T,  T],
        [T,  T,  ID, BL, ID, IM, IL, ID, BL, IL, ID, T,  T,  T,  T,  T],
        [T,  T,  IL, IM, BL, ID, IM, BL, IM, ID, IM, T,  T,  T,  T,  T],
        [T,  T,  T,  IL, IM, ID, BL, IM, ID, IL, T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  IL, IM, ID, IL, T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  IM, T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "iridium_ore_raw.png"))


def make_osmium_ore_raw():
    BL = (80, 80, 82, 255)
    OL = (160, 215, 195, 255)
    OM = (100, 165, 145, 255)
    OD = (55, 110, 95, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  OL, OM, OD, OL, T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  OL, OM, OD, BL, OM, OD, OL, T,  T,  T,  T,  T,  T],
        [T,  T,  OL, OM, BL, OD, OM, BL, OD, OM, OL, T,  T,  T,  T,  T],
        [T,  T,  OM, OD, OM, OL, BL, OM, OL, OD, OM, T,  T,  T,  T,  T],
        [T,  T,  OD, BL, OD, OM, OL, OD, BL, OL, OD, T,  T,  T,  T,  T],
        [T,  T,  OL, OM, BL, OD, OM, BL, OM, OD, OM, T,  T,  T,  T,  T],
        [T,  T,  T,  OL, OM, OD, BL, OM, OD, OL, T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  OL, OM, OD, OL, T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  OM, T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "osmium_ore_raw.png"))


# ── Ingots (16×16) ────────────────────────────────────────────────────────────
def make_titanium_ingot():
    TL = (170, 215, 235, 255)
    TM = (110, 170, 200, 255)
    TD = (65, 120, 160, 255)
    TS = (40, 80, 115, 255)
    SH = (230, 245, 255, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  TM, TM, TM, TM, TM, TM, TM, TM, TM, TM, T,  T,  T,  T],
        [T,  TM, TL, SH, TL, TL, TL, TL, TL, TL, TL, TM, TM, T,  T,  T],
        [T,  TM, TL, TL, TL, TL, TL, TL, TL, TL, TL, TM, TM, T,  T,  T],
        [T,  TM, TM, TM, TM, TM, TM, TM, TM, TM, TM, TM, TM, T,  T,  T],
        [T,  T,  TM, TD, TD, TD, TD, TD, TD, TD, TD, TD, TM, T,  T,  T],
        [T,  T,  TM, TD, TD, TM, TM, TM, TM, TD, TD, TD, TM, T,  T,  T],
        [T,  T,  TM, TD, TM, TL, TL, TL, TM, TD, TD, TD, TM, T,  T,  T],
        [T,  T,  TM, TD, TD, TM, TM, TM, TM, TD, TD, TD, TM, T,  T,  T],
        [T,  T,  TM, TD, TD, TD, TD, TD, TD, TD, TD, TD, TM, T,  T,  T],
        [T,  T,  TS, TS, TS, TS, TS, TS, TS, TS, TS, TS, TS, T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "titanium_ingot.png"))


def make_iridium_ingot():
    IL = (235, 230, 255, 255)
    IM = (185, 165, 225, 255)
    ID = (125, 105, 170, 255)
    IS = (75, 55, 120, 255)
    SH = (255, 250, 255, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  IM, IM, IM, IM, IM, IM, IM, IM, IM, IM, T,  T,  T,  T],
        [T,  IM, IL, SH, IL, IL, IL, IL, IL, IL, IL, IM, IM, T,  T,  T],
        [T,  IM, IL, IL, IL, IL, IL, IL, IL, IL, IL, IM, IM, T,  T,  T],
        [T,  IM, IM, IM, IM, IM, IM, IM, IM, IM, IM, IM, IM, T,  T,  T],
        [T,  T,  IM, ID, ID, ID, ID, ID, ID, ID, ID, ID, IM, T,  T,  T],
        [T,  T,  IM, ID, ID, IM, IM, IM, IM, ID, ID, ID, IM, T,  T,  T],
        [T,  T,  IM, ID, IM, IL, IL, IL, IM, ID, ID, ID, IM, T,  T,  T],
        [T,  T,  IM, ID, ID, IM, IM, IM, IM, ID, ID, ID, IM, T,  T,  T],
        [T,  T,  IM, ID, ID, ID, ID, ID, ID, ID, ID, ID, IM, T,  T,  T],
        [T,  T,  IS, IS, IS, IS, IS, IS, IS, IS, IS, IS, IS, T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "iridium_ingot.png"))


def make_osmium_ingot():
    OL = (180, 230, 210, 255)
    OM = (115, 180, 160, 255)
    OD = (65, 125, 108, 255)
    OS = (35, 80, 65, 255)
    SH = (220, 255, 240, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  OM, OM, OM, OM, OM, OM, OM, OM, OM, OM, T,  T,  T,  T],
        [T,  OM, OL, SH, OL, OL, OL, OL, OL, OL, OL, OM, OM, T,  T,  T],
        [T,  OM, OL, OL, OL, OL, OL, OL, OL, OL, OL, OM, OM, T,  T,  T],
        [T,  OM, OM, OM, OM, OM, OM, OM, OM, OM, OM, OM, OM, T,  T,  T],
        [T,  T,  OM, OD, OD, OD, OD, OD, OD, OD, OD, OD, OM, T,  T,  T],
        [T,  T,  OM, OD, OD, OM, OM, OM, OM, OD, OD, OD, OM, T,  T,  T],
        [T,  T,  OM, OD, OM, OL, OL, OL, OM, OD, OD, OD, OM, T,  T,  T],
        [T,  T,  OM, OD, OD, OM, OM, OM, OM, OD, OD, OD, OM, T,  T,  T],
        [T,  T,  OM, OD, OD, OD, OD, OD, OD, OD, OD, OD, OM, T,  T,  T],
        [T,  T,  OS, OS, OS, OS, OS, OS, OS, OS, OS, OS, OS, T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "osmium_ingot.png"))


# ── Iridium alloy (16×16) — ingot with purple-gold sheen ─────────────────────
def make_iridium_alloy():
    AL = (235, 220, 255, 255)  # alloy light
    AM = (190, 160, 230, 255)  # alloy mid
    AD = (135, 100, 180, 255)  # alloy dark
    AG = (255, 215, 120, 255)  # gold highlight
    AS = (80, 50, 130, 255)    # shadow
    SH = (255, 248, 255, 255)
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  AM, AM, AM, AM, AM, AM, AM, AM, AM, AM, T,  T,  T,  T],
        [T,  AM, AL, SH, AL, AL, AG, AL, AL, AL, AL, AM, AM, T,  T,  T],
        [T,  AM, AL, AL, AL, AG, AG, AG, AL, AL, AL, AM, AM, T,  T,  T],
        [T,  AM, AM, AM, AG, AM, AM, AM, AM, AM, AM, AM, AM, T,  T,  T],
        [T,  T,  AM, AD, AD, AD, AD, AD, AD, AD, AD, AD, AM, T,  T,  T],
        [T,  T,  AM, AD, AD, AG, AM, AM, AM, AD, AD, AD, AM, T,  T,  T],
        [T,  T,  AM, AD, AG, AL, AL, AL, AM, AD, AD, AD, AM, T,  T,  T],
        [T,  T,  AM, AD, AD, AG, AM, AM, AM, AD, AD, AD, AM, T,  T,  T],
        [T,  T,  AM, AD, AD, AD, AD, AD, AD, AD, AD, AD, AM, T,  T,  T],
        [T,  T,  AS, AS, AS, AS, AS, AS, AS, AS, AS, AS, AS, T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "iridium_alloy.png"))


# ── Iridium wire (16×16) — coiled thin wire ───────────────────────────────────
def make_iridium_wire():
    WL = (220, 210, 250, 255)  # wire light
    WM = (165, 148, 210, 255)  # wire mid
    WD = (105, 88, 158, 255)   # wire dark
    GRID = [
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  WL, WM, WD, T,  T,  T,  T,  T,  T,  T,  WL, WM, T,  T],
        [T,  T,  WM, T,  WD, T,  T,  WL, WM, WD, T,  T,  WM, T,  T,  T],
        [T,  T,  WL, WM, WD, T,  T,  WM, T,  WD, T,  T,  WL, WM, T,  T],
        [T,  T,  T,  T,  T,  T,  T,  WL, WM, WD, T,  T,  T,  T,  T,  T],
        [T,  T,  T,  WL, WM, WD, WL, WM, T,  WD, WL, WM, WD, T,  T,  T],
        [T,  T,  WL, WM, T,  WD, WM, T,  T,  T,  WM, T,  WD, T,  T,  T],
        [T,  T,  WL, WM, WD, WL, WM, WD, T,  T,  WL, WM, WD, T,  T,  T],
        [T,  T,  T,  T,  WD, WL, T,  WD, T,  T,  T,  WM, T,  T,  T,  T],
        [T,  T,  T,  WL, WM, WD, WL, WM, WD, T,  WL, WM, WD, T,  T,  T],
        [T,  T,  WL, WM, T,  T,  WM, T,  WD, T,  WM, T,  WD, T,  T,  T],
        [T,  T,  WD, WM, WL, T,  WL, WM, WD, T,  WL, WM, WD, T,  T,  T],
        [T,  T,  T,  T,  WD, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T],
    ]
    img = px(16, 16)
    for y, row in enumerate(GRID):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "iridium_wire.png"))


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
    make_chemical_mixer_base()
    make_chemical_mixer_wall()
    make_chemical_mixer_controller()

    make_pvc_pellets()
    make_plastic_sheet()
    make_pipe_fitting()

    make_poppy_plant_stages()
    make_poppy_seeds()
    make_raw_opium()
    make_refined_opium()
    make_morphine()
    make_adrenaline()
    make_coagulant()
    make_syringe()
    make_morphine_syringe()
    make_adrenaline_syringe()
    make_coagulant_syringe()

    make_titanium_ore()
    make_iridium_ore()
    make_osmium_ore()
    make_iridium_coil()
    make_titanium_ore_raw()
    make_iridium_ore_raw()
    make_osmium_ore_raw()
    make_titanium_ingot()
    make_iridium_ingot()
    make_osmium_ingot()
    make_iridium_alloy()
    make_iridium_wire()

    make_battlesuit_helmet()
    make_battlesuit_chestplate()
    make_battlesuit_leggings()
    make_battlesuit_boots()
    make_battlesuit_armor()

    # ── Incendiary Weapons ────────────────────────────────────────────────────
    make_napalm_bomb()
    make_thermite_grenade()

    print("Done.")
