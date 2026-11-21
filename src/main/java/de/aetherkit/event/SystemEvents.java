package de.aetherkit.event;

import de.aetherkit.AKConfig;
import de.aetherkit.block.GraveTileEntity;
import de.aetherkit.block.WardingLanternTileEntity;
import de.aetherkit.registry.ModBlocks;
import de.aetherkit.system.PlayerData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Gräber, Aufzug, Schutzlaterne, Herzkristalle. */
public class SystemEvents {
    private final Map<UUID, Boolean> wasSneaking = new HashMap<>();

    // ------------------------------------------------------------------ Gräber

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDrops(LivingDropsEvent event) {
        if (!AKConfig.GRAVES.get() || event.isCanceled()) return;
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        World level = player.level;
        if (level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || event.getDrops().isEmpty()) return;

        BlockPos pos = findGravePos(level, player.blockPosition());
        if (pos == null) return;

        List<ItemStack> items = new ArrayList<>();
        for (ItemEntity e : event.getDrops()) items.add(e.getItem());

        BlockState state = ModBlocks.GRAVE.get().defaultBlockState()
                .setValue(net.minecraft.block.HorizontalBlock.FACING, player.getDirection().getOpposite());
        level.setBlock(pos, state, 3);
        TileEntity te = level.getBlockEntity(pos);
        if (!(te instanceof GraveTileEntity)) return; // Fallback: normale Drops
        ((GraveTileEntity) te).fill(player, items);
        event.setCanceled(true);
        player.displayClientMessage(new TranslationTextComponent("message.aetherkit.grave_created",
                pos.getX(), pos.getY(), pos.getZ()).withStyle(TextFormatting.GOLD), false);
    }

    private static BlockPos findGravePos(World level, BlockPos start) {
        int y = Math.max(1, Math.min(level.getMaxBuildHeight() - 2, start.getY()));
        BlockPos base = new BlockPos(start.getX(), y, start.getZ());
        for (int r = 0; r <= 3; r++) {
            for (int dy = 0; dy <= 8; dy++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        BlockPos p = base.offset(dx, dy, dz);
                        if (p.getY() >= level.getMaxBuildHeight() - 1) continue;
                        BlockState s = level.getBlockState(p);
                        if ((s.isAir() || s.getMaterial().isReplaceable()) && level.getBlockEntity(p) == null) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ Aufzug

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        BlockPos below = player.blockPosition().below();
        if (player.level.getBlockState(below).getBlock() != ModBlocks.ELEVATOR.get()) return;
        int range = AKConfig.ELEVATOR_RANGE.get();
        for (int i = 2; i <= range; i++) {
            BlockPos p = below.above(i);
            if (isElevatorStop(player.level, p)) {
                elevate(player, p);
                return;
            }
        }
    }

    private void checkElevatorDown(ServerPlayerEntity player) {
        boolean sneaking = player.isShiftKeyDown();
        Boolean before = wasSneaking.put(player.getUUID(), sneaking);
        if (!sneaking || Boolean.TRUE.equals(before) || !player.isOnGround()) return;
        BlockPos below = player.blockPosition().below();
        if (player.level.getBlockState(below).getBlock() != ModBlocks.ELEVATOR.get()) return;
        int range = AKConfig.ELEVATOR_RANGE.get();
        for (int i = 2; i <= range; i++) {
            BlockPos p = below.below(i);
            if (p.getY() < 0) return;
            if (isElevatorStop(player.level, p)) {
                elevate(player, p);
                return;
            }
        }
    }

    private static boolean isElevatorStop(World level, BlockPos p) {
        return level.getBlockState(p).getBlock() == ModBlocks.ELEVATOR.get()
                && level.isEmptyBlock(p.above()) && level.isEmptyBlock(p.above(2));
    }

    private static void elevate(ServerPlayerEntity player, BlockPos elevator) {
        player.teleportTo(player.getX(), elevator.getY() + 1.0D, player.getZ());
        player.fallDistance = 0;
        player.level.playSound(null, elevator, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 0.4F, 1.8F);
    }

    // ------------------------------------------------------------------ Spieler-Tick

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayerEntity) {
            checkElevatorDown((ServerPlayerEntity) event.player);
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        wasSneaking.remove(event.getPlayer().getUUID());
    }

    // ------------------------------------------------------------------ Schutzlaterne

    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.isSpawner() || !(event.getEntity() instanceof IMob)) return;
        if (!(event.getWorld() instanceof World)) return;
        World level = (World) event.getWorld();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (WardingLanternTileEntity.isProtected(level.dimension(), pos, AKConfig.WARDING_RADIUS.get())) {
            event.setResult(Event.Result.DENY);
        }
    }

    // ------------------------------------------------------------------ Herzkristalle

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerData.applyHearts(event.getPlayer());
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        PlayerData.applyHearts(player);
        player.setHealth(player.getMaxHealth());
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerData.applyHearts(event.getPlayer());
    }
}
