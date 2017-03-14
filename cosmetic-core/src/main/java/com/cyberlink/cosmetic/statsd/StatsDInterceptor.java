package com.cyberlink.cosmetic.statsd;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;

@Aspect
public class StatsDInterceptor extends AbstractService {
    private StatsDUpdater updater;

    public void setUpdater(StatsDUpdater updater) {
        this.updater = updater;
    }

    @Around("@annotation(statsd)")
    public Object invoke(final ProceedingJoinPoint pjp, StatsD statsd)
            throws Throwable {
        final long begin = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            final String aspect = getAspect(pjp);
            double sampleRate = getSampleRate(statsd);
            increment(statsd, aspect, sampleRate);
            recordExecutionTime(statsd, begin, aspect, sampleRate);
        }
    }

    private void recordExecutionTime(StatsD statsd, final long begin,
            final String aspect, double sampleRate) {
        if(!Constants.getIsStatsdEnable())
            return;
        if (!isTimeEnabled(statsd)) {
            return;
        }
        if (sampleRate == 0.0) {
            updater.recordExecutionTime(aspect, getDiff(begin));
        } else {
            updater.recordExecutionTime(aspect, getDiff(begin), sampleRate);
        }
    }

    private void increment(StatsD statsd, final String aspect, double sampleRate) {
        if(!Constants.getIsStatsdEnable())
            return;
        if (!isCountEnabled(statsd)) {
            return;
        }
        if (sampleRate == 0.0) {
            updater.count(aspect, 1);
        } else {
            updater.count(aspect, 1, sampleRate);
        }
    }

    private String getAspect(final ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();
        final String aspect = targetMethod.getDeclaringClass().getName() + "."
                + targetMethod.getName();
        return aspect;
    }

    private boolean isCountEnabled(StatsD t) {
        return t.count();
    }

    private boolean isTimeEnabled(StatsD t) {
        return t.count();
    }

    private double getSampleRate(StatsD t) {
        return t.sampleRate();
    }

    private long getDiff(long begin) {
        return System.currentTimeMillis() - begin;
    }

}
