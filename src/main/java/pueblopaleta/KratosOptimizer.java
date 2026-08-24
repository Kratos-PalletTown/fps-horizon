package pueblopaleta;

import java.lang.reflect.Method;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Orchestrator for the whole mod, ported from the NeoForge @Mod class.
 *
 * On NeoForge this class registered itself plus KratosCulling / KratosSimulation
 * / KratosChunkRetainer / KratosFog on the NeoForge.EVENT_BUS and each got its
 * own ClientTickEvent.Post callback. Fabric API only exposes one client tick
 * event (ClientTickEvents.END_CLIENT_TICK), so FpsHorizonClient registers a
 * single callback that calls {@link #tick()} here, and this class fans out
 * to the sub-systems in the same order they used to fire in.
 */
public class KratosOptimizer
{
    private static final AtomicInteger silentFrames = new AtomicInteger(0);
    public static volatile boolean rdGoingUp = false;
    private static Minecraft mcInstance = null;

    private Estado estado;
    private int[] fpsSamples;
    private int sampleIndex;
    private int samplesCollected;
    private int cooldownRestante;
    private int chunkWaitTimeout;
    private static final int MAX_CHUNK_WAIT = 100;
    private int rdPendienteAntes;
    private int rdPendienteDespues;

    private final KratosFog fog;
    private final KratosCulling culling;
    private final KratosSimulation simulation;
    private final KratosChunkRetainer retainer;

    public static Minecraft getMC() {
        return mcInstance;
    }

    public static int getSilentFrames() {
        return silentFrames.get();
    }

    public static void consumirCancelacion() {
        silentFrames.decrementAndGet();
    }

    public static boolean isSilentChange() {
        return silentFrames.get() > 0;
    }

    public KratosOptimizer() {
        this.estado = Estado.IDLE;
        this.sampleIndex = 0;
        this.samplesCollected = 0;
        this.cooldownRestante = 0;
        this.chunkWaitTimeout = 0;
        this.rdPendienteAntes = -1;
        this.rdPendienteDespues = -1;
        this.fog = new KratosFog();
        this.culling = new KratosCulling(this.fog);
        this.simulation = new KratosSimulation();
        this.retainer = new KratosChunkRetainer();
        this.fpsSamples = new int[15];
    }

    /** Called once per client tick from FpsHorizonClient. */
    public void tick() {
        final Minecraft mc = mcInstance = Minecraft.getInstance();

        if (mc.level != null && mc.player != null) {
            this.fog.tick();
            switch (this.estado) {
                case IDLE:
                    this.tickIdle(mc);
                    break;
                case FOG_CERRANDO:
                    // fog callback transitions to SILENT_ARMADO
                    break;
                case SILENT_ARMADO:
                    // FIXED: Longer silent frames when RD is increasing to avoid world reload
                    // When increasing RD, Sodium needs more time to build new chunks
                    // When decreasing, fewer frames are needed since chunks are already loaded
                    final int silentFramesToSet = rdGoingUp ? 40 : 20;
                    silentFrames.set(silentFramesToSet);
                    this.estado = Estado.APLICANDO_RD;
                    break;
                case APLICANDO_RD:
                    if (silentFrames.get() > 5) {
                        this.aplicarCambioRD(mc);
                    }
                    break;
                case ESPERANDO_CHUNKS:
                    this.tickEsperandoChunks();
                    break;
                case FOG_ABRIENDO:
                    if (this.fog.estaListo()) {
                        silentFrames.set(0);
                        rdGoingUp = false;
                        if (KratosChunkRetainer.getInstance() != null) {
                            KratosChunkRetainer.getInstance().clear();
                        }
                        this.estado = Estado.COOLDOWN;
                    }
                    break;
                case COOLDOWN:
                    if (--this.cooldownRestante <= 0) {
                        this.estado = Estado.IDLE;
                    }
                    break;
            }
            this.tickDebugVerbose(mc);
        }

        this.culling.tick();
        this.simulation.tick();
        this.retainer.tick();
    }

    private void tickIdle(final Minecraft mc) {
        final int configSamples = (int)KratosConfig.FPS_SAMPLES.get();
        if (this.fpsSamples.length != configSamples) {
            this.fpsSamples = new int[configSamples];
            this.sampleIndex = 0;
            this.samplesCollected = 0;
        }
        this.fpsSamples[this.sampleIndex] = mc.getFps();
        this.sampleIndex = (this.sampleIndex + 1) % this.fpsSamples.length;
        if (this.samplesCollected < this.fpsSamples.length) {
            ++this.samplesCollected;
        }
        if (this.samplesCollected < this.fpsSamples.length) {
            return;
        }
        final int avgFps = this.calcularPromedio();
        final int currentRD = this.getRD(mc);
        final int minFps = (int)KratosConfig.MIN_FPS.get();
        final int maxFps = (int)KratosConfig.MAX_FPS.get();
        final int minRD = (int)KratosConfig.MIN_RD.get();
        final int maxRD = (int)KratosConfig.MAX_RD.get();
        int nuevoRD = -1;
        if (avgFps < minFps && currentRD > minRD) {
            nuevoRD = Math.max(currentRD - 1, minRD);
        } else if (avgFps > maxFps && currentRD < maxRD) {
            nuevoRD = Math.min(currentRD + 1, maxRD);
        }
        if (nuevoRD == -1) {
            return;
        }
        this.rdPendienteAntes = currentRD;
        this.rdPendienteDespues = nuevoRD;
        this.samplesCollected = 0;
        this.sampleIndex = 0;
        this.estado = Estado.FOG_CERRANDO;
        this.fog.iniciarCierre(this.rdPendienteDespues, () -> this.estado = Estado.SILENT_ARMADO);
    }

    private void aplicarCambioRD(final Minecraft mc) {
        try {
            Object sodium = getSodiumRenderer(mc);
            if (sodium != null) {
                ((pueblopaleta.mixin.KratosSodiumAccessor)sodium).kratos$setRenderDistance(this.rdPendienteDespues);
            }
        } catch (final Throwable t) {}

        mc.options.renderDistance().set(this.rdPendienteDespues);
        KratosDebug.mostrar(this.rdPendienteAntes, this.rdPendienteDespues);
        final boolean bajando = this.rdPendienteDespues < this.rdPendienteAntes;
        rdGoingUp = !bajando;
        this.cooldownRestante = (int)(bajando ? KratosConfig.COOLDOWN_BAJAR.get() : KratosConfig.COOLDOWN_SUBIR.get());
        this.rdPendienteAntes = -1;
        this.rdPendienteDespues = -1;
        this.chunkWaitTimeout = 0;
        this.fog.iniciarApertura();
        this.estado = Estado.FOG_ABRIENDO;
    }

    private void tickEsperandoChunks() {
        ++this.chunkWaitTimeout;
        if (this.chunkWaitTimeout >= MAX_CHUNK_WAIT) {
            silentFrames.set(0);
            this.fog.iniciarApertura();
            this.estado = Estado.FOG_ABRIENDO;
        }
    }

    private void tickDebugVerbose(final Minecraft mc) {
        if (!(boolean)KratosConfig.DEBUG_VERBOSE.get()) {
            return;
        }
        if (mc.player == null) {
            return;
        }
        final int avgFps = (this.samplesCollected > 0) ? this.calcularPromedio() : 0;
        final String msg = String.format(
"\u00a77[Kratos] \u00a7fFPS:\u00a7e%d \u00a77RD:\u00a7e%d \u00a77Estado:\u00a7b%s \u00a77Fog:\u00a7d%s \u00a77CD:\u00a7e%d \u00a77Culling:\u00a7e%d",
            avgFps, this.getRD(mc), this.estado.name(), this.fog.getEstado().name(),
            this.cooldownRestante, silentFrames.get());
        mc.player.displayClientMessage(Component.literal(msg), true);
    }

    private static Object getSodiumRenderer(final Minecraft mc) {
        if (mc.levelRenderer == null) {
            return null;
        }
        try {
            final Class<?> extClass = Class.forName("net.caffeinemc.mods.sodium.client.world.WorldRendererExtended");
            if (extClass.isInstance(mc.levelRenderer)) {
                final Method getter = extClass.getMethod("sodium$getWorldRenderer");
                return getter.invoke(mc.levelRenderer);
            }
        } catch (final Throwable t) {}
        return null;
    }

    private int getRD(final Minecraft mc) {
        return mc.options.renderDistance().get();
    }

    private int calcularPromedio() {
        long suma = 0L;
        for (final int fps : this.fpsSamples) {
            suma += fps;
        }
        return (int)(suma / this.fpsSamples.length);
    }

    private enum Estado {
        IDLE,
        FOG_CERRANDO,
        SILENT_ARMADO,
        APLICANDO_RD,
        ESPERANDO_CHUNKS,
        FOG_ABRIENDO,
        COOLDOWN
    }
}
