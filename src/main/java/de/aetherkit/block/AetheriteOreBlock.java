package de.aetherkit.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

/** Erz-Block: gibt Erfahrung und erzeugt passende Partikel (Funkeln, Glut, Leere). */
public class AetheriteOreBlock extends Block {
    private static final Random XP_RANDOM = new Random();
    private final Supplier<IParticleData> particle;
    private final int minXp;
    private final int maxXp;

    public AetheriteOreBlock(Properties properties, Supplier<IParticleData> particle, int minXp, int maxXp) {
        super(properties);
        this.particle = particle;
        this.minXp = minXp;
        this.maxXp = maxXp;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? minXp + XP_RANDOM.nextInt(maxXp - minXp + 1) : 0;
    }

    @Override
    public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
        if (rand.nextInt(6) != 0) return;
        double x = pos.getX() + rand.nextDouble();
        double y = pos.getY() + rand.nextDouble();
        double z = pos.getZ() + rand.nextDouble();
        switch (rand.nextInt(6)) {
            case 0: x = pos.getX() - 0.05; break;
            case 1: x = pos.getX() + 1.05; break;
            case 2: y = pos.getY() - 0.05; break;
            case 3: y = pos.getY() + 1.05; break;
            case 4: z = pos.getZ() - 0.05; break;
            default: z = pos.getZ() + 1.05; break;
        }
        level.addParticle(particle.get(), x, y, z, 0.0D, 0.01D, 0.0D);
    }
}
