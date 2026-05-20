package pueblopaleta.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.OptionInstance;

/**
 * Accesses the maxInclusive field of IntRange from OptionInstance.
 * This allows us to detect the actual maximum render distance supported,
 * including mods like Farsight that extend the limit beyond 32.
 */
@Mixin(OptionInstance.IntRange.class)
public interface KratosRenderDistanceAccessor {
    @Accessor("maxInclusive")
    int kratos$getMaxInclusive();
}
