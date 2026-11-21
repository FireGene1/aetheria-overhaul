package de.aetherkit.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

/**
 * Ruinenwächter: gepanzerter Untoter aus alten Ruinen. Verbrennt nicht in der Sonne,
 * verwandelt sich nicht im Wasser, schwächt Getroffene.
 */
public class RuinGuardianEntity extends ZombieEntity {
    public RuinGuardianEntity(EntityType<? extends ZombieEntity> type, World level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeModifierMap.MutableAttribute createGuardianAttributes() {
        return ZombieEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public void setBaby(boolean baby) {
        // Ruinenwächter sind immer ausgewachsen
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlotType.MAINHAND,
                new ItemStack(random.nextInt(3) == 0 ? Items.IRON_AXE : Items.IRON_SWORD));
        this.setDropChance(EquipmentSlotType.MAINHAND, 0.05F);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new EffectInstance(Effects.WEAKNESS, 100, 0));
        }
        return hit;
    }
}
