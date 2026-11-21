package de.aetherkit.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TooltipBlockItem extends BlockItem {
    private final String key;
    private final int lines;

    public TooltipBlockItem(Block block, Properties properties, String key, int lines) {
        super(block, properties);
        this.key = key;
        this.lines = lines;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Tooltips.add(tooltip, key, lines);
    }
}
