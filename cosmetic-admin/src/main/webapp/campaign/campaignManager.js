$(document).ready(function(){
	
	var zone = "+0800";
	
	Date.prototype.yyyyMMddHHmmss = function() {
		var yyyy = this.getFullYear().toString();
		var MM = (this.getMonth()+1).toString(); // getMonth() is zero-based
		var dd  = this.getDate().toString();
		var HH  = this.getHours().toString();
		var mm  = this.getMinutes().toString();
		var ss  = this.getSeconds().toString();
		return yyyy + "-" + (MM[1]?MM:"0"+MM[0]) + "-" + (dd[1]?dd:"0"+dd[0]) + " "  + (HH[1]?HH:"0"+HH[0]) +":" + (mm[1]?mm:"0"+mm[0]) + ":" + (ss[1]?ss:"0"+ss[0]) + " " + zone; // padding
	};
	var pickervalue = $("#datetimepicker").val();
	var d ;
	if(pickervalue == null || pickervalue == ""){
		d = null;
	}else{
		d = new Date(pickervalue);
	}
	var opt={dateFormat: 'yy-mm-dd',
			showSecond: true,
			timeFormat: 'HH:mm:ss',
            timezone: zone,
            showTimezone: false,
            defaultValue: (d==null)?"":d.yyyyMMddHHmmss()
           };
	$('#datetimepicker').datetimepicker(opt);
	$('#datetimepicker').val((d==null)?"":d.yyyyMMddHHmmss());
	
	$("#datetimepicker").change(function() {
		current = new Date();
		selected = new Date($(this).val());
		if (current > selected) {
			$(this).css("background-color","#FF0000");
			$('#hint').text("Incorrect time!");
			$('#hint').css("color", "#FF0000");
		} else {
			$(this).css("background-color","#FFFFFF");
			$('#hint').text("");
			$('#hint').css("color", "#0000FF");
		}
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
	
	var $inputFileImage = $("#campFileInput");
	var $inputFile720Image = $("#campFile720Input");
	var $inputFile1080Image = $("#campFile1080Input");
	
	var fileUploadTask = null;
	var file720UploadTask = null;
	var file1080UploadTask = null;
	var fileInputType;

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
        	  if (attachmentId == 0){
        		  var previewCover = $("#imgfile"); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  fileUploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
        	  }else if (attachmentId == 1) {
        		  var previewCover = $("#imgfile720"); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  file720UploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
              } else if (attachmentId == 2) {
        		  var previewCover = $("#imgfile1080"); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  file1080UploadTask = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
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
		$inputFileImage.change(function() {
			  inputChange(this, null, 0);
		  });
		$inputFile720Image.change(function() {
	    	  inputChange(this, null, 1);
	      });
		$inputFile1080Image.change(function() {
			  inputChange(this, null, 2);
		  });
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
        			$("#fileId").attr("value","{0}".format(jsonObj.fileId));
        		else if(uploadItem["attachmentId"] == 1)
        			$("#file720Id").attr("value","{0}".format(jsonObj.fileId));
        		else if(uploadItem["attachmentId"] == 2)
        			$("#file1080Id").attr("value","{0}".format(jsonObj.fileId));
        		if(uploadPhotoTask.length > 0)
        			$("#editProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
        	}, function(succeeded) {
        		if(succeeded)
        			$("#saveFileEdit").click();
        		else {
        			alert("Upload image failed...");
        			$("#editProgress").append("Error...<br>");
        		}
        	}, null); //the position of the last(null) parameter is for the login error handling
		}
    	else {
    		$("#editProgress").append("Eidting campaign profile...<br>");
    		formElm.submit();
    	}
    });
	
	
    $("#saveFileEdit").click(function(){
    	$("#editProgress").dialog("open");
    	if(fileUploadTask != null) {
    		addUploadPhotoTask("", 0, 'Photo', fileUploadTask["width"], fileUploadTask["height"], "", "", "", fileUploadTask["srcData"]);
    		fileUploadTask = null;
    	}
    		
    	if(file720UploadTask != null) {
    		addUploadPhotoTask("", 1, 'Photo', file720UploadTask["width"], file720UploadTask["height"], "", "", "", file720UploadTask["srcData"]);
    		file720UploadTask = null;
    	}
    	
    	if(file1080UploadTask != null) {
    		addUploadPhotoTask("", 2, 'Photo', file1080UploadTask["width"], file1080UploadTask["height"], "", "", "", file1080UploadTask["srcData"]);
    		file1080UploadTask = null;
    	}
    });
});