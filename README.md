# Aetheria Overhaul – Minecraft Forge Mod (Java 1.16.5)

Ein Overhaul in einer einzigen Mod: neue Erzstufen von der Oberwelt bis ins Ende, eigene Mobs und ein Boss,
Strukturen in der ganzen Welt, Schnellreise, Gräber, Rucksäcke, RPG-Herzen und viele Quality-of-Life-Funktionen.
Alle Texturen sind eigene Pixel-Art. Sprachen: Deutsch und Englisch.

![Items](docs/preview_items.png)
![Neue Inhalte](docs/preview_new.png)

---

## Bauen

Voraussetzung: **Java 8 JDK** (z. B. Temurin 8).

1. Ordner entpacken, Terminal darin öffnen.
2. Windows: `gradlew build` – macOS/Linux: `./gradlew build`
   (Beim ersten Mal werden Minecraft + Forge heruntergeladen, das dauert ein paar Minuten.)
3. Fertige Mod: `build/libs/aetherkit-1.16.5-1.0.0.jar` → in den `mods`-Ordner von **Forge 1.16.5 (36.2.x)**.

Testen ohne Installation: `gradlew runClient`.
**Neue Welt erstellen**, damit Erze und Strukturen generiert werden.

---

## Fortschritt (Progression)

| Stufe | Wo | Material | Besonderes |
|---|---|---|---|
| 1 | Oberwelt, Y 0–40 | **Aetherit-Splitter** | Werkzeuge gut verzauberbar, Spezialwerkzeuge |
| 2 | Nether | **Emberit** (Glut-Erz → Brocken → Barren) | Schwert setzt in Brand, Spitzhacke schmilzt Erze sofort |
| 3 | Ende | **Voidium** (4 Leerensplitter + 2 Emberit + Irrlicht-Essenz) | Lebensraub-Schwert, Rüstung mit **Fliegen** |
| Boss | Wächter-Heiligtum | **Wächterherz** | Klinge des Wächters (Schockwelle) |

**Rüstungs-Set-Boni (voller Satz):**
- Aetherit: Eile I (Stiefel: halber Fallschaden)
- Emberit: Feuerresistenz + Stärke
- Voidium: Fliegen, Resistenz, Eile II (Stiefel: kein Fallschaden)

---

## Mobs

| Mob | Wo | Besonderheit | Drops |
|---|---|---|---|
| **Ruinenwächter** | Oberwelt (nachts), Wachtürme | gepanzert, verbrennt nicht, schwächt | Splitter, Rückkehrschriftrolle |
| **Kristallskelett** | Kalte Biome, Berge, Katakomben | Pfeile verlangsamen + markieren | Knochen, Splitter, selten Herzkristall |
| **Moosrücken-Eber** | Wälder, Ebenen | friedlich, züchtbar (Karotten, Kartoffeln, Äpfel) | Eberfleisch, Leder |
| **Irrlicht** | dunkle Wälder, Sümpfe | schwebendes Lichtwesen | **Irrlicht-Essenz** |
| **Leerenpirscher** | Ende | starker Enderman, blendet | **Leerensplitter** |
| **Aetherwächter** (Boss) | Wächter-Altar | 320 HP, Schockwellen, Phase 2: Diener + Teleport | Wächterherz, Herzkristalle |

**Boss beschwören:** Wächter-Siegel (in Truhen oder craftbar) auf einem **Wächter-Altar** benutzen.

---

## Strukturen

| Struktur | Wo | Inhalt |
|---|---|---|
| Aether-Schrein | Ebenen, Wälder, Taiga … | Kristallaltar, Truhe mit Kernen & Herzkristallen |
| Reiselager | grüne Biome | Zelt, Lagerfeuer, Fass mit Vorräten |
| Verfallener Wachturm | fast überall an Land | Ruinenwächter-Spawner, Truhe oben |
| Kristallgeode | unterirdisch | leuchtende Kristallgruppen, Aetherit-Erz |
| Katakombe | unterirdisch | Spawner, 2 Truhen, Spinnweben |
| Wächter-Heiligtum | selten, grüne Biome | Arena mit Altar und Boss-Siegel |

