<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title>Redirecting to YMK app or perfect corp...</title>
	<meta property="al:ios:app_store_id" content="863844475"/>
	<meta property="al:ios:app_name" content="YouCam Makeup" />
	<meta property="al:ios:url" content="${actionBean.appUrl}" />
	<meta property="al:web:url"
	          content="http://www.perfectcorp.com/" />
	<meta property="al:android:url" content="${actionBean.appUrl}" />
	<meta property="al:android:app_name" content="YouCam Makeup" />
	<meta property="al:android:package" content="com.cyberlink.youcammakeup" />
	<meta property="al:web:should_fallback" content="false" /> 
</head>
<body>
<script>
    window.location = 'http://www.perfectcorp.com/' ;
</script>
</body>
</html>