package de.aetherkit.system;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Sortiert eine offene Truhe oder (ohne Truhe) das Hauptinventar des Spielers (ohne Hotbar). */
public final class InventorySorter {
    private InventorySorter() {}

    public static void sort(ServerPlayerEntity player) {
        Container menu = player.containerMenu;
        if (menu instanceof ChestContainer) {
            IInventory inv = ((ChestContainer) menu).getContainer();
            sortRange(inv, 0, inv.getContainerSize());
        } else {
            sortRange(player.inventory, 9, 36);
        }
        menu.broadcastChanges();
    }

    private static void sortRange(IInventory inv, int from, int to) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = from; i < to; i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty()) stacks.add(s.copy());
        }
        // Zusammenführen
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack s : stacks) {
            for (ItemStack m : merged) {
                if (s.isEmpty()) break;
                if (ItemStack.isSame(m, s) && ItemStack.tagMatches(m, s) && m.getCount() < m.getMaxStackSize()) {
                    int move = Math.min(s.getCount(), m.getMaxStackSize() - m.getCount());
                    m.grow(move);
                    s.shrink(move);
                }
            }
            if (!s.isEmpty()) merged.add(s);
        }
        merged.sort(Comparator
                .comparing((ItemStack s) -> String.valueOf(ForgeRegistries.ITEMS.getKey(s.getItem())))
                .thenComparing(s -> -s.getCount()));
        for (int i = from; i < to; i++) {
            int idx = i - from;
            inv.setItem(i, idx < merged.size() ? merged.get(idx) : ItemStack.EMPTY);
        }
        inv.setChanged();
    }
}
