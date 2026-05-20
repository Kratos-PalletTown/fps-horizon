package pueblopaleta;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

public class KratosBlink
{
    private long tiempoFinal;

    public KratosBlink() {
        this.tiempoFinal = 0L;
    }

    public void activar() {
        if (KratosConfig.BLINK_ACTIVO.get()) {
            this.tiempoFinal = System.currentTimeMillis() + (int)KratosConfig.BLINK_MS.get();
        }
    }

    public boolean estaActivo() {
        return System.currentTimeMillis() < this.tiempoFinal;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderOverlay(final RenderGuiOverlayEvent.Pre event) {
        if (!this.estaActivo()) {
            return;
        }
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            return;
        }
        final Minecraft mc = KratosOptimizer.getMC();
        if (mc.level == null) {
            return;
        }
        final GuiGraphics graphics = event.getGuiGraphics();
        final int w = mc.getWindow().getGuiScaledWidth();
        final int h = mc.getWindow().getGuiScaledHeight();
        RenderSystem.disableDepthTest();
        graphics.fill(0, 0, w, h, -16777216);
        RenderSystem.enableDepthTest();
    }
}
