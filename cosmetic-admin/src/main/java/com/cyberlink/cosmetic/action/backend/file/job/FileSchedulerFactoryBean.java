package com.cyberlink.cosmetic.action.backend.file.job;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class FileSchedulerFactoryBean extends SchedulerFactoryBean {
    private boolean enableQuartzTasks;

    public void setEnableQuartzTasks(boolean enableQuartzTasks) {
        this.enableQuartzTasks = enableQuartzTasks;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (enableQuartzTasks) {
            super.afterPropertiesSet();
        }
    }
}
