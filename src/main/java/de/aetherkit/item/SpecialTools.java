package de.aetherkit.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

/** Werkzeuge mit Spezialeffekten. */
public final class SpecialTools {
    private SpecialTools() {}

    /** Markiert Werkzeuge, deren Drops automatisch geschmolzen werden (siehe AutoSmeltModifier). */
    public interface IAutoSmelt {}

    /** Emberit-Schwert: setzt Gegner in Brand. */
    public static class EmberSword extends SwordItem {
        public EmberSword(IItemTier tier, int dmg, float speed, Properties p) { super(tier, dmg, speed, p); }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            target.setSecondsOnFire(5);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack s, @Nullable World w, List<ITextComponent> t, ITooltipFlag f) {
            Tooltips.add(t, "ember_sword", 1);
        }
    }

    /** Emberit-Spitzhacke: schmilzt abgebaute Erze sofort. */
    public static class EmberPickaxe extends PickaxeItem implements IAutoSmelt {
        public EmberPickaxe(IItemTier tier, int dmg, float speed, Properties p) { super(tier, dmg, speed, p); }

        @Override
        public void appendHoverText(ItemStack s, @Nullable World w, List<ITextComponent> t, ITooltipFlag f) {
            Tooltips.add(t, "ember_pickaxe", 1);
        }
    }

    /** Voidium-Schwert: Lebensraub. */
    public static class VoidSword extends SwordItem {
        public VoidSword(IItemTier tier, int dmg, float speed, Properties p) { super(tier, dmg, speed, p); }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            attacker.heal(2.0F);
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack s, @Nullable World w, List<ITextComponent> t, ITooltipFlag f) {
            Tooltips.add(t, "void_sword", 1);
        }
    }

    /** Klinge des Wächters (Boss-Drop): Rechtsklick löst eine Schockwelle aus. */
    public static class WardenBlade extends SwordItem {
        public WardenBlade(IItemTier tier, int dmg, float speed, Properties p) { super(tier, dmg, speed, p); }

        @Override
        public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide) {
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                        player.getBoundingBox().inflate(6.0D), e -> e != player && e.isAlive());
                for (LivingEntity e : targets) {
                    e.hurt(DamageSource.playerAttack(player), 8.0F);
                    e.knockback(1.6F, player.getX() - e.getX(), player.getZ() - e.getZ());
                }
                ((ServerWorld) level).sendParticles(ParticleTypes.EXPLOSION,
                        player.getX(), player.getY() + 1, player.getZ(), 12, 2.5, 0.5, 2.5, 0.0);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE,
                        SoundCategory.PLAYERS, 0.8F, 1.4F);
                stack.hurtAndBreak(5, player, p -> p.broadcastBreakEvent(hand));
                player.getCooldowns().addCooldown(this, 100);
            }
            return ActionResult.sidedSuccess(stack, level.isClientSide);
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            return true;
        }

        @Override
        public void appendHoverText(ItemStack s, @Nullable World w, List<ITextComponent> t, ITooltipFlag f) {
            Tooltips.add(t, "warden_blade", 2);
        }
    }
}
