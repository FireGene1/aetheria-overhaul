package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Einfaches Item mit einer Beschreibungszeile und optionalem Verzauberungsglanz. */
public class TooltipItem extends Item {
    private final boolean foil;
    private final String key;

    public TooltipItem(Properties properties, boolean foil, String key) {
        super(properties);
        this.foil = foil;
        this.key = key;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foil || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, key, 1);
    }
}
