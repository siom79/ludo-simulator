package de.siom79.ludo.model;

public final class Token {

    public static final int HOME = 0;
    public static final int MAIN_TRACK_LENGTH = 40;
    public static final int GOAL_START = 41;
    public static final int GOAL_END = 44;

    private int position = HOME;

    public int position() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isHome() {
        return position == HOME;
    }

    public boolean isOnMainTrack() {
        return position >= 1 && position <= MAIN_TRACK_LENGTH;
    }

    public boolean isInGoal() {
        return position >= GOAL_START && position <= GOAL_END;
    }
}
