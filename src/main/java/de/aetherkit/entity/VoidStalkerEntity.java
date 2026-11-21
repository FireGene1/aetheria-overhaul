package de.aetherkit.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

/** Leerenpirscher: stärkerer Verwandter des Endermans im Ende. Blendet Getroffene, droppt Leerensplitter. */
public class VoidStalkerEntity extends EndermanEntity {
    public VoidStalkerEntity(EntityType<? extends EndermanEntity> type, World level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeModifierMap.MutableAttribute createStalkerAttributes() {
        return EndermanEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new EffectInstance(Effects.BLINDNESS, 60, 0));
        }
        return hit;
    }
}
