package de.aetherkit.entity;

import de.aetherkit.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Moosrücken-Eber: Waldtier, züchtbar mit Karotten/Kartoffeln/Äpfeln, droppt Eberfleisch und Leder. */
public class MossbackBoarEntity extends AnimalEntity {
    private static final Ingredient FOOD = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT, Items.APPLE);

    public MossbackBoarEntity(EntityType<? extends AnimalEntity> type, World level) {
        super(type, level);
    }

    public static AttributeModifierMap.MutableAttribute createBoarAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15D, FOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD.test(stack);
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld level, AgeableEntity mate) {
        return ModEntities.MOSSBACK_BOAR.get().create(level);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.PIG_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.PIG_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.PIG_DEATH; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.PIG_STEP, 0.15F, 0.8F);
    }

    @Override
    protected float getVoicePitch() {
        return super.getVoicePitch() * 0.75F;
    }
}
