#!/usr/bin/env python3
"""
Pixel-Art-Generator für Aetherite Toolkit.
Formen werden als Masken (ein Buchstabe pro Material) definiert und automatisch
im Minecraft-Stil schattiert: Licht von oben links, dunkle Umrandung, Glanzpunkte.
"""
import math, os
from PIL import Image

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
OUT = os.path.join(BASE, "src/main/resources/assets/aetherkit/textures")
os.makedirs(os.path.join(BASE, "docs"), exist_ok=True)
os.makedirs(f"{OUT}/item", exist_ok=True)
os.makedirs(f"{OUT}/block", exist_ok=True)
os.makedirs(f"{OUT}/models/armor", exist_ok=True)


def hx(c):
    c = c.lstrip("#")
    return tuple(int(c[i:i + 2], 16) for i in (0, 2, 4)) + (255,)


def ramp(*cols):
    return [hx(c) for c in cols]


# Rampen: [Umriss, dunkel, mittel-dunkel, mittel, hell, Glanz]
R = {
    "B": ramp("#0E1F3A", "#1A4670", "#2878A3", "#3DB2C6", "#7DE3DC", "#DBFFFA"),   # Aetherit
    "H": ramp("#24170A", "#4F3519", "#6E4B27", "#8F6838", "#B08650", "#CDA56C"),   # Holz
    "G": ramp("#220E3B", "#3F1D70", "#6B3BB3", "#9566E3", "#C3A0FF", "#EFE2FF"),   # Violett-Edelstein
    "M": ramp("#3A2306", "#7D4F0F", "#B57F1C", "#E0AE38", "#F7D46A", "#FFF4BE"),   # Gold
    "S": ramp("#1B1F25", "#454D57", "#6C7682", "#97A2AE", "#C3CCD6", "#EEF3F8"),   # Stahl
    "L": ramp("#24130A", "#4E2B15", "#70411F", "#93592C", "#B5763E", "#D09257"),   # Leder
    "T": ramp("#3A342C", "#7C756A", "#A69F92", "#CBC4B6", "#E8E2D6", "#FFFFFF"),   # Faden
    "E": ramp("#03120D", "#0A3529", "#136B53", "#24A381", "#5CD9B5", "#B5FFE8"),   # Enderauge
    "P": ramp("#000000", "#020504", "#040A08", "#06100C", "#0A1A14", "#12281F"),   # Pupille
    "F": ramp("#0D260B", "#1F4F18", "#337F27", "#4FAE3D", "#86D965", "#C4F59E"),   # Blatt
    "C": ramp("#0B2C18", "#145F36", "#22995A", "#47CF83", "#97F2B6", "#E3FFEC"),   # Smaragdkristall
    "Q": ramp("#4A3A22", "#A8905F", "#C9B07E", "#E2CE9F", "#F2E5C2", "#FFF9E6"),   # Pergament
    "R": ramp("#26170A", "#5C3A1C", "#7E5229", "#A06B37", "#C2894B", "#DDA766"),   # Rolle
    "W": ramp("#2A1A0B", "#5E3D1E", "#81572D", "#A2723E", "#BF8D55", "#D8A86E"),   # Schale
    "K": ramp("#0E1F3A", "#14365A", "#1E5880", "#2B7FA0", "#46A9BE", "#8ED8DA"),   # dunkles Aetherit
}

