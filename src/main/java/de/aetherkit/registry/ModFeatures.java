package de.aetherkit.registry;

import de.aetherkit.AetherKit;
import de.aetherkit.loot.AutoSmeltModifier;
import de.aetherkit.world.Structures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** Features (Strukturen) und Loot-Modifier. */
public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, AetherKit.MODID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, AetherKit.MODID);

    public static final RegistryObject<Feature<NoFeatureConfig>> AETHER_SHRINE = FEATURES.register("aether_shrine", Structures.AetherShrine::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> TRAVELER_CAMP = FEATURES.register("traveler_camp", Structures.TravelerCamp::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> RUINED_WATCHTOWER = FEATURES.register("ruined_watchtower", Structures.RuinedWatchtower::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> CRYSTAL_GEODE = FEATURES.register("crystal_geode", Structures.CrystalGeode::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> CATACOMB = FEATURES.register("catacomb", Structures.Catacomb::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> WARDEN_SANCTUM = FEATURES.register("warden_sanctum", Structures.WardenSanctum::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> GLOWCAP_PATCH = FEATURES.register("glowcap_patch", Structures.GlowcapPatch::new);

    public static final RegistryObject<AutoSmeltModifier.Serializer> AUTO_SMELT = LOOT_MODIFIERS.register("auto_smelt", AutoSmeltModifier.Serializer::new);

    private ModFeatures() {}
}
