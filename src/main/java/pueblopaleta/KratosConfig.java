package pueblopaleta;

import net.minecraftforge.common.ForgeConfigSpec;

public class KratosConfig
{
    public static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec SPEC;

    // FPS
    public static final ForgeConfigSpec.IntValue MIN_FPS;
    public static final ForgeConfigSpec.IntValue MAX_FPS;
    public static final ForgeConfigSpec.IntValue FPS_SAMPLES;

    // Render Distance
    public static final ForgeConfigSpec.IntValue MIN_RD;
    public static final ForgeConfigSpec.IntValue MAX_RD;

    // Cooldown RD
    public static final ForgeConfigSpec.IntValue COOLDOWN_BAJAR;
    public static final ForgeConfigSpec.IntValue COOLDOWN_SUBIR;

    // Fog
    public static final ForgeConfigSpec.BooleanValue NIEBLA_ACTIVA;
    public static final ForgeConfigSpec.IntValue FOG_START_BLOQUES;
    public static final ForgeConfigSpec.DoubleValue FOG_END;
    public static final ForgeConfigSpec.DoubleValue FOG_CIERRE_END;
    public static final ForgeConfigSpec.DoubleValue FOG_VELOCIDAD_LERP;

    // Culling
    public static final ForgeConfigSpec.BooleanValue CULLING_ACTIVO;
    public static final ForgeConfigSpec.IntValue CULLING_VERTICAL;
    public static final ForgeConfigSpec.IntValue CULLING_HORIZONTAL;
    public static final ForgeConfigSpec.BooleanValue CULLING_ENTIDADES;
    public static final ForgeConfigSpec.BooleanValue CULLING_PERFILES;

    // Simulation Distance
    public static final ForgeConfigSpec.EnumValue<SdMode> SD_MODE;
    public static final ForgeConfigSpec.IntValue MIN_SD;
    public static final ForgeConfigSpec.IntValue MAX_SD;
    public static final ForgeConfigSpec.IntValue SD_COOLDOWN_BAJAR;
    public static final ForgeConfigSpec.IntValue SD_COOLDOWN_SUBIR;
    // SD por FPS
    public static final ForgeConfigSpec.IntValue SD_MIN_FPS;
    public static final ForgeConfigSpec.IntValue SD_MAX_FPS;
    // SD por MS
    public static final ForgeConfigSpec.IntValue SD_MAX_MS;
    public static final ForgeConfigSpec.IntValue SD_MIN_MS;

    // Debug
    public static final ForgeConfigSpec.BooleanValue MOSTRAR_DEBUG;
    public static final ForgeConfigSpec.BooleanValue DEBUG_VERBOSE;

    public enum SdMode { OFF, FPS, MS, BOTH }

