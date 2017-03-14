<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<style>
TR.highlight TD {
    color: #477FAE;
    font-size: 10pt;
}
</style>

<h2 class=ico_mug>File :: File Detail</h2>

<div>
    <s:form beanclass="${actionBean.class}">
        <s:submit name="submit" value="Upload New File"/><br/><br/>
    </s:form>
    <c:choose>
		<c:when test="${empty actionBean.originalUrl}">
			<table>
				<tr class="highlight">
					<td style="width: 130px">File Id:</td>
					<td>${actionBean.fileEntity.id}</td>
				</tr>
				<tr class="highlight">
					<td>File Type:</td>
					<td>${actionBean.fileEntity.fileType}</td>
				</tr>
			</table>
			<br />
			<hr style="width: 100%" />
			<br />
			<c:forEach var="fileItem" items="${actionBean.fileEntity.fileItems}">
				<table>
					<tr class="highlight">
						<td style="width: 130px">File Item Id:</td>
						<td>${fileItem.id}</td>
					</tr>
					<tr>
						<td>File Path:</td>
						<td>${fileItem.filePath}</td>
					</tr>
					<tr>
						<td>File Name:</td>
						<td>${fileItem.fileName}</td>
					</tr>
					<tr class="highlight">
						<td>File Size:</td>
						<td><fmt:formatNumber value="${fileItem.fileSize}"
								pattern="#,##0" /> (<fmt:formatNumber
								value="${fileItem.fileSize/1024.0}" pattern="#,##0" /> KB)</td>
					</tr>
					<tr>
						<td>Content Type:</td>
						<td>${fileItem.contentType}</td>
					</tr>
					<tr>
						<td>MD5:</td>
						<td>${fileItem.md5}</td>
					</tr>
					<tr>
						<td>Width:</td>
						<td>${fileItem.width}</td>
					</tr>
					<tr>
						<td>Height:</td>
						<td>${fileItem.height}</td>
					</tr>
					<tr>
						<td>Orientation:</td>
						<td>${fileItem.orientation}</td>
					</tr>
					<tr>
						<td>Metadata:</td>
						<td>${fileItem.metadata}</td>
					</tr>
					<tr class="highlight">
						<td>Is Original:</td>
						<td>${fileItem.isOriginal}</td>
					</tr>
					<tr class="highlight">
						<td>Thumbnail Type:</td>
						<td>${fileItem.thumbnailType}</td>
					</tr>
					<tr>
						<td>Created Time:</td>
						<td><fmt:formatDate value="${fileItem.createdTime}"
								pattern="yyyy-MM-dd HH:mm:ss" /></td>
					</tr>
					<tr>
						<td>Last Modified:</td>
						<td><fmt:formatDate value="${fileItem.lastModified}"
								pattern="yyyy-MM-dd HH:mm:ss" /></td>
					</tr>
					<tr>
						<td>Url:</td>
						<td>${fileItem.originalUrl}</td>
					</tr>
					<tr>
						<td colspan="2"><c:choose>
								<c:when test="${fileItem.file.fileType.isImage}">
									<img src="${fileItem.originalUrl}">
								</c:when>
								<c:otherwise>
									<a href="${fileItem.originalUrl}" target="_blank">Download</a>
								</c:otherwise>
							</c:choose></td>
					</tr>
				</table>
				<br />
				<hr style="width: 100%" />
				<br />
			</c:forEach>
		</c:when>
		<c:otherwise>
			<table>
				<tr class="highlight">
					<td>File Type:</td>
					<td>Raw</td>
				</tr>
			</table>
			<br />
				<hr style="width: 100%" />
			<br />
			<table>
				<tr>
					<td>Url:</td>
					<td>${actionBean.originalUrl}</td>
				</tr>
				<tr>
					<td colspan="2">
						<img src="${actionBean.originalUrl}">
					</td>
				</tr>
			</table>
		</c:otherwise>
	</c:choose>
</div>