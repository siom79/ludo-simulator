package de.siom79.ludo;

import de.siom79.ludo.cli.CliArguments;
import de.siom79.ludo.cli.CliParseException;
import de.siom79.ludo.cli.CliParser;
import de.siom79.ludo.simulation.ReportFormatter;
import de.siom79.ludo.simulation.SimulationConfig;
import de.siom79.ludo.simulation.SimulationReport;
import de.siom79.ludo.simulation.Simulator;

import java.util.Arrays;

public final class Main {

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--help") || Arrays.asList(args).contains("-h")) {
            System.out.println(CliParser.USAGE);
            return;
        }

        CliArguments arguments;
        try {
            arguments = new CliParser().parse(args);
        } catch (CliParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        SimulationConfig config = new SimulationConfig(arguments.allStrategies(), arguments.ruleSet(),
                arguments.runs(), arguments.seed());
        SimulationReport report = new Simulator().run(config);
        System.out.println(ReportFormatter.format(report));
    }

    private Main() {
    }
}
