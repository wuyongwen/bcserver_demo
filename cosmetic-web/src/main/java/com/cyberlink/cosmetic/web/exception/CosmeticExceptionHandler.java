package com.cyberlink.cosmetic.web.exception;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.StripesConstants;
import net.sourceforge.stripes.exception.DefaultExceptionHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberlink.cosmetic.exception.CosmeticException;
import com.cyberlink.cosmetic.exception.UnKnownException;
import com.cyberlink.cosmetic.action.api.AbstractAction;

public class CosmeticExceptionHandler extends DefaultExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final Set<String> unrecoverables = new HashSet<String>();
    static {
        unrecoverables.add("Could not open connection");
    }

    public Resolution handleException(CosmeticException se,
            HttpServletRequest req, HttpServletResponse res) {
        logger.error("", se);
        res.setStatus(se.getErrorCode());
        AbstractAction bean = (AbstractAction) req
                .getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
        return bean.json(buildErrorResult(se));
    }

    private Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() != null)
            return getRootCause(throwable.getCause());

        return throwable;
    }
    
    public Resolution handleException(Throwable se, HttpServletRequest req,
            HttpServletResponse res) {
        logger.error("", se);
        if (isUnrecoverable(se)) {
            return new ErrorResolution(502);
        }
        
        UnKnownException retException = new UnKnownException();
        AbstractAction bean = (AbstractAction) req.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
        if(bean.getCltDebug()) {
            retException.setErrorMessage(getRootCause(se).getMessage());
        }
        return handleException(retException, req, res);
    }

    private boolean isUnrecoverable(Throwable e) {
        for (final String s : unrecoverables) {
            if (StringUtils.containsIgnoreCase(e.getMessage(), s)) {
                return true;
            }
        }
        if (e instanceof OutOfMemoryError) {
            return true;
        }

        return false;
    }

    private Map<String, Object> buildErrorResult(CosmeticException se) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("errorMessage", se.getErrorMessage());
        if (se.getErrorResults() != null) {
            result.put("errorResults", se.getErrorResults());
        }
        return result;
    }

}
