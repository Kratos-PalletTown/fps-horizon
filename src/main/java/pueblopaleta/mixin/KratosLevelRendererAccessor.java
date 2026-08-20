package pueblopaleta.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Fabric has no equivalent of NeoForge's AccessTransformer, so the original
 * project's META-INF/accesstransformer.cfg (which widened
 * LevelRenderer.lastViewDistance to public) is replaced by this standard
 * Mixin @Accessor. Since Loom compiles against official Mojang mappings, the
 * field name is the same one used in the NeoForge source.
 */
@Mixin(LevelRenderer.class)
public interface KratosLevelRendererAccessor
{
    @Accessor("lastViewDistance")
    int kratos$getLastViewDistance();

    @Accessor("lastViewDistance")
    void kratos$setLastViewDistance(int value);
}
