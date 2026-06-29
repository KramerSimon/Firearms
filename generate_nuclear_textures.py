"""
Nuclear Reactor texture generator for Firearms mod — requires: pip install Pillow
Regenerates all block/item/fluid/GUI textures for nuclear reactor Stage 1.
Run from project root: python generate_nuclear_textures.py
"""

from PIL import Image, ImageDraw
import os

ROOT  = os.path.dirname(os.path.abspath(__file__))
BLOCK = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/block")
ITEM  = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/item")
FLUID = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/fluid")
GUI   = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/gui")

X = (0, 0, 0, 0)  # transparent

def px(w=16, h=16, bg=X):
    return Image.new("RGBA", (w, h), bg)

def save(img, path):
    img.save(path, "PNG")
    print(f"  wrote {os.path.relpath(path, ROOT)}")

def grid_img(grid):
    img = px()
    for y, row in enumerate(grid):
        for x, c in enumerate(row):
            img.putpixel((x, y), c)
    return img

# ── palette ───────────────────────────────────────────────────────────────────
STONE_D = ( 90,  90,  90, 255)
STONE_M = (110, 110, 110, 255)
STONE_L = (135, 135, 135, 255)

ZRFLK  = (  0, 195, 180, 255)
ZRGLO  = ( 80, 230, 215, 255)

ZR_S   = (120, 128, 138, 255)
ZR_D   = (150, 158, 168, 255)
ZR_M   = (180, 190, 200, 255)
ZR_L   = (215, 222, 228, 255)

MCH_D  = ( 45,  50,  60, 255)
MCH_M  = ( 65,  72,  88, 255)
MCH_L  = ( 90, 100, 118, 255)
MCH_A  = ( 55, 195, 240, 255)
MCH_S  = (190, 195, 205, 255)

UF6_D  = (130, 160,  45, 255)
UF6_M  = (175, 215,  80, 255)
UF6_L  = (210, 240, 110, 255)

EUF6_D = ( 50, 160,  20, 255)
EUF6_M = ( 80, 230,  50, 255)
EUF6_L = (130, 255,  90, 255)

DUF6_D = ( 90, 100,  70, 255)
DUF6_M = (120, 135, 100, 255)
DUF6_L = (150, 165, 130, 255)

UO2_D  = (155, 120,  25, 255)
UO2_M  = (185, 150,  40, 255)
UO2_L  = (215, 185,  70, 255)

ROD_S  = (100, 108, 118, 255)
ROD_D  = (145, 152, 162, 255)
ROD_M  = (190, 196, 205, 255)
ROD_L  = (220, 224, 230, 255)
ROD_P  = (170, 138,  35, 255)   # pellet fill
ROD_PD = (125, 100,  18, 255)   # pellet dark

SROD_S = ( 80,  85,  95, 255)
SROD_D = (100, 105, 115, 255)
SROD_M = (125, 130, 140, 255)
SROD_L = (150, 155, 165, 255)
SROD_G = (180,  60,  20, 255)   # glow

DU_S   = ( 50,  52,  62, 255)
DU_D   = ( 70,  75,  85, 255)
DU_M   = ( 95, 100, 112, 255)
DU_L   = (120, 126, 138, 255)

BC_D   = ( 30,  30,  38, 255)
BC_M   = ( 50,  52,  62, 255)
BC_L   = ( 72,  74,  86, 255)

GR_S   = ( 25,  26,  30, 255)
GR_D   = ( 40,  42,  48, 255)
GR_M   = ( 58,  60,  68, 255)
GR_L   = ( 80,  83,  92, 255)

CR_S   = ( 20,  22,  30, 255)
CR_D   = ( 35,  38,  48, 255)
CR_M   = ( 55,  60,  72, 255)
CR_L   = ( 75,  82,  98, 255)
CR_T   = ( 45, 160, 210, 255)   # boron tip blue
CR_TD  = ( 25, 100, 145, 255)

HW_M   = (120, 190, 235, 200)
EUF_FL = ( 75, 220,  45, 200)
DUF_FL = (110, 125,  90, 200)
UF6_FL = (165, 205,  75, 200)
STM_FL = (225, 232, 238, 180)

# ── BLOCK TEXTURES ────────────────────────────────────────────────────────────

