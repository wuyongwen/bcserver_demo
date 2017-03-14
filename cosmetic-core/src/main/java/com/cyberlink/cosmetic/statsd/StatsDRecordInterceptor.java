package com.cyberlink.cosmetic.statsd;

import java.lang.reflect.Method;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.Constants;
import com.timgroup.statsd.StatsDClient;

@Intercepts(LifecycleStage.ResolutionExecution)
public class StatsDRecordInterceptor implements Interceptor {
    private static final StatsDClient statsd = BeanLocator
            .getBean("core.statsDClient");

    public Resolution intercept(ExecutionContext ctx) throws Exception {
        if(!Constants.getIsStatsdEnable())
            return ctx.proceed();
        
        final ActionBean actionBean = ctx.getActionBean();
        final Method handler = ctx.getHandler();
        if (actionBean != null && handler != null) {
            StatsDRecord annotation = handler.getAnnotation(StatsDRecord.class);
            if (annotation != null) {
                Long start = System.currentTimeMillis();
                Resolution resolution = ctx.proceed();
                Long end = System.currentTimeMillis();
                statsd.recordExecutionTime(annotation.value(), (end - start));
                return resolution;
            }
        }
        return ctx.proceed();
    }

}