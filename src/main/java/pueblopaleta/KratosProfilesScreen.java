package pueblopaleta;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class KratosProfilesScreen extends Screen
{
    private final Screen parent;

    private enum Mode { LIST, ADD_TYPE, ADD_FORM, DELETE_SELECT }
    private Mode mode = Mode.LIST;

    private final List<KratosProfiles.Profile> selected = new ArrayList<>();

    // Add/edit form state
    private KratosProfiles.ProfileType addType;
    private EditBox nameBox;
    private EditBox rdExactBox;
    private EditBox rdMinBox;
    private EditBox rdMaxBox;
    private int addVertical   = 4;   // default 100%
    private int addHorizontal = 0;   // default 0%
    private String errorMsg = "";
    private KratosProfiles.Profile editingProfile = null;

    // Scroll
    private int scrollOffset = 0;
    private static final int ROW_H   = 26;
    private static final int LIST_TOP = 44;
    private static final int LIST_BOTTOM_PAD = 60; // space for bottom buttons

    private static final int W  = 220;
    private static final int H  = 20;
    private static final int BW = 100;

    public KratosProfilesScreen(final Screen parent) {
        super(Component.translatable("fps_horizon.profiles.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.errorMsg = "";
        switch (mode) {
            case LIST          -> initList();
            case ADD_TYPE      -> initAddType();
            case ADD_FORM      -> initAddForm();
            case DELETE_SELECT -> initDeleteSelect();
        }
    }

    // ── LIST ────────────────────────────────────────────────────────────────────
    private void initList() {
        final int cx = this.width / 2;
        final int bottom = this.height - 28;
        final int listBottom = bottom - 32;
        final int visibleRows = (listBottom - LIST_TOP) / ROW_H;
        final int totalProfiles = KratosProfiles.getProfiles().size();

        // Clamp scroll
        scrollOffset = Math.max(0, Math.min(scrollOffset,
            Math.max(0, totalProfiles - visibleRows)));

        // Render visible profiles
        int y = LIST_TOP;
        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, totalProfiles); i++) {
            final KratosProfiles.Profile p = KratosProfiles.getProfiles().get(i);
            final KratosProfiles.Profile fp = p;

            this.addRenderableWidget(Button.builder(
                Component.literal(p.displayName()),
                b -> {}
            ).bounds(cx - W / 2, y, W - 46, H).build());

            this.addRenderableWidget(Button.builder(
                Component.literal("✎"),
                b -> { editingProfile = fp; addType = fp.type;
                       addVertical = fp.vertical; addHorizontal = fp.horizontal;
                       mode = Mode.ADD_FORM; this.init(); }
            ).bounds(cx + W / 2 - 44, y, 20, H).build());

            this.addRenderableWidget(Button.builder(
                Component.literal("✗"),
                b -> { KratosProfiles.removeProfile(fp); this.init(); }
            ).bounds(cx + W / 2 - 22, y, 20, H).build());

            y += ROW_H;
        }

        // Scroll arrows (only if needed)
        if (totalProfiles > visibleRows) {
            final Button up = Button.builder(Component.literal("▲"),
                b -> { scrollOffset = Math.max(0, scrollOffset - 1); this.init(); }
            ).bounds(cx + W / 2 + 4, LIST_TOP, 18, 18).build();
            up.active = scrollOffset > 0;
            this.addRenderableWidget(up);

            final Button down = Button.builder(Component.literal("▼"),
                b -> { scrollOffset = Math.min(totalProfiles - visibleRows, scrollOffset + 1); this.init(); }
            ).bounds(cx + W / 2 + 4, listBottom - 18, 18, 18).build();
            down.active = scrollOffset < totalProfiles - visibleRows;
            this.addRenderableWidget(down);
        }

        // Bottom buttons
        this.addRenderableWidget(Button.builder(
            Component.translatable("fps_horizon.profiles.add"),
            b -> { mode = Mode.ADD_TYPE; editingProfile = null;
                   addVertical = 4; addHorizontal = 0; this.init(); }
        ).bounds(cx - BW - 4, bottom, BW, H).build());

        final Button deleteBtn = Button.builder(
            Component.translatable("fps_horizon.profiles.delete"),
            b -> { mode = Mode.DELETE_SELECT; selected.clear(); scrollOffset = 0; this.init(); }
        ).bounds(cx + 4, bottom, BW, H).build();
        deleteBtn.active = !KratosProfiles.getProfiles().isEmpty();
        this.addRenderableWidget(deleteBtn);

        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.back"),
            b -> this.onClose()
        ).bounds(cx - 50, bottom - 26, 100, H).build());
    }

    // ── ADD TYPE ────────────────────────────────────────────────────────────────
    private void initAddType() {
        final int cx = this.width / 2;
        final int cy = this.height / 2;

        this.addRenderableWidget(Button.builder(
            Component.translatable("fps_horizon.profiles.exact"),
            b -> { addType = KratosProfiles.ProfileType.EXACT; mode = Mode.ADD_FORM; this.init(); }
        ).bounds(cx - BW - 4, cy - 14, BW, H).build());

        this.addRenderableWidget(Button.builder(
            Component.translatable("fps_horizon.profiles.range"),
            b -> { addType = KratosProfiles.ProfileType.RANGE; mode = Mode.ADD_FORM; this.init(); }
        ).bounds(cx + 4, cy - 14, BW, H).build());

        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.back"),
            b -> { mode = Mode.LIST; this.init(); }
        ).bounds(cx - 50, cy + 14, 100, H).build());
    }

    // ── ADD FORM ────────────────────────────────────────────────────────────────
    private void initAddForm() {
        final int cx = this.width / 2;
        int y = 36;

        nameBox = new EditBox(this.font, cx - W / 2, y, W, H,
            Component.translatable("fps_horizon.profiles.name"));
        nameBox.setMaxLength(32);
        if (editingProfile != null) nameBox.setValue(editingProfile.name);
        this.addRenderableWidget(nameBox);
        y += H + 6;

        if (addType == KratosProfiles.ProfileType.EXACT) {
            rdExactBox = new EditBox(this.font, cx - W / 2, y, W, H,
                Component.translatable("fps_horizon.profiles.rd_exact"));
            rdExactBox.setMaxLength(4);
            rdExactBox.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
            if (editingProfile != null) rdExactBox.setValue(String.valueOf(editingProfile.rdExact));
            this.addRenderableWidget(rdExactBox);
            y += H + 6;
        } else {
            final int halfW = W / 2 - 2;
            rdMinBox = new EditBox(this.font, cx - W / 2, y, halfW, H,
                Component.translatable("fps_horizon.profiles.rd_min"));
            rdMinBox.setMaxLength(4);
            rdMinBox.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
            if (editingProfile != null) rdMinBox.setValue(String.valueOf(editingProfile.rdMin));
            this.addRenderableWidget(rdMinBox);

            rdMaxBox = new EditBox(this.font, cx + 2, y, halfW, H,
                Component.translatable("fps_horizon.profiles.rd_max"));
            rdMaxBox.setMaxLength(4);
            rdMaxBox.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
            if (editingProfile != null) rdMaxBox.setValue(String.valueOf(editingProfile.rdMax));
            this.addRenderableWidget(rdMaxBox);
            y += H + 6;
        }

        // Vertical slider
        final int initV = addVertical;
        this.addRenderableWidget(new KratosConfigScreen.CullingVerticalSlider(
            cx - W / 2, y, W, H,
            Component.translatable("fps_horizon.config.cullingVertical"),
            () -> initV, v -> addVertical = v,
            Component.translatable("fps_horizon.config.cullingVertical.tooltip")));
        y += H + 6;

        // Horizontal slider
        final int initH = addHorizontal;
        this.addRenderableWidget(new KratosConfigScreen.PercentSlider(
            cx - W / 2, y, W, H,
            Component.translatable("fps_horizon.config.cullingHorizontal"),
            () -> initH, v -> addHorizontal = v, 0, 100,
            Component.translatable("fps_horizon.config.cullingHorizontal.tooltip")));
        y += H + 10;

        this.addRenderableWidget(Button.builder(
            Component.translatable("fps_horizon.profiles.save"),
            b -> trySave()
        ).bounds(cx - BW - 4, y, BW, H).build());

        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.cancel"),
            b -> { mode = Mode.LIST; this.init(); }
        ).bounds(cx + 4, y, BW, H).build());
    }

    // ── DELETE SELECT ───────────────────────────────────────────────────────────
    private void initDeleteSelect() {
        final int cx = this.width / 2;
        final int bottom = this.height - 28;
        final int listBottom = bottom - 32;
        final int visibleRows = (listBottom - LIST_TOP) / ROW_H;
        final int totalProfiles = KratosProfiles.getProfiles().size();

        scrollOffset = Math.max(0, Math.min(scrollOffset,
            Math.max(0, totalProfiles - visibleRows)));

        int y = LIST_TOP;
        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, totalProfiles); i++) {
            final KratosProfiles.Profile p = KratosProfiles.getProfiles().get(i);
            final KratosProfiles.Profile fp = p;
            final boolean sel = selected.contains(p);

            this.addRenderableWidget(Button.builder(
                Component.literal((sel ? "§c[✓] " : "[ ] ") + p.displayName()),
                b -> {
                    if (selected.contains(fp)) selected.remove(fp);
                    else selected.add(fp);
                    this.init();
                }
            ).bounds(cx - W / 2, y, W, H).build());
            y += ROW_H;
        }

        // Scroll arrows
        if (totalProfiles > visibleRows) {
            final Button up = Button.builder(Component.literal("▲"),
                b -> { scrollOffset = Math.max(0, scrollOffset - 1); this.init(); }
            ).bounds(cx + W / 2 + 4, LIST_TOP, 18, 18).build();
            up.active = scrollOffset > 0;
            this.addRenderableWidget(up);

            final Button down = Button.builder(Component.literal("▼"),
                b -> { scrollOffset = Math.min(totalProfiles - visibleRows, scrollOffset + 1); this.init(); }
            ).bounds(cx + W / 2 + 4, listBottom - 18, 18, 18).build();
            down.active = scrollOffset < totalProfiles - visibleRows;
            this.addRenderableWidget(down);
        }

        final Button delBtn = Button.builder(
            Component.translatable("fps_horizon.profiles.confirm_delete"),
            b -> minecraft.setScreen(new ConfirmDeleteScreen(this, selected))
        ).bounds(cx - BW - 4, bottom, BW, H).build();
        delBtn.active = !selected.isEmpty();
        this.addRenderableWidget(delBtn);

        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.cancel"),
            b -> { mode = Mode.LIST; selected.clear(); scrollOffset = 0; this.init(); }
        ).bounds(cx + 4, bottom, BW, H).build());
    }

    void confirmDelete() {
        KratosProfiles.removeProfiles(new ArrayList<>(selected));
        selected.clear();
        scrollOffset = 0;
        mode = Mode.LIST;
        this.init();
    }

    // ── SAVE ────────────────────────────────────────────────────────────────────
    private void trySave() {
        errorMsg = "";
        final String name = nameBox.getValue().trim();
        if (name.isEmpty()) { errorMsg = "§cNombre requerido"; return; }

        int rdEx = 0, rdMn = 0, rdMx = 0;

        if (addType == KratosProfiles.ProfileType.EXACT) {
            if (rdExactBox.getValue().isEmpty()) { errorMsg = "§cRD requerido"; return; }
            rdEx = Integer.parseInt(rdExactBox.getValue());
        } else {
            if (rdMinBox.getValue().isEmpty() || rdMaxBox.getValue().isEmpty()) {
                errorMsg = "§cRD min y max requeridos"; return;
            }
            rdMn = Integer.parseInt(rdMinBox.getValue());
            rdMx = Integer.parseInt(rdMaxBox.getValue());
            if (rdMn >= rdMx) { errorMsg = "§cRD min debe ser menor que max"; return; }
        }

        final KratosProfiles.Profile conflict = KratosProfiles.findConflict(
            addType, rdEx, rdMn, rdMx, editingProfile);
        if (conflict != null) {
            errorMsg = "§cConflicto con: " + conflict.displayName();
            return;
        }

        if (editingProfile != null) {
            editingProfile.name       = name;
            editingProfile.type       = addType;
            editingProfile.rdExact    = rdEx;
            editingProfile.rdMin      = rdMn;
            editingProfile.rdMax      = rdMx;
            editingProfile.vertical   = addVertical;
            editingProfile.horizontal = addHorizontal;
            KratosProfiles.updateProfile(editingProfile);
        } else {
            KratosProfiles.addProfile(new KratosProfiles.Profile(
                name, addType, rdEx, rdMn, rdMx, addVertical, addHorizontal));
        }
        mode = Mode.LIST;
        scrollOffset = 0;
        this.init();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mode == Mode.LIST || mode == Mode.DELETE_SELECT) {
            final int total = KratosProfiles.getProfiles().size();
            final int visibleRows = (this.height - 28 - 32 - LIST_TOP) / ROW_H;
            scrollOffset = (int) Math.max(0, Math.min(total - visibleRows,
                scrollOffset - delta));
            this.init();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(final GuiGraphics g, final int mx, final int my, final float delta) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        switch (mode) {
            case LIST -> {
                if (KratosProfiles.getProfiles().isEmpty()) {
                    g.drawCenteredString(this.font,
                        Component.translatable("fps_horizon.profiles.empty"),
                        this.width / 2, this.height / 2, 0xAAAAAA);
                }
            }
            case ADD_TYPE ->
                g.drawCenteredString(this.font,
                    Component.translatable("fps_horizon.profiles.select_type"),
                    this.width / 2, this.height / 2 - 30, 0xFFFFAA);
            case ADD_FORM -> {
                g.drawString(this.font,
                    Component.translatable(editingProfile != null
                        ? "fps_horizon.profiles.editing" : "fps_horizon.profiles.adding"),
                    this.width / 2 - W / 2, 24, 0xFFFFAA);
                if (!errorMsg.isEmpty()) {
                    g.drawCenteredString(this.font, Component.literal(errorMsg),
                        this.width / 2, this.height - 48, 0xFF4444);
                }
            }
            case DELETE_SELECT ->
                g.drawCenteredString(this.font,
                    Component.translatable("fps_horizon.profiles.select_to_delete"),
                    this.width / 2, 28, 0xFFAAAA);
        }
        super.render(g, mx, my, delta);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    // ── Confirm Delete ───────────────────────────────────────────────────────────
    static class ConfirmDeleteScreen extends Screen {
        private final KratosProfilesScreen parent;
        private final List<KratosProfiles.Profile> toDelete;

        ConfirmDeleteScreen(KratosProfilesScreen parent, List<KratosProfiles.Profile> toDelete) {
            super(Component.translatable("fps_horizon.profiles.confirm_delete"));
            this.parent = parent;
            this.toDelete = new ArrayList<>(toDelete);
        }

        @Override
        protected void init() {
            final int cx = this.width / 2;
            final int cy = this.height / 2;
            this.addRenderableWidget(Button.builder(
                Component.translatable("fps_horizon.profiles.confirm_delete"),
                b -> { parent.confirmDelete(); minecraft.setScreen(parent); }
            ).bounds(cx - 104, cy + 10, 100, 20).build());
            this.addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                b -> minecraft.setScreen(parent)
            ).bounds(cx + 4, cy + 10, 100, 20).build());
        }

        @Override
        public void render(GuiGraphics g, int mx, int my, float delta) {
            this.renderBackground(g);
            g.drawCenteredString(this.font,
                Component.translatable("fps_horizon.profiles.confirm_delete"),
                this.width / 2, this.height / 2 - 20, 0xFF4444);
            g.drawCenteredString(this.font,
                Component.literal("§7" + toDelete.size() + " perfil(es) serán eliminados"),
                this.width / 2, this.height / 2, 0xAAAAAA);
            super.render(g, mx, my, delta);
        }
    }
}
