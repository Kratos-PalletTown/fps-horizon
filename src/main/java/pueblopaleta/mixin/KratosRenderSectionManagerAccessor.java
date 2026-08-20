package pueblopaleta.mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RenderSectionManager.class, remap = false)
public interface KratosRenderSectionManagerAccessor
{
    @Accessor("renderDistance")
    int kratos$getRenderDistance();

    @Mutable
    @Accessor("renderDistance")
    void kratos$setRenderDistance(int distance);

    @Invoker("markGraphDirty")
    void kratos$invokeMarkGraphDirty();
}
