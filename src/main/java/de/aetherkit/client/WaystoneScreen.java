package de.aetherkit.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.aetherkit.network.AKNetwork;
import de.aetherkit.network.TeleportWaystonePacket;
import de.aetherkit.system.WaystoneManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

/** Reisemenü der Wegsteine: eine Schaltfläche pro entdecktem Wegstein, seitenweise. */
public class WaystoneScreen extends Screen {
    private static final int PER_PAGE = 7;
    private final List<WaystoneManager.Entry> entries;
    private final String currentDim;
    private final BlockPos currentPos;
    private int page = 0;

    public WaystoneScreen(List<WaystoneManager.Entry> entries, String currentDim, BlockPos currentPos) {
        super(new TranslationTextComponent("gui.aetherkit.waystones"));
        this.entries = entries;
        this.currentDim = currentDim;
        this.currentPos = currentPos;
    }

    @Override
    protected void init() {
        buttons.clear();
        children.clear();
        int top = height / 2 - 90;
        int start = page * PER_PAGE;
        for (int i = start; i < Math.min(entries.size(), start + PER_PAGE); i++) {
            WaystoneManager.Entry e = entries.get(i);
            boolean here = e.dim.equals(currentDim) && e.pos.equals(currentPos);
            String dimName = e.dim.replace("minecraft:", "");
            String label = e.name + "  (" + dimName + ")";
            Button b = new Button(width / 2 - 110, top + 24 + (i - start) * 24, 220, 20,
                    here ? new StringTextComponent(label).withStyle(TextFormatting.DARK_GRAY) : new StringTextComponent(label),
                    btn -> {
                        AKNetwork.CHANNEL.sendToServer(new TeleportWaystonePacket(e.dim, e.pos));
                        onClose();
                    });
            b.active = !here;
            addButton(b);
        }
        int maxPage = Math.max(0, (entries.size() - 1) / PER_PAGE);
        Button prev = new Button(width / 2 - 110, top + 24 + PER_PAGE * 24 + 6, 50, 20, new StringTextComponent("<"), btn -> {
            page--;
            init(minecraft, width, height);
        });
        prev.active = page > 0;
        addButton(prev);
        Button next = new Button(width / 2 + 60, top + 24 + PER_PAGE * 24 + 6, 50, 20, new StringTextComponent(">"), btn -> {
            page++;
            init(minecraft, width, height);
        });
        next.active = page < maxPage;
        addButton(next);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        drawCenteredString(stack, font, title, width / 2, height / 2 - 82, 0x7DE3DC);
        if (entries.size() <= 1) {
            drawCenteredString(stack, font, new TranslationTextComponent("gui.aetherkit.waystones_empty"),
                    width / 2, height / 2 - 40, 0xAAAAAA);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
