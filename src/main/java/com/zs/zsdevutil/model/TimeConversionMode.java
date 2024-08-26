package com.zs.zsdevutil.model;

public enum TimeConversionMode {
    UNIX_TIME("Unix time (seconds since epoch)"),
    HUMAN_READABLE("Human-readable date");

    private final String displayName;

    TimeConversionMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
