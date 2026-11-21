package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Tragbare Werkbank: 3x3-Crafting überall. */
public class PocketCrafterItem extends Item {
    private static final ITextComponent TITLE = new TranslationTextComponent("container.crafting");

    public PocketCrafterItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.openMenu(new SimpleNamedContainerProvider(
                    (id, inventory, p) -> new PortableWorkbench(id, inventory, IWorldPosCallable.create(level, p.blockPosition())),
                    TITLE));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WOOD_PLACE,
                    SoundCategory.PLAYERS, 0.5F, 1.2F);
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "pocket_crafter", 1);
    }

    /** Werkbank-Container, der sich nicht schliesst, nur weil kein Werkbank-Block in der Nähe ist. */
    public static class PortableWorkbench extends WorkbenchContainer {
        public PortableWorkbench(int id, PlayerInventory inventory, IWorldPosCallable access) {
            super(id, inventory, access);
        }

        @Override
        public boolean stillValid(PlayerEntity player) {
            return true;
        }
    }
}
