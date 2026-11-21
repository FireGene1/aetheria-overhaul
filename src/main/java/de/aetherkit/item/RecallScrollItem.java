package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Rückkehrschriftrolle: 2 Sekunden lesen -> Teleport zum Bett / Seelenanker,
 * sonst zum Weltspawn. Funktioniert dimensionsübergreifend.
 */
public class RecallScrollItem extends Item {
    public RecallScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        player.startUsingItem(hand);
        return ActionResult.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        World level = entity.level;
        if (level.isClientSide) {
            for (int i = 0; i < 2; i++) {
                level.addParticle(ParticleTypes.PORTAL,
                        entity.getX() + (level.random.nextDouble() - 0.5D) * 1.5D,
                        entity.getY() + level.random.nextDouble() * 2.0D,
                        entity.getZ() + (level.random.nextDouble() - 0.5D) * 1.5D,
                        0.0D, -0.5D, 0.0D);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayerEntity)) return stack;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        MinecraftServer server = player.getServer();
        if (server == null) return stack;

        ServerWorld target = null;
        Vector3d dest = null;

        // 1) Bett / Seelenanker
        BlockPos respawn = player.getRespawnPosition();
        ServerWorld respawnLevel = server.getLevel(player.getRespawnDimension());
        if (respawn != null && respawnLevel != null) {
            Optional<Vector3d> found = PlayerEntity.findRespawnPositionAndUseSpawnBlock(
                    respawnLevel, respawn, player.getRespawnAngle(), player.isRespawnForced(), true);
            if (found.isPresent()) {
                target = respawnLevel;
                dest = found.get();
            }
        }

        // 2) Weltspawn als Rückfall
        if (dest == null) {
            target = server.overworld();
            BlockPos spawn = target.getSharedSpawnPos();
            BlockPos top = target.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawn);
            dest = new Vector3d(top.getX() + 0.5D, top.getY(), top.getZ() + 0.5D);
            player.displayClientMessage(new TranslationTextComponent("message.aetherkit.recall_no_bed")
                    .withStyle(TextFormatting.YELLOW), true);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS, 1.0F, 1.0F);
        player.teleportTo(target, dest.x, dest.y, dest.z, player.yRot, player.xRot);
        target.playSound(null, dest.x, dest.y, dest.z, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.2F);
        player.fallDistance = 0.0F;

        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(this, 100);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "recall_scroll", 2);
    }
}
