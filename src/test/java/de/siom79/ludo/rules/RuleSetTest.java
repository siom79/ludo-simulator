package de.siom79.ludo.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleSetTest {

    @Test
    void standardHasExpectedDefaults() {
        RuleSet ruleSet = RuleSet.standard();
        assertTrue(ruleSet.exitOnlyOnSix());
        assertFalse(ruleSet.mandatoryCapture());
        assertTrue(ruleSet.extraTurnOnSix());
        assertTrue(ruleSet.threeSixesForfeitsTurn());
        assertTrue(ruleSet.noSkippingInGoal());
    }

    @Test
    void builderOverridesOnlyExplicitlySetFields() {
        RuleSet ruleSet = RuleSet.builder().mandatoryCapture(true).build();
        assertTrue(ruleSet.mandatoryCapture());
        assertTrue(ruleSet.exitOnlyOnSix());
        assertTrue(ruleSet.extraTurnOnSix());
        assertTrue(ruleSet.threeSixesForfeitsTurn());
        assertTrue(ruleSet.noSkippingInGoal());
    }
}
