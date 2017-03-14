package com.cyberlink.core.service;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import com.cyberlink.core.model.ImageMetadata;

public interface ImageService {
    File generate(InputStream file, String fileName, int width, int height);

    Map<ImageMetadata, Object> getMetadatas(File file);
}
