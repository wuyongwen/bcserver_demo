<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.io.UnsupportedEncodingException"%>
<%@ page import="java.security.NoSuchAlgorithmException"%>
<%@ page import="java.security.spec.InvalidKeySpecException"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.UUID"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="org.hibernate.stat.SecondLevelCacheStatistics"%>
<%@ page import="net.sf.ehcache.CacheManager"%>
<%@ page import="net.sf.ehcache.Cache"%>

<%@ page import="org.hibernate.SessionFactory"%>

<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.apache.http.HttpResponse"%>
<%@ page import="org.apache.http.NameValuePair"%>
<%@ page import="org.apache.http.client.HttpClient"%>
<%@ page import="org.apache.http.client.entity.UrlEncodedFormEntity"%>
<%@ page import="org.apache.http.client.methods.HttpPost"%>
<%@ page import="org.apache.http.impl.client.DefaultHttpClient"%>
<%@ page import="org.apache.http.message.BasicNameValuePair"%>

<%@ page import="com.restfb.json.JsonObject"%>

<%@ page import="com.cyberlink.core.BeanLocator"%>

<%@ page import="com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.AccountDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.AttributeDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.MemberDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.SessionDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.UserDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Account"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.AccountSourceType"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Attribute"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.AttributeType"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Member"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Session"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.SessionStatus"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.User"%>

<%!
AccountDao accountDao = BeanLocator.getBean("user.AccountDao");
AttributeDao attributeDao = BeanLocator.getBean("user.AttributeDao");
SessionFactory sessionFactory = BeanLocator.getBean("core.sessionFactory");
SessionDao sessionDao = BeanLocator.getBean("user.SessionDao");
MemberDao memberDao = BeanLocator.getBean("user.MemberDao");
UserDao userDao = BeanLocator.getBean("user.UserDao");

private Boolean getCurrentUserAdmin(HttpServletRequest request) {
    User user = getCurrentUser(request);
    if (user != null) {
        List<Attribute> attr = attributeDao.findByNameAndRefIds(AttributeType.AccessControl, "Access", user.getId());
        if (attr.size() > 0 && attr.get(0).getAttrValue().equals("Admin")) {
            return Boolean.TRUE;
        }           
        return Boolean.FALSE;           
    }
    return Boolean.FALSE;
}

private User getCurrentUser(HttpServletRequest request) {
    HttpSession session = request.getSession();
    if(session != null) {
        String token = (String) request.getSession().getAttribute("token");
        if(token != null && token.length() > 0) {
            Session loginSession = sessionDao.findByToken(token);                
            if (loginSession == null) {
                return null;
            } else {    
                return userDao.findById(loginSession.getUserId());
            }
        } else {
            return null;                
        }
    }          
    return null;
}

private void login(String email, String password, HttpServletRequest request, HttpServletResponse response, JspWriter out) throws Exception {
    String accountToken = "";
    /*try {
        accountToken = loginCSE(email, password).getString("accessToken");
    } catch (Exception e1) {

    }*/
    
    Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);        
    if (accountToken.length() == 0) {
        if (account != null) {
            Member member = memberDao.findByAccountId(account.getId());
            if (member == null) {
                out.println("<font color=\"red\">Invalid password</font><br/><br/>");
                return;
            }
            try {
                if (!PasswordHashUtil.validatePassword(password, member.getPassword())) {
                    out.println("<font color=\"red\">Invalid password</font><br/><br/>");
                    return;
                }
            } catch (NoSuchAlgorithmException e) {
                out.println("<font color=\"red\">Invalid password</font><br/><br/>");
                return;
            } catch (InvalidKeySpecException e) {
                out.println("<font color=\"red\">Invalid password</font><br/><br/>");
                return;
            }
        } else {
            out.println("<font color=\"red\">Invalid password</font><br/><br/>");
            return;
        }
    }
    //Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
    User user = null;
    if (account == null) {
        out.println("<font color=\"red\">Invalid account</font><br/><br/>");
        return;
    } else {
        user = userDao.findById(account.getUserId());
    }    
    Long userId;
    String token = null;
    if (user == null) {
        out.println("<font color=\"red\">Invalid user</font><br/><br/>");
        return;
    } else {
        userId = user.getId();
        token = getToken(userId);
    }
    if (!StringUtils.isBlank(token)) {
        request.getSession().setAttribute("token", token);      
        Cookie c = new Cookie("email", email);
        c.setMaxAge(24*60*60);
        response.addCookie(c);  // response is an instance of type HttpServletReponse        
    } else {
        out.println("<font color=\"red\">Token not found</font><br/><br/>");
    }
    return;
}

private String getToken(Long userId) {
    List<Session> sessions = sessionDao.findByUserId(userId);
    for(Session s : sessions) {
        if(s.getStatus().equals(SessionStatus.SignIn))
            return s.getToken();
    }
    return null;
}

private JsonObject loginCSE(String email, String password) throws Exception {
    Map<String, String> param = new HashMap<String, String>();
    param.put("email", email);
    param.put("password", password);
    
    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost("https://cse.cyberlink.com/cse/service/signin");

    post.setHeader("User-Agent", "");

    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    for (Map.Entry<String, String> entry: param.entrySet()) {
         urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    }
    post.setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));

    HttpResponse response = client.execute(post); 
    BufferedReader rd = new BufferedReader(
                   new InputStreamReader(response.getEntity().getContent(), "utf-8"));

    StringBuffer result = new StringBuffer();
    String line = "";
    while ((line = rd.readLine()) != null) {
        result.append(line);
    }
    return new JsonObject(result.toString());
}
%>

