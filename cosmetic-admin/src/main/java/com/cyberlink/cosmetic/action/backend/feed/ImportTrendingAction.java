package com.cyberlink.cosmetic.action.backend.feed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.support.TransactionTemplate;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;
import com.cyberlink.cosmetic.modules.post.service.TrendingService;

@UrlBinding("/feed/importTrending.action")
public class ImportTrendingAction extends AbstractAction {
	
	@SpringBean("post.trendingRepository")
	private TrendingRepository trendingRepository;
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	@SpringBean("circle.circleTypeDao") 
    private CircleTypeDao circleTypeDao;
	
	@SpringBean("core.jdbcTemplate")
    private TransactionTemplate transactionTemplate;
	
	@SpringBean("post.trendingService")
    private TrendingService trendingService;
	
	private String selRegion = "en_US";
    private Long selCircleTypeId = 0L;
	private List<String> availableRegion = new ArrayList<String>(0);
    private List<CircleType> availableCircleTypes = new ArrayList<CircleType>(0);
	
	@DefaultHandler
    public Resolution route() {
		loadAvailableRegion();
		loadAvailableCircleType();
		return forward();
	}
	
	 public Resolution importTrend() {
		 trendingService.importToPostScoreTrend(selRegion, selCircleTypeId);
		 return json("done");
	 }
	
	private void loadAvailableRegion() {
        availableRegion.clear();
        availableRegion.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
    }
    
    private void loadAvailableCircleType() {
        List<String> locales = new ArrayList<String>();
        locales.add(selRegion);
        availableCircleTypes.addAll(circleTypeDao.listTypesByLocales(locales, null, new BlockLimit(0, 100)).getResults());
    }

	public String getSelRegion() {
		return selRegion;
	}

	public void setSelRegion(String selRegion) {
		this.selRegion = selRegion;
	}

	public Long getSelCircleTypeId() {
		return selCircleTypeId;
	}

	public void setSelCircleTypeId(Long selCircleTypeId) {
		this.selCircleTypeId = selCircleTypeId;
	}

	public List<String> getAvailableRegion() {
		return availableRegion;
	}

	public void setAvailableRegion(List<String> availableRegion) {
		this.availableRegion = availableRegion;
	}

	public List<CircleType> getAvailableCircleTypes() {
		return availableCircleTypes;
	}

	public void setAvailableCircleTypes(List<CircleType> availableCircleTypes) {
		this.availableCircleTypes = availableCircleTypes;
	}
    
}