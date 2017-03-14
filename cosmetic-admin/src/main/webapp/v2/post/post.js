$(document).ready(function(){
	$(".deletePost").click(function() {
        var data = "deleteId=" + $(this).get(0).id;
        $.post("listUserPost.action?delete", data, function(responseJson) {
            window.location.href = "./listUserPost.action";
         }).fail(function(e) {
             alert("Failed : " + e.status + " " + e.statusText)
         });
    });

    $(".editPost").click(function() {
        var data = "?postId=" + $(this).get(0).id;
        window.location.href = "./update-posts.action" + data;
    });

    $(".viewPost").click(function() {
        var data = "?postId=" + $(this).get(0).id;
        window.location.href = "./view-post.action" + data;
    });

	$(".add-paragraph_btn > a").click(function() {
        var index = $(".add_image_btn2").length;
		var newPhoto = $(".add_image_btn2:first").clone(true);
		newPhoto.attr("id", index);

		newPhoto.find("h2").text("Paragraph " + (index+1));
		newPhoto.find(".delete_paragraph").attr("hidden", false);

		var postImg = newPhoto.find(".postImg");
		postImg.attr("src", "");

		newPhoto.find(".postImgInput").replaceWith(newPhoto.find(".postImgInput").clone(true));
		var postImgInput = newPhoto.find(".postImgInput");

		var postImgContainer = newPhoto.find(".postImgContainer");

		postImgContainer.height("146px");
        postImgContainer.width("118px");

        postImgInput.height(postImgContainer.height());
        postImgInput.width(postImgContainer.width());
       	postImgInput.css({top: "-148px"});

       	postImg.height("100%");
        postImg.width("100%");
        
		newPhoto.find(".redirectUrl").val("");
		newPhoto.find(".redirectUrl").attr("iswidget", false);
		newPhoto.find(".fileId").val("");

		newPhoto.find(".addRedirectUrlBtn > a").html("Add URL");

        // Initialize content
        newPhoto.find("#cke_editor1").remove()
        var contentEditor = newPhoto.find(".add_url");
        contentEditor.html("");
        CKEDITOR.replace(contentEditor[0]);

        $("#photoSet").append(newPhoto);
    });

    var inputChange = function(input) {
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
            var postImg = $(input).parent().find(".postImg");
            postImg.attr("src", srcDataUrl);

            var fileId = $(input).parent().find(".fileId");
            fileId.html("");
          };
        } else {
          alert("Please choose an image file.");
        }
    };
    
    if (window.FileReader) {	  
	  $(".postImgInput").change(function() {
		  inputChange(this);
	  });

    }

    $(".postImg").load(function() {
    	var postImgContainer = $(this).parent();
        postImgContainer.height("146px");
        postImgContainer.width("118px");

        $(this).width("100%");
        $(this).height("auto");

        if ($(this).height() >  postImgContainer.height()) {
            $(this).height("100%");
            $(this).width("auto");
        }

        postImgContainer.height($(this).height());
        postImgContainer.width($(this).width());
        postImgContainer.find(".postImgInput").height(postImgContainer.height());
		postImgContainer.find(".postImgInput").width(postImgContainer.width());
        postImgContainer.find(".postImgInput").css({top: "-" + postImgContainer.height().toString() + "px"});
    });

    var allPostArray = [];
    var postStatus = null;
    var curPost;

	$("#postProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Posting..."
	});

	var genAttachmentMetadata = function (fileId, fileType, url, imgDesc, redirectUrl, width, height, size, md5, orientation) {
		var metadata = "\"{\\\"width\\\":" + width + ",";
		metadata += "\\\"height\\\":" + height + ",";
		metadata += "\\\"fileSize\\\":" + size + ",";
		metadata += "\\\"fileType\\\":\\\"" + fileType + "\\\",";
		metadata += "\\\"md5\\\":\\\"" + md5+ "\\\",";
		metadata += "\\\"orientation\\\": " + orientation + ",";
		if (imgDesc != null && imgDesc != "null" && imgDesc != "") {
			metadata += "\\\"imageDescription\\\":\\\"" + imgDesc + "\\\",";
		}
		if (redirectUrl != null && redirectUrl != "null" && redirectUrl != "") {
			metadata += "\\\"redirectUrl\\\":\\\"" + redirectUrl + "\\\",";
		}
		metadata += "\\\"originalUrl\\\":\\\"" + url + "\\\"}\"";

		var file = "{\"fileId\":" + fileId + ",";
		file += "\"metadata\":" + encodeURIComponent(metadata) + "}";

		var attachment = "{\"files\":" + "[" + file + "]}";
		return attachment;
	};

	var genPostMetadata = function (attachmentId, content, fileId, fileType, url, imgDesc, redirectUrl, width, height, size, md5, orientation, createPost) {
		var attachment = genAttachmentMetadata(fileId, fileType, url, imgDesc, redirectUrl, width, height, size, md5, orientation);

		var postMeta = "{";
		if (attachmentId == 0) {
			if (createPost != true) {
				postMeta += "\"postId\":\"" + $("#postId").html() + "\",";	
			}
			postMeta += "\"title\":\"" + encodeURIComponent(JSON.stringify($("#title").val()).slice(1, -1)) + "\",";
			postMeta += "\"circleIds\":[" + $("#circleSel option:selected").val() + "],";
			
			var scheduleCheck = $("#scheduleCheck");
			if(scheduleCheck.length > 0 && scheduleCheck[0].checked) {
				if(postStatus != null && postStatus == "Published")
					postStatus = "Hidden"
				var createdTime = moment($("#scheduleDateTime").val()).format("x");
				postMeta += "\"createdTime\":\"" + createdTime + "\",";
			}
			if (postStatus != null) {
				postMeta += "\"postStatus\":\"" + postStatus + "\",";
			}
			postMeta += "\"tags\":{";
			content = decodeURIComponent(content);
			var hashTags = twttr.txt.extractHashtags(content);
			postMeta += "\"userDefTags\":[";
			if(hashTags.length > 0){
				for(var i = 0; i < hashTags.length; i++){
					postMeta += "\"" + hashTags[i] + "\",";
				}
				postMeta = postMeta.slice(0, - 1);
			}
			postMeta += "]},";
			content = encodeURIComponent(content);
		}
		postMeta += "\"content\":\"" + content + "\",";
		postMeta += "\"attachments\":" + attachment + "}";
		return postMeta;
	};

	var uploadAll = function (onComplete, createPost) {
		$("#postProgress").append("{0} file remaining<br>".format(uploadPhotoTask.length));
		fileUploadAll_v2("../../file/upload-dataurl.action", "../../file/update-redirect-url.action", function(uploadItem, jsonObj) {
			var postMeta = genPostMetadata(uploadItem["attachmentId"],
											uploadItem["content"],
											jsonObj.fileId, 
											jsonObj.fileType, 
											"{0}//{1}/api/file/download-file.action?fileId={2}".format(window.location.protocol, window.location.host, jsonObj.fileId),
											jsonObj.metadata.imageDescription,
											jsonObj.metadata.redirectUrl, 
											jsonObj.metadata.width, 
											jsonObj.metadata.height, 
											jsonObj.metadata.fileSize, 
											jsonObj.metadata.md5, 
											jsonObj.metadata.orientation,
											createPost);				

			allPostArray.push(postMeta);
			var remainItem = uploadPhotoTask.length;
			if(remainItem > 0)
				$("#postProgress").append("{0} file remaining<br>".format(remainItem));
		}, onComplete);
	};

	var checkFieldComplete = function() {
		var title = $("#title").val();
		if(title.length <= 0)
			return "Please input the post title";
		return null;
	};
	
    var getResponse = function(createPost){
    	var checkFieldErr = checkFieldComplete();
    	if(checkFieldErr != null) {
    		alert(checkFieldErr);
    		return;
    	}
    	allPostArray.splice(0,allPostArray.length);
		uploadPhotoTask.splice(0,uploadPhotoTask.length);
    	var scheduleCheck = $("#scheduleCheck");
    	if(scheduleCheck.length > 0 && scheduleCheck[0].checked) {
			var createdTime = $("#scheduleDateTime").datepicker("getDate").getTime();
			var currentTime = (new Date()).getTime();
			var diff = createdTime - currentTime;
			if(diff < 1800000) {// 30 minutes
				alert("Create time for schedule post should at least 1 hour from now !");
				return;
			}
    	}
			
		$(".add_image_btn2").each(function(index, element) {
			var postImg = $(element).find(".postImg");
			var oriWidth = postImg[0].naturalWidth;
			var oriHeight = postImg[0].naturalHeight;
			var dataSrc = postImg.attr("src");
			var redirectUrl = $(element).find(".redirectUrl").val();
			var isWidget = $(element).find(".redirectUrl").attr("isWidget");
			var content = $(element).find(".cke_contents iframe").contents().find("body").html().replace(/&nbsp\;/g, " ");
			content = content.replace(/\"/g,"\\\"");
			content = encodeURIComponent(content);
			var fileId = $(element).find(".fileId").html();

			addUploadPhotoTask_v2(index, content, fileId, 'Photo', oriWidth, oriHeight, null, redirectUrl, dataSrc, isWidget);
		});

		$("#postProgress").dialog("open");
		uploadAll(function(succeeded) {
			if(!succeeded) {
				alert("Failed");
				allPostArray.splice(0,allPostArray.length);
				uploadPhotoTask.splice(0,uploadPhotoTask.length);
				$("#postProgress").append("Failed<br>");
				return;
			}

			if (createPost == true) {
				var data = "";
				for (var i = 0; i < allPostArray.length; i++) {
					if (i == 0) {
						data += "mainPost=" + allPostArray[i];
					}
					else {
						data += "&subPosts=" + allPostArray[i];
					}
				};

				var postUrl = "create-posts.action?create";

				$.post(postUrl, data, function(responseJson) {
					window.location.href = "./listUserPost.action";
		        }).fail(function(e) {
		        	 allPostArray.splice(0,allPostArray.length);
		        	 uploadPhotoTask.splice(0,uploadPhotoTask.length);
		        	 alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		        });
			}
			else {
				var data = "postId=" + $("#postId").html();
				for (var i = 0; i < allPostArray.length; i++) {
					if (i == 0) {
						data += "&mainPost=" + allPostArray[i];
					}
					else {
						data += "&newSubPosts=" + allPostArray[i];
					}
				};
	
				$.post("update-posts.action?update", data, function(responseJson) {
					window.location.href = "./listUserPost.action";
		        }).fail(function(e) {
		        	 alert("Failed : " + e.status + " " + e.statusText)
		        });
			}

		}, createPost);	
	}

	$("#publishBtn > a").click(function(event) {
		postStatus = "Published";
		getResponse(true);

	});

	$("#draftBtn > a").click(function(event) {
		postStatus = "Drafted";
		getResponse(true);

	});

	$("#updateBtn > a").click(function(event) {
		getResponse(false);

	});

	$("#cancelBtn > a").click(function(event) {
		window.location.href = "./listUserPost.action";

	});

	$("#deleteBtn > a").click(function(event) {
		var data = "deleteId=" + $("#postId").html();
        $.post("listUserPost.action?delete", data, function(responseJson) {
            window.location.href = "./listUserPost.action";
         }).fail(function(e) {
             alert("Failed : " + e.status + " " + e.statusText)
         });
	});

	$("#editBtn > a").click(function(event) {
		window.location.href = "./update-posts.action?postId=" + $("#postId").html();

	});

	$("#publishBtn2 > a").click(function(event) {
		var pStatus = "Published";
		var postCreatedTimeLbl = $("#postCreatedTime");
		if(postCreatedTimeLbl.length > 0) {
			var postCreatedTime = new Date(postCreatedTimeLbl.text());
			var currentTime = new Date();
			if(postCreatedTime > currentTime)
				pStatus = "Hidden";
		}
			
		window.location.href = "./view-post.action?updateStatus&postId=" + $("#postId").html() + "&postStatus=" + pStatus;

	});

	$("#draftBtn2 > a").click(function(event) {
		window.location.href = "./view-post.action?updateStatus&postId=" + $("#postId").html() + "&postStatus=Drafted";
	});

	$(".delete_paragraph > a").click(function(event) {
		$(this).closest(".add_image_btn2").remove();

	});

	$("#previewBtn").click(function(event) {
		$("#postPreviewTitle").html($("#title").val());

		var subPost = $(".subPost:first").clone(true);
		$(".preview_ctn").empty();
		$(".add_image_btn2").each(function(index, element) {
			var dataSrc = $(element).find(".postImg").attr("src");
			var redirectUrl = $(element).find(".redirectUrl").val();
			var content = $(element).find(".cke_contents iframe").contents().find("body").html().replace(/&nbsp\;/g, " ");
			
			subPost.children("img").attr("src", dataSrc);
			subPost.find(".previewContent").html(content);
			subPost.find(".previewRedirectUrl").attr("href", redirectUrl);
			subPost.find(".previewRedirectUrl").html(redirectUrl);
	        $(".preview_ctn").append(subPost);

	        subPost = $(".subPost:first").clone(true);
		});

		$("#postPreviewView").toggle(true);
		$("#createPostView").toggle(false);
        $("#backBtn").toggle(true);
        $("#previewBtn").toggle(false);
        $("#cancelBtn").toggle(false);
        $("#draftBtn").toggle(false);
        $(".page-header").html("Preview	post");
	});

	$("#backBtn").click(function(event) {

		$("#postPreviewView").toggle(false);
		$("#createPostView").toggle(true);
        $("#backBtn").toggle(false);
        $("#previewBtn").toggle(true);
        $("#cancelBtn").toggle(true);
        $("#draftBtn").toggle(true);
        $(".page-header").html("Create	post");
	});

	$(".addRedirectUrlBtn").click(function() {
		curPost = $(this).parent().parent().parent();
		$("#inputUrl").val(curPost.find(".redirectUrl").val());
		$("#widgetCheck").prop("checked", (curPost.find(".redirectUrl").attr("isWidget") === "true"));
		$("#redirectDialog").dialog("option", "title", $(this).children("a").html());
		$("#redirectDialog").dialog("open");
	});

	$("#redirectDialog").dialog({
		autoOpen: false,
        modal: true,
        width:600,
        buttons: {
        Ok: function() {
        	var url = $(this).find("#inputUrl").val();
        	curPost.find(".redirectUrl").val(url).trigger('change');
        	if (url == "")
        		curPost.find(".redirectUrl").attr("isWidget", false).trigger('change');
        	else
        		curPost.find(".redirectUrl").attr("isWidget", $("#widgetCheck").prop("checked")).trigger('change');

        	if(url.indexOf("https://www.youtube.com/") == 0 || url.indexOf("http://v.youku.com/") == 0) {
        		$.get("./get-video-meta.action?route&extUrl=" + encodeURIComponent(url), function( data ) {
					$.get("./get-video-meta.action?getDataUrl&extUrl=" + encodeURIComponent(data["images"][0]), function(result) {
						curPost.find(".postImg").attr("src", result);
						curPost.find(".postImgInput").val("");
						curPost.find(".fileId").html("");
						curPost.find(".fileSize").html("");
						curPost.find(".md5").html("");
						curPost.find(".orientation").html("");
					});
						
				}).fail(function() {

			    });
        	}

        	$(this).dialog( "close" );
        },
        Cancel: function() {
        	$(this).dialog( "close" );
        }
      }
	});
	
	$(".redirectUrl").change(function() {
		if ($(this).val() != '') {
			$(this).parent().find(".addRedirectUrlBtn > a").html("Edit URL");
		}
		else {
			$(this).parent().find(".addRedirectUrlBtn > a").html("Add URL");
		}
	});
    
	var zone = "+0000";
	var schedulePostInput = document.getElementById("scheduleDateTime"); 
	if(schedulePostInput != null) {
		var region = schedulePostInput.getAttribute("zone");
		if (region.toLowerCase() == "en_US".toLowerCase()) 
			zone = "-0700";
		else if (region.toLowerCase() == "de_DE".toLowerCase())
			zone = "+0200";
		else if (region.toLowerCase() == "fr_FR".toLowerCase())
			zone = "+0200";
		else if (region.toLowerCase() == "zh_TW".toLowerCase())
			zone = "+0800";
		else if (region.toLowerCase() == "zh_CN".toLowerCase())
			zone = "+0800";
		else if (region.toLowerCase() == "ja_JP".toLowerCase())
			zone = "+0900";
		else if (region.toLowerCase() == "ko_KR".toLowerCase())
			zone = "+0900";
	}
	
	var formatDate = function (timeIn, format, zone) {
		return moment(timeIn).format(format) + " " + zone;
	};
	
	var toTimeZone = function (timeIn, zoneIn, zoneOut, printTimeZone) {
	    var format = 'YYYY-MM-DD HH:mm';
	    if(printTimeZone)
	    	format += ' ZZ';
	    if(timeIn === undefined)
	    	timeIn = new Date();
	    var momentDate;
	    if(zoneIn == null)
	    	momentDate = moment.tz(timeIn, zoneOut);
	    else
	    	momentDate = moment.tz(timeIn, zoneIn).tz(zoneOut); 
	    momentDate.seconds(0).milliseconds(0)
	    return momentDate.format(format);
	};
	
	var showScheduleTime = function(show) {
		var scheduleDateTime = $('#scheduleDateTime');
    	if(show) {
        	scheduleDateTime.datetimepicker("destroy");
        	scheduleDateTime.show();
        	var todayDate = new Date(toTimeZone(todayDate, null, zone, false)); 
        	todayDate.setHours(todayDate.getHours() + 1);
            todayDate.setSeconds(0);
        	var postCreatedDate = scheduleDateTime.val();
        	if(postCreatedDate.length <= 0) {
        		postCreatedDate = formatDate(todayDate, "YYYY-MM-DD HH:mm", zone);
        	}
        	else {
        		postCreatedDate = toTimeZone(postCreatedDate, "+0000", zone, true);
        	}
        	scheduleDateTime.val(postCreatedDate);      
            scheduleDateTime.datetimepicker({
        		minDateTime: todayDate,
        		showSecond: false,
        		dateFormat: 'yy-mm-dd',
        		timeFormat: 'HH:mm z',
        		timezone: zone,
        		hour: postCreatedDate.substr(11,2),
        	    minute: postCreatedDate.substr(14,2),
        	    defaultDate: postCreatedDate.substr(0,10)
            });
        }
        else {
        	scheduleDateTime.hide();
        }
	}
	
	var scheduleCheck = $("#scheduleCheck");
	if(scheduleCheck.length > 0) {
		if(scheduleCheck[0].checked)
			showScheduleTime(true);
	}
	
    $("#scheduleCheck").change(function() {
    	showScheduleTime(this.checked);
    });                
});

