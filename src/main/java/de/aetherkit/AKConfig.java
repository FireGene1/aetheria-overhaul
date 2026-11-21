package de.aetherkit;

import net.minecraftforge.common.ForgeConfigSpec;

/** Konfiguration: config/aetherkit-common.toml */
public final class AKConfig {
    public static final ForgeConfigSpec SPEC;

    // Quality of Life
    public static final ForgeConfigSpec.BooleanValue RIGHT_CLICK_HARVEST;
    public static final ForgeConfigSpec.BooleanValue DOUBLE_DOORS;
    public static final ForgeConfigSpec.BooleanValue NO_FARMLAND_TRAMPLE;
    public static final ForgeConfigSpec.BooleanValue AUTO_PLANT_SAPLINGS;
    public static final ForgeConfigSpec.BooleanValue DURABILITY_WARNING;
    public static final ForgeConfigSpec.BooleanValue FOOD_TOOLTIPS;
    public static final ForgeConfigSpec.BooleanValue GRAVES;
    public static final ForgeConfigSpec.BooleanValue VEIN_MINING;
    public static final ForgeConfigSpec.IntValue VEIN_MAX;
    public static final ForgeConfigSpec.BooleanValue INFO_HUD;
    public static final ForgeConfigSpec.IntValue ELEVATOR_RANGE;
    public static final ForgeConfigSpec.IntValue WARDING_RADIUS;

    // Items
    public static final ForgeConfigSpec.IntValue MAGNET_RANGE;
    public static final ForgeConfigSpec.IntValue TREE_MAX_LOGS;
    public static final ForgeConfigSpec.IntValue AUTO_TORCH_MAX;
    public static final ForgeConfigSpec.BooleanValue ARMOR_SET_BONUS;
    public static final ForgeConfigSpec.BooleanValue VOIDIUM_FLIGHT;

    // Welt
    public static final ForgeConfigSpec.BooleanValue STRUCTURES;
    public static final ForgeConfigSpec.BooleanValue MOB_SPAWNS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.comment("Quality-of-Life-Funktionen").push("qol");
        RIGHT_CLICK_HARVEST = b.comment("Reife Pflanzen per Rechtsklick ernten und automatisch neu pflanzen").define("rightClickHarvest", true);
        DOUBLE_DOORS = b.comment("Holz-Doppeltüren öffnen/schliessen sich gemeinsam").define("doubleDoors", true);
        NO_FARMLAND_TRAMPLE = b.comment("Ackerboden kann nicht mehr zertrampelt werden").define("noFarmlandTrample", true);
        AUTO_PLANT_SAPLINGS = b.comment("Setzlinge pflanzen sich selbst ein, bevor sie verschwinden").define("autoPlantSaplings", true);
        DURABILITY_WARNING = b.comment("Warnung, wenn das Werkzeug in der Hand fast kaputt ist").define("durabilityWarning", true);
        FOOD_TOOLTIPS = b.comment("Hunger/Sättigung im Tooltip von Essen").define("foodTooltips", true);
        GRAVES = b.comment("Beim Tod entsteht ein Grab mit dem kompletten Inventar").define("graves", true);
        VEIN_MINING = b.comment("Schleichen + Erz mit Spitzhacke abbauen = ganze Erzader abbauen").define("veinMining", true);
        VEIN_MAX = b.comment("Maximale Blöcke pro Erzader").defineInRange("veinMax", 48, 4, 256);
        INFO_HUD = b.comment("Koordinaten, Biom, Tag und Uhrzeit oben links anzeigen").define("infoHud", true);
        ELEVATOR_RANGE = b.comment("Maximale Distanz zwischen zwei Aufzugblöcken").defineInRange("elevatorRange", 24, 3, 128);
        WARDING_RADIUS = b.comment("Schutzradius der Schutzlaterne").defineInRange("wardingRadius", 48, 8, 128);
        b.pop();

        b.comment("Einstellungen für Mod-Items").push("items");
        MAGNET_RANGE = b.comment("Reichweite des Magnetrings in Blöcken").defineInRange("magnetRange", 8, 2, 24);
        TREE_MAX_LOGS = b.comment("Maximale Stämme pro Baum für die Holzfälleraxt").defineInRange("treeMaxLogs", 160, 8, 512);
        AUTO_TORCH_MAX = b.comment("Maximale Fackeln pro Auto-Beleuchtung").defineInRange("autoTorchMax", 12, 1, 64);
        ARMOR_SET_BONUS = b.comment("Set-Boni der Rüstungen aktivieren").define("armorSetBonus", true);
        VOIDIUM_FLIGHT = b.comment("Voller Voidium-Satz erlaubt Fliegen").define("voidiumFlight", true);
        b.pop();

        b.comment("Weltgenerierung und Mobs (Änderungen wirken in neuen Chunks)").push("world");
        STRUCTURES = b.comment("Neue Strukturen generieren").define("structures", true);
        MOB_SPAWNS = b.comment("Neue Mobs natürlich spawnen lassen").define("mobSpawns", true);
        b.pop();

        SPEC = b.build();
    }

    private AKConfig() {}
}
