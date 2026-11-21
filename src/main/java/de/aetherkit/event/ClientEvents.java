package de.aetherkit.event;

import de.aetherkit.AKConfig;
import de.aetherkit.AetherKit;
import de.aetherkit.client.ClientSetup;
import de.aetherkit.network.AKNetwork;
import de.aetherkit.network.SortInventoryPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Nur Client: Essens-Tooltips, Info-HUD und Sortieren in Inventaren. */
@Mod.EventBusSubscriber(modid = AetherKit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!AKConfig.FOOD_TOOLTIPS.get()) return;
        ItemStack stack = event.getItemStack();
        if (!stack.isEdible()) return;
        Food food = stack.getItem().getFoodProperties();
        if (food == null) return;
        float saturation = food.getNutrition() * food.getSaturationModifier() * 2.0F;
        event.getToolTip().add(new TranslationTextComponent("tooltip.aetherkit.food",
                food.getNutrition(), String.format("%.1f", saturation)).withStyle(TextFormatting.GOLD));
    }

    /** Kleine Info-Anzeige oben links: Koordinaten, Biom, Tag und Uhrzeit. */
    @SubscribeEvent
    public static void onOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getInstance();
        if (!AKConfig.INFO_HUD.get() || mc.player == null || mc.level == null || mc.options.renderDebug) return;
        BlockPos pos = mc.player.blockPosition();
        event.getLeft().add(TextFormatting.AQUA + "XYZ: " + TextFormatting.WHITE + pos.getX() + " / " + pos.getY() + " / " + pos.getZ());

        Biome biome = mc.level.getBiome(pos);
        ResourceLocation key = mc.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
        if (key != null) {
            String name = new TranslationTextComponent("biome." + key.getNamespace() + "." + key.getPath()).getString();
            event.getLeft().add(TextFormatting.GREEN + name);
        }
        long time = mc.level.getDayTime();
        long day = time / 24000L + 1;
        int t = (int) ((time + 6000L) % 24000L);
        int hours = t / 1000;
        int minutes = (t % 1000) * 60 / 1000;
        event.getLeft().add(TextFormatting.GOLD + new TranslationTextComponent("hud.aetherkit.day", day).getString()
                + TextFormatting.WHITE + String.format("  %02d:%02d", hours, minutes));
    }

    /** Sortieren per Mausrad-Klick (Standard) oder per frei belegbarer Taste in jeder Container-Ansicht. */
    private static boolean sortable(Screen screen) {
        return screen instanceof ContainerScreen && !(screen instanceof CreativeScreen);
    }

    @SubscribeEvent
    public static void onKey(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (sortable(event.getGui()) && ClientSetup.SORT_KEY.matches(event.getKeyCode(), event.getScanCode())) {
            AKNetwork.CHANNEL.sendToServer(new SortInventoryPacket());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouse(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (sortable(event.getGui()) && ClientSetup.SORT_KEY.matchesMouse(event.getButton())) {
            AKNetwork.CHANNEL.sendToServer(new SortInventoryPacket());
            event.setCanceled(true);
        }
    }
}
