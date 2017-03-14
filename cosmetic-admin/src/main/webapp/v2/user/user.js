$(document).ready(function(){

    $("#editProgress").dialog({
        autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Editing..."
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
    
    var inputChange = function(input, type) {
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
            var srcDataUrl = this.result;

            if (type == "avatar") {
                var img = $(".icon img");
                img.attr("src", srcDataUrl);
                img.load(function() {
                	avatarUploadTask = {width:img[0].naturalWidth, height:img[0].naturalHeight, srcData:srcDataUrl};
                });
            }
            else if (type == "cover") {
                var img = $(".banner img");
                img.attr("src", srcDataUrl);
                img.load(function() {
                	coverUploadTask = {width:img[0].naturalWidth, height:img[0].naturalHeight, srcData:srcDataUrl};
                });
            }
            else if (type == "icon") {
                var img = $(".icon2 img");
                img.attr("src", srcDataUrl);
                img.load(function() {
                	iconUploadTask = {width:img[0].naturalWidth, height:img[0].naturalHeight, srcData:srcDataUrl};
                });
            }
            else if (type == "background") {
                var img = $(".banner2 img");
                img.attr("src", srcDataUrl);
                img.load(function() {
                	bgImageUploadTask = {width:img[0].naturalWidth, height:img[0].naturalHeight, srcData:srcDataUrl};
                });
            }
          };
        } else {
          alert("Please choose an image file.");
        }
    };
    
    if (window.FileReader) {
      $inputAvatarImage.change(function() {
		  inputChange(this, "avatar");
	  });
      $inputCoverImage.change(function() {
    	  inputChange(this, "cover");
      });
      $inputBgImage.change(function() {
		  inputChange(this, "background");
	  });
      $inputIconImage.change(function() {
		  inputChange(this, "icon");
	  });
    } else {
    	$inputAvatarImage.addClass("hide");
        $inputCoverImage.addClass("hide");
    }
    
    $('form').submit(function(event){
        $("#editProgress").dialog("open");
    	var formElm = this;
    	if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
    	
    	if(uploadPhotoTask.length > 0)
		{
    		$("#editProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
    		fileUploadAll("../../file/upload-dataurl.action", function(uploadItem, jsonObj) {
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
    		$("#editProgress").append("Editing user profile...<br>");
    		formElm.submit();
    	}
    });
    
    $("#saveUsrEdit").click(function(){
        if ($("#displayName").val() == "" || $(".icon img").attr("src") == "" || $(".banner img").attr("src") == "") {
            alert("Please setup display name, profile photo and cover photo");
            return false;
        }
        if ($("#description").val().length > 500) {
            alert("Description max Length:" + 500);
            return false;
        }

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

    $("#displayName").change(function() {
        $("#nameLimit").html($("#displayName").val().length + "/500 characters<br>");
    });

    $("#description").change(function() {
        $("#descLimit").html($("#description").val().length + "/500 characters<br>");
    });

    $(".banner > img").load(function() {
        $(".banner").height("191px");
        $(".banner").width("auto");

        $(this).height("100%");
        $(this).width("auto");

        if ($(this).width() >  $(".banner").parent().width()) {
            $(this).width("100%");
            $(this).height("auto");
        }


        $(".banner").height($(this).height());
        $(".banner").width($(this).width());
        $(".banner > input").height($(".banner").height());
        $(".banner > input").width($(".banner").width());
    });

    $(".icon > img").load(function() {
        $(".icon").height("191px");
        $(".icon").width("191px");

        $(this).height("100%");
        $(this).width("auto");

        if ($(this).width() >  $(".icon").parent().width()) {
            $(this).width("100%");
            $(this).height("auto");
        }

        $(".icon").height($(this).height());
        $(".icon").width($(this).width());
        $(".icon > input").height($(".icon").height());
        $(".icon > input").width($(".icon").width());
    });
    
    $(".icon2 > img").load(function() {
        $(".icon2").height("191px");
        $(".icon2").width("191px");

        $(this).height("100%");
        $(this).width("auto");

        if ($(this).width() >  $(".icon2").parent().width()) {
            $(this).width("100%");
            $(this).height("auto");
        }

        $(".icon2").height($(this).height());
        $(".icon2").width($(this).width());
        $(".icon2 > input").height($(".icon2").height());
        $(".icon2 > input").width($(".icon2").width());
    });
    
    $(".banner2 > img").load(function() {
        $(".banner2").height("191px");
        $(".banner2").width("auto");

        $(this).height("100%");
        $(this).width("auto");

        if ($(this).width() >  $(".banner2").parent().width()) {
            $(this).width("100%");
            $(this).height("auto");
        }

        $(".banner2").height($(this).height());
        $(".banner2").width($(this).width());
        $(".banner2 > input").height($(".banner2").height());
        $(".banner2 > input").width($(".banner2").width());
    });
    
});