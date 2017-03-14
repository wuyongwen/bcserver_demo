package com.cyberlink.cosmetic.action.api;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import com.cyberlink.core.event.ApplicationEventPublisherHolder;
import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.amqp.MessageProducer;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.modules.user.repository.UserSessionRepository;
import com.cyberlink.cosmetic.utils.HmacUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractAction extends
        com.cyberlink.core.web.stripes.AbstractAction implements
        ValidationErrorHandler {

    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;

    @SpringBean("core.applicationEventPublisherHolder")
    private ApplicationEventPublisherHolder applicationEventPublisherHolder;

    @SpringBean("user.SessionDao")
    protected SessionDao sessionDao;
    
    @SpringBean("user.userSessionRepository")
    protected UserSessionRepository userSessionRepository;
    
    // @SpringBean("core.amqp.messageProducer")
    // private MessageProducer messageProducer;
    
    private String token = null;
    private Session session = null;
	protected ErrorDef authError = ErrorDef.InvalidToken;
	private Boolean cltDebug = false;
	private String signature;
	
	public Session getSession() {
    	if (session == null && token != null) {
    		session = sessionDao.findByToken(token);
    	}   		
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getCltDebug() {
        return this.cltDebug;
    }
	
	public void setCltDebug(Boolean cltDebug) {
        this.cltDebug = cltDebug;
    }
	
	protected final ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Resolution input() {
        if (Constants.isProductionMode()) {
            return null;
        }
        return forward();
    }

    protected final Resolution success() {
        return new StreamingResolution("text/html", "");
    }

    protected final Resolution error() {
        return new StreamingResolution("text/html", "");
    }

    protected final Resolution json(String key, Object object) {
        return json(key, object, Views.Public.class);
    }

    protected final Resolution json(String key, Object object,
            Class<?> serializationView) {
        final Map<String, Object> m = new HashMap<String, Object>();
        m.put(key, object);
        return json(m, serializationView);
    }

    public final Resolution json(Object object) {
        return json(object, Views.Public.class);
    }

    protected final Resolution json(Object object, Class<?> serializationView) {
        try {
            return new StreamingResolution("application/json", objectMapper
                    .writerWithView(serializationView).writeValueAsString(
                            object));
        } catch (Exception e) {
            logger.error("json serialize error:" + object);
        }
        return new ErrorResolution(500);
    }

    protected Resolution result(Object result) {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("result", result);

        return json(rm);
    }

    protected Resolution result(Object result, Class<?> serializationView) {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("result", result);

        return json(rm, serializationView);
    }

    protected Resolution results(List<?> results) {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("results", results);

        return json(rm);
    }

    protected Resolution results(List<?> results, Class<?> serializationView) {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("results", results);

        return json(rm, serializationView);
    }

    protected Resolution pageResult(PageResult<?> pageResult) {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("totalSize", pageResult.getTotalSize());
        rm.put("results", pageResult.getResults());

        return json(rm);
    }

    protected Resolution pageResult(PageResult<?> pageResult,
            Class<?> serializationView) {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("totalSize", pageResult.getTotalSize());
        rm.put("results", pageResult.getResults());

        return json(rm, serializationView);
    }

    protected Resolution emptyResult() {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("result", Collections.EMPTY_MAP);

        return json(rm);
    }

    protected Resolution emptyResults() {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("results", Collections.EMPTY_LIST);

        return json(rm);
    }

    protected Resolution emptyPageResult() {
        final Map<String, Object> rm = new HashMap<String, Object>();
        rm.put("totalSize", 0);
        rm.put("results", Collections.EMPTY_LIST);

        return json(rm);
    }

    protected final void publishEvent(Event e) {
        applicationEventPublisherHolder.getApplicationEventPublisher()
                .publishEvent(e);
    }
    
    /**
     * send event object to rabbitmq
     * @param e
     */
    protected final void publishDurableEvent(DurableEvent e) {
        /*try {
            messageProducer.convertAndSend(e);
        }
        catch(Exception ex) {
            logger.error(ex.getMessage());
        } */
    }

    public Resolution handleValidationErrors(ValidationErrors errors) {
        getServletResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Map<String, Object> result = new HashMap<String, Object>();
        for (List<ValidationError> fieldErrors : errors.values()) {
            for (ValidationError error : fieldErrors) {
                result.put("errorMessage", error.getMessage(getRequestLocale()));
                break;
            }
            break;
        }
        return json(result);
    }
    
    protected final Boolean authenticate() {
        /*if (token == null) {
        	authError = ErrorDef.InvalidToken;
        	return Boolean.FALSE;        	
        }
        session = sessionDao.findByToken(token);
        if (session == null) {
        	authError = ErrorDef.InvalidToken;
        	return Boolean.FALSE;
        } else if (session.getStatus() == SessionStatus.Invalied) {
        	authError = ErrorDef.AccountEmailDeleted;
        	return Boolean.FALSE;
        }
        
        getContext().getRequest().setAttribute(Constants.PARAM_CURRENT_USER_ID, session.getUserId());*/
        
        return authenticateByRedis();
    }
    
    protected final Boolean authenticateByRedis() {
        Long userId = userSessionRepository.authenticate(token);
        if (userId == null) {
        	authError = ErrorDef.InvalidToken;
        	return Boolean.FALSE;
        }
        
        getContext().getRequest().setAttribute(Constants.PARAM_CURRENT_USER_ID, userId);
        
        return Boolean.TRUE;
    }
    
    protected RedirectResolution redirectWriteAPI() {
        if(!Constants.getWebsiteIsWritable().equals("true")) {
            HttpServletRequest request = getContext().getRequest();
            String url = request.getScheme() + "://" + Constants.getWebsiteWrite() + request.getRequestURI(); 
            return new RedirectResolution(url).addParameters(getContext().getRequest().getParameterMap());
        }
        
        return null;
    }
    
    protected Boolean authenticateBySignature(Map<String, String> params) {
		String sig = HmacUtils.getSignature(params);
		if (sig == null || !sig.equals(signature))
			return false;
		return true;
	}
    
    protected class AsycnWriteReq implements Runnable {
        private List<Pair<String, String>> params;
        private String url;
        
        public AsycnWriteReq(String url, List<Pair<String, String>> params) {
            this.params = params;
            this.url = url;
        }
        
        public void run() {
            if(url == null || url.length() <= 0)
                return;
            try {
                Connection conn = Jsoup.connect("http://" + Constants.getWebsiteWrite() + url);
                for(Pair<String, String> param : params) {
                    conn.data(param.getKey(), param.getValue());
                }
                conn.ignoreContentType(true).post();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
    }
    
    protected final Long getCurrentUserId() {
        return (Long) getServletRequest().getAttribute(
                Constants.PARAM_CURRENT_USER_ID);
    }
	
    protected Boolean checkIpAddress() {
		String ipAddress = getContext().getRequest().getRemoteAddr();
		if (ipAddress.equalsIgnoreCase("127.0.0.1") ||
			ipAddress.equalsIgnoreCase("203.73.25.227") ||
			ipAddress.equalsIgnoreCase("203.73.25.217")	|| 
			ipAddress.equalsIgnoreCase("203.73.25.204")	||
			ipAddress.equalsIgnoreCase("203.73.25.206")	||
			ipAddress.equalsIgnoreCase("203.73.25.207")	||
			ipAddress.equalsIgnoreCase("203.70.119.76")	||
			ipAddress.equalsIgnoreCase("203.73.25.218")	||
			ipAddress.equalsIgnoreCase("203.70.119.72")	||
			ipAddress.equalsIgnoreCase("65.19.143.89")	||
			ipAddress.equalsIgnoreCase("72.52.84.196")	||
			ipAddress.equalsIgnoreCase("65.19.143.94")	||
			ipAddress.equalsIgnoreCase("54.228.247.52")	||
			ipAddress.equalsIgnoreCase("72.52.91.46")	||
			ipAddress.equalsIgnoreCase("72.52.91.49")){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}    
}
