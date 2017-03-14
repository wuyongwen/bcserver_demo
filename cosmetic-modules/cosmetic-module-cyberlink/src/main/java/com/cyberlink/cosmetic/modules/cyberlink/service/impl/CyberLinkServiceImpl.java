package com.cyberlink.cosmetic.modules.cyberlink.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.cosmetic.modules.cyberlink.model.CyberLinkMember;
import com.cyberlink.cosmetic.modules.cyberlink.model.CyberLinkMemberStatus;
import com.cyberlink.cosmetic.modules.cyberlink.model.LoginResult;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.cyberlink.service.CyberLinkService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CyberLinkServiceImpl extends AbstractService implements
        CyberLinkService {

    private static final String MEMBERSHIP_SERVICE_URL = "https://membership.cyberlink.com/ws_member/member.jsp";
    private static final String MEMBERSHIP_SERVICE_URL_READ = "http://memberwebservice.cyberlink.com/ws_member/member-read.jsp";
    private static final String MEMBERSHIP_SERVICE_URL_DELETE = "https://membership.cyberlink.com/ws_member/member-delete.jsp";
    
    private static final Integer MEMBERSHIP_SERVICE_ID = 5;

    private static final String CSE_GET_MEMBER_SERVICE_URL = "http://cse.cyberlink.com/cse/service/getMember";

    private URLContentReader urlContentReader;

    private ObjectMapper objectMapper;

    public void setUrlContentReader(URLContentReader urlContentReader) {
        this.urlContentReader = urlContentReader;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

	@Override
    public LoginResult signInCyberlink(String email, String password) {
		LoginResult status = signInCyberlink(email, password, MEMBERSHIP_SERVICE_URL_READ);
    	if (status.getStatus() == CyberLinkMemberStatus.MemberNotExist || 
    			status.getStatus() == CyberLinkMemberStatus.MemberServiceUnavailable) {
    		return signInCyberlink(email, password, MEMBERSHIP_SERVICE_URL);
    	}
        return status;
    }

    @SuppressWarnings("unchecked")
    private LoginResult signInCyberlink(String email, String password, String url) {        
    	final Map<String, String> params = new HashMap<String, String>();
        params.put("method", "GET");
        params.put("email", email);
        params.put("serviceid", String.valueOf(MEMBERSHIP_SERVICE_ID));
        Map<String, Object> contentMap;
    	try {
    		String returnJson = urlContentReader.post(url, params);
            contentMap = objectMapper.readValue( 
            		returnJson, Map.class);
            if (!contentMap.containsKey("memberId"))
            	return new LoginResult(CyberLinkMemberStatus.MemberNotExist, null);
            if (contentMap.get("password_e5") == null)
            	contentMap.put("password_e5", PasswordHashUtil.createHash((String) contentMap.get("password")));
            if (PasswordHashUtil.validatePassword(password, (String) contentMap.get("password"), (String) contentMap.get("password_e5"))) {
                if (!((int) contentMap.get("isvalid") == 1)) {                	
                	return new LoginResult(CyberLinkMemberStatus.WaitValidate, contentMap);
                }
            	return new LoginResult(CyberLinkMemberStatus.OK, contentMap); 
            } else {
            	return new LoginResult(CyberLinkMemberStatus.WrongPassWord, contentMap);
            }
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	return new LoginResult(CyberLinkMemberStatus.MemberServiceUnavailable, null);
        }    	
    }
    
    @Override
    public LoginResult signUp(String email, String password, String displayName, Locale locale) {
    	LoginResult status = signInCyberlink(email, password);
    	if (status.getStatus() != CyberLinkMemberStatus.MemberNotExist) {
    		return status;
    	}
    	try {
            final Map<String, Object> member = new HashMap<String, Object>();
            if (displayName == null)
            	displayName = email;
            
            member.put("email", email);
            member.put("password", password);
            member.put("password_e5", PasswordHashUtil.createHash(password));
            member.put("FirstName", displayName);
            member.put("LastName", "");
            member.put("language", locale.getLanguage());
            member.put("rec_letter", 0);
            member.put("weekly_bulletin", 0);
            member.put("Country", locale.getCountry());
            member.put("serviceid", MEMBERSHIP_SERVICE_ID);

            final Map<String, String> params = new HashMap<String, String>();
            params.put("method", "PUT");
            params.put("email", email);
            params.put("member", objectMapper.writeValueAsString(member));

            final String content = urlContentReader.post(
                    MEMBERSHIP_SERVICE_URL, params);

            if (StringUtils.isBlank(content)) {
            	return new LoginResult(CyberLinkMemberStatus.WaitValidate, null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return new LoginResult(CyberLinkMemberStatus.MemberServiceUnavailable, null);
    }

    @Override
    public CyberLinkMember fetchCyberLinkMember(String accessToken) {
        try {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("token", accessToken);

            final String content = urlContentReader.post(
                    CSE_GET_MEMBER_SERVICE_URL, params);

            return objectMapper.readValue(content, CyberLinkMember.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

	@Override
	public Boolean checkMemberIdAndEmail(String email, Long memberId) {
		if (!doCheckMemberIdAndEmail(email, memberId, MEMBERSHIP_SERVICE_URL_READ)) {
    		return doCheckMemberIdAndEmail(email, memberId, MEMBERSHIP_SERVICE_URL);
    	}
        return Boolean.TRUE;	
	}
	
    @SuppressWarnings("unchecked")
    private Boolean doCheckMemberIdAndEmail(String email, Long memberId, String url) {        
    	final Map<String, String> params = new HashMap<String, String>();
        params.put("method", "GET");
        params.put("email", email);
        params.put("serviceid", String.valueOf(MEMBERSHIP_SERVICE_ID));
        Map<String, Object> contentMap;
    	try {
    		String returnJson = urlContentReader.post(url, params);
            contentMap = objectMapper.readValue( 
            		returnJson, Map.class);
            if (!contentMap.containsKey("memberId"))
            	return Boolean.FALSE;
            if( memberId == (int)contentMap.get("memberId")) {
            	return Boolean.TRUE;
            }

    	} catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	return Boolean.FALSE;
        }    	
    	return Boolean.FALSE;
    }

	@Override
	public Boolean updateCyberlinkUser(String email, String password,
			String displayName, Locale locale) {
    	try {
            final Map<String, Object> member = new HashMap<String, Object>();
            if (displayName == null)
            	displayName = email;
            
            member.put("email", email);
            member.put("password", password);
            member.put("password_e5", PasswordHashUtil.createHash(password));
            member.put("FirstName", displayName);
            member.put("LastName", "");
            member.put("language", locale.getLanguage());
            member.put("isValid", 1);
            member.put("Country", locale.getCountry());
            member.put("serviceid", MEMBERSHIP_SERVICE_ID);

            final Map<String, String> params = new HashMap<String, String>();
            params.put("method", "PUT");
            params.put("email", email);
            params.put("member", objectMapper.writeValueAsString(member));

            final String content = urlContentReader.post(
                    MEMBERSHIP_SERVICE_URL, params);

            if (StringUtils.isBlank(content)) {
            	return Boolean.TRUE;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return Boolean.FALSE;

	}

	@Override
	public Boolean deleteCyberlinkUser(Long memberId) {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("method", "PUT");
		params.put("MemberId", String.valueOf(memberId));
		params.put("serviceid", String.valueOf(MEMBERSHIP_SERVICE_ID));
        final String content = urlContentReader.post(
        		MEMBERSHIP_SERVICE_URL_DELETE, params);

        if (StringUtils.isBlank(content)) {
            	return Boolean.TRUE;
        }
        return Boolean.FALSE;
	}
}
