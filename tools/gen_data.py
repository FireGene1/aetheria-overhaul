#!/usr/bin/env python3
"""Erzeugt alle JSON-Ressourcen (Modelle, Blockstates, Sprachen, Rezepte, Loot, Tags, Loot-Modifier)."""
import json, os, shutil

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src/main/resources")
M = "aetherkit"
A = f"{ROOT}/assets/{M}"
D = f"{ROOT}/data"

# Alte generierte Daten entfernen (Texturen bleiben)
for sub in (f"{A}/models", f"{A}/blockstates", f"{A}/lang", f"{D}"):
    shutil.rmtree(sub, ignore_errors=True)


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)
        f.write("\n")


def b(name):
    return f"{M}:block/{name}"


# =====================================================================
#  ITEM-MODELLE
# =====================================================================
generated = ["aetherite_shard", "emberite_chunk", "emberite_ingot", "void_shard", "voidium_ingot", "aether_core",
             "wisp_essence", "warden_heart", "warden_sigil", "heart_crystal",
             "magnet_ring", "ender_pouch", "pocket_crafter", "backpack", "large_backpack", "recall_scroll",
             "trail_mix", "raw_boar", "cooked_boar", "glowcap_stew"]
for mat in ("aetherite", "emberite", "voidium"):
    generated += [f"{mat}_helmet", f"{mat}_chestplate", f"{mat}_leggings", f"{mat}_boots"]
handheld = ["aetherite_hammer", "aetherite_excavator", "lumber_axe", "torch_staff", "growth_staff", "warden_blade"]
for mat in ("aetherite", "emberite", "voidium"):
    handheld += [f"{mat}_sword", f"{mat}_pickaxe", f"{mat}_axe", f"{mat}_shovel", f"{mat}_hoe"]

for i in generated:
    write(f"{A}/models/item/{i}.json", {"parent": "item/generated", "textures": {"layer0": f"{M}:item/{i}"}})
for i in handheld:
    write(f"{A}/models/item/{i}.json", {"parent": "item/handheld", "textures": {"layer0": f"{M}:item/{i}"}})

# Bogen mit Spannstufen
write(f"{A}/models/item/aether_bow.json", {
    "parent": "item/bow", "textures": {"layer0": f"{M}:item/aether_bow"},
    "overrides": [
        {"predicate": {"pulling": 1}, "model": f"{M}:item/aether_bow_pulling_0"},
        {"predicate": {"pulling": 1, "pull": 0.65}, "model": f"{M}:item/aether_bow_pulling_1"},
        {"predicate": {"pulling": 1, "pull": 0.9}, "model": f"{M}:item/aether_bow_pulling_2"}]})
for n in range(3):
    write(f"{A}/models/item/aether_bow_pulling_{n}.json",
          {"parent": "item/bow", "textures": {"layer0": f"{M}:item/aether_bow_pulling_{n}"}})

for egg in ("ruin_guardian", "crystal_skeleton", "mossback_boar", "wisp", "void_stalker", "aether_warden"):
    write(f"{A}/models/item/{egg}_spawn_egg.json", {"parent": "item/template_spawn_egg"})

# =====================================================================
#  BLOCK-MODELLE & BLOCKSTATES
# =====================================================================
def simple_block(name, model=None):
    if model is None:
        model = {"parent": "block/cube_all", "textures": {"all": b(name)}}
    write(f"{A}/models/block/{name}.json", model)
    write(f"{A}/models/item/{name}.json", {"parent": b(name)})
    write(f"{A}/blockstates/{name}.json", {"variants": {"": {"model": b(name)}}})


for n in ("aetherite_ore", "ember_ore", "void_ore", "aetherite_block", "emberite_block", "voidium_block",
          "aether_bricks", "aether_lamp", "aether_crystal_block"):
    simple_block(n)

simple_block("elevator", {"parent": "block/cube_column",
                          "textures": {"end": b("elevator_top"), "side": b("elevator_side")}})
simple_block("warden_altar", {"parent": "block/cube_bottom_top",
                              "textures": {"top": b("warden_altar_top"), "side": b("warden_altar_side"),
                                           "bottom": "minecraft:block/polished_blackstone"}})

# Kreuz-Modelle (Pflanzen/Kristalle)
for n in ("glowcap", "aether_crystal_cluster"):
    write(f"{A}/models/block/{n}.json", {"parent": "block/cross", "textures": {"cross": b(n)}})
    write(f"{A}/models/item/{n}.json", {"parent": "item/generated", "textures": {"layer0": b(n)}})
    write(f"{A}/blockstates/{n}.json", {"variants": {"": {"model": b(n)}}})


def box(frm, to, tex, faces=("north", "south", "east", "west", "up", "down"), uv=None):
    f = {}
    for face in faces:
        e = {"texture": tex}
        if uv:
            e["uv"] = uv[face] if isinstance(uv, dict) else uv
        f[face] = e
    return {"from": frm, "to": to, "faces": f}


# Säulenartige Blöcke mit eigener Form
def column_model(name, x0, x1, h, side, top):
    return {"parent": "block/block", "textures": {"particle": side, "side": side, "top": top},
            "elements": [{"from": [x0, 0, x0], "to": [x1, h, x1], "faces": {
                "north": {"uv": [x0, 16 - h, x1, 16], "texture": "#side"},
                "south": {"uv": [x0, 16 - h, x1, 16], "texture": "#side"},
                "east": {"uv": [x0, 16 - h, x1, 16], "texture": "#side"},
                "west": {"uv": [x0, 16 - h, x1, 16], "texture": "#side"},
                "up": {"uv": [x0, x0, x1, x1], "texture": "#top"},
                "down": {"uv": [x0, x0, x1, x1], "texture": "#top", "cullface": "down"}}}]}


