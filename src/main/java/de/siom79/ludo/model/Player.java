package de.siom79.ludo.model;

import de.siom79.ludo.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

public final class Player {

    public static final int TOKENS_PER_PLAYER = 4;

    private final int index;
    private final String name;
    private final MoveStrategy strategy;
    private final List<Token> tokens;

    public Player(int index, String name, MoveStrategy strategy) {
        this.index = index;
        this.name = name;
        this.strategy = strategy;
        this.tokens = new ArrayList<>(TOKENS_PER_PLAYER);
        for (int i = 0; i < TOKENS_PER_PLAYER; i++) {
            tokens.add(new Token());
        }
    }

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    public MoveStrategy strategy() {
        return strategy;
    }

    public List<Token> tokens() {
        return tokens;
    }

    public boolean hasWon() {
        return tokens.stream().allMatch(Token::isInGoal);
    }
}
