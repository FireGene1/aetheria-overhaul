package de.aetherkit.block;

import de.aetherkit.registry.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.CactusBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

/** Löst alle 2 Sekunden zusätzliche "Random Ticks" auf Pflanzen in der Umgebung aus. */
public class GrowthTotemTileEntity extends TileEntity implements ITickableTileEntity {
    private static final int RADIUS = 4;

    public GrowthTotemTileEntity() {
        super(ModTileEntities.GROWTH_TOTEM.get());
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide || level.getGameTime() % 40 != 0) return;
        ServerWorld server = (ServerWorld) level;
        for (int i = 0; i < 10; i++) {
            BlockPos p = worldPosition.offset(
                    level.random.nextInt(RADIUS * 2 + 1) - RADIUS,
                    level.random.nextInt(3) - 1,
                    level.random.nextInt(RADIUS * 2 + 1) - RADIUS);
            BlockState state = level.getBlockState(p);
            if (state.getBlock() instanceof IGrowable || state.getBlock() instanceof SugarCaneBlock
                    || state.getBlock() instanceof CactusBlock) {
                if (state.isRandomlyTicking()) {
                    state.randomTick(server, p, level.random);
                    server.sendParticles(ParticleTypes.HAPPY_VILLAGER, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5,
                            2, 0.25, 0.25, 0.25, 0.0);
                }
            }
        }
    }
}