simple_block("waystone", column_model("waystone", 2, 14, 16, b("waystone_side"), b("waystone_top")))
simple_block("growth_totem", column_model("growth_totem", 3, 13, 16, b("growth_totem_side"), b("growth_totem_top")))

lantern = {"parent": "block/block", "ambientocclusion": False,
           "textures": {"particle": b("warding_lantern"), "all": b("warding_lantern")},
           "elements": [
               {"from": [5, 0, 5], "to": [11, 8, 11], "faces": {
                   d: {"uv": [0, 2, 6, 10], "texture": "#all"} for d in ("north", "south", "east", "west")} | {
                   "up": {"uv": [0, 9, 6, 15], "texture": "#all"}, "down": {"uv": [0, 9, 6, 15], "texture": "#all"}}},
               {"from": [6, 8, 6], "to": [10, 10, 10], "faces": {
                   d: {"uv": [1, 0, 5, 2], "texture": "#all"} for d in ("north", "south", "east", "west")} | {
                   "up": {"uv": [1, 10, 5, 14], "texture": "#all"}}},
               {"from": [7, 10, 7], "to": [9, 11, 9], "faces": {
                   d: {"uv": [11, 1, 13, 2], "texture": "#all"} for d in ("north", "south", "east", "west", "up")}}]}
simple_block("warding_lantern", lantern)
write(f"{A}/models/item/warding_lantern.json", {"parent": "item/generated", "textures": {"layer0": f"{M}:item/warding_lantern"}})

# Grab (nutzt Vanilla-Texturen)
grave = {"parent": "block/block", "textures": {"particle": "minecraft:block/stone_bricks",
                                                "base": "minecraft:block/mossy_stone_bricks",
                                                "stone": "minecraft:block/polished_andesite",
                                                "top": "minecraft:block/andesite"},
         "elements": [
             box([1, 0, 1], [15, 2, 15], "#base"),
             box([3, 2, 6], [13, 12, 10], "#stone"),
             box([4, 12, 6], [12, 14, 10], "#top"),
             box([7, 5, 5.5], [9, 11, 6], "#top", faces=("north",)),
             box([5, 8, 5.5], [11, 9.5, 6], "#top", faces=("north",))]}
write(f"{A}/models/block/grave.json", grave)
write(f"{A}/blockstates/grave.json", {"variants": {
    "facing=north": {"model": b("grave")}, "facing=east": {"model": b("grave"), "y": 90},
    "facing=south": {"model": b("grave"), "y": 180}, "facing=west": {"model": b("grave"), "y": 270}}})

# Treppen & Stufen
tex = {"bottom": b("aether_bricks"), "top": b("aether_bricks"), "side": b("aether_bricks")}
write(f"{A}/models/block/aether_brick_stairs.json", {"parent": "block/stairs", "textures": tex})
write(f"{A}/models/block/aether_brick_stairs_inner.json", {"parent": "block/inner_stairs", "textures": tex})
write(f"{A}/models/block/aether_brick_stairs_outer.json", {"parent": "block/outer_stairs", "textures": tex})
write(f"{A}/models/item/aether_brick_stairs.json", {"parent": b("aether_brick_stairs")})
variants = {}
yrot_base = {"east": 0, "south": 90, "west": 180, "north": 270}
for facing, base in yrot_base.items():
    for half in ("bottom", "top"):
        for shape in ("straight", "inner_left", "inner_right", "outer_left", "outer_right"):
            y = base
            if shape in ("inner_left", "outer_left"):
                y += 270
            if shape != "straight" and half == "top":
                y += 90
            y %= 360
            model = b("aether_brick_stairs") + ("" if shape == "straight" else "_inner" if "inner" in shape else "_outer")
            v = {"model": model}
            if half == "top":
                v["x"] = 180
            if y:
                v["y"] = y
            if y or half == "top":
                v["uvlock"] = True
            variants[f"facing={facing},half={half},shape={shape}"] = v
write(f"{A}/blockstates/aether_brick_stairs.json", {"variants": variants})

write(f"{A}/models/block/aether_brick_slab.json", {"parent": "block/slab", "textures": tex})
write(f"{A}/models/block/aether_brick_slab_top.json", {"parent": "block/slab_top", "textures": tex})
write(f"{A}/models/item/aether_brick_slab.json", {"parent": b("aether_brick_slab")})
write(f"{A}/blockstates/aether_brick_slab.json", {"variants": {
    "type=bottom": {"model": b("aether_brick_slab")}, "type=top": {"model": b("aether_brick_slab_top")},
    "type=double": {"model": b("aether_bricks")}}})