def make_zirconite_ore():
    D, M, L = STONE_D, STONE_M, STONE_L
    F, G = ZRFLK, ZRGLO
    save(grid_img([
        [D,M,D,M,L,M,D,M,D,M,L,M,D,M,D,M],
        [M,L,M,D,M,D,M,L,M,D,M,D,M,L,M,D],
        [D,M,D,M,F,G,M,D,L,M,D,F,M,M,D,M],
        [M,D,M,F,G,F,D,M,M,D,F,G,F,D,L,D],
        [L,M,D,M,F,M,L,M,D,M,M,F,M,M,M,M],
        [D,L,M,D,M,L,D,M,M,L,D,M,L,D,M,L],
        [M,D,L,M,D,M,F,G,F,M,L,D,M,L,D,M],
        [L,M,D,L,M,D,F,G,F,D,M,M,D,M,L,D],
        [M,D,M,M,L,M,M,F,D,M,D,L,M,D,M,M],
        [D,M,L,D,M,D,L,M,M,L,M,D,L,M,D,L],
        [M,L,F,G,M,L,D,M,D,M,L,M,D,F,L,M],
        [D,M,F,F,D,M,L,D,M,D,M,D,M,F,M,D],
        [M,D,M,L,M,D,M,M,L,M,D,M,L,D,D,M],
        [L,M,D,M,L,M,D,L,M,D,L,M,M,L,M,L],
        [M,D,L,D,M,L,M,M,D,M,M,D,D,M,D,M],
        [D,M,M,L,D,M,L,D,M,L,D,M,L,D,M,D],
    ]), os.path.join(BLOCK, "zirconite_ore.png"))


def make_gas_centrifuge():
    B, M, L, S, A = MCH_D, MCH_M, MCH_L, MCH_S, MCH_A
    save(grid_img([
        [B,B,B,B,B,B,B,B,B,B,B,B,B,B,B,B],
        [B,L,L,L,L,L,L,L,L,L,L,L,L,L,L,B],
        [B,L,M,M,M,M,M,M,M,M,M,M,M,M,L,B],
        [B,L,M,S,S,S,S,S,S,S,S,S,S,M,L,B],
        [B,L,M,S,B,B,B,B,B,B,B,B,S,M,L,B],
        [B,L,M,S,B,M,A,A,A,A,M,B,S,M,L,B],
        [B,L,M,S,B,M,A,L,L,A,M,B,S,M,L,B],
        [B,L,M,S,B,M,A,L,L,A,M,B,S,M,L,B],
        [B,L,M,S,B,M,A,L,L,A,M,B,S,M,L,B],
        [B,L,M,S,B,M,A,A,A,A,M,B,S,M,L,B],
        [B,L,M,S,B,B,B,B,B,B,B,B,S,M,L,B],
        [B,L,M,S,S,S,S,S,S,S,S,S,S,M,L,B],
        [B,L,M,M,M,M,M,M,M,M,M,M,M,M,L,B],
        [B,L,L,L,L,L,L,L,L,L,L,L,L,L,L,B],
        [B,B,B,B,B,B,B,B,B,B,B,B,B,B,B,B],
        [B,B,B,B,B,B,B,B,B,B,B,B,B,B,B,B],
    ]), os.path.join(BLOCK, "gas_centrifuge.png"))


# ── ITEM HELPERS ──────────────────────────────────────────────────────────────

def ingot(D, M, L, S):
    return [
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,S,S,S,S,S,S,S,S,S,S,S,S,X,X],
        [X,X,S,L,L,L,L,L,L,L,L,L,L,S,X,X],
        [X,X,S,L,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,L,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,L,M,M,D,M,M,D,M,M,D,S,X,X],
        [X,X,S,L,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,L,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,L,M,M,D,M,M,D,M,M,D,S,X,X],
        [X,X,S,L,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,L,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,D,D,D,D,D,D,D,D,D,D,S,X,X],
        [X,X,S,S,S,S,S,S,S,S,S,S,S,S,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]


