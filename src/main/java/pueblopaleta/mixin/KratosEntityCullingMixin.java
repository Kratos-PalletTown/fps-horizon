package pueblopaleta.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pueblopaleta.KratosConfig;
import pueblopaleta.KratosCulling;

@Mixin(Entity.class)
public class KratosEntityCullingMixin
{
    @Redirect(method = "shouldRender",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/entity/Entity;shouldRenderAtSqrDistance(D)Z"),
              require = 0)
    private boolean kratos$adaptEntityDist(final Entity instance,
                                            final double originalSqDist,
                                            final double camX, final double camY, final double camZ) {
        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()
                || !(boolean) KratosConfig.CULLING_ENTIDADES.get()
                || KratosCulling.INV_R2_XZ <= 0.0) {
            return instance.shouldRenderAtSqrDistance(originalSqDist);
        }
        // Use ellipsoid test for entity culling
        if (!KratosCulling.isVisible(
                instance.getBlockX(), instance.getBlockY(), instance.getBlockZ(),
                camX, camY, camZ)) {
            return false;
        }
        return instance.shouldRenderAtSqrDistance(originalSqDist);
    }
}
