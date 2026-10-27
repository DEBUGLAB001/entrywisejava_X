package com.entrywise.model;

public enum PriorityTier {
    VIP(1),
    PRIORITY(2),
    STANDARD(3);

    private final int level;

    PriorityTier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
