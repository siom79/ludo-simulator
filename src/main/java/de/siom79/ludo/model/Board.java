package de.siom79.ludo.model;

import java.util.List;

public final class Board {

    public static final int MAIN_TRACK_LENGTH = Token.MAIN_TRACK_LENGTH;

    private static final int[] START_OFFSETS = {0, 10, 20, 30};

    private final List<Player> players;

    public Board(List<Player> players) {
        this.players = players;
    }

    public List<Player> players() {
        return players;
    }

    public int globalCellOf(int playerIndex, int relativePosition) {
        if (relativePosition < 1 || relativePosition > MAIN_TRACK_LENGTH) {
            throw new IllegalArgumentException(
                    "relativePosition must be in [1," + MAIN_TRACK_LENGTH + "]: " + relativePosition);
        }
        int offset = START_OFFSETS[playerIndex];
        return (offset + relativePosition - 1) % MAIN_TRACK_LENGTH;
    }

    public Token ownTokenAt(Player player, int position) {
        for (Token token : player.tokens()) {
            if (token.position() == position) {
                return token;
            }
        }
        return null;
    }

    public Token opponentTokenAtGlobalCell(int globalCell, int excludePlayerIndex) {
        for (Player player : players) {
            if (player.index() == excludePlayerIndex) {
                continue;
            }
            for (Token token : player.tokens()) {
                if (token.isOnMainTrack() && globalCellOf(player.index(), token.position()) == globalCell) {
                    return token;
                }
            }
        }
        return null;
    }
}
