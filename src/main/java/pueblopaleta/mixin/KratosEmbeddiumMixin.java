package pueblopaleta.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import pueblopaleta.KratosOptimizer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.apache.logging.log4j.Logger;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { SodiumWorldRenderer.class }, remap = false)
public class KratosEmbeddiumMixin
{
    private static final Logger LOGGER = LogManager.getLogger("KratosEmbeddiumMixin");

    @Inject(method = { "reload" }, at = { @At("HEAD") }, cancellable = true, require = 0)
    private void kratos$blockSodiumReload(final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            LOGGER.debug("[Kratos] SodiumWorldRenderer.reload() CANCELADO (Embeddium), silentFrames={}", KratosOptimizer.getSilentFrames());
            ci.cancel();
        }
    }
}
