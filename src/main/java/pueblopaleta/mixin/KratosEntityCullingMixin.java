package pueblopaleta.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pueblopaleta.KratosConfig;
import pueblopaleta.KratosCulling;

@Mixin(Entity.class)
public class KratosEntityCullingMixin
{
    @Inject(method = "shouldRender",
            at = @At("HEAD"),
            cancellable = true,
            require = 0)
    private void kratos$cullEntity(final double camX, final double camY, final double camZ,
                                    final CallbackInfoReturnable<Boolean> cir) {
        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()) return;
        if (!(boolean) KratosConfig.CULLING_ENTIDADES.get()) return;

        final Entity self = (Entity)(Object)this;
        final double dist = KratosCulling.adjustedDistance(
            (int) self.getX(), (int) self.getY(), (int) self.getZ(),
            camX, camY, camZ
        );
        if (dist > KratosCulling.maxSqDist) {
            cir.setReturnValue(false);
        }
    }
}