# =====================================================================
#  SPRACHEN
# =====================================================================
names = {
    # Materialien
    "aetherite_shard": ("Aetherite Shard", "Aetherit-Splitter"),
    "emberite_chunk": ("Emberite Chunk", "Emberit-Brocken"),
    "emberite_ingot": ("Emberite Ingot", "Emberitbarren"),
    "void_shard": ("Void Shard", "Leerensplitter"),
    "voidium_ingot": ("Voidium Ingot", "Voidiumbarren"),
    "aether_core": ("Aether Core", "Aetherkern"),
    "wisp_essence": ("Wisp Essence", "Irrlicht-Essenz"),
    "warden_heart": ("Warden's Heart", "Wächterherz"),
    "warden_sigil": ("Warden Sigil", "Wächter-Siegel"),
    "heart_crystal": ("Heart Crystal", "Herzkristall"),
    # Werkzeuge
    "aetherite_hammer": ("Aetherite Hammer", "Aetherithammer"),
    "aetherite_excavator": ("Aetherite Excavator", "Aetheritbagger"),
    "lumber_axe": ("Lumberjack's Axe", "Holzfälleraxt"),
    "aether_bow": ("Aether Bow", "Aetherbogen"),
    "warden_blade": ("Blade of the Warden", "Klinge des Wächters"),
    # Utility
    "magnet_ring": ("Magnet Ring", "Magnetring"),
    "ender_pouch": ("Ender Pouch", "Enderbeutel"),
    "pocket_crafter": ("Pocket Crafter", "Taschenwerkbank"),
    "backpack": ("Backpack", "Rucksack"),
    "large_backpack": ("Large Backpack", "Großer Rucksack"),
    "torch_staff": ("Torch Staff", "Fackelstab"),
    "growth_staff": ("Staff of Growth", "Wachstumsstab"),
    "recall_scroll": ("Recall Scroll", "Rückkehrschriftrolle"),
    # Essen
    "trail_mix": ("Trail Mix", "Studentenfutter"),
    "raw_boar": ("Raw Boar", "Rohes Eberfleisch"),
    "cooked_boar": ("Roasted Boar", "Gebratenes Eberfleisch"),
    "glowcap_stew": ("Glowcap Stew", "Leuchtpilzeintopf"),
}
mat_names = {"aetherite": ("Aetherite", "Aetherit"), "emberite": ("Emberite", "Emberit"), "voidium": ("Voidium", "Voidium")}
part_names = {"sword": ("Sword", "schwert"), "pickaxe": ("Pickaxe", "spitzhacke"), "axe": ("Axe", "axt"),
              "shovel": ("Shovel", "schaufel"), "hoe": ("Hoe", "hacke"), "helmet": ("Helmet", "helm"),
              "chestplate": ("Chestplate", "brustpanzer"), "leggings": ("Leggings", "beinschutz"), "boots": ("Boots", "stiefel")}
for mk, (me, mg) in mat_names.items():
    for pk, (pe, pg) in part_names.items():
        names[f"{mk}_{pk}"] = (f"{me} {pe}", f"{mg}{pg}")

