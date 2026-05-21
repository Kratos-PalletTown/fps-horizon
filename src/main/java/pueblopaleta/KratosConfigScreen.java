package pueblopaleta;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import java.util.List;
import java.util.ArrayList;

public class KratosConfigScreen extends Screen
{
    private final Screen parent;
    private int detectedMaxRD = 32;
    private int detectedMaxSD = 32;

    private enum Category {
        FPS, RENDER_DISTANCE, SIMULATION_DISTANCE, COOLDOWN, FOG, CULLING, DEBUG;

        public Component label() {
            return switch (this) {
                case FPS                   -> Component.translatable("fps_horizon.config.fps");
                case RENDER_DISTANCE       -> Component.translatable("fps_horizon.config.render_distance");
                case SIMULATION_DISTANCE   -> Component.translatable("fps_horizon.config.simulation_distance");
                case COOLDOWN              -> Component.translatable("fps_horizon.config.cooldown");
                case FOG                   -> Component.translatable("fps_horizon.config.fog");
                case DEBUG                 -> Component.translatable("fps_horizon.config.debug");
                case CULLING               -> Component.translatable("fps_horizon.config.culling");
            };
        }
    }

    private Category activeCategory = Category.FPS;

    // Widgets of the current category
    private final List<net.minecraft.client.gui.components.AbstractWidget> categoryWidgets = new ArrayList<>();

    private static final int W        = 240;
    private static final int H        = 20;
    private static final int TAB_W    = 90;
    private static final int TAB_H    = 20;
    private static final int TAB_PAD  = 4;
    private static final int CONTENT_X_OFF = TAB_W + 16;

    public KratosConfigScreen(final Screen parent) {
        super(Component.translatable("fps_horizon.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.categoryWidgets.clear();
        
        // Detect max RD and SD at init time
        this.detectedMaxRD = getMaxRenderDistance();
        this.detectedMaxSD = getMaxSimulationDistance();

        // Tab buttons on the left
        int tabY = 40;
        for (final Category cat : Category.values()) {
            final Category thisCat = cat;
            final Button tab = Button.builder(cat.label(), b -> {
                this.activeCategory = thisCat;
                this.rebuildCategory();
            }).bounds(8, tabY, TAB_W, TAB_H).build();
            tab.active = (cat != this.activeCategory);
            this.addRenderableWidget(tab);
            tabY += TAB_H + TAB_PAD;
        }

        // Done button fixed at bottom right
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.done"),
            b -> this.onClose()
        ).bounds(this.width - 108, this.height - 28, 100, H).build());

        // Build initial category widgets
        this.rebuildCategory();
    }

