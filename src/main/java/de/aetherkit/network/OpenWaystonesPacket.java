package de.aetherkit.network;

import de.aetherkit.system.WaystoneManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Server -> Client: öffnet das Wegstein-Menü mit allen entdeckten Wegsteinen. */
public class OpenWaystonesPacket {
    public final List<WaystoneManager.Entry> entries;
    public final String currentDim;
    public final BlockPos currentPos;

    public OpenWaystonesPacket(List<WaystoneManager.Entry> entries, String currentDim, BlockPos currentPos) {
        this.entries = entries;
        this.currentDim = currentDim;
        this.currentPos = currentPos;
    }

    public static void encode(OpenWaystonesPacket msg, PacketBuffer buf) {
        buf.writeUtf(msg.currentDim);
        buf.writeBlockPos(msg.currentPos);
        buf.writeVarInt(msg.entries.size());
        for (WaystoneManager.Entry e : msg.entries) {
            buf.writeUtf(e.dim);
            buf.writeBlockPos(e.pos);
            buf.writeUtf(e.name);
        }
    }

    public static OpenWaystonesPacket decode(PacketBuffer buf) {
        String dim = buf.readUtf(256);
        BlockPos pos = buf.readBlockPos();
        int n = Math.min(buf.readVarInt(), 512);
        List<WaystoneManager.Entry> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(new WaystoneManager.Entry(buf.readUtf(256), buf.readBlockPos(), buf.readUtf(64)));
        }
        return new OpenWaystonesPacket(list, dim, pos);
    }

    public static void handle(OpenWaystonesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> de.aetherkit.client.ClientPacketHandler.openWaystones(msg)));
        ctx.get().setPacketHandled(true);
    }
}
