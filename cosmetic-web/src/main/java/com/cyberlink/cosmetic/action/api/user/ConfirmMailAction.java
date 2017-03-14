package com.cyberlink.cosmetic.action.api.user;


import java.util.List;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.ApiPageLang;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.error.ErrorResolution;

@UrlBinding("/api/user/confirm-mail.action")
public class ConfirmMailAction extends SignInAction {

    @SpringBean("user.MemberDao")
    private MemberDao memberDao;

    private Long memberId = null;
	private String email;
	private String activateCode;
	private ApiPageLang pageLang;
	
	private void initLangString() {
		if (pageLang == null)
			pageLang = LanguageCenter.getApiPageLang(locale);
	}
	
	/* Remove 1*/
	public String getDeactivateTitleLabel() {
	    String deactivateTitleLabel;
	    switch(locale)
        {
        case "de_DE":
            deactivateTitleLabel = "Account deaktivieren";
            break;
        case "fr_FR":
            deactivateTitleLabel = "Désactivation de votre compte";
            break;
        case "zh_TW":
            deactivateTitleLabel = "停用帳號";
            break;
        case "ja_JP":
            deactivateTitleLabel = "アカウントの解除";
            break;
        case "zh_CN":
            deactivateTitleLabel = "停用账号";
            break;
        case "it_IT":
            deactivateTitleLabel = "Disattiva il Profilo";
            break;
        case "es_ES":
            deactivateTitleLabel = "Desactivar tu Cuenta ";
            break;
        case "ko_KR":
            deactivateTitleLabel = "탈퇴하기";
            break;
        case "ru_RU":
            deactivateTitleLabel = "Деактивировать аккаунт";
            break;
        default:
            deactivateTitleLabel = "Deactivate Account";
            break;
        }
	    return deactivateTitleLabel;
	}
	
	public String getDeactivateContentLabel1() {
        String deactivateContentLabel1;
        switch(locale)
        {
        case "de_DE":
            deactivateContentLabel1 = "Die hast die Deaktivierung Deines Accounts mit der E-Mail-Adresse ";
            break;
        case "fr_FR":
            deactivateContentLabel1 = "Vous venez de demander la désactivation de votre compte Sphère Beauté avec votre e-mail ";
            break;
        case "zh_TW":
            deactivateContentLabel1 = "你要求透過e-mail：";
            break;
        case "ja_JP":
            deactivateContentLabel1 = "ご登録いただいたメールアドレス";
            break;
        case "zh_CN":
            deactivateContentLabel1 = "你要求透过e-mail：";
            break;
        case "it_IT":
            deactivateContentLabel1 = "Hai richiesto di disattivare il tuo profilo Beauty Circle usando l'indirizzo email ";
            break;
        case "es_ES":
            deactivateContentLabel1 = "Estás solicitando desactivar tu cuenta de Beauty Circle usando este correo electrónico ";
            break;
        case "ko_KR":
            deactivateContentLabel1 = "고객님의 Beauty Circle 계정 ";
            break;
        case "ru_RU":
            deactivateContentLabel1 = "";
            break;
        default:
            deactivateContentLabel1 = "You are requesting to deactivate your Beauty Circle account using the e-mail ";
            break;
        }
        return deactivateContentLabel1;
    }
	
	public String getDeactivateContentLabel2() {
        String deactivateContentLabel2;
        switch(locale)
        {
        case "de_DE":
            deactivateContentLabel2 = " beantragt. Möchtest Du fortfahren?";
            break;
        case "fr_FR":
            deactivateContentLabel2 = ". Souhaitez-vous continuer?";
            break;
        case "zh_TW":
            deactivateContentLabel2 = "，停用玩美圈帳號。你仍想要繼續？";
            break;
        case "ja_JP":
            deactivateContentLabel2 = "でビューティーサークルのアカウント解除を行います。解除を実行する場合は下記「アカウントの解除」をクリックしてください。";
            break;
        case "zh_CN":
            deactivateContentLabel2 = "，停用玩美圈账号。你仍想要继续？";
            break;
        case "it_IT":
            deactivateContentLabel2 = "Vuoi procedere?";
            break;
        case "es_ES":
            deactivateContentLabel2 = ". ¿Deseas continuar?";
            break;
        case "ko_KR":
            deactivateContentLabel2 = "의 사용 중지를 원하시나요?";
            break;
        case "ru_RU":
            deactivateContentLabel2 = "";
            break;
        default:
            deactivateContentLabel2 = ". Do you want to continue?";
            break;
        }
        return deactivateContentLabel2;
    }
	
