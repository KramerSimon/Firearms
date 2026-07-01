"""
Connected-texture generator for Firearms mod multiblocks — requires: pip install Pillow

For every "<name>.png" listed below, generates a "<name>_connected.png" sibling: the
outer 2px border of the source texture is replaced by pixels cloned from the nearest
interior pixel, erasing the dark frame/outline that makes each block read as a separate
tile. What's left is the block's interior pattern stretched to the edges, which tiles
into neighbours far more seamlessly than the framed original — used when a multiblock's
controller reports the structure complete (see ModBlockStateProperties.CONNECTED).

Run from project root: python generate_connected_textures.py
"""

from PIL import Image
import os

ROOT = os.path.dirname(os.path.abspath(__file__))
BLOCK = os.path.join(ROOT, "src/main/resources/assets/firearms/textures/block")

BORDER = 2  # px cloned inward from each edge

# Priority multiblocks: EBF, Chemical Mixer, Reactor, Vehicle Garage, Aircraft Hangar.
SOURCES = [
    "blast_furnace_casing.png",
    "chemical_mixer_base.png",
    "chemical_mixer_wall.png",
    "reactor_base.png",
    "reactor_wall.png",
    "reactor_top.png",
    "garage_floor.png",
    "garage_wall.png",
    "garage_roof.png",
    "hangar_floor.png",
    "hangar_wall.png",
    "hangar_roof.png",
]


def make_connected(src_path, dst_path, border=BORDER):
    img = Image.open(src_path).convert("RGBA")
    w, h = img.size
    src = img.load()
    out = Image.new("RGBA", (w, h))
    dst = out.load()
    for y in range(h):
        for x in range(w):
            sx = min(max(x, border), w - 1 - border)
            sy = min(max(y, border), h - 1 - border)
            dst[x, y] = src[sx, sy]
    out.save(dst_path, "PNG")
    print(f"  wrote {os.path.relpath(dst_path, ROOT)}")


def main():
    for name in SOURCES:
        src_path = os.path.join(BLOCK, name)
        if not os.path.isfile(src_path):
            print(f"  SKIP (missing) {name}")
            continue
        stem = name[:-4]
        dst_path = os.path.join(BLOCK, f"{stem}_connected.png")
        make_connected(src_path, dst_path)


if __name__ == "__main__":
    main()
