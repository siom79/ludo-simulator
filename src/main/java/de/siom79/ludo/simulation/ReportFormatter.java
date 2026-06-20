package de.siom79.ludo.simulation;

import de.siom79.ludo.rules.RuleSet;

import java.util.List;
import java.util.Locale;

public final class ReportFormatter {

    private ReportFormatter() {
    }

    public static String format(SimulationReport report) {
        int totalRuns = report.config().runs();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.ROOT, "Ludo Simulator - Ergebnis (%d Spiele)%n", totalRuns));
        sb.append("RuleSet: ").append(formatRuleSet(report.config().ruleSet())).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(String.format(Locale.ROOT, "%-12s %-22s %-8s %s%n", "Spieler", "Strategie", "Siege", "Siegquote"));
        for (PlayerStats stats : report.playerStats()) {
            sb.append(String.format(Locale.ROOT, "%-12s %-22s %-8d %6.2f%%%n",
                    stats.name(), stats.strategy().displayName(), stats.wins(), stats.winRate(totalRuns) * 100));
        }
        sb.append(System.lineSeparator());
        for (PlayerStats stats : report.playerStats()) {
            if (stats.wins() == 0) {
                continue;
            }
            sb.append(formatSampleLine(stats.name(), "Runden", stats.roundsToWin()));
            sb.append(formatSampleLine(stats.name(), "Wuerfe", stats.turnsToWin()));
        }
        return sb.toString();
    }

    private static String formatRuleSet(RuleSet ruleSet) {
        return "exitOnlyOnSix=" + ruleSet.exitOnlyOnSix()
                + ", mandatoryCapture=" + ruleSet.mandatoryCapture()
                + ", extraTurnOnSix=" + ruleSet.extraTurnOnSix()
                + ", threeSixesForfeitsTurn=" + ruleSet.threeSixesForfeitsTurn()
                + ", noSkippingInGoal=" + ruleSet.noSkippingInGoal();
    }

    private static String formatSampleLine(String name, String label, List<Integer> samples) {
        List<Integer> sorted = samples.stream().sorted().toList();
        int n = sorted.size();
        double avg = sorted.stream().mapToInt(Integer::intValue).average().orElse(0);
        double median = median(sorted);
        int min = sorted.get(0);
        int max = sorted.get(n - 1);
        return String.format(Locale.ROOT, "%s - %s bis Sieg (n=%d): avg=%.1f median=%.1f min=%d max=%d%n",
                name, label, n, avg, median, min, max);
    }

    private static double median(List<Integer> sorted) {
        int size = sorted.size();
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        }
        return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
    }
}