	public String getDeactivateButtonLabel() {
        String deactivateButtonLabel;
        switch(locale)
        {
        case "de_DE":
            deactivateButtonLabel = "Account deaktivieren";
            break;
        case "fr_FR":
            deactivateButtonLabel = "Désactivation de votre compte";
            break;
        case "zh_TW":
            deactivateButtonLabel = "停用帳號";
            break;
        case "ja_JP":
            deactivateButtonLabel = "アカウントを解除する";
            break;
        case "zh_CN":
            deactivateButtonLabel = "停用账号";
            break;
        case "it_IT":
            deactivateButtonLabel = "Disattiva il Profilo";
            break;
        case "es_ES":
            deactivateButtonLabel = "Desactivar tu Cuenta ";
            break;
        case "ko_KR":
            deactivateButtonLabel = "계정 사용 중지";
            break;
        case "ru_RU":
            deactivateButtonLabel = "Деактивировать аккаунт";
            break;
        default:
            deactivateButtonLabel = "Deactivate Account";
            break;
        }
        return deactivateButtonLabel;
    }
	/* End Remove 1*/
	
	/* Remove 2*/
	public String getDeactivatedTitleLabel() {
	    String deactivatedTitleLabel;
	    switch(locale)
        {
        case "de_DE":
            deactivatedTitleLabel = "Account deaktivieren";
            break;
        case "fr_FR":
            deactivatedTitleLabel = "Désactivation de votre compte";
            break;
        case "zh_TW":
            deactivatedTitleLabel = "停用帳號";
            break;
        case "ja_JP":
            deactivatedTitleLabel = "アカウントを解除する";
            break;
        case "zh_CN":
            deactivatedTitleLabel = "停用账号";
            break;
        case "it_IT":
            deactivatedTitleLabel = "Disattiva il Profilo";
            break;
        case "es_ES":
            deactivatedTitleLabel = "Desactivar tu Cuenta ";
            break;
        case "ko_KR":
            deactivatedTitleLabel = "계정 사용 중지";
            break;
        case "ru_RU":
            deactivatedTitleLabel = "Деактивировать аккаунт";
            break;
        default:
            deactivatedTitleLabel = "The address ";
            break;
        }
	    return deactivatedTitleLabel;
	}
	
	public String getDeactivatedContentLabel1() {
        String deactivatedContentLabel1;
        switch(locale)
        {
        case "de_DE":
            deactivatedContentLabel1 = "Die E-Mail-Adresse ";
            break;
        case "fr_FR":
            deactivatedContentLabel1 = "L'adresse e-mail ";
            break;
        case "zh_TW":
            deactivatedContentLabel1 = "帳號";
            break;
        case "ja_JP":
            deactivatedContentLabel1 = "ご登録いただいたメールアドレス ";
            break;
        case "zh_CN":
            deactivatedContentLabel1 = "账号";
            break;
        case "it_IT":
            deactivatedContentLabel1 = "L'indirizzo email ";
            break;
        case "es_ES":
            deactivatedContentLabel1 = "La dirección ";
            break;
        case "ko_KR":
            deactivatedContentLabel1 = "";
            break;
        case "ru_RU":
            deactivatedContentLabel1 = "Эл. адрес ";
            break;
        default:
            deactivatedContentLabel1 = "The address ";
            break;
        }
        return deactivatedContentLabel1;
    }
	
