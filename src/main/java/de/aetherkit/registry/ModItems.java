package de.aetherkit.registry;

import de.aetherkit.AetherKit;
import de.aetherkit.item.*;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AetherKit.MODID);

    public static Item.Properties props() {
        return new Item.Properties().tab(AetherKit.TAB);
    }

    private static RegistryObject<Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(props()));
    }

    private static RegistryObject<Item> block(String name, Supplier<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), props()));
    }

    private static RegistryObject<Item> armor(String name, ModArmorMaterial mat, EquipmentSlotType slot) {
        return ITEMS.register(name, () -> new ModArmorItem(mat, slot, props()));
    }

    private static RegistryObject<Item> egg(String name, Supplier<? extends EntityType<? extends MobEntity>> type, int a, int b) {
        return ITEMS.register(name, () -> new ForgeSpawnEggItem(type, a, b, props()));
    }

    // ================= Materialien =================
    public static final RegistryObject<Item> AETHERITE_SHARD = simple("aetherite_shard");
    public static final RegistryObject<Item> EMBERITE_CHUNK = simple("emberite_chunk");
    public static final RegistryObject<Item> EMBERITE_INGOT = ITEMS.register("emberite_ingot", () -> new Item(props().fireResistant()));
    public static final RegistryObject<Item> VOID_SHARD = simple("void_shard");
    public static final RegistryObject<Item> VOIDIUM_INGOT = ITEMS.register("voidium_ingot",
            () -> new Item(props().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> AETHER_CORE = ITEMS.register("aether_core",
            () -> new TooltipItem(props().rarity(Rarity.UNCOMMON), true, "aether_core"));
    public static final RegistryObject<Item> WISP_ESSENCE = ITEMS.register("wisp_essence",
            () -> new TooltipItem(props(), false, "wisp_essence"));
    public static final RegistryObject<Item> WARDEN_HEART = ITEMS.register("warden_heart",
            () -> new TooltipItem(props().rarity(Rarity.EPIC).fireResistant(), true, "warden_heart"));
    public static final RegistryObject<Item> WARDEN_SIGIL = ITEMS.register("warden_sigil",
            () -> new TooltipItem(props().rarity(Rarity.RARE).stacksTo(16), true, "warden_sigil"));
    public static final RegistryObject<Item> HEART_CRYSTAL = ITEMS.register("heart_crystal",
            () -> new HeartCrystalItem(props().rarity(Rarity.RARE).stacksTo(16)));

    // ================= Blöcke =================
    public static final RegistryObject<Item> AETHERITE_ORE = block("aetherite_ore", ModBlocks.AETHERITE_ORE::get);
    public static final RegistryObject<Item> EMBER_ORE = block("ember_ore", ModBlocks.EMBER_ORE::get);
    public static final RegistryObject<Item> VOID_ORE = block("void_ore", ModBlocks.VOID_ORE::get);
    public static final RegistryObject<Item> AETHERITE_BLOCK = block("aetherite_block", ModBlocks.AETHERITE_BLOCK::get);
    public static final RegistryObject<Item> EMBERITE_BLOCK = block("emberite_block", ModBlocks.EMBERITE_BLOCK::get);
    public static final RegistryObject<Item> VOIDIUM_BLOCK = block("voidium_block", ModBlocks.VOIDIUM_BLOCK::get);
    public static final RegistryObject<Item> AETHER_BRICKS = block("aether_bricks", ModBlocks.AETHER_BRICKS::get);
    public static final RegistryObject<Item> AETHER_BRICK_STAIRS = block("aether_brick_stairs", ModBlocks.AETHER_BRICK_STAIRS::get);
    public static final RegistryObject<Item> AETHER_BRICK_SLAB = block("aether_brick_slab", ModBlocks.AETHER_BRICK_SLAB::get);
    public static final RegistryObject<Item> AETHER_LAMP = block("aether_lamp", ModBlocks.AETHER_LAMP::get);
    public static final RegistryObject<Item> AETHER_CRYSTAL_BLOCK = block("aether_crystal_block", ModBlocks.AETHER_CRYSTAL_BLOCK::get);
    public static final RegistryObject<Item> AETHER_CRYSTAL_CLUSTER = block("aether_crystal_cluster", ModBlocks.AETHER_CRYSTAL_CLUSTER::get);
    public static final RegistryObject<Item> GLOWCAP = block("glowcap", ModBlocks.GLOWCAP::get);
    public static final RegistryObject<Item> ELEVATOR = ITEMS.register("elevator",
            () -> new TooltipBlockItem(ModBlocks.ELEVATOR.get(), props(), "elevator", 1));
    public static final RegistryObject<Item> WARDING_LANTERN = block("warding_lantern", ModBlocks.WARDING_LANTERN::get);
    public static final RegistryObject<Item> GROWTH_TOTEM = block("growth_totem", ModBlocks.GROWTH_TOTEM::get);
    public static final RegistryObject<Item> WAYSTONE = block("waystone", ModBlocks.WAYSTONE::get);
    public static final RegistryObject<Item> WARDEN_ALTAR = block("warden_altar", ModBlocks.WARDEN_ALTAR::get);

    // ================= Aetherit-Werkzeuge =================
    public static final RegistryObject<Item> AETHERITE_SWORD = ITEMS.register("aetherite_sword",
            () -> new SwordItem(ModTiers.AETHERITE, 3, -2.4F, props()));
    public static final RegistryObject<Item> AETHERITE_PICKAXE = ITEMS.register("aetherite_pickaxe",
            () -> new PickaxeItem(ModTiers.AETHERITE, 1, -2.8F, props()));
    public static final RegistryObject<Item> AETHERITE_AXE = ITEMS.register("aetherite_axe",
            () -> new AxeItem(ModTiers.AETHERITE, 5.0F, -3.0F, props()));
    public static final RegistryObject<Item> AETHERITE_SHOVEL = ITEMS.register("aetherite_shovel",
            () -> new ShovelItem(ModTiers.AETHERITE, 1.5F, -3.0F, props()));
    public static final RegistryObject<Item> AETHERITE_HOE = ITEMS.register("aetherite_hoe",
            () -> new HoeItem(ModTiers.AETHERITE, -3, 0.0F, props()));
    public static final RegistryObject<Item> AETHERITE_HAMMER = ITEMS.register("aetherite_hammer",
            () -> new HammerItem(ModTiers.AETHERITE, 5, -3.2F, props().durability(4200)));
    public static final RegistryObject<Item> AETHERITE_EXCAVATOR = ITEMS.register("aetherite_excavator",
            () -> new ExcavatorItem(ModTiers.AETHERITE, 2.5F, -3.2F, props().durability(4200)));
    public static final RegistryObject<Item> LUMBER_AXE = ITEMS.register("lumber_axe",
            () -> new LumberAxeItem(ModTiers.AETHERITE, 7.0F, -3.3F, props().durability(3000)));
    public static final RegistryObject<Item> AETHER_BOW = ITEMS.register("aether_bow",
            () -> new AetherBowItem(props().durability(900)));

    // ================= Emberit-Werkzeuge =================
    public static final RegistryObject<Item> EMBERITE_SWORD = ITEMS.register("emberite_sword",
            () -> new SpecialTools.EmberSword(ModTiers.EMBERITE, 3, -2.4F, props().fireResistant()));
    public static final RegistryObject<Item> EMBERITE_PICKAXE = ITEMS.register("emberite_pickaxe",
            () -> new SpecialTools.EmberPickaxe(ModTiers.EMBERITE, 1, -2.8F, props().fireResistant()));
    public static final RegistryObject<Item> EMBERITE_AXE = ITEMS.register("emberite_axe",
            () -> new AxeItem(ModTiers.EMBERITE, 5.0F, -3.0F, props().fireResistant()));
    public static final RegistryObject<Item> EMBERITE_SHOVEL = ITEMS.register("emberite_shovel",
            () -> new ShovelItem(ModTiers.EMBERITE, 1.5F, -3.0F, props().fireResistant()));
    public static final RegistryObject<Item> EMBERITE_HOE = ITEMS.register("emberite_hoe",
            () -> new HoeItem(ModTiers.EMBERITE, -4, 0.0F, props().fireResistant()));

    // ================= Voidium-Werkzeuge =================
    public static final RegistryObject<Item> VOIDIUM_SWORD = ITEMS.register("voidium_sword",
            () -> new SpecialTools.VoidSword(ModTiers.VOIDIUM, 3, -2.2F, props().fireResistant()));
    public static final RegistryObject<Item> VOIDIUM_PICKAXE = ITEMS.register("voidium_pickaxe",
            () -> new PickaxeItem(ModTiers.VOIDIUM, 1, -2.6F, props().fireResistant()));
    public static final RegistryObject<Item> VOIDIUM_AXE = ITEMS.register("voidium_axe",
            () -> new AxeItem(ModTiers.VOIDIUM, 5.0F, -2.9F, props().fireResistant()));
    public static final RegistryObject<Item> VOIDIUM_SHOVEL = ITEMS.register("voidium_shovel",
            () -> new ShovelItem(ModTiers.VOIDIUM, 1.5F, -2.9F, props().fireResistant()));
    public static final RegistryObject<Item> VOIDIUM_HOE = ITEMS.register("voidium_hoe",
            () -> new HoeItem(ModTiers.VOIDIUM, -5, 0.0F, props().fireResistant()));
    public static final RegistryObject<Item> WARDEN_BLADE = ITEMS.register("warden_blade",
            () -> new SpecialTools.WardenBlade(ModTiers.WARDEN, 5, -2.4F, props().fireResistant().rarity(Rarity.EPIC)));

    // ================= Rüstungen =================
    public static final RegistryObject<Item> AETHERITE_HELMET = armor("aetherite_helmet", ModArmorMaterial.AETHERITE, EquipmentSlotType.HEAD);
    public static final RegistryObject<Item> AETHERITE_CHESTPLATE = armor("aetherite_chestplate", ModArmorMaterial.AETHERITE, EquipmentSlotType.CHEST);
    public static final RegistryObject<Item> AETHERITE_LEGGINGS = armor("aetherite_leggings", ModArmorMaterial.AETHERITE, EquipmentSlotType.LEGS);
    public static final RegistryObject<Item> AETHERITE_BOOTS = armor("aetherite_boots", ModArmorMaterial.AETHERITE, EquipmentSlotType.FEET);
    public static final RegistryObject<Item> EMBERITE_HELMET = armor("emberite_helmet", ModArmorMaterial.EMBERITE, EquipmentSlotType.HEAD);
    public static final RegistryObject<Item> EMBERITE_CHESTPLATE = armor("emberite_chestplate", ModArmorMaterial.EMBERITE, EquipmentSlotType.CHEST);
    public static final RegistryObject<Item> EMBERITE_LEGGINGS = armor("emberite_leggings", ModArmorMaterial.EMBERITE, EquipmentSlotType.LEGS);
    public static final RegistryObject<Item> EMBERITE_BOOTS = armor("emberite_boots", ModArmorMaterial.EMBERITE, EquipmentSlotType.FEET);
    public static final RegistryObject<Item> VOIDIUM_HELMET = armor("voidium_helmet", ModArmorMaterial.VOIDIUM, EquipmentSlotType.HEAD);
    public static final RegistryObject<Item> VOIDIUM_CHESTPLATE = armor("voidium_chestplate", ModArmorMaterial.VOIDIUM, EquipmentSlotType.CHEST);
    public static final RegistryObject<Item> VOIDIUM_LEGGINGS = armor("voidium_leggings", ModArmorMaterial.VOIDIUM, EquipmentSlotType.LEGS);
    public static final RegistryObject<Item> VOIDIUM_BOOTS = armor("voidium_boots", ModArmorMaterial.VOIDIUM, EquipmentSlotType.FEET);

    // ================= Utility =================
    public static final RegistryObject<Item> MAGNET_RING = ITEMS.register("magnet_ring",
            () -> new MagnetRingItem(props().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ENDER_POUCH = ITEMS.register("ender_pouch",
            () -> new EnderPouchItem(props().stacksTo(1)));
    public static final RegistryObject<Item> POCKET_CRAFTER = ITEMS.register("pocket_crafter",
            () -> new PocketCrafterItem(props().stacksTo(1)));
    public static final RegistryObject<Item> BACKPACK = ITEMS.register("backpack",
            () -> new BackpackItem(3, props().stacksTo(1)));
    public static final RegistryObject<Item> LARGE_BACKPACK = ITEMS.register("large_backpack",
            () -> new BackpackItem(6, props().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TORCH_STAFF = ITEMS.register("torch_staff",
            () -> new TorchStaffItem(props().durability(512)));
    public static final RegistryObject<Item> GROWTH_STAFF = ITEMS.register("growth_staff",
            () -> new GrowthStaffItem(props().durability(256)));
    public static final RegistryObject<Item> RECALL_SCROLL = ITEMS.register("recall_scroll",
            () -> new RecallScrollItem(props().stacksTo(16).rarity(Rarity.UNCOMMON)));

    // ================= Essen =================
    public static final RegistryObject<Item> TRAIL_MIX = ITEMS.register("trail_mix",
            () -> new TooltipItem(props().food(new Food.Builder().nutrition(6).saturationMod(0.7F).fast()
                    .effect(() -> new EffectInstance(Effects.MOVEMENT_SPEED, 400, 0), 1.0F).build()), false, "trail_mix"));
    public static final RegistryObject<Item> RAW_BOAR = ITEMS.register("raw_boar",
            () -> new Item(props().food(new Food.Builder().nutrition(3).saturationMod(0.3F).meat().build())));
    public static final RegistryObject<Item> COOKED_BOAR = ITEMS.register("cooked_boar",
            () -> new Item(props().food(new Food.Builder().nutrition(9).saturationMod(0.9F).meat().build())));
    public static final RegistryObject<Item> GLOWCAP_STEW = ITEMS.register("glowcap_stew",
            () -> new TooltipSoupItem(props().stacksTo(1).food(new Food.Builder().nutrition(7).saturationMod(0.7F).alwaysEat()
                    .effect(() -> new EffectInstance(Effects.NIGHT_VISION, 3600, 0), 1.0F)
                    .effect(() -> new EffectInstance(Effects.REGENERATION, 100, 0), 1.0F).build()), "glowcap_stew"));

    // ================= Spawn-Eier =================
    public static final RegistryObject<Item> RUIN_GUARDIAN_EGG = egg("ruin_guardian_spawn_egg", ModEntities.RUIN_GUARDIAN, 0x4A5A63, 0x3DB2C6);
    public static final RegistryObject<Item> CRYSTAL_SKELETON_EGG = egg("crystal_skeleton_spawn_egg", ModEntities.CRYSTAL_SKELETON, 0xC9E8F0, 0x6B3BB3);
    public static final RegistryObject<Item> MOSSBACK_BOAR_EGG = egg("mossback_boar_spawn_egg", ModEntities.MOSSBACK_BOAR, 0x6E4B27, 0x4FAE3D);
    public static final RegistryObject<Item> WISP_EGG = egg("wisp_spawn_egg", ModEntities.WISP, 0xDBFFFA, 0x7DE3DC);
    public static final RegistryObject<Item> VOID_STALKER_EGG = egg("void_stalker_spawn_egg", ModEntities.VOID_STALKER, 0x14081F, 0xC03BFF);
    public static final RegistryObject<Item> AETHER_WARDEN_EGG = egg("aether_warden_spawn_egg", ModEntities.AETHER_WARDEN, 0x1A4670, 0xE0AE38);

    private ModItems() {}
}
