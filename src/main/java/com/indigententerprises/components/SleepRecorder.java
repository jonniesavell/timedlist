package com.indigententerprises.components;

class SleepRecorder {

    private long sleepBegan;
    private boolean sleepInitiated;

    SleepRecorder() {
        sleepBegan = 0L;
        sleepInitiated = false;
    }

    public long getSleepBegan() throws SleepInitializationException {
        if (!sleepInitiated) {
            throw new SleepInitializationException();
        } else {
            return sleepBegan;
        }
    }

    public void setSleepBegan(long sleepBegan) {
        this.sleepInitiated = true;
        this.sleepBegan = sleepBegan;
    }
}
