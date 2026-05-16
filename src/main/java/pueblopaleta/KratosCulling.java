package pueblopaleta;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import java.util.HashSet;

public class KratosCulling
{
    public static volatile float xStretch  = 1.0f;
    public static volatile float yStretch  = 1.0f;
    public static volatile float cosAngle  = 1.0f;
    public static volatile float sinAngle  = 0.0f;
    public static volatile int   maxSqDist = Integer.MAX_VALUE;

    private float xStretchCurrent = 1.0f;
    private float yStretchCurrent = 1.0f;
    private int   maxSqDistCurrent = Integer.MAX_VALUE;

    // Ultimo valor de vertical aplicado — para detectar cambio significativo
    private float yStretchApplied = 1.0f;
    private int   lastRD = -1;

    // Referencia al fog para disparar la transicion
    private KratosFog fog;

    // Estado del cambio de culling
    private enum EstadoCulling { NORMAL, ESPERANDO_FOG }
    private EstadoCulling estadoCulling = EstadoCulling.NORMAL;
    private float yStretchPendiente = 1.0f;
    private float xStretchPendiente = 1.0f;

    public static HashSet<BlockPos> hiddenSections = new HashSet<>();
    public static int hiddenCount = 0;
    private long nextDebugUpdate = 0L;

    private static final float LERP_SPEED   = 0.08f;
    private static final float UMBRAL_CAMBIO = 0.5f; // diferencia minima para disparar fog

    public KratosCulling(final KratosFog fog) {
        this.fog = fog;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final Minecraft mc = KratosOptimizer.getMC();
        if (mc == null || mc.level == null || mc.player == null) {
            resetToDefault();
            return;
        }

        if (!(boolean) KratosConfig.CULLING_ACTIVO.get()) {
            resetToDefault();
            return;
        }

        final int currentRD = mc.options.renderDistance().get();
        if (currentRD != lastRD) {
            lastRD = currentRD;
        }

        // Angulo de la camara
        final float yawRad = -(float)(mc.player.getYRot() * Math.PI / 180.0);
        cosAngle = (float) Math.cos(yawRad);
        sinAngle = (float) Math.sin(yawRad);

        // Calcular targets
        int vSlider = KratosConfig.CULLING_VERTICAL.get();
        int hSlider = KratosConfig.CULLING_HORIZONTAL.get();

        // Sistema de perfiles (prioridad sobre dinamico)
        if ((boolean) KratosConfig.CULLING_PERFILES.get()) {
            final KratosProfiles.Profile profile = KratosProfiles.findForRD(currentRD);
            if (profile != null) {
                vSlider = profile.vertical;
                hSlider = profile.horizontal;
            } else {
                // Sin perfil para este RD - no aplicar culling
                resetToDefault();
                return;
            }
        } else if ((boolean) KratosConfig.CULLING_DINAMICO.get()) {
            // Culling dinamico experimental
            if (currentRD <= 2) {
                vSlider = 2;
                hSlider = 30;
            } else if (currentRD == 3) {
                vSlider = 2;
                hSlider = 40;
            }
        }

        final double vScaling = (double) vSlider / 4.0;
        final float yStretchTarget = (float)(vScaling * vScaling);
        final double hScaling = 1.0 + (double) hSlider / 100.0;
        final float xStretchTarget = (float)(hScaling * hScaling);
        final int rd = currentRD * 16;
        final int maxSqDistTarget = rd * rd + 1;

        switch (this.estadoCulling) {
            case NORMAL -> {
                // Detectar cambio significativo de vertical
                if (Math.abs(yStretchTarget - yStretchApplied) > UMBRAL_CAMBIO
                        && fog.estaListo()) {
                    // Guardar pendientes y disparar fog
                    this.yStretchPendiente = yStretchTarget;
                    this.xStretchPendiente = xStretchTarget;
                    this.estadoCulling = EstadoCulling.ESPERANDO_FOG;
                    fog.iniciarCierre(currentRD, () -> {
                        // Aplicar el nuevo culling cuando el fog esta cerrado
                        yStretchCurrent  = yStretchPendiente;
                        xStretchCurrent  = xStretchPendiente;
                        yStretchApplied  = yStretchPendiente;
                        yStretch         = yStretchCurrent;
                        xStretch         = xStretchCurrent;
                        fog.iniciarApertura();
                        this.estadoCulling = EstadoCulling.NORMAL;
                    });
                } else if (Math.abs(yStretchTarget - yStretchApplied) <= UMBRAL_CAMBIO) {
                    // Cambio pequeño — interpolar suavemente sin fog
                    xStretchCurrent = lerp(xStretchCurrent, xStretchTarget, LERP_SPEED);
                    yStretchCurrent = lerp(yStretchCurrent, yStretchTarget, LERP_SPEED);
                    yStretchApplied = yStretchCurrent;
                }
            }
            case ESPERANDO_FOG -> {
                // Mantener valores actuales mientras el fog hace su trabajo
            }
        }

        maxSqDistCurrent = (int) lerp(maxSqDistCurrent, maxSqDistTarget, LERP_SPEED);
        xStretch  = xStretchCurrent;
        yStretch  = yStretchCurrent;
        maxSqDist = maxSqDistCurrent;

        // Debug
        if ((boolean) KratosConfig.DEBUG_VERBOSE.get()) {
            final long gameTime = mc.level.getGameTime();
            if (gameTime > nextDebugUpdate) {
                nextDebugUpdate = gameTime + 40L;
                if (!hiddenSections.isEmpty()) {
                    hiddenCount = hiddenSections.size();
                    hiddenSections.clear();
                }
            }
        }
    }

    private void resetToDefault() {
        xStretchCurrent  = 1.0f;
        yStretchCurrent  = 1.0f;
        maxSqDistCurrent = Integer.MAX_VALUE;
        xStretch  = 1.0f;
        yStretch  = 1.0f;
        maxSqDist = Integer.MAX_VALUE;
        cosAngle  = 1.0f;
        sinAngle  = 0.0f;
        lastRD    = -1;
        yStretchApplied = 1.0f;
        estadoCulling = EstadoCulling.NORMAL;
    }

    private static float lerp(final float a, final float b, final float t) {
        return a + (b - a) * t;
    }

    public static double adjustedDistance(final int x1, final int y1, final int z1,
                                          final double x2, final double y2, final double z2) {
        final double x2New = (x2 - x1) * cosAngle - (z2 - z1) * sinAngle + x1;
        final double z2New = (x2 - x1) * sinAngle + (z2 - z1) * cosAngle + z1;
        final double d0 = x1 - x2New;
        final double d2 = y1 - y2;
        final double d3 = z1 - z2New;
        return xStretch * d0 * d0 + yStretch * (d2 * d2) + d3 * d3;
    }

    public static double adjustedDistance(final BlockPos chunk, final BlockPos player) {
        return adjustedDistance(chunk.getX(), chunk.getY(), chunk.getZ(),
                                player.getX(), player.getY(), player.getZ());
    }
}
