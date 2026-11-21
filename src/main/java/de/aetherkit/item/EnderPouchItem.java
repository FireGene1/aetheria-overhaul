package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Öffnet die eigene Endertruhe von überall. */
public class EnderPouchItem extends Item {
    private static final ITextComponent TITLE = new TranslationTextComponent("container.enderchest");

    public EnderPouchItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            EnderChestInventory ender = player.getEnderChestInventory();
            player.openMenu(new SimpleNamedContainerProvider(
                    (id, inventory, p) -> ChestContainer.threeRows(id, inventory, ender), TITLE));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_CHEST_OPEN,
                    SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "ender_pouch", 1);
    }
}
