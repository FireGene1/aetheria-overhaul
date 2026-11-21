package de.aetherkit.block;

import de.aetherkit.registry.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/** Grab: entsteht beim Tod und bewahrt das komplette Inventar auf. Rechtsklick = alles zurück. */
public class GraveBlock extends HorizontalBlock {
    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D),
            Block.box(3.0D, 2.0D, 6.0D, 13.0D, 14.0D, 10.0D));

    public GraveBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, net.minecraft.util.Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.GRAVE.get().create();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (level.isClientSide) return ActionResultType.SUCCESS;
        TileEntity te = level.getBlockEntity(pos);
        if (!(te instanceof GraveTileEntity)) return ActionResultType.PASS;
        GraveTileEntity grave = (GraveTileEntity) te;

        if (!grave.canOpen(player)) {
            player.displayClientMessage(new TranslationTextComponent("message.aetherkit.grave_not_yours", grave.getOwnerName())
                    .withStyle(TextFormatting.RED), true);
            return ActionResultType.FAIL;
        }
        for (ItemStack stack : grave.takeAll()) {
            if (!player.inventory.add(stack)) {
                player.drop(stack, false);
            }
        }
        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, 1.0F, 0.8F);
        level.removeBlock(pos, false);
        player.displayClientMessage(new TranslationTextComponent("message.aetherkit.grave_recovered").withStyle(TextFormatting.GREEN), true);
        return ActionResultType.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean moving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity te = level.getBlockEntity(pos);
            if (te instanceof GraveTileEntity) {
                InventoryHelper.dropContents(level, pos, ((GraveTileEntity) te).takeAll());
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }
}
