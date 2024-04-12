package com.indigententerprises.services;

/**
 * author: jonnie savell
 */
public interface TimedListService<T> {
    public void add(T t, long milliseconds) throws ElementAlreadyFoundException;
    public void remove(T t);
}
