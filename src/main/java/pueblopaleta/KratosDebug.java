package pueblopaleta;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class KratosDebug
{
    public static void mostrar(final int antes, final int despues) {
        if (!(boolean)KratosConfig.MOSTRAR_DEBUG.get()) {
            return;
        }
        final Minecraft mc = KratosOptimizer.getMC();
        if (mc.player == null) {
            return;
        }
        final String direccion = (despues > antes) ? "\u00a7a\u25b2" : "\u00a7c\u25bc";
        final String mensaje = String.format("\u00a77[Kratos] \u00a7fRD: \u00a7e%d \u00a77-> %s\u00a7f%d", antes, direccion, despues);
        mc.player.displayClientMessage(Component.literal(mensaje), true);
    }
}
