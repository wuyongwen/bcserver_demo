package com.cyberlink.cosmetic.statsd;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
@Documented
public @interface StatsD {

    /**
     * Whether to log event triggering or not, default is true.
     * 
     * @return
     */
    boolean count() default true;

    /**
     * Whether to record execution time or not, default is true
     * 
     * @return
     */
    boolean time() default true;

    /**
     * the sampling rate being employed. For example, a rate of 0.1 would tell
     * StatsD that this counter is being sent sampled every 1/10th of the time
     * 
     * @return
     */
    double sampleRate() default 0.0;
}
