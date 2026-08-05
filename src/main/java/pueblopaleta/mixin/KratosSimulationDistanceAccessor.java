package pueblopaleta.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.OptionInstance;

/**
 * Accesses the maxInclusive field of IntRange from OptionInstance for Simulation Distance.
 * Allows detecting the actual maximum simulation distance supported.
 */
@Mixin(OptionInstance.IntRange.class)
public interface KratosSimulationDistanceAccessor {
    @Accessor("maxInclusive")
    int kratos$getSimMaxInclusive();
}
