package pueblopaleta;

import net.minecraftforge.common.ForgeConfigSpec;

public class KratosConfig
{
    public static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MIN_FPS;
    public static final ForgeConfigSpec.IntValue MAX_FPS;
    public static final ForgeConfigSpec.IntValue FPS_SAMPLES;
    public static final ForgeConfigSpec.IntValue MIN_RD;
    public static final ForgeConfigSpec.IntValue MAX_RD;
    public static final ForgeConfigSpec.IntValue COOLDOWN_BAJAR;
    public static final ForgeConfigSpec.IntValue COOLDOWN_SUBIR;
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
    public static final ForgeConfigSpec.BooleanValue CULLING_DINAMICO;
    public static final ForgeConfigSpec.BooleanValue CULLING_PERFILES;
    // Debug
    public static final ForgeConfigSpec.BooleanValue MOSTRAR_DEBUG;
    public static final ForgeConfigSpec.BooleanValue DEBUG_VERBOSE;

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
        BUILDER.comment("Activa o desactiva la niebla de distancia de Kratos");
        NIEBLA_ACTIVA = BUILDER.define("nieblaActiva", true);
        BUILDER.comment("Distancia fija en bloques donde EMPIEZA la niebla.\n  0  = desde la camara\n  64 = a 64 bloques (recomendado)");
        FOG_START_BLOQUES = BUILDER.defineInRange("fogStartBloques", 0, 0, 512);
        BUILDER.comment("Fraccion del render distance donde la niebla tapa completamente.");
        FOG_END = BUILDER.defineInRange("fogEnd", 0.8, 0.1, 1.0);
        BUILDER.comment("Fraccion de cierre de niebla al cambiar RD.");
        FOG_CIERRE_END = BUILDER.defineInRange("fogCierreEnd", 0.8, 0.3, 1.0);
        BUILDER.comment("Velocidad de animacion de la niebla. 0.01 = lento, 0.5 = rapido");
        FOG_VELOCIDAD_LERP = BUILDER.defineInRange("fogVelocidadLerp", 0.05, 0.01, 0.5);
        BUILDER.pop();

        BUILDER.push("5_culling");
        BUILDER.comment("Activa o desactiva el culling dinamico de chunks");
        CULLING_ACTIVO = BUILDER.define("cullingActivo", true);
        BUILDER.comment("Escala vertical del culling. 8 = default (2.0x), 2 = minimo (0.5x), 40 = maximo (10x). Slider interno divide por 4.");
        CULLING_VERTICAL = BUILDER.defineInRange("cullingVertical", 8, 2, 40);
        BUILDER.comment("Escala horizontal del culling. 0 = normal (1.0x), 100 = maximo (2.0x). Default: 10");
        CULLING_HORIZONTAL = BUILDER.defineInRange("cullingHorizontal", 10, 0, 100);
        BUILDER.comment("Aplica el culling tambien a entidades (mobs, items, etc)");
        CULLING_ENTIDADES = BUILDER.define("cullingEntidades", true);
        BUILDER.comment("[EXPERIMENTAL] Ajusta automaticamente el culling segun el RD actual.\n  RD <= 2: vertical 50%, horizontal 30%\n  RD == 3: vertical 50%, horizontal 40%\n  RD >= 4: usa los valores configurados");
        CULLING_DINAMICO = BUILDER.define("cullingDinamico", false);
        BUILDER.comment("[EXPERIMENTAL] Activa el sistema de perfiles de culling. Cuando esta activo, ignora los sliders de vertical y horizontal.");
        CULLING_PERFILES = BUILDER.define("cullingPerfiles", false);
        BUILDER.pop();

        BUILDER.push("6_debug");
        BUILDER.comment("Muestra el render distance en el Action Bar al cambiar");
        MOSTRAR_DEBUG = BUILDER.define("mostrarDebug", false);
        BUILDER.comment("Muestra FPS promedio, estado, cooldown y culling en cada tick");
        DEBUG_VERBOSE = BUILDER.define("debugVerbose", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
