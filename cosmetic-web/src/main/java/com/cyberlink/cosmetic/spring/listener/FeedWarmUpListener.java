package com.cyberlink.cosmetic.spring.listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.cosmetic.Constants;

public class FeedWarmUpListener extends AbstractService implements
        ApplicationListener<ContextRefreshedEvent> {
    private static boolean runOnce = Boolean.FALSE;
    private String warmUpUrl;
    private int iterations = 1000;
    private long waitForTomcatStartup;
    private int threads = 20;
    private URLContentReader reader;
    private boolean enabled = Boolean.FALSE;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setWaitForTomcatStartup(long waitForTomcatStartup) {
        this.waitForTomcatStartup = waitForTomcatStartup;
    }

    public void setReader(URLContentReader reader) {
        this.reader = reader;
    }

    public void setWarmUpUrl(String warmUpUrl) {
        this.warmUpUrl = warmUpUrl;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (runOnce) {
            return;
        }
        runOnce = Boolean.TRUE;
        if (!enabled) {
            setInitialized();
            return;
        }

        final ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(waitForTomcatStartup);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), 1);
                }
                warmUp();
            }
        });
        executor.shutdown();
    }

    private void setInitialized() {
        Constants.setInitialized(Boolean.TRUE);
    }

    private void warmUp() {
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < iterations; i++) {
            final int j = i;
            try {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        reader.get(warmUpUrl);
                        if (j == iterations - 1) {
                            setInitialized();
                        }
                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        executor.shutdown();
    }
}
