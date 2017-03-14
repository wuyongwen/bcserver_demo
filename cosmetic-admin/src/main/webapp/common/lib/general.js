String.prototype.hashCode = function() {
	var hash = 0, i, chr, len;
	if (this.length == 0) return hash;
	for (i = 0, len = this.length; i < len; i++) {
	  chr   = this.charCodeAt(i);
	  hash  = ((hash << 5) - hash) + chr;
	  hash |= 0;
	}
	return hash;
};

String.prototype.format = function() {
	  var str = this;
	  for (var i = 0; i < arguments.length; i++) {       
	    var reg = new RegExp("\\{" + i + "\\}", "gm");             
	    str = str.replace(reg, arguments[i]);
	  }
	  return str;
};

var uploadPhotoTask = [];
var uploadPhotoTaskTemp = [];
var fileUploadAll = function (uploadUrl, onProgress, onComplete, onLogin) {
	if(uploadPhotoTask.length <= 0) {
		onComplete(true);
		return;
	}
	
	//$("#postProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
	var uploadItem = uploadPhotoTask.shift();
	var data = new FormData();
	data.append("dataUrl", uploadItem["srcData"]);
	var redirectUrlValue = uploadItem["redirectUrl"];
	var imgDescValue = uploadItem["imgDesc"];
	var storeUrlValue = uploadItem["storeUrl"];
	if(redirectUrlValue == null || redirectUrlValue.length <= 0)
		redirectUrlValue = "";
	if(imgDescValue == null || imgDescValue.length <= 0)
		imgDescValue = "";
	if(storeUrlValue == null || storeUrlValue.length <= 0)
		storeUrlValue = "";
	data.append("metadata", "{\"width\":{0},\"height\":{1},\"redirectUrl\":\"{2}\",\"imageDescription\":\"{3}\",\"lookStoreUrl\":\"{4}\"}".format(uploadItem["width"], uploadItem["height"], redirectUrlValue, imgDescValue, storeUrlValue));
	data.append("fileType", uploadItem["type"]);
	
	jQuery.ajax({
	    url: uploadUrl,
	    data: data,
	    cache: false,
	    mimeType: "multipart/form-data",
	    contentType: false,
	    processData: false,
	    type: 'POST',
	    success: function(response, status){
	    	if(response!=null && response.length > 0){
	    		try {
	    			var responseJson = jQuery.parseJSON(response);
	    			if(responseJson == "You need to login"){
	    				alert("You need to login");
	    				onComplete(false);
	    				onLogin(); //open the login panel
	    				return;
	    			}
	    			var jsonObj = jQuery.parseJSON(responseJson);
		    		if(jsonObj.fileId == null) {
		    			alert("Failed to upload image : " + e.responseText);
			    		onComplete(false);
		    		}
			    	onProgress(uploadItem, jsonObj); //upload to server successfully, call for generating Attachment Metadata
	    		}
	    		catch(e) {
	    			alert("Failed to upload image : " + e.responseText);
	    			onComplete(false);
	    			return;
	    		}
	    		fileUploadAll(uploadUrl, onProgress, onComplete, onLogin);
	    	 }         		
	    },
      error: function (jqXHR, textStatus, errorThrown) {
      	alert("Failed to upload image : " + errorThrown);
  		onComplete(false);
  		return;
      }
  	});
};

