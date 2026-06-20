package de.siom79.ludo.model;

public record Move(Player player, Token token, int fromPosition, int toPosition, Token capturedToken) {

    public boolean isCapture() {
        return capturedToken != null;
    }
}
