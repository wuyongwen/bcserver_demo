package com.cyberlink.cosmetic.modules.cyberlink.service;

import java.util.Locale;

import com.cyberlink.cosmetic.modules.cyberlink.model.CyberLinkMember;
import com.cyberlink.cosmetic.modules.cyberlink.model.LoginResult;

public interface CyberLinkService {

	LoginResult signInCyberlink(String email, String password);
	
	LoginResult signUp(String email, String password, String displayName, Locale locale);

	Boolean updateCyberlinkUser(String email, String password, String displayName, Locale locale);

	Boolean deleteCyberlinkUser(Long memberId);

    CyberLinkMember fetchCyberLinkMember(String accessToken);
    
    Boolean checkMemberIdAndEmail(String email, Long memberId);
}
