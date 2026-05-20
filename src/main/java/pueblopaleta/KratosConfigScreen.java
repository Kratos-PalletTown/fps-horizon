package pueblopaleta;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.ArrayList;

public class KratosConfigScreen extends Screen
{
    private final Screen parent;

    private enum Category {
        FPS, RENDER_DISTANCE, COOLDOWN, FOG, CULLING, DEBUG;

        public Component label() {
            return switch (this) {
                case FPS             -> Component.translatable("fps_horizon.config.fps");
                case RENDER_DISTANCE -> Component.translatable("fps_horizon.config.render_distance");
                case COOLDOWN        -> Component.translatable("fps_horizon.config.cooldown");
                case FOG             -> Component.translatable("fps_horizon.config.fog");
                case DEBUG           -> Component.translatable("fps_horizon.config.debug");
                case CULLING         -> Component.translatable("fps_horizon.config.culling");
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
            }
            case RENDER_DISTANCE -> {
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.minRenderDistance"),
                    KratosConfig.MIN_RD, 2, 32,
                    Component.translatable("fps_horizon.config.minRenderDistance.tooltip")));
                y = addSlider(startX, y, new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.maxRenderDistance"),
                    KratosConfig.MAX_RD, 2, 32,
                    Component.translatable("fps_horizon.config.maxRenderDistance.tooltip")));
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
                    KratosConfig.CULLING_VERTICAL,
                    Component.translatable("fps_horizon.config.cullingVertical.tooltip"));
                vSlider.active = !perfilesActivos;
                y = addSlider(startX, y, vSlider);

                // Slider horizontal (gris si perfiles activos)
                final IntSlider hSlider = new IntSlider(0, 0, W, H,
                    Component.translatable("fps_horizon.config.cullingHorizontal"),
                    KratosConfig.CULLING_HORIZONTAL, 0, 100,
                    Component.translatable("fps_horizon.config.cullingHorizontal.tooltip"));
                hSlider.active = !perfilesActivos;
                y = addSlider(startX, y, hSlider);

                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.CULLING_ENTIDADES.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingEntidades.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.cullingEntidades"),
                        (btn, val) -> KratosConfig.CULLING_ENTIDADES.set(val)));

                y = addToggle(startX, y, CycleButton.onOffBuilder(KratosConfig.CULLING_DINAMICO.get())
                    .withTooltip(v -> Tooltip.create(Component.translatable("fps_horizon.config.cullingDinamico.tooltip")))
                    .create(0, 0, W, H,
                        Component.translatable("fps_horizon.config.cullingDinamico"),
                        (btn, val) -> KratosConfig.CULLING_DINAMICO.set(val)));

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

    // ── Int Slider ──────────────────────────────────────────────────────────────
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

    // ── Step Int Slider (saltos fijos) ─────────────────────────────────────────
    public static class StepIntSlider extends AbstractSliderButton {
        private final net.minecraftforge.common.ForgeConfigSpec.IntValue config;
        private final int min, max, step;
        private final Component label;

        StepIntSlider(int x, int y, int w, int h, Component label,
                      net.minecraftforge.common.ForgeConfigSpec.IntValue config,
                      int min, int max, int step, Component tooltip) {
            super(x, y, w, h, Component.empty(), (double)(config.get() - min) / (max - min));
            this.config = config; this.min = min; this.max = max;
            this.step = step; this.label = label;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        private int snappedValue() {
            int raw = min + (int) Math.round(this.value * (max - min));
            return Math.min(max, min + Math.round((float)(raw - min) / step) * step);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(label.getString() + ": " + snappedValue()));
        }

        @Override
        protected void applyValue() {
            config.set(snappedValue());
        }
    }


    // ── Culling Vertical Slider (muestra multiplicador real = val/4) ────────────
    public static class CullingVerticalSlider extends AbstractSliderButton {
        private final net.minecraftforge.common.ForgeConfigSpec.IntValue config;
        private final Component label;

        CullingVerticalSlider(int x, int y, int w, int h, Component label,
                              net.minecraftforge.common.ForgeConfigSpec.IntValue config,
                              Component tooltip) {
            super(x, y, w, h, Component.empty(), (double)(config.get() - 2) / (40 - 2));
            this.config = config; this.label = label;
            this.setTooltip(Tooltip.create(tooltip));
            this.updateMessage();
        }

        private int rawValue() {
            return 2 + (int) Math.round(this.value * (40 - 2));
        }

        @Override
        protected void updateMessage() {
            double multiplier = rawValue() / 4.0;
            this.setMessage(Component.literal(
                String.format("%s: %.2fx", label.getString(), multiplier)));
        }

        @Override
        protected void applyValue() {
            config.set(rawValue());
        }
    }

}