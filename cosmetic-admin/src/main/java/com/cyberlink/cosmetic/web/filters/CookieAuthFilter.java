package com.cyberlink.cosmetic.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class CookieAuthFilter extends OncePerRequestFilter {
    private static final String SITEMESH_DECORATOR_PARAM = "_decorator";
    private static final String SITEMESH_DECORATOR_NO_HEADER = "backend-v2-no-header";
    private String cookieName = "_console_token";
    private String sessionName = "token";

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = retrieveTokenFromCookie(request);
        if (StringUtils.isNotBlank(token)) {
            authUserViaToken(request, token);
            registerSitemeshDecorator(request);
        }

        filterChain.doFilter(request, response);
    }

    private void registerSitemeshDecorator(HttpServletRequest request) {
        request.getSession().setAttribute(SITEMESH_DECORATOR_PARAM, SITEMESH_DECORATOR_NO_HEADER);
    }

    private void authUserViaToken(HttpServletRequest request, String token) {
        request.getSession().setAttribute(sessionName, token);
    }

    private String retrieveTokenFromCookie(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (final Cookie c : cookies) {
            if (StringUtils.equalsIgnoreCase(c.getName(), cookieName)) {
                return c.getValue();
            }
        }
        return null;
    }

}
