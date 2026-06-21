package de.siom79.ludo.strategy;

public record StrategySpec(StrategyType type, StrategyType fallback) {

    public static final StrategyType DEFAULT_FALLBACK = StrategyType.FURTHEST_FIRST;

    public static StrategySpec of(StrategyType type) {
        return new StrategySpec(type, DEFAULT_FALLBACK);
    }

    public static StrategySpec parse(String raw) {
        String[] parts = raw.split(":", 2);
        StrategyType type = StrategyType.parse(parts[0]);
        StrategyType fallback = parts.length == 2 ? StrategyType.parse(parts[1]) : DEFAULT_FALLBACK;
        return new StrategySpec(type, fallback);
    }

    private static boolean usesFallback(StrategyType type) {
        return type == StrategyType.CAPTURE_PRIORITY
                || type == StrategyType.DEFENSIVE
                || type == StrategyType.EXIT_PRIORITY;
    }

    public String displayName() {
        return usesFallback(type) ? type.name() + ":" + fallback.name() : type.name();
    }
}
