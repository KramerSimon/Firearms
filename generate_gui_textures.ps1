# generate_gui_textures.ps1
# Generates all Firearms mod GUI textures (256x256 RGBA PNG) using System.Drawing.
# No text is drawn — all labels are rendered by the Java screen classes.
# Run: powershell -ExecutionPolicy Bypass -File generate_gui_textures.ps1

Add-Type -AssemblyName System.Drawing

$OUT = "src\main\resources\assets\firearms\textures\gui"
New-Item -ItemType Directory -Force -Path $OUT | Out-Null

# ── Colours ──────────────────────────────────────────────────────────────────
$BG         = [System.Drawing.Color]::FromArgb(255, 198, 185, 154)
$SLOT_BG    = [System.Drawing.Color]::FromArgb(255, 139, 139, 139)
$SLOT_DARK  = [System.Drawing.Color]::FromArgb(255,  85,  85,  85)
$SLOT_LIGHT = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)
$FLAME_A    = [System.Drawing.Color]::FromArgb(255, 255, 200,   0)
$FLAME_B    = [System.Drawing.Color]::FromArgb(255, 255, 100,   0)
$FLAME_C    = [System.Drawing.Color]::FromArgb(255, 255, 240, 120)
$ENERGY_CLR = [System.Drawing.Color]::FromArgb(255, 200,  20,  20)
$FUEL_CLR   = [System.Drawing.Color]::FromArgb(255, 255, 140,   0)
$OIL_CLR    = [System.Drawing.Color]::FromArgb(255,  15,  15,  15)
$STATUS_BG  = [System.Drawing.Color]::FromArgb(255,  70,  70,  70)
$BAR_EMPTY  = [System.Drawing.Color]::FromArgb(255,  25,  25,  25)

function mkBrush($c) { New-Object System.Drawing.SolidBrush($c) }
function mkPen($c)   { New-Object System.Drawing.Pen($c, 1) }

