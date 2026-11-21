package de.aetherkit.world;

import de.aetherkit.AKConfig;
import de.aetherkit.AetherKit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

/**
 * Basis für alle prozeduralen Strukturen. Jede Struktur wird pro Chunk mit einer Wahrscheinlichkeit
 * (chance-Decorator) aufgerufen und baut sich um die Chunkmitte herum (max. ~9 Blöcke Radius).
 */
public abstract class AKFeature extends Feature<NoFeatureConfig> {
    protected AKFeature() {
        super(NoFeatureConfig.CODEC);
    }

    @Override
    public boolean place(ISeedReader level, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (!structuresEnabled()) return false;
        return generate(level, rand, new BlockPos(pos.getX() + 8, 0, pos.getZ() + 8));
    }

    protected abstract boolean generate(ISeedReader level, Random rand, BlockPos center);

    static boolean structuresEnabled() {
        try {
            return AKConfig.STRUCTURES.get();
        } catch (Exception e) {
            return true;
        }
    }

    // ------------------------------------------------------------------ Helfer

    /** Oberste feste Bodenhöhe (ohne Wasser). */
    protected static int ground(ISeedReader level, int x, int z) {
        return level.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
    }

    /** true, wenn an x/z Wasser über dem Boden steht. */
    protected static boolean wet(ISeedReader level, int x, int z) {
        return level.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z) != ground(level, x, z);
    }

    /** Prüft, ob die Fläche trocken und halbwegs eben ist. */
    protected static boolean suitable(ISeedReader level, BlockPos center, int radius, int maxDiff) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int dx = -radius; dx <= radius; dx += radius) {
            for (int dz = -radius; dz <= radius; dz += radius) {
                int x = center.getX() + dx, z = center.getZ() + dz;
                if (wet(level, x, z)) return false;
                int h = ground(level, x, z);
                min = Math.min(min, h);
                max = Math.max(max, h);
            }
        }
        return max - min <= maxDiff && min > 2;
    }

    protected static void set(ISeedReader level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos).getBlock() == Blocks.BEDROCK) return;
        level.setBlock(pos, state, 2);
    }

    protected static void chest(ISeedReader level, Random rand, BlockPos pos, Direction facing, String lootTable) {
        set(level, pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing));
        LockableLootTileEntity.setLootTable(level, rand, pos, new ResourceLocation(AetherKit.MODID, "chests/" + lootTable));
    }

    protected static void barrel(ISeedReader level, Random rand, BlockPos pos, String lootTable) {
        set(level, pos, Blocks.BARREL.defaultBlockState());
        LockableLootTileEntity.setLootTable(level, rand, pos, new ResourceLocation(AetherKit.MODID, "chests/" + lootTable));
    }

    protected static void spawner(ISeedReader level, BlockPos pos, EntityType<?> type) {
        set(level, pos, Blocks.SPAWNER.defaultBlockState());
        TileEntity te = level.getBlockEntity(pos);
        if (te instanceof MobSpawnerTileEntity) {
            ((MobSpawnerTileEntity) te).getSpawner().setEntityId(type);
        }
    }

    /** Füllt Luft/Flüssigkeit unter einer Position bis zu depth Blöcke mit einem Fundament. */
    protected static void foundation(ISeedReader level, BlockPos top, BlockState state, int depth) {
        for (int i = 1; i <= depth; i++) {
            BlockPos p = top.below(i);
            BlockState s = level.getBlockState(p);
            if (s.isAir() || !s.getFluidState().isEmpty() || s.getMaterial().isReplaceable()) {
                set(level, p, state);
            } else {
                return;
            }
        }
    }

    /** Zufällig verwitterte Steinziegel. */
    protected static BlockState ruinBrick(Random rand) {
        int r = rand.nextInt(10);
        if (r < 2) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        if (r < 4) return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        return Blocks.STONE_BRICKS.defaultBlockState();
    }
}