<%
String defaultEmail = request.getParameter("email");
if (defaultEmail == null) {  
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for(int i = 0; i < cookies.length; i++) { 
            Cookie c = cookies[i];
            if (c.getName().equals("email")) {
                try {
                    defaultEmail = URLDecoder.decode(c.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    defaultEmail = c.getValue();
                }
            }
        }
    }
        
    if (defaultEmail == null)
        defaultEmail = "";
}


if (!StringUtils.isBlank(request.getParameter("login"))) {
    if (!StringUtils.isBlank(request.getParameter("email")) && !StringUtils.isBlank(request.getParameter("password")))
        login(request.getParameter("email"), request.getParameter("password"), request, response, out);
    else
        out.println("<font color=\"red\">Please input Email and Password !!</font><br/><br/>");
}

if (getCurrentUserAdmin(request)) {
    if (!StringUtils.isBlank(request.getParameter("refresh_all"))) {
        sessionFactory.getCache().evictEntityRegions();
        sessionFactory.getCache().evictCollectionRegions();
        sessionFactory.getCache().evictDefaultQueryRegion();
        sessionFactory.getCache().evictQueryRegions();
        
        out.println("<font color=\"#477FAE\">All Hibernate second level caches are removed</font><br/><br/>");
        
    }

    if (!StringUtils.isBlank(request.getParameter("refresh_query"))) {
        if (!StringUtils.isBlank(request.getParameter("regionName"))) {
            sessionFactory.getCache().evictQueryRegion(request.getParameter("regionName"));
            
            out.println("<font color=\"#477FAE\">The Query Cache, <B>" + request.getParameter("regionName") + "</B>, is removed</font><br/><br/>");
        } else {
            out.println("<font color=\"red\">Please input Region Name !!</font><br/><br/>");
        }    
    }  
	
	if (!StringUtils.isBlank(request.getParameter("check_query"))) {
        if (!StringUtils.isBlank(request.getParameter("checkRegionName"))) {
            try{
                SecondLevelCacheStatistics secondLvlCache = sessionFactory.getStatistics().getSecondLevelCacheStatistics(request.getParameter("checkRegionName"));
                out.println("Hit count : " + String.valueOf(secondLvlCache.getHitCount()) + "<br/>");
            	out.println("Hit miss count : " + String.valueOf(secondLvlCache.getMissCount()) + "<br/>");
            	out.println("Hit put count : " + String.valueOf(secondLvlCache.getPutCount()) + "<br/>");
            	out.println("Element count in memory : " + String.valueOf(secondLvlCache.getElementCountInMemory()) + "<br/>");
            	out.println("Element count in disk : " + String.valueOf(secondLvlCache.getElementCountOnDisk()) + "<br/>");
            	out.println("Size in memory : " + String.valueOf(secondLvlCache.getSizeInMemory()) + "<br/>");
            	/*if(secondLvlCache.getEntries() == null)
            	    out.println("Null<br/>");*/
            	
                CacheManager ehCacheManager = CacheManager.create();
                if(ehCacheManager == null)
                    out.println("Null<br/>");
                else {
                    Cache cacheName = ehCacheManager.getCache(request.getParameter("checkRegionName"));
                    for(Object key : cacheName.getKeys())
                        out.println(key + "Null<br/>");
                }
            }
            catch(Exception e) {
                out.println("Error : " + e.getMessage() + "<br/>");	
            }
        } else {
            out.println("<font color=\"red\">Please input Region Name !!</font><br/><br/>");
        }    
    }  
}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Refresh Hibernate Cache</title>
</head>
<body>
<% if(!getCurrentUserAdmin(request)) { %>
<form action="refresh_cache.jsp" method="post">
    <table>
        <tr>
          <td>Email:</td>
          <td><input type="text" name="email" value="<%=defaultEmail%>"/></td>
        </tr>
        <tr>
          <td>Password:</td>
          <td><input type="password" name="password"/></td>
        </tr>

        <tr>
          <td>&nbsp;</td>
          <td>
            <input type="submit" name="login" value="Login"/>
            <input type="reset" name="reset" value="Reset"/>
          </td>
        </tr>
    </table>
</form>
<% } else { %>
<form action="refresh_cache.jsp" method="post">
    <table>
        <tr>
          <td>&#8226; Refresh <font color="#477FAE"><B>all</B></font> Hibernate second level caches (includes Entity, Collection, Query)&nbsp;</td>
          <td><input type="submit" name="refresh_all" value="Refresh All"/></td>
        </tr>
        <tr>
          <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
          <td>&#8226; Refresh the <font color="#477FAE"><B>specific</B></font> Query cache, Region Name: <input type="text" name="regionName"/></td>
          <td><input type="submit" name="refresh_query" value="Refresh Query Cache"/></td>
        </tr>
		<tr>
          <td colspan="2">&nbsp;</td>
        </tr>
		<tr>
          <td>&#8226; Check the <font color="#477FAE"><B>specific</B></font> Query cache statistic, Region Name: <input type="text" name="checkRegionName"/></td>
          <td><input type="submit" name="check_query" value="Check Query Cache"/></td>
        </tr>
    </table>
</form>
<% } %>
</body>
</html>