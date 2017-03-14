<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/decorators/backend/styles.jsp"%>

 <display:table id="row" name="actionBean.pageResult.results" requestURI="selectUser.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
   <display:column title="tag user">
   	<input type="checkbox" name="checkboxChoices" value="${row.id}" text="${row.displayName}"/>
   </display:column>
   <display:column title="userId" sortable="true" sortName="id">
       <c:out value="${row.id}"/>
   </display:column>
   <display:column title="avatar" sortable="false">
       <img src="${row.avatarUrl}" height="50" width="50" />
   </display:column>
   <display:column title="displayName" sortable="true" sortName="displayName">
       <c:out value="${row.displayName}"/>
   </display:column>
   <display:column title="gender" sortable="true" sortName="gender">
       <c:out value="${row.gender}"/>
   </display:column>
   <display:column title="userType" sortable="true" sortName="userType">
       <c:out value="${row.userType}"/>
   </display:column>
   <display:column title="description" sortable="false">
       <c:out value="${row.description}"/>
   </display:column>
 </display:table>