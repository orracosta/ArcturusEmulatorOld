package com.habboproject.server.utilities;

public class Counter {
    private int currentCount;

    public Counter(int start) {
        this.currentCount = start;
    }

    public Counter() {
        this.currentCount = 0;
    }

    public void increase() {
        this.increase(1);
    }

    public void increase(int delta) {
        this.currentCount += delta;
    }

    public int increaseAndGet() {
        this.increase(1);

        return this.currentCount;
    }

    public int increaseAndGet(int delta) {
        this.increase(delta);

        return this.currentCount;
    }

    public void decrease() {
        this.decrease(1);
    }

    public void decrease(int delta) {
        this.currentCount -= delta;
    }

    public void set(int newCounter) {
        this.currentCount = newCounter;
    }

    public int get() {
        return this.currentCount;
    }
}
