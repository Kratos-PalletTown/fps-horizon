package pueblopaleta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

/**
 * Fabric replacement for NeoForge's ModConfigSpec.
 *
 * ModConfigSpec.IntValue / DoubleValue / BooleanValue / EnumValue all exposed
 * a plain get()/set() API, so the tick logic (KratosOptimizer, KratosCulling,
 * KratosFog, KratosSimulation, KratosDebug) barely had to change during the
 * port - it still calls KratosConfig.MIN_FPS.get() etc. The Cloth Config
 * screen (KratosModMenuIntegration) reads/writes these same ConfigValue
 * instances directly.
 *
 * Persistence is a plain JSON file at config/fpshorizon.json instead of the
 * NeoForge .toml, saved through Fabric Loader's config directory.
 */
public class KratosConfig
{
    public static class ConfigValue<T>
    {
        private final T defaultValue;
        private T value;

        public ConfigValue(T defaultValue) {
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public T get() { return value; }

        public void set(T newValue) {
            this.value = newValue;
        }

        public T getDefault() { return defaultValue; }
    }

    public enum SdMode { OFF, FPS, MS, BOTH }

    // FPS
    public static final ConfigValue<Integer> MIN_FPS      = new ConfigValue<>(30);
    public static final ConfigValue<Integer> MAX_FPS      = new ConfigValue<>(50);
    public static final ConfigValue<Integer> FPS_SAMPLES  = new ConfigValue<>(15);

    // Render Distance
    public static final ConfigValue<Integer> MIN_RD = new ConfigValue<>(4);
    public static final ConfigValue<Integer> MAX_RD = new ConfigValue<>(12);

    // Cooldown RD
    public static final ConfigValue<Integer> COOLDOWN_BAJAR = new ConfigValue<>(30);
    public static final ConfigValue<Integer> COOLDOWN_SUBIR = new ConfigValue<>(100);

    // Fog
    public static final ConfigValue<Boolean> NIEBLA_ACTIVA        = new ConfigValue<>(true);
    public static final ConfigValue<Integer> FOG_START_BLOQUES    = new ConfigValue<>(0);
    public static final ConfigValue<Double>  FOG_END              = new ConfigValue<>(0.95);
    public static final ConfigValue<Double>  FOG_CIERRE_END       = new ConfigValue<>(0.8);
    public static final ConfigValue<Double>  FOG_VELOCIDAD_LERP   = new ConfigValue<>(0.05);

    // Culling
    public static final ConfigValue<Boolean> CULLING_ACTIVO         = new ConfigValue<>(true);
    public static final ConfigValue<Boolean> CULLING_ENTIDADES      = new ConfigValue<>(true);
    public static final ConfigValue<Double>  CULLING_RADIUS_FACTOR  = new ConfigValue<>(1.125);
    public static final ConfigValue<Integer> CULLING_EXTRA_BLOCKS   = new ConfigValue<>(0);

    // Simulation Distance
    public static final ConfigValue<SdMode>  SD_MODE           = new ConfigValue<>(SdMode.OFF);
    public static final ConfigValue<Integer> MIN_SD            = new ConfigValue<>(5);
    public static final ConfigValue<Integer> MAX_SD            = new ConfigValue<>(10);
    public static final ConfigValue<Integer> SD_COOLDOWN_BAJAR = new ConfigValue<>(30);
    public static final ConfigValue<Integer> SD_COOLDOWN_SUBIR = new ConfigValue<>(100);
    public static final ConfigValue<Integer> SD_MIN_FPS        = new ConfigValue<>(30);
    public static final ConfigValue<Integer> SD_MAX_FPS        = new ConfigValue<>(50);
    public static final ConfigValue<Integer> SD_MAX_MS         = new ConfigValue<>(100);
    public static final ConfigValue<Integer> SD_MIN_MS         = new ConfigValue<>(50);

    // Debug
    public static final ConfigValue<Boolean> MOSTRAR_DEBUG = new ConfigValue<>(false);
    public static final ConfigValue<Boolean> DEBUG_VERBOSE = new ConfigValue<>(false);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "fpshorizon.json";

    /** Plain POJO mirror of the values above, used only for GSON (de)serialization. */
    private static class Data {
        int minFps = MIN_FPS.getDefault();
        int maxFps = MAX_FPS.getDefault();
        int fpsSamples = FPS_SAMPLES.getDefault();
        int minRenderDistance = MIN_RD.getDefault();
        int maxRenderDistance = MAX_RD.getDefault();
        int cooldownBajar = COOLDOWN_BAJAR.getDefault();
        int cooldownSubir = COOLDOWN_SUBIR.getDefault();
        boolean nieblaActiva = NIEBLA_ACTIVA.getDefault();
        int fogStartBloques = FOG_START_BLOQUES.getDefault();
        double fogEnd = FOG_END.getDefault();
        double fogCierreEnd = FOG_CIERRE_END.getDefault();
        double fogVelocidadLerp = FOG_VELOCIDAD_LERP.getDefault();
        boolean cullingActivo = CULLING_ACTIVO.getDefault();
        boolean cullingEntidades = CULLING_ENTIDADES.getDefault();
        double cullingRadiusFactor = CULLING_RADIUS_FACTOR.getDefault();
        int cullingExtraBlocks = CULLING_EXTRA_BLOCKS.getDefault();
        String sdMode = SD_MODE.getDefault().name();
        int minSimDistance = MIN_SD.getDefault();
        int maxSimDistance = MAX_SD.getDefault();
        int sdCooldownBajar = SD_COOLDOWN_BAJAR.getDefault();
        int sdCooldownSubir = SD_COOLDOWN_SUBIR.getDefault();
        int sdMinFps = SD_MIN_FPS.getDefault();
        int sdMaxFps = SD_MAX_FPS.getDefault();
        int sdMaxMs = SD_MAX_MS.getDefault();
        int sdMinMs = SD_MIN_MS.getDefault();
        boolean mostrarDebug = MOSTRAR_DEBUG.getDefault();
        boolean debugVerbose = DEBUG_VERBOSE.getDefault();
    }