# Feste Farben (werden nicht schattiert): Zeichen -> (Farbe, Umrissfarbe)
FIXED = {
    "1": ("#C2410F", "#5A1404"), "2": ("#F2861B", "#5A1404"), "3": ("#FFD23F", "#5A1404"),
    "4": ("#FFF8C8", "#5A1404"),                                     # Flamme
    "n": ("#1E5880", "#0E1F3A"),                                     # Runenschrift
    "w": ("#B3202A", "#4A0A0E"), "x": ("#E0444A", "#4A0A0E"),        # Siegelwachs
    "k": ("#E0AE38", "#3A2306"),                                     # Goldknauf
    "a": ("#D9B07A", "#3B2410"), "u": ("#8C5A2B", "#2A1608"),        # Nüsse
    "r": ("#C4283A", "#4A0A12"), "z": ("#4A2A16", "#1A0C04"),        # Beeren, Schoko
    "y": ("#EFE3B0", "#5A4E28"), "p": ("#F07AB8", "#5A1640"),        # Kerne, Blüte
    "q": ("#FFFFFF", "#5A1640"),
    "j": ("#BE9660", "#24170A"), "g": ("#5A3D1F", "#24170A"), "f": ("#8F6838", "#24170A"),
    "e": ("#64431F", "#24170A"), "o": ("#B9C3CD", "#24170A"), "l": ("#A0622F", "#24170A"),
    "m": ("#6E3E1A", "#24170A"), "b": ("#F2C94C", "#24170A"),
}
SPARKLE_PARTS = {"B", "G", "C", "K", "E", "EMB", "EMK", "VOI", "VOK", "Z"}


def h(x, y, seed=0):
    v = (x * 374761393 + y * 668265263 + seed * 2147483647) & 0xFFFFFFFF
    v = (v ^ (v >> 13)) * 1274126177 & 0xFFFFFFFF
    return (v ^ (v >> 16)) & 0xFFFF


def render(mask, seed=1, outline=True, sparkle=True, size=16, remap=None):
    """mask: Liste von Strings. Großbuchstaben = schattierte Materialien, Kleinbuchstaben/Ziffern = feste Farben."""
    H_ = len(mask)
    W_ = len(mask[0])
    assert all(len(r) == W_ for r in mask), [len(r) for r in mask]
    img = Image.new("RGBA", (W_, H_), (0, 0, 0, 0))
    px = img.load()

    def at(x, y):
        if 0 <= x < W_ and 0 <= y < H_:
            return mask[y][x]
        return "."

    def run(x, y, dx, dy, ch):
        n = 0
        while at(x + dx * (n + 1), y + dy * (n + 1)) == ch:
            n += 1
        return n + 1

    for y in range(H_):
        for x in range(W_):
            ch = at(x, y)
            if ch == ".":
                continue
            if ch in FIXED:
                px[x, y] = hx(FIXED[ch][0])
                continue
            rp = R[(remap or {}).get(ch, ch)]
            d_tl = run(x, y, -1, -1, ch)
            d_br = run(x, y, 1, 1, ch)
            t = d_tl / (d_tl + d_br)
            idx = round(4.6 - t * 3.2)
            tl = at(x - 1, y) != ch or at(x, y - 1) != ch
            br = at(x + 1, y) != ch or at(x, y + 1) != ch
            if tl and not br:
                idx += 1
            if br and not tl:
                idx -= 1
            idx = max(1, min(5, idx))
            if sparkle and (remap or {}).get(ch, ch) in SPARKLE_PARTS and idx in (3, 4) and h(x, y, seed) % 13 == 0:
                idx = 5
            px[x, y] = rp[idx]

    if outline:
        out = img.copy()
        op = out.load()
        for y in range(H_):
            for x in range(W_):
                if at(x, y) != ".":
                    continue
                for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
                    n = at(x + dx, y + dy)
                    if n != ".":
                        op[x, y] = hx(FIXED[n][1]) if n in FIXED else R[(remap or {}).get(n, n)][0]
                        break
        img = out
    return img


def from_fn(fn, w=16, h_=16):
    """Erzeugt eine Maske aus einer Funktion f(x, y) -> Zeichen."""
    return ["".join(fn(x, y) for x in range(w)) for y in range(h_)]


def save(img, name, folder="item"):
    img.save(f"{OUT}/{folder}/{name}.png")


# =====================================================================
#  Werkzeuge (Achse diagonal: s = x + y quer, d = x - y entlang)
# =====================================================================

