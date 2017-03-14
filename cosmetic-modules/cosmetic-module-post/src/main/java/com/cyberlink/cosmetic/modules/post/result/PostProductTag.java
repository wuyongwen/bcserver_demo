package com.cyberlink.cosmetic.modules.post.result;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.fasterxml.jackson.annotation.JsonView;


public class PostProductTag {
    
    @JsonView(Views.Public.class)
    public Long productId = (long)0;
    
    @JsonView(Views.Public.class)
    public String productName = "Maybelline Mascara";
    
    @JsonView(Views.Public.class)
    public String productThumbnail = "http://www.mascaraforsensitiveeyes.com/wp-content/gallery/mascara-maybelline/maybelline-colossal-volume-express-waterproof-mascara_2a630788e52db5cf14104187fcc6b31a_images_1080_1440_mini.jpg";
    
    @JsonView(Views.Public.class)
    public String tagPoint;
    
    public PostProductTag()
    {
        productName = "";
        productThumbnail = "";
        tagPoint = "";
    }
    
    public PostProductTag(Product product, String tagPoint) {
        this.productId = product.getId();
        productName = product.getProductName();
        productThumbnail = product.getImg_original();
        this.tagPoint = tagPoint;
    }
}