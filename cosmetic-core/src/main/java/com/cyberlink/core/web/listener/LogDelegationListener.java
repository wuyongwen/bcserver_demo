package com.cyberlink.core.web.listener;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

import org.slf4j.bridge.SLF4JBridgeHandler;

public class LogDelegationListener {
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // turn off original jul loggers
            final Logger root = LogManager.getLogManager().getLogger("");
            final Handler[] handlers = root.getHandlers();
            root.removeHandler(handlers[0]);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(LogDelegationListener.class)
                    .error("Fail to turn off original jul loggers", e);
        }
        SLF4JBridgeHandler.install();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        SLF4JBridgeHandler.uninstall();
    }
}