block_names = {
    "aetherite_ore": ("Aetherite Ore", "Aetheriterz"), "ember_ore": ("Ember Ore", "Glut-Erz"),
    "void_ore": ("Void Ore", "Leerenerz"), "aetherite_block": ("Block of Aetherite", "Aetheritblock"),
    "emberite_block": ("Block of Emberite", "Emberitblock"), "voidium_block": ("Block of Voidium", "Voidiumblock"),
    "aether_bricks": ("Aether Bricks", "Aetherziegel"), "aether_brick_stairs": ("Aether Brick Stairs", "Aetherziegeltreppe"),
    "aether_brick_slab": ("Aether Brick Slab", "Aetherziegelstufe"), "aether_lamp": ("Aether Lamp", "Aetherlampe"),
    "aether_crystal_block": ("Aether Crystal Block", "Aetherkristallblock"),
    "aether_crystal_cluster": ("Aether Crystal Cluster", "Aetherkristallgruppe"), "glowcap": ("Glowcap", "Leuchtpilz"),
    "elevator": ("Elevator", "Aufzug"), "warding_lantern": ("Warding Lantern", "Schutzlaterne"),
    "growth_totem": ("Growth Totem", "Wachstumstotem"), "waystone": ("Waystone", "Wegstein"),
    "grave": ("Grave", "Grab"), "warden_altar": ("Warden Altar", "Wächter-Altar"),
}
entity_names = {
    "ruin_guardian": ("Ruin Guardian", "Ruinenwächter"), "crystal_skeleton": ("Crystal Skeleton", "Kristallskelett"),
    "mossback_boar": ("Mossback Boar", "Moosrücken-Eber"), "wisp": ("Wisp", "Irrlicht"),
    "void_stalker": ("Void Stalker", "Leerenpirscher"), "aether_warden": ("Aether Warden", "Aetherwächter"),
}
extra = {
    "itemGroup.aetherkit": ("Aetheria Overhaul", "Aetheria Overhaul"),
    "key.categories.aetherkit": ("Aetheria", "Aetheria"),
    "key.aetherkit.sort": ("Sort inventory / chest", "Inventar / Truhe sortieren"),
    "gui.aetherkit.waystones": ("Waystone Network", "Wegstein-Netzwerk"),
    "gui.aetherkit.waystones_empty": ("Discover more waystones to travel between them!", "Entdecke weitere Wegsteine, um zwischen ihnen zu reisen!"),
    "hud.aetherkit.day": ("Day %s", "Tag %s"),
    # Tooltips
    "tooltip.aetherkit.aether_core.0": ("Pulses with condensed aether energy", "Pulsiert vor verdichteter Aetherenergie"),
    "tooltip.aetherkit.wisp_essence.0": ("Dropped by Wisps in dark forests", "Droppt von Irrlichtern in dunklen Wäldern"),
    "tooltip.aetherkit.warden_heart.0": ("Still beating... dropped by the Aether Warden", "Schlägt noch... Beute des Aetherwächters"),
    "tooltip.aetherkit.warden_sigil.0": ("Use on a Warden Altar to summon the boss", "Auf einem Wächter-Altar benutzen, um den Boss zu beschwören"),
    "tooltip.aetherkit.heart_crystal.0": ("Use: +1 max heart permanently (up to +10)", "Benutzen: dauerhaft +1 Herz (bis +10)"),
    "tooltip.aetherkit.trail_mix.0": ("Quick snack – grants a short Speed boost", "Schneller Snack – gibt kurz Tempo"),
    "tooltip.aetherkit.glowcap_stew.0": ("Night Vision for 3 minutes", "3 Minuten Nachtsicht"),
    "tooltip.aetherkit.hammer.0": ("Mines a 3x3 area", "Baut eine 3x3-Fläche ab"),
    "tooltip.aetherkit.excavator.0": ("Digs a 3x3 area of dirt, sand, gravel...", "Gräbt 3x3 Erde, Sand, Kies, ..."),
    "tooltip.aetherkit.lumber_axe.0": ("Fells the whole tree at once", "Fällt den ganzen Baum auf einmal"),
    "tooltip.aetherkit.lumber_axe.1": ("Ignores wooden builds without natural leaves", "Lässt Holzbauten ohne natürliches Laub in Ruhe"),
    "tooltip.aetherkit.sneak_single": ("Sneak to break a single block", "Schleichen: nur einen Block abbauen"),
    "tooltip.aetherkit.ember_sword.0": ("Sets enemies on fire", "Setzt Gegner in Brand"),
    "tooltip.aetherkit.ember_pickaxe.0": ("Smelts mined blocks instantly", "Schmilzt abgebaute Blöcke sofort"),
    "tooltip.aetherkit.void_sword.0": ("Heals you with every hit", "Heilt dich bei jedem Treffer"),
    "tooltip.aetherkit.warden_blade.0": ("Right-click: shockwave hits all nearby enemies", "Rechtsklick: Schockwelle trifft alle Gegner in der Nähe"),
    "tooltip.aetherkit.warden_blade.1": ("Forged from the Warden's heart", "Geschmiedet aus dem Herz des Wächters"),
    "tooltip.aetherkit.aether_bow.0": ("+60% arrow damage, marks targets", "+60 % Pfeilschaden, markiert Ziele"),
    "tooltip.aetherkit.aetherite_boots.0": ("Halves fall damage", "Halbiert Fallschaden"),
    "tooltip.aetherkit.armor_set_aetherite": ("Full set: Haste I", "Voller Satz: Eile I"),
    "tooltip.aetherkit.armor_set_emberite": ("Full set: Fire Resistance + Strength", "Voller Satz: Feuerresistenz + Stärke"),
    "tooltip.aetherkit.armor_set_voidium": ("Full set: Flight, Resistance, Haste II – boots: no fall damage", "Voller Satz: Fliegen, Resistenz, Eile II – Stiefel: kein Fallschaden"),
    "tooltip.aetherkit.state_on": ("Active", "Aktiv"),
    "tooltip.aetherkit.state_off": ("Inactive", "Inaktiv"),
    "tooltip.aetherkit.magnet_ring.0": ("Pulls nearby items and XP while in your inventory", "Zieht Items und XP an, solange er im Inventar ist"),
    "tooltip.aetherkit.magnet_ring.1": ("Right-click: toggle  •  Sneak: pause", "Rechtsklick: an/aus  •  Schleichen: Pause"),
    "tooltip.aetherkit.ender_pouch.0": ("Opens your Ender Chest anywhere", "Öffnet deine Endertruhe überall"),
    "tooltip.aetherkit.pocket_crafter.0": ("A crafting table that fits in your pocket", "Eine Werkbank für die Hosentasche"),
    "tooltip.aetherkit.backpack.0": ("27 extra slots – right-click to open", "27 Extra-Plätze – Rechtsklick zum Öffnen"),
    "tooltip.aetherkit.large_backpack.0": ("54 extra slots – right-click to open", "54 Extra-Plätze – Rechtsklick zum Öffnen"),
    "tooltip.aetherkit.torch_staff.0": ("Right-click a block: place a torch", "Rechtsklick auf Block: Fackel setzen"),
    "tooltip.aetherkit.torch_staff.1": ("Sneak + right-click: light up dark spots nearby", "Schleichen + Rechtsklick: dunkle Stellen beleuchten"),
    "tooltip.aetherkit.growth_staff.0": ("Bonemeals a 5x5 area – no bone meal needed", "Düngt eine 5x5-Fläche – ohne Knochenmehl"),
    "tooltip.aetherkit.recall_scroll.0": ("Read to return to your bed or respawn anchor", "Lesen, um zum Bett/Seelenanker zurückzukehren"),
    "tooltip.aetherkit.recall_scroll.1": ("Works across dimensions", "Funktioniert über Dimensionen hinweg"),
    "tooltip.aetherkit.elevator.0": ("Jump to go up, sneak to go down", "Springen = hoch, Schleichen = runter"),
    "tooltip.aetherkit.warding_lantern.0": ("Stops monsters from spawning nearby", "Verhindert das Spawnen von Monstern in der Nähe"),
    "tooltip.aetherkit.growth_totem.0": ("Speeds up plant growth around it", "Lässt Pflanzen in der Umgebung schneller wachsen"),
    "tooltip.aetherkit.waystone.0": ("Discover and fast-travel between waystones", "Wegsteine entdecken und zwischen ihnen reisen"),
    "tooltip.aetherkit.food": ("Hunger: %s  |  Saturation: %s", "Hunger: %s  |  Sättigung: %s"),
    # Nachrichten
    "message.aetherkit.magnet_on": ("Magnet activated", "Magnet aktiviert"),
    "message.aetherkit.magnet_off": ("Magnet deactivated", "Magnet deaktiviert"),
    "message.aetherkit.torches_placed": ("Placed %s torches", "%s Fackeln gesetzt"),
    "message.aetherkit.recall_no_bed": ("No bed found – returning to world spawn", "Kein Bett gefunden – zurück zum Weltspawn"),
    "message.aetherkit.durability_low": ("%s is almost broken! (%s uses left)", "%s ist fast kaputt! (noch %s Nutzungen)"),
    "message.aetherkit.grave_created": ("Your items are safe in a grave at %s, %s, %s", "Deine Items liegen sicher in einem Grab bei %s, %s, %s"),
    "message.aetherkit.grave_not_yours": ("This is %s's grave", "Das ist das Grab von %s"),
    "message.aetherkit.grave_recovered": ("Items recovered!", "Items zurückgeholt!"),
    "message.aetherkit.waystone_discovered": ("Waystone discovered: %s", "Wegstein entdeckt: %s"),
    "message.aetherkit.waystone_gone": ("The waystone %s no longer exists", "Der Wegstein %s existiert nicht mehr"),
    "message.aetherkit.altar_hint": ("The altar hungers for a Warden Sigil...", "Der Altar verlangt nach einem Wächter-Siegel..."),
    "message.aetherkit.warden_awakens": ("The Aether Warden awakens!", "Der Aetherwächter erwacht!"),
    "message.aetherkit.hearts_max": ("You already have the maximum bonus hearts", "Du hast bereits die maximalen Bonusherzen"),
    "message.aetherkit.hearts_up": ("Max health increased! (%s/%s bonus hearts)", "Maximale Gesundheit erhöht! (%s/%s Bonusherzen)"),
}
en, de = {}, {}
for k, (e, g) in names.items():
    en[f"item.{M}.{k}"], de[f"item.{M}.{k}"] = e, g
