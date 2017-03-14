package com.cyberlink.cosmetic.modules.file.utils.TupuTech;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.cyberlink.core.BeanLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TupuClient {
    
    private String url;
    private ObjectMapper objectMapper;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    public TupuClient() {
        this.url = TupuConstant.tupuBaseUrl;
        this.objectMapper = BeanLocator.getBean("web.objectMapper");
        try {
            InputStream inPrivate = this.getClass().getResourceAsStream("/TupuClient/pem/pkcs8_private_key.pem");
            String privateKeyStr = SignatureAndVerify.ReadKey(inPrivate);
            byte[] buffer = Base64.decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            
            InputStream inPublic = this.getClass().getResourceAsStream("/TupuClient/pem/open_tuputech_com_public_key.pem");
            String publicKeyStr = SignatureAndVerify.ReadKey(inPublic);
            byte[] pubBuffer = Base64.decode(publicKeyStr);
            KeyFactory pubKeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubBuffer);
            publicKey = (RSAPublicKey) pubKeyFactory.generatePublic(pubKeySpec);

        } catch (Exception e) {
        }
    }
    
    public Boolean Detect(BufferedImage tempImg, TupuConstant.DetectType type, Map<String, Object> info) {
        BufferedImage scaledImg;
        if(tempImg.getWidth() >  TupuConstant.MaxImageSize || tempImg.getHeight() > TupuConstant.MaxImageSize)
            scaledImg = Scalr.resize(tempImg, TupuConstant.MaxImageSize);
        else 
            scaledImg = tempImg;
        
        return upload(type, scaledImg, info);
    }

    private Boolean getTargetImages(JsonNode json, TupuConstant.DetectType type, Map<String, Object> info) {
        Boolean violated = false;
        try {
            JsonNode nodeFileList = json.get(TupuConstant.typeTaskIdMap.get(type)).get("fileList");
            if(nodeFileList == null || !nodeFileList.isArray())
                return null;
            Iterator<JsonNode> fileListIt = nodeFileList.iterator();
            while(fileListIt.hasNext()) {
                JsonNode file = fileListIt.next();
                if(info != null)
                    info.put(type.toString() + "_" + file.get("name").asText(), file);
                if(file.get("review").asBoolean()) {
                    violated = false;
                    break;
                }
                if(type.IsViolating(file.get("label").asInt())) {
                    violated = true;
                    break;
                }
            }
        } catch (NullPointerException e) {
            violated = null;
        }
        
        return violated;
    }
    
    private CloseableHttpClient initHttpClient(String secretId) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000000).setConnectTimeout(5000000)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        HttpHost localhost = new HttpHost(url + secretId);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);
        
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(
                requestConfig).build();
        return httpClient;
    }
    
    @SuppressWarnings("deprecation")
    private Boolean upload(TupuConstant.DetectType type, BufferedImage imgBuffer, Map<String, Object> info) {
        String secretId = TupuConstant.typeSecrectIdMap.get(type);
        String timestamp = Math.round(System.currentTimeMillis() / 1000.0) + "";
        String nonce = Math.random() + "";
        String sign_string = secretId + "," + timestamp + "," + nonce;
        String signature = SignatureAndVerify.Signature(privateKey, sign_string);
        
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        HttpPost httpPost = new HttpPost(url + secretId);
        Boolean voilated = null;
        try {
            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(Charset.forName(HTTP.UTF_8));

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(imgBuffer, "jpg", stream);
            ContentBody cbFile = new ByteArrayBody(stream.toByteArray(), timestamp);
            
            builder.addPart("image", cbFile);
            builder.addPart("timestamp", new StringBody(timestamp, contentType));
            builder.addPart("nonce", new StringBody(nonce, contentType));
            builder.addPart("signature", new StringBody(signature, contentType));
            
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            CloseableHttpClient httpClient = initHttpClient(secretId);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            
            String result = EntityUtils.toString(response.getEntity(),  HTTP.UTF_8);
            JsonNode objResult;
            objResult = objectMapper.readValue(result, JsonNode.class);
            
            JsonNode nodeJson = objResult.get("json");
            JsonNode nodeSignature = objResult.get("signature");
            if(nodeJson == null || nodeSignature == null)
                return null;

            String strJson = nodeJson.asText();
            boolean verify = SignatureAndVerify.Verify(publicKey, nodeSignature.asText(), strJson);
            if (verify) {
                JsonNode objJson = objectMapper.readValue(strJson, JsonNode.class);
                voilated = getTargetImages(objJson, type, info);
            }
            response.close();
            return voilated;
        } catch (Exception e) {
            return voilated;
        } finally {
            httpPost.releaseConnection();
            httpPost.abort();
        }
    }
}
