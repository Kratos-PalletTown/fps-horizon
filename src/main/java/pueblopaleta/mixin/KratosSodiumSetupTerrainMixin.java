package pueblopaleta.mixin;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pueblopaleta.KratosOptimizer;

import java.lang.reflect.Field;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class KratosSodiumSetupTerrainMixin
{
    private static Field rsmField = null;

    @Inject(method = "setupTerrain", at = @At("HEAD"), remap = false, require = 0)
    private void kratos$syncRenderDistance(final CallbackInfo ci) {
        if (!KratosOptimizer.isSilentChange()) return;

        final Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null) return;

        final int viewDist = mc.options.renderDistance().get();

        try {
            // Sync renderDistance in SodiumWorldRenderer itself
            ((KratosSodiumAccessor)(Object)this).kratos$setRenderDistance(viewDist);

            // Sync RenderSectionManager and mark graph dirty
            if (rsmField == null) {
                rsmField = this.getClass().getDeclaredField("renderSectionManager");
                rsmField.setAccessible(true);
            }
            final Object rsm = rsmField.get(this);
            if (rsm instanceof KratosRenderSectionManagerAccessor accessor) {
                accessor.kratos$setRenderDistance(viewDist);
                accessor.kratos$invokeMarkGraphDirty();
            }
        } catch (final Throwable t) {
            // Embeddium API mismatch - ignore silently
        }
    }
}
