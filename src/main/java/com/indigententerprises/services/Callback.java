package com.indigententerprises.services;

public interface Callback<T> {
    public void timedOut(T t);
}
