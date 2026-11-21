package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Wachstumsstab: wirkt wie Knochenmehl auf einer 5x5-Fläche (3 Ebenen hoch), ohne Knochenmehl zu verbrauchen. */
public class GrowthStaffItem extends Item {
    public GrowthStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        World level = ctx.getLevel();
        PlayerEntity player = ctx.getPlayer();
        if (player == null) return ActionResultType.PASS;
        if (level.isClientSide) return ActionResultType.SUCCESS;

        BlockPos center = ctx.getClickedPos();
        int grown = 0;
        for (BlockPos p : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
            BlockPos pos = p.immutable();
            // Eigener Stack pro Block, da applyBonemeal ihn verkleinert
            if (BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, pos, player)) {
                level.levelEvent(2005, pos, 0); // grüne Knochenmehl-Partikel
                grown++;
            }
        }

        if (grown == 0) return ActionResultType.FAIL;

        level.playSound(null, center, SoundEvents.GRASS_PLACE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        Hand hand = ctx.getHand();
        ctx.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        player.getCooldowns().addCooldown(this, 10);
        return ActionResultType.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "growth_staff", 1);
    }
}
