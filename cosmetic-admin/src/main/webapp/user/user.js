$(document).ready(function(){
	$("#croppingArea").dialog({
		autoOpen: false,
		maxWidth:700,
	    maxHeight: 700,
	    minWidth: 700,
	    minHeight: 700,
	    modal: true,
	    title: "Select Image..."
	});
	
	$("#editProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Editing..."
	});
	
	var $image = $(".img-container img");
    var options = {
      aspectRatio: 1,
    };
	
	$image.cropper(options).on({
	      "build.cropper": function(e) {
	        console.log(e.type);
	      },
	      "built.cropper": function(e) {
	        console.log(e.type);
	      }
	    });
	
	var $inputAvatarImage = $("#usrAvatarInput");
	var $inputCoverImage = $("#usrCoverInput");
	var $inputBgImage = $("#usrBgImageInput");
	var $inputIconImage = $("#usrIconInput");
	
	var coverUploadTask = null;
	var avatarUploadTask = null;
	var bgImageUploadTask = null;
	var iconUploadTask = null;
	var fileInputType;
	
    $("#confirmAvatar").click(function(e){
    	var srcDataUrl = $image.cropper("getDataURL", fileInputType);
    	var previewCover = $("#usrAvatar"); 
    	previewCover.attr("src", srcDataUrl);
    	previewCover.load(function() {
    		avatarUploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:srcDataUrl};
    		$("#croppingArea").dialog("close");
    	});
    });
    
    $("#confirmCover").click(function(e){
    	var srcDataUrl = $image.cropper("getDataURL", fileInputType);
    	var previewCover = $("#usrCover"); 
    	previewCover.attr("src", srcDataUrl);
    	previewCover.load(function() {
    		coverUploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:srcDataUrl};
    		$("#croppingArea").dialog("close");
    	});
    });
    
    var inputChange = function(input, ratio, attachmentId) {
        var fileReader = new FileReader(),
            files = input.files,
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
          fileInputType = file.type;
          fileReader.readAsDataURL(file);
          fileReader.onload = function () {
        	  var fileSource = this;
        	  if (attachmentId == 2) {
        		  var previewCover = $("#bgImage"); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  bgImageUploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
              } else if (attachmentId == 3) {
        		  var previewCover = $("#icon"); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  iconUploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
              }  else {
            	  $("#croppingArea").dialog("open");
            	  $image.cropper("reset", true).cropper("replace", fileSource.result);
            	  $image.cropper("setAspectRatio", ratio);
              }
          };
        } else {
          alert("Please choose an image file.");
        }
    };
    
    if (window.FileReader) {
      $inputAvatarImage.change(function() {
    	  $("#confirmAvatar").show();
		  $("#confirmCover").hide();
		  inputChange(this, 1, 0);
	  });
      $inputCoverImage.change(function() {
    	  $("#confirmAvatar").hide();
		  $("#confirmCover").show();
    	  inputChange(this, 4/3, 1);
      });
      $inputBgImage.change(function() {
		  inputChange(this, null, 2);
	  });
      $inputIconImage.change(function() {
		  inputChange(this, null, 3);
	  });
    } else {
    	$inputAvatarImage.addClass("hide");
        $inputCoverImage.addClass("hide");
    }
    
    $('form').submit(function(event){
    	var formElm = this;
    	if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
    	
    	if(uploadPhotoTask.length > 0)
		{
    		$("#editProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
    		fileUploadAll("../file/upload-dataurl.action", function(uploadItem, jsonObj) {
        		if(uploadItem["attachmentId"] == 0)
        			$("#avatarId").attr("value","{0}".format(jsonObj.fileId));
        		else if(uploadItem["attachmentId"] == 1)
        			$("#coverId").attr("value","{0}".format(jsonObj.fileId));
        		else if(uploadItem["attachmentId"] == 2)
        			$("#bgImageId").attr("value","{0}".format(jsonObj.fileId));
        		else if(uploadItem["attachmentId"] == 3)
        			$("#iconId").attr("value","{0}".format(jsonObj.fileId));
        		if(uploadPhotoTask.length > 0)
        			$("#editProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
        	}, function(succeeded) {
        		if(succeeded)
        			$("#saveUsrEdit").click();
        		else {
        			alert("Upload image failed...");
        			$("#editProgress").append("Error...<br>");
        		}
        	}, null); //the position of the last(null) parameter is for the login error handling
		}
    	else {
    		$("#editProgress").append("Eidting user profile...<br>");
    		formElm.submit();
    	}
    });
    
    $("#saveUsrEdit").click(function(){
    	$("#editProgress").dialog("open");
    	if(coverUploadTask != null) {
    		addUploadPhotoTask("", 1, 'Photo', coverUploadTask["width"], coverUploadTask["height"], "", "", "", coverUploadTask["srcData"]);
    		coverUploadTask = null;
    	}
    		
    	if(avatarUploadTask != null) {
    		addUploadPhotoTask("", 0, 'Avatar', avatarUploadTask["width"], avatarUploadTask["height"], "", "", "", avatarUploadTask["srcData"]);
    		avatarUploadTask = null;
    	}
    	
    	if(bgImageUploadTask != null) {
    		addUploadPhotoTask("", 2, 'Photo', bgImageUploadTask["width"], bgImageUploadTask["height"], "", "", "", bgImageUploadTask["srcData"]);
    		bgImageUploadTask = null;
    	}
    	
    	if(iconUploadTask != null) {
    		addUploadPhotoTask("", 3, 'Photo', iconUploadTask["width"], iconUploadTask["height"], "", "", "", iconUploadTask["srcData"]);
    		iconUploadTask = null;
    	}
    		
    });
});