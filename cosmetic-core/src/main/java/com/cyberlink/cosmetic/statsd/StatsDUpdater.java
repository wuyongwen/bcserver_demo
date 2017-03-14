package com.cyberlink.cosmetic.statsd;

public interface StatsDUpdater {
    void recordGaugeValue(String aspect, Number value);

    void recordExecutionTime(String aspect, long timeInMs);

    void recordExecutionTime(String aspect, long timeInMs, double sampleRate);

    void increment(String aspect);

    void increment(String aspect, double sampleRate);

    void count(String aspect, long delta);

    void count(String aspect, long delta, double sampleRate);
}
