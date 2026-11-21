package de.aetherkit.network;

import de.aetherkit.system.WaystoneManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> Server: "Bring mich zu diesem Wegstein". */
public class TeleportWaystonePacket {
    private final String dim;
    private final BlockPos pos;

    public TeleportWaystonePacket(String dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }

    public static void encode(TeleportWaystonePacket msg, PacketBuffer buf) {
        buf.writeUtf(msg.dim);
        buf.writeBlockPos(msg.pos);
    }

    public static TeleportWaystonePacket decode(PacketBuffer buf) {
        return new TeleportWaystonePacket(buf.readUtf(256), buf.readBlockPos());
    }

    public static void handle(TeleportWaystonePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) WaystoneManager.teleport(player, msg.dim, msg.pos);
        });
        ctx.get().setPacketHandled(true);
    }
}
