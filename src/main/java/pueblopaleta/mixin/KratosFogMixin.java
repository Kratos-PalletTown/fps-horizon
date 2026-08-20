package pueblopaleta.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pueblopaleta.KratosFog;

/**
 * NeoForge exposes ViewportEvent.RenderFog, fired from inside
 * FogRenderer#setupFog, which lets a listener overwrite the near/far plane
 * and cancel vanilla's own fog calculation. Fabric has no such event, so
 * this mixin injects at the TAIL of the same vanilla method (still
 * {@code setupFog(Camera, FogMode, float, boolean, float)} in 1.21.1 - the
 * FogParameters record rewrite only lands in 1.21.2) and lets KratosFog
 * overwrite the RenderSystem fog uniforms right after vanilla sets its own.
 */
@Mixin(FogRenderer.class)
public class KratosFogMixin
{
    @Inject(method = "setupFog", at = @At("TAIL"), require = 0)
    private static void kratos$applyFog(final Camera camera, final FogRenderer.FogMode fogMode,
                                         final float renderDistance, final boolean isFoggy,
                                         final float partialTick, final CallbackInfo ci) {
        final KratosFog fog = KratosFog.getInstance();
        if (fog != null) {
            fog.applyFog(camera);
        }
    }
}
