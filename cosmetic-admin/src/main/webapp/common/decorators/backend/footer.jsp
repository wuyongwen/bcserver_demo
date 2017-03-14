<%@ include file="/common/taglibs.jsp"%>
<%@ page import="java.util.Date"%>
<jsp:useBean id="now" class="java.util.Date"/>
<div id=footer class=clearfix>
<p class=left>Beauty Circle Administration Console</p>
<p class=right>All content &copy <fmt:formatDate value="${now}" pattern="yyyy"/> CyberLink Corp. All Rights
Reserved.</p>
</div>