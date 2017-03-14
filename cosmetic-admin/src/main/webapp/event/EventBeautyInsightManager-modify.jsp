<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Even :: Beauty Buzz Management</h2>
<div>
	<s:form beanclass="${actionBean.class}">
	<table class="form" width="100%">
		<input type="hidden" name="beautyInsight.id" value="${actionBean.beautyInsight.id}"/>
        <tr>
        	<td width="100px">Locale :</td>
        	<td>
	        	<select name="beautyInsight.locale" id="locale">
				    <c:forEach items="${actionBean.availableLocale}" var="loc">
				    	<option value="${loc}" ${loc == actionBean.beautyInsight.locale ? 'selected' : ''}>${loc}</option>
				    </c:forEach>
			    </select>
        	</td>
        </tr>
        <tr>
        	<td>ImgUrl :</td>
          	<td>
          		<input type="text" name="beautyInsight.imgUrl" style="width:600px" placeholder="please enter image url" value="${actionBean.beautyInsight.imgUrl}"/>
			</td>
        </tr>
        <tr>
        	<td>RedirectUrl :</td>
          	<td>
          		<input type="text" rows="5" name="beautyInsight.redirectUrl" style="width:600px" placeholder="please enter redirect url" value="${actionBean.beautyInsight.redirectUrl}"/>
			</td>
        </tr>
        <tr>
        	<td>Description :</td>
          	<td>
          		<textarea name="beautyInsight.description" style="width:600px" placeholder="please enter beauty insight description" rows="5">${actionBean.beautyInsight.description}</textarea>
			</td>
        </tr>
        <tr>
        	<td>Post Id :</td>
          	<td>
          		<input type="number" rows="5" name="postId" style="width:600px" placeholder="please enter post id" value="${actionBean.postId}"/>
			</td>
        </tr>
        <tr>
        	<td colspan="2">
        	<c:if test="${actionBean.isUpdate}">
	        	<s:submit name="update" class="button" value="Update"></s:submit>
        	</c:if>
        	<c:if test="${actionBean.isCreate}">
	        	<s:submit name="create" class="button" value="create"></s:submit>
        	</c:if>
        	<s:submit name="cancel" class="button" value="cancle"></s:submit>
        	</td>
        </tr>
    </table>
    </s:form>
</div>