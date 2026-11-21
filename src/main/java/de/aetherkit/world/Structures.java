package de.aetherkit.world;

import de.aetherkit.registry.ModBlocks;
import de.aetherkit.registry.ModEntities;
import net.minecraft.block.*;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import java.util.Random;

/** Alle Strukturen der Mod. */
public final class Structures {
    private Structures() {}

    private static BlockState aetherBrick(Random rand) {
        int r = rand.nextInt(10);
        if (r < 6) return ModBlocks.AETHER_BRICKS.get().defaultBlockState();
        if (r < 8) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    }

    // =====================================================================
    /** Aether-Schrein: verfallener Tempel mit Kristallaltar und Truhe. */
    public static class AetherShrine extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            if (!suitable(level, c, 5, 3)) return false;
            int y0 = ground(level, c.getX(), c.getZ());
            BlockPos o = new BlockPos(c.getX(), y0, c.getZ());

            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    boolean inside = Math.abs(dx) <= 4 && Math.abs(dz) <= 4;
                    BlockPos floor = o.offset(dx, -1, dz);
                    if (inside) {
                        if (rand.nextInt(12) != 0) set(level, floor, aetherBrick(rand));
                        foundation(level, floor, Blocks.STONE_BRICKS.defaultBlockState(), 4);
                        for (int dy = 0; dy <= 6; dy++) set(level, o.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
                    } else if (rand.nextInt(3) == 0) {
                        set(level, floor, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
                    }
                }
            }
            // Säulen an den Ecken, teils eingestürzt
            for (int sx = -4; sx <= 4; sx += 8) {
                for (int sz = -4; sz <= 4; sz += 8) {
                    int h = 2 + rand.nextInt(4);
                    for (int dy = 0; dy < h; dy++) set(level, o.offset(sx, dy, sz), aetherBrick(rand));
                    if (h >= 5) set(level, o.offset(sx, h, sz), ModBlocks.AETHER_LAMP.get().defaultBlockState());
                    else set(level, o.offset(sx, h, sz), ModBlocks.AETHER_BRICK_SLAB.get().defaultBlockState());
                }
            }
            // Altar
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    set(level, o.offset(dx, 0, dz), ModBlocks.AETHER_BRICK_SLAB.get().defaultBlockState());
                }
            }
            set(level, o, ModBlocks.AETHER_CRYSTAL_BLOCK.get().defaultBlockState());
            set(level, o.above(), ModBlocks.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState());
            chest(level, rand, o.offset(0, 0, 3), Direction.NORTH, "aether_shrine");
            // verstreute Kristalle
            for (int i = 0; i < 4; i++) {
                BlockPos p = o.offset(rand.nextInt(7) - 3, 0, rand.nextInt(7) - 3);
                if (level.isEmptyBlock(p) && level.getBlockState(p.below()).isRedstoneConductor(level, p.below())) {
                    set(level, p, ModBlocks.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState());
                }
            }
            return true;
        }
    }

    // =====================================================================
    /** Reiselager: Zelt, Lagerfeuer, Sitzbank und ein Fass mit Vorräten. */
    public static class TravelerCamp extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            if (!suitable(level, c, 4, 2)) return false;
            int y0 = ground(level, c.getX(), c.getZ());
            BlockPos o = new BlockPos(c.getX(), y0, c.getZ());

            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -3; dz <= 6; dz++) {
                    BlockPos g = o.offset(dx, -1, dz);
                    foundation(level, g.above(), Blocks.DIRT.defaultBlockState(), 3);
                    for (int dy = 0; dy <= 4; dy++) set(level, o.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
                    if (rand.nextInt(4) == 0) set(level, g, Blocks.COARSE_DIRT.defaultBlockState());
                }
            }
            set(level, o.below(), Blocks.COBBLESTONE.defaultBlockState());
            set(level, o, Blocks.CAMPFIRE.defaultBlockState());

            // Zelt (A-Rahmen) entlang der x-Achse
            BlockState wall = (rand.nextBoolean() ? Blocks.WHITE_WOOL : Blocks.BROWN_WOOL).defaultBlockState();
            BlockState stripe = Blocks.RED_WOOL.defaultBlockState();
            for (int dx = -2; dx <= 2; dx++) {
                BlockState w = dx == 0 ? stripe : wall;
                set(level, o.offset(dx, 0, 2), w);
                set(level, o.offset(dx, 0, 6), w);
                set(level, o.offset(dx, 1, 3), w);
                set(level, o.offset(dx, 1, 5), w);
                set(level, o.offset(dx, 2, 4), w);
                for (int dz = 3; dz <= 5; dz++) set(level, o.offset(dx, 0, dz), Blocks.BROWN_CARPET.defaultBlockState());
            }
            for (int dz = 3; dz <= 5; dz++) set(level, o.offset(2, 0, dz), wall);
            set(level, o.offset(2, 1, 4), wall);
            set(level, o.offset(-2, 0, 4), Blocks.AIR.defaultBlockState()); // Eingang
            set(level, o.offset(1, 0, 4), Blocks.RED_CARPET.defaultBlockState());
            barrel(level, rand, o.offset(1, 0, 3), "traveler_camp");

            // Sitzbank aus Stämmen
            BlockState log = Blocks.SPRUCE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
            set(level, o.offset(-2, 0, -1), log);
            set(level, o.offset(-2, 0, 0), log);
            set(level, o.offset(2, 0, -1), log);
            // Laterne auf Zaun
            set(level, o.offset(-3, 0, 2), Blocks.SPRUCE_FENCE.defaultBlockState());
            set(level, o.offset(-3, 1, 2), Blocks.LANTERN.defaultBlockState());
            return true;
        }
    }

    // =====================================================================
    /** Verfallener Wachturm mit Ruinenwächter-Spawner unten und Truhe oben. */
    public static class RuinedWatchtower extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            if (!suitable(level, c, 3, 3)) return false;
            int y0 = ground(level, c.getX(), c.getZ());
            BlockPos o = new BlockPos(c.getX(), y0, c.getZ());
            int height = 13 + rand.nextInt(4);

            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    boolean wall = Math.max(Math.abs(dx), Math.abs(dz)) == 3;
                    BlockPos f = o.offset(dx, -1, dz);
                    set(level, f, Blocks.COBBLESTONE.defaultBlockState());
                    foundation(level, f, Blocks.COBBLESTONE.defaultBlockState(), 5);
                    for (int dy = 0; dy < height; dy++) {
                        BlockPos p = o.offset(dx, dy, dz);
                        if (wall) {
                            // obere Ebenen verfallen
                            int fromTop = height - dy;
                            if (fromTop <= 3 && rand.nextInt(fromTop + 1) == 0) set(level, p, Blocks.AIR.defaultBlockState());
                            else set(level, p, ruinBrick(rand));
                        } else {
                            set(level, p, Blocks.AIR.defaultBlockState());
                        }
                    }
                    // Zwischenböden
                    if (!wall && !(dx == 2 && dz == 0)) {
                        set(level, o.offset(dx, 5, dz), Blocks.SPRUCE_PLANKS.defaultBlockState());
                        set(level, o.offset(dx, 10, dz), Blocks.SPRUCE_PLANKS.defaultBlockState());
                    }
                }
            }
            // Leiter an der Ostwand
            for (int dy = 0; dy <= 11; dy++) {
                set(level, o.offset(2, dy, 0), Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST));
            }
            // Eingang & Schießscharten
            set(level, o.offset(0, 0, -3), Blocks.AIR.defaultBlockState());
            set(level, o.offset(0, 1, -3), Blocks.AIR.defaultBlockState());
            for (int dy : new int[]{3, 7, 12}) {
                set(level, o.offset(0, dy, 3), Blocks.AIR.defaultBlockState());
                set(level, o.offset(-3, dy, 0), Blocks.AIR.defaultBlockState());
            }
            spawner(level, o.offset(-1, 0, 1), ModEntities.RUIN_GUARDIAN.get());
            chest(level, rand, o.offset(-2, 11, -2), Direction.SOUTH, "watchtower");
            set(level, o.offset(-2, 11, 2), Blocks.LANTERN.defaultBlockState());
            set(level, o.offset(1, 6, 1), Blocks.COBWEB.defaultBlockState());
            set(level, o.offset(-2, 6, -1), Blocks.COBWEB.defaultBlockState());
            return true;
        }
    }

    // =====================================================================
    /** Kristallgeode: unterirdische Hohlkugel aus Basalt und Aetherkristall mit leuchtenden Kristallgruppen. */
    public static class CrystalGeode extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            int r = 5 + rand.nextInt(2);
            BlockPos o = new BlockPos(c.getX(), 12 + rand.nextInt(30), c.getZ());
            for (int dx = -r - 1; dx <= r + 1; dx++) {
                for (int dy = -r - 1; dy <= r + 1; dy++) {
                    for (int dz = -r - 1; dz <= r + 1; dz++) {
                        double d = Math.sqrt(dx * dx + dy * dy * 1.2 + dz * dz);
                        if (d > r + 0.5) continue;
                        BlockPos p = o.offset(dx, dy, dz);
                        if (d > r - 0.6) {
                            set(level, p, Blocks.BASALT.defaultBlockState());
                        } else if (d > r - 1.7) {
                            set(level, p, rand.nextInt(9) == 0
                                    ? ModBlocks.AETHERITE_ORE.get().defaultBlockState()
                                    : ModBlocks.AETHER_CRYSTAL_BLOCK.get().defaultBlockState());
                        } else {
                            set(level, p, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    for (int dy = -r; dy <= 0; dy++) {
                        BlockPos p = o.offset(dx, dy, dz);
                        if (level.isEmptyBlock(p) && level.getBlockState(p.below()).getBlock() == ModBlocks.AETHER_CRYSTAL_BLOCK.get()
                                && rand.nextInt(3) == 0) {
                            set(level, p, ModBlocks.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState());
                        }
                    }
                }
            }
            return true;
        }
    }

    // =====================================================================
    /** Katakombe: unterirdische Gruft mit Säulen, Spinnweben, Spawner und zwei Truhen. */
    public static class Catacomb extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            BlockPos o = new BlockPos(c.getX(), 15 + rand.nextInt(25), c.getZ());
            // nicht mitten in eine große Höhle bauen
            int air = 0;
            for (int dx = -5; dx <= 5; dx += 5) for (int dz = -5; dz <= 5; dz += 5) for (int dy = 0; dy <= 5; dy += 5)
                if (level.isEmptyBlock(o.offset(dx, dy, dz))) air++;
            if (air > 6) return false;

            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    for (int dy = 0; dy <= 5; dy++) {
                        BlockPos p = o.offset(dx, dy, dz);
                        boolean shell = Math.abs(dx) == 5 || Math.abs(dz) == 5 || dy == 0 || dy == 5;
                        set(level, p, shell ? ruinBrick(rand) : Blocks.AIR.defaultBlockState());
                    }
                }
            }
            for (int sx = -2; sx <= 2; sx += 4) {
                for (int sz = -2; sz <= 2; sz += 4) {
                    for (int dy = 1; dy <= 4; dy++) set(level, o.offset(sx, dy, sz), Blocks.CHISELED_STONE_BRICKS.defaultBlockState());
                }
            }
            for (int i = 0; i < 10; i++) {
                BlockPos p = o.offset(rand.nextInt(9) - 4, 4 - rand.nextInt(2), rand.nextInt(9) - 4);
                if (level.isEmptyBlock(p)) set(level, p, Blocks.COBWEB.defaultBlockState());
            }
            for (int i = 0; i < 3; i++) {
                BlockPos p = o.offset(rand.nextInt(9) - 4, 1, rand.nextInt(9) - 4);
                if (level.isEmptyBlock(p)) set(level, p, Blocks.SKELETON_SKULL.defaultBlockState()
                        .setValue(SkullBlock.ROTATION, rand.nextInt(16)));
            }
            set(level, o.offset(0, 4, 3), Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
            set(level, o.offset(0, 4, -3), Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
            spawner(level, o.offset(0, 1, 0), rand.nextBoolean() ? ModEntities.CRYSTAL_SKELETON.get() : ModEntities.RUIN_GUARDIAN.get());
            chest(level, rand, o.offset(-4, 1, 0), Direction.EAST, "catacomb");
            chest(level, rand, o.offset(4, 1, 0), Direction.WEST, "catacomb");
            return true;
        }
    }

    // =====================================================================
    /** Wächter-Heiligtum: runde Arena mit Säulenring und Altar zur Beschwörung des Aetherwächters. */
    public static class WardenSanctum extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            if (!suitable(level, c, 8, 4)) return false;
            int y0 = ground(level, c.getX(), c.getZ());
            BlockPos o = new BlockPos(c.getX(), y0, c.getZ());

            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -8; dz <= 8; dz++) {
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 8.4) continue;
                    BlockPos f = o.offset(dx, -1, dz);
                    boolean ring = d > 6.5;
                    set(level, f, ring ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState() : aetherBrick(rand));
                    foundation(level, f, Blocks.STONE_BRICKS.defaultBlockState(), 6);
                    for (int dy = 0; dy <= 8; dy++) set(level, o.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
                }
            }
            // Säulenring
            for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4;
                int px = (int) Math.round(Math.cos(a) * 7), pz = (int) Math.round(Math.sin(a) * 7);
                for (int dy = 0; dy < 5; dy++) set(level, o.offset(px, dy, pz), ModBlocks.AETHER_BRICKS.get().defaultBlockState());
                set(level, o.offset(px, 5, pz), ModBlocks.AETHER_LAMP.get().defaultBlockState());
            }
            // Podest + Altar
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    set(level, o.offset(dx, 0, dz), ModBlocks.AETHER_BRICK_SLAB.get().defaultBlockState()
                            .setValue(SlabBlock.TYPE, SlabType.BOTTOM));
                }
            }
            set(level, o, ModBlocks.AETHER_BRICKS.get().defaultBlockState());
            set(level, o.above(), ModBlocks.WARDEN_ALTAR.get().defaultBlockState());
            chest(level, rand, o.offset(0, 1, 3), Direction.NORTH, "warden_sanctum");
            return true;
        }
    }

    // =====================================================================
    /** Leuchtpilz-Flecken in dunklen Wäldern. */
    public static class GlowcapPatch extends AKFeature {
        @Override
        protected boolean generate(ISeedReader level, Random rand, BlockPos c) {
            int placed = 0;
            for (int i = 0; i < 10; i++) {
                int x = c.getX() + rand.nextInt(11) - 5;
                int z = c.getZ() + rand.nextInt(11) - 5;
                if (wet(level, x, z)) continue;
                BlockPos p = new BlockPos(x, ground(level, x, z), z);
                BlockState glow = ModBlocks.GLOWCAP.get().defaultBlockState();
                if (level.isEmptyBlock(p) && glow.canSurvive(level, p)) {
                    level.setBlock(p, glow, 2);
                    placed++;
                }
            }
            return placed > 0;
        }

        @Override
        public boolean place(ISeedReader level, net.minecraft.world.gen.ChunkGenerator gen, Random rand, BlockPos pos,
                             net.minecraft.world.gen.feature.NoFeatureConfig config) {
            return generate(level, rand, new BlockPos(pos.getX() + 8, 0, pos.getZ() + 8));
        }
    }
}
