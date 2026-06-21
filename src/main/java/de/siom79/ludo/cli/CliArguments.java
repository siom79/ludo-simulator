package de.siom79.ludo.cli;

import de.siom79.ludo.rules.RuleSet;
import de.siom79.ludo.strategy.StrategySpec;

import java.util.ArrayList;
import java.util.List;

public record CliArguments(
        int opponents,
        StrategySpec selfStrategy,
        List<StrategySpec> opponentStrategies,
        RuleSet ruleSet,
        int runs,
        Long seed) {

    public List<StrategySpec> allStrategies() {
        List<StrategySpec> all = new ArrayList<>();
        all.add(selfStrategy);
        all.addAll(opponentStrategies);
        return all;
    }
}
