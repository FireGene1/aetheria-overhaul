package de.aetherkit.loot;

import com.google.gson.JsonObject;
import de.aetherkit.item.SpecialTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** Schmilzt Block-Drops sofort, wenn mit einem IAutoSmelt-Werkzeug (Emberit-Spitzhacke) abgebaut wird. */
public class AutoSmeltModifier extends LootModifier {
    public AutoSmeltModifier(ILootCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> loot, LootContext ctx) {
        ItemStack tool = ctx.getParamOrNull(LootParameters.TOOL);
        if (tool == null || !(tool.getItem() instanceof SpecialTools.IAutoSmelt)) return loot;
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) return loot;
        World level = ctx.getLevel();
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack stack : loot) {
            out.add(level.getRecipeManager()
                    .getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), level)
                    .map(r -> r.getResultItem())
                    .filter(r -> !r.isEmpty())
                    .map(r -> {
                        ItemStack copy = r.copy();
                        copy.setCount(Math.min(copy.getMaxStackSize(), stack.getCount() * r.getCount()));
                        return copy;
                    })
                    .orElse(stack));
        }
        return out;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AutoSmeltModifier> {
        @Override
        public AutoSmeltModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new AutoSmeltModifier(conditions);
        }

        @Override
        public JsonObject write(AutoSmeltModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
