# Ludo Simulator — Specification

## Goal

A command-line simulator for the board game Ludo ("Mensch ärgere Dich nicht")
that plays a large number of games between configurable AI strategies and
reports win rates and game-length statistics. The simulator is meant to help
compare how different movement strategies and rule variants affect a
player's chances of winning.

## Scope

- 2 to 4 players (1 "self" player plus 1–3 opponents), each with 4 tokens.
- A single shared 40-cell main track plus a private 4-cell goal lane per
  player (no separate home-row movement before exiting).
- A configurable rule set covering the most common Ludo rule variations.
- A batch simulation mode (no interactive/human play) driven entirely by the
  CLI, producing an aggregate text report.

## Board Model

- `Token`: position `0` = at home (not yet in play), `1..40` = relative
  position on the player's own main track, `41..44` = goal lane slots.
- `Player`: index (0-based seat order), display name, 4 tokens, an assigned
  `MoveStrategy`.
- `Board`: holds all players; converts a player-relative main-track position
  (`1..40`) into a global cell index via a fixed per-seat offset
  (`{0, 10, 20, 30}`, i.e. an even 40/4 split), so captures between players
  can be detected on a shared coordinate space. Looks up a player's own
  token at a given relative position, and any opponent token occupying a
  given global cell.

## Rules (`RuleSet`)

All toggles default to common physical-board Ludo rules and can be
overridden individually via CLI flags:

| Rule | Default | Effect |
|---|---|---|
| `exitOnlyOnSix` | `true` | A token may only leave home on a roll of 6 (if `false`, any roll value can be used to exit, moving that many steps onto the track). |
| `mandatoryCapture` | `false` | If at least one legal move captures an opponent token, only capturing moves are legal that turn. |
| `extraTurnOnSix` | `true` | Rolling a 6 grants the same player another roll/turn. |
| `threeSixesForfeitsTurn` | `true` | Three consecutive 6s by the same player forfeit that turn entirely (no move is made, turn passes). |
| `noSkippingInGoal` | `true` | A token cannot jump over another of the player's own tokens already placed in the goal lane. |

Additional fixed rules (not configurable):
- A player may never move onto a cell already occupied by one of their own
  tokens (own tokens block each other, both on the main track and in goal).
- Landing on a global cell occupied by an opponent's token captures it,
  sending it back home (position `0`).
- A player wins when all 4 tokens reach the goal lane (`41..44`).
- If a roll produces no legal move for any token, the turn passes (subject
  to the extra-turn-on-six rule above).

## Game Engine

- `LegalMoveCalculator`: for a given player and dice roll, computes the list
  of legal moves across all 4 tokens (exiting home, advancing on the main
  track, entering/advancing within the goal lane), applying the rules above
  (own-token blocking, goal skipping, mandatory capture filtering).
- `CaptureResolver`: applies a chosen move — moves the token to its target
  position and, if it's a capturing move, sends the captured opponent token
  home.
- `GameEngine`: drives a single game turn-by-turn: rolls the dice, computes
  legal moves, asks the current player's strategy to pick one, applies it,
  checks for a win, and handles extra turns on six / three-sixes forfeiture.
  Returns a `GameResult` (winner index, total rounds, total turns) once a
  player completes all 4 tokens.

## Strategies (`MoveStrategy`)

Pluggable move-selection policies, chosen per player via the CLI:

- `RANDOM` — picks uniformly at random among legal moves.
- `FURTHEST_FIRST` — always advances the token that is furthest along its
  own track (highest relative position).
- `NEAREST_FIRST` — always advances the token that is least far along
  (lowest relative position), prioritizing getting tokens out and moving
  evenly.
- `CAPTURE_PRIORITY` — prefers any move that captures an opponent token;
  if none is available, delegates to a fallback strategy.
- `DEFENSIVE` — prefers the move that lands on the cell with the lowest
  computed capture risk (counts opponent tokens within reach of a single
  die roll of the destination cell); ties are broken by a fallback
  strategy.
- `EXIT_PRIORITY` — on a roll of 6, prefers bringing a new token out of
  home; otherwise (or if no token can exit) delegates to a fallback
  strategy.

`CAPTURE_PRIORITY`, `DEFENSIVE`, and `EXIT_PRIORITY` are composable with any
other strategy as a fallback/tie-breaker (`StrategySpec`, syntax
`NAME[:FALLBACK]`, default fallback `FURTHEST_FIRST`).

## Simulation & Reporting

- `Simulator` runs the configured number of independent games. Each game
  gets a fresh board and a per-game `Random` (seeded deterministically from
  a base seed if provided, for reproducibility), and players are
  reconstructed for every run with their assigned strategy.
- `PlayerStats` aggregates, per player: number of wins, win rate, and the
  full sample of rounds-to-win / dice-rolls-to-win for games that player
  won.
- `ReportFormatter` renders a plain-text report: the active rule set, a
  per-player table (name, strategy, wins, win rate), and per-player
  rounds-to-win / rolls-to-win statistics (average, median, min, max).

## CLI

Entry point: `de.siom79.ludo.Main`, packaged as an executable jar.

```
java -jar ludo-simulator.jar [OPTIONS]

--opponents <1-3>                   Number of opponents. Default: 3.
--strategy-self <NAME[:FALLBACK]>   Default: RANDOM.
--strategy-opponents <NAME[:FALLBACK][,NAME[:FALLBACK]...]>
                                     One entry (applied to all opponents) OR exactly as
                                     many comma-separated entries as --opponents. Default: RANDOM.
                                     Valid names: RANDOM, FURTHEST_FIRST, NEAREST_FIRST,
                                     CAPTURE_PRIORITY, DEFENSIVE, EXIT_PRIORITY

--rule-exit-only-on-six <bool>      Default: true
--rule-mandatory-capture <bool>     Default: false
--rule-extra-turn-on-six <bool>     Default: true
--rule-three-sixes-forfeit <bool>   Default: true
--rule-no-skipping-in-goal <bool>   Default: true

--runs <N>                          Number of simulated games. Default: 1000.
--seed <long>                       Optional base seed for reproducibility.
--help, -h                          Show help.
```

Invalid arguments (unknown flags, out-of-range values, malformed
strategy/rule/number syntax) print a usage message to stderr and exit with
a non-zero status.

## Out of Scope (for this iteration)

- Interactive/human-controlled play.
- More than 4 players or non-standard board layouts.
- Graphical/visual board rendering.
- Persisting simulation results to a file or database (the report is
  printed to stdout only).
