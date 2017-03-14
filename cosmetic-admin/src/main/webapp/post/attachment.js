var attachmentCount = 1;
var postContentList = [];
var postContentListTemp = [];

var addAttchPhoto = function(attachmentBlock, src) {
	var imageArea = $('<img class="attchPhoto" style="max-height:100px" />');
	imageArea.attr("src", src);
	imageArea.on("click", function() {
		var removeAttchDialog = $("#removeAttachmentdialog");
		var toRemoveImg = removeAttchDialog.find("#toRemoveImg");
		var yesButton = removeAttchDialog.find("#yes");
		var noButton = removeAttchDialog.find("#no");
		toRemoveImg.attr("src",$(this).attr("src"));
		yesButton.attr("elmIndex",$(this).attr("elmIndex"));
		yesButton.unbind('click').click(function() {
			var elmIndex = $(this).attr("elmIndex");
			$("#attachDisplayImg"+elmIndex).remove();
			var attachMetadata = $("#attachmentMetadata"+elmIndex);
			if(attachMetadata.length > 0)
				attachMetadata.remove();		
			for(var idx = 0; idx < uploadPhotoTask.length; idx++) {
				if(uploadPhotoTask[idx].attachmentId == elmIndex)
					uploadPhotoTask.splice(idx,1);
			}
			removeAttchDialog.dialog("close");
		});
		noButton.unbind('click').click(function() {
			removeAttchDialog.dialog("close");
		});
		
		removeAttchDialog.dialog("open");
		return;
	});
	
	imageArea.attr("id", "attachDisplayImg" + (++attachmentCount));
	imageArea.attr("elmIndex",attachmentCount);
	imageArea.insertBefore(attachmentBlock.find("#attachmentButton"));
	var attachmentDisplay = attachmentBlock.find("#attachmentDisplay");
	attachmentDisplay.animate({ scrollTop: attachmentDisplay[0].scrollHeight}, 100);
	return attachmentCount;
};

var genMetadata = function (fileId, fileType, url, imgDesc, redirectUrl, storeUrl, width, height, size, md5, orientation) {
	var newMetadata = "{\"width\":" + width + ",";
	newMetadata += "\"height\":" + height + ",";
	newMetadata += "\"fileId\":" + fileId + ",";
	newMetadata += "\"fileSize\":" + size + ",";
	newMetadata += "\"fileType\":\"" + fileType + "\"" + ",";
	newMetadata += "\"md5\":\"" + md5+ "\",";
	newMetadata += "\"orientation\": " + orientation + ",";
	newMetadata += "\"originalUrl\":\"" + url + "\"" + ",";
	newMetadata += "\"imageDescription\":\"" + imgDesc + "\"" + ",";
	newMetadata += "\"storeUrl\":\"" + storeUrl + "\"" + ",";
	newMetadata += "\"redirectUrl\":\"" + redirectUrl + "\"}";
	return newMetadata;
};

var genAttachMetadata = function (attachmentId, attachmentBlock, fileId, fileType, url, imgDesc, redirectUrl, storeUrl, width, height, size, md5, orientation) {
	var metadataDiv = $('<div name="attachments" />');
	var newMetadata = genMetadata(fileId, fileType, url, imgDesc, redirectUrl, storeUrl, width, height, size, md5, orientation);
	metadataDiv.attr("id", ("attachmentMetadata" + attachmentId));
	metadataDiv.text(newMetadata);
	var attachmentMetadatas = attachmentBlock.find("#attachmentMetadatas");
	attachmentMetadatas.append(metadataDiv);
	var breakLine = $('<br />');
	attachmentMetadatas.append(breakLine);
};

