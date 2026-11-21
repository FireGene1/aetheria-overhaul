package de.aetherkit.registry;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

/**
 * Werkzeug-Stufen (Fortschritt):
 * Aetherit (Oberwelt) -> Emberit (Nether) -> Voidium (Ende) ; Wächter = Boss-Waffe
 */
public enum ModTiers implements IItemTier {
    AETHERITE(3, 1400, 8.5F, 3.0F, 20, () -> Ingredient.of(ModItems.AETHERITE_SHARD.get())),
    EMBERITE(4, 2200, 9.5F, 4.0F, 18, () -> Ingredient.of(ModItems.EMBERITE_INGOT.get())),
    VOIDIUM(5, 3600, 12.0F, 5.0F, 25, () -> Ingredient.of(ModItems.VOIDIUM_INGOT.get())),
    WARDEN(5, 3000, 10.0F, 6.0F, 22, () -> Ingredient.of(ModItems.WARDEN_HEART.get()));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantability;
    private final LazyValue<Ingredient> repair;

    ModTiers(int level, int uses, float speed, float damage, int enchantability, Supplier<Ingredient> repair) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantability = enchantability;
        this.repair = new LazyValue<>(repair);
    }

    @Override public int getUses() { return uses; }
    @Override public float getSpeed() { return speed; }
    @Override public float getAttackDamageBonus() { return damage; }
    @Override public int getLevel() { return level; }
    @Override public int getEnchantmentValue() { return enchantability; }
    @Override public Ingredient getRepairIngredient() { return repair.get(); }
}
