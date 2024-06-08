package com.indigententerprises.components;

import com.indigententerprises.services.Callback;

public class RunnableCallbackExecution<T> implements Runnable {

    private final Callback<T> callback;
    private final T payload;

    public RunnableCallbackExecution(
            final Callback<T> callback,
            final T payload
    ) {
        this.callback = callback;
        this.payload = payload;
    }

    @Override
    public void run() {
        try {
            callback.timedOut(payload);
        } catch (RuntimeException e) {
            // swallow
        }
    }
}
