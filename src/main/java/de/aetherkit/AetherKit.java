package de.aetherkit;

import de.aetherkit.event.QolEvents;
import de.aetherkit.event.SystemEvents;
import de.aetherkit.event.ToolEvents;
import de.aetherkit.network.AKNetwork;
import de.aetherkit.registry.*;
import de.aetherkit.world.ModWorldGen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Aetheria Overhaul (Mod-ID aetherkit) – Hauptklasse.
 * Drei Erzstufen, neue Mobs und ein Boss, sechs Strukturen, Wegsteine, Gräber, Rucksäcke,
 * Herzkristalle und viele Quality-of-Life-Funktionen.
 */
@Mod(AetherKit.MODID)
public class AetherKit {
    public static final String MODID = "aetherkit";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ItemGroup TAB = new ItemGroup(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.AETHERITE_HAMMER.get());
        }
    };

    public AetherKit() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModTileEntities.TILE_ENTITIES.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModFeatures.LOOT_MODIFIERS.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(ModEntities::onAttributes);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AKConfig.SPEC, "aetherkit-common.toml");

        MinecraftForge.EVENT_BUS.register(new ToolEvents());
        MinecraftForge.EVENT_BUS.register(new QolEvents());
        MinecraftForge.EVENT_BUS.register(new SystemEvents());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ModWorldGen::onBiomeLoad);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AKNetwork.register();
        event.enqueueWork(() -> {
            ModWorldGen.register();
            ModEntities.registerSpawnPlacements();
        });
        LOGGER.info("Aetheria Overhaul geladen – viel Spass!");
    }
}
