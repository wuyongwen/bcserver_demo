package com.cyberlink.cosmetic.modules.file.utils.Osiris;

import java.io.File;

public class OsirisHandler {
    
    private native void Init(String libPath, int threadCount);
    private native void Unit();
    private native long Create();
    private native void Release(long peer);
    private native float GetScore(long peer, int width, int height, int stride, int pixelFormat, int contentLength, byte[] pData);
    private native String GetFaceArea(long peer, int width, int height, int stride, int pixelFormat, byte[] pData);
    private native String Echo(long peer, String s);
    
    static private Boolean isSupport = null;    
    static private String platform;
    private String nativeLibPath;
    private int threadCount = 2;
    private Long peer;
    
    private int MIN_HEIGHT = 400;
    private int MIN_WIDTH = 400;
    private int LOW_RESOLUTION = 250000;
    
    public OsirisHandler(String nativeLibPath, int threadCount) {
        this.nativeLibPath = nativeLibPath;
        this.threadCount = threadCount;
        if(!IsSupport())
            return;
        peer = Create();
    }
    
    private boolean IsSupport() {
        if(isSupport != null)
            return isSupport;
        
        String libPath = GetLibraryDirectory(nativeLibPath, "osiris_jni", true);
        if(libPath == null) {
            isSupport = false;
            return isSupport;
        }
        if(!platform.contains("win")) {
            libPath = GetLibraryDirectory(nativeLibPath, "Osiris", false);
            if(libPath == null) {
                isSupport = false;
                return isSupport;
            }
        }
        Init(libPath, this.threadCount);
        isSupport = true;
        return isSupport;
    }
    
    private boolean IsUsablePeer() {
        return peer != null;
    }
    
    public String Test(String message) {
        if(!IsUsablePeer())
            return platform + " is not supported";
        String result = Echo(peer, message);
        return result;
    }
    
    public Float GetPhotoScore(int width, int height, int stride, int pixelFormat, int contentLength, byte[] pData) {
        if(!IsUsablePeer())
            return null;
        
        if (width <= MIN_WIDTH || height <= MIN_HEIGHT || (width*height <= LOW_RESOLUTION))
            return 0F;
        
        /* Temporary disable to get score from Osiris module
         * float result = GetScore(peer, width, height, stride, pixelFormat, contentLength, pData);
         */
        float result = 100F;
        return result;
    }
    
    public String Detect(int width, int height, int stride, int pixelFormat, byte[] pData) {
        if(!IsUsablePeer())
            return null;
        
        String result = GetFaceArea(peer, width, height, stride, pixelFormat, pData); 
        return result; 
    }
    
    public void Destroy(Boolean unload) {
        if(!IsSupport() || !IsUsablePeer())
            return;
        
        Release(peer);
        if(unload) {
            Unit();
            isSupport = null;
        }
    }
    
    public Long GetPeerId() {
        return peer;
    }
    
    private String GetLibraryDirectory(String path, String library, Boolean load) {
        if(path == null) {
            return null;
        }
        
        String javaLibPath = System.getProperty("java.library.path");
        String additionPath = path;
        
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String libFileName;
        platform = osName + ":" + osArch;
        
        if (osName.startsWith("win")) {
            libFileName = library + ".dll";
            if (osArch.contains("86")) {
                additionPath += "/win-x86";
            } else if (osArch.contains("64")) {
                additionPath += "/win-x86_64";
            } else {
                return null;
            }
            javaLibPath = additionPath + ";" + javaLibPath;
        } else if (osName.startsWith("linux")) {
            libFileName = "lib" + library + ".so";
            if (osArch.equalsIgnoreCase("amd64")) {
                additionPath += "/linux-amd64";
            } else if (osArch.equalsIgnoreCase("ia64")) {
                additionPath += "/linux-ia64";
            } else if (osArch.equalsIgnoreCase("i386")) {
                additionPath += "/linux-x86";
            } else {
                return null;
            }
            javaLibPath = additionPath + ":" + javaLibPath;
        } else {
            return null;
        }

        File f = new File(additionPath + "/" + libFileName);
        if(f.exists() && !f.isDirectory()) 
        {
            if(load)
                System.load(f.getAbsolutePath());
            return additionPath;
        }
        return null;
    }

}
