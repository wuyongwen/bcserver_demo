package com.cyberlink.cosmetic.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class RequestProfilingFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private boolean productionMode = Boolean.FALSE;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (productionMode) {
            filterChain.doFilter(request, response);
            return;
        }
        final MyHttpServletResponseWrapper w = new MyHttpServletResponseWrapper(
                response);
        String parameters = dumpParameters(request);
        try {
            filterChain.doFilter(request, w);
        } finally {
            log(request, w, parameters);
        }
    }

    private void log(HttpServletRequest request,
            MyHttpServletResponseWrapper response, String parameters) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        Long endTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append(request.getRequestURI());
        sb.append("\t");
        sb.append(parameters);

        sb.append("\t");
        sb.append(response.getStatus());
        
        sb.append("\t");
        final String locale = request.getParameter("locale");
        sb.append(locale);
        
        sb.append("\t");
        final String userId = request.getParameter("userId");
        sb.append(userId);
        
        sb.append("\t");
        final String remoteIp = getIpAddr(request);
        sb.append(remoteIp);
        
        sb.append("\t");
        final String apiVersion = request.getParameter("apiVersion");
        sb.append(apiVersion);
        
        sb.append("\t");
        sb.append(String.valueOf(endTime - response.getStartTime()) + "ms");
        
        logger.debug(sb.toString().replaceAll("\r\n", ""));
    }

    @SuppressWarnings("rawtypes")
    private String dumpParameters(HttpServletRequest request) {
        final List<String> results = new ArrayList<String>();
        final Map m = request.getParameterMap();
        for (final Object name : m.keySet()) {
            final String s = (String) name;
            if (StringUtils.startsWith(s, "_")) {
                continue;
            }
            results.add(s + " = " + request.getParameter(s));
        }
        return StringUtils.join(results, ", ");
    }

    private final class MyHttpServletResponseWrapper extends
            HttpServletResponseWrapper {
        private int httpStatus;
        private Long startTime;
        public MyHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
            startTime = System.currentTimeMillis();
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        public int getStatus() {
            return httpStatus;
        }
        
        public long getStartTime() {
            return startTime;
        }

    }

    private String getIpAddr(HttpServletRequest request) { 
        String ip = request.getHeader("x-forwarded-for"); 
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            int idx = ip.indexOf(',');
            if (idx > -1) {
                ip = ip.substring(0, idx);
            }
        } else {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getRemoteAddr(); 
        } 
        return ip; 
    }
}
