$(document).ready(function(){
	
	$("#newGroup").click(function() {
		window.location.href = "./circle-type-group-manager.action?newGroup";
	});
	
	$("#progressDialog").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false
	});
	
	var createCircleType = function(circleTypeGroupId, iconId, uploadedImgUrl) {
		$("#progressDialog").append("Creating circle ...<br>");
		var data = "typeGroupName=" + encodeURIComponent($("#groupName").val());
		if(circleTypeGroupId != null)
			data += "&circleTypeGroupId=" + circleTypeGroupId;
		data += "&typeGroupOrder=" + $("#typeGroupOrder").val();
		data += "&iconId=" + iconId;
		if(uploadedImgUrl !== undefined)
			data += "&imgUrl=" + encodeURIComponent(uploadedImgUrl);
		var typeNames = $(".typeName");
		if(typeNames.length > 0) {
			data += "&typeNameMap={";
			var typeNameMaps = "";
			typeNames.each(function() {
				typeNameMaps += "\"" + $(this).get(0).id + "\":\"" + $(this).val() + "\",";
			});
			if(typeNameMaps.length > 0) {
				typeNameMaps = encodeURIComponent(typeNameMaps.substr(0, typeNameMaps.length - 1));
			}
			
			data += typeNameMaps;
			data += "}";
		}
			
		$.post("circle-type-group-manager.action?create", data, function(responseJson) {
			window.location.href = "./circle-type-group-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	
	};
	
	$("#create").click(function() {
		/*if($("#iconPreview").attr("src").length <= 0) {
			alert("A circle must have an icon image!");
			return;
		}*/
			
		if($("#groupName").val().length <= 0) {
			alert("A circle type group must have a name!");
			return;
		}
		
		$("#progressDialog").dialog("open");
		var iconUrl = $("#iconPreview").attr("src");
		if(iconUrl.substring(0, 5).toLowerCase() == "data:") {
			$("#progressDialog").append("Uploading icon file...<br>");
			addUploadPhotoTask(null, 0, 'Photo', $("#iconPreview").get(0).naturalWidth, $("#iconPreview").get(0).naturalHeight, "", "", "", iconUrl);
		}
		
		var imgUrl = $("#imgPreview").attr("src");
		if(imgUrl.substring(0, 5).toLowerCase() == "data:") {
			$("#progressDialog").append("Uploading image file...<br>");
			addUploadPhotoTask(null, 1, 'Photo', $("#imgPreview").get(0).naturalWidth, $("#imgPreview").get(0).naturalHeight, "", "", "", imgUrl);
		}
		
		if(uploadPhotoTask.length > 0)
		{
			var iconId = $("#iconInput").attr("iconId");
			var uploadedImgUrl = $("#imgInput").attr("imgUrl");
    		fileUploadAll("../file/upload-dataurl.action", function(uploadItem, jsonObj) {
    			if(uploadItem.attachmentId == 0) {
    				iconId = "{0}".format(jsonObj.fileId);
        			$("#progressDialog").append("Upload icon complete...<br>");	
    			}
    			else if(uploadItem.attachmentId == 1) {
    				uploadedImgUrl = "{0}".format(jsonObj.originalUrl);
        			$("#progressDialog").append("Upload image complete...<br>");	
    			}
        	}, function(succeeded) {
        		if(succeeded) {
        			createCircleType($("#groupId").val(), iconId, uploadedImgUrl);
        		}
        		else {
        			alert("Upload image failed...");
        			$("#editProgress").append("Error...<br>");
        		}
        	}, null); //the position of the last(null) parameter is for the login error handling
		}
		else {
			createCircleType($("#groupId").val(), $("#iconInput").attr("iconId"), $("#imgInput").attr("imgUrl"));
		}
	});
	
	$(".publish").click(function() {
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Publishing circle group...<br>");
		var data = "circleTypeId=" + $(this).get(0).id + "&isVisible=1";
		$.post("circle-type-group-manager.action?publishType", data, function(responseJson) {
			location.reload();
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".unpublish").click(function() {
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Publishing circle group...<br>");
		var data = "circleTypeId=" + $(this).get(0).id + "&isVisible=0";
		$.post("circle-type-group-manager.action?publishType", data, function(responseJson) {
			location.reload();
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".delete").click(function() {
		if (confirm("Delete circle !!!!") == false) {
		    return;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Deleting circle group...<br>");
		var data = "circleTypeGroupId=" + $(this).get(0).id;
		$.post("circle-type-group-manager.action?deleteGroup", data, function(responseJson) {
			window.location.href = "./circle-type-group-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".add").click(function(){
		window.location.href = "./circle-type-group-manager.action?newGroup&circleTypeGroupId=" + $(this).get(0).id;
	});
	
	$("#iconInput").change(function() {
        var fileReader = new FileReader(),
            files = this.files,
            file;

        if (!files.length) {
          return;
        }

        file = files[0];
        if(file.size > 10000000) {
        	alert("File size should not exceed 10MB.");
        	return;
        }
        if (/^image\/\w+$/.test(file.type)) {
          fileReader.readAsDataURL(file);
          fileReader.onload = function () {
            $("#iconPreview").attr("src", this.result);
          };
        } else {
          alert("Please choose an image file.");
        }
	});
	
	$("#imgInput").change(function() {
        var fileReader = new FileReader(),
            files = this.files,
            file;

        if (!files.length) {
          return;
        }

        file = files[0];
        if(file.size > 10000000) {
        	alert("File size should not exceed 10MB.");
        	return;
        }
        if (/^image\/\w+$/.test(file.type)) {
          fileReader.readAsDataURL(file);
          fileReader.onload = function () {
            $("#imgPreview").attr("src", this.result);
          };
        } else {
          alert("Please choose an image file.");
        }
    
	});
});

