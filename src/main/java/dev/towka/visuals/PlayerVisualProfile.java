package dev.towka.visuals;

public final class PlayerVisualProfile {
    public float overall = 1f;
    public float width = 1f;
    public float height = 1f;
    public float head = 1f;
    public float body = 1f;
    public float arms = 1f;
    public float legs = 1f;
    public boolean spin = false;
    public boolean bounce = false;

    public float get(int i) {
        return switch (i) {
            case 0 -> overall;
            case 1 -> width;
            case 2 -> height;
            case 3 -> head;
            case 4 -> body;
            case 5 -> arms;
            case 6 -> legs;
            default -> 1f;
        };
    }

    public void set(int i, float value) {
        float min = (i <= 2) ? 0.35f : 0.50f;
        float max = (i == 0) ? 3.5f : 2.5f;
        value = Math.max(min, Math.min(max, value));
        switch (i) {
            case 0 -> overall = value;
            case 1 -> width = value;
            case 2 -> height = value;
            case 3 -> head = value;
            case 4 -> body = value;
            case 5 -> arms = value;
            case 6 -> legs = value;
            default -> { }
        }
    }

    public boolean isDefault() {
        return overall == 1f && width == 1f && height == 1f && head == 1f && body == 1f && arms == 1f && legs == 1f && !spin && !bounce;
    }
}
