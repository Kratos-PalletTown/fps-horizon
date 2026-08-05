package pueblopaleta;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class KratosCulling
{
    // Precalculated per-frame values (written in tick, read in mixins)
    public static volatile double R2_XZ   = Double.MAX_VALUE;
    public static volatile double INV_R2_XZ = 0.0;
    public static volatile double INV_R2_Y  = 0.0;

    // Debug
    public static int hiddenCount = 0;

    private int lastRD = -1;

    private KratosFog fog;

    public KratosCulling(final KratosFog fog) {
        this.fog = fog;
    }

    @SubscribeEvent
    public void onClientTick(final ClientTickEvent.Post event) {

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

        // Horizontal radius: use fog end actual + extra margin
        // This ensures culling always happens BEHIND the fog — chunks that
        // disappear are already invisible to the player
        final float fogEnd = fog.getFogEndActual();
        final double radiusFactor = KratosConfig.CULLING_RADIUS_FACTOR.get();
        final int extraBlocks    = KratosConfig.CULLING_EXTRA_BLOCKS.get();

        double radiusH;
        if (fogEnd > 0.0f) {
            radiusH = fogEnd * radiusFactor + extraBlocks;
        } else {
            // Fallback if fog not initialized yet
            radiusH = currentRD * 16.0 * radiusFactor + extraBlocks;
        }
        // Minimum radius: 2 chunks
        if (radiusH < 32.0) radiusH = 32.0;

        prevR2XZ = R2_XZ;
        R2_XZ    = radiusH * radiusH;
        INV_R2_XZ = 1.0 / R2_XZ;

        // Vertical radius: auto-scale based on RD same as Better Render Distance
        final double verticalScale = computeAutoVerticalScale(currentRD);
        final double radiusV = radiusH * verticalScale;
        INV_R2_Y = 1.0 / (radiusV * radiusV);

        // Notify Embeddium if radius changed significantly
        if (Math.abs(R2_XZ - prevR2XZ) > 1024.0) {
            notifyEmbeddium(mc);
        }

        if ((boolean) KratosConfig.DEBUG_VERBOSE.get()) {
            hiddenCount = 0; // reset each tick, mixins increment it
        }
    }

    private double prevR2XZ = Double.MAX_VALUE;

    private void notifyEmbeddium(final Minecraft mc) {
        try {
            final Object renderer = mc.levelRenderer;
            final java.lang.reflect.Method getter = renderer.getClass()
                .getMethod("sodium");
            final Object sodium = getter.invoke(renderer);
            if (sodium != null) {
                sodium.getClass().getMethod("scheduleTerrainUpdate").invoke(sodium);
            }
        } catch (final Throwable ignored) {}
    }

    /**
     * Auto vertical scale based on render distance — adapted from Better Render Distance.
     * At low RD the vertical scale is near 1.0 (sphere).
     * At high RD it shrinks to 0.5 (more aggressive vertical culling).
     */
    public static double computeAutoVerticalScale(int rd) {
        // Piecewise smooth curve: RD 2=1.0, RD 8=0.8, RD 32=0.6, RD 64=0.5
        return piecewise(rd, 2, 1.0, 8, 0.8, 32, 0.6, 64, 0.5);
    }

    private static double piecewise(int x,
                                     int x0, double y0,
                                     int x1, double y1,
                                     int x2, double y2,
                                     int x3, double y3) {
        if (x <= x0) return y0;
        if (x >= x3) return y3;
        if (x <= x1) return lerp(y0, y1, smoothstep((double)(x - x0) / (x1 - x0)));
        if (x <= x2) return lerp(y1, y2, smoothstep((double)(x - x1) / (x2 - x1)));
        return lerp(y2, y3, smoothstep((double)(x - x2) / (x3 - x2)));
    }

    private static double smoothstep(double t) { return t * t * (2.0 - t); }
    private static double lerp(double a, double b, double t) { return a + (b - a) * t; }

    /**
     * Ellipsoid test — same approach as Better Render Distance.
     * Uses the nearest point of the chunk section to the camera in Y,
     * avoiding false culling of tall sections.
     *
     * Returns true if the section should be VISIBLE (within ellipsoid).
     */
    public static boolean isVisible(final int sectionOriginX, final int sectionOriginY,
                                     final int sectionOriginZ,
                                     final double camX, final double camY, final double camZ) {
        // Center of section in XZ
        final double cx  = sectionOriginX + 8.0;
        final double cz  = sectionOriginZ + 8.0;
        final double dx  = cx - camX;
        final double dz  = cz - camZ;
        final double d2xz = dx * dx + dz * dz;

        // Fast XZ rejection
        if (d2xz > R2_XZ) return false;

        // Nearest point of section to camera in Y (section spans originY to originY+16)
        final double oy = sectionOriginY - camY;
        final double dy = nearestToZero(oy - 1.0, oy + 17.0);

        // Ellipsoid norm test
        final double norm = d2xz * INV_R2_XZ + dy * dy * INV_R2_Y;
        return norm <= 1.0;
    }

    /** Returns the value closest to zero between a and b */
    private static double nearestToZero(final double a, final double b) {
        return a * a <= b * b ? a : b;
    }

    private void resetToDefault() {
        R2_XZ     = Double.MAX_VALUE;
        INV_R2_XZ = 0.0;
        INV_R2_Y  = 0.0;
        lastRD    = -1;
    }
}
