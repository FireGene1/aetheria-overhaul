package de.aetherkit.item;

import de.aetherkit.registry.ModArmorMaterial;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/** Rüstung aller drei Materialien. Set-Boni werden in QolEvents angewendet. */
public class ModArmorItem extends ArmorItem {
    private final ModArmorMaterial mat;

    public ModArmorItem(ModArmorMaterial material, EquipmentSlotType slot, Properties properties) {
        super(material, slot, properties);
        this.mat = material;
    }

    public ModArmorMaterial getMat() {
        return mat;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        String m = mat.name().toLowerCase(java.util.Locale.ROOT);
        if (getSlot() == EquipmentSlotType.FEET && mat == ModArmorMaterial.AETHERITE) {
            Tooltips.add(tooltip, "aetherite_boots", 1);
        }
        Tooltips.hint(tooltip, "armor_set_" + m);
    }
}