def sword(x, y):
    s, d = x + y, x - y
    if 1 <= y <= 10 and s in (15, 16, 17) and x <= 14:
        return "B"
    if d in (-6, -7) and 13 <= s <= 20:
        return "G"
    if -12 <= d <= -8 and s in (15, 16, 17):
        return "L" if d % 2 == 0 else "H"
    if -14 <= d <= -13 and s in (15, 16, 17):
        return "M"
    return "."


PICKAXE = [
    "................",
    "...BBBBBBB......",
    "..BBBBBBBBBB....",
    ".BB.....BBBBB...",
    ".B......HBBBB...",
    ".......HH.BBBB..",
    "......HH...BBB..",
    ".....HH.....BB..",
    "....GG......BB..",
    "...HH.......BBB.",
    "..HH.........BB.",
    ".HH...........B.",
    "HH..............",
    "H...............",
    "................",
    "................",
]

AXE = [
    "................",
    "......BBB.......",
    ".....BBBBB......",
    "....BBBBBBB..HH.",
    "....BBBBBBBBHH..",
    ".....BBBBBBHH...",
    "......BBBBHHK...",
    ".......BBHHKK...",
    "........HH......",
    ".......HH.......",
    "......GG........",
    ".....HH.........",
    "....HH..........",
    "...HH...........",
    "..HH............",
    ".HH.............",
]

SHOVEL = [
    "................",
    "...........BBB..",
    "..........BBBBB.",
    ".........BBBBBB.",
    ".........BBBBB..",
    "........MMBBB...",
    ".......HH.......",
    "......HH........",
    ".....HH.........",
    "....HH..........",
    "...GG...........",
    "..HH............",
    ".HH.............",
    ".H..............",
    "................",
    "................",
]

HOE = [
    "................",
    "....BBBBBBBBB...",
    "....BBBBBBBBBB..",
    "....BB....MM....",
    "..........HH....",
    ".........HH.....",
    "........HH......",
    ".......HH.......",
    "......GG........",
    ".....HH.........",
    "....HH..........",
    "...HH...........",
    "..HH............",
    ".HH.............",
    ".H..............",
    "................",
]


def hammer(x, y):
    s, d = x + y, x - y
    a = abs(s - 15)
    # Kopf: grosser Block quer zur Achse mit abgeschrägten Ecken
    if a <= 6 and 2 <= d <= 9 and not (a == 6 and d in (2, 9)):
        if a >= 5:
            return "K"            # Schlagflächen
        if a == 4:
            return "M"            # Goldbänder
        if d in (5, 6) and a <= 1:
            return "G"            # Edelstein
        return "B"
    if -13 <= d <= 1 and s in (15, 16):
        return "G" if d in (-6, -7) else "H"
    if d <= -14 and s in (15, 16):
        return "M"
    return "."


def excavator(x, y):
    s, d = x + y, x - y
    u = (d - 7.5) / 1.414   # entlang der Achse
    v = (s - 15.5) / 1.414  # quer
    if (u / 4.0) ** 2 + (v / 3.6) ** 2 <= 1.0:
        return "B"
    if d in (2, 3) and abs(s - 15.5) <= 2:
        return "M"
    if -11 <= d <= 1 and s in (15, 16):
        return "G" if d in (-5, -6) else "H"
    if -14 <= d <= -12 and abs(s - 15.5) <= 2.6:
        return "M"                # T-Griff
    return "."


LUMBER_AXE = [
    "................",
    "...SSBB.........",
    "..SBBBBB.....HH.",
    ".SBBBBBBB...HH..",
    ".SBBBBBBBBMHH...",
    ".SBBBBBBBMHHK...",
    ".SBBBBBBMHHKK...",
    "..SBBBB.HHKK....",
    "...SBB.HH.......",
    "......HH........",
    ".....HH.........",
    "....GG..........",
    "...HH...........",
    "..HH............",
    ".HH.............",
    "MM..............",
]


