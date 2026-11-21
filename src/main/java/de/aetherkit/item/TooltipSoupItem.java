package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SoupItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Eintopf mit Tooltip – gibt die Schüssel zurück. */
public class TooltipSoupItem extends SoupItem {
    private final String key;

    public TooltipSoupItem(Properties properties, String key) {
        super(properties);
        this.key = key;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, key, 1);
    }
}
