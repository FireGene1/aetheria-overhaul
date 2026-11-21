package de.aetherkit.registry;

import de.aetherkit.AetherKit;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.function.Supplier;

/** Rüstungsmaterialien. Texturen: textures/models/armor/<name>_layer_1/2.png */
public enum ModArmorMaterial implements IArmorMaterial {
    AETHERITE("aetherite", 30, new int[]{3, 6, 8, 3}, 20, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.05F,
            () -> Ingredient.of(ModItems.AETHERITE_SHARD.get())),
    EMBERITE("emberite", 36, new int[]{3, 6, 8, 3}, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(ModItems.EMBERITE_INGOT.get())),
    VOIDIUM("voidium", 45, new int[]{4, 7, 9, 4}, 25, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.15F,
            () -> Ingredient.of(ModItems.VOIDIUM_INGOT.get()));

    // Reihenfolge wie Vanilla: FEET, LEGS, CHEST, HEAD
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

    private final String name;
    private final int durabilityMultiplier;
    private final int[] defense;
    private final int enchantability;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyValue<Ingredient> repair;

    ModArmorMaterial(String name, int durabilityMultiplier, int[] defense, int enchantability, SoundEvent sound,
                     float toughness, float knockbackResistance, Supplier<Ingredient> repair) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.defense = defense;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repair = new LazyValue<>(repair);
    }

    @Override public int getDurabilityForSlot(EquipmentSlotType slot) { return BASE_DURABILITY[slot.getIndex()] * durabilityMultiplier; }
    @Override public int getDefenseForSlot(EquipmentSlotType slot) { return defense[slot.getIndex()]; }
    @Override public int getEnchantmentValue() { return enchantability; }
    @Override public SoundEvent getEquipSound() { return sound; }
    @Override public Ingredient getRepairIngredient() { return repair.get(); }
    @Override public String getName() { return AetherKit.MODID + ":" + name; }
    @Override public float getToughness() { return toughness; }
    @Override public float getKnockbackResistance() { return knockbackResistance; }
}
