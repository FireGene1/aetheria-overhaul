#!/usr/bin/env python3
"""Erweiterung: Texturen für Emberit/Voidium, neue Items, Blöcke, Rüstungs-Layer und Mobs."""
import math, os
from PIL import Image
import gen_textures as g
from gen_textures import R, FIXED, hx, h, render, from_fn, save, OUT, BASE

os.makedirs(f"{OUT}/entity", exist_ok=True)

# ---------------------------------------------------------------- neue Rampen
R["EMB"] = g.ramp("#2A0A04", "#6B1A08", "#A8330F", "#E0631C", "#FFA347", "#FFE3A8")
R["EMK"] = g.ramp("#140805", "#33170D", "#4F2414", "#6B341D", "#8C4828", "#B0623A")
R["VOI"] = g.ramp("#0A0414", "#1E0B33", "#3A1666", "#6A2BB0", "#A65CF2", "#E7C8FF")
R["VOK"] = g.ramp("#050308", "#120C1C", "#22182F", "#332545", "#4A3863", "#6A5885")
R["BL"] = g.ramp("#4A2A00", "#A86A00", "#D99A0A", "#F5C431", "#FFE27A", "#FFF6C8")
R["Z"] = g.ramp("#2E0508", "#6E0F17", "#A81E2A", "#DB3A45", "#FF7A80", "#FFD3D6")
R["RM"] = g.ramp("#4A1414", "#8E2F2F", "#B84A4A", "#D96B6B", "#F09A9A", "#FFD0D0")
R["CM"] = g.ramp("#2E1607", "#5E2F12", "#87471F", "#A9652E", "#C98744", "#E5B070")
R["Y"] = g.ramp("#33454C", "#8FB3BD", "#B5D4DC", "#D3EAF0", "#ECF8FB", "#FFFFFF")
FIXED["s"] = ("#5C6B3A", "#2A1A0B")
FIXED["v"] = ("#C03BFF", "#2A0A3A")

EMBER = {"B": "EMB", "K": "EMK", "G": "M", "H": "BL"}
VOID = {"B": "VOI", "K": "VOK", "G": "B", "H": "VOK"}
EMBER_ARMOR = {"B": "EMB", "K": "EMK", "G": "M", "M": "EMK"}
VOID_ARMOR = {"B": "VOI", "K": "VOK", "G": "B"}


def outline_img(img, color):
    px = img.load()
    out = img.copy()
    op = out.load()
    W, H = img.size
    for y in range(H):
        for x in range(W):
            if px[x, y][3]:
                continue
            for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < W and 0 <= ny < H and px[nx, ny][3]:
                    op[x, y] = hx(color)
                    break
    return out


def mix(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (c[3],)


# =====================================================================
#  ITEM-MASKEN
# =====================================================================
CHUNK = [
    "................",
    "................",
    "................",
    "......KKK.......",
    "....KKBKKK......",
    "...KKBBBKKK.....",
    "...KKKBKKBKK....",
    "..KKBKKKBBBK....",
    "..KBBBKKKBKKK...",
    "..KKBKKKKKKBK...",
    "...KKKBBKKBBK...",
    "....KKKBKKKK....",
    ".....KKKKKK.....",
    "................",
    "................",
    "................",
]

INGOT = [
    "................",
    "................",
    "................",
    "................",
    "................",
    ".....BBBBBBBB...",
    "....BBBBBBBBBB..",
    "...FFFFFFFFFFFF.",
    "...FFFFFFFFFFFF.",
    "...FFFFFFFFFFFF.",
    "....FFFFFFFFFF..",
    "................",
    "................",
    "................",
    "................",
    "................",
]

WISP_ESSENCE = [
    "................",
    "................",
    ".......HH.......",
    ".......HH.......",
    "......YYYY......",
    ".......YY.......",
    "......YBBY......",
    ".....YBBBBY.....",
    "....YBBqBBBY....",
    "....YBBBBBBY....",
    "....YBBBBqBY....",
    "....YBBBBBBY....",
    ".....YBBBBY.....",
    "......YYYY......",
    "................",
    "................",
]

HEART = [
    "................",
    "................",
    "...GGG...GGG....",
    "..GGGGG.GGGGG...",
    ".GGGGGGGGGGGGG..",
    ".GGGBGGGGGBGGG..",
    ".GGGGBGGGBGGGG..",
    ".GGGGGBBBGGGGG..",
    "..GGGGGBGGGGG...",
    "...GGGGBGGGG....",
    "....GGGBGGG.....",
    ".....GGGGG......",
    "......GGG.......",
    ".......G........",
    "................",
    "................",
]

HEART_SMALL = [
    "................",
    "................",
    "................",
    "....ZZ...ZZ.....",
    "...ZZZZ.ZZZZ....",
    "..ZZZqZZZZZZZ...",
    "..ZZqZZZZZZZZ...",
    "..ZZZZZZZZZZZ...",
    "...ZZZZZZZZZ....",
    "....ZZZZZZZ.....",
    ".....ZZZZZ......",
    "......ZZZ.......",
    ".......Z........",
    "................",
    "................",
    "................",
]

BACKPACK = [
    "................",
    "......RRRR......",
    ".....R....R.....",
    "....LLLLLLLL....",
    "...LLFFFFFFLL...",
    "...LFFFFFFFFL...",
    "...LFFFMMFFFL...",
    "...LLLLMMLLLL...",
    "..RLLLLLLLLLLR..",
    "..RLLLLLLLLLLR..",
    "..RLLWWWWWWLLR..",
    "..RLLWWWWWWLLR..",
    "...LLLLLLLLLL...",
    "....LLLLLLLL....",
    "................",
    "................",
]

MEAT = [
    "................",
    "................",
    "................",
    ".....MMMMM......",
    "...MMMMMMMMM....",
    "..MMMMMMMMMMM...",
    "..MMMFFMMMMMMM..",
    ".MMMFFFFMMMMMM..",
    ".MMMMFFMMMMMMM..",
    ".MMMMMMMMMMMMT..",
    "..MMMMMMMMMMTTT.",
    "...MMMMMMMM.TTT.",
    ".....MMMM.......",
    "................",
    "................",
    "................",
]

STEW = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "....sBsqsBss....",
    "..WsssBsssqssW..",
    "..WWWWWWWWWWWW..",
    "...WWWWWWWWWW...",
    "....WWWWWWWW....",
    ".....WWWWWW.....",
    "................",
    "................",
    "................",
    "................",
]

