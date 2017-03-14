package com.cyberlink.cosmetic.modules.post.result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.post.model.AttachmentExtLink;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostExProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostHoroscopeTag;
import com.cyberlink.cosmetic.modules.post.model.PostProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostTags;
import com.cyberlink.cosmetic.modules.post.model.PostTags.MainPostDetailView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubPostSimpleWrapper extends SubPostBaseWrapper {
    
    public static class File {
        
        /*public File(Attachment attachment, Long fileId, String serverDownloadUrl, String fileType)
        {
            metadata = attachment.getMetadata();
            attachmentId = attachment.getId();
            ObjectMapper mapper = new ObjectMapper();
            this.fileType = fileType;
            this.fileId = fileId;
            try {
                ObjectNode actualObj = (ObjectNode)mapper.readTree(metadata);
                if(actualObj == null)
                    return;
                JsonNode attrNode = actualObj.get("originalUrl");
                if(attrNode != null)
                    downloadUrl = attrNode.asText();
                else {
                    downloadUrl = serverDownloadUrl;
                    actualObj.put("originalUrl", downloadUrl);
                    metadata = mapper.writer((PrettyPrinter)null).writeValueAsString(actualObj);
                }
                attrNode = actualObj.get("redirectUrl");
                if(attrNode != null)
                    redirectUrl = attrNode.asText();
                attrNode = actualObj.get("imageDescription");
                if(attrNode != null)
                    imageDescription = attrNode.asText();                    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        
        public File() {
        }
        
        public File(FileItem fileItem) {
            ObjectMapper mapper = new ObjectMapper();
            com.cyberlink.cosmetic.modules.file.model.File file = fileItem.getFile();
            this.fileId = file.getId();
            this.fileType = file.getFileType().toString();
            ObjectNode actualObj = (ObjectNode)fileItem.getMetadataJson();
            if(actualObj == null)
                return;
            JsonNode attrNode = actualObj.get("redirectUrl");
            if(attrNode != null)
                redirectUrl = attrNode.asText();
            attrNode = actualObj.get("imageDescription");
            if(attrNode != null)
                imageDescription = attrNode.asText();     
            
            downloadUrl = fileItem.getOriginalUrl();
            actualObj.put("originalUrl", downloadUrl);
            
            try {
                metadata = mapper.writer((PrettyPrinter)null).writeValueAsString(actualObj);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        public File(AttachmentExtLink extLink) {
            ObjectMapper mapper = new ObjectMapper();
            this.fileId = null;
            this.fileType = extLink.getLinkType();
            this.metadata = extLink.getMetadata();
            ObjectNode actualObj = null;
            try {
                actualObj = (ObjectNode)mapper.readTree(this.metadata);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            if(actualObj == null)
                return;
            JsonNode attrNode = actualObj.get("redirectUrl");
            if(attrNode != null)
                redirectUrl = attrNode.asText();
            attrNode = actualObj.get("imageDescription");
            if(attrNode != null)
                imageDescription = attrNode.asText();     
            attrNode = actualObj.get("originalUrl");
            if(attrNode != null)
                downloadUrl = attrNode.asText();       
        }
        
        /*public File(Attachment attachment)
        {
            Object t = attachment.getTarget();
            if(!(t instanceof com.cyberlink.cosmetic.modules.file.model.File))
                return;
            
            com.cyberlink.cosmetic.modules.file.model.File tFile = (com.cyberlink.cosmetic.modules.file.model.File)t;
            metadata = attachment.getMetadata();
            attachmentId = attachment.getId();
            ObjectMapper mapper = new ObjectMapper();
            this.fileId = tFile.getId();
            if(this.fileId.equals((long)0)) {
                this.fileType = "Photo";
                try {
                    ObjectNode actualObj = (ObjectNode)mapper.readTree(metadata);
                    if(actualObj == null)
                        return;
                    JsonNode attrNode = actualObj.get("originalUrl");
                    if(attrNode != null)
                        downloadUrl = attrNode.asText();
                    attrNode = actualObj.get("redirectUrl");
                    if(attrNode != null)
                        redirectUrl = attrNode.asText();
                    attrNode = actualObj.get("imageDescription");
                    if(attrNode != null)
                        imageDescription = attrNode.asText();                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                this.fileType = tFile.getFileType().toString();
                try {
                    ObjectNode actualObj = (ObjectNode)mapper.readTree(metadata);
                    if(actualObj == null)
                        return;
                    JsonNode attrNode = actualObj.get("originalUrl");
                    if(attrNode != null)
                        downloadUrl = attrNode.asText();
                    else {
                        downloadUrl = tFile.getFileItems().get(0).getOriginalUrl();
                        actualObj.put("originalUrl", downloadUrl);
                        metadata = mapper.writer((PrettyPrinter)null).writeValueAsString(actualObj);
                    }
                    attrNode = actualObj.get("redirectUrl");
                    if(attrNode != null)
                        redirectUrl = attrNode.asText();
                    attrNode = actualObj.get("imageDescription");
                    if(attrNode != null)
                        imageDescription = attrNode.asText();                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        
        @JsonView(Views.Public.class)
        public Long fileId = (long)1;
        
        @JsonView(Views.Public.class)
        public String fileType = "Photo";
        
        @JsonView(Views.Public.class)
        public Long downloadCount = (long)0;
        
        private String metadata;
        //private Long attachmentId = (long)0;
        
        @JsonView(Views.Public.class)
        public String getMetadata()
        {
            return metadata;
        }
        
        private String downloadUrl = "";
        private String redirectUrl = "";
        private String imageDescription = "";
        
        public String getDownloadUrl()
        {
            return downloadUrl;
        }
        
        public String getRedirectUrl()
        {
            return redirectUrl;
        }
        
        public String getImageDescription()
        {
            return imageDescription;
        }
        
        public String getFileType(){
            return fileType;
        }
        
        public Long getFileId()
        {
            return fileId;
        }
        
        /*public Long getAttachmentId()
        {
            return attachmentId;
        }*/
    }
    
    public static class Attachments {
        
        private List<File> files;
        public Attachments()
        {
            files = new ArrayList<File>(0);
        }
        
        public void setFileItem(List<Object> objs) {
            for(Object obj : objs) {
                if(obj instanceof FileItem) {
                    FileItem fileItem = (FileItem)obj;
                    files.add(new File(fileItem));
                }
            }
        }
        
        @JsonView(Views.Public.class)
        public List<File> getFiles()
        {
            return files;
        }
    }
    
    public static class Tags {
        private List<PostProductTag> productTags = new ArrayList<PostProductTag>(0);
        private List<PostExProductTag> exProductTags = new ArrayList<PostExProductTag>();
        private String lookTag = null;
		private PostHoroscopeTag horoscopeTag = null;

		public Tags()
        {
            
        }
        
        void setProductTags(List<PostProductTag> productTags)
        {
            this.productTags = productTags;
        }

        @JsonView(Views.Public.class)
        public List<PostProductTag> getProductTags() {
            return productTags;
        }
        
        public void setExProductTags(List<PostExProductTag> exProductTags) {
            this.exProductTags = exProductTags;
        }
        
        @JsonView(MainPostDetailView.class)
        public List<PostExProductTag> getExProductTags() {
            return exProductTags;
        }
        
        void setLookTag(String lookTag)
        {
            this.lookTag = lookTag;
        }

        @JsonView(Views.Public.class)
        public String getLookTag() {
            return lookTag;
        }
        
        @JsonView(Views.Public.class)
		public PostHoroscopeTag getHoroscopeTag() {
			return horoscopeTag;
		}

		public void setHoroscopeTag(PostHoroscopeTag horoscopeTag) {
			this.horoscopeTag = horoscopeTag;
		}
        
    }
    
    private Tags tags = null;
    private Boolean isLiked = false;
    private Long likeCount = (long)0;
    private Long commentCount = (long)0;
    protected Attachments attachments;
    protected String content;
    protected String extLookUrl;

    public SubPostSimpleWrapper() {
        super();
    }
    
    public SubPostSimpleWrapper(Post subPost, List<PostProductTag> productTag, List<Object> fileItems, List<PostExProductTag> postExProductTags) {
        super(subPost);
        attachments = new Attachments();
        if(fileItems != null)
            attachments.setFileItem(fileItems);
        if((productTag != null && productTag.size() > 0) || (postExProductTags != null && postExProductTags.size() > 0)) {
            if(tags == null)
                tags = new Tags();
            tags.setProductTags(productTag);
            tags.setExProductTags(postExProductTags);
        }
        PostTags pt = subPost.getPostTags();
        if(pt != null) {
            if(tags == null)
                tags = new Tags();
    		if (pt.lookTag != null)
    			tags.setLookTag(pt.lookTag);
    		else if (pt.horoscopeTag != null)
    			tags.setHoroscopeTag(pt.horoscopeTag);
        }
        content = subPost.getContent();
        extLookUrl = subPost.getExtLookUrl();
    }
    
    @JsonView(Views.Simple.class)
    public Tags getTags() {
        return tags;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    @JsonView(Views.Simple.class)
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
    
    @JsonView(Views.Simple.class)
    public Long getCommentCount() {
        return commentCount;
    }
    
    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
    
    @JsonView(Views.Simple.class)
    public Boolean getIsLiked() {
        return isLiked;
    }
    
    @JsonView(Views.Simple.class)
    public Date getLastModified() {
        return lastModified;
    }
    
    @JsonView(Views.Simple.class)
    public Attachments getAttachments() {
        return attachments;
    }
    
    @JsonView(Views.Simple.class)
    public String getContent() {
        return content;
    }

    @JsonView(Views.Simple.class)
    public String getExtLookUrl() {
        return extLookUrl;
    }
    
	@JsonView(Views.Simple.class)
    public Long getSubPostId() {
        return subPostId;
    }
    
}
