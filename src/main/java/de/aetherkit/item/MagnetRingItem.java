package de.aetherkit.item;

import de.aetherkit.AKConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Magnetring: zieht Items und Erfahrungskugeln im Umkreis an, solange er irgendwo im Inventar ist.
 * Rechtsklick schaltet ihn an/aus, Schleichen pausiert ihn (zum Wegwerfen von Items).
 */
public class MagnetRingItem extends Item {
    private static final String TAG_ACTIVE = "Active";

    public MagnetRingItem(Properties properties) {
        super(properties);
    }

    public static boolean isActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(TAG_ACTIVE);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean active = !isActive(stack);
            stack.getOrCreateTag().putBoolean(TAG_ACTIVE, active);
            player.displayClientMessage(new TranslationTextComponent(active ? "message.aetherkit.magnet_on" : "message.aetherkit.magnet_off")
                    .withStyle(active ? TextFormatting.AQUA : TextFormatting.GRAY), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundCategory.PLAYERS, 0.4F, active ? 1.4F : 0.6F);
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !isActive(stack) || !(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entity;
        if (player.isSpectator() || player.isShiftKeyDown()) return;

        double range = AKConfig.MAGNET_RANGE.get();
        AxisAlignedBB box = player.getBoundingBox().inflate(range);
        double x = player.getX();
        double y = player.getY() + 0.5D;
        double z = player.getZ();

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box,
                e -> e.isAlive() && !e.getPersistentData().getBoolean("PreventRemoteMovement"));
        for (ItemEntity item : items) {
            item.setPos(x, y, z);
        }
        List<ExperienceOrbEntity> orbs = level.getEntitiesOfClass(ExperienceOrbEntity.class, box, Entity::isAlive);
        for (ExperienceOrbEntity orb : orbs) {
            orb.setPos(x, y, z);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(isActive(stack) ? "tooltip.aetherkit.state_on" : "tooltip.aetherkit.state_off")
                .withStyle(isActive(stack) ? TextFormatting.GREEN : TextFormatting.RED));
        Tooltips.add(tooltip, "magnet_ring", 2);
    }
}
