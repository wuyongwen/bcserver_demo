package com.cyberlink.core.web.stripes;

import java.util.Locale;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstraction class is designed for app-server communication
 * 
 * @author steve_lee
 * 
 */
public abstract class AbstractAction implements ActionBean {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private ActionBeanContext context;

    public ActionBeanContext getContext() {
        return this.context;
    }

    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    public HttpServletRequest getServletRequest() {
        if (this.context != null) {
            return this.context.getRequest();
        }
        return null;
    }

    public HttpServletResponse getServletResponse() {
        if (this.context != null) {
            return this.context.getResponse();
        }
        return null;
    }

    public Locale getRequestLocale() {
        ServletRequestWrapper m = new ServletRequestWrapper(
                (HttpServletRequest) this.context.getRequest());
        return m.getLocale();
    }

    /**
     * For example: Current URL: /modules/newsfeed/picture-admin.action, Caller
     * : ListAllPictures
     * 
     * @return new Resolution(
     *         "/modules/newsfeed/picture-admin-list-all-pictures.jsp")
     */
    protected final Resolution forward() {
        final String callerMethod = getCallerMethod("forward");
        final StringBuffer sb = new StringBuffer();
        sb.append(getUriWithoutPostfix());
        sb.append(convertToLowerCaseWithDashes(callerMethod));
        sb.append(".jsp");
        return new ForwardResolution(sb.toString());
    }

    protected final Resolution backToReferer() {
        return new RedirectResolution(getServletRequest().getHeader("referer"),
                false);
    }

    private String getCallerMethod(String method) {
        final StackTraceElement[] elements = Thread.currentThread()
                .getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            if (StringUtils.equalsIgnoreCase(method,
                    elements[i].getMethodName())) {
                return elements[i + 1].getMethodName();
            }
        }
        return null;
    }

    protected final ForwardResolution forward(String jsp) {
        if (jsp.startsWith("/")) {
            return new ForwardResolution(jsp);
        }

        final String uri = getUriWithoutPostfix();
        return new ForwardResolution(uri + jsp);
    }

    private String getUriWithoutPostfix() {
        final String uri = getServletRequest().getServletPath();
        int index = uri.indexOf("."); 
        if(index > 0){
            return uri.substring(0, index) + "-";    
        }else{
            return uri + "-";
        }
        

    }

    private final String convertToLowerCaseWithDashes(String url) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0, t = url.length(); i < t; i++) {
            char ch = url.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
                if (i > 1) {
                    sb.append("-");
                }
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
