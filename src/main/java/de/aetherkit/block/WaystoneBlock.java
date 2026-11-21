package de.aetherkit.block;

import de.aetherkit.system.WaystoneManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/** Wegstein: Rechtsklick entdeckt ihn und öffnet die Reiseliste aller entdeckten Wegsteine. */
public class WaystoneBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public WaystoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayerEntity) {
            WaystoneManager.activate((ServerPlayerEntity) player, level, pos);
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
        if (rand.nextInt(2) == 0) {
            level.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5,
                    (rand.nextDouble() - 0.5) * 1.5, -rand.nextDouble() * 0.8, (rand.nextDouble() - 0.5) * 1.5);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("tooltip.aetherkit.waystone.0").withStyle(TextFormatting.GRAY));
    }
}
