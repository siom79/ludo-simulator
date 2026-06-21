package de.siom79.ludo.simulation;

import de.siom79.ludo.rules.RuleSet;
import de.siom79.ludo.strategy.StrategySpec;

import java.util.List;

public record SimulationConfig(List<StrategySpec> strategies, RuleSet ruleSet, int runs, Long seed) {
}
