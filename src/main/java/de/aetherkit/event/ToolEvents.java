package de.aetherkit.event;

import de.aetherkit.AKConfig;
import de.aetherkit.item.IAreaTool;
import de.aetherkit.item.LumberAxeItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

/**
 * 3x3-Abbau für Hammer/Bagger und Baumfällen für die Holzfälleraxt.
 * Läuft über BlockEvent.BreakEvent, damit Schutz-Mods (Claims, Spawn-Schutz) respektiert werden:
 * Jeder Zusatzblock wird über den normalen Abbau-Weg des Spielers zerstört.
 */
public class ToolEvents {
    /** Verhindert Rekursion, weil destroyBlock selbst wieder ein BreakEvent auslöst. */
    private static boolean busy = false;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (busy || event.isCanceled()) return;
        PlayerEntity player = event.getPlayer();
        if (!(player instanceof ServerPlayerEntity) || !(event.getWorld() instanceof ServerWorld)) return;
        if (player.isCreative()) return;

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ServerWorld level = (ServerWorld) event.getWorld();
        ItemStack tool = player.getMainHandItem();
        Item item = tool.getItem();

        busy = true;
        try {
            if (player.isShiftKeyDown()) {
                // Schleichen: Area-Tools bauen nur einen Block ab, Spitzhacken bauen ganze Erzadern ab
                if (AKConfig.VEIN_MINING.get() && event.getState().is(Tags.Blocks.ORES)
                        && item.getToolTypes(tool).contains(ToolType.PICKAXE)
                        && ForgeHooks.canHarvestBlock(event.getState(), player, level, event.getPos())) {
                    veinMine(level, serverPlayer, event.getPos(), event.getState(), tool);
                }
            } else if (item instanceof IAreaTool) {
                mineArea(level, serverPlayer, event.getPos(), event.getState(), tool);
            } else if (item instanceof LumberAxeItem && event.getState().is(BlockTags.LOGS)) {
                fellTree(level, serverPlayer, event.getPos(), event.getState(), tool);
            }
        } finally {
            busy = false;
        }
    }

    // ------------------------------------------------------------------ 3x3

    private static void mineArea(ServerWorld level, ServerPlayerEntity player, BlockPos origin, BlockState originState, ItemStack tool) {
        if (tool.getDestroySpeed(originState) <= 1.0F) return; // nur wenn das Werkzeug am Zentrum effektiv ist
        Direction face = lookedAtFace(level, player);
        if (face == null) return;

        float originHardness = originState.getDestroySpeed(level, origin);
        Direction.Axis axis = face.getAxis();

        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) continue;
                BlockPos pos;
                switch (axis) {
                    case X: pos = origin.offset(0, a, b); break;
                    case Y: pos = origin.offset(a, 0, b); break;
                    default: pos = origin.offset(a, b, 0); break;
                }
                BlockState state = level.getBlockState(pos);
                if (state.isAir()) continue;
                float hardness = state.getDestroySpeed(level, pos);
                // Kein Grundgestein, nichts deutlich Härteres als der Zentrumsblock (z.B. Obsidian neben Stein)
                if (hardness < 0 || hardness > originHardness + 3.0F) continue;
                if (tool.getDestroySpeed(state) <= 1.0F) continue;
                if (!ForgeHooks.canHarvestBlock(state, player, level, pos)) continue;

                player.gameMode.destroyBlock(pos);
                if (tool.isEmpty()) return; // Werkzeug kaputt
            }
        }
    }

    /** Welche Seite des Blocks schaut der Spieler an? (bestimmt die Ebene des 3x3) */
    private static Direction lookedAtFace(ServerWorld level, PlayerEntity player) {
        Vector3d eye = new Vector3d(player.getX(), player.getEyeY(), player.getZ());
        Vector3d end = eye.add(player.getLookAngle().scale(7.0D));
        BlockRayTraceResult hit = level.clip(new RayTraceContext(eye, end,
                RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        return hit.getType() == RayTraceResult.Type.BLOCK ? hit.getDirection() : null;
    }

    // ------------------------------------------------------------------ Vein-Mining

    private static void veinMine(ServerWorld level, ServerPlayerEntity player, BlockPos origin, BlockState originState, ItemStack tool) {
        Block ore = originState.getBlock();
        int max = AKConfig.VEIN_MAX.get();
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> found = new ArrayList<>();
        visited.add(origin);
        queue.add(origin);
        while (!queue.isEmpty() && found.size() < max) {
            BlockPos current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos n = current.offset(dx, dy, dz);
                        if (!visited.add(n)) continue;
                        if (level.getBlockState(n).getBlock() == ore) {
                            found.add(n);
                            queue.add(n);
                            if (found.size() >= max) break;
                        }
                    }
                }
            }
        }
        for (BlockPos p : found) {
            if (tool.isEmpty()) return;
            player.gameMode.destroyBlock(p);
        }
    }

    // ------------------------------------------------------------------ Baumfällen

    private static void fellTree(ServerWorld level, ServerPlayerEntity player, BlockPos origin, BlockState originState, ItemStack tool) {
        Block logBlock = originState.getBlock();
        int max = AKConfig.TREE_MAX_LOGS.get();

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> logs = new ArrayList<>();
        boolean naturalLeaves = false;

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && logs.size() < max) {
            BlockPos current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos n = current.offset(dx, dy, dz);
                        if (n.getY() < origin.getY() || !visited.add(n)) continue;
                        BlockState s = level.getBlockState(n);
                        if (s.getBlock() == logBlock) {
                            logs.add(n);
                            queue.add(n);
                        } else if (!naturalLeaves && s.is(BlockTags.LEAVES)) {
                            naturalLeaves = !(s.getBlock() instanceof LeavesBlock) || !s.getValue(LeavesBlock.PERSISTENT);
                        }
                    }
                }
            }
        }

        // Ohne natürliches Laub ist es vermutlich ein Haus/eine Holzkonstruktion -> nur normaler Abbau
        if (!naturalLeaves) return;

        logs.sort(Comparator.comparingInt((BlockPos p) -> p.getY()).reversed());
        for (BlockPos p : logs) {
            if (tool.isEmpty()) return;
            player.gameMode.destroyBlock(p);
        }
    }
}
