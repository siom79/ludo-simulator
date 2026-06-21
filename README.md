# ludo-simulator

A command-line simulator for the board game Ludo ("Mensch ärgere Dich
nicht"). It plays a large number of automated games between configurable
AI strategies and reports win rates and game-length statistics, so you can
compare how different movement strategies and rule variants affect a
player's chances of winning.

For a detailed description of the board model, rules, strategies, and
engine, see [spec.md](spec.md).

## Building

Requires JDK 21 and Maven.

```
mvn package
```

This compiles the project, runs the test suite, and produces a runnable
jar at `target/ludo-simulator-0.1.0-SNAPSHOT.jar`.

## Running

```
java -jar target/ludo-simulator-0.1.0-SNAPSHOT.jar [OPTIONS]
```

Run with no options to simulate 1000 games of 1 self player against 3
random-strategy opponents under the standard rule set. Use `--help` to
print the full list of options:

```
java -jar target/ludo-simulator-0.1.0-SNAPSHOT.jar --help
```

### Example

Run 5000 games where you play `CAPTURE_PRIORITY` (falling back to
`FURTHEST_FIRST`) against two `DEFENSIVE` opponents and one `RANDOM`
opponent, with a fixed seed for reproducible results:

```
java -jar target/ludo-simulator-0.1.0-SNAPSHOT.jar \
  --opponents 3 \
  --strategy-self CAPTURE_PRIORITY:FURTHEST_FIRST \
  --strategy-opponents DEFENSIVE,DEFENSIVE,RANDOM \
  --runs 5000 \
  --seed 42
```

### Options

| Option | Default | Description |
|---|---|---|
| `--opponents <1-3>` | `3` | Number of opponents. |
| `--strategy-self <NAME[:FALLBACK]>` | `RANDOM` | Strategy for the self player. |
| `--strategy-opponents <NAME[:FALLBACK][,...]>` | `RANDOM` | One entry (applied to all opponents) or exactly as many comma-separated entries as `--opponents`. |
| `--rule-exit-only-on-six <bool>` | `true` | A token may only leave home on a roll of 6. |
| `--rule-mandatory-capture <bool>` | `false` | Capturing moves are mandatory when available. |
| `--rule-extra-turn-on-six <bool>` | `true` | Rolling a 6 grants an extra turn. |
| `--rule-three-sixes-forfeit <bool>` | `true` | Three consecutive 6s forfeit the turn. |
| `--rule-no-skipping-in-goal <bool>` | `true` | A token cannot skip over the player's own tokens in the goal lane. |
| `--runs <N>` | `1000` | Number of simulated games. |
| `--seed <long>` | _(random)_ | Optional base seed for reproducible results. |
| `--help`, `-h` | | Show usage. |

Valid strategy names: `RANDOM`, `FURTHEST_FIRST`, `NEAREST_FIRST`,
`CAPTURE_PRIORITY`, `DEFENSIVE`, `EXIT_PRIORITY`.

## Running without a local build

The repository includes a GitHub Action ("Run Ludo Simulator") that builds
the jar and runs it with options entered through the GitHub Actions UI —
trigger it from the **Actions** tab via "Run workflow". The simulation
report is printed in the job log and in the run's summary.
