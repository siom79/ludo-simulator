package de.siom79.ludo.engine;

import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Token;

public final class CaptureResolver {

    private CaptureResolver() {
    }

    public static void apply(Move move) {
        move.token().setPosition(move.toPosition());
        if (move.isCapture()) {
            move.capturedToken().setPosition(Token.HOME);
        }
    }
}
