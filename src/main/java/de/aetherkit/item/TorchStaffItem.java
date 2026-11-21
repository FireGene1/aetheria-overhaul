package de.aetherkit.item;

import de.aetherkit.AKConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Fackelstab: setzt Fackeln ohne Fackeln im Inventar (kostet Haltbarkeit).
 * Schleichen + Rechtsklick in die Luft: beleuchtet automatisch dunkle Stellen in der Umgebung.
 */
public class TorchStaffItem extends Item {
    private static final int AUTO_RADIUS = 10;
    private static final int MIN_SPACING_SQ = 6 * 6;

    public TorchStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        World level = ctx.getLevel();
        Direction face = ctx.getClickedFace();
        if (face == Direction.DOWN) return ActionResultType.FAIL;

        BlockPos pos = ctx.getClickedPos().relative(face);
        BlockState torch = face == Direction.UP
                ? Blocks.TORCH.defaultBlockState()
                : Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, face);

        if (!canPlaceAt(level, pos, torch)) return ActionResultType.FAIL;

        if (!level.isClientSide) {
            level.setBlock(pos, torch, 3);
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                Hand hand = ctx.getHand();
                ctx.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) return ActionResult.pass(stack);

        if (!level.isClientSide) {
            int placed = autoLight(level, player, stack, hand);
            player.displayClientMessage(new TranslationTextComponent("message.aetherkit.torches_placed", placed)
                    .withStyle(placed > 0 ? TextFormatting.GOLD : TextFormatting.GRAY), true);
            player.getCooldowns().addCooldown(this, 30);
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    /** Sucht dunkle, begehbare Stellen und setzt Fackeln mit Mindestabstand. */
    private int autoLight(World level, PlayerEntity player, ItemStack stack, Hand hand) {
        BlockPos center = player.blockPosition();
        List<BlockPos> candidates = new ArrayList<>();
        BlockState torch = Blocks.TORCH.defaultBlockState();

        for (BlockPos p : BlockPos.betweenClosed(center.offset(-AUTO_RADIUS, -4, -AUTO_RADIUS),
                center.offset(AUTO_RADIUS, 4, AUTO_RADIUS))) {
            if (!level.isEmptyBlock(p)) continue;
            if (level.getBrightness(LightType.BLOCK, p) > 6) continue;
            BlockPos immutable = p.immutable();
            if (!torch.canSurvive(level, immutable)) continue;
            candidates.add(immutable);
        }

        // Nächste Stellen zuerst
        candidates.sort(Comparator.comparingDouble(p -> distSq(p, center)));

        List<BlockPos> placed = new ArrayList<>();
        int max = AKConfig.AUTO_TORCH_MAX.get();
        for (BlockPos p : candidates) {
            if (placed.size() >= max || stack.isEmpty()) break;
            boolean tooClose = false;
            for (BlockPos q : placed) {
                if (distSq(p, q) < MIN_SPACING_SQ) { tooClose = true; break; }
            }
            if (tooClose) continue;
            level.setBlock(p, torch, 3);
            placed.add(p);
            stack.hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(hand));
        }
        if (!placed.isEmpty()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE,
                    SoundCategory.PLAYERS, 0.4F, 1.6F);
        }
        return placed.size();
    }

    private static boolean canPlaceAt(World level, BlockPos pos, BlockState torch) {
        BlockState existing = level.getBlockState(pos);
        return existing.getMaterial().isReplaceable()
                && level.getFluidState(pos).isEmpty()
                && torch.canSurvive(level, pos);
    }

    private static double distSq(BlockPos a, BlockPos b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "torch_staff", 2);
    }
}
