package de.aetherkit.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Irrlicht: friedliches, schwebendes Lichtwesen in Wäldern und Sümpfen. Droppt Irrlicht-Essenz. */
public class WispEntity extends CreatureEntity {
    public WispEntity(EntityType<? extends CreatureEntity> type, World level) {
        super(type, level);
        this.moveControl = new FlyingMovementController(this, 20, true);
        this.setNoGravity(true);
        this.xpReward = 3;
    }

    public static AttributeModifierMap.MutableAttribute createWispAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected PathNavigator createNavigation(World level) {
        FlyingPathNavigator nav = new FlyingPathNavigator(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.8D));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level.isClientSide && random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.END_ROD, getRandomX(0.6D), getRandomY(), getRandomZ(0.6D), 0.0D, 0.01D, 0.0D);
        }
    }
}