def crystal(D, M, L):
    S = (max(0,D[0]-20), max(0,D[1]-20), max(0,D[2]-20), 255)
    return [
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,S,S,S,S,S,X,X,X,X,X,X],
        [X,X,X,X,S,D,D,D,D,D,S,X,X,X,X,X],
        [X,X,X,S,D,M,M,L,M,M,D,S,X,X,X,X],
        [X,X,X,S,D,M,L,L,L,M,D,S,X,X,X,X],
        [X,X,S,D,M,L,M,M,M,L,M,D,S,X,X,X],
        [X,X,S,D,M,M,M,M,M,M,M,D,S,X,X,X],
        [X,X,S,D,M,M,M,M,M,M,M,D,S,X,X,X],
        [X,X,S,D,M,M,M,M,M,M,M,D,S,X,X,X],
        [X,X,S,D,M,M,M,M,M,M,M,D,S,X,X,X],
        [X,X,X,S,D,M,M,M,M,M,D,S,X,X,X,X],
        [X,X,X,X,S,D,D,D,D,D,S,X,X,X,X,X],
        [X,X,X,X,X,S,S,S,S,S,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]


# ── ITEM TEXTURES ─────────────────────────────────────────────────────────────

def make_zirconium_ingot():
    save(grid_img(ingot(ZR_D, ZR_M, ZR_L, ZR_S)), os.path.join(ITEM, "zirconium_ingot.png"))


def make_zirconium_ore_raw():
    D, M, L, S = ZR_D, ZR_M, ZR_L, ZR_S
    F = ZRFLK
    save(grid_img([
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,S,S,S,S,S,X,X,X,X,X,X],
        [X,X,X,S,S,D,D,D,D,D,S,S,X,X,X,X],
        [X,X,S,D,D,M,M,F,M,M,D,D,S,X,X,X],
        [X,X,S,D,M,M,F,F,F,M,M,D,S,X,X,X],
        [X,S,D,M,L,M,F,M,F,M,L,M,D,S,X,X],
        [X,S,D,M,L,M,M,M,M,M,L,M,D,S,X,X],
        [X,S,D,M,M,M,F,M,M,F,M,M,D,S,X,X],
        [X,S,D,M,M,M,M,M,M,M,M,M,D,S,X,X],
        [X,X,S,D,M,L,M,M,M,L,M,D,S,X,X,X],
        [X,X,S,D,D,M,M,M,M,M,D,D,S,X,X,X],
        [X,X,X,S,D,D,M,M,D,D,S,X,X,X,X,X],
        [X,X,X,X,S,S,S,S,S,S,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]), os.path.join(ITEM, "zirconium_ore_raw.png"))


def make_uranium_hexafluoride():
    save(grid_img(crystal(UF6_D, UF6_M, UF6_L)), os.path.join(ITEM, "uranium_hexafluoride.png"))


def make_enriched_uranium_hexafluoride():
    save(grid_img(crystal(EUF6_D, EUF6_M, EUF6_L)), os.path.join(ITEM, "enriched_uranium_hexafluoride.png"))


def make_depleted_uranium_hexafluoride():
    save(grid_img(crystal(DUF6_D, DUF6_M, DUF6_L)), os.path.join(ITEM, "depleted_uranium_hexafluoride.png"))


def make_uranium_dioxide_powder():
    D, M, L = UO2_D, UO2_M, UO2_L
    img = px()
    for (x, y, c) in [
        (4,3,D),(5,3,M),(9,3,D),(10,3,M),(13,3,D),
        (3,4,D),(4,4,M),(5,4,L),(6,4,D),(8,4,D),(9,4,M),(10,4,L),(11,4,D),(12,4,D),(13,4,M),(14,4,D),
        (3,5,M),(4,5,L),(5,5,M),(6,5,M),(8,5,M),(9,5,L),(10,5,M),(11,5,M),(12,5,L),(13,5,M),(14,5,M),
        (4,6,D),(5,6,M),(6,6,L),(7,6,M),(8,6,M),(9,6,D),(10,6,M),(11,6,L),(12,6,M),(13,6,D),
        (3,7,D),(4,7,M),(5,7,D),(7,7,D),(8,7,M),(9,7,L),(10,7,M),(11,7,D),(12,7,M),(14,7,D),
        (3,8,M),(4,8,L),(5,8,M),(6,8,D),(7,8,M),(8,8,D),(9,8,M),(10,8,D),(11,8,L),(12,8,D),(13,8,M),
        (4,9,D),(5,9,M),(6,9,L),(7,9,D),(8,9,M),(9,9,L),(10,9,M),(11,9,D),(12,9,M),(13,9,L),
        (3,10,D),(4,10,M),(5,10,D),(6,10,M),(7,10,M),(8,10,D),(9,10,M),(10,10,L),(11,10,M),(12,10,D),
        (4,11,M),(5,11,L),(7,11,M),(8,11,D),(9,11,M),(10,11,L),(11,11,M),(12,11,D),
        (5,12,D),(8,12,D),(9,12,M),(10,12,D),
    ]:
        img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "uranium_dioxide_powder.png"))


