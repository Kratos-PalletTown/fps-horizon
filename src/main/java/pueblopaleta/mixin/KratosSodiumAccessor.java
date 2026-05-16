package pueblopaleta.mixin;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { SodiumWorldRenderer.class }, remap = false)
public interface KratosSodiumAccessor
{
    @Accessor("renderDistance")
    int kratos$getRenderDistance();

    @Mutable
    @Accessor("renderDistance")
    void kratos$setRenderDistance(int p0);
}
