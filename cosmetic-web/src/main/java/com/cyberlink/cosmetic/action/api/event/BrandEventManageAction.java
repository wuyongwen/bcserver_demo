package com.cyberlink.cosmetic.action.api.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractMsrAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.ApplyType;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventAttr;
import com.cyberlink.cosmetic.modules.event.model.EventType;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.event.model.Stores;
import com.cyberlink.cosmetic.modules.event.model.Stores.Store;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/event/BrandEventManage.action")
public class BrandEventManageAction extends AbstractMsrAction {

    @SpringBean("event.BrandEventDao")
    private BrandEventDao brandEventDao;
	
    @SpringBean("event.EventUserDao")
    private EventUserDao eventUserDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    @SpringBean("file.storageService")
    private StorageService storageService;
    
    private String brandEventGroups;
    private String locale;
    private String brandEventIds;
    
    private FileBean beJZipFile;

    public Resolution createOrUpdate() {
        MsrApiResult apiResult = new MsrApiResult();
        if(brandEventGroups == null) {
            apiResult.setError("Invalid brand event group format.");
            return apiResult.getResult();
        }    
        
        List<BrandEventGroup> tmp;
        try {
            tmp = objectMapper.readValue(brandEventGroups, new TypeReference<List<BrandEventGroup>>() {});
        } catch (IOException e) {
        	//logger.error(e.getMessage());
            apiResult.setError(e.getMessage());
            return apiResult.getResult();
        }
        return createOrUpdate(tmp);
    }
    
