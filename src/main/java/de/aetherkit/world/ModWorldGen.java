package de.aetherkit.world;

import de.aetherkit.AKConfig;
import de.aetherkit.AetherKit;
import de.aetherkit.registry.ModBlocks;
import de.aetherkit.registry.ModEntities;
import de.aetherkit.registry.ModFeatures;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Weltgenerierung: 3 Erze (Oberwelt/Nether/Ende), 6 Strukturen, Leuchtpilze und Mob-Spawns.
 */
public final class ModWorldGen {
    private static ConfiguredFeature<?, ?> oreAetherite, oreEmber, oreVoid;
    private static ConfiguredFeature<?, ?> shrine, camp, watchtower, geode, catacomb, sanctum, glowcaps;

    private static final Set<Biome.Category> SURFACE_LAND = EnumSet.of(Biome.Category.PLAINS, Biome.Category.FOREST,
            Biome.Category.TAIGA, Biome.Category.SAVANNA, Biome.Category.EXTREME_HILLS, Biome.Category.JUNGLE,
            Biome.Category.MESA, Biome.Category.DESERT, Biome.Category.ICY, Biome.Category.SWAMP);
    private static final Set<Biome.Category> GREEN = EnumSet.of(Biome.Category.PLAINS, Biome.Category.FOREST,
            Biome.Category.TAIGA, Biome.Category.SAVANNA, Biome.Category.EXTREME_HILLS, Biome.Category.JUNGLE);
    private static final Set<Biome.Category> DARK_WOODS = EnumSet.of(Biome.Category.FOREST, Biome.Category.TAIGA,
            Biome.Category.SWAMP, Biome.Category.JUNGLE);

    private ModWorldGen() {}

    private static ConfiguredFeature<?, ?> reg(String name, ConfiguredFeature<?, ?> feature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(AetherKit.MODID, name), feature);
    }

    private static ConfiguredFeature<?, ?> structure(Feature<NoFeatureConfig> feature, int rarity) {
        return feature.configured(NoFeatureConfig.INSTANCE).chance(rarity);
    }

    public static void register() {
        oreAetherite = reg("ore_aetherite", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                ModBlocks.AETHERITE_ORE.get().defaultBlockState(), 6)).range(40).squared().count(5));
        oreEmber = reg("ore_ember", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK,
                ModBlocks.EMBER_ORE.get().defaultBlockState(), 5)).range(118).squared().count(7));
        oreVoid = reg("ore_void", Feature.ORE.configured(new OreFeatureConfig(new BlockMatchRuleTest(Blocks.END_STONE),
                ModBlocks.VOID_ORE.get().defaultBlockState(), 4)).range(90).squared().count(6));

        shrine = reg("aether_shrine", structure(ModFeatures.AETHER_SHRINE.get(), 110));
        camp = reg("traveler_camp", structure(ModFeatures.TRAVELER_CAMP.get(), 90));
        watchtower = reg("ruined_watchtower", structure(ModFeatures.RUINED_WATCHTOWER.get(), 150));
        geode = reg("crystal_geode", structure(ModFeatures.CRYSTAL_GEODE.get(), 45));
        catacomb = reg("catacomb", structure(ModFeatures.CATACOMB.get(), 70));
        sanctum = reg("warden_sanctum", structure(ModFeatures.WARDEN_SANCTUM.get(), 400));
        glowcaps = reg("glowcap_patch", structure(ModFeatures.GLOWCAP_PATCH.get(), 5));
    }

    private static boolean mobsEnabled() {
        try {
            return AKConfig.MOB_SPAWNS.get();
        } catch (Exception e) {
            return true;
        }
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (oreAetherite == null) return;
        Biome.Category cat = event.getCategory();
        GenerationStage.Decoration ores = GenerationStage.Decoration.UNDERGROUND_ORES;

        if (cat == Biome.Category.NETHER) {
            event.getGeneration().addFeature(ores, oreEmber);
            return;
        }
        if (cat == Biome.Category.THEEND) {
            event.getGeneration().addFeature(ores, oreVoid);
            if (mobsEnabled()) {
                event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.VOID_STALKER.get(), 6, 1, 2));
            }
            return;
        }
        if (cat == Biome.Category.NONE) return;

        // ---------------- Oberwelt ----------------
        event.getGeneration().addFeature(ores, oreAetherite);
        event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, geode);
        event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, catacomb);

        if (SURFACE_LAND.contains(cat)) {
            event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, watchtower);
        }
        if (GREEN.contains(cat)) {
            event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, shrine);
            event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, camp);
            event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, sanctum);
        }
        if (DARK_WOODS.contains(cat)) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, glowcaps);
        }

        if (!mobsEnabled()) return;
        if (cat != Biome.Category.OCEAN && cat != Biome.Category.RIVER && cat != Biome.Category.MUSHROOM) {
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.RUIN_GUARDIAN.get(), 12, 1, 2));
            int skeletonWeight = (cat == Biome.Category.ICY || cat == Biome.Category.EXTREME_HILLS || cat == Biome.Category.TAIGA) ? 35 : 8;
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.CRYSTAL_SKELETON.get(), skeletonWeight, 1, 3));
        }
        if (cat == Biome.Category.FOREST || cat == Biome.Category.TAIGA || cat == Biome.Category.PLAINS) {
            event.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.MOSSBACK_BOAR.get(), 9, 2, 4));
        }
        if (DARK_WOODS.contains(cat)) {
            event.getSpawns().addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.WISP.get(), 10, 1, 3));
        }
    }
}
