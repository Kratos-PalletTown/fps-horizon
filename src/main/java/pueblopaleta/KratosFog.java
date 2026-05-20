package pueblopaleta;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraft.client.Minecraft;

public class KratosFog
{
    private EstadoFog estado;

    // Valores actuales interpolados (hilo de render los lee)
    private volatile float fogEndActual;
    private volatile float fogStartActual;

    // Targets hacia donde interpolar
    private float fogEndTarget;
    private float fogStartTarget;

    private Runnable onFogCerrado;

    // Flag para saber si ya inicializamos con valores reales
    private boolean inicializado;

    public KratosFog() {
        this.estado = EstadoFog.NORMAL;
        this.fogEndActual   = -1.0f;
        this.fogStartActual = -1.0f;
        this.fogEndTarget   = -1.0f;
        this.fogStartTarget = -1.0f;
        this.onFogCerrado   = null;
        this.inicializado   = false;
    }

    private float calcFogEnd(final int rd) {
        final float configEnd = (float)(double) KratosConfig.FOG_END.get();
        return rd * 16.0f * configEnd;
    }

    private float calcFogStart() {
        return Math.max((float)(int) KratosConfig.FOG_START_BLOQUES.get(), 0.1f);
    }

    /** Llamado cuando hay que tapar los chunks antes de cambiar el RD */
    public void iniciarCierre(final int rdNuevo, final Runnable callback) {
        this.onFogCerrado = callback;
        this.estado = EstadoFog.CERRANDO;
        final float fogEndNuevo = this.calcFogEnd(rdNuevo);
        final float fracCierre  = (float)(double) KratosConfig.FOG_CIERRE_END.get();
        this.fogEndTarget   = Math.max(fogEndNuevo * fracCierre, 1.0f);
        this.fogStartTarget = Math.max(this.fogStartActual * fracCierre, 0.1f);
    }

    /** Llamado después de aplicar el cambio de RD para abrir la niebla de vuelta */
    public void iniciarApertura() {
        this.estado = EstadoFog.ABRIENDO;
    }

    public boolean estaListo() {
        return this.estado == EstadoFog.NORMAL;
    }

    /** Tick — corre en el hilo del cliente, 20 veces por segundo */
    public void tick() {
        final Minecraft mc = KratosOptimizer.getMC();
        if (mc == null || mc.level == null) return;

        final int rdActual = mc.options.renderDistance().get();

        // Primera vez: inicializar con los valores reales en vez de -1
        if (!this.inicializado) {
            this.fogEndActual   = this.calcFogEnd(rdActual);
            this.fogStartActual = this.calcFogStart();
            this.fogEndTarget   = this.fogEndActual;
            this.fogStartTarget = this.fogStartActual;
            this.inicializado   = true;
            return;
        }

        final float velocidad = (float)(double) KratosConfig.FOG_VELOCIDAD_LERP.get();

        switch (this.estado) {
            case CERRANDO: {
                this.fogEndActual   = lerp(this.fogEndActual,   this.fogEndTarget,   velocidad);
                this.fogStartActual = lerp(this.fogStartActual, this.fogStartTarget, velocidad);
                if (Math.abs(this.fogEndActual - this.fogEndTarget) < 0.5f) {
                    this.fogEndActual   = this.fogEndTarget;
                    this.fogStartActual = this.fogStartTarget;
                    this.estado = EstadoFog.CERRADO;
                    if (this.onFogCerrado != null) {
                        this.onFogCerrado.run();
                        this.onFogCerrado = null;
                    }
                }
                break;
            }
            case CERRADO: {
                // Mantener fijo hasta que se llame iniciarApertura()
                this.fogEndActual   = this.fogEndTarget;
                this.fogStartActual = this.fogStartTarget;
                break;
            }
            case ABRIENDO: {
                final float targetEnd   = this.calcFogEnd(rdActual);
                final float targetStart = this.calcFogStart();
                this.fogEndActual   = lerp(this.fogEndActual,   targetEnd,   velocidad);
                this.fogStartActual = lerp(this.fogStartActual, targetStart, velocidad);
                if (Math.abs(this.fogEndActual - targetEnd) < 0.5f) {
                    this.fogEndActual   = targetEnd;
                    this.fogStartActual = targetStart;
                    this.estado = EstadoFog.NORMAL;
                }
                break;
            }
            case NORMAL:
            default: {
                // Seguir suavemente el RD actual por si cambia manualmente en opciones
                final float targetEnd   = this.calcFogEnd(rdActual);
                final float targetStart = this.calcFogStart();
                this.fogEndActual   = lerp(this.fogEndActual,   targetEnd,   0.05f);
                this.fogStartActual = lerp(this.fogStartActual, targetStart, 0.05f);
                break;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderFog(final ViewportEvent.RenderFog event) {
        if (!(boolean) KratosConfig.NIEBLA_ACTIVA.get()) return;
        if (event.getCamera().getFluidInCamera() != FogType.NONE) return;

        final Minecraft mc = KratosOptimizer.getMC();
        if (mc == null || mc.level == null) return;

        // Si todavía no inicializamos, dejar que Minecraft maneje el fog
        if (!this.inicializado) return;

        float farPlane  = this.fogEndActual;
        float nearPlane = this.fogStartActual;

        // Sanidad mínima
        if (farPlane < 1.0f) farPlane = 1.0f;
        if (nearPlane < 0.0f) nearPlane = 0.0f;
        if (nearPlane >= farPlane) nearPlane = farPlane - 0.5f;

        event.setNearPlaneDistance(nearPlane);
        event.setFarPlaneDistance(farPlane);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    private static float lerp(final float a, final float b, final float t) {
        return a + (b - a) * t;
    }

    public EstadoFog getEstado() {
        return this.estado;
    }

    public enum EstadoFog {
        NORMAL,
        CERRANDO,
        CERRADO,
        ABRIENDO
    }
}
