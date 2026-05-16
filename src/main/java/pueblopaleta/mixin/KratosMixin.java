package pueblopaleta.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import pueblopaleta.KratosOptimizer;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ LevelRenderer.class })
public class KratosMixin
{
    private static final Logger LOGGER = LogManager.getLogger("KratosMixin");
    private static Field lastViewDistanceField = null;

    private static Field getLastViewDistanceField() {
        if (lastViewDistanceField != null) {
            return lastViewDistanceField;
        }
        try {
            for (final Field f : LevelRenderer.class.getDeclaredFields()) {
                f.setAccessible(true);
                final String name = f.getName();
                if (name.equals("lastViewDistance") || name.equals("f_109541_")) {
                    lastViewDistanceField = f;
                    LOGGER.info("[Kratos] Campo lastViewDistance encontrado: {}", name);
                    return lastViewDistanceField;
                }
            }
        } catch (final Exception e) {
            LOGGER.error("[Kratos] Error buscando lastViewDistance: {}", e.getMessage());
        }
        return null;
    }

    private void syncLastViewDistance() {
        final Minecraft mc = KratosOptimizer.getMC();
        if (mc == null || mc.options == null || mc.level == null) {
            return;
        }
        final Field f = getLastViewDistanceField();
        if (f == null) {
            return;
        }
        try {
            final int rdActual = mc.options.renderDistance().get();
            final int current = (int)f.get(this);
            if (current != rdActual) {
                f.set(this, rdActual);
                LOGGER.info("[Kratos] lastViewDistance sincronizado: {} -> {}", current, rdActual);
            }
        } catch (final Exception e) {
            LOGGER.error("[Kratos] Error sincronizando lastViewDistance: {}", e.getMessage());
        }
    }

    @Inject(method = { "renderLevel" }, at = { @At("HEAD") }, require = 0)
    private void kratos$syncLastViewDistance(final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            this.syncLastViewDistance();
        }
    }

    @Inject(method = { "m_109599_" }, at = { @At("HEAD") }, remap = false, require = 0)
    private void kratos$syncLastViewDistanceSrg(final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            this.syncLastViewDistance();
        }
    }

    @Inject(method = { "allChanged" }, at = { @At("HEAD") }, cancellable = true, require = 0)
    private void kratos$blockSilentReload(final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            LOGGER.info("[Kratos] allChanged() CANCELADO (vanilla), silentFrames={}", KratosOptimizer.getSilentFrames());
            ci.cancel();
        }
    }

    @Inject(method = { "m_109818_" }, at = { @At("HEAD") }, cancellable = true, remap = false, require = 0)
    private void kratos$blockSilentReloadSrg(final CallbackInfo ci) {
        if (KratosOptimizer.isSilentChange()) {
            ci.cancel();
        }
    }
}