# =====================================================================
#  Rüstungs-Icons
# =====================================================================
HELMET = [
    "................",
    "................",
    "................",
    ".....BBBBBB.....",
    "....BBBBBBBB....",
    "...BBBBGGBBBB...",
    "...BBBBBBBBBB...",
    "...BBBKKKKBBB...",
    "...BBB....BBB...",
    "...BBB....BBB...",
    "...KK......KK...",
    "................",
    "................",
    "................",
    "................",
    "................",
]

CHESTPLATE = [
    "................",
    "..BBBB....BBBB..",
    ".BBBBBB..BBBBBB.",
    ".BBBBBBBBBBBBBB.",
    ".BBBBBBBBBBBBBB.",
    ".BBB.BBGGBB.BBB.",
    ".BBB.BBGGBB.BBB.",
    ".KKK.BBBBBB.KKK.",
    ".....BBBBBB.....",
    ".....BBBBBB.....",
    ".....MMMMMM.....",
    ".....BBBBBB.....",
    ".....BBBBBB.....",
    "......KKKK......",
    "................",
    "................",
]

LEGGINGS = [
    "................",
    "................",
    "....MMMGGMMM....",
    "....BBBBBBBB....",
    "....BBBBBBBB....",
    "....BBBB.BBB....",
    "....BBB..BBB....",
    "....BBB..BBB....",
    "....GBB..BBG....",
    "....BBB..BBB....",
    "....BBB..BBB....",
    "....BBB..BBB....",
    "....KKK..KKK....",
    "................",
    "................",
    "................",
]

BOOTS = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "...MMM....MMM...",
    "...BBB....BBB...",
    "...BBB....BBB...",
    "...BBB....BBB...",
    "..BBBG....GBBB..",
    ".BBBBB....BBBBB.",
    ".KKKKK....KKKKK.",
    "................",
    "................",
    "................",
    "................",
]

# =====================================================================
#  Utility-Items
# =====================================================================
ENDER_POUCH = [
    "................",
    "......T..T......",
    ".......TT.......",
    "......LLLL......",
    ".....LLMMLL.....",
    ".....LLLLLL.....",
    "....LLLLLLLL....",
    "...LLLLEELLLL...",
    "...LLLEPPELLL...",
    "..LLLLEPPELLLL..",
    "..LLLLLEELLLLL..",
    "..LLLLLLLLLLLL..",
    "..LLLLLLLLLLLL..",
    "...LLLLLLLLLL...",
    "....LLLLLLLL....",
    "................",
]

TORCH_STAFF = [
    "............1...",
    "...........121..",
    "..........B232B.",
    "..........B343B.",
    "...........BBB..",
    "..........MM....",
    ".........HH.....",
    "........HH......",
    ".......HH.......",
    "......MM........",
    ".....HH.........",
    "....HH..........",
    "...HH...........",
    "..HH............",
    ".MM.............",
    "................",
]

GROWTH_STAFF = [
    "..........C.....",
    ".........CCC....",
    "...FF...CCCCC...",
    "..FFFF...CCC.FF.",
    "...FFFF..MM.FFF.",
    "....FFF.HH.FFF..",
    ".......HH..FF...",
    "......HHFF......",
    ".....HH.FF......",
    "....MM..........",
    "...HH...........",
    "..HH............",
    ".HH.............",
    ".H..............",
    "................",
    "................",
]

RECALL_SCROLL = [
    "................",
    "..RRRRRRRRRRRR..",
    ".kRRRRRRRRRRRRk.",
    "...QQQQQQQQQQ...",
    "...QQnnQnnnQQ...",
    "...QQQQQQQQQQ...",
    "...QnnnQQnnQQ...",
    "...QQQQQQQQQQ...",
    "...QQnnnnQnQQ...",
    "...QQQQQwwQQQ...",
    "...QQQQwxxwQQ...",
    "...QQQQQwwQQQ...",
    "..RRRRRRRRRRRR..",
    ".kRRRRRRRRRRRRk.",
    "................",
    "................",
]

