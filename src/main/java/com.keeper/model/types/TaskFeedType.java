package com.keeper.model.types;

/*
 * Created by @GoodforGod on 30.04.2017.
 */

/**
 * Default Comment
 */
public enum TaskFeedType {
    ALL(0),
    MY(10),
    NEW(20),
    LOCAL(30),
    HOT(40);

    private final int value;

    TaskFeedType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
