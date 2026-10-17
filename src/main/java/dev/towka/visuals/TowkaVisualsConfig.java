package dev.towka.visuals;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class TowkaVisualsConfig {
    public static final TowkaVisualsConfig INSTANCE = new TowkaVisualsConfig();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("towkavisuals.properties");

    public boolean hudEnabled = true;
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showCoords = false;
    public boolean showClock = false;
    public boolean watermark = true;

    public boolean swingHand = false;
    public int swingSpeed = 3;

    public boolean customWorld = true;
    public boolean worldParticles = false;
    public int particleMode = 0;
    public boolean fullBright = false;

    public boolean bigHeadSelf = false;
    public boolean tinyLegsSelf = false;
    public boolean spinSelf = false;
    public boolean bounceSelf = false;
    public boolean memeMode = false;

    private TowkaVisualsConfig() {}

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            props.load(in);
            INSTANCE.hudEnabled = bool(props, "hudEnabled", INSTANCE.hudEnabled);
            INSTANCE.showFps = bool(props, "showFps", INSTANCE.showFps);
            INSTANCE.showPing = bool(props, "showPing", INSTANCE.showPing);
            INSTANCE.showCoords = bool(props, "showCoords", INSTANCE.showCoords);
            INSTANCE.showClock = bool(props, "showClock", INSTANCE.showClock);
            INSTANCE.watermark = bool(props, "watermark", INSTANCE.watermark);
            INSTANCE.swingHand = bool(props, "swingHand", INSTANCE.swingHand);
            INSTANCE.swingSpeed = integer(props, "swingSpeed", INSTANCE.swingSpeed);
            INSTANCE.customWorld = bool(props, "customWorld", INSTANCE.customWorld);
            INSTANCE.worldParticles = bool(props, "worldParticles", INSTANCE.worldParticles);
            INSTANCE.particleMode = integer(props, "particleMode", INSTANCE.particleMode);
            INSTANCE.fullBright = bool(props, "fullBright", INSTANCE.fullBright);
            INSTANCE.bigHeadSelf = bool(props, "bigHeadSelf", INSTANCE.bigHeadSelf);
            INSTANCE.tinyLegsSelf = bool(props, "tinyLegsSelf", INSTANCE.tinyLegsSelf);
            INSTANCE.spinSelf = bool(props, "spinSelf", INSTANCE.spinSelf);
            INSTANCE.bounceSelf = bool(props, "bounceSelf", INSTANCE.bounceSelf);
            INSTANCE.memeMode = bool(props, "memeMode", INSTANCE.memeMode);
        } catch (IOException e) {
            System.err.println("[TowkaVisuals] Cannot read config: " + e.getMessage());
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("hudEnabled", Boolean.toString(INSTANCE.hudEnabled));
        props.setProperty("showFps", Boolean.toString(INSTANCE.showFps));
        props.setProperty("showPing", Boolean.toString(INSTANCE.showPing));
        props.setProperty("showCoords", Boolean.toString(INSTANCE.showCoords));
        props.setProperty("showClock", Boolean.toString(INSTANCE.showClock));
        props.setProperty("watermark", Boolean.toString(INSTANCE.watermark));
        props.setProperty("swingHand", Boolean.toString(INSTANCE.swingHand));
        props.setProperty("swingSpeed", Integer.toString(INSTANCE.swingSpeed));
        props.setProperty("customWorld", Boolean.toString(INSTANCE.customWorld));
        props.setProperty("worldParticles", Boolean.toString(INSTANCE.worldParticles));
        props.setProperty("particleMode", Integer.toString(INSTANCE.particleMode));
        props.setProperty("fullBright", Boolean.toString(INSTANCE.fullBright));
        props.setProperty("bigHeadSelf", Boolean.toString(INSTANCE.bigHeadSelf));
        props.setProperty("tinyLegsSelf", Boolean.toString(INSTANCE.tinyLegsSelf));
        props.setProperty("spinSelf", Boolean.toString(INSTANCE.spinSelf));
        props.setProperty("bounceSelf", Boolean.toString(INSTANCE.bounceSelf));
        props.setProperty("memeMode", Boolean.toString(INSTANCE.memeMode));
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) {
                props.store(out, "TowkaVisuals config");
            }
        } catch (IOException e) {
            System.err.println("[TowkaVisuals] Cannot save config: " + e.getMessage());
        }
    }

    public static String particleLabel() {
        return switch (INSTANCE.particleMode) {
            case 0 -> "End Rod";
            case 1 -> "Happy";
            case 2 -> "Hearts";
            default -> "Firework";
        };
    }

    public static void cycleParticleMode() {
        INSTANCE.particleMode = (INSTANCE.particleMode + 1) % 4;
        save();
    }

    public static void cycleSwingSpeed() {
        INSTANCE.swingSpeed++;
        if (INSTANCE.swingSpeed > 5) INSTANCE.swingSpeed = 1;
        save();
    }

    private static boolean bool(Properties props, String key, boolean fallback) {
        return Boolean.parseBoolean(props.getProperty(key, Boolean.toString(fallback)));
    }

    private static int integer(Properties props, String key, int fallback) {
        try {
            return Integer.parseInt(props.getProperty(key, Integer.toString(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
