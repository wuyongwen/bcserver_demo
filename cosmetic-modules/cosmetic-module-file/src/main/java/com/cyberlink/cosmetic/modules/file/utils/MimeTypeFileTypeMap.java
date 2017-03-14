package com.cyberlink.cosmetic.modules.file.utils;

import java.util.HashMap;
import org.springframework.http.MediaType;

public class MimeTypeFileTypeMap {
    private final static HashMap<String, String> mimeTypes = new HashMap<String, String>();
    
    static {
        mimeTypes.put(MediaType.IMAGE_GIF_VALUE, "gif");
        mimeTypes.put(MediaType.IMAGE_JPEG_VALUE, "jpg");
        mimeTypes.put(MediaType.IMAGE_PNG_VALUE, "png");
        mimeTypes.put("image/bmp", "bmp");
        mimeTypes.put("image/tiff", "tif");
        mimeTypes.put("image/jpg", "jpg");
    }
    
    public static String getExtension(String mimeType) {
        return mimeTypes.get(mimeType);
    }
}
