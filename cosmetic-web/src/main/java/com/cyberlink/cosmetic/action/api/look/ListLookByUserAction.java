package com.cyberlink.cosmetic.action.api.look;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.look.dao.LookDao;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.Look;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.ApplyType;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventType;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/look/list-look-by-user.action")
public class ListLookByUserAction extends AbstractAction {

    @SpringBean("look.LookDao")
    private LookDao lookDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
	private int offset = 0;
    private int limit = Integer.MAX_VALUE;
    private Long userId;
	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public void setUserId(Long userId) {
        this.userId = userId;
    }
	
	@DefaultHandler
	public Resolution route() {
	    if(userId == null || !userDao.exists(userId))
	    	return new ErrorResolution(ErrorDef.InvalidUserId);
		
	    PageResult<Look> looks = lookDao.findByUserId(userId, new BlockLimit(offset, limit));
		return json(looks);
	}
}
