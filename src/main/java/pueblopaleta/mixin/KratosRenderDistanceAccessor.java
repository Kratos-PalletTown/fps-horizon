package pueblopaleta.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.OptionInstance;

/**
 * Accesses the maxInclusive field of IntRange from OptionInstance.
 * Used for both render distance and simulation distance max detection.
 */
@Mixin(OptionInstance.IntRange.class)
public interface KratosRenderDistanceAccessor {
    @Accessor("maxInclusive")
    int kratos$getMaxInclusive();
}