TRAIL_MIX = [
    "................",
    "................",
    "................",
    "................",
    "......ar.u......",
    "....uazryaru....",
    "...yrauzrayuz...",
    "..WWWWWWWWWWWW..",
    "..WWWWWWWWWWWW..",
    "...WWWWWWWWWW...",
    "....WWWWWWWW....",
    ".....WWWWWW.....",
    "................",
    "................",
    "................",
    "................",
]

def shard(x, y):
    s, d = x + y, x - y
    if -8 <= d <= 11:
        w = 3.3
        if d > 3:
            w = 3.3 * (11 - d) / 8
        if d < -5:
            w = 3.3 * (d + 9) / 4
        if abs(s - 15) <= w:
            return "B" if s <= 15 else "K"
    # kleiner violetter Kristall unten rechts
    if -1 <= d - 6 <= 0 + 5 and False:
        return "G"
    s2, d2 = s - 22, d - 2
    if -3 <= d2 <= 4:
        w2 = 1.4 if d2 <= 1 else 1.4 * (4 - d2) / 3
        if d2 < -2:
            w2 = 0.8
        if abs(s2) <= w2:
            return "G"
    return "."


# =====================================================================
#  Programmatische Items
# =====================================================================

def aether_core():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    cx, cy, r = 7.5, 7.5, 5.6
    lx, ly = 5.2, 5.0
    B, G, M = R["B"], R["G"], R["M"]
    for y in range(16):
        for x in range(16):
            dx, dy = x - cx, y - cy
            dist = math.hypot(dx, dy)
            if dist <= r:
                light = 1 - math.hypot(x - lx, y - ly) / (r * 1.7)
                swirl = math.sin(math.atan2(dy, dx) * 2 + dist * 0.9)
                pal = G if swirl > 0.35 and dist > 2 else B
                idx = max(1, min(5, round(1 + light * 4.6)))
                px[x, y] = pal[idx]
    # Goldring (Ellipse)
    for y in range(16):
        for x in range(16):
            e = ((x - cx) / 7.2) ** 2 + ((y - cy) / 2.4) ** 2
            if 0.72 <= e <= 1.08 and not (y < cy and math.hypot(x - cx, y - cy) < r - 0.3):
                px[x, y] = M[4] if x < cx else M[3] if y < cy + 1 else M[2]
    # Umriss
    out = img.copy()
    op = out.load()
    for y in range(16):
        for x in range(16):
            if px[x, y][3]:
                continue
            for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3]:
                    op[x, y] = hx("#160A2A")
                    break
    op[5, 4] = R["B"][5]
    op[6, 4] = R["B"][5]
    op[5, 5] = R["B"][5]
    return out


def magnet_ring():
    def fn(x, y):
        cx, cy = 7.5, 9.5
        e_out = ((x - cx) / 6.0) ** 2 + ((y - cy) / 4.2) ** 2
        e_in = ((x - cx) / 3.6) ** 2 + ((y - cy) / 2.0) ** 2
        if 3 <= y <= 5 and 6 <= x <= 9:
            return "B"                       # Fassung + Stein oben
        if y == 6 and 6 <= x <= 9:
            return "M"
        if e_out <= 1.0 and e_in > 1.0:
            if y >= cy + 2 and x <= 4:
                return "x"                   # roter Pol
            if y >= cy + 2 and x >= 11:
                return "n"                   # blauer Pol
            return "S"
        return "."
    m = from_fn(fn)
    m[2] = "......BB........"[:16]
    m[2] = ".......BB......."
    return render(m, seed=7)


POCKET_CRAFTER = [
    "................",
    "................",
    "..gggggggggggg..",
    "..gjjjgjjgjjjg..",
    "..gjjjgjjgjjjg..",
    "..gggggggggggg..",
    "..gjjjgjjgjjjg..",
    "..eeeeeeeeeeee..",
    "..ffoofffffoff..",
    "..fofoffffooof..",
    "..llllllbbllll..",
    "..mmmmmmbbmmmm..",
    "..ffffffffffff..",
    "..feffffffffef..",
    "..eeeeeeeeeeee..",
    "................",
]


