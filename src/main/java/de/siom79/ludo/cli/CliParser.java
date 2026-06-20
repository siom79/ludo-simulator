package de.siom79.ludo.cli;

import de.siom79.ludo.rules.RuleSet;
import de.siom79.ludo.strategy.StrategySpec;
import de.siom79.ludo.strategy.StrategyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class CliParser {

    public static final String USAGE = """
            java -jar ludo-simulator.jar [OPTIONEN]

            --opponents <1-3>                  Anzahl Gegner. Default: 3.
            --strategy-self <NAME[:FALLBACK]>  Default: RANDOM.
            --strategy-opponents <NAME[:FALLBACK][,NAME[:FALLBACK]...]>
                                                Ein Eintrag (fuer alle Gegner) ODER genau so viele
                                                kommagetrennte Eintraege wie --opponents. Default: RANDOM.
                                                Gueltige Namen: RANDOM, FURTHEST_FIRST, NEAREST_FIRST,
                                                CAPTURE_PRIORITY, DEFENSIVE, EXIT_PRIORITY

            --rule-exit-only-on-six <bool>      Default: true
            --rule-mandatory-capture <bool>     Default: false
            --rule-extra-turn-on-six <bool>     Default: true
            --rule-three-sixes-forfeit <bool>   Default: true
            --rule-no-skipping-in-goal <bool>   Default: true

            --runs <N>                         Anzahl simulierter Spiele. Default: 1000.
            --seed <long>                      Optionaler Basis-Seed fuer Reproduzierbarkeit.
            --help, -h                         Hilfe anzeigen.
            """;

    public CliArguments parse(String[] args) {
        Map<String, String> options = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) {
                throw error("Unbekanntes Argument: " + arg);
            }
            String key = arg.substring(2);
            if (i + 1 >= args.length) {
                throw error("Fehlender Wert fuer --" + key);
            }
            options.put(key, args[++i]);
        }

        int opponents = parseIntOption(options, "opponents", 3);
        if (opponents < 1 || opponents > 3) {
            throw error("--opponents muss zwischen 1 und 3 liegen: " + opponents);
        }

        StrategySpec selfStrategy = parseStrategyOption(options.get("strategy-self"),
                StrategySpec.of(StrategyType.RANDOM));
        List<StrategySpec> opponentStrategies = parseOpponentStrategies(options.get("strategy-opponents"),
                opponents);

        RuleSet ruleSet = RuleSet.builder()
                .exitOnlyOnSix(parseBoolOption(options, "rule-exit-only-on-six", true))
                .mandatoryCapture(parseBoolOption(options, "rule-mandatory-capture", false))
                .extraTurnOnSix(parseBoolOption(options, "rule-extra-turn-on-six", true))
                .threeSixesForfeitsTurn(parseBoolOption(options, "rule-three-sixes-forfeit", true))
                .noSkippingInGoal(parseBoolOption(options, "rule-no-skipping-in-goal", true))
                .build();

        int runs = parseIntOption(options, "runs", 1000);
        if (runs < 1) {
            throw error("--runs muss >= 1 sein: " + runs);
        }

        Long seed = options.containsKey("seed") ? parseLong(options.get("seed"), "seed") : null;

        return new CliArguments(opponents, selfStrategy, opponentStrategies, ruleSet, runs, seed);
    }

    private List<StrategySpec> parseOpponentStrategies(String raw, int opponents) {
        StrategySpec defaultSpec = StrategySpec.of(StrategyType.RANDOM);
        List<StrategySpec> parsed;
        if (raw == null) {
            parsed = List.of(defaultSpec);
        } else {
            parsed = new ArrayList<>();
            for (String part : raw.split(",")) {
                parsed.add(parseStrategyOption(part, defaultSpec));
            }
        }
        if (parsed.size() == 1) {
            return Stream.generate(() -> parsed.get(0)).limit(opponents).toList();
        }
        if (parsed.size() != opponents) {
            throw error("--strategy-opponents muss 1 oder " + opponents
                    + " kommagetrennte Eintraege haben, war: " + parsed.size());
        }
        return parsed;
    }

    private StrategySpec parseStrategyOption(String raw, StrategySpec defaultValue) {
        if (raw == null) {
            return defaultValue;
        }
        try {
            return StrategySpec.parse(raw);
        } catch (IllegalArgumentException e) {
            throw error(e.getMessage());
        }
    }

    private int parseIntOption(Map<String, String> options, String key, int defaultValue) {
        if (!options.containsKey(key)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(options.get(key));
        } catch (NumberFormatException e) {
            throw error("Ungueltiger Zahlenwert fuer --" + key + ": " + options.get(key));
        }
    }

    private long parseLong(String value, String key) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw error("Ungueltiger Zahlenwert fuer --" + key + ": " + value);
        }
    }

    private boolean parseBoolOption(Map<String, String> options, String key, boolean defaultValue) {
        if (!options.containsKey(key)) {
            return defaultValue;
        }
        String value = options.get(key);
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw error("--" + key + " akzeptiert nur 'true' oder 'false': " + value);
    }

    private CliParseException error(String message) {
        return new CliParseException(message + System.lineSeparator() + System.lineSeparator() + USAGE);
    }
}
