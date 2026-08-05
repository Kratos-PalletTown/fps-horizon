package pueblopaleta;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import java.util.List;
import java.util.ArrayList;

public class KratosConfigScreen extends Screen
{
    private final Screen parent;
    private int detectedMaxRD = 32;
    private int detectedMaxSD = 32;

    private enum Category {
        RENDER_DISTANCE, SIMULATION, FOG, CULLING, DEBUG;

        public Component label() {
            return switch (this) {
                case RENDER_DISTANCE -> Component.translatable("fps_horizon.config.render_distance");
                case SIMULATION      -> Component.translatable("fps_horizon.config.simulation");
                case FOG             -> Component.translatable("fps_horizon.config.fog");
                case CULLING         -> Component.translatable("fps_horizon.config.culling");
                case DEBUG           -> Component.translatable("fps_horizon.config.debug");
            };
        }
    }

    private Category activeCategory = Category.RENDER_DISTANCE;

    // Widgets of the current category
    private final List<net.minecraft.client.gui.components.AbstractWidget> categoryWidgets = new ArrayList<>();

    private static final int W        = 240;
    private static final int H        = 20;
    private static final int TAB_W    = 115;
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
            case RENDER_DISTANCE -> {
                // FPS thresholds
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
                // Render distance
                y = addSlider(startX, y, new ValidatedIntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.minRenderDistance"),
                    KratosConfig.MIN_RD, 2, this.detectedMaxRD,
                    Component.literal("⚡ " + this.detectedMaxRD + " chunks max"),
                    () -> KratosConfig.MIN_RD.get() >= KratosConfig.MAX_RD.get()));
                y = addSlider(startX, y, new ValidatedIntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.maxRenderDistance"),
                    KratosConfig.MAX_RD, 2, this.detectedMaxRD,
                    Component.literal("⚡ " + this.detectedMaxRD + " chunks max"),
                    () -> KratosConfig.MIN_RD.get() >= KratosConfig.MAX_RD.get()));
                // Cooldowns
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
                // Toggle culling activo
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.CULLING_ACTIVO.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingActivo.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.cullingActivo"),
                        (btn, val) -> KratosConfig.CULLING_ACTIVO.set(val)));

                // Cull entities
                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.CULLING_ENTIDADES.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingEntidades.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.cullingEntidades"),
                        (btn, val) -> KratosConfig.CULLING_ENTIDADES.set(val)));

                // Radius factor
                y = addSlider(startX, y, new DoubleSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cullingRadiusFactor"),
                    KratosConfig.CULLING_RADIUS_FACTOR, 0.8, 1.5,
                    Component.translatable("fps_horizon.config.cullingRadiusFactor.tooltip")));

                // Extra blocks
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cullingExtraBlocks"),
                    KratosConfig.CULLING_EXTRA_BLOCKS, 0, 64,
                    Component.translatable("fps_horizon.config.cullingExtraBlocks.tooltip")));
            }
            case SIMULATION -> {
                final int colW = (W - 4) / 2;
                final int colR = startX + colW + 4;
                final KratosConfig.SdMode currentMode = KratosConfig.SD_MODE.get();
                final boolean fpsActive = currentMode != KratosConfig.SdMode.MS
                                       && currentMode != KratosConfig.SdMode.OFF;
                final boolean msActive  = currentMode != KratosConfig.SdMode.FPS
                                       && currentMode != KratosConfig.SdMode.OFF;
                final net.minecraft.client.Minecraft _mcInst = net.minecraft.client.Minecraft.getInstance();
                final boolean isHost = _mcInst.level == null || _mcInst.hasSingleplayerServer();

                // Mode button - full width at top
                y = addToggle(startX, y, CycleButton.<KratosConfig.SdMode>builder(
                    v -> Component.translatable("fps_horizon.config.sd_mode." + v.name().toLowerCase()))
                    .withValues(KratosConfig.SdMode.values())
                    .withInitialValue(currentMode)
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.sd_mode.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.sd_mode"),
                        (btn, val) -> { KratosConfig.SD_MODE.set(val); this.rebuildCategory(); }));
                y += 4;

                // Two columns layout
                int yL = y; // left column y
                int yR = y; // right column y

                // LEFT: SD min/max + cooldowns
                final ValidatedIntSlider minSd = new ValidatedIntSlider(startX, yL, colW, H,
                    Component.translatable("fps_horizon.config.minSimDistance"),
                    KratosConfig.MIN_SD, 5, 32,
                    Component.translatable("fps_horizon.config.minSimDistance.tooltip"),
                    () -> KratosConfig.MIN_SD.get() >= KratosConfig.MAX_SD.get());
                this.addRenderableWidget(minSd); this.categoryWidgets.add(minSd); yL += H + 6;

                final ValidatedIntSlider maxSd = new ValidatedIntSlider(startX, yL, colW, H,
                    Component.translatable("fps_horizon.config.maxSimDistance"),
                    KratosConfig.MAX_SD, 5, 32,
                    Component.translatable("fps_horizon.config.maxSimDistance.tooltip"),
                    () -> KratosConfig.MIN_SD.get() >= KratosConfig.MAX_SD.get());
                this.addRenderableWidget(maxSd); this.categoryWidgets.add(maxSd); yL += H + 6;

                final IntSlider cdBajar = new IntSlider(startX, yL, colW, H,
                    Component.translatable("fps_horizon.config.sdCooldownBajar"),
                    KratosConfig.SD_COOLDOWN_BAJAR, 5, 400,
                    Component.translatable("fps_horizon.config.sdCooldownBajar.tooltip"));
                this.addRenderableWidget(cdBajar); this.categoryWidgets.add(cdBajar); yL += H + 6;

                final IntSlider cdSubir = new IntSlider(startX, yL, colW, H,
                    Component.translatable("fps_horizon.config.sdCooldownSubir"),
                    KratosConfig.SD_COOLDOWN_SUBIR, 5, 400,
                    Component.translatable("fps_horizon.config.sdCooldownSubir.tooltip"));
                this.addRenderableWidget(cdSubir); this.categoryWidgets.add(cdSubir); yL += H + 6;

                // RIGHT: FPS/MS thresholds
                final IntSlider sdMinFps = new IntSlider(colR, yR, colW, H,
                    Component.translatable("fps_horizon.config.sdMinFps"),
                    KratosConfig.SD_MIN_FPS, 10, 120,
                    Component.translatable("fps_horizon.config.sdMinFps.tooltip"));
                sdMinFps.active = fpsActive;
                this.addRenderableWidget(sdMinFps); this.categoryWidgets.add(sdMinFps); yR += H + 6;

                final IntSlider sdMaxFps = new IntSlider(colR, yR, colW, H,
                    Component.translatable("fps_horizon.config.sdMaxFps"),
                    KratosConfig.SD_MAX_FPS, 10, 120,
                    Component.translatable("fps_horizon.config.sdMaxFps.tooltip"));
                sdMaxFps.active = fpsActive;
                this.addRenderableWidget(sdMaxFps); this.categoryWidgets.add(sdMaxFps); yR += H + 6;

                final IntSlider sdMaxMs = new IntSlider(colR, yR, colW, H,
                    Component.translatable("fps_horizon.config.sdMaxMs"),
                    KratosConfig.SD_MAX_MS, 10, 500,
                    Component.translatable("fps_horizon.config.sdMaxMs.tooltip"));
                sdMaxMs.active = msActive && isHost;
                this.addRenderableWidget(sdMaxMs); this.categoryWidgets.add(sdMaxMs); yR += H + 6;

                final IntSlider sdMinMs = new IntSlider(colR, yR, colW, H,
                    Component.translatable("fps_horizon.config.sdMinMs"),
                    KratosConfig.SD_MIN_MS, 5, 500,
                    Component.translatable("fps_horizon.config.sdMinMs.tooltip"));
                sdMinMs.active = msActive && isHost;
                this.addRenderableWidget(sdMinMs); this.categoryWidgets.add(sdMinMs); yR += H + 6;

                // Warning if MS selected but not host - below columns
                y = Math.max(yL, yR);
                if ((currentMode == KratosConfig.SdMode.MS || currentMode == KratosConfig.SdMode.BOTH) && !isHost) {
                    final Button warningBtn = Button.builder(
                        Component.translatable("fps_horizon.config.sd_ms_warning"), b -> {}
                    ).bounds(startX, y, W, H).build();
                    warningBtn.active = false;
                    this.addRenderableWidget(warningBtn);
                    this.categoryWidgets.add(warningBtn);
                }
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
        this.renderBackground(g, mx, my, delta);

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
        return 32;
    }


    /**
     * Detects the maximum supported Simulation Distance.
     */
    private int getMaxSimulationDistance() {
        return 32;
    }


    // ── Int Slider ────────────────────────────────────────────────────────────
    public static class IntSlider extends AbstractSliderButton {
        private final net.neoforged.neoforge.common.ModConfigSpec.IntValue config;
        private final int min, max;
        private final Component label;

        IntSlider(int x, int y, int w, int h, Component label,
                  net.neoforged.neoforge.common.ModConfigSpec.IntValue config,
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

        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            g.drawString(mc.font, label.getString(), this.getX() + 4, textY, 0xFFFFFF, false);
            final String valStr = String.valueOf(min + (int) Math.round(this.value * (max - min)));
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, 0xFFFFFF, false);
        }
    }

    // ── Double Slider ───────────────────────────────────────────────────────────
    public static class DoubleSlider extends AbstractSliderButton {
        private final net.neoforged.neoforge.common.ModConfigSpec.DoubleValue config;
        private final double min, max;
        private final Component label;

        DoubleSlider(int x, int y, int w, int h, Component label,
                     net.neoforged.neoforge.common.ModConfigSpec.DoubleValue config,
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
        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            g.drawString(mc.font, label.getString(), this.getX() + 4, textY, 0xFFFFFF, false);
            final String valStr = String.format("%.2f", min + this.value * (max - min));
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, 0xFFFFFF, false);
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
        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            g.drawString(mc.font, label.getString(), this.getX() + 4, textY, 0xFFFFFF, false);
            final String valStr = (min + (int) Math.round(this.value * (max - min))) + "%";
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, 0xFFFFFF, false);
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
        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            g.drawString(mc.font, label.getString(), this.getX() + 4, textY, 0xFFFFFF, false);
            final String valStr = rawValue() * 25 + "%";
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, 0xFFFFFF, false);
        }
    }


    // ── Validated Int Slider (shows warning color when invalid) ─────────────────
    public static class ValidatedIntSlider extends AbstractSliderButton {
        private final net.neoforged.neoforge.common.ModConfigSpec.IntValue config;
        private final int min, max;
        private final Component label;
        private final java.util.function.BooleanSupplier isInvalid;

        public ValidatedIntSlider(int x, int y, int w, int h, Component label,
                      net.neoforged.neoforge.common.ModConfigSpec.IntValue config,
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
        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            final boolean invalid = isInvalid.getAsBoolean();
            final int color = invalid ? 0xFF4444 : 0xFFFFFF;
            final String labelStr = (invalid ? "§c⚠ " : "") + label.getString();
            g.drawString(mc.font, labelStr, this.getX() + 4, textY, color, false);
            final String valStr = String.valueOf(min + (int) Math.round(this.value * (max - min)));
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, color, false);
        }
    }

}