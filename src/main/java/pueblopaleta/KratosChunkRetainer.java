package pueblopaleta;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Delays chunk unloading when RD is increasing, so the player sees
 * the old chunks while new ones load — no visible pop-in or blank areas.
 *
 * Based on Farsight's delayUnload approach, adapted for FPS Horizon's
 * dynamic RD system. Only active when rdGoingUp = true.
 */
public class KratosChunkRetainer
{
    // Chunks the server told us to unload, but we're holding temporarily
    private final Long2ObjectOpenHashMap<ClientboundForgetLevelChunkPacket> retained =
        new Long2ObjectOpenHashMap<>();

    // How many extra chunks beyond RD to keep loaded
    private static final int LEEWAY = 4;

    // Whether we are currently processing a real unload (to avoid recursion)
    public boolean unloading = false;

    // Reference to packet listener, set by mixin
    public net.minecraft.client.multiplayer.ClientPacketListener packetListener = null;

    private static KratosChunkRetainer instance;

    public KratosChunkRetainer() {
        instance = this;
    }

    public static KratosChunkRetainer getInstance() {
        return instance;
    }

    /**
     * Called from KratosPacketMixin when a ForgetLevelChunk packet arrives.
     * Returns true if we retained the chunk (caller should suppress the packet).
     * Returns false if the chunk should be unloaded normally.
     */
    public boolean tryRetain(final ClientboundForgetLevelChunkPacket packet) {
        if (unloading) return false;

        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            retained.clear();
            return false;
        }

        // Only retain chunks when RD is going up
        if (!KratosOptimizer.rdGoingUp) {
            // RD not going up — process any retained chunks that are now far enough
            flushFarChunks(mc);
            return false;
        }

        final int currentRD = mc.options.renderDistance().get();
        final ChunkPos playerChunk = mc.player.chunkPosition();
        final ChunkPos chunkPos = new ChunkPos(packet.getX(), packet.getZ());

        // If chunk is within RD + LEEWAY, retain it
        final int dist = Math.max(
            Math.abs(chunkPos.x - playerChunk.x),
            Math.abs(chunkPos.z - playerChunk.z)
        );

        if (dist <= currentRD + LEEWAY) {
            retained.put(ChunkPos.asLong(packet.getX(), packet.getZ()), packet);
            return true; // suppress unload
        }

        return false;
    }

    /**
     * Flush chunks that are now beyond RD + LEEWAY (actually unload them).
     */
    private void flushFarChunks(final Minecraft mc) {
        if (retained.isEmpty() || mc.player == null) return;
        final int currentRD = mc.options.renderDistance().get();
        final ChunkPos playerChunk = mc.player.chunkPosition();

        final var iter = retained.long2ObjectEntrySet().fastIterator();
        while (iter.hasNext()) {
            final var entry = iter.next();
            final long key = entry.getLongKey();
            final int cx = ChunkPos.getX(key);
            final int cz = ChunkPos.getZ(key);
            final int dist = Math.max(Math.abs(cx - playerChunk.x), Math.abs(cz - playerChunk.z));

            if (dist > currentRD + LEEWAY) {
                unloading = true;
                if (packetListener != null) {
                    packetListener.handleForgetLevelChunk(entry.getValue());
                }
                unloading = false;
                iter.remove();
            }
        }
    }

    /**
     * Called every tick to flush chunks that are now too far away.
     */
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            retained.clear();
            return;
        }
        flushFarChunks(mc);
    }

    public void clear() {
        retained.clear();
    }
}
