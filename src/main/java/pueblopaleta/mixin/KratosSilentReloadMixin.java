package pueblopaleta.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pueblopaleta.KratosOptimizer;

/**
 * Fabric rewrite of the original KratosMixin. NeoForge's mixin annotation
 * processor keeps Mojang-mapped method names at all times, which is why the
 * original file also injected into SRG names (m_109599_, m_109818_) as a
 * defensive fallback. Fabric Loom's refmap remapping makes that unnecessary
 * - a single injection per method, using the accessor above instead of
 * reflection, is enough.
 */
@Mixin(LevelRenderer.class)
public abstract class KratosSilentReloadMixin
{
    private static final Logger LOGGER = LogManager.getLogger("KratosMixin");

    @Inject(method = "renderLevel", at = @At("HEAD"), require = 0)
    private void kratos$syncLastViewDistance(final CallbackInfo ci) {
        if (!KratosOptimizer.isSilentChange()) return;

        final Minecraft mc = KratosOptimizer.getMC();
        if (mc == null || mc.options == null || mc.level == null) return;

        final KratosLevelRendererAccessor self = (KratosLevelRendererAccessor)(Object) this;
        final int rdActual = mc.options.renderDistance().get();
        final int current = self.kratos$getLastViewDistance();
        if (current != rdActual) {
            self.kratos$setLastViewDistance(rdActual);
            LOGGER.info("[Kratos] lastViewDistance sincronizado: {} -> {}", current, rdActual);
        }
    }

    @Inject(method = "allChanged", at = @At("HEAD"), cancellable = true, require = 0)
    private void kratos$blockSilentReload(final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            LOGGER.info("[Kratos] allChanged() CANCELADO (vanilla), silentFrames={}", KratosOptimizer.getSilentFrames());
            ci.cancel();
        }
    }
}
