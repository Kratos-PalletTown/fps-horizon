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
    public static final ForgeConfigSpec.BooleanValue CULLING_ENTIDADES;
    public static final ForgeConfigSpec.DoubleValue CULLING_RADIUS_FACTOR;
    public static final ForgeConfigSpec.IntValue CULLING_EXTRA_BLOCKS;

    // Simulation Distance
    public static final ForgeConfigSpec.EnumValue<SdMode> SD_MODE;
    public static final ForgeConfigSpec.IntValue MIN_SD;
    public static final ForgeConfigSpec.IntValue MAX_SD;
    public static final ForgeConfigSpec.IntValue SD_COOLDOWN_BAJAR;
    public static final ForgeConfigSpec.IntValue SD_COOLDOWN_SUBIR;
    public static final ForgeConfigSpec.IntValue SD_MIN_FPS;
    public static final ForgeConfigSpec.IntValue SD_MAX_FPS;
    public static final ForgeConfigSpec.IntValue SD_MAX_MS;
    public static final ForgeConfigSpec.IntValue SD_MIN_MS;

    // Debug
    public static final ForgeConfigSpec.BooleanValue MOSTRAR_DEBUG;
    public static final ForgeConfigSpec.BooleanValue DEBUG_VERBOSE;

    public enum SdMode { OFF, FPS, MS, BOTH }

    static {
        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("1_fps");
        MIN_FPS = BUILDER.defineInRange("minFps", 30, 10, 120);
        MAX_FPS = BUILDER.defineInRange("maxFps", 50, 10, 120);
        FPS_SAMPLES = BUILDER.defineInRange("fpsSamples", 15, 5, 60);
        BUILDER.pop();

        BUILDER.push("2_render_distance");
        MIN_RD = BUILDER.defineInRange("minRenderDistance", 4, 2, 32);
        MAX_RD = BUILDER.defineInRange("maxRenderDistance", 12, 2, 32);
        BUILDER.pop();

        BUILDER.push("3_cooldown");
        COOLDOWN_BAJAR = BUILDER.defineInRange("cooldownBajar", 30, 5, 400);
        COOLDOWN_SUBIR = BUILDER.defineInRange("cooldownSubir", 100, 5, 400);
        BUILDER.pop();

        BUILDER.push("4_niebla");
        NIEBLA_ACTIVA = BUILDER.define("nieblaActiva", true);
        FOG_START_BLOQUES = BUILDER.defineInRange("fogStartBloques", 0, 0, 512);
        FOG_END = BUILDER.defineInRange("fogEnd", 0.95, 0.5, 1.0);
        FOG_CIERRE_END = BUILDER.defineInRange("fogCierreEnd", 0.8, 0.3, 1.0);
        FOG_VELOCIDAD_LERP = BUILDER.defineInRange("fogVelocidadLerp", 0.05, 0.01, 0.5);
        BUILDER.pop();

        BUILDER.push("5_culling");
        CULLING_ACTIVO = BUILDER.define("cullingActivo", true);
        CULLING_ENTIDADES = BUILDER.define("cullingEntidades", true);
        CULLING_RADIUS_FACTOR = BUILDER.defineInRange("cullingRadiusFactor", 1.125, 0.8, 1.5);
        CULLING_EXTRA_BLOCKS = BUILDER.defineInRange("cullingExtraBlocks", 0, 0, 64);
        BUILDER.pop();

        BUILDER.push("6_simulation_distance");
        SD_MODE = BUILDER.defineEnum("sdMode", SdMode.OFF);
        MIN_SD = BUILDER.defineInRange("minSimDistance", 5, 5, 32);
        MAX_SD = BUILDER.defineInRange("maxSimDistance", 10, 2, 32);
        SD_COOLDOWN_BAJAR = BUILDER.defineInRange("sdCooldownBajar", 30, 5, 400);
        SD_COOLDOWN_SUBIR = BUILDER.defineInRange("sdCooldownSubir", 100, 5, 400);
        SD_MIN_FPS = BUILDER.defineInRange("sdMinFps", 30, 10, 120);
        SD_MAX_FPS = BUILDER.defineInRange("sdMaxFps", 50, 10, 120);
        SD_MAX_MS = BUILDER.defineInRange("sdMaxMs", 100, 10, 500);
        SD_MIN_MS = BUILDER.defineInRange("sdMinMs", 50, 5, 500);
        BUILDER.pop();

        BUILDER.push("7_debug");
        MOSTRAR_DEBUG = BUILDER.define("mostrarDebug", false);
        DEBUG_VERBOSE = BUILDER.define("debugVerbose", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}