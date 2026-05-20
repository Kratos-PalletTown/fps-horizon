package pueblopaleta;

import java.lang.reflect.Method;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraft.client.Minecraft;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

@Mod("fps_horizon")
public class KratosOptimizer
{
    private static final AtomicInteger silentFrames = new AtomicInteger(0);
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
        KratosProfiles.load();
        final KratosCulling culling = new KratosCulling(this.fog);
        MinecraftForge.EVENT_BUS.register(culling);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, KratosConfig.SPEC);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new KratosConfigScreen(parent)));
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(this.fog);
        this.fpsSamples = new int[15];
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        final Minecraft mc = mcInstance = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        this.fog.tick();
        switch (this.estado) {
            case IDLE:
                this.tickIdle(mc);
                break;
            case FOG_CERRANDO:
                // fog callback transitions to SILENT_ARMADO
                break;
            case SILENT_ARMADO:
                silentFrames.set(20);
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
"\00a77[Kratos] \00a7fFPS:\00a7e%d \00a77RD:\00a7e%d \00a77Estado:\00a7b%s \00a77Fog:\00a7d%s \00a77CD:\00a7e%d \00a77Culling:\00a7e%d",
            avgFps, this.getRD(mc), this.estado.name(), this.fog.getEstado().name(),
            this.cooldownRestante, silentFrames.get());
        mc.player.displayClientMessage(Component.literal(msg), true);
    }

    private static Object getSodiumRenderer(final Minecraft mc) {
        if (mc.levelRenderer == null) {
            return null;
        }
        try {
            final Class<?> extClass = Class.forName("me.jellysquid.mods.sodium.client.world.WorldRendererExtended");
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
