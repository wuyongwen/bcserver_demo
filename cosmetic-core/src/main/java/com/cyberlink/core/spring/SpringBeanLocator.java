package com.cyberlink.core.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.cyberlink.core.BeanLocator;

final class SpringBeanLocator extends BeanLocator implements
        ApplicationContextAware {

    private ApplicationContext applicationContext = null;
    private Logger logger = LoggerFactory.getLogger(SpringBeanLocator.class);

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
        performRegistration();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findBean(String beanName) {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "Spring environment is not fully started up yet. Cannot getBean at this time.");
        }
        try {
            return (T) applicationContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("", e);
            return null;
        }
    }
}