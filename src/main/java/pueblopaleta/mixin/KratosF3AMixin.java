package pueblopaleta.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import pueblopaleta.KratosOptimizer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ KeyboardHandler.class })
public class KratosF3AMixin
{
    private static final Logger LOGGER = LogManager.getLogger("KratosF3AMixin");

    @Inject(method = { "handleDebugKeys" }, at = { @At("HEAD") }, cancellable = true, require = 0)
    private void kratos$interceptF3A(final int key, final CallbackInfoReturnable<Boolean> cir) {
        if (key == 65 && KratosOptimizer.isSilentChange()) {
            LOGGER.info("[Kratos] F3+A bloqueado durante cambio silencioso");
            cir.setReturnValue(true);
        }
    }
}
