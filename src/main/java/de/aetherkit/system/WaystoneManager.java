package de.aetherkit.system;

import de.aetherkit.network.AKNetwork;
import de.aetherkit.network.OpenWaystonesPacket;
import de.aetherkit.registry.ModBlocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/** Wegstein-Netzwerk: pro Spieler eine Liste entdeckter Wegsteine (überlebt den Tod). */
public final class WaystoneManager {
    private static final String KEY = "Waystones";
    private static final String[] PRE = {"Eld", "Mor", "Ash", "Vel", "Thar", "Sil", "Kor", "Ny", "Bra", "Lun", "Ost", "Gal", "Fen", "Ira", "Dun", "Wyr"};
    private static final String[] MID = {"a", "e", "i", "o", "u", "ae", "ei", "ar", "en", "or"};
    private static final String[] SUF = {"dor", "heim", "wyn", "mar", "thal", "grove", "fell", "ris", "stead", "moor", "ion", "vale"};

    private WaystoneManager() {}

    public static class Entry {
        public final String dim;
        public final BlockPos pos;
        public final String name;

        public Entry(String dim, BlockPos pos, String name) {
            this.dim = dim;
            this.pos = pos;
            this.name = name;
        }
    }

    /** Erzeugt einen festen, schönen Namen aus der Position. */
    public static String nameFor(BlockPos pos) {
        long h = pos.asLong() * 0x9E3779B97F4A7C15L;
        h ^= (h >>> 29);
        int a = (int) Math.floorMod(h, PRE.length);
        int b = (int) Math.floorMod(h >>> 8, MID.length);
        int c = (int) Math.floorMod(h >>> 16, SUF.length);
        return PRE[a] + MID[b] + SUF[c];
    }

    public static List<Entry> getEntries(ServerPlayerEntity player) {
        List<Entry> out = new ArrayList<>();
        ListNBT list = PlayerData.get(player).getList(KEY, 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT e = list.getCompound(i);
            out.add(new Entry(e.getString("Dim"), BlockPos.of(e.getLong("Pos")), e.getString("Name")));
        }
        return out;
    }

    private static void save(ServerPlayerEntity player, List<Entry> entries) {
        ListNBT list = new ListNBT();
        for (Entry e : entries) {
            CompoundNBT c = new CompoundNBT();
            c.putString("Dim", e.dim);
            c.putLong("Pos", e.pos.asLong());
            c.putString("Name", e.name);
            list.add(c);
        }
        PlayerData.get(player).put(KEY, list);
    }

    /** Rechtsklick auf einen Wegstein: entdecken (falls neu) und Reisemenü öffnen. */
    public static void activate(ServerPlayerEntity player, World level, BlockPos pos) {
        String dim = level.dimension().location().toString();
        List<Entry> entries = getEntries(player);
        boolean known = false;
        for (Entry e : entries) {
            if (e.dim.equals(dim) && e.pos.equals(pos)) { known = true; break; }
        }
        if (!known) {
            String name = nameFor(pos);
            entries.add(new Entry(dim, pos.immutable(), name));
            save(player, entries);
            player.displayClientMessage(new TranslationTextComponent("message.aetherkit.waystone_discovered", name)
                    .withStyle(TextFormatting.AQUA), false);
            level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.6F, 1.6F);
        }
        AKNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenWaystonesPacket(entries, dim, pos));
    }

    /** Teleport-Anfrage vom Client – wird serverseitig vollständig geprüft. */
    public static void teleport(ServerPlayerEntity player, String dim, BlockPos target) {
        if (player.getCooldowns().isOnCooldown(ModBlocks.WAYSTONE.get().asItem())) return;

        // Spieler muss an einem Wegstein stehen
        boolean nearWaystone = false;
        for (BlockPos p : BlockPos.betweenClosed(player.blockPosition().offset(-4, -2, -4), player.blockPosition().offset(4, 2, 4))) {
            if (player.level.getBlockState(p).getBlock() == ModBlocks.WAYSTONE.get()) { nearWaystone = true; break; }
        }
        if (!nearWaystone) return;

        List<Entry> entries = getEntries(player);
        Entry found = null;
        for (Entry e : entries) {
            if (e.dim.equals(dim) && e.pos.equals(target)) { found = e; break; }
        }
        if (found == null || player.getServer() == null) return;

        RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim));
        ServerWorld level = player.getServer().getLevel(key);
        if (level == null) return;

        if (level.getBlockState(target).getBlock() != ModBlocks.WAYSTONE.get()) {
            entries.remove(found);
            save(player, entries);
            player.displayClientMessage(new TranslationTextComponent("message.aetherkit.waystone_gone", found.name)
                    .withStyle(TextFormatting.RED), false);
            return;
        }

        BlockPos dest = findStandPos(level, target);
        ((ServerWorld) player.level).sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 40, 0.4, 0.8, 0.4, 0.4);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        player.teleportTo(level, dest.getX() + 0.5D, dest.getY(), dest.getZ() + 0.5D, player.yRot, player.xRot);
        level.playSound(null, dest, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.2F);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, dest.getX() + 0.5, dest.getY() + 1, dest.getZ() + 0.5, 40, 0.4, 0.8, 0.4, 0.05);
        player.getCooldowns().addCooldown(ModBlocks.WAYSTONE.get().asItem(), 60);
        player.fallDistance = 0;
    }

    /** Freier Platz neben dem Wegstein (2 Blöcke Luft, fester Boden). */
    private static BlockPos findStandPos(ServerWorld level, BlockPos stone) {
        BlockPos[] options = {stone.north(), stone.south(), stone.east(), stone.west(),
                stone.north().east(), stone.south().west(), stone.north().west(), stone.south().east()};
        for (BlockPos p : options) {
            for (int dy = -1; dy <= 1; dy++) {
                BlockPos q = p.above(dy);
                if (level.isEmptyBlock(q) && level.isEmptyBlock(q.above())
                        && level.getBlockState(q.below()).getMaterial().isSolid()) {
                    return q;
                }
            }
        }
        return stone.above();
    }
}
