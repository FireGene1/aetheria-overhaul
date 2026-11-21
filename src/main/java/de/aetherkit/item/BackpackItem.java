package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Rucksack: 27 (normal) oder 54 (groß) Plätze, Inhalt wird im Item gespeichert. */
public class BackpackItem extends Item {
    private final int rows;

    public BackpackItem(int rows, Properties properties) {
        super(properties);
        this.rows = rows;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            int lockedSlot = hand == Hand.MAIN_HAND ? player.inventory.selected : -1;
            player.openMenu(new SimpleNamedContainerProvider(
                    (id, inv, p) -> new BackpackContainer(id, inv, new BackpackInventory(stack, rows * 9), rows, stack, lockedSlot),
                    stack.getHoverName()));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER,
                    SoundCategory.PLAYERS, 0.7F, 1.0F);
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, rows == 6 ? "large_backpack" : "backpack", 1);
    }

    /** Inventar, das seinen Inhalt im NBT des Rucksack-Items speichert. */
    public static class BackpackInventory extends Inventory {
        private final ItemStack backpack;

        public BackpackInventory(ItemStack backpack, int size) {
            super(size);
            this.backpack = backpack;
            CompoundNBT tag = backpack.getTag();
            if (tag != null && tag.contains("Items")) {
                NonNullList<ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(tag, list);
                for (int i = 0; i < size; i++) setItem(i, list.get(i));
            }
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            save();
        }

        @Override
        public void stopOpen(PlayerEntity player) {
            super.stopOpen(player);
            save();
        }

        private void save() {
            NonNullList<ItemStack> list = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < getContainerSize(); i++) list.set(i, getItem(i));
            ItemStackHelper.saveAllItems(backpack.getOrCreateTag(), list, true);
        }
    }

    /** Truhen-GUI, die verhindert, dass der Rucksack in sich selbst gelegt oder verschoben wird. */
    public static class BackpackContainer extends ChestContainer {
        private final ItemStack backpack;
        private final int lockedSlot;

        public BackpackContainer(int id, PlayerInventory inv, BackpackInventory container, int rows, ItemStack backpack, int lockedSlot) {
            super(rows == 6 ? ContainerType.GENERIC_9x6 : ContainerType.GENERIC_9x3, id, inv, container, rows);
            this.backpack = backpack;
            this.lockedSlot = lockedSlot;
        }

        @Override
        public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
            if (slotId >= 0 && slotId < slots.size() && slots.get(slotId).getItem() == backpack) {
                return ItemStack.EMPTY;
            }
            if (clickType == ClickType.SWAP && dragType == lockedSlot) {
                return ItemStack.EMPTY;
            }
            return super.clicked(slotId, dragType, clickType, player);
        }

        @Override
        public boolean stillValid(PlayerEntity player) {
            return !backpack.isEmpty() && (player.getMainHandItem() == backpack || player.getOffhandItem() == backpack);
        }
    }
}
