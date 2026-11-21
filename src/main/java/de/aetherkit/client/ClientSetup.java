package de.aetherkit.client;

import de.aetherkit.AetherKit;
import de.aetherkit.registry.ModBlocks;
import de.aetherkit.registry.ModEntities;
import de.aetherkit.registry.ModItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = AetherKit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    public static final KeyBinding SORT_KEY = new KeyBinding("key.aetherkit.sort",
            InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "key.categories.aetherkit");

    private ClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.RUIN_GUARDIAN.get(), ModRenderers.RuinGuardianRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CRYSTAL_SKELETON.get(), ModRenderers.CrystalSkeletonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.MOSSBACK_BOAR.get(), ModRenderers.BoarRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.WISP.get(), ModRenderers.WispRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VOID_STALKER.get(), ModRenderers.VoidStalkerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.AETHER_WARDEN.get(), ModRenderers.WardenRenderer::new);

        ClientRegistry.registerKeyBinding(SORT_KEY);

        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(ModBlocks.GLOWCAP.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.AETHER_CRYSTAL_CLUSTER.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.WARDING_LANTERN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.GROWTH_TOTEM.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.WAYSTONE.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.GRAVE.get(), RenderType.cutout());

            // Spannanimation des Aetherbogens
            ItemModelsProperties.register(ModItems.AETHER_BOW.get(), new ResourceLocation("pull"), (stack, level, entity) -> {
                if (entity == null || entity.getUseItem() != stack) return 0.0F;
                return (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            });
            ItemModelsProperties.register(ModItems.AETHER_BOW.get(), new ResourceLocation("pulling"),
                    (stack, level, entity) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
        });
    }
}