LANTERN_ICON = [
    "................",
    ".......SS.......",
    "......S..S......",
    ".....KKKKKK.....",
    "....KKKKKKKK....",
    "....KBBBBBBK....",
    "....KBBqBBBK....",
    "....KBBBBBBK....",
    "....KBBBBqBK....",
    "....KBBBBBBK....",
    "....KKKKKKKK....",
    ".....KKKKKK.....",
    "................",
    "................",
    "................",
    "................",
]


def sigil(x, y):
    d = math.hypot(x - 7.5, y - 7.5)
    if d > 7.2:
        return "."
    if d > 5.6:
        return "M"
    if d > 4.8:
        return "K"
    ax, ay = abs(x - 7.5), abs(y - 7.5)
    if ax < 1 or ay < 1 or abs(ax - ay) < 0.6:
        return "n"
    return "B"


def warden_blade(x, y):
    s, d = x + y, x - y
    if 1 <= y <= 10 and 13 <= s <= 18 and x <= 14:
        return "K" if s in (13, 18) else "B"
    if d in (-6, -7) and 11 <= s <= 22:
        return "G" if s in (15, 16, 17) else "M"
    if -12 <= d <= -8 and s in (15, 16, 17):
        return "G" if d % 2 == 0 else "H"
    if -14 <= d <= -13 and s in (15, 16, 17):
        return "M"
    return "."


def bow(pull):
    def fn(x, y):
        s, d = x + y, x - y
        if abs(d) <= 12:
            limb = 15 - 5.0 * (1 - (d / 12.5) ** 2)
            if abs(s - limb) <= 0.75:
                if abs(d) >= 10:
                    return "B"
                if abs(d) <= 1:
                    return "M"
                return "H"
        if abs(d) <= 11:
            ss = 15 + pull * (1 - abs(d) / 11.0)
            if abs(s - ss) <= 0.75:
                return "T"
        if pull > 0 and d == 0:
            tail = int(15 + pull)
            if 5 <= s <= tail:
                return "q" if s >= tail - 2 else "H"
            if 2 <= s <= 4:
                return "S"
        if pull > 0 and abs(d) == 1 and s in (4, 5):
            return "S"
        return "."
    return fn


# =====================================================================
#  BLÖCKE
# =====================================================================

