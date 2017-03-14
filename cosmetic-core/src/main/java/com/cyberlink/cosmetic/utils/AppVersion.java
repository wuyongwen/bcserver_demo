package com.cyberlink.cosmetic.utils;

public class AppVersion {
    
    static private int [] verIdxPad = new int[]{8, 4, 4};
    static public Long defaultVersion = 100000000L;
    static public String defaultVersionString = "1.0.0";
    
    static public Long getAppVersion(String appVersion) {
        if(appVersion == null)
            return defaultVersion;
        
        Long result = 0L;
        String [] vers = appVersion.split("\\.");
        String varStr = "";
        for(int idx = 0; idx < verIdxPad.length; idx++) {
            String v = "0";
            if(idx < vers.length)
                v = vers[idx];
            varStr += String.format("%0" + verIdxPad[idx] +"d", Long.valueOf(v));
        }
        if(varStr.length() > 0)
            result = Long.valueOf(varStr);
        
        return result;
    }
    
    static public String getAppVersion(Long appVersion) {
        String version = String.valueOf(appVersion);
        int [] verBs = new int[verIdxPad.length];
        int [] verLs = new int[verIdxPad.length];
        int length = 0;
        for(int idx = verIdxPad.length - 1; idx > 0; idx--) {
            verBs[idx] = version.length() - verIdxPad[idx] - length;
            verLs[idx] = verIdxPad[idx];
            length += verIdxPad[idx];
        }
        verBs[0] = 0;
        verLs[0] = version.length() - length;
        
        String result = "";
        for(int idx = 0; idx < verBs.length ; idx++){
            result += String.valueOf(Long.valueOf(version.substring(verBs[idx], verBs[idx] + verLs[idx])));
            if(idx < verBs.length - 1)
                result += ".";
        }
        if(result.length() <= 0)
            return defaultVersionString;
        
        return result;
    }
}
