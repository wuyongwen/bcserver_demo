package com.cyberlink.cosmetic.action.backend.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.action.backend.service.BackendPostService;
import com.cyberlink.cosmetic.action.backend.service.BackendPostService.Status;
import com.cyberlink.cosmetic.modules.post.service.PostService;

public class BackendPostServiceImpl extends AbstractService implements BackendPostService {
    
    private PostService postService;
    private TransactionTemplate transactionTemplate;

    private final int nThreads = 2;
    private final PoolWorker[] threads;
    private final LinkedList<BackendPostRunnable> queue;
    private Boolean forceStop;
    
    public BackendPostServiceImpl()
    {
        queue = new LinkedList<BackendPostRunnable>();
        threads = new PoolWorker[nThreads];
        start();
    }

    public void execute(BackendPostRunnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }
    
    private class PoolWorker extends Thread {
        public Status status = Status.FREE;
        public PoolWorker(String name) {
            super(name);
        }
        
        public void run() {
            BackendPostRunnable r = null;
            while (true && !forceStop) {
                synchronized(queue) {
                    while (queue.isEmpty() && !forceStop) {
                        try
                        {
                            status = Status.FREE;
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }
                    if(!queue.isEmpty())
                        r = queue.removeFirst();
                }
                try {
                    if(r != null) {
                        status = Status.PROCESSING;
                        r.doWork(transactionTemplate);
                    }
                }
                catch (RuntimeException e) {
                }
            }
        }
    }

    @Override
    public void start() {
        forceStop = false;
        for (int i = 0; i < nThreads; i++) {
            if(threads[i] != null)
                continue;
            threads[i] = new PoolWorker("BackendPostService_" + String.valueOf(i));
            threads[i].start();
        }
    }

    @Override
    public void stop() {
        synchronized(queue) {
            forceStop = true;
            queue.notifyAll();
        }
        for (int i = 0; i < nThreads; i++) {
            try {
                if(threads[i] == null)
                    continue;
                threads[i].join();
            } catch (InterruptedException e) {
            } finally {
                threads[i] = null;
            }
        }
    }

    @Override
    public void addTask(BackendPostRunnable runable) {
        execute(runable);
    }

    @Override
    public Map<String, Status> getStatus() {
        Map<String, Status> result = new HashMap<String, Status>();
        for (int i = 0; i < nThreads; i++) {
            if(threads[i] == null)
                result.put("Thread-" + String.valueOf(i), Status.NULL);
            else
                result.put(threads[i].getName(), threads[i].status);
        }
        return result;
    }
    
    @Override
    public int getTaskCount() {
        int result = 0;
        synchronized(queue) {
            result = queue.size();
        }
        return result;
    }
    
    public PostService getPostService() {
        return postService;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

}
