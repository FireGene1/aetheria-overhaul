package de.aetherkit.block;

import de.aetherkit.registry.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Merkt sich alle geladenen Schutzlaternen pro Dimension, damit die Spawn-Prüfung schnell ist. */
public class WardingLanternTileEntity extends TileEntity {
    private static final Map<RegistryKey<World>, Set<BlockPos>> ACTIVE = new ConcurrentHashMap<>();

    public WardingLanternTileEntity() {
        super(ModTileEntities.WARDING_LANTERN.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            ACTIVE.computeIfAbsent(level.dimension(), k -> ConcurrentHashMap.newKeySet()).add(worldPosition.immutable());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        unregister();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unregister();
    }

    private void unregister() {
        if (level != null && !level.isClientSide) {
            Set<BlockPos> set = ACTIVE.get(level.dimension());
            if (set != null) set.remove(worldPosition);
        }
    }

    /** Liegt pos im Schutzradius irgendeiner Laterne? */
    public static boolean isProtected(RegistryKey<World> dim, BlockPos pos, int radius) {
        Set<BlockPos> set = ACTIVE.get(dim);
        if (set == null || set.isEmpty()) return false;
        long r2 = (long) radius * radius;
        for (BlockPos p : set) {
            long dx = p.getX() - pos.getX();
            long dy = p.getY() - pos.getY();
            long dz = p.getZ() - pos.getZ();
            if (dx * dx + dy * dy + dz * dz <= r2) return true;
        }
        return false;
    }
}
