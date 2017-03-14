package com.cyberlink.core;

import java.util.ArrayList;
import java.util.List;

public abstract class BeanLocator {
    private static List<BeanLocator> BEAN_LOCATORS = new ArrayList<BeanLocator>();

    public abstract <T> T findBean(String beanName);

    protected void performRegistration() {
        registerLocator(this);
    }

    private static void registerLocator(BeanLocator beanLocator) {
        BEAN_LOCATORS.add(beanLocator);
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> T getBean(String beanName) {
        if (BEAN_LOCATORS.size() == 0) {
            throw new IllegalStateException(
                    "No bean locator is registered. Seems environment is not prepared (Such as: Spring not startup yet).");
        }
        for (BeanLocator beanLocator : BEAN_LOCATORS) {
            Object bean = beanLocator.findBean(beanName);
            if (bean != null) {
                return (T) bean;
            }
        }
        return null;
    }
}
