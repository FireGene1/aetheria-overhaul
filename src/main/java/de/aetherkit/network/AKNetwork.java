package de.aetherkit.network;

import de.aetherkit.AetherKit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public final class AKNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AetherKit.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    private AKNetwork() {}

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, OpenWaystonesPacket.class, OpenWaystonesPacket::encode, OpenWaystonesPacket::decode,
                OpenWaystonesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, TeleportWaystonePacket.class, TeleportWaystonePacket::encode, TeleportWaystonePacket::decode,
                TeleportWaystonePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, SortInventoryPacket.class, SortInventoryPacket::encode, SortInventoryPacket::decode,
                SortInventoryPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
