package de.aetherkit.client;

import de.aetherkit.network.OpenWaystonesPacket;
import net.minecraft.client.Minecraft;

/** Wird nur auf dem Client geladen. */
public final class ClientPacketHandler {
    private ClientPacketHandler() {}

    public static void openWaystones(OpenWaystonesPacket msg) {
        Minecraft.getInstance().setScreen(new WaystoneScreen(msg.entries, msg.currentDim, msg.currentPos));
    }
}