def make_uranium_dioxide_pellet():
    D, M, L = UO2_D, UO2_M, UO2_L
    S = (max(0,D[0]-30), max(0,D[1]-30), max(0,D[2]-30), 255)
    save(grid_img([
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,S,S,S,S,X,X,X,X,X,X],
        [X,X,X,X,X,S,D,D,D,D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D,M,L,D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D,M,M,D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D,M,M,D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D,D,D,D,S,X,X,X,X,X],
        [X,X,X,X,X,X,S,S,S,S,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]), os.path.join(ITEM, "uranium_dioxide_pellet.png"))


def _rod_grid(inner_col, inner_dark):
    C, D, L, S = ROD_M, ROD_D, ROD_L, ROD_S
    P, PD = inner_col, inner_dark
    return [
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,S,S,S,S,S,S,S,S,S,X,X,X,X],
        [X,X,X,S,L,L,L,L,L,L,L,S,X,X,X,X],
        [X,X,X,S,C,PD,P,P,P,PD,C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,PD,P,P,P,PD,C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,PD,P,P,P,PD,C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,PD,P,P,P,PD,C,S,X,X,X],
        [X,X,X,S,D,D, D,D,D,D, D,S,X,X,X],
        [X,X,X,S,S,S, S,S,S,S, S,S,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]


def make_fuel_rod_cladding():
    C, D, L, S = ROD_M, ROD_D, ROD_L, ROD_S
    save(grid_img([
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,S,S,S,S,S,S,S,S,S,X,X,X,X],
        [X,X,X,S,L,L,L,L,L,L,L,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,C,X,X,X,X,X,C,S,X,X,X,X],
        [X,X,X,S,D,D,D,D,D,D,D,S,X,X,X,X],
        [X,X,X,S,S,S,S,S,S,S,S,S,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]), os.path.join(ITEM, "fuel_rod_cladding.png"))


def make_fuel_rod():
    save(grid_img(_rod_grid(ROD_P, ROD_PD)), os.path.join(ITEM, "fuel_rod.png"))


def make_fuel_rod_assembly():
    C, D, L, S = ROD_M, ROD_D, ROD_L, ROD_S
    P, PD = ROD_P, ROD_PD
    save(grid_img([
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,S,S,S,X,S,S,S,X,S,S,S,X,X,X,X],
        [X,S,L,S,X,S,L,S,X,S,L,S,X,X,X,X],
        [X,S,P,S,X,S,P,S,X,S,P,S,X,X,X,X],
        [X,S,P,S,X,S,P,S,X,S,P,S,X,X,X,X],
        [X,S,PD,S,X,S,PD,S,X,S,PD,S,X,X,X,X],
        [X,S,P,S,X,S,P,S,X,S,P,S,X,X,X,X],
        [X,S,P,S,X,S,P,S,X,S,P,S,X,X,X,X],
        [X,S,PD,S,X,S,PD,S,X,S,PD,S,X,X,X,X],
        [X,S,P,S,X,S,P,S,X,S,P,S,X,X,X,X],
        [X,S,P,S,X,S,P,S,X,S,P,S,X,X,X,X],
        [X,S,D,S,X,S,D,S,X,S,D,S,X,X,X,X],
        [X,S,S,S,X,S,S,S,X,S,S,S,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]), os.path.join(ITEM, "fuel_rod_assembly.png"))


