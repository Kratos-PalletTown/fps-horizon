package pueblopaleta;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

public class KratosSimulation
{
    private int[] fpsSamples;
    private int sampleIndex;
    private int samplesCollected;
    private int cooldownRestante;

    // MS tracking
    private long lastTickTime = -1L;
    private long[] msSamples;
    private int msIndex;
    private int msCollected;
    // MS_SAMPLES is dynamic - uses FPS_SAMPLES config

    public KratosSimulation() {
        this.fpsSamples = new int[15];
        this.msSamples  = new long[Math.max(5, (int) KratosConfig.FPS_SAMPLES.get())];
        this.cooldownRestante = 0;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final Minecraft mc = KratosOptimizer.getMC();
        if (mc == null || mc.level == null || mc.player == null) return;

        final KratosConfig.SdMode mode = KratosConfig.SD_MODE.get();
        if (mode == KratosConfig.SdMode.OFF) return;

        // Measure server tick time (ms)
        final long now = System.currentTimeMillis();
        if (lastTickTime > 0) {
            final long elapsed = now - lastTickTime;
            msSamples[msIndex] = elapsed;
            msIndex = (msIndex + 1) % this.msSamples.length;
            if (msCollected < this.msSamples.length) msCollected++;
        }
        lastTickTime = now;

        // FPS samples
        final int configSamples = (int) KratosConfig.FPS_SAMPLES.get();
        if (fpsSamples.length != configSamples) {
            fpsSamples = new int[configSamples];
            sampleIndex = 0;
            samplesCollected = 0;
        }
        fpsSamples[sampleIndex] = mc.getFps();
        sampleIndex = (sampleIndex + 1) % fpsSamples.length;
        if (samplesCollected < fpsSamples.length) samplesCollected++;

        if (cooldownRestante > 0) {
            cooldownRestante--;
            return;
        }

        if (samplesCollected < fpsSamples.length) return;
        if (msCollected < this.msSamples.length) return;

        final int avgFps = calcAvgFps();
        final long avgMs = calcAvgMs();
        final int currentSD = mc.options.simulationDistance().get();
        final int minSD = KratosConfig.MIN_SD.get();
        final int maxSD = KratosConfig.MAX_SD.get();
        if (minSD >= maxSD) return; // invalid config, skip

        boolean shouldDecrease = false;
        boolean shouldIncrease = false;

        final boolean isHost = mc.hasSingleplayerServer();

        // MS mode only works reliably when you are the host (singleplayer or LAN opened by you)
        // In external multiplayer, tick delta includes network ping and is unreliable
        final boolean msAvailable = isHost;

        switch (mode) {
            case FPS -> {
                shouldDecrease = avgFps < KratosConfig.SD_MIN_FPS.get() && currentSD > minSD;
                shouldIncrease = avgFps > KratosConfig.SD_MAX_FPS.get() && currentSD < maxSD;
            }
            case MS -> {
                if (!msAvailable) break; // silently skip in external multiplayer
                shouldDecrease = avgMs > KratosConfig.SD_MAX_MS.get() && currentSD > minSD;
                shouldIncrease = avgMs < KratosConfig.SD_MIN_MS.get() && currentSD < maxSD;
            }
            case BOTH -> {
                shouldDecrease = (avgFps < KratosConfig.SD_MIN_FPS.get()
                               || (msAvailable && avgMs > KratosConfig.SD_MAX_MS.get())) && currentSD > minSD;
                shouldIncrease = (avgFps > KratosConfig.SD_MAX_FPS.get()
                               && (!msAvailable || avgMs < KratosConfig.SD_MIN_MS.get())) && currentSD < maxSD;
            }
        }

        if (shouldDecrease) {
            final int newSD = Math.max(currentSD - 1, minSD);
            mc.options.simulationDistance().set(newSD);
            if ((boolean) KratosConfig.MOSTRAR_DEBUG.get() && mc.player != null) {
                mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "\u00a77[Kratos] \u00a7fSD: \u00a7c\u25bc\u00a7f" + newSD), true);
            }
            cooldownRestante = KratosConfig.SD_COOLDOWN_BAJAR.get();
            samplesCollected = 0; sampleIndex = 0;
            msCollected = 0; msIndex = 0;
        } else if (shouldIncrease) {
            final int newSD = Math.min(currentSD + 1, maxSD);
            mc.options.simulationDistance().set(newSD);
            if ((boolean) KratosConfig.MOSTRAR_DEBUG.get() && mc.player != null) {
                mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "\u00a77[Kratos] \u00a7fSD: \u00a7a\u25b2\u00a7f" + newSD), true);
            }
            cooldownRestante = KratosConfig.SD_COOLDOWN_SUBIR.get();
            samplesCollected = 0; sampleIndex = 0;
            msCollected = 0; msIndex = 0;
        }
    }

    private int calcAvgFps() {
        long sum = 0;
        for (int f : fpsSamples) sum += f;
        return (int)(sum / fpsSamples.length);
    }

    private long calcAvgMs() {
        if (msSamples.length == 0) return 0;
        long sum = 0;
        for (long m : msSamples) sum += m;
        return sum / msSamples.length;
    }
}
