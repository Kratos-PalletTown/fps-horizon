package pueblopaleta.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.Minecraft;
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

    private ChunkRenderDispatcher.RenderChunk kratos$currentChunk;

    @Redirect(method = "renderChunkLayer",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;getCompiledChunk()Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;"),
              require = 0)
    private ChunkRenderDispatcher.CompiledChunk kratos$trackChunk(final ChunkRenderDispatcher.RenderChunk chunk) {
        this.kratos$currentChunk = chunk;
        return chunk.getCompiledChunk();
    }

    @Redirect(method = "renderChunkLayer",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;isEmpty(Lnet/minecraft/client/renderer/RenderType;)Z"),
              require = 0)
    private boolean kratos$cullChunk(final ChunkRenderDispatcher.CompiledChunk compiled, final RenderType type) {
        if (compiled.isEmpty(type)) return true;
        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()) return false;
        if (this.minecraft.player == null || this.kratos$currentChunk == null) return false;

        final BlockPos chunkPos = this.kratos$currentChunk.getOrigin();
        final double dist = KratosCulling.adjustedDistance(
            chunkPos.getX(), chunkPos.getY(), chunkPos.getZ(),
            this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ()
        );

        if (dist > KratosCulling.maxSqDist) {
            if ((boolean) KratosConfig.DEBUG_VERBOSE.get()) {
                KratosCulling.hiddenSections.add(chunkPos);
            }
            return true;
        }
        return false;
    }
}