    private static Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static void load() {
        final Path path = getPath();
        if (!path.toFile().exists()) {
            save();
            return;
        }
        try (Reader r = new FileReader(path.toFile())) {
            final Data d = GSON.fromJson(r, Data.class);
            if (d == null) return;

            MIN_FPS.set(d.minFps);
            MAX_FPS.set(d.maxFps);
            FPS_SAMPLES.set(d.fpsSamples);
            MIN_RD.set(d.minRenderDistance);
            MAX_RD.set(d.maxRenderDistance);
            COOLDOWN_BAJAR.set(d.cooldownBajar);
            COOLDOWN_SUBIR.set(d.cooldownSubir);
            NIEBLA_ACTIVA.set(d.nieblaActiva);
            FOG_START_BLOQUES.set(d.fogStartBloques);
            FOG_END.set(d.fogEnd);
            FOG_CIERRE_END.set(d.fogCierreEnd);
            FOG_VELOCIDAD_LERP.set(d.fogVelocidadLerp);
            CULLING_ACTIVO.set(d.cullingActivo);
            CULLING_ENTIDADES.set(d.cullingEntidades);
            CULLING_RADIUS_FACTOR.set(d.cullingRadiusFactor);
            CULLING_EXTRA_BLOCKS.set(d.cullingExtraBlocks);
            try {
                SD_MODE.set(SdMode.valueOf(d.sdMode));
            } catch (final Exception ignored) {
                SD_MODE.set(SdMode.OFF);
            }
            MIN_SD.set(d.minSimDistance);
            MAX_SD.set(d.maxSimDistance);
            SD_COOLDOWN_BAJAR.set(d.sdCooldownBajar);
            SD_COOLDOWN_SUBIR.set(d.sdCooldownSubir);
            SD_MIN_FPS.set(d.sdMinFps);
            SD_MAX_FPS.set(d.sdMaxFps);
            SD_MAX_MS.set(d.sdMaxMs);
            SD_MIN_MS.set(d.sdMinMs);
            MOSTRAR_DEBUG.set(d.mostrarDebug);
            DEBUG_VERBOSE.set(d.debugVerbose);
        } catch (final Exception e) {
            // Corrupt or unreadable config - keep current in-memory defaults.
        }
    }

    public static void save() {
        final Data d = new Data();
        d.minFps = MIN_FPS.get();
        d.maxFps = MAX_FPS.get();
        d.fpsSamples = FPS_SAMPLES.get();
        d.minRenderDistance = MIN_RD.get();
        d.maxRenderDistance = MAX_RD.get();
        d.cooldownBajar = COOLDOWN_BAJAR.get();
        d.cooldownSubir = COOLDOWN_SUBIR.get();
        d.nieblaActiva = NIEBLA_ACTIVA.get();
        d.fogStartBloques = FOG_START_BLOQUES.get();
        d.fogEnd = FOG_END.get();
        d.fogCierreEnd = FOG_CIERRE_END.get();
        d.fogVelocidadLerp = FOG_VELOCIDAD_LERP.get();
        d.cullingActivo = CULLING_ACTIVO.get();
        d.cullingEntidades = CULLING_ENTIDADES.get();
        d.cullingRadiusFactor = CULLING_RADIUS_FACTOR.get();
        d.cullingExtraBlocks = CULLING_EXTRA_BLOCKS.get();
        d.sdMode = SD_MODE.get().name();
        d.minSimDistance = MIN_SD.get();
        d.maxSimDistance = MAX_SD.get();
        d.sdCooldownBajar = SD_COOLDOWN_BAJAR.get();
        d.sdCooldownSubir = SD_COOLDOWN_SUBIR.get();
        d.sdMinFps = SD_MIN_FPS.get();
        d.sdMaxFps = SD_MAX_FPS.get();
        d.sdMaxMs = SD_MAX_MS.get();
        d.sdMinMs = SD_MIN_MS.get();
        d.mostrarDebug = MOSTRAR_DEBUG.get();
        d.debugVerbose = DEBUG_VERBOSE.get();

        try (Writer w = new FileWriter(getPath().toFile())) {
            GSON.toJson(d, w);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