for k, (e, g) in block_names.items():
    en[f"block.{M}.{k}"], de[f"block.{M}.{k}"] = e, g
for k, (e, g) in entity_names.items():
    en[f"entity.{M}.{k}"], de[f"entity.{M}.{k}"] = e, g
    en[f"item.{M}.{k}_spawn_egg"], de[f"item.{M}.{k}_spawn_egg"] = f"{e} Spawn Egg", f"{g}-Spawn-Ei"
for k, (e, g) in extra.items():
    en[k], de[k] = e, g
write(f"{A}/lang/en_us.json", en)
write(f"{A}/lang/de_de.json", de)

# =====================================================================
#  REZEPTE
# =====================================================================
R = f"{D}/{M}/recipes"
it = lambda x: {"item": x if ":" in x else f"{M}:{x}"}
tg = lambda x: {"tag": x}


def shaped(name, pattern, key, result=None, count=1):
    write(f"{R}/{name}.json", {"type": "minecraft:crafting_shaped", "pattern": pattern, "key": key,
                               "result": {"item": f"{M}:{result or name}", "count": count}})


def shapeless(name, ingredients, result=None, count=1):
    write(f"{R}/{name}.json", {"type": "minecraft:crafting_shapeless", "ingredients": ingredients,
                               "result": {"item": f"{M}:{result or name}", "count": count}})


def cook(name, ingredient, result, xp=0.7, kinds=("smelting", "blasting")):
    times = {"smelting": 200, "blasting": 100, "smoking": 100, "campfire_cooking": 600}
    for k in kinds:
        write(f"{R}/{name}_{k}.json", {"type": f"minecraft:{k}", "ingredient": it(ingredient),
                                        "result": result if ":" in result else f"{M}:{result}",
                                        "experience": xp, "cookingtime": times[k]})


STICK = tg("forge:rods/wooden")
mats = {"aetherite": it("aetherite_shard"), "emberite": it("emberite_ingot"), "voidium": it("voidium_ingot")}
handle = {"aetherite": STICK, "emberite": tg("forge:rods/blaze"), "voidium": tg("forge:rods/blaze")}
for mat, X in mats.items():
    H = handle[mat]
    shaped(f"{mat}_sword", ["#", "#", "|"], {"#": X, "|": H})
    shaped(f"{mat}_pickaxe", ["###", " | ", " | "], {"#": X, "|": H})
    shaped(f"{mat}_axe", ["##", "#|", " |"], {"#": X, "|": H})
    shaped(f"{mat}_shovel", ["#", "|", "|"], {"#": X, "|": H})
    shaped(f"{mat}_hoe", ["##", " |", " |"], {"#": X, "|": H})
    shaped(f"{mat}_helmet", ["###", "# #"], {"#": X})
    shaped(f"{mat}_chestplate", ["# #", "###", "###"], {"#": X})
    shaped(f"{mat}_leggings", ["###", "# #", "# #"], {"#": X})
    shaped(f"{mat}_boots", ["# #", "# #"], {"#": X})

shaped("aetherite_block", ["###", "###", "###"], {"#": it("aetherite_shard")})
shapeless("aetherite_shard_from_block", [it("aetherite_block")], "aetherite_shard", 9)
shaped("emberite_block", ["###", "###", "###"], {"#": it("emberite_ingot")})
shapeless("emberite_ingot_from_block", [it("emberite_block")], "emberite_ingot", 9)
shaped("voidium_block", ["###", "###", "###"], {"#": it("voidium_ingot")})
shapeless("voidium_ingot_from_block", [it("voidium_block")], "voidium_ingot", 9)
cook("aetherite_shard_from_ore", "aetherite_ore", "aetherite_shard", 1.0)
cook("emberite_ingot_from_chunk", "emberite_chunk", "emberite_ingot", 1.0)
cook("emberite_ingot_from_ore", "ember_ore", "emberite_ingot", 1.2)
cook("void_shard_from_ore", "void_ore", "void_shard", 1.5)
shapeless("voidium_ingot", [it("void_shard")] * 4 + [it("emberite_ingot")] * 2 + [it("wisp_essence")])
cook("cooked_boar", "raw_boar", "cooked_boar", 0.35, ("smelting", "smoking", "campfire_cooking"))

