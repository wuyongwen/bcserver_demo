package com.cyberlink.cosmetic.action.api.post;

import com.cyberlink.cosmetic.action.api.AbstractMsrAction;
import com.cyberlink.cosmetic.modules.post.model.Horoscope;
import com.cyberlink.cosmetic.modules.post.dao.HoroscopeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/post/horoscopeManage.action")
public class HoroscopeManageAction extends AbstractMsrAction {

	@SpringBean("post.HoroscopeDao")
	private HoroscopeDao horoscopeDao;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
	private Long horoscopeId;
	private String locale;
	private Long postId;
	private String title = "";
	private String imageUrl = "";
	
	@Validate(required = true, on = "update")
	public void setHoroscopeId(Long horoscopeId) {
		this.horoscopeId = horoscopeId;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Resolution create() {
		
		MsrApiResult apiResult = new MsrApiResult();
		
		if (!postDao.exists(postId)) {
			apiResult.setError("Invalid postId.");
			return apiResult.getResult();
		}

		Locale validLocale = localeDao.getAvailableInputLocale(locale);
		Horoscope newHoroscope = new Horoscope();
		newHoroscope.setLocale(validLocale.getInputLocale());
		newHoroscope.setPostId(postId);
		newHoroscope.setTitle(title);
		newHoroscope.setImageUrl(imageUrl);
		Horoscope horoscope = horoscopeDao.create(newHoroscope);
		if (horoscope != null)
			apiResult.Add("id", horoscope.getId());
		return apiResult.getResult();
	}
	
	public Resolution update() {
		MsrApiResult apiResult = new MsrApiResult();
		Horoscope refHoroscope = horoscopeDao.findById(horoscopeId);
		if (refHoroscope == null) {
			apiResult.setError("Invalid horoscopeId.");
			return apiResult.getResult();
		}

		if (locale != null) {
			Locale validLocale = localeDao.getAvailableInputLocale(locale);
			refHoroscope.setLocale(validLocale.getInputLocale());
		}
		if (postId != null) {
			if (!postDao.exists(postId)) {
				apiResult.setError("Invalid postId.");
				return apiResult.getResult();
			}
			refHoroscope.setPostId(postId);
		}
		if (title != null)
			refHoroscope.setTitle(title);
		if (imageUrl != null)
			refHoroscope.setImageUrl(imageUrl);
		horoscopeDao.update(refHoroscope);
		return apiResult.getResult();
	}
}
