package com.cyberlink.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.imgscalr.Scalr;

import com.cyberlink.core.model.ImageMetadata;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.service.ImageService;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class ImageServiceImpl extends AbstractService implements ImageService {
    @Override
    public File generate(InputStream file, String fileName, int width,
            int height) {
        String imageName = fileName + "_" + width + "_" + height;
        String extension = FilenameUtils.getExtension(fileName);
        try {
            File image = File.createTempFile(imageName, ".tmp");
            BufferedImage bi = Scalr.resize(ImageIO.read(file), width, height);
            ImageIO.write(bi, StringUtils.isEmpty(extension) ? "jpg"
                    : extension, image);
            return image;
        } catch (IOException ioe) {
            logger.error("Can't resize image file: " + imageName, ioe);
            return null;
        }
    }

    public Map<ImageMetadata, Object> getMetadatas(File file) {
        try {
            Metadata m = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory d = m.getDirectory(ExifSubIFDDirectory.class);
            if(d == null){
                return new HashMap<ImageMetadata, Object>();
            }
            
            Map<ImageMetadata, Object> ms = new HashMap<ImageMetadata, Object>();
            ms.put(ImageMetadata.TakenDate,
                    d.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
            ms.put(ImageMetadata.ResolutionX,
                    d.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
            ms.put(ImageMetadata.ResolutionY,
                    d.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
            ExifIFD0Directory d0 = m.getDirectory(ExifIFD0Directory.class);
            ms.put(ImageMetadata.Orientation,
                    d0.getString(ExifIFD0Directory.TAG_ORIENTATION));
            return ms;
        } catch (Exception e) {
            logger.error("", e);
            return new HashMap<ImageMetadata, Object>();
        }
    }
}