    private Resolution createOrUpdate(List<BrandEventGroup> tmp) {
        MsrApiResult apiResult = new MsrApiResult();
        if(tmp == null) {
            apiResult.setError("Invalid brand event group format.");
            return apiResult.getResult();
        }    
        
        try {
            for(BrandEventGroup list : tmp) {
                User user = getUserByEmail(list.email);
                Set<Long> inputEventIds = new HashSet<Long>();
                Map<Long, BrandEventInput> toCreateBrandEventMap = new HashMap<Long, BrandEventInput>();
                for(BrandEventInput input : list.brandEvents) {
                    if(input.brandEventId == null)
                        continue;
                    inputEventIds.add(input.brandEventId);
                    toCreateBrandEventMap.put(input.brandEventId, input);
                }

                int offset = 0;
                int limit = 100;
                do {
                    BlockLimit blockLimit = new BlockLimit(offset, limit);
                    PageResult<Pair<Long, BrandEvent>> pResult = brandEventDao.findByEventIds(user.getId(), inputEventIds, blockLimit);
                    if(pResult.getResults().size() <= 0)
                        break;
                    for(Pair<Long, BrandEvent> pair : pResult.getResults()) {
                        BrandEventInput beInput = toCreateBrandEventMap.get(pair.getLeft());
                        toCreateBrandEventMap.remove(pair.getLeft());
                        updateBrandEvent(pair.getRight(), user.getId(), beInput.getLocale(), beInput);
                    }
                    
                    offset += limit;
                    if(offset > pResult.getTotalSize())
                        break;
                } while(true);
                
                list.brandEvents.clear();
                for(Long eventKey : toCreateBrandEventMap.keySet()) {
                    list.brandEvents.add(toCreateBrandEventMap.get(eventKey));
                }
                createBrandEvent(list);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            apiResult.setError(ex.getMessage());
            return apiResult.getResult();
        }
        return apiResult.getResult();
    }
    
    public void updateBrandEvent(BrandEvent brandEvent, Long userId, String locale, BrandEventInput input) throws Exception {
        if(brandEvent == null || userId == null || locale == null || input == null)
            throw new Exception("Invalid productFeatureId.");
        
        input.setUserId(userId);
        input.setLocale(locale);
        BrandEvent be = copyBrandEvent(input, brandEvent);
        BrandEvent updatedBe = brandEventDao.update(be);
        if(updatedBe == null)
            throw new Exception("Unknown error.");
    }
    
    public void createBrandEvent(BrandEventGroup inputList) throws Exception {
        List<BrandEvent> toCreate = new ArrayList<BrandEvent>();
        for(BrandEventInput input : inputList.brandEvents) {
            input.email = inputList.email;
            BrandEvent be = toBrandEvent(input);
            toCreate.add(be);
        }
        brandEventDao.batchCreate(toCreate);
    }
    
    BrandEvent copyBrandEvent(BrandEventInput input, BrandEvent be) throws Exception {
        if(input.userId != null && input.locale != null) {
            be.setBrandId(input.userId);
            be.setLocale(input.locale);
        }
        else if(input.email != null) {
            User user = getUserByEmail(input.email);
            be.setBrandId(user.getId());
            be.setLocale(input.locale);
        }
            
        if(input.brandEventId != null)
            be.setId(input.brandEventId);
        be.setTitle(input.getTitle());
        be.setDescription(input.getDescription());
        EventAttr eventAttr = input.getEventAttr();
        if(eventAttr == null) {
            eventAttr = new EventAttr();
            eventAttr.setStartTime(input.getStartDate());
            eventAttr.setEndTime(input.getEndDate());
            eventAttr.setDrawTime(input.getDrawDate());
        }
        be.setEventAttrJNode(eventAttr);
        be.setQuantity(input.getJoinLimit());
        be.setApplyType(input.getApplyType());
        be.setEventType(input.getEventType());
        be.setReceiveType(input.getReceiveType());
        be.setReceiveTemplate(objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(input.getReceiveTemplate()));
        List<Stores> storeMap = getStores(input.getStoreAddress());
        if(storeMap != null)
            be.setStoresValue(objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(storeMap));
        else
            be.setStoresValue(null);
        be.setComment(input.getRemarks());
        EventBanner banner = input.getBanner();
        if(banner != null) {
            be.setImageUrl(banner.getImageUrl());
            be.setEventLink(banner.getEventUrl());
        }
        EventProduct product = input.getProduct();
        if(product != null) {
            be.setProdName(product.getName());
            be.setProdAttribute(objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(product.getProdAttr()));
            be.setProdDescription(product.getDescription());
            be.setProdDetail(product.getDetail());
        }
        be.setPipedaLink(input.getPipedaLink());
        be.setPriority(input.getPriority());
        be.setServiceType(input.getServiceType());
        be.setCouponCode(input.getCouponCode());
        be.setWebsiteUrl(input.getWebsiteUrl());
        be.setMetadata(input.getMetadata());
        return be;
    }
    
    BrandEvent toBrandEvent(BrandEventInput input) throws Exception {
        BrandEvent be = new BrandEvent();
        return copyBrandEvent(input, be);
    }
    
    BrandEventInput toBrandEventInput(BrandEvent be) throws Exception {
        BrandEventInput input = new BrandEventInput();
        input.setUserId(be.getBrandId());
        input.setLocale(be.getLocale());
        input.setBrandEventId(be.getId());
        input.setTitle(be.getTitle());
        input.setDescription(be.getDescription());
        EventAttr eventAttr = be.getEventAttrJNode();
        if(eventAttr == null) {
            eventAttr = new EventAttr();
            eventAttr.setStartTime(be.getStartTime());
            eventAttr.setEndTime(be.getEndTime());
            eventAttr.setDrawTime(be.getDrawTime());
        }
        input.setEventAttr(eventAttr);
        input.setJoinLimit(be.getQuantity());
        input.setApplyType(be.getApplyType());
        input.setEventType(be.getEventType());
        input.setReceiveType(be.getReceiveType());
        if(be.getReceiveTemplate() != null && be.getReceiveTemplate().length() >= 0) {
            ReceiveTemplete template = objectMapper.readValue(be.getReceiveTemplate(), new TypeReference<ReceiveTemplete>() {});
            input.setReceiveTemplate(template);
        }
        
        if(be.getStoreAddress() != null && be.getStoreAddress().length() >= 0) {
            Map<String, List<Store>> stores = objectMapper.readValue(be.getStoreAddress(), new TypeReference<Map<String, List<Store>>>() {});
            input.setStores(stores);
        }
        input.setRemarks(be.getComment());
        EventBanner banner = new EventBanner();
        banner.setImageUrl(be.getImageUrl());
        banner.setEventUrl(be.getEventLink());
        input.setBanner(banner);
        EventProduct product = new EventProduct();
        product.setName(be.getProdName());
        if(be.getProdAttribute() != null) {
            EventProductAttr prodAttr = objectMapper.readValue(be.getProdAttribute(), EventProductAttr.class);
            product.setProdAttr(prodAttr);
        }
        product.setDescription(be.getProdDescription());
        product.setDetail(be.getProdDetail());
        input.setProduct(product);
        input.setPipedaLink(be.getPipedaLink());
        input.setPriority(be.getPriority());
        input.setServiceType(be.getServiceType());
        input.setMetadata(be.getMetadata());
        return input;
    }
    
    public Resolution list() throws Exception {
        MsrApiResult apiResult = new MsrApiResult();
        List<BrandEvent> beventList = brandEventDao.listBrandEvent(locale, ServiceType.FREE_SAMPLE);
        Date currentTime = new Date();
        apiResult.Add("currentTime", currentTime.getTime());
        apiResult.Add("results", beventList);
        apiResult.Add("totalSize", beventList.size());
        return apiResult.getResult();
    }
    
    public Resolution delete() {
        Set<Long> eventIdSet;
        MsrApiResult apiResult = new MsrApiResult();
        if(brandEventIds != null && brandEventIds.length() > 0) {
            try {
                eventIdSet = objectMapper.readValue(brandEventIds, new TypeReference<Set<Long>>() {});
            } catch (IOException e) {
            	//logger.error(e.getMessage());
                apiResult.setError(e.getMessage());
                return apiResult.getResult();
            }
        }
        else {
            apiResult.setError("Invalid event Id format.");
            return apiResult.getResult();
        }
        
        deleteByEventIds(eventIdSet);
        return apiResult.getResult();
    }

    public Resolution availableLocale() {
        MsrApiResult apiResult = new MsrApiResult();
        apiResult.Add("locales", localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE));
        return apiResult.getResult();
    }
    
