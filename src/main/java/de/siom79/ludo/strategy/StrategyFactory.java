package de.siom79.ludo.strategy;

import java.util.Random;

public final class StrategyFactory {

    private StrategyFactory() {
    }

    public static MoveStrategy create(StrategySpec spec, Random random) {
        return createForType(spec.type(), spec.fallback(), random);
    }

    private static MoveStrategy createForType(StrategyType type, StrategyType fallbackType, Random random) {
        return switch (type) {
            case RANDOM -> new RandomStrategy(random);
            case FURTHEST_FIRST -> new FurthestFirstStrategy();
            case NEAREST_FIRST -> new NearestFirstStrategy();
            case CAPTURE_PRIORITY ->
                    new CapturePriorityStrategy(createForType(fallbackType, StrategySpec.DEFAULT_FALLBACK, random));
            case DEFENSIVE ->
                    new DefensiveStrategy(createForType(fallbackType, StrategySpec.DEFAULT_FALLBACK, random));
            case EXIT_PRIORITY ->
                    new ExitPriorityStrategy(createForType(fallbackType, StrategySpec.DEFAULT_FALLBACK, random));
        };
    }
}
