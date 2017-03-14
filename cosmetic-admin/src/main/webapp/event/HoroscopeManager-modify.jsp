<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Even :: Horoscope Management</h2>
<div>
	<s:form beanclass="${actionBean.class}">
	<table class="form" width="100%">
		<input type="hidden" name="horoscopeId" value="${actionBean.horoscopeId}"/>
        <tr>
        	<td width="100px">Locale :</td>
        	<td>
	        	<select name="horoscope.locale" id="locale">
				    <c:forEach items="${actionBean.availableLocale}" var="loc">
				    	<option value="${loc}" ${loc == actionBean.horoscope.locale ? 'selected' : ''}>${loc}</option>
				    </c:forEach>
			    </select>
        	</td>
        </tr>
        <tr>
        	<td>Post Id :</td>
          	<td>
          		<input type="text" name="horoscope.postId" style="width:600px" placeholder="please enter the reference post id" value="${actionBean.horoscope.postId}"/>
			</td>
        </tr>
        <tr>
        	<td>Title :</td>
          	<td>
          		<input type="text" name="horoscope.title" style="width:600px" placeholder="please enter the title" value="${actionBean.horoscope.title}"/>
			</td>
        </tr>
        <tr>
        	<td>Image Url :</td>
          	<td>
          		<input type="text" rows="5" name="horoscope.imageUrl" style="width:600px" placeholder="please enter image url" value="${actionBean.horoscope.imageUrl}"/>
			</td>
        </tr>
        <tr>
        	<td colspan="2">
	        <s:submit name="update" class="button" value="Update"></s:submit>
        	<s:submit name="cancel" class="button" value="Cancle"></s:submit>
        	</td>
        </tr>
    </table>
    </s:form>
</div>