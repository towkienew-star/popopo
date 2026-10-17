package dev.towka.visuals;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public final class PlayerVisualStore {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("towkavisuals-players.properties");
    private static final Map<String, PlayerVisualProfile> PROFILES = new HashMap<>();
    private static final Map<String, UUID> NAME_TO_UUID = new HashMap<>();
    private static String currentServer = "singleplayer";

    private PlayerVisualStore() {}

    public static void syncOnlinePlayers(MinecraftClient client) {
        if (client.getNetworkHandler() == null) {
            NAME_TO_UUID.clear();
            currentServer = "singleplayer";
            return;
        }
        currentServer = client.getCurrentServerEntry() != null ? client.getCurrentServerEntry().address : "singleplayer";
        NAME_TO_UUID.clear();
        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            NAME_TO_UUID.put(entry.getProfile().getName(), entry.getProfile().getId());
        }
    }

    private static String key(UUID uuid) {
        return currentServer + "|" + uuid;
    }

    public static PlayerVisualProfile edit(UUID uuid) {
        return PROFILES.computeIfAbsent(key(uuid), s -> new PlayerVisualProfile());
    }

    public static void reset(UUID uuid) {
        PROFILES.remove(key(uuid));
        save();
    }

    public static void clearSelfOverride(UUID uuid) {
        PlayerVisualProfile profile = PROFILES.get(key(uuid));
        if (profile == null) return;
        if (profile.isDefault()) {
            PROFILES.remove(key(uuid));
            save();
        }
    }

    public static PlayerVisualProfile getForName(String name) {
        UUID uuid = NAME_TO_UUID.get(name);
        return uuid == null ? null : PROFILES.get(key(uuid));
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            props.load(in);
            for (String compound : props.stringPropertyNames()) {
                int idx = compound.lastIndexOf('#');
                if (idx <= 0) continue;
                String playerKey = compound.substring(0, idx);
                String property = compound.substring(idx + 1);
                PlayerVisualProfile profile = PROFILES.computeIfAbsent(playerKey, s -> new PlayerVisualProfile());
                String value = props.getProperty(compound);
                try {
                    switch (property) {
                        case "overall" -> profile.overall = Float.parseFloat(value);
                        case "width" -> profile.width = Float.parseFloat(value);
                        case "height" -> profile.height = Float.parseFloat(value);
                        case "head" -> profile.head = Float.parseFloat(value);
                        case "body" -> profile.body = Float.parseFloat(value);
                        case "arms" -> profile.arms = Float.parseFloat(value);
                        case "legs" -> profile.legs = Float.parseFloat(value);
                        case "spin" -> profile.spin = Boolean.parseBoolean(value);
                        case "bounce" -> profile.bounce = Boolean.parseBoolean(value);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("[TowkaVisuals] Cannot read player config: " + e.getMessage());
        }
    }

    public static void save() {
        Properties props = new Properties();
        PROFILES.forEach((playerKey, profile) -> {
            props.setProperty(playerKey + "#overall", Float.toString(profile.overall));
            props.setProperty(playerKey + "#width", Float.toString(profile.width));
            props.setProperty(playerKey + "#height", Float.toString(profile.height));
            props.setProperty(playerKey + "#head", Float.toString(profile.head));
            props.setProperty(playerKey + "#body", Float.toString(profile.body));
            props.setProperty(playerKey + "#arms", Float.toString(profile.arms));
            props.setProperty(playerKey + "#legs", Float.toString(profile.legs));
            props.setProperty(playerKey + "#spin", Boolean.toString(profile.spin));
            props.setProperty(playerKey + "#bounce", Boolean.toString(profile.bounce));
        });
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) {
                props.store(out, "TowkaVisuals player visuals");
            }
        } catch (IOException e) {
            System.err.println("[TowkaVisuals] Cannot save player config: " + e.getMessage());
        }
    }
}
