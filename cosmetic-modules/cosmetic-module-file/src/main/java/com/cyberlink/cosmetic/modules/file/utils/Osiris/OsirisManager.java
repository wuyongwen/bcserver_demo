package com.cyberlink.cosmetic.modules.file.utils.Osiris;

import java.util.ArrayList;
import java.util.List;

public class OsirisManager {
    
    public OsirisManager(String nativeLibPath, int maxHandlerCount, int threadCount) {
        this.handlerMaxCount = maxHandlerCount;
        this.nativeLibPath = nativeLibPath;
        this.threadMaxCount = threadCount;
    }

    public interface ProcessHandler {
        public <E> E Execute(final OsirisHandler handler);
    }
    
    public <E> E Process(ProcessHandler proc) {
        if(proc == null)
            return null;
        OsirisHandler handler = GetHandler();
        if(handler == null)
            return null;
        E result = proc.Execute(handler);
        ReturnHandler(handler);
        return result;
    }
    
    private OsirisHandler GetHandler() {
        OsirisHandler handler = null;
        if(!IsEnable())
            return handler;
        
        synchronized(osirisHandlers) {
            if(osirisHandlers.size() > 0) {
                handler = osirisHandlers.remove(0);
            }
        }
        return handler;
    }
    
    private void ReturnHandler(OsirisHandler handler) {
        if(!IsEnable()) {
            Boolean unload = (releaseCount + 1) >= handlerMaxCount;
            handler.Destroy(unload);
            return;
        }
        
        synchronized(osirisHandlers) {
            osirisHandlers.add(handler);
        }
    }
    
    public void Start() {
        for(int c = 0; c < handlerMaxCount; c++) {
            if(osirisHandlers.size() >= handlerMaxCount)
                break;
            OsirisHandler oh = new OsirisHandler(nativeLibPath, threadMaxCount);
            if(oh.GetPeerId() == null)
                continue;
            osirisHandlers.add(oh);
        }
        
        enable = true;
    }
    
    public void Stop() {
        enable = false;
        
        synchronized(osirisHandlers) {
            for(OsirisHandler h : osirisHandlers) {
                Boolean unload = (releaseCount + 1) >= handlerMaxCount;
                h.Destroy(unload);
                releaseCount++;
            }
        }
        
        osirisHandlers.clear();
    }
    
    public boolean IsEnable() {
        return enable;
    }
    
    private int handlerMaxCount;
    private int threadMaxCount;
    private int releaseCount;
    private String nativeLibPath;
    private Boolean enable = false;
    private List<OsirisHandler> osirisHandlers = new ArrayList<OsirisHandler>();
}