# ── Canvas ────────────────────────────────────────────────────────────────────
function New-GUI {
    $bmp = New-Object System.Drawing.Bitmap(256, 256, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g   = [System.Drawing.Graphics]::FromImage($bmp)
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.FillRectangle((mkBrush $BG), 0, 0, 176, 166)
    return @{ bmp = $bmp; g = $g }
}

function Save-GUI {
    param($gui, [string]$name)
    $path = Join-Path $OUT $name
    $gui.bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $gui.g.Dispose()
    $gui.bmp.Dispose()
    Write-Host "  saved  $name"
}

# ── Primitives ────────────────────────────────────────────────────────────────
function Draw-Slot {
    param($g, [int]$x, [int]$y)
    $g.FillRectangle((mkBrush $SLOT_BG), $x, $y, 18, 18)
    $g.DrawLine((mkPen $SLOT_DARK),  $x,      $y,      ($x+17), $y     )
    $g.DrawLine((mkPen $SLOT_DARK),  $x,      $y,      $x,      ($y+17))
    $g.DrawLine((mkPen $SLOT_LIGHT), $x,      ($y+17), ($x+17), ($y+17))
    $g.DrawLine((mkPen $SLOT_LIGHT), ($x+17), $y,      ($x+17), ($y+17))
}

function Draw-Grid {
    param($g, [int]$x, [int]$y, [int]$cols, [int]$rows)
    for ($r = 0; $r -lt $rows; $r++) {
        for ($c = 0; $c -lt $cols; $c++) {
            Draw-Slot $g ($x + $c*18) ($y + $r*18)
        }
    }
}

function Draw-Arrow {
    param($g, [int]$x, [int]$y)
    $cy = $y + 7
    $g.FillRectangle((mkBrush $SLOT_DARK), $x, ($cy-2), 14, 5)
    for ($i = 0; $i -lt 8; $i++) {
        $half = 7 - $i
        $g.DrawLine((mkPen $SLOT_DARK), ($x+14+$i), ($cy-$half), ($x+14+$i), ($cy+$half))
    }
}

function Draw-FlameSlot {
    # Empty recessed area in the GUI where the flame sprite renders at runtime.
    param($g, [int]$x, [int]$y, [int]$w=14, [int]$h=14)
    $g.FillRectangle((mkBrush $SLOT_DARK), ($x-1), ($y-1), ($w+2), ($h+2))
    $g.FillRectangle((mkBrush $BAR_EMPTY), $x,     $y,     $w,     $h    )
}

function Draw-FlameSprite {
    # Flame sprite at UV (176, 0) — read by animated blit in coal/fuel generator screens.
    param($g, [int]$ux=176, [int]$uy=0, [int]$w=14, [int]$h=14)
    $g.FillRectangle((mkBrush $FLAME_B), ($ux+3),    ($uy+5), ($w-7),  ($h-6))
    $g.FillRectangle((mkBrush $FLAME_A), ($ux+4),    ($uy+2), ($w-9),  8     )
    $g.FillRectangle((mkBrush $FLAME_C), ($ux+5),    $uy,     ($w-11), 5     )
    $g.FillRectangle((mkBrush $FLAME_B), ($ux+2),    ($uy+7), 3,       ($h-8))
    $g.FillRectangle((mkBrush $FLAME_B), ($ux+$w-5), ($uy+7), 3,       ($h-8))
}

function Draw-ArrowSlot {
    # Empty recessed area in the GUI where the arrow sprite renders at runtime.
    param($g, [int]$x, [int]$y)
    $g.FillRectangle((mkBrush $SLOT_DARK), ($x-1), ($y+4),  26, 9)
    $g.FillRectangle((mkBrush $BAR_EMPTY), $x,     ($y+5),  24, 7)
}

function Draw-ArrowSprite {
    # Arrow sprite at UV (176, 0) — read by animated blit in refinery screen.
    param($g, [int]$ux=176, [int]$uy=0, [int]$w=24, [int]$h=16)
    $cy = $uy + ($h / 2)
    $g.FillRectangle((mkBrush $SLOT_DARK), $ux, ($cy-2), ($w-9), 5)
    for ($i = 0; $i -lt 9; $i++) {
        $half = [Math]::Max(0, ($h/2) - 1 - $i)
        $g.DrawLine((mkPen $SLOT_DARK), ($ux+$w-9+$i), ($cy-$half), ($ux+$w-9+$i), ($cy+$half))
    }
}

function Draw-Bar {
    # Empty bar container — Java draws the color fill on top at runtime.
    param($g, [int]$x, [int]$y, [int]$w=12, [int]$h=52)
    $g.FillRectangle((mkBrush $SLOT_DARK), ($x-1), ($y-1), ($w+2), ($h+2))
    $g.FillRectangle((mkBrush $BAR_EMPTY), $x,     $y,     $w,     $h    )
}

function Draw-StatusBox {
    param($g, [int]$x, [int]$y, [int]$w=56, [int]$h=40)
    $g.FillRectangle((mkBrush $STATUS_BG), $x, $y, $w, $h)
    $g.DrawLine((mkPen $SLOT_DARK),  $x,        $y,        ($x+$w-1), $y       )
    $g.DrawLine((mkPen $SLOT_DARK),  $x,        $y,        $x,        ($y+$h-1))
    $g.DrawLine((mkPen $SLOT_LIGHT), ($x+$w-1), $y,        ($x+$w-1), ($y+$h-1))
    $g.DrawLine((mkPen $SLOT_LIGHT), $x,        ($y+$h-1), ($x+$w-1), ($y+$h-1))
}

function Draw-PlayerInv {
    # Player inventory and hotbar slots — no text labels.
    param($g, [int]$ix=7, [int]$iy=83, [int]$hx=7, [int]$hy=141)
    Draw-Grid $g $ix $iy 9 3
    Draw-Grid $g $hx $hy 9 1
}

# ── Textures ──────────────────────────────────────────────────────────────────

Write-Host "Generating GUI textures -> $OUT`n"

# 1. Gunsmith Table
$gui = New-GUI
Draw-Grid      $gui.g 29 16 3 3
Draw-Arrow     $gui.g 97 34
Draw-Slot      $gui.g 123 34
Draw-PlayerInv $gui.g
Save-GUI       $gui "gunsmith_table.png"

# 2. Metal Press
$gui = New-GUI
Draw-Slot      $gui.g 56 17
Draw-Slot      $gui.g 56 35
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-PlayerInv $gui.g
Save-GUI       $gui "metal_press.png"

# 3. Coal Generator
$gui = New-GUI
Draw-Slot      $gui.g 56 35
Draw-Slot      $gui.g 116 35
Draw-FlameSlot $gui.g 79 34
Draw-Bar       $gui.g 150 14
Draw-PlayerInv $gui.g
Draw-FlameSprite $gui.g 176 0
Save-GUI       $gui "coal_generator.png"

# 4. Heat Treatment Furnace
$gui = New-GUI
Draw-Slot      $gui.g 56 26
Draw-Slot      $gui.g 56 44
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-Bar       $gui.g 150 14
Draw-PlayerInv $gui.g
Save-GUI       $gui "heat_treatment_furnace.png"

# 5. Lathe
$gui = New-GUI
Draw-Slot      $gui.g 56 26
Draw-Slot      $gui.g 56 44
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-Bar       $gui.g 150 14
Draw-PlayerInv $gui.g
Save-GUI       $gui "lathe.png"

# 6. Assembly Bench — 3x3 shapeless input grid
$gui = New-GUI
Draw-Grid        $gui.g 29 16 3 3
Draw-ArrowSlot   $gui.g 88 35
Draw-Slot        $gui.g 116 35
Draw-Bar         $gui.g 152 10
Draw-PlayerInv   $gui.g
Draw-ArrowSprite $gui.g 176 0
Save-GUI         $gui "assembly_bench.png"

# 7. Fuel Generator
$gui = New-GUI
Draw-Slot      $gui.g 56 26
Draw-Slot      $gui.g 56 44
Draw-FlameSlot $gui.g 79 34
Draw-Bar       $gui.g 7   14
Draw-Bar       $gui.g 150 14
Draw-PlayerInv $gui.g
Draw-FlameSprite $gui.g 176 0
Save-GUI       $gui "fuel_generator.png"

# 8. Oil Derrick
$gui = New-GUI
Draw-Bar       $gui.g 7   14
Draw-Bar       $gui.g 150 14
Draw-StatusBox $gui.g 60 20 56 40
Draw-PlayerInv $gui.g
Save-GUI       $gui "oil_derrick.png"

# 9. Refinery — 3 output slots: fuel (130,14), gun_oil (130,32), rubber (130,50)
$gui = New-GUI
Draw-Bar       $gui.g 7   14
Draw-Bar       $gui.g 40  14
Draw-ArrowSlot $gui.g 70 34
Draw-Bar       $gui.g 100 14
Draw-Slot      $gui.g 130 14
Draw-Slot      $gui.g 130 32
Draw-Slot      $gui.g 130 50
Draw-PlayerInv $gui.g
Draw-ArrowSprite $gui.g 176 0
Save-GUI       $gui "refinery.png"

# 10. Auto Turret
$gui = New-GUI
Draw-Slot      $gui.g 80 26
Draw-Bar       $gui.g 150 14
Draw-StatusBox $gui.g 40 34 56 40
Draw-PlayerInv $gui.g
Save-GUI       $gui "auto_turret.png"

# 11. Gun Modification Table
$gui = New-GUI
Draw-Slot      $gui.g 26 26
Draw-Slot      $gui.g 62 17
Draw-Slot      $gui.g 62 53
Draw-Arrow     $gui.g 88 35
Draw-Slot      $gui.g 124 35
Draw-PlayerInv $gui.g
Save-GUI       $gui "gun_modification_table.png"

# 12. Coke Oven
$gui = New-GUI
Draw-Slot        $gui.g 56 35
Draw-Slot        $gui.g 116 35
Draw-ArrowSlot   $gui.g 88 34
Draw-Bar         $gui.g 150 14
Draw-PlayerInv   $gui.g
Draw-ArrowSprite $gui.g 176 0
Save-GUI         $gui "coke_oven.png"

# 13. Electric Blast Furnace
$gui = New-GUI
Draw-Bar         $gui.g 7   14
Draw-Slot        $gui.g 56  17
Draw-Slot        $gui.g 56  53
Draw-ArrowSlot   $gui.g 78  30
Draw-Slot        $gui.g 116 35
Draw-PlayerInv   $gui.g
Draw-ArrowSprite $gui.g 176 0
Save-GUI         $gui "ebf.png"

# 14. Chemical Mixer — 5 slots: inputA/B/bucket-in on left, item-out/empty-bucket-out on right
$gui = New-GUI
Draw-Bar         $gui.g  7  14
Draw-Bar         $gui.g 26  14
Draw-Slot        $gui.g 47  17
Draw-Slot        $gui.g 47  35
Draw-Slot        $gui.g 47  53
Draw-ArrowSlot   $gui.g 75  35
Draw-Slot        $gui.g 113 26
Draw-Slot        $gui.g 113 53
Draw-Bar         $gui.g 140 14
Draw-PlayerInv   $gui.g
Draw-ArrowSprite $gui.g 176 0
Save-GUI         $gui "chemical_mixer.png"

# 15. Acid Bath — energy bar, acid bar, item input, arrow, item output
$gui = New-GUI
Draw-Bar         $gui.g  7 14
Draw-Bar         $gui.g 26 14
Draw-Slot        $gui.g 56 35
Draw-ArrowSlot   $gui.g 80 35
Draw-Slot        $gui.g 116 35
Draw-PlayerInv   $gui.g
Draw-ArrowSprite $gui.g 176 0
Save-GUI         $gui "acid_bath.png"

# 16. Water Pump — energy bar + water tank bar, no item slots
$gui = New-GUI
Draw-Bar         $gui.g  7 14
Draw-Bar         $gui.g 26 14
Draw-PlayerInv   $gui.g
Save-GUI         $gui "water_pump.png"

Write-Host "`nDone - 16 textures generated."
