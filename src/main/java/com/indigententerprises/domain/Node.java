package com.indigententerprises.domain;

public class Node<T> {

    private final T t;

    public Node(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }

    // mutable state
    private long timeoutInMilliseconds;

    public long getTimeoutInMilliseconds() {
        return timeoutInMilliseconds;
    }

    public void setTimeoutInMilliseconds(long timeoutInMilliseconds) {
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }
}
