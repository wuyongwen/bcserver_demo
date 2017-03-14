package com.cyberlink.cosmetic.action.backend.post;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.IndexAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter.ImageHandler;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter.MainPostContent;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter.PostContent;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableList;
import com.restfb.json.JsonObject;

@UrlBinding("/post/CreateCircle.action")
public class CreateCircleAction extends AbstractAction {
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleTypeDao")
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;

    // Route
    private List<CircleType> circleTypes = new ArrayList<CircleType>();
    
    // Create
    public String circleName;
    public String description;
    public Long circleTypeId;
    public Boolean isSecret = false;
    
    public List<CircleType> getCircleTypes() {
        return circleTypes;
    }
    
    public void setCircleTypes(List<CircleType> circleTypes) {
        this.circleTypes = circleTypes;
    }
    
    public String getCircleName() {
        return circleName;
    }
    
    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCircleTypeId() {
        return circleTypeId;
    }
    
    public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }
    
    public Boolean getIsSecret() {
        return isSecret;
    }
    
    public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }
    
    @DefaultHandler
    public Resolution route() {       
        Boolean isLogin = false;
        HttpSession session = getContext().getRequest().getSession();
        Long userId=(long)0;
        User user = null;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                userId = loginSession.getUserId();
                user = userDao.findById(userId);
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
        
        Set<String> postLocales = localeDao.getLocaleByType(user.getRegion(), LocaleType.POST_LOCALE);
        PageResult<CircleType> pageResult = circleTypeDao.listTypesByLocale(postLocales.iterator().next(), null, new BlockLimit(0, 100));
        for(CircleType cirType : pageResult.getResults()) {
            circleTypes.add(cirType);
        }
        
        return forward();
    }
    
    public Resolution create() {       
        Boolean isLogin = false;
        HttpSession session = getContext().getRequest().getSession();
        Long userId=(long)0;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                userId = loginSession.getUserId();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
        
        Circle toCreateCircle = new Circle();
        toCreateCircle.setCreatorId(userId);
        toCreateCircle.setCircleName(circleName);
        toCreateCircle.setDescription(description);
        toCreateCircle.setCricleTypeId(circleTypeId);
        toCreateCircle.setIsSecret(isSecret);
        Circle createdCircle = circleDao.create(toCreateCircle);
        if(createdCircle == null)
            return new ErrorResolution(400, "Failed to create circle");
        return new StreamingResolution("text/html", toCreateCircle.getCircleName() + " created successfully.");
    }
    
    public Resolution cancel() {
        return new RedirectResolution(IndexAction.class, "route");
    }   
}
