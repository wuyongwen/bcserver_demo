$(document).ready(function(){
	
	$("#progressDialog").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false
	});
	
	var createLookType = function(lookTypeId, iconId, iconUrl) {
		$("#progressDialog").append("Creating look type ...<br>");
		var nameMap = {};
		nameMap[$('#locale').find(":selected").val()] = $("#typeName").val();
		var data = "typeNameMap=" + JSON.stringify(nameMap);
		data += "&typeCodeName=" + $("#typeCodeName").val();
		if(lookTypeId != null)
			data += "&lookTypeId=" + lookTypeId;
		data += "&iconId=" + iconId;
		data += "&iconUrl=" + iconUrl;
		data += "&isVisible=" + $("#isVisible").find(":selected").val();
		$.post("look-type-manager.action?create", data, function(responseJson) {
			window.location.href = "./look-type-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	
	};
	
	$("#create").click(function() {
		if($("#typeName").val().length <= 0) {
			alert("A look type must have a name!");
			return;
		}
		
		$("#progressDialog").dialog("open");
		var iconUrl = $("#iconPreview").attr("src");
		if(iconUrl.substring(0, 5).toLowerCase() == "data:") {
			$("#progressDialog").append("Uploading icon file...<br>");
			addUploadPhotoTask(null, 0, 'Photo', $("#iconPreview").get(0).naturalWidth, $("#iconPreview").get(0).naturalHeight, "", "", "", iconUrl);
		}
		
		if(uploadPhotoTask.length > 0)
		{
			var iconId = "0";
    		fileUploadAll("../file/upload-dataurl.action", function(uploadItem, jsonObj) {
    			iconId = "{0}".format(jsonObj.fileId);
    			iconUrl = "{0}".format(jsonObj.originalUrl);
    			$("#progressDialog").append("Upload complete...<br>");
        	}, function(succeeded) {
        		if(succeeded) {
        			createLookType($("#lookTypeId").val(), iconId, iconUrl);
        		}
        		else {
        			alert("Upload image failed...");
        			$("#editProgress").append("Error...<br>");
        		}
        	}, null); //the position of the last(null) parameter is for the login error handling
		}
		else {
			createLookType($("#lookTypeId").val(), $("#iconInput").attr("iconId"), iconUrl);
		}
	});
	
	$(".deleteTypeBtn").click(function(event) {
		if (confirm("Delete Look Type !!!!") == false) {
			event.preventDefault();
		    return false;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Deleting look type...<br>");
		var data = "lookTypeId=" + $(this).attr("ild");
		$.post("look-type-manager.action?delete", data, function(responseJson) {
			window.location.href = "./look-type-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".showTypeBtn").click(function(event) {
		if (confirm("Show Look Type !!!!") == false) {
			event.preventDefault();
		    return false;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Showing look type...<br>");
		var data = "lookTypeId=" + $(this).attr("ild");
		$.post("look-type-manager.action?show", data, function(responseJson) {
			window.location.href = "./look-type-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".hideTypeBtn").click(function(event) {
		if (confirm("Hide Look Type !!!!") == false) {
			event.preventDefault();
		    return false;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Hiding look type...<br>");
		var data = "lookTypeId=" + $(this).attr("ild");
		$.post("look-type-manager.action?hide", data, function(responseJson) {
			window.location.href = "./look-type-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$("#newType").click(function(){
		window.location.href = "./look-type-manager.action?newType";
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
});

