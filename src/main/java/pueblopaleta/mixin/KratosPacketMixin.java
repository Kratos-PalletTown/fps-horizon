package pueblopaleta.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import pueblopaleta.KratosOptimizer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
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

    @Inject(method = { "m_143613_" }, at = { @At("HEAD") }, cancellable = true, remap = false, require = 0)
    private void kratos$blockChunkCacheRadiusSrg(final ClientboundSetChunkCacheRadiusPacket packet, final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            ci.cancel();
        }
    }
}
