package com.cyberlink.cosmetic.modules.file.utils.TupuTech;

import java.util.HashMap;
import java.util.Map;

public final class TupuConstant {
    public static enum DetectType {
        PORN(new ValueHandler() {
            public Boolean IsViolating(int value) {
                switch(value) {
                case 0:
                case 1:
                    return true;
                case 2:
                    return false;
                default:
                    return null;
                }
            }
        }),
        VIOLENCE(new ValueHandler() {
            public Boolean IsViolating(int value) {
                switch(value) {
                case 0:
                    return false;
                case 1:
                    return true;
                default:
                    return null;
                }
            }
        }),
        AD(new ValueHandler() {
            public Boolean IsViolating(int value) {
                switch(value) {
                case 0:
                    return false;
                case 1:
                case 2:
                    return true;
                default:
                    return null;
                }
            }
        });
        
        private interface ValueHandler {
            Boolean IsViolating(int value);
        }

        private ValueHandler valueHandler;

        private DetectType(ValueHandler valueHandler) {
            this.valueHandler = valueHandler;
        }
        
        public Boolean IsViolating(int value) {
            return valueHandler.IsViolating(value);
        }
    }
    
    public static final String tupuBaseUrl = "http://api.open.tuputech.com/v3/recognition/";
    public static final int batchThreshold = 100;
    public static final int MaxImageSize = 512;
    
    public static final Map<DetectType, String> typeTaskIdMap;
    
    static {
        typeTaskIdMap = new HashMap<DetectType, String>();
        typeTaskIdMap.put(DetectType.PORN, "54bcfc6c329af61034f7c2fc");
        typeTaskIdMap.put(DetectType.VIOLENCE, "55c03cf238dc1cfb3d80be14");
        typeTaskIdMap.put(DetectType.AD, "55b84cb66be856d869726d97");
    }
    
    public static final Map<DetectType, String> typeSecrectIdMap;
    
    static {
        typeSecrectIdMap = new HashMap<DetectType, String>();
        typeSecrectIdMap.put(DetectType.PORN, "5694d1f619520bf43ce0b52c");
        typeSecrectIdMap.put(DetectType.VIOLENCE, "56aeb9c1af2fb3b24ee02f31");
        typeSecrectIdMap.put(DetectType.AD, "56aeb9d8af2fb3b24ee02f34");
    }
}
