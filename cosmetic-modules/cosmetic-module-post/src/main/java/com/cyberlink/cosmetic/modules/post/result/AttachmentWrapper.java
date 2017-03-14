package com.cyberlink.cosmetic.modules.post.result;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AttachmentWrapper implements Serializable{
	private static final long serialVersionUID = -8769804369949176336L;

	@JsonView(Views.Public.class)
    public Long fileId = (long)1;
    
    @JsonView(Views.Public.class)
    public String fileType = "Photo";
    
    @JsonView(Views.Public.class)
    public Long downloadCount = (long)0;

    @JsonView(Views.Public.class)
    public String metadata;

	public  AttachmentWrapper(Long fileId, String fileType, Long downloadCount, ObjectNode metadataObj, String downloadUrl) {
		this.fileId = fileId;
		this.fileType = fileType;
		this.downloadCount = downloadCount;
        ObjectMapper mapper = new ObjectMapper();
        if(metadataObj != null) {
        	metadataObj.put("originalUrl", downloadUrl);
        	try {
        		this.metadata = mapper.writer((PrettyPrinter)null).writeValueAsString(metadataObj);
        	} catch (JsonProcessingException e) {
        	}
        } else {
        	metadata = "";
        }
	}
	public  AttachmentWrapper(Long fileId, String fileType, Long downloadCount, String metadata) {
		this.fileId = fileId;
		this.fileType = fileType;
		this.downloadCount = downloadCount;
		this.metadata = metadata;
	}

}
