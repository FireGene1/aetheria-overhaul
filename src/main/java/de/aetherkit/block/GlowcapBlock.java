package de.aetherkit.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Random;

/** Leuchtpilz: natürliche Lichtquelle in Wäldern, Zutat für Leuchtpilzeintopf. */
public class GlowcapBlock extends BushBlock {
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);

    public GlowcapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
        if (rand.nextInt(8) == 0) {
            level.addParticle(ParticleTypes.MYCELIUM, pos.getX() + rand.nextDouble(), pos.getY() + 0.6,
                    pos.getZ() + rand.nextDouble(), 0, 0, 0);
        }
    }
}