    public void deleteByEventIds(Set<Long> eventIdSet) {
        brandEventDao.bacthDeleteByEventIds(eventIdSet);
    }
    
    private void deletePath(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
        	logger.error(e.getMessage());
        }
    }
    public Resolution importEvent() {
        String outputPath = Constants.getStorageLocalRoot() + "/" + beJZipFile.getFileName() + "/"; 
        Throwable error = null;
        List<BrandEventGroup> inputList = null;
        try {
            unzip(beJZipFile.getInputStream(), outputPath);
            InputStreamReader fileStream = new InputStreamReader(new FileInputStream(outputPath + "event.json"), "UTF-8");
            inputList = objectMapper.readValue(fileStream, new TypeReference<List<BrandEventGroup>>() {});
            if(inputList == null) {
                error = new Throwable("Invalid json file");
            }
            else {
                for(BrandEventGroup beg : inputList) {
                    List<BrandEventInput> input = beg.getBrandEvents();
                    for(BrandEventInput bei : input) {
                        EventBanner ban = bei.getBanner();
                        if(ban.getImageUrl() != null) {
                            if(!ban.getImageUrl().startsWith("http://") && !ban.getImageUrl().startsWith("https://")) {
                                File ioFile = new File(outputPath + ban.getImageUrl());
                                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                                String mimeType = fileNameMap.getContentTypeFor(ban.getImageUrl());
                                String bannerImgUrl = storageService.uploadFile(ioFile, "event/banner/", mimeType);
                                ban.setImageUrl(bannerImgUrl);
                            }
                        }
                        
                        EventProduct ep = bei.getProduct();
                        if(ep != null) {
                            EventProductAttr eps = ep.getProdAttr();
                            if(eps != null) {
                                if(eps.getThumbnailUrl() != null) {
                                    if(!eps.getThumbnailUrl().startsWith("http://") && !eps.getThumbnailUrl().startsWith("https://")) {
                                        File ioFile = new File(outputPath + eps.getThumbnailUrl());
                                        FileNameMap fileNameMap = URLConnection.getFileNameMap();
                                        String mimeType = fileNameMap.getContentTypeFor(eps.getThumbnailUrl());
                                        String productImgUrl = storageService.uploadFile(ioFile, "event/product/", mimeType);
                                        eps.setThumbnailUrl(productImgUrl);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            error = e;
        } finally {
            deletePath(outputPath);
        }
        
        if(error != null)
            return new ErrorResolution(400, error.getMessage());
        
        return createOrUpdate(inputList);
    }
    
    private Boolean unzip(InputStream zipFileStream, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);
            if(!folder.exists()) {
                folder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(zipFileStream);
            ZipEntry ze = zis.getNextEntry();
 
            while(ze!=null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);             
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
    
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();    
       } catch(IOException ex) {
           ex.printStackTrace(); 
           return false;
       }  
       return true;
    }
    
    public static class EventBanner {
        public String imageUrl;
        public String eventUrl;
        
        public EventBanner() {
            
        }
        
        public String getImageUrl() {
            return imageUrl;
        }
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public String getEventUrl() {
            return eventUrl;
        }
        public void setEventUrl(String eventUrl) {
            this.eventUrl = eventUrl;
        }
    }
    
    public static class EventProductAttr {
        @JsonView(Views.Public.class)
        public String bcProductId;
        @JsonView(Views.Public.class)
        public String thumbnailUrl;
        @JsonView(Views.Public.class)
        public String productLink;
        @JsonView(Views.Public.class)
        public String brandName;
        @JsonView(Views.Public.class)
        public String category;
        @JsonView(Views.Public.class)
        public String name;
        @JsonView(Views.Public.class)
        public String price;
        
        public EventProductAttr() {
            
        }
        
        public String getBcProductId() {
            return bcProductId;
        }
        public void setBcProductId(String bcProductId) {
            this.bcProductId = bcProductId;
        }
        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }
        public String getProductLink() {
            return productLink;
        }
        public void setProductLink(String productLink) {
            this.productLink = productLink;
        }
        public String getBrandName() {
            return brandName;
        }
        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
    }
    
    public static class EventProduct {
        public String name;
        public String description;
        public String detail;
        public EventProductAttr prodAttr;
        
        public EventProduct() {
            
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public EventProductAttr getProdAttr() {
            return prodAttr;
        }

        public void setProdAttr(EventProductAttr prodAttr) {
            this.prodAttr = prodAttr;
        }

        public String getDescription() {
            return StringEscapeUtils.unescapeJava(description);
        }

        public void setDescription(String description) {
            this.description = StringEscapeUtils.escapeJava(description);
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
    
    public static class ReceiveTemplete {
        @JsonView(Views.Public.class)
        public String message;
        
        @JsonView(Views.Public.class)
        public String footer;
        
        public ReceiveTemplete() {
            
        }
        
        public String getMessage() {
            return StringEscapeUtils.unescapeJava(message);
        }
        public void setMessage(String message) {
            this.message = StringEscapeUtils.escapeJava(message);
        }
        public String getFooter() {
            return StringEscapeUtils.unescapeJava(footer);
        }
        public void setFooter(String footer) {
            this.footer = StringEscapeUtils.escapeJava(footer);
        }
    }
    
    public static class BrandEventGroup {
        public String email;
        public Long userId;
        public List<BrandEventInput> brandEvents;
        
        public BrandEventGroup() {
            
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        public List<BrandEventInput> getBrandEvents() {
            return brandEvents;
        }
        public void setBrandEvents(List<BrandEventInput> brandEvents) {
            this.brandEvents = brandEvents;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrandEventInput {
        public Long brandEventId;
        public String email;
        public String locale;
        public Long userId;
        
        public String title;
        public String description;
        public Date startDate;
        public Date endDate;
        public Date drawDate;
        public EventAttr eventAttr;
        public Long joinLimit;
        public ApplyType applyType;
        public EventType eventType;
        public ReceiveType receiveType;
        public ReceiveTemplete receiveTemplate;
        public Map<String, List<Store>> stores;
        public String remarks;
        public EventBanner banner;
        public EventProduct product;
        public String pipedaLink;
        public Integer priority;
        public ServiceType serviceType = ServiceType.FREE_SAMPLE;
        public String couponCode;
        public String websiteUrl;
        public String metadata;
        
        public BrandEventInput() {
            
        }
        
        public Long getBrandEventId() {
            return brandEventId;
        }
        public void setBrandEventId(Long brandEventId) {
            this.brandEventId = brandEventId;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getLocale() {
            return locale;
        }
        public void setLocale(String locale) {
            this.locale = locale;
        }
        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getDescription() {
            return StringEscapeUtils.unescapeJava(description);
        }
        public void setDescription(String description) {
            this.description = StringEscapeUtils.escapeJava(description);
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
        public Date getDrawDate() {
            return drawDate;
        }
        public void setDrawDate(Date drawDate) {
            this.drawDate = drawDate;
        }
        public EventAttr getEventAttr() {
            return eventAttr;
        }
        public void setEventAttr(EventAttr eventAttr) {
            this.eventAttr = eventAttr;
        }
        public Long getJoinLimit() {
            return joinLimit;
        }
        public void setJoinLimit(Long joinLimit) {
            this.joinLimit = joinLimit;
        }
        public ApplyType getApplyType() {
            return applyType;
        }
        public void setApplyType(ApplyType applyType) {
            this.applyType = applyType;
        }
        public EventType getEventType() {
            return eventType;
        }
        public void setEventType(EventType eventType) {
            this.eventType = eventType;
        }
        public ReceiveType getReceiveType() {
            return receiveType;
        }
        public void setReceiveType(ReceiveType receiveType) {
            this.receiveType = receiveType;
        }
        public ReceiveTemplete getReceiveTemplate() {
            return receiveTemplate;
        }
        public void setReceiveTemplate(ReceiveTemplete receiveTemplate) {
            this.receiveTemplate = receiveTemplate;
        }
        public Map<String, List<Store>> getStoreAddress() {
            return stores;
        }
        public void setStores(Map<String, List<Store>> stores) {
            this.stores = stores;
        }
        public String getRemarks() {
            return remarks;
        }
        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
        public EventBanner getBanner() {
            return banner;
        }
        public void setBanner(EventBanner banner) {
            this.banner = banner;
        }
        public EventProduct getProduct() {
            return product;
        }
        public void setProduct(EventProduct product) {
            this.product = product;
        }
        public String getPipedaLink() {
            return pipedaLink;
        }
        public void setPipedaLink(String pipedaLink) {
            this.pipedaLink = pipedaLink;
        }
        public Integer getPriority() {
            return priority;
        }
        public void setPriority(Integer priority) {
            this.priority = priority;
        }
        public ServiceType getServiceType() {
            return serviceType;
        }
        public void setServiceType(ServiceType serviceType) {
            this.serviceType = serviceType;
        }
		public String getCouponCode() {
			return couponCode;
		}
		public void setCouponCode(String couponCode) {
			this.couponCode = couponCode;
		}
		public String getWebsiteUrl() {
			return websiteUrl;
		}
		public void setWebsiteUrl(String websiteUrl) {
			this.websiteUrl = websiteUrl;
		}
		public String getMetadata() {
			return metadata;
		}
		public void setMetadata(String metadata) {
			this.metadata = metadata;
		}
    }
    
    public String getBrandEventGroups() {
        return brandEventGroups;
    }

    public void setBrandEventGroups(String brandEventGroups) {
        this.brandEventGroups = brandEventGroups;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBrandEventIds() {
        return brandEventIds;
    }

    public void setBrandEventIds(String brandEventIds) {
        this.brandEventIds = brandEventIds;
    }
    
    public FileBean getBeJZipFile() {
        return beJZipFile;
    }

    public void setBeJZipFile(FileBean beJZipFile) {
        this.beJZipFile = beJZipFile;
    }
    
    public List<Stores> getStores(Map<String, List<Store>> stores) {
        if(stores == null)
            return null;
        
        try {
            List<Stores> tmpList = new ArrayList<Stores>();
            for(String key : stores.keySet()) {
                Stores ss = new Stores();
                ss.setCity(key);
                ss.setStores(stores.get(key));
                tmpList.add(ss);
            }
            return tmpList;
        }
        catch(Exception e) {
            return null;
        }
    }
}
