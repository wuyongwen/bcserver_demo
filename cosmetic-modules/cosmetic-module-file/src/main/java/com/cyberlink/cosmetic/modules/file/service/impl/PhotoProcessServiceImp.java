package com.cyberlink.cosmetic.modules.file.service.impl;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService.ImageViolationType;
import com.cyberlink.cosmetic.modules.file.utils.Osiris.OsirisHandler;
import com.cyberlink.cosmetic.modules.file.utils.Osiris.OsirisManager;
import com.cyberlink.cosmetic.modules.file.utils.Osiris.OsirisManager.ProcessHandler;
import com.cyberlink.cosmetic.modules.file.utils.TupuTech.TupuClient;
import com.cyberlink.cosmetic.modules.file.utils.TupuTech.TupuConstant;
import com.fasterxml.jackson.annotation.JsonView;

public class PhotoProcessServiceImp extends AbstractService implements PhotoProcessService {

    private OsirisManager osirisManager = null;
    private TupuClient tupuClient = null;
    private String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36";
    private Integer UNKNOWN_FILE_SIZE = -1;

    static public class Rect {
        public int left;
        public int top;
        public int right;
        public int bottom;
        
        public Rect() {
            
        }
        @JsonView(Views.Public.class)
        public int getLeft() {
            return left;
        }
        public void setLeft(int left) {
            this.left = left;
        }
        @JsonView(Views.Public.class)
        public int getTop() {
            return top;
        }
        public void setTop(int top) {
            this.top = top;
        }
        @JsonView(Views.Public.class)
        public int getRight() {
            return right;
        }
        public void setRight(int right) {
            this.right = right;
        }
        @JsonView(Views.Public.class)
        public int getBottom() {
            return bottom;
        }
        public void setBottom(int bottom) {
            this.bottom = bottom;
        }

    }
    
    public PhotoProcessServiceImp(int maxHandler, int maxThreadCount) {
        Start(maxHandler, maxThreadCount);
        tupuClient = new TupuClient();
    }
    
    public void Destroy() {
        Stop();
    }

    @Override
    public Pair<BufferedImage, Integer> getBufferAndLengthFromUrl(String imgUrl) throws IOException {
        return getBufferAndLengthFromUrl(imgUrl, 5000);
    }
    
    @Override
    public Pair<BufferedImage, Integer> getBufferAndLengthFromUrl(String imgUrl, Integer readTimeOut) throws IOException {
        int pushbackLimit = 100;
        imgUrl = URLDecoder.decode(imgUrl, "UTF-8");
        URL url = new URL(imgUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent",  USER_AGENT);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(readTimeOut);
        Integer contentLength = connection.getContentLength();
        InputStream urlStream = connection.getInputStream();
        PushbackInputStream pushUrlStream = new PushbackInputStream(urlStream, pushbackLimit);
        byte [] firstBytes = new byte[pushbackLimit];
        pushUrlStream.read(firstBytes);
        pushUrlStream.unread(firstBytes);

        ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
        String mimeType = URLConnection.guessContentTypeFromStream(bais);
        if(mimeType == null) {
            mimeType = connection.getContentType();
        }
        if (mimeType != null && mimeType.startsWith("image/")) {
            return Pair.of(ImageIO.read(pushUrlStream), contentLength);
        }
        return null;
    }
    
    @Override
    public Pair<BufferedImage, Integer> getBufferAndLengthFromDataUrl(String dataUrl) throws IOException {
        String encodingPrefix = "base64,";
        int contentStartIndex = dataUrl.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = Base64.decodeBase64(dataUrl.substring(contentStartIndex));
        BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(imageData));
        return Pair.of(inputImage, UNKNOWN_FILE_SIZE);
    }

    @Override
    public Float GetScore(final Pair<BufferedImage, Integer> imgResult) {
        if(osirisManager == null || !osirisManager.IsEnable() || imgResult == null)
            return null;
        
        return osirisManager.Process(new ProcessHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public Float Execute(OsirisHandler handler) {
                try {
                    BufferedImage bufferedImage = imgResult.getLeft();
                    byte[] pData = extractBytes(bufferedImage);
                    int pixelType = GetPixelType(bufferedImage);
                    return handler.GetPhotoScore(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getWidth() * pixelType,  pixelType, imgResult.getRight(), pData);
                }
                catch (Exception e) {
                    return null;
                }
            }
            
        });
    }    
    
    @Override
    public String DetectFace(final Pair<BufferedImage, Integer> imgResult, final Boolean drawFace) {
        String result = null;
        if(osirisManager == null || !osirisManager.IsEnable() || imgResult == null)
            return result;
        
        return osirisManager.Process(new ProcessHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public String Execute(OsirisHandler handler) {
                try {
                    BufferedImage bufferedImage = imgResult.getLeft();
                    byte[] pData = extractBytes(bufferedImage);
                    int pixelType = GetPixelType(bufferedImage);
                    String result = handler.Detect(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getWidth() * pixelType,  pixelType, pData);                   
                    return result;
                }
                catch (Exception e) {
                    return null;
                }
            }
        });
    }
    
    @Override
    public Map<ImageViolationType, Boolean> DetectImageViolation(BufferedImage img, List<ImageViolationType> detecType, Map<String, Object> info) {
        Map<ImageViolationType, Boolean> result = new HashMap<ImageViolationType, Boolean>();
        if(img == null || detecType == null || detecType.size() <= 0)
            return result;
        
        for(ImageViolationType vt : detecType) {
            switch(vt) {
            case Porn:{
                result.put(vt, tupuClient.Detect(img, TupuConstant.DetectType.PORN, info));
                break;
            }
            case Violence:{
                result.put(vt, tupuClient.Detect(img, TupuConstant.DetectType.VIOLENCE, info));
                break;
            }
            default:
                break;
            }
        }
        return result;
    }
    
    @Override
    public Boolean DetectWordViolation(String content) {
        return false;
    }
    
    private int GetPixelType(BufferedImage bufferedImage) {
        int pixelType = 3;
        switch(bufferedImage.getType()) {
        case BufferedImage.TYPE_4BYTE_ABGR:
            pixelType = 4;
            break;
        case BufferedImage.TYPE_3BYTE_BGR:
        default:
            pixelType = 3;
            break;
        }
        return pixelType;
    }
    
    private byte[] extractBytes (BufferedImage bufferedImage) throws IOException {
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
        return ( data.getData() );
   }

    @Override
    public void Start(int handlerCount, int threadCount) {
        if(osirisManager != null)
            return;
        osirisManager = new OsirisManager(Constants.getNativeLibPath(), handlerCount, threadCount);
        osirisManager.Start();
    }

    @Override
    public void Stop() {
        if(osirisManager == null)
            return;
        osirisManager.Stop();
        osirisManager = null;
    }
   
}
