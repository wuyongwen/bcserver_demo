package com.cyberlink.cosmetic.modules.file.service;

import java.util.List;

import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;

public interface ImageService {
    
    void thumbnail(FileItem sourceItem, Integer width, Integer height, FileItem newItem);
        
    void thumbnail(FileItem sourceItem, ThumbnailType type, FileItem newItem);
    
    void appendImages(Integer combineImagePixel,String direction,String outputImageFullPath,List<String> sourceImageFullPaths);
}
