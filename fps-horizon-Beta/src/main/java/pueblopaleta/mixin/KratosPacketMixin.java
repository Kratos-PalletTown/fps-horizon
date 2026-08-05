package pueblopaleta.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import pueblopaleta.KratosOptimizer;
import pueblopaleta.KratosChunkRetainer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientPacketListener.class })
public class KratosPacketMixin
{
    private static final Logger LOGGER = LogManager.getLogger("KratosPacketMixin");

    @Inject(method = { "handleSetChunkCacheRadius" }, at = { @At("HEAD") }, cancellable = true, require = 0)
    private void kratos$blockChunkCacheRadius(final ClientboundSetChunkCacheRadiusPacket packet, final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            LOGGER.info("[Kratos] handleSetChunkCacheRadius BLOQUEADO durante silentChange");
            ci.cancel();
        }
    }

    @Inject(method = { "handleForgetLevelChunk" }, at = { @At("HEAD") }, cancellable = true, require = 0)
    private void kratos$retainChunk(final ClientboundForgetLevelChunkPacket packet, final CallbackInfo ci) {
        final KratosChunkRetainer retainer = KratosChunkRetainer.getInstance();
        if (retainer == null) return;

        // Give retainer a reference to this packet listener for deferred unloads
        if (retainer.packetListener == null) {
            retainer.packetListener = (ClientPacketListener)(Object)this;
        }

        // If retainer wants to hold this chunk, suppress unload
        if (!retainer.unloading && retainer.tryRetain(packet)) {
            LOGGER.debug("[Kratos] Chunk ({},{}) retenido durante subida de RD",
                packet.getX(), packet.getZ());
            ci.cancel();
        }
    }
}
