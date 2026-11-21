package de.aetherkit.registry;

import de.aetherkit.AetherKit;
import de.aetherkit.entity.*;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, AetherKit.MODID);

    private static String id(String name) {
        return AetherKit.MODID + ":" + name;
    }

    public static final RegistryObject<EntityType<RuinGuardianEntity>> RUIN_GUARDIAN = ENTITIES.register("ruin_guardian",
            () -> EntityType.Builder.<RuinGuardianEntity>of(RuinGuardianEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build(id("ruin_guardian")));

    public static final RegistryObject<EntityType<CrystalSkeletonEntity>> CRYSTAL_SKELETON = ENTITIES.register("crystal_skeleton",
            () -> EntityType.Builder.<CrystalSkeletonEntity>of(CrystalSkeletonEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.99F).clientTrackingRange(8).build(id("crystal_skeleton")));

    public static final RegistryObject<EntityType<MossbackBoarEntity>> MOSSBACK_BOAR = ENTITIES.register("mossback_boar",
            () -> EntityType.Builder.<MossbackBoarEntity>of(MossbackBoarEntity::new, EntityClassification.CREATURE)
                    .sized(1.0F, 0.95F).clientTrackingRange(10).build(id("mossback_boar")));

    public static final RegistryObject<EntityType<WispEntity>> WISP = ENTITIES.register("wisp",
            () -> EntityType.Builder.<WispEntity>of(WispEntity::new, EntityClassification.AMBIENT)
                    .sized(0.5F, 0.5F).clientTrackingRange(8).build(id("wisp")));

    public static final RegistryObject<EntityType<VoidStalkerEntity>> VOID_STALKER = ENTITIES.register("void_stalker",
            () -> EntityType.Builder.<VoidStalkerEntity>of(VoidStalkerEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 2.9F).clientTrackingRange(8).build(id("void_stalker")));

    public static final RegistryObject<EntityType<AetherWardenEntity>> AETHER_WARDEN = ENTITIES.register("aether_warden",
            () -> EntityType.Builder.<AetherWardenEntity>of(AetherWardenEntity::new, EntityClassification.MONSTER)
                    .sized(1.1F, 3.5F).fireImmune().clientTrackingRange(10).build(id("aether_warden")));

    /** Mod-Bus: Attribute für alle Mobs. */
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(RUIN_GUARDIAN.get(), RuinGuardianEntity.createGuardianAttributes().build());
        event.put(CRYSTAL_SKELETON.get(), CrystalSkeletonEntity.createCrystalAttributes().build());
        event.put(MOSSBACK_BOAR.get(), MossbackBoarEntity.createBoarAttributes().build());
        event.put(WISP.get(), WispEntity.createWispAttributes().build());
        event.put(VOID_STALKER.get(), VoidStalkerEntity.createStalkerAttributes().build());
        event.put(AETHER_WARDEN.get(), AetherWardenEntity.createWardenAttributes().build());
    }

    /** In commonSetup (enqueueWork) aufrufen. */
    public static void registerSpawnPlacements() {
        EntitySpawnPlacementRegistry.register(RUIN_GUARDIAN.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        EntitySpawnPlacementRegistry.register(CRYSTAL_SKELETON.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        EntitySpawnPlacementRegistry.register(MOSSBACK_BOAR.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
        EntitySpawnPlacementRegistry.register(WISP.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::checkMobSpawnRules);
        EntitySpawnPlacementRegistry.register(VOID_STALKER.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
    }

    private ModEntities() {}
}
