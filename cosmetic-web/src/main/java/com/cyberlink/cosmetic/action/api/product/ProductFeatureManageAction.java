package com.cyberlink.cosmetic.action.api.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractMsrAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductFeatureDao;
import com.cyberlink.cosmetic.modules.product.model.ProductFeature;
import com.cyberlink.cosmetic.modules.product.model.ProductFeature.InApiView;
import com.cyberlink.cosmetic.modules.product.model.ProductFeatureMetadata;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.utils.AppVersion;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/ProductFeatureManage.action")
public class ProductFeatureManageAction extends AbstractMsrAction {

    @SpringBean("product.ProductFeatureDao")
	private ProductFeatureDao productFeatureDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    private int offset = 0;
    private int limit = 10;
    private String productFeatureGroup;
    private String extProductId;
    private String extProductIds;
    private String email;
    
    Pair<Long, String> getUserIdLocalePair(String token, String email) throws Exception {
        User user = null;
        if(token != null && token.length() > 0)
            user = getUserByToken(token);
        else if(email != null && email.length() > 0)
            user = getUserByEmail(email);
        else
            throw new Exception("Invalid token or email.");
        String region = user.getRegion();
        if(region == null)
            throw new Exception("Invalid user locale");
        
        return Pair.of(user.getId(), region);
    }
    
    ProductFeature copyProductFeature(ProductFeatureInput input, ProductFeature pf) throws Exception {
        if(input.userId != null && input.userLocale != null) {
            pf.setUserId(input.userId);
            pf.setLocale(input.userLocale);
        }
        else if(input.userToken != null || input.userEmail != null) {
            Pair<Long, String> usrIdLocalPair = getUserIdLocalePair(input.userToken, input.userEmail);
            pf.setUserId(usrIdLocalPair.getLeft());
            pf.setLocale(usrIdLocalPair.getRight());
        }
            
        pf.setExtProductId(input.extProductId);
        pf.setProductIndex(input.productIndex);
        pf.setProductType(input.productType);
        pf.setTypeIndex(input.typeIndex);
        pf.setProductTitle(input.productTitle);
        pf.setProductDescription(input.productDescription);
        pf.setImgOriginal(input.imgOriginal);
        pf.setImgUrl(input.imgUrl);
        pf.setPrice(input.price);
        pf.setStartDate(input.startDate);
        pf.setEndDate(input.endDate);
        pf.setVersion(AppVersion.getAppVersion(input.getAppVersion()));
        if(input.metadata != null) {
            pf.setMetadataValue(objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(input.metadata));
        }
        else
            pf.setMetadataValue(null);
        return pf;
    }
    
    ProductFeature toProductFeature(ProductFeatureInput input) throws Exception {
        ProductFeature pf = new ProductFeature();
        return copyProductFeature(input, pf);
    }
    
    ProductFeatureInput toProductFeatureInput(ProductFeature pf) {
        ProductFeatureInput input = new ProductFeatureInput();
        input.userId = pf.getId();
        input.userLocale = pf.getLocale();
        input.extProductId = pf.getExtProductId();
        input.productIndex = pf.getProductIndex();
        input.productType = pf.getProductType();
        input.typeIndex = pf.getTypeIndex();
        input.productTitle = pf.getProductTitle();
        input.productDescription = pf.getProductDescription();
        input.imgOriginal = pf.getImgOriginal();
        input.imgUrl = pf.getImgUrl();
        input.price = pf.getPrice();
        input.startDate = pf.getStartDate();
        input.endDate = pf.getEndDate();
        input.appVersion = pf.getAppVersion();
        input.metadata = pf.getMetadata();
        return input;
    }
    
