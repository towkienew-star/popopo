package dev.towka.visuals.gui;

import dev.towka.visuals.PlayerVisualProfile;
import dev.towka.visuals.PlayerVisualStore;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class PlayerVisualsScreen extends Screen {
    private static final String[] LABELS = {
        "Overall Scale", "Width", "Height", "Head", "Body", "Arms", "Legs"
    };

    private UUID selected;
    private String selectedName = "";
    private int page = 0;
    private int rows = 5;
    private int left;
    private int top;
    private List<PlayerListEntry> players = List.of();

    public PlayerVisualsScreen() {
        super(Text.literal("TowkaVisuals Player Editor"));
    }

    @Override
    protected void init() {
        if (client == null || client.getNetworkHandler() == null) return;
        players = new ArrayList<>(client.getNetworkHandler().getPlayerList());
        players.sort(Comparator.comparing(p -> p.getProfile().getName(), String.CASE_INSENSITIVE_ORDER));
        rows = Math.max(3, Math.min(7, (height - 105) / 23));
        int panelWidth = Math.min(480, width - 20);
        left = (width - panelWidth) / 2;
        top = Math.max(8, (height - Math.min(height - 12, 265)) / 2);

        if (selected == null && client.player != null) {
            selected = client.player.getUuid();
        }
        boolean found = false;
        for (PlayerListEntry entry : players) {
            if (entry.getProfile().getId().equals(selected)) {
                selectedName = entry.getProfile().getName();
                found = true;
                break;
            }
        }
        if (!found) {
            selected = players.isEmpty() ? null : players.get(0).getProfile().getId();
            selectedName = players.isEmpty() ? "" : players.get(0).getProfile().getName();
        }

        page = Math.max(0, Math.min(page, Math.max(0, (players.size() - 1) / rows)));
        int listWidth = 150;
        int sx = left + listWidth + 18;
        int sliderWidth = Math.max(130, panelWidth - listWidth - 30);
        int listY = top + 40;

        for (int i = page * rows; i < Math.min(players.size(), (page + 1) * rows); i++) {
            PlayerListEntry entry = players.get(i);
            String name = entry.getProfile().getName();
            int y = listY + (i - page * rows) * 23;
            String label = entry.getProfile().getId().equals(selected) ? "§b▶ " + name : name;
            addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> {
                selected = entry.getProfile().getId();
                selectedName = name;
                clearAndInit();
            }).dimensions(left + 8, y, listWidth - 8, 20).build());
        }

        int navY = top + 42 + rows * 23;
        addDrawableChild(ButtonWidget.builder(Text.literal("◀"), b -> {
            if (page > 0) { page--; clearAndInit(); }
        }).dimensions(left + 8, navY, 40, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("▶"), b -> {
            if ((page + 1) * rows < players.size()) { page++; clearAndInit(); }
        }).dimensions(left + listWidth - 42, navY, 40, 20).build());

        if (selected != null) {
            PlayerVisualProfile profile = PlayerVisualStore.edit(selected);
            for (int i = 0; i < LABELS.length; i++) {
                int sliderY = top + 36 + i * 23;
                addDrawableChild(new ValueSlider(sx, sliderY, sliderWidth, 20, selected, i, profile.get(i)));
            }
            int by = top + 36 + LABELS.length * 23;
            addDrawableChild(ButtonWidget.builder(Text.literal("Spin: " + (profile.spin ? "ON" : "OFF")), b -> {
                profile.spin = !profile.spin;
                PlayerVisualStore.save();
                clearAndInit();
            }).dimensions(sx, by, sliderWidth / 2 - 3, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Bounce: " + (profile.bounce ? "ON" : "OFF")), b -> {
                profile.bounce = !profile.bounce;
                PlayerVisualStore.save();
                clearAndInit();
            }).dimensions(sx + sliderWidth / 2 + 3, by, sliderWidth / 2 - 3, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
                PlayerVisualStore.reset(selected);
                clearAndInit();
            }).dimensions(sx, height - top - 30, sliderWidth / 2 - 3, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(sx + sliderWidth / 2 + 3, height - top - 30, sliderWidth / 2 - 3, 20).build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int panelWidth = Math.min(480, width - 20);
        int panelHeight = height - top * 2;
        context.fill(left - 4, top - 4, left + panelWidth + 4, top + panelHeight + 4, 0xD90A1322);
        context.fill(left - 4, top - 4, left + panelWidth + 4, top, 0xFF2AC6F8);
        context.drawCenteredTextWithShadow(textRenderer, "TowkaVisuals Player Visuals", width / 2, top + 10, 0xFF78E7FF);
        context.drawTextWithShadow(textRenderer, "Online players: " + players.size(), left + 8, top + 24, 0xFFD4F0FF);
        context.drawTextWithShadow(textRenderer, selectedName.isEmpty() ? "Nobody selected" : selectedName, left + 168, top + 24, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Visual only • good for clips", left + 168, height - top - 14, 0xFF9CCBE0);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        PlayerVisualStore.save();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static final class ValueSlider extends SliderWidget {
        private final UUID player;
        private final int property;

        private ValueSlider(int x, int y, int width, int height, UUID player, int property, float value) {
            super(x, y, width, height, Text.empty(), normalized(value, property));
            this.player = player;
            this.property = property;
            updateMessage();
        }

        private static float min(int property) { return property <= 2 ? 0.35f : 0.50f; }
        private static float max(int property) { return property == 0 ? 3.5f : 2.5f; }
        private static double normalized(float value, int property) {
            return Math.max(0D, Math.min(1D, (value - min(property)) / (max(property) - min(property))));
        }
        private float actual() {
            return (float) (min(property) + value * (max(property) - min(property)));
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(LABELS[property] + ": " + String.format(Locale.ROOT, "%.2fx", actual())));
        }

        @Override
        protected void applyValue() {
            PlayerVisualStore.edit(player).set(property, actual());
        }
    }
}