STONE_BRICKS = it("minecraft:stone_bricks")
shaped("aether_bricks", ["BBB", "BSB", "BBB"], {"B": STONE_BRICKS, "S": it("aetherite_shard")}, count=8)
shaped("aether_brick_stairs", ["#  ", "## ", "###"], {"#": it("aether_bricks")}, count=4)
shaped("aether_brick_slab", ["###"], {"#": it("aether_bricks")}, count=6)
shaped("aether_lamp", ["G#G", "#L#", "G#G"], {"G": tg("forge:glass"), "#": it("aetherite_shard"), "L": it("minecraft:glowstone")}, count=4)
shaped("aether_crystal_block", ["##", "##"], {"#": it("aetherite_shard")})

shaped("aether_core", [" # ", "RER", " # "], {"#": it("aetherite_shard"), "R": tg("forge:dusts/redstone"), "E": tg("forge:ender_pearls")})
shaped("aetherite_hammer", ["B#B", " T ", " | "], {"B": it("aetherite_block"), "#": it("aetherite_shard"), "T": it("aetherite_pickaxe"), "|": STICK})
shaped("aetherite_excavator", ["#B#", " T ", " | "], {"B": it("aetherite_block"), "#": it("aetherite_shard"), "T": it("aetherite_shovel"), "|": STICK})
shaped("lumber_axe", ["B#", "BT", " |"], {"B": it("aetherite_block"), "#": it("aetherite_shard"), "T": it("aetherite_axe"), "|": STICK})
shaped("aether_bow", [" #S", "C S", " #S"], {"#": it("aetherite_shard"), "C": it("aether_core"), "S": tg("forge:string")})
shaped("warden_blade", [" H ", " S ", " | "], {"H": it("warden_heart"), "S": it("aetherite_sword"), "|": tg("forge:rods/blaze")})
shaped("warden_sigil", ["W#W", "#C#", "W#W"], {"W": it("wisp_essence"), "#": it("aetherite_block"), "C": it("aether_core")})
shapeless("heart_crystal", [it("minecraft:golden_apple"), it("wisp_essence"), it("wisp_essence"), it("aether_crystal_block")])

shaped("magnet_ring", ["I#I", "I I", " C "], {"I": tg("forge:ingots/iron"), "#": it("aetherite_shard"), "C": it("aether_core")})
shaped("ender_pouch", ["LSL", "LEL", "LCL"], {"L": tg("forge:leather"), "S": tg("forge:string"), "E": it("minecraft:ender_chest"), "C": it("aether_core")})
shapeless("pocket_crafter", [it("minecraft:crafting_table"), tg("forge:leather"), tg("forge:leather"), tg("forge:string"), it("aetherite_shard")])
shaped("backpack", ["LSL", "LCL", "LLL"], {"L": tg("forge:leather"), "S": tg("forge:string"), "C": tg("forge:chests/wooden")})
shaped("large_backpack", ["L#L", "#B#", "L#L"], {"L": tg("forge:leather"), "#": it("aetherite_shard"), "B": it("backpack")})
shaped("torch_staff", ["  G", " C ", "|  "], {"G": it("minecraft:glowstone"), "C": it("aether_core"), "|": STICK})
shaped("growth_staff", ["  G", " C ", "|  "], {"G": it("minecraft:bone_block"), "C": it("aether_core"), "|": STICK})
shapeless("recall_scroll", [it("minecraft:paper"), it("minecraft:paper"), tg("forge:ender_pearls"), it("aetherite_shard")], count=2)
shapeless("trail_mix", [tg("forge:seeds"), it("minecraft:cocoa_beans"), it("minecraft:sweet_berries"), it("minecraft:dried_kelp")], count=2)
shapeless("glowcap_stew", [it("minecraft:bowl"), it("glowcap"), it("glowcap"), it("minecraft:brown_mushroom")])

shaped("elevator", ["WWW", "WPW", "WWW"], {"W": tg("minecraft:wool"), "P": tg("forge:ender_pearls")}, count=2)
shaped("warding_lantern", ["NWN", "WLW", "NWN"], {"N": tg("forge:nuggets/iron"), "W": it("wisp_essence"), "L": it("minecraft:soul_lantern")})
shaped("growth_totem", ["LGL", "LBL", "LCL"], {"L": tg("minecraft:logs"), "G": it("glowcap"), "B": it("minecraft:bone_block"), "C": it("aether_core")})
shaped("waystone", ["S#S", "#E#", "SSS"], {"S": STONE_BRICKS, "#": it("aether_bricks"), "E": it("minecraft:ender_eye")})
shaped("warden_altar", ["OBO", "BCB", "OOO"], {"O": tg("forge:obsidian"), "B": it("aether_bricks"), "C": it("aether_core")})

# =====================================================================
#  LOOT TABLES
# =====================================================================
L = f"{D}/{M}/loot_tables"


def uni(a, b_):
    return {"min": float(a), "max": float(b_), "type": "minecraft:uniform"}


def entry(name, weight=1, count=None, extra_fn=None):
    e = {"type": "minecraft:item", "name": name if ":" in name else f"{M}:{name}", "weight": weight}
    fns = []
    if count:
        fns.append({"function": "minecraft:set_count", "count": uni(*count)})
    if extra_fn:
        fns += extra_fn
    if fns:
        e["functions"] = fns
    return e


