package de.aetherkit.event;

import de.aetherkit.AKConfig;
import de.aetherkit.item.ModArmorItem;
import de.aetherkit.registry.ModArmorMaterial;
import de.aetherkit.registry.ModItems;
import net.minecraft.item.Item;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/** Alle Quality-of-Life-Funktionen + Rüstungsboni. Jede Funktion ist in der Config abschaltbar. */
public class QolEvents {

    /** Werkzeuge, für die schon gewarnt wurde (schwache Referenzen -> kein Speicherleck). */
    private final Set<ItemStack> warnedTools = Collections.newSetFromMap(new WeakHashMap<>());

    // ------------------------------------------------------------------ Rechtsklick auf Blöcke

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != Hand.MAIN_HAND) return;
        PlayerEntity player = event.getPlayer();
        if (player.isShiftKeyDown()) return;

        World level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (AKConfig.RIGHT_CLICK_HARVEST.get() && state.getBlock() instanceof CropsBlock) {
            if (harvestCrop(level, pos, state, player)) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.sidedSuccess(level.isClientSide));
            }
            return;
        }

        if (AKConfig.DOUBLE_DOORS.get() && state.getBlock() instanceof DoorBlock && state.getMaterial() == Material.WOOD) {
            openPartnerDoor(level, pos, state);
        }
    }

    /** Erntet eine reife Pflanze, droppt die Ernte (minus ein Saatgut) und setzt sie auf Stufe 0 zurück. */
    private static boolean harvestCrop(World level, BlockPos pos, BlockState state, PlayerEntity player) {
        CropsBlock crop = (CropsBlock) state.getBlock();
        if (!crop.isMaxAge(state)) return false;
        if (level.isClientSide) return true;

        List<ItemStack> drops = Block.getDrops(state, (ServerWorld) level, pos, null, player, player.getMainHandItem());
        ItemStack seed = crop.getCloneItemStack(level, pos, state);
        boolean seedRemoved = false;
        for (ItemStack drop : drops) {
            if (!seedRemoved && !seed.isEmpty() && drop.getItem() == seed.getItem()) {
                drop.shrink(1);
                seedRemoved = true;
            }
            if (!drop.isEmpty()) {
                Block.popResource(level, pos, drop);
            }
        }
        level.levelEvent(2001, pos, Block.getId(state)); // Abbau-Partikel + Geräusch
        level.setBlock(pos, crop.getStateForAge(0), 2);
        return true;
    }

    /** Öffnet/schliesst den passenden zweiten Türflügel. */
    private static void openPartnerDoor(World level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
        Direction side = hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
        BlockPos otherPos = pos.relative(side);
        BlockState other = level.getBlockState(otherPos);

        if (other.getBlock() instanceof DoorBlock
                && other.getMaterial() == Material.WOOD
                && other.getValue(DoorBlock.FACING) == facing
                && other.getValue(DoorBlock.HINGE) != hinge
                && other.getValue(DoorBlock.HALF) == state.getValue(DoorBlock.HALF)
                && other.getValue(DoorBlock.OPEN) == state.getValue(DoorBlock.OPEN)) {
            level.setBlock(otherPos, other.setValue(DoorBlock.OPEN, !state.getValue(DoorBlock.OPEN)), 10);
        }
    }

    // ------------------------------------------------------------------ Ackerboden

    @SubscribeEvent
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (AKConfig.NO_FARMLAND_TRAMPLE.get()) {
            event.setCanceled(true);
        }
    }

    // ------------------------------------------------------------------ Setzlinge

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        if (!AKConfig.AUTO_PLANT_SAPLINGS.get()) return;
        ItemEntity entity = event.getEntityItem();
        World level = entity.level;
        if (level.isClientSide) return;
        ItemStack stack = entity.getItem();
        if (!(stack.getItem() instanceof BlockItem)) return;
        Block block = ((BlockItem) stack.getItem()).getBlock();
        if (!(block instanceof SaplingBlock)) return;

        BlockPos pos = entity.blockPosition();
        BlockState sapling = block.defaultBlockState();
        if (level.isEmptyBlock(pos) && sapling.canSurvive(level, pos)) {
            level.setBlock(pos, sapling, 3);
        }
    }

    // ------------------------------------------------------------------ Spieler-Tick

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PlayerEntity player = event.player;
        if (player.level.isClientSide || player.tickCount % 10 != 0) return;

        if (AKConfig.DURABILITY_WARNING.get()) {
            checkDurability(player);
        }
        if (AKConfig.ARMOR_SET_BONUS.get() && player.tickCount % 40 == 0) {
            applySetBonuses(player);
        }
    }

    private static final String FLIGHT_TAG = "aetherkit_flight";

    /** Set-Boni: Aetherit = Eile, Emberit = Feuerresistenz + Stärke, Voidium = Fliegen + Resistenz. */
    private static void applySetBonuses(PlayerEntity player) {
        ModArmorMaterial set = fullSet(player);
        if (set == ModArmorMaterial.AETHERITE) {
            player.addEffect(new EffectInstance(Effects.DIG_SPEED, 100, 0, true, false, true));
        } else if (set == ModArmorMaterial.EMBERITE) {
            player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 100, 0, true, false, true));
            player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 100, 0, true, false, true));
        } else if (set == ModArmorMaterial.VOIDIUM) {
            player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 100, 0, true, false, true));
            player.addEffect(new EffectInstance(Effects.DIG_SPEED, 100, 1, true, false, true));
        }

        // Fliegen mit vollem Voidium-Satz (nur entziehen, wenn wir es gegeben haben)
        boolean wantsFlight = set == ModArmorMaterial.VOIDIUM && AKConfig.VOIDIUM_FLIGHT.get();
        boolean granted = player.getPersistentData().getBoolean(FLIGHT_TAG);
        if (wantsFlight && !player.abilities.mayfly) {
            player.abilities.mayfly = true;
            player.getPersistentData().putBoolean(FLIGHT_TAG, true);
            player.onUpdateAbilities();
        } else if (!wantsFlight && granted) {
            player.getPersistentData().putBoolean(FLIGHT_TAG, false);
            if (!player.isCreative() && !player.isSpectator()) {
                player.abilities.mayfly = false;
                player.abilities.flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    private void checkDurability(PlayerEntity player) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.isDamageableItem()) return;
        int left = stack.getMaxDamage() - stack.getDamageValue();
        int threshold = Math.max(8, stack.getMaxDamage() / 20);
        if (left <= threshold) {
            if (warnedTools.add(stack)) {
                player.displayClientMessage(new TranslationTextComponent("message.aetherkit.durability_low",
                        stack.getHoverName(), left).withStyle(TextFormatting.RED), true);
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_BREAK, SoundCategory.PLAYERS, 0.4F, 0.6F);
            }
        } else {
            warnedTools.remove(stack);
        }
    }

    // ------------------------------------------------------------------ Rüstung

    /** Liefert das Material, wenn alle vier Rüstungsteile aus demselben Mod-Material sind, sonst null. */
    @Nullable
    public static ModArmorMaterial fullSet(LivingEntity entity) {
        ModArmorMaterial found = null;
        for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST,
                EquipmentSlotType.LEGS, EquipmentSlotType.FEET}) {
            Item item = entity.getItemBySlot(slot).getItem();
            if (!(item instanceof ModArmorItem)) return null;
            ModArmorMaterial m = ((ModArmorItem) item).getMat();
            if (found == null) found = m;
            else if (found != m) return null;
        }
        return found;
    }

    /** Aetherit-Stiefel halbieren Fallschaden, Voidium-Stiefel verhindern ihn ganz. */
    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        Item boots = event.getEntityLiving().getItemBySlot(EquipmentSlotType.FEET).getItem();
        if (boots == ModItems.AETHERITE_BOOTS.get()) {
            event.setDamageMultiplier(event.getDamageMultiplier() * 0.5F);
        } else if (boots == ModItems.VOIDIUM_BOOTS.get()) {
            event.setDamageMultiplier(0.0F);
        }
    }
}