var fileUploadAll_v2 = function (uploadUrl, updateUrl, onProgress, onComplete) {
	if(uploadPhotoTask.length <= 0) {
		onComplete(true);
		return;
	}
	
	//$("#postProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
	var uploadItem = uploadPhotoTask.shift();

	var requestUrl;
	var data = new FormData();
	if (uploadItem["fileId"] != undefined && uploadItem["fileId"] != null && uploadItem["fileId"] != "") {
		requestUrl = updateUrl;
		data.append("fileId", uploadItem["fileId"]);
		data.append("redirectUrl", uploadItem["redirectUrl"]);
		data.append("fileType", uploadItem["type"]);
		data.append("isWidget", uploadItem["isWidget"]);
	}
	else {
		requestUrl = uploadUrl;
		data.append("dataUrl", uploadItem["srcData"]);
		data.append("metadata", "{\"width\":{0},\"height\":{1},\"redirectUrl\":\"{2}\",\"imageDescription\":\"{3}\",\"isWidget\":\"{4}\"}".format(uploadItem["width"], uploadItem["height"], uploadItem["redirectUrl"], uploadItem["imgDesc"], uploadItem["isWidget"]));
		data.append("fileType", uploadItem["type"]);
	}
	
	jQuery.ajax({
	    url: requestUrl,
	    data: data,
	    cache: false,
	    mimeType: "multipart/form-data",
	    contentType: false,
	    processData: false,
	    type: 'POST',
	    success: function(response, status){
	    	if(response!=null && response.length > 0){
	    		try {
	    			var responseJson = jQuery.parseJSON(response);
	    			var jsonObj = jQuery.parseJSON(responseJson);
					if(jsonObj.fileId == null) {
	    				alert("Failed to upload image : " + e.responseText);
		    			onComplete(false);
	    			}
	    			onProgress(uploadItem, jsonObj);
	    		}
	    		catch(e) {
	    			alert("Failed to upload image : " + e.responseText);
	    			onComplete(false);
	    			return;
	    		}
	    		fileUploadAll_v2(uploadUrl, updateUrl, onProgress, onComplete);
	    	 }         		
	    },
      error: function (jqXHR, textStatus, errorThrown) {
      	alert("Failed to upload image : " + errorThrown);
  		onComplete(false);
  		return;
      }
  });
};

var addUploadPhotoTask = function(attchBlockVal, attachmentIdVal, typeVal, widthVal, heightVal, imgDescVal, redirectUrlVal, storeUrlVal, srcDataVal) {
	uploadPhotoTask.push({attachmentId:attachmentIdVal , attchBlock:attchBlockVal, type:typeVal, width:widthVal, height:heightVal, imgDesc:imgDescVal, redirectUrl:redirectUrlVal, storeUrl:storeUrlVal, srcData:srcDataVal});
	uploadPhotoTaskTemp.push({attachmentId:attachmentIdVal , attchBlock:attchBlockVal, type:typeVal, width:widthVal, height:heightVal, imgDesc:imgDescVal, redirectUrl:redirectUrlVal, storeUrl:storeUrlVal, srcData:srcDataVal});
};

var addUploadPhotoTask_v2 = function(attachmentIdVal, contentVal, fileIdVal, typeVal, widthVal, heightVal, imgDescVal, redirectUrlVal, srcDataVal, isWidgetVal) {
	uploadPhotoTask.push({attachmentId:attachmentIdVal, content:contentVal, fileId:fileIdVal, type:typeVal, width:widthVal, height:heightVal, imgDesc:imgDescVal, redirectUrl:redirectUrlVal, srcData:srcDataVal, isWidget:isWidgetVal});
};

var openInNewTab = function (url) {
	var popup = window.open("about:blank", "Beauty Circle");
	popup.location = url;
}

var hyperlinkCtrl = function(hrefValue, event) {
	if(event.preventDefault) 
		event.preventDefault();
	else
		event.returnValue = false;
	if(hrefValue.indexOf("ybc://") >= 0) {
		var path = hrefValue.substring(6).toLowerCase();
		if(path.indexOf("post") >= 0) {
			var postId = path.substring(5);
			if(postId.length > 0) {
				window.location.href = "queryPost.action?postId=" + postId;
			}
		}
		else {
			return;
		}
	}
	else if(hrefValue.indexOf("ycp://") >= 0) {
		return;
	}
	else if(hrefValue.indexOf("ymk://") >= 0) {
		return;
	}
	else {
		openInNewTab(hrefValue);
	}
};