def pocket_crafter():
    return render(POCKET_CRAFTER, seed=21)


# =====================================================================
#  Blöcke
# =====================================================================

def stone_base(seed=3):
    """Vanilla-ähnlicher Stein: weiche Flecken in 4 Grautönen, kachelbar."""
    cols = [hx("#686868"), hx("#747474"), hx("#7F7F7F"), hx("#8C8C8C"), hx("#999999")]
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    for y in range(16):
        for x in range(16):
            v = 0.0
            for (f, w) in ((4, 0.55), (8, 0.3), (16, 0.15)):
                gx, gy = x * f / 16.0, y * f / 16.0
                x0, y0 = int(gx), int(gy)
                tx, ty = gx - x0, gy - y0
                def g(a, b):
                    return h(a % f, b % f, seed + f) / 65535.0
                top = g(x0, y0) * (1 - tx) + g(x0 + 1, y0) * tx
                bot = g(x0, y0 + 1) * (1 - tx) + g(x0 + 1, y0 + 1) * tx
                v += (top * (1 - ty) + bot * ty) * w
            v += (h(x, y, seed * 7) / 65535.0 - 0.5) * 0.25
            px[x, y] = cols[max(0, min(4, int(v * 5)))]
    return img


ORE_MASK = [
    "................",
    "..BB............",
    ".BBBB......B....",
    "..BBBB....BBB...",
    "...BB....BBBBB..",
    ".........BBBB...",
    "..........BB....",
    "......G.........",
    ".....GGG.....B..",
    "....GGGG....BBB.",
    ".....GG....BBBB.",
    "............BB..",
    "..B.............",
    ".BBB.....BBB....",
    "..BBB...BBBBB...",
    "...B.....BBB....",
]


