package de.siom79.ludo.strategy;

public enum StrategyType {
    RANDOM,
    FURTHEST_FIRST,
    NEAREST_FIRST,
    CAPTURE_PRIORITY,
    DEFENSIVE,
    EXIT_PRIORITY;

    public static StrategyType parse(String value) {
        try {
            return StrategyType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown strategy: " + value);
        }
    }
}
