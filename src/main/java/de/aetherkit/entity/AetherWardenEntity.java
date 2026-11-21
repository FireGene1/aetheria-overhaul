package de.aetherkit.entity;

import de.aetherkit.registry.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

/**
 * Aetherwächter – Bossgegner.
 * Phase 1: Nahkampf + Schockwelle alle 6 Sekunden.
 * Phase 2 (unter 50 %): ruft Ruinenwächter, teleportiert sich hinter das Ziel, wird schneller.
 * Droppt Wächterherz, Herzkristalle und Aetheritblöcke.
 */
public class AetherWardenEntity extends MonsterEntity {
    private final ServerBossInfo bossEvent = new ServerBossInfo(getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.NOTCHED_10);
    private int shockwaveTimer = 120;
    private int teleportTimer = 200;
    private boolean phaseTwo = false;

    public AetherWardenEntity(EntityType<? extends MonsterEntity> type, World level) {
        super(type, level);
        this.xpReward = 250;
        this.setPersistenceRequired();
    }

    public static AttributeModifierMap.MutableAttribute createWardenAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 320.0D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
        this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setPercent(getHealth() / getMaxHealth());
        ServerWorld server = (ServerWorld) level;

        if (!phaseTwo && getHealth() < getMaxHealth() * 0.5F) {
            phaseTwo = true;
            enterPhaseTwo(server);
        }

        LivingEntity target = getTarget();
        if (target == null) return;

        if (--shockwaveTimer <= 0) {
            shockwaveTimer = phaseTwo ? 80 : 120;
            shockwave(server);
        }
        if (phaseTwo && --teleportTimer <= 0) {
            teleportTimer = 160;
            teleportBehind(server, target);
        }
    }

    private void shockwave(ServerWorld server) {
        List<PlayerEntity> players = server.getEntitiesOfClass(PlayerEntity.class, getBoundingBox().inflate(8.0D),
                p -> p.isAlive() && !p.isCreative() && !p.isSpectator());
        for (PlayerEntity p : players) {
            p.hurt(DamageSource.mobAttack(this), 8.0F);
            p.knockback(1.8F, getX() - p.getX(), getZ() - p.getZ());
            p.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 1));
        }
        for (int i = 0; i < 24; i++) {
            double a = i * Math.PI * 2 / 24;
            server.sendParticles(ParticleTypes.CLOUD, getX() + Math.cos(a) * 3, getY() + 0.2, getZ() + Math.sin(a) * 3,
                    2, 0.2, 0.0, 0.2, 0.05);
        }
        server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5, getZ(), 1, 0, 0, 0, 0);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundCategory.HOSTILE, 1.5F, 0.6F);
    }

    private void enterPhaseTwo(ServerWorld server) {
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.4F);
        addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 999999, 0, false, false));
        addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 999999, 0, false, false));
        for (int i = 0; i < 3; i++) {
            RuinGuardianEntity minion = ModEntities.RUIN_GUARDIAN.get().create(server);
            if (minion == null) continue;
            double a = i * Math.PI * 2 / 3;
            minion.moveTo(getX() + Math.cos(a) * 3, getY(), getZ() + Math.sin(a) * 3, random.nextFloat() * 360F, 0);
            minion.finalizeSpawn(server, server.getCurrentDifficultyAt(blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
            server.addFreshEntity(minion);
            server.sendParticles(ParticleTypes.SOUL, minion.getX(), minion.getY() + 1, minion.getZ(), 15, 0.3, 0.6, 0.3, 0.02);
        }
    }

    private void teleportBehind(ServerWorld server, LivingEntity target) {
        Vector3d look = target.getLookAngle();
        double x = target.getX() - look.x * 2.5;
        double z = target.getZ() - look.z * 2.5;
        server.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 1.5, getZ(), 40, 0.5, 1.0, 0.5, 0.3);
        if (randomTeleport(x, target.getY(), z, true)) {
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 0.6F);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Fernkampf-Spam wird reduziert: Projektile machen nur 60 % Schaden
        if (source.isProjectile()) amount *= 0.6F;
        return super.hurt(source, amount);
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.RAVAGER_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.IRON_GOLEM_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.WITHER_DEATH; }

    @Override
    protected float getVoicePitch() { return 0.6F; }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("PhaseTwo", phaseTwo);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        phaseTwo = nbt.getBoolean("PhaseTwo");
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }
}