def ore():
    base = stone_base()
    crystals = render(ORE_MASK, seed=11, outline=False)
    bp, cp = base.load(), crystals.load()
    m = ORE_MASK
    for y in range(16):
        for x in range(16):
            if cp[x, y][3]:
                bp[x, y] = cp[x, y]
            else:
                # dunkler Rand um Kristalle, wie bei Vanilla-Erzen
                for dx, dy in ((1, 0), (0, 1), (-1, 0), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < 16 and 0 <= ny < 16 and m[ny][nx] != ".":
                        bp[x, y] = hx("#3E4A55") if (dx, dy) in ((-1, 0), (0, -1)) else hx("#55606A")
                        break
    return base


def aetherite_block(B=None, G=None):
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    B, G = B or R["B"], G or R["G"]
    for y in range(16):
        for x in range(16):
            ring = min(x, y, 15 - x, 15 - y)
            if ring == 0:
                idx = 4 if (x == 0 or y == 0) and not (x == 15 or y == 15) else 1
                if (x, y) in ((0, 15), (15, 0)):
                    idx = 2
                c = B[idx]
            elif ring == 1:
                idx = 2 if (x == 1 or y == 1) and not (x == 14 or y == 14) else 4
                c = B[idx]
            else:
                # Pyramiden-Facetten wie ein geschliffener Stein
                dx, dy = x - 7.5, y - 7.5
                if abs(dx) + abs(dy) <= 3.0:
                    # Edelstein in der Mitte
                    gi = 5 if (dx < 0 and dy < 0 and abs(dx) + abs(dy) <= 1.5) else 4 if dx + dy < 0 else 2 if dx + dy > 1 else 3
                    c = G[gi]
                elif abs(dx) + abs(dy) <= 4.0:
                    c = G[1]
                else:
                    if dy <= -abs(dx):
                        idx = 4      # oben
                    elif dx <= -abs(dy):
                        idx = 3      # links
                    elif dx >= abs(dy):
                        idx = 2      # rechts
                    else:
                        idx = 2 if dy > 0 else 3
                        idx = 1 if dy > 3 else idx
                    if abs(abs(dx) - abs(dy)) < 0.6:
                        idx = 5 if dx < 0 and dy < 0 else min(idx + 1, 5)
                    if h(x, y, 5) % 17 == 0:
                        idx = 5
                    c = B[idx]
            px[x, y] = c
    return img


# =====================================================================
#  Rüstungs-Layer (64x32)
# =====================================================================

def armor_layers(B=None, G=None, M=None, K=None):
    B, G, M, K = B or R["B"], G or R["G"], M or R["M"], K or R["K"]
    l1 = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    l2 = Image.new("RGBA", (64, 32), (0, 0, 0, 0))

    def face(img, x0, y0, w, hh, rows=None, seam=4, seed=0, holes=(), extras=None):
        p = img.load()
        for v in range(hh):
            if rows and v not in rows:
                continue
            for u in range(w):
                if (u, v) in holes:
                    continue
                idx = 3
                if v == 0 or (rows and v == min(rows)):
                    idx = 4
                if u == 0:
                    idx = min(idx + 1, 5)
                if u == w - 1 or v == hh - 1 or (rows and v == max(rows)):
                    idx = 2
                if seam and v % seam == seam - 1 and 0 < v < hh - 1:
                    idx = 1
                if idx == 3 and (u + v) % 2 == 0 and h(x0 + u, y0 + v, seed) % 5 == 0:
                    idx = 4
                c = B[idx]
                if extras and (u, v) in extras:
                    c = extras[(u, v)]
                p[x0 + u, y0 + v] = c

    # ----- Layer 1: Helm -----
    face(l1, 8, 0, 8, 8, seam=0, seed=1)                                   # oben
    eye = {(u, v) for v in (3, 4) for u in (1, 2, 5, 6)}
    mouth = {(u, v) for v in (5, 6, 7) for u in (2, 3, 4, 5)}
    face(l1, 8, 8, 8, 8, seam=0, seed=2, holes=eye | mouth,
         extras={(3, 1): G[4], (4, 1): G[3], (3, 2): G[2], (4, 2): G[2]})   # vorne
    for x0 in (0, 16, 24):
        face(l1, x0, 8, 8, 8, seam=0, seed=x0)                              # Seiten/hinten
    # Goldband um den Helm
    p1 = l1.load()
    for x in range(0, 32):
        if p1[x, 8][3]:
            p1[x, 8] = M[4] if x % 8 else M[3]

    # ----- Layer 1: Brustpanzer -----
    face(l1, 20, 16, 8, 4, seam=0, seed=3)                                  # oben
    core = {(3, 3): G[5], (4, 3): G[4], (3, 4): G[4], (4, 4): G[3], (3, 5): G[2], (4, 5): G[2],
            (2, 4): G[1], (5, 4): G[1]}
    face(l1, 20, 20, 8, 12, seed=4, extras=core)                            # vorne
    face(l1, 32, 20, 8, 12, seed=5)                                         # hinten
    face(l1, 16, 20, 4, 12, seed=6)                                         # rechts
    face(l1, 28, 20, 4, 12, seed=7)                                         # links
    for x in range(16, 40):                                                 # Goldgürtel unten
        if p1[x, 30][3]:
            p1[x, 30] = M[3]
    # Schulterstücke
    face(l1, 44, 16, 4, 4, seam=0, seed=8)
    for x0 in (40, 44, 48, 52):
        face(l1, x0, 20, 4, 12, rows=range(0, 6), seam=0, seed=x0)
        p1[x0, 25] = K[1]
        for u in range(4):
            p1[x0 + u, 25] = K[2]

    # ----- Layer 1: Stiefel -----
    face(l1, 8, 16, 4, 4, seam=0, seed=9)                                   # Sohle
    for x0 in (0, 4, 8, 12):
        face(l1, x0, 20, 4, 12, rows=range(7, 12), seam=0, seed=x0 + 20)
        for u in range(4):
            p1[x0 + u, 27] = M[4]
            p1[x0 + u, 31] = K[1]

    # ----- Layer 2: Beinschutz -----
    p2 = l2.load()
    for (x0, w) in ((20, 8), (32, 8), (16, 4), (28, 4)):
        face(l2, x0, 20, w, 12, rows=range(8, 12), seam=0, seed=x0 + 40)
        for u in range(w):
            p2[x0 + u, 28] = M[3]
    p2[23, 28] = G[4]
    p2[24, 28] = G[3]
    face(l2, 4, 16, 4, 4, seam=0, seed=50)
    for x0 in (0, 4, 8, 12):
        face(l2, x0, 20, 4, 12, rows=range(0, 10), seam=5, seed=x0 + 60)
    for (u, v) in ((1, 4), (2, 4), (1, 5), (2, 5)):                         # Kniestück
        p2[4 + u, 20 + v] = G[3] if v == 4 else G[2]
    return l1, l2


# =====================================================================
#  Ausgabe
# =====================================================================

def main():
    items = {
        "aetherite_shard": render(from_fn(shard), seed=3),
        "aether_core": aether_core(),
        "aetherite_sword": render(from_fn(sword), seed=4),
        "aetherite_pickaxe": render(PICKAXE, seed=5),
        "aetherite_axe": render(AXE, seed=6),
        "aetherite_shovel": render(SHOVEL, seed=7),
        "aetherite_hoe": render(HOE, seed=8),
        "aetherite_hammer": render(from_fn(hammer), seed=9),
        "aetherite_excavator": render(from_fn(excavator), seed=10),
        "lumber_axe": render(LUMBER_AXE, seed=11),
        "aetherite_helmet": render(HELMET, seed=12),
        "aetherite_chestplate": render(CHESTPLATE, seed=13),
        "aetherite_leggings": render(LEGGINGS, seed=14),
        "aetherite_boots": render(BOOTS, seed=15),
        "magnet_ring": magnet_ring(),
        "ender_pouch": render(ENDER_POUCH, seed=16),
        "pocket_crafter": pocket_crafter(),
        "torch_staff": render(TORCH_STAFF, seed=17),
        "growth_staff": render(GROWTH_STAFF, seed=18),
        "recall_scroll": render(RECALL_SCROLL, seed=19),
        "trail_mix": render(TRAIL_MIX, seed=20),
    }
    for name, img in items.items():
        save(img, name)

    blocks = {"aetherite_ore": ore(), "aetherite_block": aetherite_block()}
    for name, img in blocks.items():
        save(img, name, "block")

    l1, l2 = armor_layers()
    l1.save(f"{OUT}/models/armor/aetherite_layer_1.png")
    l2.save(f"{OUT}/models/armor/aetherite_layer_2.png")

    # Vorschaublatt
    allimgs = list(items.items()) + list(blocks.items())
    cols, scale, pad = 6, 8, 16
    rows = math.ceil(len(allimgs) / cols)
    cell = 16 * scale + pad
    sheet = Image.new("RGBA", (cols * cell + pad, rows * cell + pad), hx("#2B2F36"))
    for i, (name, img) in enumerate(allimgs):
        cx, cy = pad + (i % cols) * cell, pad + (i // cols) * cell
        bg = Image.new("RGBA", (16 * scale, 16 * scale), hx("#8B8B8B"))
        sheet.alpha_composite(bg, (cx, cy))
        sheet.alpha_composite(img.resize((16 * scale, 16 * scale), Image.NEAREST), (cx, cy))
    sheet.save(os.path.join(BASE, "docs/preview_items.png"))
    arm = Image.new("RGBA", (64 * 8, 64 * 8 + 8), hx("#2B2F36"))
    arm.alpha_composite(l1.resize((512, 256), Image.NEAREST), (0, 0))
    arm.alpha_composite(l2.resize((512, 256), Image.NEAREST), (0, 264))
    arm.save(os.path.join(BASE, "docs/preview_armor.png"))
    print("Texturen erzeugt:", len(items), "Items,", len(blocks), "Blöcke, 2 Rüstungslayer")


if __name__ == "__main__":
    main()
    import gen_textures_ext
    gen_textures_ext.main()