    private void rebuildCategory() {
        // Remove old category widgets from renderables
        for (final var w : this.categoryWidgets) {
            this.removeWidget(w);
        }
        this.categoryWidgets.clear();

        // Rebuild tab active states
        this.renderables.stream()
            .filter(r -> r instanceof Button)
            .map(r -> (Button) r)
            .forEach(b -> {
                for (final Category cat : Category.values()) {
                    if (b.getMessage().equals(cat.label())) {
                        b.active = (cat != this.activeCategory);
                    }
                }
            });

        final int startX = CONTENT_X_OFF + (this.width - CONTENT_X_OFF) / 2 - W / 2;
        int y = 40;

        switch (this.activeCategory) {
            case FPS -> {
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.minFps"),
                    KratosConfig.MIN_FPS, 10, 120,
                    Component.translatable("fps_horizon.config.minFps.tooltip")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.maxFps"),
                    KratosConfig.MAX_FPS, 10, 120,
                    Component.translatable("fps_horizon.config.maxFps.tooltip")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.fpsSamples"),
                    KratosConfig.FPS_SAMPLES, 5, 60,
                    Component.translatable("fps_horizon.config.fpsSamples.tooltip")));
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.MEMORY_GUARD.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.memoryGuard.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.memoryGuard"),
                        (btn, val) -> KratosConfig.MEMORY_GUARD.set(val)));
            }
            case RENDER_DISTANCE -> {
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.minRenderDistance"),
                    KratosConfig.MIN_RD, 2, this.detectedMaxRD,
                    Component.literal("⚡ Soporte detectado: " + this.detectedMaxRD + " chunks")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.maxRenderDistance"),
                    KratosConfig.MAX_RD, 2, this.detectedMaxRD,
                    Component.literal("⚡ Soporte detectado: " + this.detectedMaxRD + " chunks")));
            }
            case SIMULATION_DISTANCE -> {
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.DYNAMIC_SIMULATION.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.dynamicSimulation.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.dynamicSimulation"),
                        (btn, val) -> KratosConfig.DYNAMIC_SIMULATION.set(val)));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.minSimulationDistance"),
                    KratosConfig.MIN_SD, 2, this.detectedMaxSD,
                    Component.literal("⚡ Soporte detectado: " + this.detectedMaxSD + " chunks")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.maxSimulationDistance"),
                    KratosConfig.MAX_SD, 2, this.detectedMaxSD,
                    Component.literal("⚡ Soporte detectado: " + this.detectedMaxSD + " chunks")));
            }
            case COOLDOWN -> {
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cooldownBajar"),
                    KratosConfig.COOLDOWN_BAJAR, 5, 400,
                    Component.translatable("fps_horizon.config.cooldownBajar.tooltip")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cooldownSubir"),
                    KratosConfig.COOLDOWN_SUBIR, 5, 400,
                    Component.translatable("fps_horizon.config.cooldownSubir.tooltip")));
            }
            case FOG -> {
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.NIEBLA_ACTIVA.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.nieblaActiva.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.nieblaActiva"),
                        (btn, val) -> KratosConfig.NIEBLA_ACTIVA.set(val)));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.fogStartBloques"),
                    KratosConfig.FOG_START_BLOQUES, 0, 512,
                    Component.translatable("fps_horizon.config.fogStartBloques.tooltip")));
                y = addSlider(startX, y, new DoubleSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.fogEnd"),
                    KratosConfig.FOG_END, 0.1, 1.0,
                    Component.translatable("fps_horizon.config.fogEnd.tooltip")));
                y = addSlider(startX, y, new DoubleSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.fogCierreEnd"),
                    KratosConfig.FOG_CIERRE_END, 0.3, 1.0,
                    Component.translatable("fps_horizon.config.fogCierreEnd.tooltip")));
                y = addSlider(startX, y, new DoubleSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.fogVelocidadLerp"),
                    KratosConfig.FOG_VELOCIDAD_LERP, 0.01, 0.5,
                    Component.translatable("fps_horizon.config.fogVelocidadLerp.tooltip")));
            }
            case CULLING -> {
                final boolean perfilesActivos = KratosConfig.CULLING_PERFILES.get();

                // Toggle culling activo (gris si perfiles activos)
                final CycleButton<Boolean> btnCulling = CycleButton.onOffBuilder(KratosConfig.CULLING_ACTIVO.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingActivo.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.cullingActivo"),
                        (btn, val) -> KratosConfig.CULLING_ACTIVO.set(val));
                btnCulling.active = !perfilesActivos;
                y = addToggle(startX, y, btnCulling);

                // Slider vertical (gris si perfiles activos)
                final CullingVerticalSlider vSlider = new CullingVerticalSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cullingVertical"),
                    KratosConfig.CULLING_VERTICAL::get, KratosConfig.CULLING_VERTICAL::set,
                    Component.translatable("fps_horizon.config.cullingVertical.tooltip"));
                vSlider.active = !perfilesActivos;
                y = addSlider(startX, y, vSlider);

                // Slider horizontal (gris si perfiles activos)
                final PercentSlider hSlider = new PercentSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cullingHorizontal"),
                    KratosConfig.CULLING_HORIZONTAL::get, KratosConfig.CULLING_HORIZONTAL::set, 0, 100,
                    Component.translatable("fps_horizon.config.cullingHorizontal.tooltip"));
                hSlider.active = !perfilesActivos;
                y = addSlider(startX, y, hSlider);

                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.CULLING_ENTIDADES.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingEntidades.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.cullingEntidades"),
                        (btn, val) -> KratosConfig.CULLING_ENTIDADES.set(val)));

                y += 4;
                // Toggle perfiles + boton gestionar en la misma fila
                final int halfW = W / 2 - 2;
                final CycleButton<Boolean> btnPerfiles = CycleButton.onOffBuilder(perfilesActivos)
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingPerfiles.tooltip")))
                    .create(startX, y, halfW, H,
                        Component.translatable("fps_horizon.config.cullingPerfiles"),
                        (btn, val) -> { KratosConfig.CULLING_PERFILES.set(val); this.rebuildCategory(); });
                y = addToggle(startX, y, btnPerfiles);
                y -= H + 6; // volver a la misma fila
                final Button btnGestionar = Button.builder(
                    Component.translatable("fps_horizon.profiles.manage"),
                    b -> minecraft.setScreen(new pueblopaleta.KratosProfilesScreen(this))
                ).bounds(startX + halfW + 4, y, halfW, H).build();
                btnGestionar.active = perfilesActivos;
                this.addRenderableWidget(btnGestionar);
                this.categoryWidgets.add(btnGestionar);
                y += H + 6;
            }
            case SIMULATION -> {
                // Mode cycle button: OFF / FPS / MS / BOTH
                y = addToggle(startX, y, CycleButton.<KratosConfig.SdMode>builder(
                    v -> Component.translatable("fps_horizon.config.sd_mode." + v.name().toLowerCase()))
                    .withValues(KratosConfig.SdMode.values())
                    .withInitialValue(KratosConfig.SD_MODE.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.sd_mode.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.sd_mode"),
                        (btn, val) -> KratosConfig.SD_MODE.set(val)));

                y = addSlider(startX, y, new ValidatedIntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.minSimDistance"),
                    KratosConfig.MIN_SD, 2, 32,
                    Component.translatable("fps_horizon.config.minSimDistance.tooltip"),
                    () -> KratosConfig.MIN_SD.get() >= KratosConfig.MAX_SD.get()));
                y = addSlider(startX, y, new ValidatedIntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.maxSimDistance"),
                    KratosConfig.MAX_SD, 2, 32,
                    Component.translatable("fps_horizon.config.maxSimDistance.tooltip"),
                    () -> KratosConfig.MIN_SD.get() >= KratosConfig.MAX_SD.get()));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.sdCooldownBajar"),
                    KratosConfig.SD_COOLDOWN_BAJAR, 5, 400,
                    Component.translatable("fps_horizon.config.sdCooldownBajar.tooltip")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.sdCooldownSubir"),
                    KratosConfig.SD_COOLDOWN_SUBIR, 5, 400,
                    Component.translatable("fps_horizon.config.sdCooldownSubir.tooltip")));

                // FPS options (shown always, greyed if mode is MS)
                final boolean fpsActive = KratosConfig.SD_MODE.get() != KratosConfig.SdMode.MS
                                       && KratosConfig.SD_MODE.get() != KratosConfig.SdMode.OFF;
                final IntSlider sdMinFps = new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.sdMinFps"),
                    KratosConfig.SD_MIN_FPS, 10, 120,
                    Component.translatable("fps_horizon.config.sdMinFps.tooltip"));
                sdMinFps.active = fpsActive;
                y = addSlider(startX, y, sdMinFps);

                final IntSlider sdMaxFps = new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.sdMaxFps"),
                    KratosConfig.SD_MAX_FPS, 10, 120,
                    Component.translatable("fps_horizon.config.sdMaxFps.tooltip"));
                sdMaxFps.active = fpsActive;
                y = addSlider(startX, y, sdMaxFps);

                // Show warning if MS mode selected but not host
                final net.minecraft.client.Minecraft _mc = net.minecraft.client.Minecraft.getInstance();
                final boolean _isHost = _mc.hasSingleplayerServer();
                final KratosConfig.SdMode _sdMode = KratosConfig.SD_MODE.get();
                final boolean _msSelected = _sdMode == KratosConfig.SdMode.MS || _sdMode == KratosConfig.SdMode.BOTH;
                if (_msSelected && !_isHost) {
                    // Add warning label widget
                    final net.minecraft.client.gui.components.AbstractWidget warningBtn =
                        Button.builder(
                            net.minecraft.network.chat.Component.translatable("fps_horizon.config.sd_ms_warning"),
                            b -> {}
                        ).bounds(startX, y, W, H).build();
                    warningBtn.active = false;
                    this.addRenderableWidget(warningBtn);
                    this.categoryWidgets.add(warningBtn);
                    y += H + 6;
                }

                // MS options (shown always, greyed if mode is FPS)
                final boolean msActive = KratosConfig.SD_MODE.get() != KratosConfig.SdMode.FPS
                                      && KratosConfig.SD_MODE.get() != KratosConfig.SdMode.OFF;
                final IntSlider sdMaxMs = new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.sdMaxMs"),
                    KratosConfig.SD_MAX_MS, 10, 500,
                    Component.translatable("fps_horizon.config.sdMaxMs.tooltip"));
                sdMaxMs.active = msActive;
                y = addSlider(startX, y, sdMaxMs);

                final IntSlider sdMinMs = new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.sdMinMs"),
                    KratosConfig.SD_MIN_MS, 5, 500,
                    Component.translatable("fps_horizon.config.sdMinMs.tooltip"));
                sdMinMs.active = msActive;
                y = addSlider(startX, y, sdMinMs);
            }
            case DEBUG -> {
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.MOSTRAR_DEBUG.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.mostrarDebug.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.mostrarDebug"),
                        (btn, val) -> KratosConfig.MOSTRAR_DEBUG.set(val)));
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.DEBUG_VERBOSE.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.debugVerbose.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.debugVerbose"),
                        (btn, val) -> KratosConfig.DEBUG_VERBOSE.set(val)));
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.MICRO_HUD.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.microHud.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.microHud"),
                        (btn, val) -> KratosConfig.MICRO_HUD.set(val)));
            }
        }
    }

    private int addSlider(int x, int y, AbstractSliderButton slider) {
        slider.setX(x);
        slider.setY(y);
        this.addRenderableWidget(slider);
        this.categoryWidgets.add(slider);
        return y + H + 6;
    }

    private int addToggle(int x, int y, CycleButton<?> btn) {
        btn.setX(x);
        btn.setY(y);
        this.addRenderableWidget(btn);
        this.categoryWidgets.add(btn);
        return y + H + 6;
    }

    @Override
    public void render(final GuiGraphics g, final int mx, final int my, final float delta) {
        this.renderBackground(g);

        // Vertical divider between tabs and content
        final int divX = TAB_W + 12;
        g.fill(divX, 32, divX + 1, this.height - 32, 0x88FFFFFF);

        // Title
        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        // Active category title in content area
        final int contentCenterX = divX + 1 + (this.width - divX - 1) / 2;
        g.drawCenteredString(this.font,
            this.activeCategory.label().copy().withStyle(s -> s.withBold(true)),
            contentCenterX, 26, 0xFFFFAA);

        super.render(g, mx, my, delta);
    }

    @Override
    public void onClose() {
        KratosConfig.SPEC.save();
        this.minecraft.setScreen(this.parent);
    }

    /**
     * Detects the maximum supported Render Distance by querying the OptionInstance.
     */
    private int getMaxRenderDistance() {
        try {
            final Minecraft mc = this.minecraft;
            if (mc != null && mc.options != null) {
                final OptionInstance<Integer> rdOption = mc.options.renderDistance();
                if (rdOption != null) {
                    // Try to access maxInclusive via Mixin Accessor
                    try {
                        final Object range = rdOption.range;
                        if (range instanceof net.minecraft.client.OptionInstance.IntRange intRange) {
                            return ((pueblopaleta.mixin.KratosRenderDistanceAccessor) intRange).kratos$getMaxInclusive();
                        }
                    } catch (final Throwable t) {}
                }
            }
        } catch (final Throwable t) {}
        return 32; // Fallback
    }

    /**
     * Detects the maximum supported Simulation Distance.
     */
    private int getMaxSimulationDistance() {
        try {
            final Minecraft mc = this.minecraft;
            if (mc != null && mc.options != null) {
                final OptionInstance<Integer> sdOption = mc.options.simulationDistance();
                if (sdOption != null) {
                    // Try to access maxInclusive via Mixin Accessor
                    try {
                        final Object range = sdOption.range;
                        if (range instanceof net.minecraft.client.OptionInstance.IntRange intRange) {
                            return ((pueblopaleta.mixin.KratosSimulationDistanceAccessor) intRange).kratos$getSimMaxInclusive();
                        }
                    } catch (final Throwable t) {}
                }
            }
        } catch (final Throwable t) {}
        return 32; // Fallback
    }

    // ── Int Slider ────────────────────────────────────────────────────────────
    public static class IntSlider extends AbstractSliderButton {
        private final net.minecraftforge.common.ForgeConfigSpec.IntValue config;
        private final int min, max;
        private final Component label;

        IntSlider(int x, int y, int w, int h, Component label,
                  net.minecraftforge.common.ForgeConfigSpec.IntValue config,
                  int min, int max, Component tooltip) {
            super(x, y, w, h, Component.empty(), (double)(config.get() - min) / (max - min));
            this.config = config; this.min = min; this.max = max; this.label = label;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(
                label.getString() + ": " + (min + (int) Math.round(this.value * (max - min)))));
        }

        @Override
        protected void applyValue() {
            config.set(min + (int) Math.round(this.value * (max - min)));
        }
    }

    // ── Double Slider ───────────────────────────────────────────────────────────
    public static class DoubleSlider extends AbstractSliderButton {
        private final net.minecraftforge.common.ForgeConfigSpec.DoubleValue config;
        private final double min, max;
        private final Component label;

        DoubleSlider(int x, int y, int w, int h, Component label,
                     net.minecraftforge.common.ForgeConfigSpec.DoubleValue config,
                     double min, double max, Component tooltip) {
            super(x, y, w, h, Component.empty(), (config.get() - min) / (max - min));
            this.config = config; this.min = min; this.max = max; this.label = label;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(String.format("%s: %.2f",
                label.getString(), min + this.value * (max - min))));
        }

        @Override
        protected void applyValue() {
            config.set(Math.round((min + this.value * (max - min)) * 100.0) / 100.0);
        }
    }

    // ── Percent Slider ───────────────────────────────────────────────────────────
    public static class PercentSlider extends AbstractSliderButton {
        private final java.util.function.Supplier<Integer> getter;
        private final java.util.function.Consumer<Integer> setter;
        private final int min, max;
        private final Component label;

        public PercentSlider(int x, int y, int w, int h, Component label,
                             java.util.function.Supplier<Integer> getter,
                             java.util.function.Consumer<Integer> setter,
                             int min, int max, Component tooltip) {
            super(x, y, w, h, Component.empty(), (double)(getter.get() - min) / (max - min));
            this.getter = getter; this.setter = setter;
            this.min = min; this.max = max; this.label = label;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(label.getString() + ": " + 
                (min + (int) Math.round(this.value * (max - min))) + "%"));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + (int) Math.round(this.value * (max - min)));
        }
    }

    // ── Culling Vertical Slider (muestra porcentaje = val * 25) ────────────────
    public static class CullingVerticalSlider extends AbstractSliderButton {
        private final java.util.function.Supplier<Integer> getter;
        private final java.util.function.Consumer<Integer> setter;
        private final Component label;

        public CullingVerticalSlider(int x, int y, int w, int h, Component label,
                                     java.util.function.Supplier<Integer> getter,
                                     java.util.function.Consumer<Integer> setter,
                                     Component tooltip) {
            super(x, y, w, h, Component.empty(), (double)(getter.get() - 2) / (40 - 2));
            this.getter = getter; this.setter = setter; this.label = label;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        private int rawValue() {
            return 2 + (int) Math.round(this.value * (40 - 2));
        }

        @Override
        protected void updateMessage() {
            int percent = rawValue() * 25;
            this.setMessage(Component.literal(label.getString() + ": " + percent + "%"));
        }

        @Override
        protected void applyValue() {
            setter.accept(rawValue());
        }
    }


    // ── Validated Int Slider (shows warning color when invalid) ─────────────────
    public static class ValidatedIntSlider extends AbstractSliderButton {
        private final net.minecraftforge.common.ForgeConfigSpec.IntValue config;
        private final int min, max;
        private final Component label;
        private final java.util.function.BooleanSupplier isInvalid;

        public ValidatedIntSlider(int x, int y, int w, int h, Component label,
                      net.minecraftforge.common.ForgeConfigSpec.IntValue config,
                      int min, int max, Component tooltip,
                      java.util.function.BooleanSupplier isInvalid) {
            super(x, y, w, h, Component.empty(), (double)(config.get() - min) / (max - min));
            this.config = config; this.min = min; this.max = max;
            this.label = label; this.isInvalid = isInvalid;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            final int val = min + (int) Math.round(this.value * (max - min));
            final String prefix = isInvalid.getAsBoolean() ? "§c⚠ " : "";
            this.setMessage(Component.literal(prefix + label.getString() + ": " + val));
        }

        @Override
        protected void applyValue() {
            config.set(min + (int) Math.round(this.value * (max - min)));
        }
    }

}