SURVIVES = [{"condition": "minecraft:survives_explosion"}]
SILK = {"condition": "minecraft:match_tool", "predicate": {"enchantments": [{"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}]}}


def self_drop(name):
    write(f"{L}/blocks/{name}.json", {"type": "minecraft:block", "pools": [{
        "rolls": 1, "entries": [{"type": "minecraft:item", "name": f"{M}:{name}"}], "conditions": SURVIVES}]})


def ore_drop(name, item, lo, hi):
    write(f"{L}/blocks/{name}.json", {"type": "minecraft:block", "pools": [{
        "rolls": 1, "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "name": f"{M}:{name}", "conditions": [SILK]},
            {"type": "minecraft:item", "name": f"{M}:{item}", "functions": [
                {"function": "minecraft:set_count", "count": uni(lo, hi)},
                {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                {"function": "minecraft:explosion_decay"}]}]}]}]})


for n in ("aetherite_block", "emberite_block", "voidium_block", "aether_bricks", "aether_brick_stairs", "aether_lamp",
          "glowcap", "elevator", "warding_lantern", "growth_totem", "waystone", "warden_altar"):
    self_drop(n)
ore_drop("aetherite_ore", "aetherite_shard", 1, 2)
ore_drop("ember_ore", "emberite_chunk", 1, 1)
ore_drop("void_ore", "void_shard", 1, 2)
write(f"{L}/blocks/aether_brick_slab.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{
    "type": "minecraft:item", "name": f"{M}:aether_brick_slab", "functions": [
        {"function": "minecraft:set_count", "count": 2, "conditions": [{"condition": "minecraft:block_state_property",
                                                                      "block": f"{M}:aether_brick_slab", "properties": {"type": "double"}}]},
        {"function": "minecraft:explosion_decay"}]}]}]})
write(f"{L}/blocks/aether_crystal_block.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{
    "type": "minecraft:alternatives", "children": [
        {"type": "minecraft:item", "name": f"{M}:aether_crystal_block", "conditions": [SILK]},
        {"type": "minecraft:item", "name": f"{M}:aetherite_shard", "functions": [{"function": "minecraft:set_count", "count": uni(2, 4)}]}]}]}]})
