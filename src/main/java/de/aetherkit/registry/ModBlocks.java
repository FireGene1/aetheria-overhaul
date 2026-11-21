package de.aetherkit.registry;

import de.aetherkit.AetherKit;
import de.aetherkit.block.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AetherKit.MODID);

    private static AbstractBlock.Properties stone(float hardness, float resistance, int harvest) {
        return AbstractBlock.Properties.of(Material.STONE).strength(hardness, resistance)
                .requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(harvest);
    }

    private static AbstractBlock.Properties metal(int harvest, int light) {
        return AbstractBlock.Properties.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL)
                .requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(harvest)
                .lightLevel(s -> light);
    }

    // ---------------- Erze & Speicherblöcke ----------------
    public static final RegistryObject<Block> AETHERITE_ORE = BLOCKS.register("aetherite_ore",
            () -> new AetheriteOreBlock(stone(3.0F, 3.0F, 2).sound(SoundType.STONE).lightLevel(s -> 3),
                    () -> ParticleTypes.END_ROD, 2, 5));
    public static final RegistryObject<Block> EMBER_ORE = BLOCKS.register("ember_ore",
            () -> new AetheriteOreBlock(stone(3.5F, 4.0F, 3).sound(SoundType.NETHER_ORE).lightLevel(s -> 6),
                    () -> ParticleTypes.FLAME, 3, 7));
    public static final RegistryObject<Block> VOID_ORE = BLOCKS.register("void_ore",
            () -> new AetheriteOreBlock(stone(4.5F, 9.0F, 4).sound(SoundType.STONE).lightLevel(s -> 4),
                    () -> ParticleTypes.PORTAL, 5, 10));

    public static final RegistryObject<Block> AETHERITE_BLOCK = BLOCKS.register("aetherite_block", () -> new Block(metal(2, 10)));
    public static final RegistryObject<Block> EMBERITE_BLOCK = BLOCKS.register("emberite_block", () -> new Block(metal(3, 12)));
    public static final RegistryObject<Block> VOIDIUM_BLOCK = BLOCKS.register("voidium_block", () -> new Block(metal(4, 8)));

    // ---------------- Bau- & Dekoblöcke ----------------
    public static final RegistryObject<Block> AETHER_BRICKS = BLOCKS.register("aether_bricks",
            () -> new Block(stone(2.0F, 6.0F, 0).sound(SoundType.STONE)));
    public static final RegistryObject<Block> AETHER_BRICK_STAIRS = BLOCKS.register("aether_brick_stairs",
            () -> new StairsBlock(() -> AETHER_BRICKS.get().defaultBlockState(), stone(2.0F, 6.0F, 0)));
    public static final RegistryObject<Block> AETHER_BRICK_SLAB = BLOCKS.register("aether_brick_slab",
            () -> new SlabBlock(stone(2.0F, 6.0F, 0)));
    public static final RegistryObject<Block> AETHER_LAMP = BLOCKS.register("aether_lamp",
            () -> new Block(AbstractBlock.Properties.of(Material.GLASS).strength(0.5F).sound(SoundType.GLASS).lightLevel(s -> 15)));
    public static final RegistryObject<Block> AETHER_CRYSTAL_BLOCK = BLOCKS.register("aether_crystal_block",
            () -> new Block(AbstractBlock.Properties.of(Material.GLASS).strength(1.5F).sound(SoundType.GLASS)
                    .harvestTool(ToolType.PICKAXE).lightLevel(s -> 7)));
    public static final RegistryObject<Block> AETHER_CRYSTAL_CLUSTER = BLOCKS.register("aether_crystal_cluster",
            () -> new CrystalClusterBlock(AbstractBlock.Properties.of(Material.GLASS).strength(1.0F).sound(SoundType.GLASS)
                    .noOcclusion().lightLevel(s -> 9)));
    public static final RegistryObject<Block> GLOWCAP = BLOCKS.register("glowcap",
            () -> new GlowcapBlock(AbstractBlock.Properties.of(Material.PLANT).noCollission().instabreak()
                    .sound(SoundType.GRASS).lightLevel(s -> 11)));

    // ---------------- Funktionsblöcke ----------------
    public static final RegistryObject<Block> ELEVATOR = BLOCKS.register("elevator",
            () -> new Block(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> WARDING_LANTERN = BLOCKS.register("warding_lantern",
            () -> new WardingLanternBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F)
                    .sound(SoundType.LANTERN).noOcclusion().lightLevel(s -> 15)));
    public static final RegistryObject<Block> GROWTH_TOTEM = BLOCKS.register("growth_totem",
            () -> new GrowthTotemBlock(AbstractBlock.Properties.of(Material.WOOD).strength(2.0F)
                    .sound(SoundType.WOOD).noOcclusion().lightLevel(s -> 6)));
    public static final RegistryObject<Block> WAYSTONE = BLOCKS.register("waystone",
            () -> new WaystoneBlock(stone(5.0F, 1200.0F, 1).sound(SoundType.STONE).noOcclusion().lightLevel(s -> 8)));
    public static final RegistryObject<Block> GRAVE = BLOCKS.register("grave",
            () -> new GraveBlock(AbstractBlock.Properties.of(Material.STONE).strength(2.0F, 3600000.0F)
                    .sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> WARDEN_ALTAR = BLOCKS.register("warden_altar",
            () -> new WardenAltarBlock(stone(50.0F, 1200.0F, 3).sound(SoundType.STONE).lightLevel(s -> 5)));

    private ModBlocks() {}
}