    static {
        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("1_fps");
        BUILDER.comment("FPS por debajo del cual se reduce el render distance");
        MIN_FPS = BUILDER.defineInRange("minFps", 30, 10, 120);
        BUILDER.comment("FPS por encima del cual se aumenta el render distance");
        MAX_FPS = BUILDER.defineInRange("maxFps", 50, 10, 120);
        BUILDER.comment("Cuantas muestras de FPS promediar antes de decidir un cambio");
        FPS_SAMPLES = BUILDER.defineInRange("fpsSamples", 15, 5, 60);
        BUILDER.pop();

        BUILDER.push("2_render_distance");
        BUILDER.comment("Render distance minimo en chunks");
        MIN_RD = BUILDER.defineInRange("minRenderDistance", 4, 2, 32);
        BUILDER.comment("Render distance maximo en chunks");
        MAX_RD = BUILDER.defineInRange("maxRenderDistance", 12, 2, 32);
        BUILDER.pop();

        BUILDER.push("3_cooldown");
        BUILDER.comment("Ticks de espera tras BAJAR el render distance. 20 ticks = 1 segundo");
        COOLDOWN_BAJAR = BUILDER.defineInRange("cooldownBajar", 30, 5, 400);
        BUILDER.comment("Ticks de espera tras SUBIR el render distance");
        COOLDOWN_SUBIR = BUILDER.defineInRange("cooldownSubir", 100, 5, 400);
        BUILDER.pop();

        BUILDER.push("4_niebla");
        BUILDER.comment("Activa o desactiva la niebla de distancia");
        NIEBLA_ACTIVA = BUILDER.define("nieblaActiva", true);
        BUILDER.comment("Distancia fija en bloques donde EMPIEZA la niebla.\n  0 = desde la camara");
        FOG_START_BLOQUES = BUILDER.defineInRange("fogStartBloques", 0, 0, 512);
        BUILDER.comment("Fraccion del render distance donde la niebla tapa completamente.");
        FOG_END = BUILDER.defineInRange("fogEnd", 0.8, 0.1, 1.0);
        BUILDER.comment("Fraccion de cierre de niebla al cambiar RD.");
        FOG_CIERRE_END = BUILDER.defineInRange("fogCierreEnd", 0.8, 0.3, 1.0);
        BUILDER.comment("Velocidad de animacion de la niebla. 0.01 = lento, 0.5 = rapido");
        FOG_VELOCIDAD_LERP = BUILDER.defineInRange("fogVelocidadLerp", 0.05, 0.01, 0.5);
        BUILDER.pop();

        BUILDER.push("5_culling");
        BUILDER.comment("Activa o desactiva el culling de chunks");
        CULLING_ACTIVO = BUILDER.define("cullingActivo", true);
        BUILDER.comment("Escala vertical. 4 = 100%, 2 = 50%, 40 = 1000%");
        CULLING_VERTICAL = BUILDER.defineInRange("cullingVertical", 4, 2, 40);
        BUILDER.comment("Escala horizontal. 0 = 0%, 100 = 100%");
        CULLING_HORIZONTAL = BUILDER.defineInRange("cullingHorizontal", 0, 0, 100);
        BUILDER.comment("Aplica culling a entidades");
        CULLING_ENTIDADES = BUILDER.define("cullingEntidades", true);
        BUILDER.comment("[EXPERIMENTAL] Activa perfiles de culling por RD");
        CULLING_PERFILES = BUILDER.define("cullingPerfiles", false);
        BUILDER.pop();

        BUILDER.push("6_simulation_distance");
        BUILDER.comment("Modo de control de simulation distance.\n  OFF  = desactivado\n  FPS  = basado en FPS del cliente\n  MS   = basado en tiempo de tick del servidor (ms)\n  BOTH = cualquiera de los dos dispara el cambio");
        SD_MODE = BUILDER.defineEnum("sdMode", SdMode.OFF);
        BUILDER.comment("Simulation distance minima en chunks");
        MIN_SD = BUILDER.defineInRange("minSimDistance", 4, 2, 32);
        BUILDER.comment("Simulation distance maxima en chunks");
        MAX_SD = BUILDER.defineInRange("maxSimDistance", 10, 2, 32);
        BUILDER.comment("Ticks de espera tras BAJAR la simulation distance");
        SD_COOLDOWN_BAJAR = BUILDER.defineInRange("sdCooldownBajar", 30, 5, 400);
        BUILDER.comment("Ticks de espera tras SUBIR la simulation distance");
        SD_COOLDOWN_SUBIR = BUILDER.defineInRange("sdCooldownSubir", 100, 5, 400);
        BUILDER.comment("[Modo FPS/BOTH] FPS por debajo del cual se reduce la SD");
        SD_MIN_FPS = BUILDER.defineInRange("sdMinFps", 30, 10, 120);
        BUILDER.comment("[Modo FPS/BOTH] FPS por encima del cual se aumenta la SD");
        SD_MAX_FPS = BUILDER.defineInRange("sdMaxFps", 50, 10, 120);
        BUILDER.comment("[Modo MS/BOTH] Ms por encima del cual se REDUCE la SD (servidor lento)");
        SD_MAX_MS = BUILDER.defineInRange("sdMaxMs", 100, 10, 500);
        BUILDER.comment("[Modo MS/BOTH] Ms por debajo del cual se AUMENTA la SD (servidor rapido)");
        SD_MIN_MS = BUILDER.defineInRange("sdMinMs", 50, 5, 500);
        BUILDER.pop();

        BUILDER.push("7_debug");
        BUILDER.comment("Muestra el render distance en el Action Bar al cambiar");
        MOSTRAR_DEBUG = BUILDER.define("mostrarDebug", false);
        BUILDER.comment("Muestra FPS promedio, estado, cooldown y culling en cada tick");
        DEBUG_VERBOSE = BUILDER.define("debugVerbose", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
