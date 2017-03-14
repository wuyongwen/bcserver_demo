package com.cyberlink.cosmetic.action.backend.service;

import java.util.Map;

import org.springframework.transaction.support.TransactionTemplate;

public interface BackendPostService {

    public enum Status {
        NULL, FREE, PROCESSING, STOP;
    }
    
    public abstract class BackendPostRunnable implements Runnable {
        public String getName() {
            return this.getClass().getName();
        }
        
        protected abstract Boolean getDoInTransaction();
        
        public void doWork(TransactionTemplate transactionTemplate) {
            if(!getDoInTransaction()) {
                run();
                return;
            }
            doInTransaction(transactionTemplate);
        }

        abstract protected void doInTransaction(TransactionTemplate transactionTemplate);
    }
    
    public abstract class TransactionRunnable extends BackendPostRunnable {
        protected Boolean getDoInTransaction() {
            return true;
        }
    }
    
	void start();
	void stop();
	void addTask(BackendPostRunnable runable);
	int getTaskCount();
	Map<String, Status> getStatus();

}