write(f"{L}/blocks/aether_crystal_cluster.json", {"type": "minecraft:block", "pools": [
    {"rolls": 1, "entries": [{"type": "minecraft:alternatives", "children": [
        {"type": "minecraft:item", "name": f"{M}:aether_crystal_cluster", "conditions": [SILK]},
        {"type": "minecraft:item", "name": f"{M}:aetherite_shard", "functions": [
            {"function": "minecraft:set_count", "count": uni(2, 4)},
            {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"}]}]}]},
    {"rolls": 1, "entries": [{"type": "minecraft:item", "name": f"{M}:heart_crystal"}],
     "conditions": [{"condition": "minecraft:inverted", "term": SILK},
                    {"condition": "minecraft:random_chance", "chance": 0.04}]}]})
write(f"{L}/blocks/grave.json", {"type": "minecraft:block", "pools": []})

LOOTING = lambda a, b_: [{"function": "minecraft:looting_enchant", "count": uni(a, b_)}]


def mob(name, pools):
    write(f"{L}/entities/{name}.json", {"type": "minecraft:entity", "pools": pools})


def mpool(item, lo, hi, loot=(0, 1), chance=None, extra_fn=None):
    e = {"type": "minecraft:item", "name": item if ":" in item else f"{M}:{item}",
         "functions": [{"function": "minecraft:set_count", "count": uni(lo, hi)}] + LOOTING(*loot) + (extra_fn or [])}
    p = {"rolls": 1, "entries": [e]}
    if chance:
        p["conditions"] = [{"condition": "minecraft:killed_by_player"},
                           {"condition": "minecraft:random_chance_with_looting", "chance": chance, "looting_multiplier": 0.02}]
    return p


mob("ruin_guardian", [mpool("minecraft:rotten_flesh", 0, 2), mpool("aetherite_shard", 0, 1),
                      mpool("minecraft:iron_nugget", 1, 3), mpool("recall_scroll", 1, 1, (0, 0), chance=0.05)])
mob("crystal_skeleton", [mpool("minecraft:bone", 0, 2), mpool("minecraft:arrow", 0, 2), mpool("aetherite_shard", 0, 1),
                         mpool("heart_crystal", 1, 1, (0, 0), chance=0.015)])
mob("mossback_boar", [mpool("raw_boar", 1, 3, extra_fn=[{"function": "minecraft:furnace_smelt", "conditions": [
    {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"flags": {"is_on_fire": True}}}]}]),
                      mpool("minecraft:leather", 0, 2)])
mob("wisp", [mpool("wisp_essence", 1, 2)])
mob("void_stalker", [mpool("void_shard", 1, 2), mpool("minecraft:ender_pearl", 0, 1)])
mob("aether_warden", [
    {"rolls": 1, "entries": [{"type": "minecraft:item", "name": f"{M}:warden_heart"}]},
    {"rolls": 1, "entries": [entry("heart_crystal", count=(2, 3))]},
    {"rolls": 1, "entries": [entry("aetherite_block", count=(2, 4))]},
    {"rolls": 1, "entries": [entry("minecraft:enchanted_book", extra_fn=[{"function": "minecraft:enchant_with_levels",
                                                                          "levels": 30, "treasure": True}])]}])


def chest(name, guaranteed, rolls, pool):
    pools = [{"rolls": 1, "entries": [g]} for g in guaranteed]
    pools.append({"rolls": uni(*rolls), "entries": pool})
    write(f"{L}/chests/{name}.json", {"type": "minecraft:chest", "pools": pools})


BOOK = entry("minecraft:book", 5, extra_fn=[{"function": "minecraft:enchant_randomly"}])
chest("aether_shrine", [entry("aetherite_shard", count=(3, 8))], (4, 7), [
    entry("aether_core", 6, (1, 1)), entry("heart_crystal", 3), entry("recall_scroll", 8, (1, 2)),
    entry("minecraft:golden_apple", 4), entry("aetherite_pickaxe", 2), entry("aetherite_sword", 2), BOOK,
    entry("glowcap", 6, (2, 5)), entry("aether_crystal_cluster", 5, (1, 3)), entry("wisp_essence", 6, (1, 3)),
    entry("minecraft:experience_bottle", 6, (2, 6))])
chest("traveler_camp", [entry("trail_mix", count=(2, 5))], (3, 6), [
    entry("minecraft:bread", 10, (2, 5)), entry("cooked_boar", 8, (1, 4)), entry("recall_scroll", 6, (1, 2)),
    entry("backpack", 2), entry("magnet_ring", 1), entry("minecraft:torch", 10, (4, 12)), entry("minecraft:compass", 4),
    entry("minecraft:leather", 8, (1, 4)), entry("minecraft:map", 4), entry("pocket_crafter", 2),
    entry("minecraft:emerald", 5, (1, 4))])
chest("watchtower", [entry("minecraft:arrow", count=(6, 16))], (4, 7), [
    entry("minecraft:iron_ingot", 10, (2, 6)), entry("aetherite_shard", 10, (2, 6)), entry("aether_bow", 2),
    entry("minecraft:crossbow", 4), entry("minecraft:emerald", 6, (2, 6)), entry("heart_crystal", 2),
    entry("warden_sigil", 1), entry("minecraft:spectral_arrow", 5, (4, 10)), BOOK, entry("torch_staff", 2)])
chest("catacomb", [entry("minecraft:bone", count=(2, 6))], (4, 8), [
    entry("minecraft:gold_ingot", 8, (2, 6)), entry("aetherite_shard", 10, (3, 8)), entry("heart_crystal", 4),
    BOOK, entry("warden_sigil", 2), entry("minecraft:diamond", 3, (1, 2)), entry("minecraft:name_tag", 3),
    entry("minecraft:saddle", 3), entry("recall_scroll", 5, (1, 2)), entry("minecraft:golden_apple", 3),
    entry("growth_staff", 2), entry("large_backpack", 1)])
chest("warden_sanctum", [entry("warden_sigil", count=(1, 2)), entry("aether_core", count=(1, 2))], (3, 5), [
    entry("aetherite_block", 6, (1, 2)), entry("heart_crystal", 6), entry("minecraft:golden_apple", 6, (1, 2)),
    entry("minecraft:enchanted_golden_apple", 1), BOOK, entry("minecraft:diamond", 5, (1, 3))])

# =====================================================================
#  TAGS & LOOT-MODIFIER
# =====================================================================
def tagfile(path, values):
    write(path, {"replace": False, "values": values})


T = f"{D}/forge/tags"
tagfile(f"{T}/items/gems/aetherite.json", [f"{M}:aetherite_shard"])
tagfile(f"{T}/items/gems/void.json", [f"{M}:void_shard"])
tagfile(f"{T}/items/gems.json", ["#forge:gems/aetherite", "#forge:gems/void"])
tagfile(f"{T}/items/ingots/emberite.json", [f"{M}:emberite_ingot"])
tagfile(f"{T}/items/ingots/voidium.json", [f"{M}:voidium_ingot"])
tagfile(f"{T}/items/ingots.json", ["#forge:ingots/emberite", "#forge:ingots/voidium"])
for ore in ("aetherite", "ember", "void"):
    for kind in ("items", "blocks"):
        tagfile(f"{T}/{kind}/ores/{ore}.json", [f"{M}:{ore}_ore"])
for kind in ("items", "blocks"):
    tagfile(f"{T}/{kind}/ores.json", ["#forge:ores/aetherite", "#forge:ores/ember", "#forge:ores/void"])
    for blk in ("aetherite", "emberite", "voidium"):
        tagfile(f"{T}/{kind}/storage_blocks/{blk}.json", [f"{M}:{blk}_block"])
tagfile(f"{D}/minecraft/tags/blocks/beacon_base_blocks.json", [f"{M}:aetherite_block", f"{M}:emberite_block", f"{M}:voidium_block"])
tagfile(f"{D}/minecraft/tags/items/beacon_payment_items.json", [f"{M}:aetherite_shard", f"{M}:emberite_ingot", f"{M}:voidium_ingot"])
tagfile(f"{D}/minecraft/tags/blocks/stairs.json", [f"{M}:aether_brick_stairs"])
tagfile(f"{D}/minecraft/tags/blocks/slabs.json", [f"{M}:aether_brick_slab"])
tagfile(f"{D}/minecraft/tags/items/stairs.json", [f"{M}:aether_brick_stairs"])
tagfile(f"{D}/minecraft/tags/items/slabs.json", [f"{M}:aether_brick_slab"])
tagfile(f"{D}/minecraft/tags/items/piglin_loved.json", [f"{M}:emberite_ingot"])

write(f"{D}/forge/loot_modifiers/global_loot_modifiers.json", {"replace": False, "entries": [f"{M}:auto_smelt"]})
write(f"{D}/{M}/loot_modifiers/auto_smelt.json", {"type": f"{M}:auto_smelt", "conditions": []})

with open(f"{ROOT}/pack.mcmeta", "w") as f:
    json.dump({"pack": {"description": "Aetheria Overhaul resources", "pack_format": 6}}, f, indent=2)

print("ok:", sum(len(fs) for _, _, fs in os.walk(ROOT)), "Dateien")
