package com.cyberlink.cosmetic.statsd;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class DefaultStatsDUpdater extends AbstractService implements
        StatsDUpdater {
    private StatsDClient statsDClient;
    private double defaultSampleRate = 1.0;

    public DefaultStatsDUpdater(String prefix, String host, int port, double sampleRate) {
        if(!Constants.getIsStatsdEnable())
            return;
        statsDClient = new NonBlockingStatsDClient(prefix, host, port);
        defaultSampleRate = sampleRate;
    }

    public void recordGaugeValue(String aspect, Number value) {
        if(!Constants.getIsStatsdEnable())
            return;
        statsDClient.recordGaugeValue(aspect, value.longValue());
    }

    public void recordExecutionTime(String aspect, long timeInMs) {
        recordExecutionTime(aspect, timeInMs, defaultSampleRate);
    }

    public void recordExecutionTime(String aspect, long timeInMs,
            double sampleRate) {
        if(!Constants.getIsStatsdEnable())
            return;
        statsDClient.recordExecutionTime(aspect, timeInMs, sampleRate);
    }

    @Override
    public void count(String aspect, long delta) {
        count(aspect, delta, defaultSampleRate);
    }

    @Override
    public void count(String aspect, long delta, double sampleRate) {
        if(!Constants.getIsStatsdEnable())
            return;
        statsDClient.count(aspect, delta, sampleRate);
    }

    @Override
    public void increment(String aspect) {
        count(aspect, 1l, defaultSampleRate);
    }

    @Override
    public void increment(String aspect, double sampleRate) {
        count(aspect, 1l, sampleRate);

    }
}
