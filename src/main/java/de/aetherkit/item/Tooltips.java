package de.aetherkit.item;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

/** Hilfsklasse: fügt Tooltip-Zeilen "tooltip.aetherkit.<key>.0", ".1", ... hinzu. */
public final class Tooltips {
    private Tooltips() {}

    public static void add(List<ITextComponent> list, String key, int lines) {
        for (int i = 0; i < lines; i++) {
            list.add(new TranslationTextComponent("tooltip.aetherkit." + key + "." + i).withStyle(TextFormatting.GRAY));
        }
    }

    public static void hint(List<ITextComponent> list, String key) {
        list.add(new TranslationTextComponent("tooltip.aetherkit." + key).withStyle(TextFormatting.DARK_AQUA, TextFormatting.ITALIC));
    }
}
