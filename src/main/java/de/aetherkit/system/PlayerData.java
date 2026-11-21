package de.aetherkit.system;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

/**
 * Spielerdaten, die den Tod überleben (Forge kopiert PERSISTED_NBT_TAG beim Respawn):
 * Bonus-Herzen und entdeckte Wegsteine.
 */
public final class PlayerData {
    public static final int MAX_BONUS_HEARTS = 10;
    private static final UUID HEART_MODIFIER = UUID.fromString("6f3d8a1e-2b7c-4c1a-9e55-a37be7a1c0de");
    private static final String ROOT = "aetherkit";

    private PlayerData() {}

    public static CompoundNBT get(PlayerEntity player) {
        CompoundNBT persisted = player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        CompoundNBT mine = persisted.getCompound(ROOT);
        persisted.put(ROOT, mine);
        player.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, persisted);
        return mine;
    }

    public static int getBonusHearts(PlayerEntity player) {
        return get(player).getInt("Hearts");
    }

    public static void setBonusHearts(PlayerEntity player, int hearts) {
        get(player).putInt("Hearts", Math.max(0, Math.min(MAX_BONUS_HEARTS, hearts)));
        applyHearts(player);
    }

    /** Setzt den Lebens-Modifikator passend zur gespeicherten Herzanzahl. */
    public static void applyHearts(PlayerEntity player) {
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health == null) return;
        health.removeModifier(HEART_MODIFIER);
        int hearts = getBonusHearts(player);
        if (hearts > 0) {
            health.addPermanentModifier(new AttributeModifier(HEART_MODIFIER, "aetherkit bonus hearts",
                    hearts * 2.0D, AttributeModifier.Operation.ADDITION));
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