var uploadAll = function (onComplete) {
	$("#postProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
	fileUploadAll("../file/upload-dataurl.action", function(uploadItem, jsonObj) {
		genAttachMetadata($(uploadItem["attachmentId"]), $(uploadItem["attchBlock"]), jsonObj.fileId, jsonObj.fileType, "{0}//{1}/api/file/download-file.action?fileId={2}".format(window.location.protocol, window.location.host, jsonObj.fileId), jsonObj.metadata.imageDescription, jsonObj.metadata.redirectUrl, jsonObj.metadata.storeUrl, jsonObj.metadata.width, jsonObj.metadata.height, jsonObj.metadata.fileSize, jsonObj.metadata.md5, jsonObj.metadata.orientation);
		var remainItem = uploadPhotoTask.length;
		if(remainItem > 0)
			$("#postProgress").append("{0} file remaining<br>".format(remainItem));
	}, 
	onComplete, 
	function(){
		$("#loginDialog").dialog("open");
		$("#postProgress").append("<br>---<br>");
	});
};

$(document).ready(function(){
	attachmentCount = $("#attachmentMetadatas").find("div[name=attachments]").length;
	var oriWidth = 0;
	var oriHeight = 0;
	var sourceType = 'URL';
	var currentAttachmentBlock = null;
	
	var resetAttachmentJs = function() {
		oriWidth = 0;
		oriHeight = 0;
		sourceType = 'URL';
		$("#attachmentImg").attr("src","");
		$("#photoUrl").val("");	
		$("#inputAttachImage").val("");
		$("#imageDescription").val("");
		$("#redirectUrl").val("");
		$("#storeUrl").val("");
		$("#addAttachImgBut").hide();
	};
	
	var getResponse = function(type){
		var attachmentId = addAttchPhoto(currentAttachmentBlock, $("#attachmentImg").attr("src"));
		if(type == 'URL') {
			if($("#photoUrl").val().length > 0) {
				var redirectUrl = $("#redirectUrl").val();
				var storeUrl = $("#storeUrl").val();
				if(redirectUrl.length > 0)
					redirectUrl = $("#redirectUrlProtocol option:selected" ).val() + redirectUrl;
				else
					redirectUrl = "";
				if(storeUrl.length <= 0)
					storeUrl = null;
				genAttachMetadata(attachmentId, currentAttachmentBlock, 0, 'Photo', $("#photoUrl").val(), $("#imageDescription").val(), redirectUrl, storeUrl, oriWidth, oriHeight, 0, "", 1);
			}
		}
		else {
			if($("#inputAttachImage").val().length > 0) {
				var redirectUrl = $("#redirectUrl").val();
				var storeUrl = $("#storeUrl").val();
				if(redirectUrl.length > 0)
					redirectUrl = $("#redirectUrlProtocol option:selected" ).val() + redirectUrl;
				else
					redirectUrl = "";
				addUploadPhotoTask(currentAttachmentBlock, attachmentId, 'Photo', oriWidth, oriHeight, $("#imageDescription").val(), redirectUrl, storeUrl, $("#attachmentImg").attr("src"));
			}
		}		
		
		resetAttachmentJs();
		$("#dialog").dialog("close");
	}
	
	$("#removeAttachmentdialog").dialog({
		autoOpen: false,
		maxWidth:600,
        maxHeight: 500,
        width: 600,
        height: 500
	});
	
	$("#dialog").dialog({
		autoOpen: false,
		maxWidth:600,
        maxHeight: 500,
        width: 600,
        height: 500,
        modal: true,
        title: "Select Photo..."
	});
	
	var fillRedirectUrl = function(url) {
		if(url.match(/^(?:http:\/\/)/)) {
			$("#dialog #redirectUrlProtocol option[value='http://']").prop('selected', true);
			$("#dialog #redirectUrl").val(url.substring(7));
		}
		else if(url.match(/^(?:https:\/\/)/)) {
			$("#dialog #redirectUrlProtocol option[value='https://']").prop('selected', true);
			$("#dialog #redirectUrl").val(url.substring(8));
		}
	};
	
	$("#attachmentButton").on("click", function() {
		currentAttachmentBlock = $(this).parent().parent();
		var attachPhotos = $(this).parent().find(".attchPhoto"); 
		for(var idx = 0; idx < attachPhotos.length; idx++) {
			var attachPhoto = $(attachPhotos[idx]);
			if(attachPhoto.attr("src") && attachPhoto.attr("src").length > 0) {
				alert("Only one attachment is allow in one single main-post/sub-post!");
				return;
			}
		}
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var exRedirectUrlLbl = $("#exRedirectUrl");
		if(exRedirectUrlLbl.length > 0) {
			fillRedirectUrl(exRedirectUrlLbl.text());			
		}
		var exLookStoreUrlLbl = $("#exLookStoreUrl");
		if(exLookStoreUrlLbl.length > 0) {
			$("#dialog #storeUrl").val(exLookStoreUrlLbl.text());			
		}
		$("#dialog").dialog("open");
		return;
	});

	$("#attachmentImg").load(function() {
		oriWidth = this.naturalWidth;
		oriHeight = this.naturalHeight;
		$("#addAttachImgBut").show();
	});
	
	var inputAttachImage = $("#inputAttachImage");
	if (window.FileReader) {
      inputAttachImage.change(function() {
        var fileReader = new FileReader();
        var files = this.files;
        if (!files.length) {
          return;
        }

        var file = files[0];
        if(file.size > 10000000) {
        	alert("File size should not exceed 10MB.");
        	return;
        }
        if (/^image\/\w+$/.test(file.type)) {
          fileReader.readAsDataURL(file);
          fileReader.onload = function () {
        	$("#attachmentImg").attr("src", this.result);
			sourceType = 'FILE';
			$("#photoUrl").val("");
          };
        } else {
          alert("Please choose an image file.");
        }
      });
    } else {
      inputAttachImage.addClass("hide");
    }
	
	$("#addAttachImgBut").click(function(e) {
		if(!$("#attachmentImg").attr("src") || $("#attachmentImg").attr("src").length <= 0 || oriWidth == 0 || oriHeight == 0) {
			resetAttachmentJs();
			$("#dialog").dialog("close");
			return;
		}
		getResponse(sourceType);
	});
	
	$("#getImage").click(function(e){
		var photoUrl = $("#photoUrl").val();	
		$("#attachmentImg").attr("src",photoUrl);
		sourceType = 'URL';
		$("#inputAttachImage").val("")
	});
	
	$(".attchPhoto").click(function() {
		var removeAttchDialog = $("#removeAttachmentdialog");
		var toRemoveImg = removeAttchDialog.find("#toRemoveImg");
		var yesButton = removeAttchDialog.find("#yes");
		var noButton = removeAttchDialog.find("#no");
		toRemoveImg.attr("src",$(this).attr("src"));
		yesButton.attr("elmIndex",$(this).attr("elmIndex"));
		yesButton.unbind('click').click(function() {
			var elmIndex = $(this).attr("elmIndex");
			$("#attachDisplayImg"+elmIndex).remove();
			var attachMetadata = $("#attachmentMetadata"+elmIndex);
			if(attachMetadata.length > 0)
				attachMetadata.remove();		
			removeAttchDialog.dialog("close");
		});
		noButton.unbind('click').click(function() {
			removeAttchDialog.dialog("close");
		});
		
		removeAttchDialog.dialog("open");
		return;
	});

	$("#redirectUrl").on('paste', function () {
		var $this = $(this);
		  setTimeout(function () {
			  fillRedirectUrl($this.val());
		  }, 100);
	});
});