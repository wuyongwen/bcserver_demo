package com.cyberlink.cosmetic.action.api.user;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;

@UrlBinding("/api/user/unsubscribe-email.action")
public class UnSubscribeMailAction extends AbstractAction {
	@SpringBean("user.AccountDao")
    protected AccountDao accountDao;

	private String code;

	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;
		
		String email = getHexToString(code);
		List<Account> accountList = accountDao.findByEmail(email);         
    	if (accountList == null || accountList.isEmpty()) {
    		return new ErrorResolution(ErrorDef.InvalidAccount);
    	}

    	for (Account account : accountList) {
    		if (AccountMailStatus.INVALID.equals(account.getMailStatus()))
    			continue;
    		account.setMailStatus(AccountMailStatus.UNSUBSCRIBE);
    		accountDao.update(account);
    	}
		
		return success();
	}
	
	private String getHexToString(String strValue) {
	     int length = strValue.length() / 2;
	    String strReturn = "";
	    String strHex = "";
	    int hex = 0;
	    byte byteData[] = new byte[length];
	     
	    try {
	    for(int i=0; i<length; i++) {
	         strHex = strValue.substring(0, 2);
	             strValue = strValue.substring(2);
	             hex = Integer.parseInt(strHex, 16);
	             if(hex > 128)
	              hex = hex - 256;
	                  byteData[i] = (byte) hex;
	    }
	         strReturn = new String(byteData,"ISO8859-1");
	    }catch(Exception ex) {
	    ex.printStackTrace();
	    }
	    return strReturn;
	}
	
	@Validate(required = true, on = "route")
	public void setCode(String code) {
		this.code = code;
	}
	
	private static String getStringToHex(String strValue) {
        byte byteData[] = null;
        int hex = 0;
        String strHex = "";
         
        try {
        byteData = strValue.getBytes("ISO8859-1");
            for(int i=0; i<byteData.length; i++) {
            hex = (int)byteData[i];
                 if(hex < 0)
                 hex += 256;
                 if(hex < 16)
                 strHex += "0" + Integer.toHexString(hex).toUpperCase();
                 else
                 strHex += Integer.toHexString(hex).toUpperCase();
            }                      
        }catch(Exception ex) {
        ex.printStackTrace();
        }
        return strHex;
    }

}