def make_spent_fuel_rod():
    C  = SROD_M
    D  = SROD_D
    L  = SROD_L
    S  = SROD_S
    P  = (140, 110, 25, 255)
    PD = (100,  80, 15, 255)
    G  = SROD_G
    save(grid_img([
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,S,S,S,S,S,S,S,S,S,X,X,X,X],
        [X,X,X,S,L,L,L,L,L,L,L,S,X,X,X,X],
        [X,X,X,S,C,G, P,P,P,G, C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,G, P,P,P,G, C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,G, P,P,P,G, C,S,X,X,X],
        [X,X,X,S,C,P, P,P,P,P, C,S,X,X,X],
        [X,X,X,S,C,G, P,P,P,G, C,S,X,X,X],
        [X,X,X,S,D,D, D,D,D,D, D,S,X,X,X],
        [X,X,X,S,S,S, S,S,S,S, S,S,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]), os.path.join(ITEM, "spent_fuel_rod.png"))


def make_depleted_uranium():
    save(grid_img(ingot(DU_D, DU_M, DU_L, DU_S)), os.path.join(ITEM, "depleted_uranium.png"))


def make_control_rod():
    D, M, L, S = CR_D, CR_M, CR_L, CR_S
    T, TD = CR_T, CR_TD
    save(grid_img([
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,S,TD,T, T,TD,S,X,X,X,X,X],
        [X,X,X,X,X,S,T, T, T, T,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, M, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, L, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, M, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, L, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, M, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, L, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, M, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, L, M, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,D, D, D, D,S,X,X,X,X,X],
        [X,X,X,X,X,S,S, S, S, S,S,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
        [X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X],
    ]), os.path.join(ITEM, "control_rod.png"))


def make_boron_carbide():
    D, M, L = BC_D, BC_M, BC_L
    img = px()
    for (x, y, c) in [
        (4,3,D),(5,3,M),(9,3,D),(10,3,M),(13,3,D),
        (3,4,D),(4,4,M),(5,4,L),(6,4,D),(8,4,D),(9,4,M),(10,4,L),(11,4,D),(12,4,D),(13,4,M),(14,4,D),
        (3,5,M),(4,5,L),(5,5,M),(6,5,M),(8,5,M),(9,5,L),(10,5,M),(11,5,M),(12,5,L),(13,5,M),(14,5,M),
        (4,6,D),(5,6,M),(6,6,L),(7,6,M),(8,6,M),(9,6,D),(10,6,M),(11,6,L),(12,6,M),(13,6,D),
        (3,7,D),(4,7,M),(5,7,D),(7,7,D),(8,7,M),(9,7,L),(10,7,M),(11,7,D),(12,7,M),(14,7,D),
        (3,8,M),(4,8,L),(5,8,M),(6,8,D),(7,8,M),(8,8,D),(9,8,M),(10,8,D),(11,8,L),(12,8,D),(13,8,M),
        (4,9,D),(5,9,M),(6,9,L),(7,9,D),(8,9,M),(9,9,L),(10,9,M),(11,9,D),(12,9,M),(13,9,L),
        (3,10,D),(4,10,M),(5,10,D),(6,10,M),(7,10,M),(8,10,D),(9,10,M),(10,10,L),(11,10,M),(12,10,D),
        (4,11,M),(5,11,L),(7,11,M),(8,11,D),(9,11,M),(10,11,L),(11,11,M),
        (5,12,D),(8,12,D),(9,12,M),(10,12,D),
    ]:
        img.putpixel((x, y), c)
    save(img, os.path.join(ITEM, "boron_carbide.png"))


def make_graphite_block_item():
    save(grid_img(ingot(GR_D, GR_M, GR_L, GR_S)), os.path.join(ITEM, "graphite_block_item.png"))


def make_bucket_item(fluid_rgba, name):
    img = px()
    r, g, b, a = fluid_rgba
    for y in range(2, 12):
        w = 10 if y < 10 else 8
        for bx in range(3, 3 + w):
            if bx < 13:
                img.putpixel((bx, y), (r, g, b, 200))
    save(img, os.path.join(ITEM, f"{name}.png"))


# ── FLUID TEXTURES ────────────────────────────────────────────────────────────

def make_fluid(color_rgba, name):
    still = px(16, 16, color_rgba)
    save(still, os.path.join(FLUID, f"{name}_still.png"))
    r, g, b, a = color_rgba
    flowing = px(16, 16, (max(r-20,0), max(g-20,0), max(b-20,0), max(a-30,0)))
    save(flowing, os.path.join(FLUID, f"{name}_flowing.png"))