	public String getDeactivatedContentLabel2() {
        String deactivatedContentLabel2;
        switch(locale)
        {
        case "de_DE":
            deactivatedContentLabel2 = " wurde vom Beauty Circle entfernt.";
            break;
        case "fr_FR":
            deactivatedContentLabel2 = " a été effacé de Sphère Beauté.";
            break;
        case "zh_TW":
            deactivatedContentLabel2 = "已從玩美圈移除。";
            break;
        case "ja_JP":
            deactivatedContentLabel2 = "がビューティーサークルのアカウントより削除されました。";
            break;
        case "zh_CN":
            deactivatedContentLabel2 = "已从玩美圈移除。";
            break;
        case "it_IT":
            deactivatedContentLabel2 = " è stato rimosso dal Beauty Circle.";
            break;
        case "es_ES":
            deactivatedContentLabel2 = " ha sido eliminada del sistema de Beauty Circle. ";
            break;
        case "ko_KR":
            deactivatedContentLabel2 = " 계정이 성공적으로 탈퇴되었습니다.";
            break;
        case "ru_RU":
            deactivatedContentLabel2 = " удален из Beauty Circle.";
            break;
        default:
            deactivatedContentLabel2 = " has been removed from Beauty Circle. ";
            break;
        }
        return deactivatedContentLabel2;
    }
	
	public String getFooterLabel() {
		if (pageLang == null)
			return "";
		return pageLang.getCopyRight();
    }
    /* End Remove 2*/
	
	public String getActivateCode() {
		return activateCode;
	}

	public void setActivateCode(String activateCode) {
		this.activateCode = activateCode;
	}

	public void setMemberId(Long memberId) {
	    this.memberId = memberId;
	}
	
	public String getEmail() {
	    return email;
	}
	
	public Long getMemberId() {
	    return memberId;
	}
	
	@DefaultHandler
    public Resolution confirm2() {
		initLangString();
		
	    if(memberId == null)
	        return new ErrorResolution(ErrorDef.InvalidAccount);
	    if(!memberDao.exists(memberId))
	        return new ErrorResolution(ErrorDef.InvalidAccount);
	    if(activateCode == null)
	        return new ErrorResolution(ErrorDef.InvalidToken);

	    Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
	    Account userAccount = userMember.getAccount();
	    email = userAccount.getAccount();
	    if (!activateCode.equals(userMember.getActivateCode()))
	    	return new ErrorResolution(ErrorDef.InvalidToken);
        if (userAccount.getUserId() == null) {
        	locale = "de_DE";
        	User user = createNewUser();
        	userAccount.setUserId(user.getId());
        	userAccount = accountDao.update(userAccount);
        }
        return new ForwardResolution("/api/user/confirm2_deu.jsp");
    }
	
	public Resolution remove1() {
		initLangString();
		
	    if(memberId == null)
            return new ErrorResolution(ErrorDef.InvalidAccount);
        if(!memberDao.exists(memberId))
            return new ErrorResolution(ErrorDef.InvalidAccount);
        Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
        Account userAccount = userMember.getAccount();
        email = userAccount.getAccount();
        return new ForwardResolution("/api/user/remove1.jsp");
	}
	
	public Resolution remove2() {
		initLangString();
		
        if(memberId == null)
            return new ErrorResolution(ErrorDef.InvalidAccount);
        if(!memberDao.exists(memberId))
            return new ErrorResolution(ErrorDef.InvalidAccount);
        
        Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
        Account userAccount = userMember.getAccount();
        email = userAccount.getAccount();
		if (userAccount != null) {
			List<Session> sessionList = sessionDao.findByUserId(userAccount.getUserId());
			for (Session session : sessionList) {
				session.setStatus(SessionStatus.Invalied);
				sessionDao.update(session);
			}
			userAccount.setAccount(null);
			accountDao.update(userAccount);
		}
		userMember.setIsDeleted(Boolean.TRUE);
		userMember = memberDao.update(userMember);
		return new ForwardResolution("/api/user/remove2.jsp");
    }
}
