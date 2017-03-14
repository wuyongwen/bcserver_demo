package com.cyberlink.cosmetic.lang;

import com.cyberlink.cosmetic.lang.model.ApiPageLang;
import com.cyberlink.cosmetic.lang.model.ConsultationLang;
import com.cyberlink.cosmetic.lang.model.FreeSampleLang;
import com.cyberlink.cosmetic.lang.model.MailLang;
import com.cyberlink.cosmetic.lang.model.NotifyLang;

/**
 * Control all language model by LanguageCenter. If some language model only
 * supported the specific language, please handle supported language here.
 * 
 * 
 * [EXAMPLE] If NotifyLang only support ENU,
 * 	
 * 	NotifyLang getNotifyLang(String locale) {
 * 		locale = "en_US"
 * 		return new NotifyLang(locale);
 * 	}
 * 
 */

public class LanguageCenter {

	private LanguageCenter() {

	}

	public static NotifyLang getNotifyLang(String locale) {
		return new NotifyLang(locale);
	}
	
	public static MailLang getMailLang(String locale) {
		return new MailLang(locale);
	}
	
	public static FreeSampleLang getFreeSampleLang(String locale) {
		return new FreeSampleLang(locale);
	}
	
	public static ConsultationLang getConsultationLang(String locale) {
		return new ConsultationLang(locale);
	}
	
	public static ApiPageLang getApiPageLang(String locale) {
		return new ApiPageLang(locale);
	}
}