# ── GAS CENTRIFUGE GUI ────────────────────────────────────────────────────────

def make_gas_centrifuge_gui():
    BG   = (198, 185, 154, 255)
    DARK = ( 85,  85,  85, 255)
    LITE = (255, 255, 255, 255)
    SLOT = (139, 139, 139, 255)
    BLK  = (  0,   0,   0, 255)
    ENRG = (180,  20,  20, 255)
    UF6C = (130, 200,  70, 255)
    EUFC = ( 50, 210,  50, 255)
    DUFC = (110, 125,  95, 255)
    ARRW = (200, 200,  50, 255)

    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d   = ImageDraw.Draw(img)
    d.rectangle([0, 0, 175, 165], fill=BG)

    def slot(x, y):
        d.rectangle([x, y, x+17, y+17], fill=BLK)
        d.rectangle([x+1, y+1, x+16, y+16], fill=SLOT)
        d.line([(x, y), (x+17, y)], fill=DARK)
        d.line([(x, y), (x, y+17)], fill=DARK)
        d.line([(x+17, y), (x+17, y+17)], fill=LITE)
        d.line([(x, y+17), (x+17, y+17)], fill=LITE)

    def bar(bx, top, w, h, label_col):
        d.rectangle([bx-1, top-1, bx+w, top+h], fill=DARK)
        d.rectangle([bx,   top,   bx+w-1, top+h-1], fill=BLK)
        d.rectangle([bx+1, top-5, bx+w-2, top-2], fill=label_col)

    # x=8 energy, x=30 UF6 in, x=118 enriched out, x=148 depleted out — all top=14, h=52
    bar(8,   14, 12, 52, ENRG)
    bar(30,  14, 12, 52, UF6C)
    bar(118, 14, 12, 52, EUFC)
    bar(148, 14, 12, 52, DUFC)

    # Progress arrow area x=76..100, y=35..50
    d.rectangle([75, 34, 101, 51], fill=DARK)
    d.rectangle([76, 35, 100, 50], fill=BLK)
    d.rectangle([77, 40,  94, 45], fill=ARRW)

    # Separator
    d.line([(7, 78), (168, 78)], fill=DARK)
    d.line([(7, 79), (168, 79)], fill=LITE)

    # Player inventory: rows at y=83, 101, 119; hotbar at y=141
    for row in range(3):
        for col in range(9):
            slot(8 + col*18, 83 + row*18)
    for col in range(9):
        slot(8 + col*18, 141)

    img.save(os.path.join(GUI, "gas_centrifuge.png"))
    print(f"  wrote textures/gui/gas_centrifuge.png")


# ── MAIN ──────────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    print("Generating nuclear reactor textures...\n")

    print("-- Block textures --")
    make_zirconite_ore()
    make_gas_centrifuge()

    print("\n-- Item textures --")
    make_zirconium_ingot()
    make_zirconium_ore_raw()
    make_uranium_hexafluoride()
    make_enriched_uranium_hexafluoride()
    make_depleted_uranium_hexafluoride()
    make_uranium_dioxide_powder()
    make_uranium_dioxide_pellet()
    make_fuel_rod_cladding()
    make_fuel_rod()
    make_fuel_rod_assembly()
    make_spent_fuel_rod()
    make_depleted_uranium()
    make_control_rod()
    make_boron_carbide()
    make_graphite_block_item()
    make_bucket_item((175, 215,  80, 255), "uranium_hexafluoride_bucket")
    make_bucket_item(( 80, 230,  50, 255), "enriched_uf6_bucket")
    make_bucket_item((120, 135, 100, 255), "depleted_uf6_bucket")
    make_bucket_item((120, 190, 235, 255), "heavy_water_bucket")
    make_bucket_item((230, 235, 240, 255), "steam_bucket")

    print("\n-- Fluid textures --")
    make_fluid((175, 215,  80, 200), "uranium_hexafluoride")
    make_fluid(( 80, 230,  50, 200), "enriched_uf6")
    make_fluid((120, 135, 100, 200), "depleted_uf6")
    make_fluid((120, 190, 235, 200), "heavy_water")
    make_fluid((228, 234, 240, 170), "steam")

    print("\n-- GUI textures --")
    make_gas_centrifuge_gui()

    print("\nDone — 33 textures regenerated.")
