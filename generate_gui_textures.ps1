# generate_gui_textures.ps1
# Generates all Firearms mod GUI textures (256x256 RGBA PNG) using System.Drawing.
# Run: powershell -ExecutionPolicy Bypass -File generate_gui_textures.ps1

Add-Type -AssemblyName System.Drawing

$OUT = "src\main\resources\assets\firearms\textures\gui"
New-Item -ItemType Directory -Force -Path $OUT | Out-Null

# ── Colours ──────────────────────────────────────────────────────────────────
$BG         = [System.Drawing.Color]::FromArgb(255, 198, 185, 154)
$SLOT_BG    = [System.Drawing.Color]::FromArgb(255, 139, 139, 139)
$SLOT_DARK  = [System.Drawing.Color]::FromArgb(255,  85,  85,  85)
$SLOT_LIGHT = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)
$TEXT_DARK  = [System.Drawing.Color]::FromArgb(255,  64,  64,  64)
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
    param([string]$Title = "")
    $bmp = New-Object System.Drawing.Bitmap(256, 256, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g   = [System.Drawing.Graphics]::FromImage($bmp)
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.FillRectangle((mkBrush $BG), 0, 0, 176, 166)
    if ($Title) {
        $font = New-Object System.Drawing.Font("Arial", 7, [System.Drawing.FontStyle]::Bold)
        $g.DrawString($Title, $font, (mkBrush $TEXT_DARK), 8, 6)
        $font.Dispose()
    }
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
    $g.DrawLine((mkPen $SLOT_DARK),  $x,      $y,      ($x+17), $y     )   # top
    $g.DrawLine((mkPen $SLOT_DARK),  $x,      $y,      $x,      ($y+17))   # left
    $g.DrawLine((mkPen $SLOT_LIGHT), $x,      ($y+17), ($x+17), ($y+17))   # bottom
    $g.DrawLine((mkPen $SLOT_LIGHT), ($x+17), $y,      ($x+17), ($y+17))   # right
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

function Draw-Flame {
    param($g, [int]$x, [int]$y, [int]$w=14, [int]$h=14)
    $g.FillRectangle((mkBrush $FLAME_B), ($x+3),     ($y+5), ($w-7),  ($h-6))
    $g.FillRectangle((mkBrush $FLAME_A), ($x+4),     ($y+2), ($w-9),  8     )
    $g.FillRectangle((mkBrush $FLAME_C), ($x+5),     $y,     ($w-11), 5     )
    $g.FillRectangle((mkBrush $FLAME_B), ($x+2),     ($y+7), 3,       ($h-8))
    $g.FillRectangle((mkBrush $FLAME_B), ($x+$w-5),  ($y+7), 3,       ($h-8))
}

function Draw-Bar {
    param($g, [int]$x, [int]$y, [int]$w=12, [int]$h=52, $color=$ENERGY_CLR, [double]$fill=0.5)
    $g.FillRectangle((mkBrush $SLOT_DARK),  ($x-1), ($y-1), ($w+2), ($h+2))
    $g.FillRectangle((mkBrush $BAR_EMPTY),  $x,     $y,     $w,     $h    )
    $fh = [int]($h * $fill)
    if ($fh -gt 0) {
        $g.FillRectangle((mkBrush $color), $x, ($y+$h-$fh), $w, $fh)
    }
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
    param($g, [int]$ix=7, [int]$iy=83, [int]$hx=7, [int]$hy=141)
    $font = New-Object System.Drawing.Font("Arial", 7)
    $g.DrawString("Inventory", $font, (mkBrush $TEXT_DARK), $ix, ($iy-11))
    $font.Dispose()
    Draw-Grid $g $ix $iy 9 3
    Draw-Grid $g $hx $hy 9 1
}

# ── Textures ──────────────────────────────────────────────────────────────────

Write-Host "Generating GUI textures -> $OUT`n"

# 1. Gunsmith Table
$gui = New-GUI "Gunsmith Table"
Draw-Grid      $gui.g 29 16 3 3
Draw-Arrow     $gui.g 97 34
Draw-Slot      $gui.g 123 34
Draw-PlayerInv $gui.g
Save-GUI       $gui "gunsmith_table.png"

# 2. Metal Press
$gui = New-GUI "Metal Press"
Draw-Slot      $gui.g 56 17
Draw-Slot      $gui.g 56 35
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-PlayerInv $gui.g
Save-GUI       $gui "metal_press.png"

# 3. Coal Generator
$gui = New-GUI "Coal Generator"
Draw-Slot      $gui.g 56 35
Draw-Slot      $gui.g 116 35
Draw-Flame     $gui.g 79 34
Draw-Bar       $gui.g 150 14 12 52 $ENERGY_CLR 0.60
Draw-PlayerInv $gui.g
Save-GUI       $gui "coal_generator.png"

# 4. Heat Treatment Furnace
$gui = New-GUI "Heat Treatment Furnace"
Draw-Slot      $gui.g 56 26
Draw-Slot      $gui.g 56 44
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-Bar       $gui.g 150 14 12 52 $ENERGY_CLR 0.45
Draw-PlayerInv $gui.g
Save-GUI       $gui "heat_treatment_furnace.png"

# 5. Lathe
$gui = New-GUI "Lathe"
Draw-Slot      $gui.g 56 26
Draw-Slot      $gui.g 56 44
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-Bar       $gui.g 150 14 12 52 $ENERGY_CLR 0.55
Draw-PlayerInv $gui.g
Save-GUI       $gui "lathe.png"

# 6. Assembly Bench
$gui = New-GUI "Assembly Bench"
Draw-Grid      $gui.g 29 16 2 3
Draw-Arrow     $gui.g 88 34
Draw-Slot      $gui.g 116 34
Draw-Bar       $gui.g 150 14 12 52 $ENERGY_CLR 0.50
Draw-PlayerInv $gui.g
Save-GUI       $gui "assembly_bench.png"

# 7. Fuel Generator
$gui = New-GUI "Fuel Generator"
Draw-Slot      $gui.g 56 26
Draw-Slot      $gui.g 56 44
Draw-Flame     $gui.g 79 34
Draw-Bar       $gui.g 7   14 12 52 $FUEL_CLR   0.70
Draw-Bar       $gui.g 150 14 12 52 $ENERGY_CLR 0.40
Draw-PlayerInv $gui.g
Save-GUI       $gui "fuel_generator.png"

# 8. Oil Derrick
$gui = New-GUI "Oil Derrick"
Draw-Bar       $gui.g 7   14 12 52 $ENERGY_CLR 0.50
Draw-Bar       $gui.g 150 14 12 52 $OIL_CLR    0.30
Draw-StatusBox $gui.g 60 20 56 40
Draw-PlayerInv $gui.g
Save-GUI       $gui "oil_derrick.png"

# 9. Refinery
$gui = New-GUI "Refinery"
Draw-Bar       $gui.g 7   14 12 52 $ENERGY_CLR 0.50
Draw-Bar       $gui.g 40  14 12 52 $OIL_CLR    0.60
Draw-Arrow     $gui.g 70 34
Draw-Bar       $gui.g 100 14 12 52 $FUEL_CLR   0.20
Draw-Slot      $gui.g 130 34
Draw-PlayerInv $gui.g
Save-GUI       $gui "refinery.png"

# 10. Auto Turret
$gui = New-GUI "Auto Turret"
Draw-Slot      $gui.g 80 26
Draw-Bar       $gui.g 150 14 12 52 $ENERGY_CLR 0.80
Draw-StatusBox $gui.g 40 34 56 40
Draw-PlayerInv $gui.g
Save-GUI       $gui "auto_turret.png"

# 11. Gun Modification Table
$gui = New-GUI "Gun Modification Table"
Draw-Slot      $gui.g 26 26
Draw-Slot      $gui.g 62 17
Draw-Slot      $gui.g 62 53
Draw-Arrow     $gui.g 88 35
Draw-Slot      $gui.g 124 35
Draw-PlayerInv $gui.g
Save-GUI       $gui "gun_modification_table.png"

Write-Host "`nDone - 11 textures generated."
