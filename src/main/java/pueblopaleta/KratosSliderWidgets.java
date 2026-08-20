package pueblopaleta;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Small custom slider widgets used by KratosProfilesScreen (percent slider
 * and the 2-40 "vertical culling" slider). These used to be static inner
 * classes of the old NeoForge-only KratosConfigScreen; extracted here since
 * the main settings screen was replaced by Cloth Config, but the profile
 * editor screen still needs them.
 */
public class KratosSliderWidgets
{
    public static class PercentSlider extends AbstractSliderButton
    {
        private final Supplier<Integer> getter;
        private final Consumer<Integer> setter;
        private final int min, max;
        private final Component label;

        public PercentSlider(int x, int y, int w, int h, Component label,
                              Supplier<Integer> getter, Consumer<Integer> setter,
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
        public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final Minecraft mc = Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            g.drawString(mc.font, label.getString(), this.getX() + 4, textY, 0xFFFFFF, false);
            final String valStr = (min + (int) Math.round(this.value * (max - min))) + "%";
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, 0xFFFFFF, false);
        }
    }

    public static class CullingVerticalSlider extends AbstractSliderButton
    {
        private final Supplier<Integer> getter;
        private final Consumer<Integer> setter;
        private final Component label;

        public CullingVerticalSlider(int x, int y, int w, int h, Component label,
                                      Supplier<Integer> getter, Consumer<Integer> setter,
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
        public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            final Component saved = this.getMessage();
            this.setMessage(Component.empty());
            super.renderWidget(g, mouseX, mouseY, partialTick);
            this.setMessage(saved);
            final Minecraft mc = Minecraft.getInstance();
            final int textY = this.getY() + (this.height - 8) / 2;
            g.drawString(mc.font, label.getString(), this.getX() + 4, textY, 0xFFFFFF, false);
            final String valStr = rawValue() * 25 + "%";
            g.drawString(mc.font, valStr, this.getX() + this.width - mc.font.width(valStr) - 4, textY, 0xFFFFFF, false);
        }
    }
}
