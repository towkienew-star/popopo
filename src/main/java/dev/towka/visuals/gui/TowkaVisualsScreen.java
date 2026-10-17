package dev.towka.visuals.gui;

import dev.towka.visuals.TowkaVisualsClient;
import dev.towka.visuals.TowkaVisualsConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class TowkaVisualsScreen extends Screen {
    private Category category = Category.HUD;
    private int left;
    private int top;
    private int panelWidth;
    private int panelHeight;

    public TowkaVisualsScreen() {
        super(Text.literal("TowkaVisuals"));
    }

    @Override
    protected void init() {
        panelWidth = Math.min(520, width - 20);
        panelHeight = Math.min(285, height - 20);
        left = (width - panelWidth) / 2;
        top = (height - panelHeight) / 2;

        int sidebarX = left + 8;
        int sidebarY = top + 34;
        int sidebarWidth = 118;
        int sidebarHeight = 20;
        int i = 0;
        for (Category value : Category.values()) {
            final Category cat = value;
            addDrawableChild(ButtonWidget.builder(Text.literal((category == cat ? "▶ " : "") + cat.label), b -> {
                category = cat;
                clearAndInit();
            }).dimensions(sidebarX, sidebarY + (i++ * 24), sidebarWidth, sidebarHeight).build());
        }

        int x = left + 138;
        int y = top + 40;
        int w = panelWidth - 154;
        int column = (w - 8) / 2;

        switch (category) {
            case COMBAT -> {
                toggle(x, y, column, "Swing Hand", TowkaVisualsConfig.INSTANCE.swingHand, () -> TowkaVisualsConfig.INSTANCE.swingHand = !TowkaVisualsConfig.INSTANCE.swingHand);
                action(x + column + 8, y, column, "Swing Speed: " + TowkaVisualsConfig.INSTANCE.swingSpeed, TowkaVisualsConfig::cycleSwingSpeed);
                action(x, y + 24, column, "Player Visuals", TowkaVisualsClient::openPlayerEditor);
                action(x + column + 8, y + 24, column, "Close", this::close);
            }
            case WORLD -> {
                toggle(x, y, column, "Custom World", TowkaVisualsConfig.INSTANCE.customWorld, () -> TowkaVisualsConfig.INSTANCE.customWorld = !TowkaVisualsConfig.INSTANCE.customWorld);
                toggle(x + column + 8, y, column, "World Particles", TowkaVisualsConfig.INSTANCE.worldParticles, () -> TowkaVisualsConfig.INSTANCE.worldParticles = !TowkaVisualsConfig.INSTANCE.worldParticles);
                action(x, y + 24, column, "Particle Style: " + TowkaVisualsConfig.particleLabel(), TowkaVisualsConfig::cycleParticleMode);
                toggle(x + column + 8, y + 24, column, "FullBright", TowkaVisualsConfig.INSTANCE.fullBright, () -> TowkaVisualsConfig.INSTANCE.fullBright = !TowkaVisualsConfig.INSTANCE.fullBright);
            }
            case HUD -> {
                toggle(x, y, column, "HUD", TowkaVisualsConfig.INSTANCE.hudEnabled, () -> TowkaVisualsConfig.INSTANCE.hudEnabled = !TowkaVisualsConfig.INSTANCE.hudEnabled);
                toggle(x + column + 8, y, column, "Logo TowkaVisuals", TowkaVisualsConfig.INSTANCE.watermark, () -> TowkaVisualsConfig.INSTANCE.watermark = !TowkaVisualsConfig.INSTANCE.watermark);
                toggle(x, y + 24, column, "Show FPS", TowkaVisualsConfig.INSTANCE.showFps, () -> TowkaVisualsConfig.INSTANCE.showFps = !TowkaVisualsConfig.INSTANCE.showFps);
                toggle(x + column + 8, y + 24, column, "Show Ping", TowkaVisualsConfig.INSTANCE.showPing, () -> TowkaVisualsConfig.INSTANCE.showPing = !TowkaVisualsConfig.INSTANCE.showPing);
                toggle(x, y + 48, column, "Show Coordinates", TowkaVisualsConfig.INSTANCE.showCoords, () -> TowkaVisualsConfig.INSTANCE.showCoords = !TowkaVisualsConfig.INSTANCE.showCoords);
                toggle(x + column + 8, y + 48, column, "Show Clock", TowkaVisualsConfig.INSTANCE.showClock, () -> TowkaVisualsConfig.INSTANCE.showClock = !TowkaVisualsConfig.INSTANCE.showClock);
            }
            case FUN -> {
                toggle(x, y, column, "Big Head (Self)", TowkaVisualsConfig.INSTANCE.bigHeadSelf, () -> TowkaVisualsConfig.INSTANCE.bigHeadSelf = !TowkaVisualsConfig.INSTANCE.bigHeadSelf);
                toggle(x + column + 8, y, column, "Tiny Legs (Self)", TowkaVisualsConfig.INSTANCE.tinyLegsSelf, () -> TowkaVisualsConfig.INSTANCE.tinyLegsSelf = !TowkaVisualsConfig.INSTANCE.tinyLegsSelf);
                toggle(x, y + 24, column, "Spin (Self)", TowkaVisualsConfig.INSTANCE.spinSelf, () -> TowkaVisualsConfig.INSTANCE.spinSelf = !TowkaVisualsConfig.INSTANCE.spinSelf);
                toggle(x + column + 8, y + 24, column, "Bounce (Self)", TowkaVisualsConfig.INSTANCE.bounceSelf, () -> TowkaVisualsConfig.INSTANCE.bounceSelf = !TowkaVisualsConfig.INSTANCE.bounceSelf);
                toggle(x, y + 48, column, "Meme Mode", TowkaVisualsConfig.INSTANCE.memeMode, () -> TowkaVisualsConfig.INSTANCE.memeMode = !TowkaVisualsConfig.INSTANCE.memeMode);
                action(x + column + 8, y + 48, column, "Player Visuals", TowkaVisualsClient::openPlayerEditor);
            }
            case EXTRA -> {
                action(x, y, w, "Open Player Visuals Editor", TowkaVisualsClient::openPlayerEditor);
                action(x, y + 24, w, "Quick HUD Toggle Key: H", () -> {});
                action(x, y + 48, w, "Main Menu Key: J", () -> {});
            }
        }
    }

    private void toggle(int x, int y, int width, String name, boolean state, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (state ? "ON" : "OFF")), b -> {
            action.run();
            TowkaVisualsConfig.save();
            clearAndInit();
        }).dimensions(x, y, width, 20).build());
    }

    private void action(int x, int y, int width, String name, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(name), b -> {
            action.run();
            TowkaVisualsConfig.save();
            clearAndInit();
        }).dimensions(x, y, width, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(left - 4, top - 4, left + panelWidth + 4, top + panelHeight + 4, 0xD90A1322);
        context.fill(left - 4, top - 4, left + panelWidth + 4, top, 0xFF2AC6F8);
        context.fill(left + 128, top + 24, left + 129, top + panelHeight - 8, 0x4FA3DFFF);
        context.drawCenteredTextWithShadow(textRenderer, "TowkaVisuals", width / 2, top + 8, 0xFF79E9FF);
        context.drawTextWithShadow(textRenderer, "Blue Neon Visuals • Fabric 1.21.4", left + 10, top + 18, 0xFFD1EEFF);
        context.drawTextWithShadow(textRenderer, category.description, left + 138, top + 18, 0xFFA5C9DE);
        context.drawTextWithShadow(textRenderer, "Funny modules for videos included", left + 138, top + panelHeight - 18, 0xFF96C7E3);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private enum Category {
        COMBAT("Combat", "Swing, player visuals and action modules"),
        WORLD("World", "Ambient world features and particles"),
        HUD("HUD", "Top overlay with FPS, ping and info"),
        FUN("Fun", "Funny character effects for clips"),
        EXTRA("Extra", "Utility buttons and quick reminders");

        final String label;
        final String description;

        Category(String label, String description) {
            this.label = label;
            this.description = description;
        }
    }
}
