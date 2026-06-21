package de.siom79.ludo.cli;

import de.siom79.ludo.strategy.StrategyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliParserTest {

    private final CliParser parser = new CliParser();

    @Test
    void noArgumentsYieldDocumentedDefaults() {
        CliArguments arguments = parser.parse(new String[0]);

        assertEquals(3, arguments.opponents());
        assertEquals(StrategyType.RANDOM, arguments.selfStrategy().type());
        assertEquals(3, arguments.opponentStrategies().size());
        arguments.opponentStrategies().forEach(spec -> assertEquals(StrategyType.RANDOM, spec.type()));
        assertTrue(arguments.ruleSet().exitOnlyOnSix());
        assertEquals(1000, arguments.runs());
        assertNull(arguments.seed());
    }

    @Test
    void explicitArgumentsOverrideDefaults() {
        CliArguments arguments = parser.parse(new String[] {
                "--opponents", "2",
                "--strategy-self", "FURTHEST_FIRST",
                "--strategy-opponents", "CAPTURE_PRIORITY:DEFENSIVE,RANDOM",
                "--rule-mandatory-capture", "true",
                "--runs", "50",
                "--seed", "7"
        });

        assertEquals(2, arguments.opponents());
        assertEquals(StrategyType.FURTHEST_FIRST, arguments.selfStrategy().type());
        assertEquals(2, arguments.opponentStrategies().size());
        assertEquals(StrategyType.CAPTURE_PRIORITY, arguments.opponentStrategies().get(0).type());
        assertEquals(StrategyType.DEFENSIVE, arguments.opponentStrategies().get(0).fallback());
        assertEquals(StrategyType.RANDOM, arguments.opponentStrategies().get(1).type());
        assertTrue(arguments.ruleSet().mandatoryCapture());
        assertEquals(50, arguments.runs());
        assertEquals(7L, arguments.seed());
    }

    @Test
    void singleOpponentStrategyIsBroadcastToAllOpponents() {
        CliArguments arguments = parser.parse(new String[] {"--opponents", "3", "--strategy-opponents", "NEAREST_FIRST"});

        assertEquals(3, arguments.opponentStrategies().size());
        arguments.opponentStrategies().forEach(spec -> assertEquals(StrategyType.NEAREST_FIRST, spec.type()));
    }

    @Test
    void fallbackSyntaxIsParsedForSelfStrategy() {
        CliArguments arguments = parser.parse(new String[] {"--strategy-self", "EXIT_PRIORITY:NEAREST_FIRST"});

        assertEquals(StrategyType.EXIT_PRIORITY, arguments.selfStrategy().type());
        assertEquals(StrategyType.NEAREST_FIRST, arguments.selfStrategy().fallback());
    }

    @Test
    void opponentsCountOutOfRangeThrows() {
        assertThrows(CliParseException.class, () -> parser.parse(new String[] {"--opponents", "5"}));
    }

    @Test
    void unknownStrategyNameThrows() {
        assertThrows(CliParseException.class, () -> parser.parse(new String[] {"--strategy-self", "NOT_A_STRATEGY"}));
    }

    @Test
    void mismatchedOpponentStrategyCountThrows() {
        assertThrows(CliParseException.class, () -> parser.parse(new String[] {
                "--opponents", "2", "--strategy-opponents", "RANDOM,RANDOM,RANDOM"
        }));
    }

    @Test
    void invalidBooleanValueThrows() {
        assertThrows(CliParseException.class,
                () -> parser.parse(new String[] {"--rule-mandatory-capture", "maybe"}));
    }

    @Test
    void missingValueForOptionThrows() {
        assertThrows(CliParseException.class, () -> parser.parse(new String[] {"--opponents"}));
    }

    @Test
    void argumentNotStartingWithDoubleDashThrows() {
        assertThrows(CliParseException.class, () -> parser.parse(new String[] {"opponents"}));
    }

    @Test
    void runsBelowOneThrows() {
        assertThrows(CliParseException.class, () -> parser.parse(new String[] {"--runs", "0"}));
    }
}
