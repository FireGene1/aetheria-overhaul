package de.aetherkit.block;

import de.aetherkit.entity.AetherWardenEntity;
import de.aetherkit.registry.ModEntities;
import de.aetherkit.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/** Wächter-Altar: Wächter-Siegel darauf benutzen, um den Boss zu beschwören. */
public class WardenAltarBlock extends Block {
    public WardenAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() != ModItems.WARDEN_SIGIL.get()) {
            if (!level.isClientSide) {
                player.displayClientMessage(new TranslationTextComponent("message.aetherkit.altar_hint")
                        .withStyle(TextFormatting.DARK_PURPLE), true);
            }
            return ActionResultType.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) return ActionResultType.SUCCESS;

        ServerWorld server = (ServerWorld) level;
        AetherWardenEntity boss = ModEntities.AETHER_WARDEN.get().create(server);
        if (boss == null) return ActionResultType.FAIL;
        boss.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, player.yRot + 180.0F, 0.0F);
        boss.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), SpawnReason.TRIGGERED, null, null);
        server.addFreshEntity(boss);

        LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(server);
        if (bolt != null) {
            bolt.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            bolt.setVisualOnly(true);
            server.addFreshEntity(bolt);
        }
        if (!player.abilities.instabuild) held.shrink(1);
        server.getPlayers(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64).forEach(p ->
                p.displayClientMessage(new TranslationTextComponent("message.aetherkit.warden_awakens")
                        .withStyle(TextFormatting.DARK_AQUA, TextFormatting.BOLD), false));
        return ActionResultType.CONSUME;
    }

    @Override
    public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
        level.addParticle(ParticleTypes.PORTAL, pos.getX() + rand.nextDouble(), pos.getY() + 1.1, pos.getZ() + rand.nextDouble(),
                0, 0.2, 0);
    }
}
