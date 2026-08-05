package pueblopaleta.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
import me.jellysquid.mods.sodium.client.render.viewport.CameraTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pueblopaleta.KratosConfig;
import pueblopaleta.KratosCulling;

@Mixin(value = OcclusionCuller.class, remap = false)
public class KratosOcclusionCullerMixin
{
    @Inject(method = "isWithinRenderDistance",
            at = @At("HEAD"),
            remap = false,
            cancellable = true,
            require = 0)
    private static void kratos$cullEllipsoid(final CameraTransform camera,
                                              final RenderSection section,
                                              final float maxDistance,
                                              final CallbackInfoReturnable<Boolean> cir) {
        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()) return;
        if (KratosCulling.INV_R2_XZ <= 0.0) return;

        if (!KratosCulling.isVisible(
                section.getOriginX(), section.getOriginY(), section.getOriginZ(),
                camera.x, camera.y, camera.z)) {
            if ((boolean) KratosConfig.DEBUG_VERBOSE.get()) {
                KratosCulling.hiddenCount++;
            }
            cir.setReturnValue(false);
        }
    }
}
