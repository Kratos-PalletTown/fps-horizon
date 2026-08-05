package pueblopaleta.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pueblopaleta.KratosConfig;
import pueblopaleta.KratosCulling;

@Mixin(LevelRenderer.class)
public class KratosChunkCullingMixin
{
    @Shadow @Final private Minecraft minecraft;
    private SectionRenderDispatcher.RenderSection kratos$current = null;

    @Redirect(method = "renderChunkLayer",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;getCompiledSection()Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;"),
              require = 0)
    public SectionRenderDispatcher.CompiledSection kratos$trackChunk(final SectionRenderDispatcher.RenderSection chunk) {
        kratos$current = chunk;
        return chunk.getCompiled();
    }

    @Redirect(method = "renderChunkLayer",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;isEmpty(Lnet/minecraft/client/renderer/RenderType;)Z"),
              require = 0)
    public boolean kratos$cullChunk(final SectionRenderDispatcher.CompiledSection compiled, final RenderType type) {
        if (compiled.isEmpty(type)) return true;
        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()) return false;
        if (minecraft.cameraEntity == null || kratos$current == null) return false;
        if (KratosCulling.INV_R2_XZ <= 0.0) return false;

        final BlockPos origin = kratos$current.getOrigin();
        if (!KratosCulling.isVisible(
                origin.getX(), origin.getY(), origin.getZ(),
                minecraft.cameraEntity.getX(),
                minecraft.cameraEntity.getY(),
                minecraft.cameraEntity.getZ())) {
            return true;
        }
        return false;
    }
}
