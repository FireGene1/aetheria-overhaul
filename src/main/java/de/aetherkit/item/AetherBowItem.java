package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Aetherbogen: +60 % Pfeilschaden, getroffene Ziele sind durch leuchtende Pfeile markiert. */
public class AetherBowItem extends BowItem {
    public AetherBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
        arrow.setBaseDamage(arrow.getBaseDamage() * 1.6D);
        arrow.setGlowing(true);
        return arrow;
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, "aether_bow", 1);
    }
}