def noise_base(cols, seed):
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    for y in range(16):
        for x in range(16):
            v = 0.0
            for (f, w) in ((4, 0.55), (8, 0.3), (16, 0.15)):
                gx, gy = x * f / 16.0, y * f / 16.0
                x0, y0 = int(gx), int(gy)
                tx, ty = gx - x0, gy - y0
                gg = lambda a, b_: h(a % f, b_ % f, seed + f) / 65535.0
                top = gg(x0, y0) * (1 - tx) + gg(x0 + 1, y0) * tx
                bot = gg(x0, y0 + 1) * (1 - tx) + gg(x0 + 1, y0 + 1) * tx
                v += (top * (1 - ty) + bot * ty) * w
            v += (h(x, y, seed * 7) / 65535.0 - 0.5) * 0.3
            px[x, y] = hx(cols[max(0, min(len(cols) - 1, int(v * len(cols))))])
    return img


def ore_on(base, mask, remap, edge_dark, edge_light):
    crystals = render(mask, seed=23, outline=False, remap=remap)
    bp, cp = base.load(), crystals.load()
    for y in range(16):
        for x in range(16):
            if cp[x, y][3]:
                bp[x, y] = cp[x, y]
            else:
                for dx, dy in ((1, 0), (0, 1), (-1, 0), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < 16 and 0 <= ny < 16 and mask[ny][nx] != ".":
                        bp[x, y] = hx(edge_dark) if (dx, dy) in ((-1, 0), (0, -1)) else hx(edge_light)
                        break
    return base


def flip(mask):
    return [r[::-1] for r in mask]


def bricks():
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    K, B = R["K"], R["B"]
    for y in range(16):
        row = y // 4
        off = 4 if row % 2 else 0
        for x in range(16):
            lx = (x + off) % 8
            ly = y % 4
            if ly == 3 or lx == 7:
                c = K[1]
            else:
                idx = 3
                if ly == 0 or lx == 0:
                    idx = 4
                if ly == 2 or lx == 6:
                    idx = 2
                if h(x, y, 31) % 9 == 0:
                    idx = max(1, idx - 1)
                c = K[idx]
                if h(x, y, 77) % 41 == 0:
                    c = B[4]
            px[x, y] = c
    return img


def lamp():
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    K, B = R["K"], R["B"]
    for y in range(16):
        for x in range(16):
            ring = min(x, y, 15 - x, 15 - y)
            if ring == 0:
                c = K[1]
            elif ring == 1:
                c = K[3] if (x == 1 or y == 1) else K[2]
            else:
                d = math.hypot(x - 7.5, y - 7.5)
                c = B[5] if d < 2.5 else B[4] if d < 4.5 else B[3]
                if x in (7, 8) or y in (7, 8):
                    c = B[5] if d < 4 else B[4]
                if (x, y) in ((2, 2), (13, 2), (2, 13), (13, 13)):
                    c = R["M"][4]
            px[x, y] = c
    return img


def crystal_block():
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    B = R["B"]
    for y in range(16):
        for x in range(16):
            cx, cy = (x % 8) - 3.5, (y % 8) - 3.5
            if ((x // 8) + (y // 8)) % 2:
                cx, cy = -cy, cx
            dd = abs(cx) + abs(cy)
            if dd > 3.6:
                c = B[1]
            elif cx + cy < -1:
                c = B[5] if dd > 2.5 else B[4]
            elif cx + cy > 1:
                c = B[2]
            else:
                c = B[3]
            if h(x, y, 9) % 23 == 0:
                c = B[5]
            px[x, y] = c
    return img


def metal_plate(arrow_up=True, top=False):
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    S, B = R["S"], R["B"]
    for y in range(16):
        for x in range(16):
            ring = min(x, y, 15 - x, 15 - y)
            idx = 3
            if ring == 0:
                idx = 1
            elif ring == 1:
                idx = 4 if (x == 1 or y == 1) else 2
            if not top and ring >= 2 and y in (3, 12):
                idx = 2
            c = S[idx]
            if (x, y) in ((2, 2), (13, 2), (2, 13), (13, 13)):
                c = S[5]
            px[x, y] = c
    # Pfeile
    def arrow(cx, cy, up):
        for i in range(4):
            for dx in range(-i, i + 1):
                yy = cy + i if up else cy - i
                px[cx + dx, yy] = B[4] if dx <= 0 else B[3]
        for yy in range(cy + 4, cy + 7) if up else range(cy - 6, cy - 3):
            px[cx, yy] = B[3]
    if top:
        arrow(7, 3, True)
        arrow(7, 12, False)
    else:
        arrow(7, 4, True)
    return img


def altar(top):
    img = Image.new("RGBA", (16, 16))
    px = img.load()
    V, M, G = R["VOK"], R["M"], R["G"]
    for y in range(16):
        for x in range(16):
            idx = 3 if h(x, y, 5) % 3 else 2
            c = V[idx]
            if top:
                d = math.hypot(x - 7.5, y - 7.5)
                if 5.6 < d < 6.6:
                    c = M[4] if x < 8 else M[3]
                elif d < 2.2:
                    c = G[5] if d < 1 else G[4]
                elif d < 4.5 and (abs(x - 7.5) < 0.6 or abs(y - 7.5) < 0.6):
                    c = G[3]
                if min(x, y, 15 - x, 15 - y) == 0:
                    c = V[1]
            else:
                if y in (0, 1):
                    c = M[4] if y == 0 else M[2]
                elif y == 15:
                    c = V[1]
                elif (y - 2) % 5 == 4 or (x + (4 if ((y - 2) // 5) % 2 else 0)) % 8 == 7:
                    c = V[1]
                if 5 <= y <= 10 and x in (7, 8):
                    c = G[4] if y % 2 else G[3]
            px[x, y] = c
    return img


def waystone_tex(top):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    S, B = R["S"], R["B"]
    for y in range(16):
        for x in range(16):
            idx = 3 if h(x, y, 41) % 4 else 2
            c = S[idx]
            if top:
                d = math.hypot(x - 7.5, y - 7.5)
                if 3 < d < 4.2:
                    c = B[4]
                elif d < 1.5:
                    c = B[5]
                if x in (2, 13) or y in (2, 13):
                    c = S[1]
            else:
                if x in (2, 13):
                    c = S[1]
                elif x == 3:
                    c = S[4]
                if y in (0, 15):
                    c = S[1]
                elif y in (1, 14):
                    c = S[2]
                # Runen
                rune = {(7, 3), (8, 3), (7, 4), (6, 5), (9, 5), (7, 6), (8, 6), (7, 8), (8, 8), (6, 9), (7, 10),
                        (8, 10), (9, 9), (7, 12), (8, 12), (6, 11), (9, 11)}
                if (x, y) in rune:
                    c = B[5] if (x + y) % 3 else B[4]
            px[x, y] = c
    return img


def totem_tex(top):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    H_, F = R["H"], R["F"]
    for y in range(16):
        for x in range(16):
            if top:
                d = math.hypot(x - 7.5, y - 7.5)
                c = H_[4] if int(d) % 2 else H_[3]
                if d > 5:
                    c = H_[1]
                if h(x, y, 3) % 7 == 0 and d > 3.5:
                    c = F[3]
            else:
                idx = 3 if (x + h(0, y, 2)) % 3 else 2
                c = H_[idx]
                if x in (3, 12):
                    c = H_[1]
                if y <= 2:
                    c = F[4] if h(x, y, 8) % 2 else F[3]
                # Gesicht
                if (x, y) in ((5, 5), (6, 5), (9, 5), (10, 5)):
                    c = R["B"][5]
                if (x, y) in ((5, 4), (6, 4), (9, 4), (10, 4)):
                    c = H_[1]
                if y == 9 and 5 <= x <= 10:
                    c = H_[1]
                if y == 10 and x in (6, 9):
                    c = H_[1]
                if y == 13:
                    c = R["M"][3]
            px[x, y] = c
    return img


def lantern_tex():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    S, B = R["VOK"], R["B"]
    for y in range(2, 10):
        for x in range(0, 6):
            border = x in (0, 5) or y in (2, 9)
            px[x, y] = S[3] if border else (B[5] if (x + y) % 3 == 0 else B[4])
    for y in range(0, 2):
        for x in range(1, 5):
            px[x, y] = S[4] if y == 0 else S[2]
    for y in range(9, 15):
        for x in range(0, 6):
            px[x, y] = B[4] if (x in (2, 3) and y in (11, 12)) else S[2]
    px[11, 1] = S[4]
    px[12, 1] = S[3]
    return img


GLOWCAP = [
    "................",
    "................",
    "................",
    ".....BBBBB......",
    "....BBqBBBB.....",
    "...BBBBBBqBB....",
    "...BqBBBBBBB....",
    "....KKKKKKK.....",
    "......TT........",
    "......TT...BBB..",
    ".....TTT..BqBBB.",
    "......TT..KKKKK.",
    "......TT....T...",
    ".....TTTT...T...",
    "....TTTTTT.TTT..",
    "................",
]

CLUSTER = [
    "................",
    ".......B........",
    "......BB........",
    "......BB....B...",
    "..B...BBB..BB...",
    "..BB..BBB..BB...",
    "..BB.BBBB.BBB...",
    "..BBBBBBBBBB....",
    "...BBBBBBBBB....",
    "...BBBBBBBB.....",
    "....BBBBBBB.....",
    "....GBBBBBG.....",
    "...GGGBBBGGG....",
    "...KKKKKKKKK....",
    "................",
    "................",
]


# =====================================================================
#  MOB-TEXTUREN
# =====================================================================

def faces(u, v, w, hh, d):
    return {"top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d), "right": (u, v + d, d, hh),
            "front": (u + d, v + d, w, hh), "left": (u + d + w, v + d, d, hh), "back": (u + d + w + d, v + d, w, hh)}


def paint_box(img, u, v, w, hh, d, fn):
    px = img.load()
    for face, (fx, fy, fw, fh) in faces(u, v, w, hh, d).items():
        for y in range(fh):
            for x in range(fw):
                c = fn(face, x, y, fw, fh)
                if c is not None:
                    px[fx + x, fy + y] = c if len(c) == 4 else c + (255,)


def shaded(base, seed, amount=0.12):
    """Fläche mit leichter Körnung und Kanten-Schattierung."""
    def fn(face, x, y, fw, fh):
        c = hx(base)
        f = 1.0 + ((h(x + fw * 3, y, seed + len(face)) / 65535.0) - 0.5) * amount * 2
        if face in ("top",):
            f += 0.08
        if face in ("bottom",):
            f -= 0.15
        if y == fh - 1:
            f -= 0.08
        return mix(c, f)
    return fn


def ruin_guardian():
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    skin, steel, dark = "#6F8A86", "#46525C", "#2F3A44"
    cyan = hx("#7DE3DC")

    def head(face, x, y, fw, fh):
        if face == "top" or (face != "bottom" and y < 3):
            c = mix(hx(steel), 1.0 + (h(x, y, 1) % 9 - 4) * 0.02)
            if face == "front" and y == 2:
                c = mix(hx(steel), 0.75)
            if face == "top" and x in (3, 4):
                c = cyan
            return c
        c = mix(hx(skin), 1.0 + (h(x, y, 2) % 9 - 4) * 0.025)
        if face == "front":
            if y == 4 and x in (1, 2, 5, 6):
                return cyan if x in (2, 5) else hx("#1E2A28")
            if y == 6 and 2 <= x <= 5:
                return hx("#2A3532")
            if y == 3 and x in (1, 2, 5, 6):
                return mix(hx(skin), 0.7)
        return c

    def body(face, x, y, fw, fh):
        c = mix(hx(steel), 1.0 + (h(x, y, 3) % 9 - 4) * 0.02)
        if y == 10:
            return hx("#3A2E1C")
        if y == 11:
            return hx(dark)
        if face == "front":
            if 3 <= x <= 4 and 3 <= y <= 5:
                return hx("#3DB2C6") if y != 3 else cyan
            if x in (0, 7):
                return mix(hx(steel), 0.8)
            if (x, y) in ((1, 7), (2, 8), (6, 2), (5, 7)):
                return mix(cyan, 0.8)
        return c

    def arm(face, x, y, fw, fh):
        if y < 4 or face == "top":
            c = mix(hx(steel), 1.05 if y == 0 else 0.95)
            if y == 3:
                c = mix(hx(steel), 0.75)
            return c
        if y in (7, 8):
            return hx("#5B4A33")
        if face == "bottom":
            return mix(hx(skin), 0.85)
        return mix(hx(skin), 1.0 + (h(x, y, 4) % 7 - 3) * 0.03)

    def leg(face, x, y, fw, fh):
        if y >= 9 or face == "bottom":
            return mix(hx("#252B30"), 1.0 if y != 9 else 1.25)
        c = mix(hx(dark), 1.0 + (h(x, y, 5) % 7 - 3) * 0.03)
        if face == "front" and y in (4, 5):
            return hx(steel)
        return c

    paint_box(img, 0, 0, 8, 8, 8, head)
    paint_box(img, 16, 16, 8, 12, 4, body)
    paint_box(img, 40, 16, 4, 12, 4, arm)
    paint_box(img, 32, 48, 4, 12, 4, arm)
    paint_box(img, 0, 16, 4, 12, 4, leg)
    paint_box(img, 16, 48, 4, 12, 4, leg)
    return img


def crystal_skeleton():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    bone = "#D3E2E8"
    cyan, violet = R["B"], R["G"]

    def head(face, x, y, fw, fh):
        c = mix(hx(bone), 1.0 + (h(x, y, 6) % 9 - 4) * 0.025)
        if face == "top" and h(x, y, 7) % 4 == 0:
            return cyan[4] if (x + y) % 2 else cyan[3]
        if face == "front":
            if y in (3, 4) and x in (1, 2, 5, 6):
                return hx("#1A1622") if y == 3 or x in (1, 6) else violet[4]
            if y == 6 and 1 <= x <= 6:
                return hx("#2A3440") if x % 2 else c
            if y == 5 and x in (3, 4):
                return hx("#3A4650")
        return c

    def body(face, x, y, fw, fh):
        if face == "top":
            return cyan[4] if h(x, y, 9) % 3 == 0 else hx(bone)
        if face in ("front", "back"):
            if x in (3, 4):
                return mix(hx(bone), 0.9)
            if y % 2 == 1 and y < 9:
                return hx("#2A3440")
            if y >= 9:
                return hx("#2A3440") if y == 9 else mix(hx(bone), 0.85)
        return mix(hx(bone), 0.9 if y % 2 else 1.0)

    def limb(face, x, y, fw, fh):
        c = mix(hx(bone), 1.0 if x == 0 else 0.88)
        if y in (5, 6) and face in ("front", "left") and h(x, y, 2) % 2:
            return cyan[3]
        return c

    paint_box(img, 0, 0, 8, 8, 8, head)
    paint_box(img, 16, 16, 8, 12, 4, body)
    paint_box(img, 40, 16, 2, 12, 2, limb)
    paint_box(img, 0, 16, 2, 12, 2, limb)
    return img


def boar():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    fur, moss = "#6E4B27", R["F"]

    def head(face, x, y, fw, fh):
        c = mix(hx(fur), 1.0 + (h(x, y, 11) % 9 - 4) * 0.035)
        if face == "top":
            return moss[3] if h(x, y, 12) % 3 else moss[2]
        if face == "front":
            if y == 3 and x in (1, 6):
                return hx("#111111")
            if y == 3 and x in (2, 5):
                return hx("#EEEEEE")
            if y in (6, 7) and x in (1, 6):
                return hx("#F2EAD8")
        if y < 2:
            return moss[2]
        return c

    def snout(face, x, y, fw, fh):
        c = hx("#A9785A")
        if face == "front" and y == 1 and x in (1, 2):
            return hx("#3A2416")
        return c

    def body(face, x, y, fw, fh):
        c = mix(hx(fur), 1.0 + (h(x, y, 13) % 9 - 4) * 0.035)
        if face == "back":
            return moss[3] if h(x, y, 14) % 4 else moss[4]
        if face in ("right", "left"):
            edge = x if face == "left" else fw - 1 - x
            if edge <= 1:
                return moss[2] if h(x, y, 15) % 2 else moss[3]
        if face == "front":
            return mix(hx(fur), 0.85)
        return c

    def leg(face, x, y, fw, fh):
        if y >= 4 or face == "bottom":
            return hx("#2E1E10")
        return mix(hx(fur), 0.8)

    paint_box(img, 0, 0, 8, 8, 8, head)
    paint_box(img, 16, 16, 4, 3, 1, snout)
    paint_box(img, 28, 8, 10, 16, 8, body)
    paint_box(img, 0, 16, 4, 6, 4, leg)
    return img


def wisp():
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))

    def core(face, x, y, fw, fh):
        return hx("#F4FFFF") if (x + y) % 3 else hx("#BFFFF7")

    def shell(face, x, y, fw, fh):
        edge = x in (0, fw - 1) or y in (0, fh - 1)
        c = hx("#A8F5EE") if edge else hx("#7DE3DC")
        return c[:3] + (200 if edge else 130,)

    px = img.load()
    paint_box(img, 0, 0, 4, 4, 4, core)
    paint_box(img, 0, 8, 6, 6, 6, shell)
    for y in range(0, 8):
        for x in range(16, 26):
            vein = (x - 16) % 5 == 0 or y % 4 == 0
            c = hx("#E8FFFC") if vein else hx("#C4F7F1")
            px[x, y] = c[:3] + (210 if vein else 150,)
    return img


def void_stalker():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    base, v = "#150A20", R["VOI"]

    def dark(seed):
        def fn(face, x, y, fw, fh):
            c = mix(hx(base), 1.0 + (h(x, y, seed) % 9 - 4) * 0.06)
            if h(x, y, seed + 50) % 17 == 0:
                return v[3]
            return c
        return fn

    def head(face, x, y, fw, fh):
        if face == "front" and y == 4 and x in (1, 2, 5, 6):
            return hx("#E079FF") if x in (2, 5) else hx("#C03BFF")
        return dark(21)(face, x, y, fw, fh)

    def body(face, x, y, fw, fh):
        if face == "front" and x in (3, 4) and 2 <= y <= 9:
            return v[4] if y % 2 else v[3]
        return dark(22)(face, x, y, fw, fh)

    paint_box(img, 0, 0, 8, 8, 8, head)
    paint_box(img, 0, 16, 8, 8, 8, dark(23))
    paint_box(img, 32, 16, 8, 12, 4, body)
    paint_box(img, 56, 0, 2, 30, 2, lambda f, x, y, fw, fh: v[3] if y % 7 == 3 else dark(24)(f, x, y, fw, fh))
    return img


def warden():
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    steel, gold, cyan, gem = "#24405C", R["M"], R["B"], R["G"]

    def plate(seed):
        def fn(face, x, y, fw, fh):
            c = mix(hx(steel), 1.0 + (h(x, y, seed) % 9 - 4) * 0.03)
            if face == "top":
                c = mix(c, 1.15)
            if x == 0:
                c = mix(c, 1.12)
            if x == fw - 1:
                c = mix(c, 0.85)
            return c
        return fn

    def head(face, x, y, fw, fh):
        if face == "top" and x in (3, 4):
            return cyan[4] if y % 2 else cyan[5]
        if face == "front":
            if y == 4 and 1 <= x <= 6:
                return cyan[5] if x in (2, 5) else cyan[4]
            if y == 5 and 1 <= x <= 6:
                return hx("#0E1F3A")
            if x in (3, 4) and y >= 6:
                return gold[3]
        if y == 7 or y == 0:
            return gold[3] if face != "top" else plate(31)(face, x, y, fw, fh)
        return plate(31)(face, x, y, fw, fh)

    def body(face, x, y, fw, fh):
        if y == 10:
            return gold[4] if face == "front" and x in (3, 4) else gold[3]
        if face == "front":
            dd = abs(x - 3.5) + abs(y - 4.5)
            if dd <= 1.5:
                return gem[5] if dd < 1 else gem[4]
            if dd <= 2.5:
                return gold[3]
            if y == 0:
                return gold[2]
        return plate(32)(face, x, y, fw, fh)

    def arm(face, x, y, fw, fh):
        if face == "top":
            return cyan[4] if (x + y) % 2 else cyan[3]
        if y in (0, 1):
            return cyan[3] if face in ("front", "right", "left", "back") and y == 0 else plate(33)(face, x, y, fw, fh)
        if y in (5, 10):
            return gold[3]
        return plate(33)(face, x, y, fw, fh)

    def leg(face, x, y, fw, fh):
        if y in (0, 6):
            return gold[3]
        if y >= 10:
            return mix(hx(steel), 0.7)
        return plate(34)(face, x, y, fw, fh)

    paint_box(img, 0, 0, 8, 8, 8, head)
    paint_box(img, 16, 16, 8, 12, 4, body)
    paint_box(img, 40, 16, 4, 12, 4, arm)
    paint_box(img, 32, 48, 4, 12, 4, arm)
    paint_box(img, 0, 16, 4, 12, 4, leg)
    paint_box(img, 16, 48, 4, 12, 4, leg)
    return img


# =====================================================================
#  AUSGABE
# =====================================================================

def main():
    items = {}
    # Werkzeuge in drei Materialien
    tool_masks = {"sword": from_fn(g.sword), "pickaxe": g.PICKAXE, "axe": g.AXE, "shovel": g.SHOVEL, "hoe": g.HOE}
    for mat, remap in (("emberite", EMBER), ("voidium", VOID)):
        for t, mask in tool_masks.items():
            items[f"{mat}_{t}"] = render(mask, seed=40 + len(t), remap=remap)
    armor_masks = {"helmet": g.HELMET, "chestplate": g.CHESTPLATE, "leggings": g.LEGGINGS, "boots": g.BOOTS}
    for mat, remap in (("emberite", EMBER_ARMOR), ("voidium", VOID_ARMOR)):
        for t, mask in armor_masks.items():
            items[f"{mat}_{t}"] = render(mask, seed=50 + len(t), remap=remap)

    items["emberite_chunk"] = render(CHUNK, seed=61, remap={"K": "EMK", "B": "EMB"})
    items["emberite_ingot"] = render(INGOT, seed=62, remap={"B": "EMB", "F": "EMB"})
    items["voidium_ingot"] = render(INGOT, seed=63, remap={"B": "VOI", "F": "VOI"})
    items["void_shard"] = render(from_fn(g.shard), seed=64, remap={"B": "VOI", "K": "VOK", "G": "B"})
    items["wisp_essence"] = render(WISP_ESSENCE, seed=65)
    items["warden_heart"] = render(HEART, seed=66)
    items["heart_crystal"] = render(HEART_SMALL, seed=67)
    items["warden_sigil"] = render(from_fn(sigil), seed=68)
    items["warden_blade"] = render(from_fn(warden_blade), seed=69, remap={"H": "VOK"})
    items["backpack"] = render(BACKPACK, seed=70, remap={"F": "W"})
    items["large_backpack"] = render(BACKPACK, seed=71, remap={"F": "K", "M": "B", "L": "L"})
    items["raw_boar"] = render(MEAT, seed=72, remap={"M": "RM", "F": "T"})
    items["cooked_boar"] = render(MEAT, seed=73, remap={"M": "CM", "F": "BL"})
    items["glowcap_stew"] = render(STEW, seed=74)
    items["warding_lantern"] = render(LANTERN_ICON, seed=75, remap={"K": "VOK"})
    items["aether_bow"] = render(from_fn(bow(0)), seed=76)
    for i, p in enumerate((1, 2, 3)):
        items[f"aether_bow_pulling_{i}"] = render(from_fn(bow(p)), seed=77)
    for name, img in items.items():
        save(img, name)

    blocks = {
        "ember_ore": ore_on(noise_base(["#4A1010", "#5E1616", "#6F1D1D", "#82262A", "#933232"], 81), flip(g.ORE_MASK),
                            {"B": "EMB", "G": "M"}, "#2A0808", "#3A0E0E"),
        "void_ore": ore_on(noise_base(["#B9B47E", "#C9C58F", "#D6D39E", "#DEDBA8", "#E7E4B5"], 82), g.ORE_MASK[::-1],
                           {"B": "VOI", "G": "B"}, "#6E6A48", "#8A8660"),
        "emberite_block": g.aetherite_block(R["EMB"], R["M"]),
        "voidium_block": g.aetherite_block(R["VOI"], R["B"]),
        "aether_bricks": bricks(),
        "aether_lamp": lamp(),
        "aether_crystal_block": crystal_block(),
        "elevator_top": metal_plate(top=True),
        "elevator_side": metal_plate(),
        "warden_altar_top": altar(True),
        "warden_altar_side": altar(False),
        "glowcap": render(GLOWCAP, seed=83),
        "aether_crystal_cluster": render(CLUSTER, seed=84),
        "waystone_side": waystone_tex(False),
        "waystone_top": waystone_tex(True),
        "growth_totem_side": totem_tex(False),
        "growth_totem_top": totem_tex(True),
        "warding_lantern": lantern_tex(),
    }
    for name, img in blocks.items():
        save(img, name, "block")

    for mat, args in (("emberite", (R["EMB"], R["M"], R["EMK"], R["EMK"])),
                      ("voidium", (R["VOI"], R["B"], R["M"], R["VOK"]))):
        l1, l2 = g.armor_layers(*args)
        l1.save(f"{OUT}/models/armor/{mat}_layer_1.png")
        l2.save(f"{OUT}/models/armor/{mat}_layer_2.png")

    mobs = {"ruin_guardian": ruin_guardian(), "crystal_skeleton": crystal_skeleton(), "mossback_boar": boar(),
            "wisp": wisp(), "void_stalker": void_stalker(), "aether_warden": warden()}
    for name, img in mobs.items():
        img.save(f"{OUT}/entity/{name}.png")

    # Vorschau
    allimgs = list(items.items()) + list(blocks.items())
    cols, scale, pad = 9, 6, 10
    rows = math.ceil(len(allimgs) / cols)
    cell = 16 * scale + pad
    sheet = Image.new("RGBA", (cols * cell + pad, rows * cell + pad), hx("#2B2F36"))
    for i, (name, img) in enumerate(allimgs):
        cx, cy = pad + (i % cols) * cell, pad + (i // cols) * cell
        sheet.alpha_composite(Image.new("RGBA", (16 * scale, 16 * scale), hx("#8B8B8B")), (cx, cy))
        sheet.alpha_composite(img.resize((16 * scale, 16 * scale), Image.NEAREST), (cx, cy))
    sheet.save(os.path.join(BASE, "docs/preview_new.png"))
    msheet = Image.new("RGBA", (64 * 6 * 3 + 40, 64 * 6 + 20), hx("#2B2F36"))
    x = 10
    for name, img in mobs.items():
        sc = 6 if img.size[0] == 32 else 3
        big = img.resize((img.size[0] * sc, img.size[1] * sc), Image.NEAREST)
        if x + big.size[0] > msheet.size[0]:
            break
        msheet.alpha_composite(big, (x, 10))
        x += big.size[0] + 6
    msheet.save(os.path.join(BASE, "docs/preview_mobs.png"))
    print("Erweiterung:", len(items), "Items,", len(blocks), "Blocktexturen,", len(mobs), "Mobs")


if __name__ == "__main__":
    main()
