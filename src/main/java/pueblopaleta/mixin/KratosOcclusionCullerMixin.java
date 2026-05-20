package pueblopaleta.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.viewport.CameraTransform;
import me.jellysquid.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
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
    private static void kratos$checkDistance(final CameraTransform camera,
                                              final RenderSection section,
                                              final float maxDistance,
                                              final CallbackInfoReturnable<Boolean> cir) {
        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()) return;
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        final double dist = KratosCulling.adjustedDistance(
            section.getOriginX(), section.getOriginY(), section.getOriginZ(),
            mc.player.getX(), mc.player.getY(), mc.player.getZ()
        );

        if (dist > KratosCulling.maxSqDist) {
            if ((boolean) KratosConfig.DEBUG_VERBOSE.get()) {
                KratosCulling.hiddenSections.add(
                    new BlockPos(section.getOriginX(), section.getOriginY(), section.getOriginZ()));
            }
            cir.setReturnValue(false);
        }
    }
}
