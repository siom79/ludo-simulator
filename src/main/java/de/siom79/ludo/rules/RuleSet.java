package de.siom79.ludo.rules;

public record RuleSet(
        boolean exitOnlyOnSix,
        boolean mandatoryCapture,
        boolean extraTurnOnSix,
        boolean threeSixesForfeitsTurn,
        boolean noSkippingInGoal) {

    public static RuleSet standard() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean exitOnlyOnSix = true;
        private boolean mandatoryCapture = false;
        private boolean extraTurnOnSix = true;
        private boolean threeSixesForfeitsTurn = true;
        private boolean noSkippingInGoal = true;

        public Builder exitOnlyOnSix(boolean value) {
            this.exitOnlyOnSix = value;
            return this;
        }

        public Builder mandatoryCapture(boolean value) {
            this.mandatoryCapture = value;
            return this;
        }

        public Builder extraTurnOnSix(boolean value) {
            this.extraTurnOnSix = value;
            return this;
        }

        public Builder threeSixesForfeitsTurn(boolean value) {
            this.threeSixesForfeitsTurn = value;
            return this;
        }

        public Builder noSkippingInGoal(boolean value) {
            this.noSkippingInGoal = value;
            return this;
        }

        public RuleSet build() {
            return new RuleSet(exitOnlyOnSix, mandatoryCapture, extraTurnOnSix, threeSixesForfeitsTurn,
                    noSkippingInGoal);
        }
    }
}
