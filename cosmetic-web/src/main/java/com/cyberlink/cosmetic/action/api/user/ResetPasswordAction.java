package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.ApiPageLang;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.utils.EncrUtil;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.error.ErrorResolution;
import com.restfb.json.JsonObject;

@UrlBinding("/api/user/reset-password.action")
public class ResetPasswordAction extends AbstractAction {

	@SpringBean("user.MemberDao")
	private MemberDao memberDao;

	@SpringBean("user.AccountDao")
	private AccountDao accountDao;

	private Long memberId = null;
	private String password;
	private String memberCode;
	private String locale = "en_US";
	private ApiPageLang pageLang;
	private static final int EXPIREDMIN = 30;

	private void initLangString() {
		if (pageLang == null)
			pageLang = LanguageCenter.getApiPageLang(locale);
	}
	
	/* MemberCod Expired */
	public String getExpiredTitle() {
		if (pageLang == null)
			return "";
		return pageLang.getRsetpasswordExpired1();
	}
	
	public String getExpiredContent() {
		if (pageLang == null)
			return "";
		return pageLang.getRsetpasswordExpired2();
	}
	
	/* Reset Password 1 */
	public String getResetPasswordLabel() {
		String resetPasswordLabel;
		switch (locale) {
		case "de_DE":
			resetPasswordLabel = "Passwort zurücksetzen";
			break;
		case "fr_FR":
			resetPasswordLabel = "Réinitialiser le mot de passe";
			break;
		case "zh_TW":
			resetPasswordLabel = "重新設定密碼";
			break;
		case "ja_JP":
			resetPasswordLabel = "パスワードのリセット";
			break;
		case "zh_CN":
			resetPasswordLabel = "重新设定密码";
			break;
		case "it_IT":
			resetPasswordLabel = "Re-imposta Password";
			break;
		case "es_ES":
			resetPasswordLabel = "Borrar Contraseña";
			break;
		case "ko_KR":
			resetPasswordLabel = "비밀번호 재설정";
			break;
		case "ru_RU":
			resetPasswordLabel = "Восстановить пароль";
			break;
		default:
			resetPasswordLabel = "Reset password";
			break;
		}
		return resetPasswordLabel;
	}

	public String getNewPasswordLabel() {
		String newPasswordLabel;
		switch (locale) {
		case "de_DE":
			newPasswordLabel = "Neues Passwort (6-20 Zeichen)";
			break;
		case "fr_FR":
			newPasswordLabel = "Nouveau mot de passe (6-20)";
			break;
		case "zh_TW":
			newPasswordLabel = "新密碼（6-20個字元）";
			break;
		case "ja_JP":
			newPasswordLabel = "新しいパスワード（6-20半角英数文字)";
			break;
		case "zh_CN":
			newPasswordLabel = "新密码（6-20个字符）";
			break;
		case "it_IT":
			newPasswordLabel = "Nuova password (6-20 caratteri)";
			break;
		case "es_ES":
			newPasswordLabel = "Nueva Contraseña (6-20)";
			break;
		case "ko_KR":
			newPasswordLabel = "새로운 비밀번호 (6-20 자)";
			break;
		case "ru_RU":
			newPasswordLabel = "Новый пароль (6- 20)";
			break;
		default:
			newPasswordLabel = "New password (6-20)";
			break;
		}
		return newPasswordLabel;
	}

	public String getRetypePasswordLabel() {
		String retypePasswordLabel;
		switch (locale) {
		case "de_DE":
			retypePasswordLabel = "Passwort wiederholen (6-20 Zeichen)";
			break;
		case "fr_FR":
			retypePasswordLabel = "Resaisir le mot de passe (6-20)";
			break;
		case "zh_TW":
			retypePasswordLabel = "重新密碼（6-20個字元）";
			break;
		case "ja_JP":
			retypePasswordLabel = "パスワードの再入力（6-20半角英数文字)";
			break;
		case "zh_CN":
			retypePasswordLabel = "重新密码（6-20个字符）";
			break;
		case "it_IT":
			retypePasswordLabel = "Re-inserisci nuova Password (6-20)";
			break;
		case "es_ES":
			retypePasswordLabel = "Re-ingresa Contraseña (6-20)";
			break;
		case "ko_KR":
			retypePasswordLabel = "새로운 비밀번호 확인 (6-20 자)";
			break;
		case "ru_RU":
			retypePasswordLabel = "Повторить пароль (6- 20)";
			break;
		default:
			retypePasswordLabel = "Re-type password (6-20)";
			break;
		}
		return retypePasswordLabel;
	}

