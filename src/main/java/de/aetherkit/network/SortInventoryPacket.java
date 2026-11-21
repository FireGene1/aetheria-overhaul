package de.aetherkit.network;

import de.aetherkit.system.InventorySorter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> Server: offene Truhe bzw. Inventar sortieren. */
public class SortInventoryPacket {
    public static void encode(SortInventoryPacket msg, PacketBuffer buf) {}

    public static SortInventoryPacket decode(PacketBuffer buf) {
        return new SortInventoryPacket();
    }

    public static void handle(SortInventoryPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) InventorySorter.sort(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
