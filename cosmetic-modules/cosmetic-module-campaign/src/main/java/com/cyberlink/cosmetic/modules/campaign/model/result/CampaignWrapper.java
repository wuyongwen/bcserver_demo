package com.cyberlink.cosmetic.modules.campaign.model.result;

import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.campaign.model.Campaign;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.fasterxml.jackson.annotation.JsonView;

public class CampaignWrapper {
	Campaign compaign;
	File cover_1080;
	File cover_720;
	public CampaignWrapper(Campaign compaign)
    {
        this.compaign = compaign;
        this.cover_1080 = compaign.getCover_1080();
        this.cover_720 = compaign.getCover_720();
    }

	@JsonView(Views.Simple.class)
    public String getLink() {
        return compaign.getLink();
    }
    
	@JsonView(Views.Simple.class)
    public Long getCoverId_1080() {
		if (cover_1080 != null)
			return cover_1080.getId();
		return null;
    }

	@JsonView(Views.Simple.class)
    public String getCoverURL_1080() {
		if (cover_1080 != null) {
			List<FileItem> list = cover_1080.getFileItems();
			if (list.size() > 0) {
				return list.get(0).getOriginalUrl();				
			}
		}
		return null;
    }

	@JsonView(Views.Simple.class)
    public Long getCoverId_720() {
		if (cover_720 != null)
			return cover_720.getId();
		return null;
    }

	@JsonView(Views.Simple.class)
    public String getCoverURL_720() {
		if (cover_720 != null) {
			List<FileItem> list = cover_720.getFileItems();
			if (list.size() > 0) {
				return list.get(0).getOriginalUrl();				
			}
		}
		return null;
    }
	
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Public.class)
    public Date getEndDate() {
		return compaign.getEndDate();
    }
}