Dazu **Leuchtpilze** in dunklen Wäldern.

---

## Systeme & Quality of Life

- **Wegsteine:** Rechtsklick entdeckt sie; danach Schnellreise zu allen entdeckten Wegsteinen (auch in andere Dimensionen)
- **Gräber:** beim Tod landet das komplette Inventar in einem Grab, Koordinaten im Chat, Rechtsklick = alles zurück
- **Rucksäcke:** 27 bzw. 54 Plätze
- **Herzkristalle:** +1 Herz dauerhaft (bis +10), bleibt nach dem Tod
- **Aufzug-Block:** springen = hoch, schleichen = runter
- **Schutzlaterne:** keine Monster-Spawns im Umkreis von 48 Blöcken
- **Wachstumstotem:** Pflanzen wachsen in der Nähe schneller
- **Inventar sortieren:** Mausrad-Klick in jeder Truhe/im Inventar (Taste änderbar)
- **Info-HUD:** Koordinaten, Biom, Tag & Uhrzeit
- **Vein-Mining:** schleichen + Erz mit Spitzhacke = ganze Ader
- **3x3-Hammer & Bagger, Holzfälleraxt** (fällt ganze Bäume)
- Magnetring, Enderbeutel, Taschenwerkbank, Fackelstab, Wachstumsstab, Rückkehrschriftrolle, Aetherbogen
- Rechtsklick-Ernte, Doppeltüren, kein Ackerzertrampeln, selbstpflanzende Setzlinge, Haltbarkeitswarnung, Essens-Tooltips

Alles abschaltbar in `config/aetherkit-common.toml`.

---

## Rezepte (Auswahl)

`S` Aetherit-Splitter · `B` Aetheritblock · `C` Aetherkern · `W` Irrlicht-Essenz · `|` Stock

| Item | Rezept |
|---|---|
| Aetherkern | ` S ` / `R E R` / ` S ` (Redstone, Enderperle) |
| Voidiumbarren | formlos: 4 Leerensplitter + 2 Emberitbarren + Irrlicht-Essenz |
| Wegstein | `Ziegel Aether Ziegel` / `Aether Enderauge Aether` / `Ziegel Ziegel Ziegel` |
| Aufzug (2x) | 8 Wolle um eine Enderperle |
| Schutzlaterne | Eisennuggets + 4 Irrlicht-Essenz um eine Seelenlaterne |
| Wachstumstotem | Stämme + Leuchtpilz + Knochenblock + Aetherkern |
| Rucksack | Leder + Faden + Truhe |
| Herzkristall | formlos: Goldapfel + 2 Irrlicht-Essenz + Aetherkristallblock |
| Wächter-Siegel | 4 Irrlicht-Essenz + 4 Aetheritblöcke + Aetherkern |
| Klinge des Wächters | Wächterherz über Aetheritschwert über Lohenrute |

Alle Rezepte werden in JEI angezeigt.

---

## Projektstruktur
```
src/main/java/de/aetherkit/
  registry/   Blöcke, Items, Mobs, TileEntities, Features, Stufen, Rüstungen
  block/      Erze, Kristalle, Wegstein, Grab, Laterne, Totem, Altar
  item/       Spezial-Items, Rucksack, Herzkristall, Bogen
  entity/     Mobs + Boss
  client/     Renderer, Modelle, Wegstein-Menü, Client-Setup
  event/      Werkzeug-, QoL-, System- und Client-Events
  system/     Wegstein-Netzwerk, Spielerdaten, Sortierung
  network/    Pakete Client <-> Server
  world/      Strukturen und Weltgenerierung
  loot/       Auto-Schmelzen
tools/        Python-Generatoren für Texturen und JSON-Daten
```