	/* End Reset Password 1 */

	/* Reset Password 2 */
	public String getPasswordResetedLabel() {
		String passwordResetedLabel;
		switch (locale) {
		case "de_DE":
			passwordResetedLabel = "Dein Passwort wurde zurückgesetzt. Du kannst Dich jetzt wieder einloggen.";
			break;
		case "fr_FR":
			passwordResetedLabel = "Votre mot de passe a été réinitialisé. Veuillez vous connecter à nouveau.";
			break;
		case "zh_TW":
			passwordResetedLabel = "你的密碼已經重新設定，請再次登入使用。";
			break;
		case "ja_JP":
			passwordResetedLabel = "パスワードがリセットされました。再度ログインしてお確かめください。";
			break;
		case "zh_CN":
			passwordResetedLabel = "你的密码已经重新设定，请再次登入使用。";
			break;
		case "it_IT":
			passwordResetedLabel = "La tua password è stata re-impostata. Fai nuovamente il login e inizia a usarla. ";
			break;
		case "es_ES":
			passwordResetedLabel = "Tu contraseña ha sido borrada. Por favor vuelve a iniciar sesión para usarla.";
			break;
		case "ko_KR":
			passwordResetedLabel = "새로운 비밀번호로 변경 완료되었습니다. 다시 로그인 하십시오.";
			break;
		case "ru_RU":
			passwordResetedLabel = "Ваш пароль восстановлен. Войдите еще раз для активации.";
			break;
		default:
			passwordResetedLabel = "Your password has been reset. Please log in again to start using it.";
			break;
		}
		return passwordResetedLabel;
	}

	public String getFooterLabel() {
		if (pageLang == null)
			return "";
		return pageLang.getCopyRight();
	}

	/* End Reset Password 2 */

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLocale() {
		return locale;
	}

	@DefaultHandler
	public Resolution resetpwd() {
		initLangString();
		
		if (memberId == null)
			return new ErrorResolution(ErrorDef.InvalidAccount);
		if (!memberDao.exists(memberId))
			return new ErrorResolution(ErrorDef.InvalidAccount);
		Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
		try {
			if (memberCode == null
					|| !PasswordHashUtil.validatePassword(memberId + "%"
							+ userMember.getAccount().getAccount(), memberCode))
				return new ErrorResolution(ErrorDef.InvalidToken);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return new ErrorResolution(ErrorDef.InvalidToken);
		}
		
		// check expired
		String codeObjStr = userMember.getMemberCode();
		if (codeObjStr != null && !codeObjStr.isEmpty()) {
			try {
				JsonObject codeObj = new JsonObject(codeObjStr);
				if (codeObj.has(memberCode)) {
					Long sendTime = codeObj.getLong(memberCode);
					sendTime += EXPIREDMIN * 60 * 1000;
					if (sendTime.compareTo(Calendar.getInstance().getTimeInMillis()) < 0) {
						return new ForwardResolution("/api/user/reset_expired.jsp");
					}
				} else {
					return new ForwardResolution("/api/user/reset_expired.jsp");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		
		return new ForwardResolution("/api/user/reset_pwd1.jsp");
	}

	public Resolution resetpwd2() {
		initLangString();
		
		if (memberId == null)
			return new ErrorResolution(ErrorDef.InvalidAccount);
		if (!memberDao.exists(memberId))
			return new ErrorResolution(ErrorDef.InvalidAccount);
		Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
		try {
			userMember.setPassword(PasswordHashUtil.createHash(password));
			userMember.setEncryption(EncrUtil.encrypt(password));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return new ErrorResolution(ErrorDef.InvalidPassword);
		}
		userMember = memberDao.update(userMember);
		Account account = accountDao.findById(userMember.getAccountId());
		if (account != null) {
			String accountLocale = userMember.getLocale();
			if (accountLocale == null || accountLocale.length() < 5)
				accountLocale = "en_US";

		}
		return new ForwardResolution("/api/user/reset_pwd2.jsp");
	}
}
