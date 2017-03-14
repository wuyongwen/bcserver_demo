$(document).ready(function(){
	
	var locales = $('#locales').val();
	var localeArray =locales.split(" ");
	
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

	var pickervalues = [];
	var d = [];
	var opt = [];
	for(i = 0; i < localeArray.length ; i++){
		pickervalues[i] = $("#datetimepicker-"+localeArray[i]).val();
		
		if(pickervalues[i] == null || pickervalues[i] == ""){
			d[i] = null;
		}else{
			d[i] = new Date(pickervalues[i]);
		}
		
		opt[i]={dateFormat: 'yy-mm-dd',
				showSecond: true,
				timeFormat: 'HH:mm:ss',
	            timezone: zone,
	            showTimezone: false,
	            defaultValue: (d[i]==null)?"":d[i].yyyyMMddHHmmss()
	           };
		
		$("#datetimepicker-"+localeArray[i]).datetimepicker(opt[i]);
		$("#datetimepicker-"+localeArray[i]).val((d[i]==null)?"":d[i].yyyyMMddHHmmss());
		$("#datetimepicker-"+localeArray[i]).change(function() {
			current = new Date();
			selected = new Date($(this).val());
			var locale = $(this).attr("locale");
			if (current > selected) {
				$(this).css("background-color","#FF0000");
				$("#hint-"+locale).text("Incorrect time!");
				$("#hint-"+locale).css("color", "#FF0000");
			} else {
				$(this).css("background-color","#FFFFFF");
				$("#hint-"+locale).text("");
			}
		});
	}
	
	
	$("#editProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Editing..."
	});
	
	var $inputFileImage = [];
	var $inputFile720Image = [];
	var $inputFile1080Image = [];
	
	var fileUploadTask = [];
	var file720UploadTask = [];
	var file1080UploadTask = [];
	
	var fileInputType;
	
	for(i = 0; i < localeArray.length ; i++){
		$inputFileImage[i] = $("#campFileInput-"+localeArray[i]);
		$inputFile720Image[i] = $("#campFile720Input-"+localeArray[i]);
		$inputFile1080Image[i] = $("#campFile1080Input-"+localeArray[i]);
		fileUploadTask[i] = null;
		file720UploadTask[i] = null;
		file1080UploadTask[i] = null;
	}

	var fileInputType;

    var inputChange = function(input, ratio, attachmentId) {
    	var index = parseInt(attachmentId/3);
        var fileReader = new FileReader(),
            files = input.files,
            file;
        if (!files.length) {
          return;
        }
        var locale = input.getAttribute("locale")
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
        	  if ((attachmentId%3) == 0){
        		  var previewCover = $("#imgfile-"+locale); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  fileUploadTask[index] = [];
        			  fileUploadTask[index] = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
        	  }else if ((attachmentId%3) == 1) {
        		  var previewCover = $("#imgfile720-"+locale);
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  file720UploadTask[index] = [];
        			  file720UploadTask[index] = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
              } else if ((attachmentId%3) == 2) {
        		  var previewCover = $("#imgfile1080-"+locale); 
        		  previewCover.attr("src", fileSource.result);
        		  previewCover.load(function() {
        			  file1080UploadTask[index] = [];
        			  file1080UploadTask[index] = {width:previewCover[0].naturalWidth, height:previewCover[0].naturalHeight, srcData:fileSource.result};
        		  });
              }
          };
        } else {
          alert("Please choose an image file.");
        }
    };
	
	if (window.FileReader) {
		for(i = 0; i < localeArray.length ; i++){
			$inputFileImage[i].change(function() {inputChange(this, null, $(this).attr("index"));});
			$inputFile720Image[i].change(function() {inputChange(this, null, $(this).attr("index"));});
			$inputFile1080Image[i].change(function() {inputChange(this, null, $(this).attr("index"));});
		}
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
    			try{
    			var attachmentId = parseInt(uploadItem["attachmentId"]);
    			var index = parseInt(attachmentId/3);
        		if(attachmentId%3 == 0)
        			$("#fileId-"+localeArray[index]).attr("value","{0}".format(jsonObj.fileId));
        		else if(attachmentId%3 == 1)
        			$("#file720Id-"+localeArray[index]).attr("value","{0}".format(jsonObj.fileId));
        		else if(attachmentId%3 == 2)
        			$("#file1080Id-"+localeArray[index]).attr("value","{0}".format(jsonObj.fileId));
        		if(uploadPhotoTask.length > 0)
        			$("#editProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
    			}catch(e){
    				alert(e);
    			}
        	}, function(succeeded) {
        		if(succeeded){
        			$("#saveFileEdit").click();
        		}
        		else {
        			alert("Upload image failed...");
        			$("#editProgress").append("Error...<br>");
        		}
        	}, null); //the position of the last(null) parameter is for the login error handling
		}
    	else {
    		$("#editProgress").append("Eidting campaign profile...<br>");
			var campaignList = [];
			for(i = 0; i < localeArray.length ; i++){
				campaignList.push(new Campaign(
						$("#campaignGroupId-"+localeArray[i]).attr("value"),
						localeArray[i],
						$("#fileId-"+localeArray[i]).attr("value"),
						$("#file720Id-"+localeArray[i]).attr("value"),
						$("#file1080Id-"+localeArray[i]).attr("value"),
						$("#link-"+localeArray[i]).val(),
						$("#datetimepicker-"+localeArray[i]).val()));
			}
			$("#campaignListJsonString").attr("value",JSON.stringify(campaignList));
    		formElm.submit();
    	}
    });
	
    /**
     * Campaign Object
     */
    function Campaign(campaignGroupId, locale, fileId, file720Id, file1080Id, link, endDate){
    	this.campaignGroupId = campaignGroupId;
    	this.locale = locale;
    	this.fileId = fileId;
    	this.file720Id = file720Id;
    	this.file1080Id = file1080Id;
    	this.link = link;
    	this.endDate = endDate;
    }
	
    $("#saveFileEdit").click(function(){
    	$("#editProgress").dialog("open");
    	var localeCount = localeArray.length;
    	for(i = 0;i < localeCount ; i++){
	    	if(fileUploadTask[i] != null) {
	    		addUploadPhotoTask("", (i*3), 'Photo', fileUploadTask[i]["width"], fileUploadTask[i]["height"], "", "", "", fileUploadTask[i]["srcData"]);
	    		fileUploadTask[i] = null;
	    	}
	    	if(file720UploadTask[i] != null) {
	    		addUploadPhotoTask("", (i*3+1), 'Photo', file720UploadTask[i]["width"], file720UploadTask[i]["height"], "", "", "", file720UploadTask[i]["srcData"]);
	    		file720UploadTask[i] = null;
	    	}
	    	if(file1080UploadTask[i] != null) {
	    		addUploadPhotoTask("", (i*3+2), 'Photo', file1080UploadTask[i]["width"], file1080UploadTask[i]["height"], "", "", "", file1080UploadTask[i]["srcData"]);
	    		file1080UploadTask[i] = null;
	    	}
		}
    });
});