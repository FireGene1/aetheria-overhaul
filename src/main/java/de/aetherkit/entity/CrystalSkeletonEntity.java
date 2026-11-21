package de.aetherkit.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

/** Kristallskelett: Pfeile verlangsamen und markieren das Ziel (Leuchten). Lebt in kalten Gebieten und Höhlen. */
public class CrystalSkeletonEntity extends AbstractSkeletonEntity {
    public CrystalSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> type, World level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeModifierMap.MutableAttribute createCrystalAttributes() {
        return AbstractSkeletonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.STRAY_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.STRAY_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.STRAY_DEATH; }

    @Override
    protected SoundEvent getStepSound() { return SoundEvents.STRAY_STEP; }

    @Override
    protected AbstractArrowEntity getArrow(ItemStack arrowStack, float distanceFactor) {
        AbstractArrowEntity arrow = super.getArrow(arrowStack, distanceFactor);
        if (arrow instanceof ArrowEntity) {
            ((ArrowEntity) arrow).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 120, 1));
            ((ArrowEntity) arrow).addEffect(new EffectInstance(Effects.GLOWING, 100, 0));
        }
        arrow.setBaseDamage(arrow.getBaseDamage() + 1.0D);
        return arrow;
    }
}
