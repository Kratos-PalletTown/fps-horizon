package pueblopaleta;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Replaces the NeoForge registration:
 *   container.registerExtensionPoint(IConfigScreenFactory.class,
 *       (cont, parent) -> new KratosConfigScreen(parent));
 *
 * Fabric doesn't have a built-in "config screen" slot on the mod container,
 * so Mod Menu is the de-facto standard: it scans for a "modmenu" entrypoint
 * (declared in fabric.mod.json) implementing this interface and shows a
 * gear icon for the mod in its list, wired to getModConfigScreenFactory().
 *
 * The custom KratosConfigScreen (hand-built sliders) from the NeoForge
 * version is replaced by a Cloth Config screen, per your request - Cloth
 * Config gives the same category/tooltip/slider layout with far less code.
 * KratosProfilesScreen (the profile CRUD list) is unchanged and is instead
 * reachable through a dedicated keybinding (Controls > FPS Horizon), since
 * Cloth Config's entry list isn't meant to host a full custom sub-screen.
 */
public class KratosModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return KratosModMenuIntegration::buildConfigScreen;
    }

    public static Screen buildConfigScreen(final Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("fps_horizon.config.title"))
            .setSavingRunnable(KratosConfig::save);

        final ConfigEntryBuilder eb = builder.entryBuilder();

        // ---- Render Distance ----
        final ConfigCategory rd = builder.getOrCreateCategory(Component.translatable("fps_horizon.config.render_distance"));
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.minFps"),
                    KratosConfig.MIN_FPS.get(), 5, 260)
                .setDefaultValue(KratosConfig.MIN_FPS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.minFps.tooltip"))
                .setSaveConsumer(KratosConfig.MIN_FPS::set)
                .build());
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.maxFps"),
                    KratosConfig.MAX_FPS.get(), 5, 260)
                .setDefaultValue(KratosConfig.MAX_FPS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.maxFps.tooltip"))
                .setSaveConsumer(KratosConfig.MAX_FPS::set)
                .build());
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.fpsSamples"),
                    KratosConfig.FPS_SAMPLES.get(), 1, 60)
                .setDefaultValue(KratosConfig.FPS_SAMPLES.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.fpsSamples.tooltip"))
                .setSaveConsumer(KratosConfig.FPS_SAMPLES::set)
                .build());
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.minRenderDistance"),
                    KratosConfig.MIN_RD.get(), 2, 32)
                .setDefaultValue(KratosConfig.MIN_RD.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.minRenderDistance.tooltip"))
                .setSaveConsumer(KratosConfig.MIN_RD::set)
                .build());
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.maxRenderDistance"),
                    KratosConfig.MAX_RD.get(), 2, 32)
                .setDefaultValue(KratosConfig.MAX_RD.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.maxRenderDistance.tooltip"))
                .setSaveConsumer(KratosConfig.MAX_RD::set)
                .build());
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.cooldownBajar"),
                    KratosConfig.COOLDOWN_BAJAR.get(), 0, 400)
                .setDefaultValue(KratosConfig.COOLDOWN_BAJAR.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.cooldownBajar.tooltip"))
                .setSaveConsumer(KratosConfig.COOLDOWN_BAJAR::set)
                .build());
        rd.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.cooldownSubir"),
                    KratosConfig.COOLDOWN_SUBIR.get(), 0, 400)
                .setDefaultValue(KratosConfig.COOLDOWN_SUBIR.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.cooldownSubir.tooltip"))
                .setSaveConsumer(KratosConfig.COOLDOWN_SUBIR::set)
                .build());
        rd.addEntry(eb.startTextDescription(Component.translatable("fps_horizon.config.manageProfiles.tooltip")).build());

        // ---- Fog ----
        final ConfigCategory fog = builder.getOrCreateCategory(Component.translatable("fps_horizon.config.fog"));
        fog.addEntry(eb.startBooleanToggle(Component.translatable("fps_horizon.config.nieblaActiva"),
                    KratosConfig.NIEBLA_ACTIVA.get())
                .setDefaultValue(KratosConfig.NIEBLA_ACTIVA.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.nieblaActiva.tooltip"))
                .setSaveConsumer(KratosConfig.NIEBLA_ACTIVA::set)
                .build());
        fog.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.fogStartBloques"),
                    KratosConfig.FOG_START_BLOQUES.get(), 0, 256)
                .setDefaultValue(KratosConfig.FOG_START_BLOQUES.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.fogStartBloques.tooltip"))
                .setSaveConsumer(KratosConfig.FOG_START_BLOQUES::set)
                .build());
        fog.addEntry(eb.startDoubleField(Component.translatable("fps_horizon.config.fogEnd"),
                    KratosConfig.FOG_END.get())
                .setMin(0.1).setMax(1.0)
                .setDefaultValue(KratosConfig.FOG_END.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.fogEnd.tooltip"))
                .setSaveConsumer(KratosConfig.FOG_END::set)
                .build());
        fog.addEntry(eb.startDoubleField(Component.translatable("fps_horizon.config.fogCierreEnd"),
                    KratosConfig.FOG_CIERRE_END.get())
                .setMin(0.1).setMax(1.0)
                .setDefaultValue(KratosConfig.FOG_CIERRE_END.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.fogCierreEnd.tooltip"))
                .setSaveConsumer(KratosConfig.FOG_CIERRE_END::set)
                .build());
        fog.addEntry(eb.startDoubleField(Component.translatable("fps_horizon.config.fogVelocidadLerp"),
                    KratosConfig.FOG_VELOCIDAD_LERP.get())
                .setMin(0.01).setMax(1.0)
                .setDefaultValue(KratosConfig.FOG_VELOCIDAD_LERP.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.fogVelocidadLerp.tooltip"))
                .setSaveConsumer(KratosConfig.FOG_VELOCIDAD_LERP::set)
                .build());

        // ---- Culling ----
        final ConfigCategory culling = builder.getOrCreateCategory(Component.translatable("fps_horizon.config.culling"));
        culling.addEntry(eb.startBooleanToggle(Component.translatable("fps_horizon.config.cullingActivo"),
                    KratosConfig.CULLING_ACTIVO.get())
                .setDefaultValue(KratosConfig.CULLING_ACTIVO.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.cullingActivo.tooltip"))
                .setSaveConsumer(KratosConfig.CULLING_ACTIVO::set)
                .build());
        culling.addEntry(eb.startBooleanToggle(Component.translatable("fps_horizon.config.cullingEntidades"),
                    KratosConfig.CULLING_ENTIDADES.get())
                .setDefaultValue(KratosConfig.CULLING_ENTIDADES.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.cullingEntidades.tooltip"))
                .setSaveConsumer(KratosConfig.CULLING_ENTIDADES::set)
                .build());
        culling.addEntry(eb.startDoubleField(Component.translatable("fps_horizon.config.cullingRadiusFactor"),
                    KratosConfig.CULLING_RADIUS_FACTOR.get())
                .setMin(1.0).setMax(3.0)
                .setDefaultValue(KratosConfig.CULLING_RADIUS_FACTOR.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.cullingRadiusFactor.tooltip"))
                .setSaveConsumer(KratosConfig.CULLING_RADIUS_FACTOR::set)
                .build());
        culling.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.cullingExtraBlocks"),
                    KratosConfig.CULLING_EXTRA_BLOCKS.get(), 0, 128)
                .setDefaultValue(KratosConfig.CULLING_EXTRA_BLOCKS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.cullingExtraBlocks.tooltip"))
                .setSaveConsumer(KratosConfig.CULLING_EXTRA_BLOCKS::set)
                .build());

        // ---- Simulation Distance ----
        final ConfigCategory sim = builder.getOrCreateCategory(Component.translatable("fps_horizon.config.simulation"));
        sim.addEntry(eb.startEnumSelector(Component.translatable("fps_horizon.config.sd_mode"),
                    KratosConfig.SdMode.class, KratosConfig.SD_MODE.get())
                .setDefaultValue(KratosConfig.SD_MODE.getDefault())
                .setEnumNameProvider(v -> Component.translatable("fps_horizon.config.sd_mode." + v.name().toLowerCase()))
                .setTooltip(Component.translatable("fps_horizon.config.sd_mode.tooltip"))
                .setSaveConsumer(KratosConfig.SD_MODE::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.minSimDistance"),
                    KratosConfig.MIN_SD.get(), 2, 32)
                .setDefaultValue(KratosConfig.MIN_SD.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.minSimDistance.tooltip"))
                .setSaveConsumer(KratosConfig.MIN_SD::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.maxSimDistance"),
                    KratosConfig.MAX_SD.get(), 2, 32)
                .setDefaultValue(KratosConfig.MAX_SD.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.maxSimDistance.tooltip"))
                .setSaveConsumer(KratosConfig.MAX_SD::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.sdCooldownBajar"),
                    KratosConfig.SD_COOLDOWN_BAJAR.get(), 0, 400)
                .setDefaultValue(KratosConfig.SD_COOLDOWN_BAJAR.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.sdCooldownBajar.tooltip"))
                .setSaveConsumer(KratosConfig.SD_COOLDOWN_BAJAR::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.sdCooldownSubir"),
                    KratosConfig.SD_COOLDOWN_SUBIR.get(), 0, 400)
                .setDefaultValue(KratosConfig.SD_COOLDOWN_SUBIR.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.sdCooldownSubir.tooltip"))
                .setSaveConsumer(KratosConfig.SD_COOLDOWN_SUBIR::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.sdMinFps"),
                    KratosConfig.SD_MIN_FPS.get(), 5, 260)
                .setDefaultValue(KratosConfig.SD_MIN_FPS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.sdMinFps.tooltip"))
                .setSaveConsumer(KratosConfig.SD_MIN_FPS::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.sdMaxFps"),
                    KratosConfig.SD_MAX_FPS.get(), 5, 260)
                .setDefaultValue(KratosConfig.SD_MAX_FPS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.sdMaxFps.tooltip"))
                .setSaveConsumer(KratosConfig.SD_MAX_FPS::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.sdMaxMs"),
                    KratosConfig.SD_MAX_MS.get(), 1, 1000)
                .setDefaultValue(KratosConfig.SD_MAX_MS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.sdMaxMs.tooltip"))
                .setSaveConsumer(KratosConfig.SD_MAX_MS::set)
                .build());
        sim.addEntry(eb.startIntSlider(Component.translatable("fps_horizon.config.sdMinMs"),
                    KratosConfig.SD_MIN_MS.get(), 1, 1000)
                .setDefaultValue(KratosConfig.SD_MIN_MS.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.sdMinMs.tooltip"))
                .setSaveConsumer(KratosConfig.SD_MIN_MS::set)
                .build());

        // ---- Debug ----
        final ConfigCategory debug = builder.getOrCreateCategory(Component.translatable("fps_horizon.config.debug"));
        debug.addEntry(eb.startBooleanToggle(Component.translatable("fps_horizon.config.mostrarDebug"),
                    KratosConfig.MOSTRAR_DEBUG.get())
                .setDefaultValue(KratosConfig.MOSTRAR_DEBUG.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.mostrarDebug.tooltip"))
                .setSaveConsumer(KratosConfig.MOSTRAR_DEBUG::set)
                .build());
        debug.addEntry(eb.startBooleanToggle(Component.translatable("fps_horizon.config.debugVerbose"),
                    KratosConfig.DEBUG_VERBOSE.get())
                .setDefaultValue(KratosConfig.DEBUG_VERBOSE.getDefault())
                .setTooltip(Component.translatable("fps_horizon.config.debugVerbose.tooltip"))
                .setSaveConsumer(KratosConfig.DEBUG_VERBOSE::set)
                .build());

        return builder.build();
    }
}
