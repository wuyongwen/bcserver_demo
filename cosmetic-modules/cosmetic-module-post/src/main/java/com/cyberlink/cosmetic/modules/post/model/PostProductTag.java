package com.cyberlink.cosmetic.modules.post.model;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PostProductTag {
    
    @JsonView(Views.Public.class)
    public Long productId = (long)0;
    
    @JsonView(Views.Public.class)
    public String productName = "Maybelline Mascara";
    
    @JsonView(Views.Public.class)
    public String brandName = "Maybelline";
    
    @JsonView(Views.Public.class)
    public String productThumbnail = "http://www.mascaraforsensitiveeyes.com/wp-content/gallery/mascara-maybelline/maybelline-colossal-volume-express-waterproof-mascara_2a630788e52db5cf14104187fcc6b31a_images_1080_1440_mini.jpg";
    
    public Object tagPoint;
    
    @JsonView(Views.Public.class)
    public Object getTagPoint() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String tpStr = StringEscapeUtils.unescapeJava(tagPoint.toString());
            if(tpStr.startsWith("\""))
                tpStr = tpStr.substring(1);
            if(tpStr.endsWith("\""))
                tpStr = tpStr.substring(0, tpStr.length() - 1);
            JsonNode actualObj = mapper.readValue(tpStr, JsonNode.class);      
            return mapper.writer((PrettyPrinter)null).writeValueAsString(actualObj);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public PostProductTag()
    {
        productName = "";
        productThumbnail = "";
        tagPoint = null;
    }
    
    public PostProductTag(Product product, String tagPoint) {
        this.productId = product.getId();
        productName = product.getProductName();
        brandName = product.getBrand().getBrandName();
        productThumbnail = product.getImg_original();
        this.tagPoint = tagPoint;
    }
}