    public Resolution list() {
        MsrApiResult apiResult = new MsrApiResult();
        try {
            Pair<Long, String> userIdLocaleMap = getUserIdLocalePair(null, email);
            PageResult<ProductFeature> pageResult = listProductFeature(userIdLocaleMap.getLeft());
            apiResult.Add("results", pageResult.getResults());
            apiResult.Add("totalSize", pageResult.getTotalSize());
        } catch (Exception e) {
        	//logger.error(e.getMessage());
            apiResult.setError(e.getMessage());
            return apiResult.getResult();
        }
        return apiResult.getResult(InApiView.class);
    }
    
	public PageResult<ProductFeature> listProductFeature(Long userId) {
	    BlockLimit blockLimit = new BlockLimit(offset, limit);
	    return productFeatureDao.getProductFeatureByUser(userId, null, null, null, blockLimit);
	}

	public Resolution createOrUpdate() {
	    MsrApiResult apiResult = new MsrApiResult();
        if(productFeatureGroup == null) {
            apiResult.setError("Invalid product feature group format.");
            return apiResult.getResult();
        }    
        
        try {
            List<ProductFeatureListInput> tmp = objectMapper.readValue(productFeatureGroup, new TypeReference<List<ProductFeatureListInput>>() {});
            for(ProductFeatureListInput list : tmp) {
                Pair<Long, String> usrIdLocalePair = getUserIdLocalePair(list.token, list.email);
                Set<String> inputExtProductIds = new HashSet<String>();
                Map<String, ProductFeatureInput> toCreateProductFeatureMap = new HashMap<String, ProductFeatureInput>();
                for(ProductFeatureInput input : list.productFeatureList) {
                    inputExtProductIds.add(input.extProductId);
                    toCreateProductFeatureMap.put(input.extProductId, input);
                }

                int offset = 0;
                int limit = 100;
                do {
                    BlockLimit blockLimit = new BlockLimit(offset, limit);
                    PageResult<Pair<String, ProductFeature>> pResult = productFeatureDao.findByExtProductIds(usrIdLocalePair.getLeft(), inputExtProductIds, blockLimit);
                    if(pResult.getResults().size() <= 0)
                        break;
                    for(Pair<String, ProductFeature> pair : pResult.getResults()) {
                        ProductFeatureInput pfInput = toCreateProductFeatureMap.get(pair.getLeft());
                        toCreateProductFeatureMap.remove(pair.getLeft());
                        updateProductFeature(pair.getRight(), usrIdLocalePair.getLeft(), usrIdLocalePair.getRight(), pfInput);
                    }
                    
                    offset += limit;
                    if(offset > pResult.getTotalSize())
                        break;
                } while(true);
                
                list.productFeatureList.clear();
                for(String extProductKey : toCreateProductFeatureMap.keySet()) {
                    list.productFeatureList.add(toCreateProductFeatureMap.get(extProductKey));
                }
                createProductFeature(list);
            }
        }
        catch(Exception ex) {
            //ex.printStackTrace();
            apiResult.setError(ex.getMessage());
            return apiResult.getResult();
        }
        return apiResult.getResult();
	}
	
	public void updateProductFeature(ProductFeature productFeature, Long userId, String locale, ProductFeatureInput input) throws Exception {
	    if(productFeature == null || userId == null || locale == null || input == null)
	        throw new Exception("Invalid productFeatureId.");
	    
	    input.setUserId(userId);
	    input.setUserLocale(locale);
	    ProductFeature pf = copyProductFeature(input, productFeature);
	    ProductFeature updatedPf = productFeatureDao.update(pf);
	    if(updatedPf == null)
	        throw new Exception("Unknown error.");
	}
	
	public void createProductFeature(ProductFeatureListInput inputList) throws Exception {
	    List<ProductFeature> toCreate = new ArrayList<ProductFeature>();
	    for(ProductFeatureInput input : inputList.productFeatureList) {
	        input.userToken = inputList.token;
	        input.userEmail = inputList.email;
	        ProductFeature pf = toProductFeature(input);
	        toCreate.add(pf);
	    }
	    productFeatureDao.batchCreate(toCreate);
	}
	
