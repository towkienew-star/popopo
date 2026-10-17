package dev.towka.visuals;

import dev.towka.visuals.gui.PlayerVisualsScreen;
import dev.towka.visuals.gui.TowkaVisualsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TowkaVisualsClient implements ClientModInitializer {
    private static KeyBinding openKey;
    private static KeyBinding hudKey;
    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        TowkaVisualsConfig.load();
        PlayerVisualStore.load();

        openKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.towkavisuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.towkavisuals")
        );
        hudKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.towkavisuals.hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.towkavisuals")
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            PlayerVisualStore.syncOnlinePlayers(client);
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new TowkaVisualsScreen());
                }
            }
            while (hudKey.wasPressed()) {
                TowkaVisualsConfig.INSTANCE.hudEnabled = !TowkaVisualsConfig.INSTANCE.hudEnabled;
                TowkaVisualsConfig.save();
            }
            handleSwingHand(client);
            handleWorldParticles(client);
            handleFunnySelfProfile(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> renderHud(context));
    }

    public static void openPlayerEditor() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new PlayerVisualsScreen());
    }

    private static void handleSwingHand(MinecraftClient client) {
        if (!TowkaVisualsConfig.INSTANCE.swingHand || client.player == null || client.world == null) return;
        int interval = Math.max(4, 18 - TowkaVisualsConfig.INSTANCE.swingSpeed * 3);
        if (tickCounter % interval == 0 && client.currentScreen == null) {
            client.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private static void handleWorldParticles(MinecraftClient client) {
        if (!TowkaVisualsConfig.INSTANCE.worldParticles || client.player == null || client.world == null) return;
        if (tickCounter % 3 != 0) return;
        double x = client.player.getX() + (client.world.random.nextDouble() - 0.5D) * 1.4D;
        double y = client.player.getBodyY(0.55D) + (client.world.random.nextDouble() - 0.5D) * 0.6D;
        double z = client.player.getZ() + (client.world.random.nextDouble() - 0.5D) * 1.4D;

        switch (TowkaVisualsConfig.INSTANCE.particleMode) {
            case 0 -> client.world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0D, 0.01D, 0.0D);
            case 1 -> client.world.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0D, 0.02D, 0.0D);
            case 2 -> client.world.addParticle(ParticleTypes.HEART, x, y, z, 0.0D, 0.03D, 0.0D);
            default -> client.world.addParticle(ParticleTypes.FIREWORK, x, y, z, 0.0D, 0.02D, 0.0D);
        }
    }

    private static void handleFunnySelfProfile(MinecraftClient client) {
        if (client.player == null) return;
        if (!TowkaVisualsConfig.INSTANCE.bigHeadSelf && !TowkaVisualsConfig.INSTANCE.tinyLegsSelf
            && !TowkaVisualsConfig.INSTANCE.spinSelf && !TowkaVisualsConfig.INSTANCE.bounceSelf && !TowkaVisualsConfig.INSTANCE.memeMode) {
            PlayerVisualStore.clearSelfOverride(client.player.getUuid());
            return;
        }
        PlayerVisualProfile profile = PlayerVisualStore.edit(client.player.getUuid());
        if (TowkaVisualsConfig.INSTANCE.memeMode) {
            profile.overall = 1.15f;
            profile.head = 1.8f;
            profile.body = 0.85f;
            profile.arms = 1.25f;
            profile.legs = 0.75f;
            profile.spin = true;
            profile.bounce = true;
            return;
        }
        profile.head = TowkaVisualsConfig.INSTANCE.bigHeadSelf ? 1.65f : 1f;
        profile.legs = TowkaVisualsConfig.INSTANCE.tinyLegsSelf ? 0.70f : 1f;
        profile.spin = TowkaVisualsConfig.INSTANCE.spinSelf;
        profile.bounce = TowkaVisualsConfig.INSTANCE.bounceSelf;
        if (!TowkaVisualsConfig.INSTANCE.bigHeadSelf) profile.body = 1f;
        if (!TowkaVisualsConfig.INSTANCE.bigHeadSelf) profile.arms = 1f;
        profile.overall = 1f;
    }

    private static void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!TowkaVisualsConfig.INSTANCE.hudEnabled || client == null || client.getWindow() == null) return;
        int x = 8;
        int y = 8;
        int neon = 0xFF78E7FF;
        int white = 0xFFFFFFFF;
        if (TowkaVisualsConfig.INSTANCE.watermark) {
            context.fill(5, 5, 140, 24, 0x7F041A29);
            context.fill(5, 5, 140, 7, 0xFF2AC6F8);
            context.drawTextWithShadow(client.textRenderer, Text.literal("TowkaVisuals").formatted(Formatting.AQUA), x, y, neon);
            y += 18;
        }
        if (TowkaVisualsConfig.INSTANCE.showFps) {
            context.drawTextWithShadow(client.textRenderer, "FPS: " + client.getCurrentFps(), x, y, white);
            y += 10;
        }
        if (TowkaVisualsConfig.INSTANCE.showPing && client.player != null && client.getNetworkHandler() != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            int ping = entry != null ? entry.getLatency() : 0;
            context.drawTextWithShadow(client.textRenderer, "Ping: " + ping + " ms", x, y, white);
            y += 10;
        }
        if (TowkaVisualsConfig.INSTANCE.showCoords && client.player != null) {
            context.drawTextWithShadow(client.textRenderer,
                String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f", client.player.getX(), client.player.getY(), client.player.getZ()),
                x, y, white);
            y += 10;
        }
        if (TowkaVisualsConfig.INSTANCE.showClock) {
            context.drawTextWithShadow(client.textRenderer,
                "Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), x, y, white);
        }
    }
}
