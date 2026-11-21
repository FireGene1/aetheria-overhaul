package de.aetherkit.item;

import de.aetherkit.system.PlayerData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

/** Herzkristall: erhöht die maximale Gesundheit dauerhaft um 1 Herz (max. +10 Herzen). Bleibt nach dem Tod erhalten. */
public class HeartCrystalItem extends Item {
    public HeartCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return ActionResult.success(stack);

        int hearts = PlayerData.getBonusHearts(player);
        if (hearts >= PlayerData.MAX_BONUS_HEARTS) {
            player.displayClientMessage(new TranslationTextComponent("message.aetherkit.hearts_max").withStyle(TextFormatting.GRAY), true);
            return ActionResult.fail(stack);
        }
        PlayerData.setBonusHearts(player, hearts + 1);
        player.heal(2.0F);
        if (!player.abilities.instabuild) stack.shrink(1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8F, 1.4F);
        ((ServerWorld) level).sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(), 8, 0.5, 0.5, 0.5, 0.1);
        player.displayClientMessage(new TranslationTextComponent("message.aetherkit.hearts_up", hearts + 1, PlayerData.MAX_BONUS_HEARTS)
                .withStyle(TextFormatting.RED), true);
        return ActionResult.consume(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "heart_crystal", 1);
    }
}