	public Resolution delete() {
	    Set<String> extProductIdSet;
	    MsrApiResult apiResult = new MsrApiResult();
	    if(extProductIds == null || extProductIds.length() <= 0) {
	        if(extProductId == null || extProductId.length() <= 0) {
                apiResult.setError("Invalid external product Id format.");
                return apiResult.getResult();
	        }
	        else {
	            extProductIdSet = new HashSet<String>();
	            extProductIdSet.add(extProductId);
	        }
	    }
	    else {
            try {
                extProductIdSet = objectMapper.readValue(extProductIds, new TypeReference<Set<String>>() {});
            } catch (IOException e) {
                apiResult.setError(e.getMessage());
                return apiResult.getResult();
            }
	    }
	    
	    deleteByExtProductIds(extProductIdSet);
	    return apiResult.getResult();
	}
	
	public Resolution deleteByEmail() {
	    MsrApiResult apiResult = new MsrApiResult();
	    try {
            Pair<Long, String> userIdLocaleMap = getUserIdLocalePair(null, email);
            productFeatureDao.bacthDeleteByUserId(userIdLocaleMap.getLeft());
        } catch (Exception e) {
            apiResult.setError(e.getMessage());
            return apiResult.getResult();
        }
	    return apiResult.getResult();
	}
	
	public void deleteByExtProductIds(Set<String> extProductIdSet) {
        productFeatureDao.bacthDeleteByExtProductIds(extProductIdSet);
    }
    
    public static class ProductFeatureListInput {
        public String token;
        public String email;
        public List<ProductFeatureInput> productFeatureList;
        
        public ProductFeatureListInput() {
            
        }
        
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<ProductFeatureInput> getProductFeatureList() {
            return productFeatureList;
        }

        public void setProductFeatureList(List<ProductFeatureInput> productFeatureList) {
            this.productFeatureList = productFeatureList;
        }
    }
    
    public static class ProductFeatureInput {
        public String userToken;
        public String userEmail;
        public Long userId;
        public String userLocale;
        public String extProductId;
        public Long productIndex;
        public String productType;
        public Long typeIndex;
        public String productTitle;
        public String productDescription;
        public String imgOriginal;
        public String imgUrl;
        public Float price;
        public Date startDate;
        public Date endDate;
        public ProductFeatureMetadata metadata;
        public String appVersion = AppVersion.defaultVersionString;

        public ProductFeatureInput() {
            
        }
        
        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public String getUserId() {
            return userToken;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUserLocale() {
            return userLocale;
        }

        public void setUserLocale(String userLocale) {
            this.userLocale = userLocale;
        }
        
        public String getExtProductId() {
            return extProductId;
        }

        public void setExtProductId(String extProductId) {
            this.extProductId = extProductId;
        }

        public Long getProductIndex() {
            return productIndex;
        }

        public void setProductIndex(Long productIndex) {
            this.productIndex = productIndex;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public Long getTypeIndex() {
            return typeIndex;
        }

        public void setTypeIndex(Long typeIndex) {
            this.typeIndex = typeIndex;
        }

        public String getProductTitle() {
            return productTitle;
        }

        public void setProductTitle(String productTitle) {
            this.productTitle = productTitle;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        public String getImgOriginal() {
            return imgOriginal;
        }

        public void setImgOriginal(String imgOriginal) {
            this.imgOriginal = imgOriginal;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public Float getPrice() {
            return price;
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public ProductFeatureMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(ProductFeatureMetadata metadata) {
            this.metadata = metadata;
        }
        
        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }
    }

    public String getProductFeatureGroup() {
        return productFeatureGroup;
    }

    public void setProductFeatureGroup(String productFeatureGroup) {
        this.productFeatureGroup = productFeatureGroup;
    }

    public String getExtProductIds() {
        return extProductIds;
    }

    public void setExtProductIds(String extProductIds) {
        this.extProductIds = extProductIds;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getExtProductId() {
        return extProductId;
    }

    public void setExtProductId(String extProductId) {
        this.extProductId = extProductId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
