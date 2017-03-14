<%@ page language="java" contentType="application/json;charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.security.NoSuchAlgorithmException"%>
<%@ page import="java.security.spec.InvalidKeySpecException"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.UUID"%>

<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="com.cyberlink.core.web.jackson.Views"%>
<%@ page import="com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.AccountDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.MemberDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.SessionDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.dao.UserDao"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Account"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.AccountSourceType"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Member"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.Session"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.SessionStatus"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.User"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.UserType"%>
<%@ page import="com.fasterxml.jackson.core.JsonProcessingException"%>
<%@ page import="com.fasterxml.jackson.core.PrettyPrinter"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.model.User"%>
<%@ page import="com.cyberlink.cosmetic.modules.user.service.UserService"%>

<%!
	
    public UserDao userDao = BeanLocator.getBean("user.UserDao");
    
    public AccountDao accountDao = BeanLocator.getBean("user.AccountDao");
    
    public MemberDao memberDao = BeanLocator.getBean("user.MemberDao");
    
    public SessionDao sessionDao = BeanLocator.getBean("user.SessionDao");
		
	public ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
	
	public UserService userService = BeanLocator.getBean("user.userService");
	
	public Account createUser(String email, String password, String displayName){
		Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
		if (account == null) {
			try {
				account = doSignIn(email, "en_US", displayName);
				createNewMember(PasswordHashUtil.createHash(password), account.getId(), null, "en_US");
    		} catch (Exception e) {
    			return null;
    		}
		}
		return account;
	}

	public Account doSignIn(String email, String locale, String displayName){
        Account	account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        
        User user = null;
        Long userId;
        if (account == null) {
        	account = new Account();
            account.setAccount(email);
            account.setEmail(email);
            account.setAccountSource(AccountSourceType.Email);            
        } 
        
        if (user == null) {
            user = createNewUser(locale, displayName);
            userId = user.getId();
            account.setUserId(userId);
            account = accountDao.update(account);
        } 
        return account;
    }
	
	public User createNewUser(String locale, String displayName) {
        User user = new User();
        user.setUserType(UserType.Blogger);
    	user.setRegion(locale);
        user.setObjVersion(0);
    	user.setIpAddress("118.163.84.235");
		user.setAttribute("{}");
		user.setDisplayName(displayName);
        user = userDao.create(user);
        return user;
    }
	
	public Member createNewMember(String pass, Long accountId, Long memberId, String locale) {
    	Member member = new Member();
    	member.setLocale(locale);
    	member.setAccountId(accountId);
    	member.setActivateCode(null);
    	member.setMemberId(memberId);
    	member.setPassword(pass);
    	return memberDao.create(member);
    }
	
	public String run(String email, String password, String displayName) {
	    Account c = createUser(email,password,displayName);
	    Map<String, Object> result = new HashMap<String, Object>();
	    User user = c.getUser();
	    result.put("token", userService.getToken(user.getId(), user.getUserType()));
	    Map<String, Object> userInfo = new HashMap<String, Object>();
	    userInfo.put("id", c.getUserId());
	    result.put("userInfo", userInfo);
	    Map<String, Object> response = new HashMap<String, Object>();
	    response.put("result", result);
	    
	    try {
            return objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
	}

%>

<%
	out.println(run(request.getParameter("email"), request.getParameter("password"), request.getParameter("displayName")));
%>