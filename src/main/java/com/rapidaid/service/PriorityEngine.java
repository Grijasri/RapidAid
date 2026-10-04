package com.rapidaid.service;

public interface PriorityEngine {

    class PriorityResult {
        private final int score;
        private final String label;

        public PriorityResult(int score, String label) {
            this.score = score;
            this.label = label;
        }

        public int getScore() { return score; }
        public String getLabel() { return label; }
    }

    PriorityResult evaluatePriority(String emergencyType, String description);
}
