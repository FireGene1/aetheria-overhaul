package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Fällt ganze Bäume auf einmal (nur echte Bäume mit natürlichem Laub). Logik in ToolEvents. */
public class LumberAxeItem extends AxeItem {
    public LumberAxeItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "lumber_axe", 2);
        Tooltips.hint(tooltip, "sneak_single");
    }
}
