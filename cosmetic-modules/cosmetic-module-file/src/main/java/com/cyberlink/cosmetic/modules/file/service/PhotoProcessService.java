package com.cyberlink.cosmetic.modules.file.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public interface PhotoProcessService {
    
    public enum ImageViolationType {
        Porn, Violence, ForbiddenWord;
    }
    
    void Start(int handlerCount, int threadCount);
    
    void Stop();
    
    Pair<BufferedImage, Integer> getBufferAndLengthFromUrl(String imgUrl) throws IOException;
    
    Pair<BufferedImage, Integer> getBufferAndLengthFromUrl(String imgUrl, Integer readTimeOut) throws IOException;
    
    Pair<BufferedImage, Integer> getBufferAndLengthFromDataUrl(String dataUrl) throws IOException;
    
    Float GetScore(final Pair<BufferedImage, Integer> imgResult);

    String DetectFace(final Pair<BufferedImage, Integer> imgResult, final Boolean drawFace);
    
    Map<ImageViolationType, Boolean> DetectImageViolation(BufferedImage img, List<ImageViolationType> detecType, Map<String, Object> info);
    
    Boolean DetectWordViolation(String content);
}
