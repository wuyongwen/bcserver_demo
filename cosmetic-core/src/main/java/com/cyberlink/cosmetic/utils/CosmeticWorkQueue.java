package com.cyberlink.cosmetic.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CosmeticWorkQueue {
    private final int nThreads;
    private final PoolWorker[] threads;
    private final LinkedList<CallbackRunable> queue;
    private final String name;
    
    public CosmeticWorkQueue(int nThreads, String name)
    {
        this.nThreads = nThreads;
        queue = new LinkedList<CallbackRunable>();
        threads = new PoolWorker[nThreads];
        this.name = name;
        initWorker();
    }

    public void initWorker() {
        for (int i=0; i<nThreads; i++) {
            if(threads[i] != null && threads[i].isAlive())
                continue;
                
            threads[i] = new PoolWorker(name + "_" + i);
            threads[i].start();
        }
    }

    public void execute(Runnable r) {
        synchronized(queue) {
            queue.addLast(new CallbackRunable(r, null));
            queue.notify();
        }
    }

    public Map<Integer, Boolean> getWorkerStatus() {
        Map<Integer, Boolean> result = new HashMap<Integer, Boolean>();
        Integer count = 0;
        for(PoolWorker pw : threads) {
            result.put(count++, pw.isAlive());
        }
        return result;
    }
    
    public Integer getTaskCount() {
        Integer size = null;
        synchronized(queue) {
            size = queue.size(); 
        }
        return size;
    }
    
    public void clearAllTask() {
        synchronized(queue) {
            queue.clear(); 
        }
    }
    
    public void execute(Runnable r, Callback c) {
        synchronized(queue) {
            queue.addLast(new CallbackRunable(r, c));
            queue.notify();
        }
    }
    
    public void dropTask() {
    	synchronized(queue) {
    		queue.removeFirst();
    	}
    }
    
    private class CallbackRunable implements Runnable {
    	private Runnable runable;
    	private Callback callback = null;

    	CallbackRunable(Runnable runable, Callback callback) {
    		this.runable = runable;
    		this.callback = callback;
    	}
    	
		@Override
		public void run() {
			runable.run();
			if (callback != null) {
				callback.callback();
			}
		}
    }
    
    private class PoolWorker extends Thread {
        public PoolWorker(String name) {
            super(name);
            setDaemon(false);
        }
        
        public void run() {
            Runnable r;
            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }

                    try {
                        r = (Runnable) queue.removeFirst();
                    }
                    catch(Exception e) {
                        r = null;
                        e.printStackTrace();
                    }
                }
                try {
                    if(r != null)
                        r.run();
                }
                catch (RuntimeException e) {
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}