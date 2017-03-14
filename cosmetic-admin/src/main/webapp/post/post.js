var needVerifyMasterId = false;
var needUploadMasterUrl = false;

$(document).ready(function(){
	
	var current_fs, next_fs, previous_fs;
	var left, opacity, scale;
	var animating;
	var defaultPromoteScore = 9999;
	var selectedKeyword = [];
	
	var resetPostJs = function() {
		uploadPhotoTask = [];
		postContentList = [];
		attachmentCount = 0;
		$("#attachmentMetadatas div").remove();
		$("#attachmentDisplay img").remove();
	};

	var addContentEditor = function(table, scrollToNewRow) {	
		var tr = $("<tr>");
		var newPostContentDiv = genPostEditor(postContentList.length);
		postContentList.push(newPostContentDiv.attr("id"));
		tr.append(newPostContentDiv);
		table.append(tr);
		var container = $("#dashboard");
		if(scrollToNewRow)
			container.animate({ scrollTop: container[0].scrollHeight}, 100);
	}

	var genPostEditor = function (idx) {
		var newPostEditor = $("#postEditorTemplate").clone(true);
		newPostEditor.attr("id", "postEditor"+idx);
		var contentEditor = newPostEditor.find("#postContentInput");
		contentEditor.attr("id", "postContentInput"+idx);
		CKEDITOR.replace(contentEditor[0], contentEditor.height);
		newPostEditor.show();
		return newPostEditor;
	};

	var toEscape = {
        	"&quot;" : "\"", "&amp;": "&", "&#39;": "'",
        	"&#47;": "/",  "&lt;": "<",  "&gt;": ">",
        	"&nbsp;": " "
        };
	var escapeHtml = function (text) {
		return text.replace(/&(?:#x[a-f0-9]+|#[0-9]+|[a-z0-9]+);?/ig, function (a) {
			var r = toEscape[a];
			if(r === undefined)
				return a;
	        return r;
	    });
	}
	
	$("#addSubPost").click(function() {
		var contentTable = $("#postContent table#postContentTable");
		addContentEditor(contentTable, true);
	});
	
	var createTenSubPostEditor = function (contentTable) {
		while(contentTable.find("tr").length < 10) {
			addContentEditor(contentTable, false);
		}	
	}
	
	$("#postProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Posting..."
	});
	
	$("input[name=selCircleTypes]").change(function(){
		var data = "";
		$("input[name=selCircleTypes]").each(function () {
		       if(this.checked) 
		    	   data += "selCircleTypes=" + $(this).val() + "&";
		  });
        $.post("listUserPost.action?getCircles", data, function(responseJson) {
         if(responseJson!=null && responseJson.length > 0){
        	 $("#circleNameDiv").hide();
        	 $("#circleNameDiv").empty();
             $.each(responseJson, function(key,value) { 
            	 var circleCheckBox = $("<input type=\"checkbox\" id=\"selCircles\" name=\"selCircles\" />");
            	 circleCheckBox.val(value.circleName);
            	 $("#circleNameDiv").append(circleCheckBox);
            	 $("#circleNameDiv").append(" " + value.circleName + " ");
            	 var breakLine = $('<br />');
       		  	 $("#circleNameDiv").append(breakLine);
             });
             $("#circleNameDiv").show();
             }
         }).fail(function(e) {
        	 alert("Failed");
         });
	});      
	
	var createMainPost = function(data, onComplete) {
		var apiUrl = "./CreatePost.action?create";
		$("#postProgress").append("Creating MainPost...<br>");
		$.post(apiUrl, data, function(responseJson) {
			if(responseJson!=null && responseJson.length > 0){
				if(responseJson == "You need to login"){
					onComplete(false);
					$("#loginDialog").dialog("open");
					$("#postProgress").append("<br>---<br>");
					return;
				}
				var jsonObj = jQuery.parseJSON(responseJson);
				$("#relPostId").attr("value", jsonObj.postId);
				onComplete(true);
			 }         		
		}).fail(function(e) {
			alert("Failed to create main post : " + e.responseText);
			onComplete(false);
		});
	};
	
	var createAllSubPost = function(postId, onComplete) {
		$("#postProgress").dialog("open");
		
		if(postContentList.length <= 0) {
			onComplete();
			return;
		}
		
		var subData = null;
		while(subData == null && postContentList.length > 0) {
			var subPostContent = $(document).find("#" + postContentList.shift());
			subData = genPostParams(subPostContent);
		}
		
		if(subData == null) {
			onComplete();
			return;
		}
		
		$("#postProgress").append("Creating SubPost...<br>");
		subData += genSubPostSpecParams(postId, subPostContent);
		var apiUrl = "./CreatePost.action?createSubPost";
		$.post(apiUrl, subData, function(responseJson) {
			if(responseJson!=null && responseJson.length > 0){
				if(responseJson == "You need to login"){
					$("#loginDialog").dialog("open");
					$("#postProgress").append("<br>---<br>");
					return;
				}
				else if(responseJson == "You are not the owner of this MainPost"){
					alert(responseJson);
					$("#postProgress").append("<br>---<br>");
					onComplete();
					return;
				}
				var jsonObj = jQuery.parseJSON(responseJson);
				createAllSubPost(postId, onComplete);
			 }
		}).fail(function(e) {
			alert("Failed to create sub post : " + e.responseText);
			onComplete();
			return;
		});
	};
	
	var genMainPostSpecParams = function(docElem) {
		var data = "";
		
		var title = docElem.find("textarea[name=title]");
		data += "title=" + encodeURIComponent(title.val()) + "&";
		
		var promoteScore = docElem.find("#promoteScoreInput:checked");
		if(promoteScore.length > 0)
			data += "promoteScore=" + $("#promoteOrder").val() + "&";
		
		var selCircles = docElem.find("#selCircles:checked");
		$.each(selCircles, function( index, value ) {
			data += "selCircles=" + encodeURIComponent($(value).val()) + "&";
		});

		$.each(selectedKeyword, function( index, value) {
			data += "selKeywords=" + encodeURIComponent(value.word) + "&";
		});
		
		var editorElm = docElem.find("#postEditor0");
		var content = escapeHtml(editorElm.find(".cke_contents iframe").contents().find("body").html());
			
		data += "content=" + encodeURIComponent(content) + "&";
		
		var emoji = editorElm.find("select[name=emoji] option:selected:not(:disabled)");
		if(emoji.length > 0) {
			data += "emoji=" + encodeURIComponent(emoji.val()) + "&";
		}
		
		var postType = editorElm.find("#postType");
		if(postType.length > 0) 
			data += "postType=" + encodeURIComponent(postType.val()) + "&";
		
		var lookTypes = editorElm.find("#lookTypesSelection");
		if(lookTypes.length > 0) {
			if(lookTypes.val() != null)
				data += "lookTypeId=" + encodeURIComponent(lookTypes.val()) + "&";
		}
		
		var extLookUrlInput = editorElm.find("#extLookUrlInput");
		if(extLookUrlInput.length > 0) {
			var extLookUrl = extLookUrlInput.val();
			if(extLookUrl.length > 0)
				data += "extLookUrl=" + encodeURIComponent(extLookUrl) + "&";
		}
		
		var extLookType = editorElm.find("#extLookTypeInput");
		if(extLookType.length > 0) {
			var extLookTypeVal = extLookType.val();
			if(extLookTypeVal.length >= 0)
				data += "lookTag=" + encodeURIComponent(extLookTypeVal) + "&";
		}
		
		var extHoroscopeType = editorElm.find("#extHoroscopeTypeInput");
		if(extHoroscopeType.length > 0) {
			var extHoroscopeTypeVal = extHoroscopeType.val();
			if(extHoroscopeTypeVal.length >= 0)
				data += "horoscopeType=" + encodeURIComponent(extHoroscopeTypeVal) + "&";
			
			var horoscopeMasterId = editorElm.find("#masterId");
			if(horoscopeMasterId.length > 0) {
				var masterId = horoscopeMasterId.val();
				if(masterId.length >= 0)
					data += "masterId=" + encodeURIComponent(masterId) + "&";
			}
			
			var horoscopeMasterDisplayName = editorElm.find("#masterDisplayName");
			if(horoscopeMasterDisplayName.length > 0) {
				var masterDisplayName = horoscopeMasterDisplayName.val();
				if(masterDisplayName.length >= 0)
					data += "masterDisplayName=" + encodeURIComponent(masterDisplayName) + "&";
			}
			
			var horoscopeMasterAvatarUrl = editorElm.find("#masterAvatarUrl");
			if(horoscopeMasterAvatarUrl.length > 0) {
				var masterAvatarUrl = horoscopeMasterAvatarUrl.attr("src");
				if(masterAvatarUrl.length >= 0)
					data += "masterAvatarUrl=" + encodeURIComponent(masterAvatarUrl) + "&";
			}
			
			var horoscopeMasterDescription = editorElm.find("#masterDescription");
			if(horoscopeMasterDescription.length > 0) {
				var masterDescription = horoscopeMasterDescription.val();
				if(masterDescription.length >= 0)
					data += "masterDescription=" + encodeURIComponent(masterDescription) + "&";
			}
			
			var horoscopeMasterExtLink = editorElm.find("#masterExternalLink");
			if(horoscopeMasterExtLink.length > 0) {
				var masterExtLink = horoscopeMasterExtLink.val();
				if(masterExtLink.length >= 0)
					data += "masterExtLink=" + encodeURIComponent(masterExtLink) + "&";
			}
		}
		
		var attachments = editorElm.find("div[name=attachments]");
		if(attachments.length > 0) {
			$.each(attachments, function( index, value ) {
				data += "attachments=" + encodeURIComponent($(value).text()) + "&";
			});
		}

		return data;
	};
	
	var genSubPostSpecParams = function(postId, element) {
		var data = "postId=" + encodeURIComponent(postId) + "&";
		return data;
	};
	
	var genPostParams = function(element) {
		var data = "";
		
		var attachments = element.find("div[name=attachments]");
		if(attachments.length <= 0)
			return null;
		else {
			$.each(attachments, function( index, value ) {
				data += "attachments=" + encodeURIComponent($(value).text()) + "&";
			});
		}
		
		var content = escapeHtml(element.find(".cke_contents iframe").contents().find("body").html());
		data += "content=" + encodeURIComponent(content) + "&";
		
		var emoji = element.find("select[name=emoji] option:selected:not(:disabled)");
		if(emoji.length > 0) {
			data += "emoji=" + encodeURIComponent(emoji.val()) + "&";
		}
		
		var extLookType = element.find("#extLookTypeInput");
		if(extLookType.length > 0) {
			var extLookTypeVal = extLookType.val();
			if(extLookTypeVal.length >= 0)
				data += "lookTag=" + encodeURIComponent(extLookTypeVal) + "&";
		}
		
		var extHoroscopeType = element.find("#extHoroscopeTypeInput");
		if(extHoroscopeType.length > 0) {
			var extHoroscopeTypeVal = extHoroscopeType.val();
			if(extHoroscopeTypeVal.length >= 0)
				data += "horoscopeType=" + encodeURIComponent(extHoroscopeTypeVal) + "&";
		}

		var extLookUrlInput = element.find("#extLookUrlInput");
		if(extLookUrlInput.length > 0) {
			var extLookUrl = extLookUrlInput.val();
			if(extLookUrl.length > 0)
				data += "extLookUrl=" + encodeURIComponent(extLookUrl) + "&";
		}
		
		return data;
	};
	
	$("#createPostButton").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		
		$("#postProgress").dialog("open");
		if(needUploadMasterUrl) {
			var image = new Image();
		    image.src = $("#masterAvatarUrl").attr("src");
			var src = image.src;
		    var width = image.width;
		    var height = image.height;
			uploadMasterAvatar(src, width, height);
		}
		
		$.extend(true, uploadPhotoTask, uploadPhotoTaskTemp);
		uploadAll(function(succeeded) {
			if(!succeeded) {
				alert("Failed to upload file");
				$("#postProgress").append("Failed to upload file<br>");
				return;
			}
			var mainPostContent = $(document).find("#" + postContentList.shift());
			var data = genMainPostSpecParams($(document));
			createMainPost(data, function(succeed) {
				if(!succeed) {
					alert("Failed to create MainPost");
					$("#postProgress").append("Failed to create MainPost<br>");
					return;
				}
				setTimeout(
				  function() 
				  {
					  var postId = $("#relPostId").attr("value")
					  createAllSubPost(postId, function() {
							$("#postProgress").append("Complete!<br>");
							window.location.href = "./queryPost.action?postId=" + postId;
						});
				  }, 1000);
			});
		});
	});
	
	$("#createSubPostButton").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		$.extend(true, uploadPhotoTask, uploadPhotoTaskTemp);
		uploadAll(function(succeeded) {
			if(!succeeded) {
				alert("Failed to upload file");
				$("#postProgress").append("Failed to upload file<br>");
				return;
			}
			var postId = $("#relPostId").attr("value");
			  createAllSubPost(postId, function() {
				  $("#postProgress").append("Complete!<br>");
					window.location.href = "./queryPost.action?postId=" + postId;
				});
		});
	});
	
	$("#promoteScoreInput").change(function() {
		if(this.checked)
			$("#promoteOrder").val(defaultPromoteScore).show();
		else
			$("#promoteOrder").hide();
	});
	
	$(".lPostDiv a").click(function(event) {
		hyperlinkCtrl($(this).attr("href"), event);
	});
	
	$(".qPostDiv a").click(function(event) {
		hyperlinkCtrl($(this).attr("href"), event);
	});

	if($("li.active").text() == "Post Content") {
		var contentTable = $("table#postContentTable");
		contentTable.hide();
		if(contentTable.length > 0) {
			contentTable = $(contentTable[0]);
			createTenSubPostEditor(contentTable);
		}
		
		contentTable.show();
	}
	else if($("li.active").text() == "Post Description") {
		addContentEditor($("#postDescription table#postContentTable"), true);
	}
	
	$(".next").click(function(){
	
		if(animating) return false;
		
		var curFs = $(this).parent().parent();
		if(curFs.attr("id") == "circleSelection") {
			if($("#circleSelection input:checked").length <= 0) {
				alert("A post must contain a circle");
				return;
			}
		}
		else if(curFs.attr("id") == "postDescription") {
			if($("#postDescription textarea[name=title]").val().length <= 0) {
				alert("A post must contain a title");
				return;
			}
			if($("#circleSelection input:checked").length <= 0) {
				alert("A post must contain a circle");
				return;
			}
			var attachPhoto = $("#attachmentDisplay .attchPhoto");
			if(attachPhoto == null || attachPhoto.length <= 0) {
				alert("A post must have at least one photo");
				return;
			}
			if(CKEDITOR.instances[$("#postEditor0").find("[id*=postContentInput]").attr("id")].mode == "source") {
				alert("Please uncheck [Source] to view final content");
				return;
			}
			if(needVerifyMasterId){
				$("#varifyHoroscopeMaster").css("border", "3px solid #900");
				$("#invalidMasterId").text("* Please click to verify the master id");
				return;
			} else{
				$("#varifyHoroscopeMaster").css("border", "");
				$("#invalidMasterId").text("");
			}
		}
		else if(curFs.attr("id") == "postContent") {
			for(idx = 0; idx < postContentList.length; idx++) {
				var postContent = $(document).find("#" + postContentList[idx]);
				var attachments = postContent.find("div#attachmentDisplay");
				var attachImg = attachments.find("img"); 
				if(attachImg.length > 1) {
					if(CKEDITOR.instances[postContent.find("[id*=postContentInput]").attr("id")].mode == "source") {
						alert("Please uncheck [Source] to view final content");
						return;
					}
				}				
			}
		}
		animating = true;
		
		current_fs = $(this).parent().parent();
		next_fs = $(this).parent().parent().next();
		
		if(next_fs.attr("id") == "postContent") {
			var contentTable = next_fs.find(".postContent table#postContentTable");
			if(contentTable.length > 0) {
				contentTable = $(contentTable[0]);
				createTenSubPostEditor(contentTable);
			}
			$("#postContent #horoscopeMasterArea").hide();
			$("#postContent #postTypeDiv").hide();
		}
		else if(next_fs.attr("id") == "final") {
			var previewDiv = next_fs.find("div#postPreviewDiv");
			previewDiv.empty();
			
			var gotMainPost = false;
			if($("#postDescription").length > 0)
				gotMainPost = true;
			
			if(gotMainPost) {
				var lPostDiv = $("<div class='lPostDiv'>");
				var lPostHeader = $("#prePostHeaderTemplate").clone();
				lPostHeader.attr("id", "prePostHeader0");
				lPostHeader.show();
				var lPostContent = $("#prePostContentTemplate").clone();
				lPostContent.attr("id", "prePostContent0");
				lPostContent.show();
				var lLikeComment = $("<div class='lLikeCountDiv'>0 people like this</div><div class='lPostInteractDiv'><input class='lLikeAction' type='button' value='Like'><input class='lCommentAction' type='button' value='Comment'></div>");
				
				var title = $(document).find("textarea[name=title]");
				lPostHeader.find(".lPostTitleDiv").append(title.val());
				
				var cover = $(document).find("#attachmentDisplay .attchPhoto");
				lPostContent.find(".lCover").attr("src", cover.attr("src"));
				
				var mainContent = escapeHtml($(document).find("#" + postContentList[0]).find(".cke_contents iframe").contents().find("body").html());
				lPostContent.find(".lPostContentDiv").append(mainContent);
				
				lPostDiv.append(lPostHeader);
				lPostDiv.append(lPostContent);
				lPostDiv.append(lLikeComment);
				previewDiv.append("<p>Cover :</p>");
				previewDiv.append(lPostDiv);
				previewDiv.append("<br><br><br><br>");
			}
			
			
			var qPostDiv = $("<div class='qPostDiv'>");
			var lPostHeader1 = $("#prePostHeaderTemplate").clone();
			lPostHeader1.attr("id", "prePostHeader1");
			if(gotMainPost)
				lPostHeader1.find(".lPostTitleDiv").append(title.val());
			lPostHeader1.show();
			qPostDiv.append(lPostHeader1);
			qPostDiv.append("<h1 class='divider'>&nbsp;</h1>");
			for(idx = 0; idx < postContentList.length; idx++) {
				var postContent = $(document).find("#" + postContentList[idx]);
				if(CKEDITOR.instances[postContent.find("[id*=postContentInput]").attr("id")].mode == "source") {
					alert("Please uncheck [Source] to view final content");
					return;
				}
				
				var attachments = postContent.find("div#attachmentDisplay");
				var attachImg = attachments.find("img"); 
				if(attachImg.length <= 1)
					continue;
				
				var lPostContentIdx = $("#prePostContentTemplate").clone();
				lPostContentIdx.attr("id", "prePostContent" + 1);
				lPostContentIdx.show();
				lPostContentIdx.find(".llCoverBody").attr("class", "llPhotoBody");
				var lCoverTemplate = lPostContentIdx.find(".lCover");
				if(attachImg.length > 1)
				{
					var lastImgElm = lCoverTemplate;
					for(aIdx = 0; aIdx < attachImg.length; aIdx++) {
						var img = $(attachImg[aIdx]);
						if(img.attr("id") == "attachmentButton")
							continue;
						var newPreviewImg = lCoverTemplate.clone().attr("class", "lPhoto").attr("src", img.attr("src"));
						newPreviewImg.insertAfter(lastImgElm);
						lastImgElm = newPreviewImg;
					}
				}
				else {
					lPostContentIdx.find(".llCoverBody").css("max-height", "0px");
					lPostContentIdx.find(".llCoverBody").css("padding", "");
				}
				var content = escapeHtml(postContent.find(".cke_contents iframe").contents().find("body").html());
				lPostContentIdx.find(".lPostContentDiv").append(content);
				qPostDiv.append(lPostContentIdx);
				qPostDiv.append("<h1 class='divider'>&nbsp;</h1>");
			}
			
			qPostDiv.append("<div class='lPostInteractDiv'><input class='lLikeAction' type='button' value='Like'><input class='lCommentAction' type='button' value='Comment'></div>");
			previewDiv.append("<p>Posts :</p>");
			previewDiv.append(qPostDiv);
			
			$("div#postPreviewDiv a").click(function(event) {
				if(event.preventDefault) 
					event.preventDefault();
				else
					event.returnValue = false;
				openInNewTab($(this).attr("href"));
			});
		}		
		
		$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
		
		next_fs.show(); 
		current_fs.animate({opacity: 0}, {
			step: function(now, mx) {
				opacity = 1 - now;
				next_fs.css({'left': left, 'opacity': opacity});
			}, 
			duration: 800, 
			complete: function(){
				current_fs.hide();
				animating = false;
			}, 
			easing: 'easeInOutBack'
		});
	});

	$(".previous").click(function(){
		if(animating) return false;
		animating = true;
		
		current_fs = $(this).parent().parent();
		previous_fs = $(this).parent().parent().prev();
		
		$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
		
		//show the previous fieldset
		previous_fs.show(); 
		//hide the current fieldset with style
		current_fs.animate({opacity: 0}, {
			step: function(now, mx) {
				opacity = 1 - now;
				current_fs.css({'left': left});
				previous_fs.css({'opacity': opacity});
			}, 
			duration: 800, 
			complete: function(){
				current_fs.hide();
				animating = false;
			}, 
			easing: 'easeInOutBack'
		});
	});
	
	$(".checkboxes label").click(function() {
		var clickedLabel = $(this); 
		if(clickedLabel.find("input:checked").length > 0) {
			clickedLabel.attr("class","checkedLabel");
		}
			
		else {
			clickedLabel.attr("class","unCheckedLabel");
		}
	});

	$("#newSubPostBtn").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		window.location.href = "./CreatePost.action?postForm=subpost&mainPostId=" + $(this).attr("pId");
	});
	
	$("input[name=publishPostBtn]").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var r = confirm("Confirm to publish this post?");
		var isCLPost = window.location.search.indexOf("clpost") > 0 ? true : false;
	    if (r == true) {
	    	var data = "postId=" + $(this).attr("pId") + "&postStatus=Published";
	    	if($(this).attr("isProm") == "1")
	    		data += "&promoteScore=" + defaultPromoteScore;
	    	$.post("./ModifyPost.action?update", data, function(responseJson) {
				if(responseJson!=null && responseJson.length > 0){
					if(isCLPost)
						window.location.href = "./listUserPost.action?clpost";	
					else
						window.location.href = "./listUserPost.action";
				 }         		
			}).fail(function(e) {
				alert("Failed to publish this post");
			});
	    }
	    	
	});
	
	$("input[name=submitPostBtn]").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var r = confirm("Confirm to submit this post for auditing?");
		var isCLPost = window.location.search.indexOf("clpost") > 0 ? true : false;
	    if (r == true) {
	    	var data = "postId=" + $(this).attr("pId") + "&postStatus=Auditing";
	    	$.post("./ModifyPost.action?update", data, function(responseJson) {
				if(responseJson!=null && responseJson.length > 0){
					if(isCLPost)
						window.location.href = "./listUserPost.action?clpost";	
					else
						window.location.href = "./listUserPost.action";
				 }         		
			}).fail(function(e) {
				alert("Failed to submit this post");
			});
	    }
	    	
	});
	
	$("#shareLinkDialog").dialog({
		autoOpen: false,
		maxWidth:700,
        maxHeight: 800,
        width: 700,
        height: 800,
        modal: true,
        title: "Share Link ..."
	});
	$("#shareLinkBtn").click(function() {
		$("#shareLinkDialog").dialog("open");
	});
	
	var createShareLinkPost = function(data, onComplete) {
		var apiUrl = "./CreatePost.action?create";
		$("#postProgress").append("Share Link<br>");
		$.post(apiUrl, data, function(responseJson) {
			if(responseJson!=null && responseJson.length > 0){
				var jsonObj = jQuery.parseJSON(responseJson);
				onComplete(true, jsonObj.postId);
			 }         		
		}).fail(function(e) {
			alert("Failed to share link : " + e.responseText);
			onComplete(false, null);
		});
	};
	
	var genShareLinkSpecParams = function(docElem) {
		var data = "";
		
		var title = docElem.find("textarea[name=title]");
		data += "title=" + encodeURIComponent(title.val()) + "&";
		
		var content = escapeHtml(docElem.find(".cke_contents iframe").contents().find("body").html());
		if(content.length > 0) {
			var contentText = docElem.find(".cke_contents iframe").contents().find("body").text().replace(/[\u200B-\u200D\uFEFF]/g, '');
			if(contentText.trim().length > 0)
				data += "content=" + encodeURIComponent(content) + "&";
		}
		
		var selCircles = docElem.find("#selCircles:checked");
		$.each(selCircles, function( index, value ) {
			data += "selCircles=" + encodeURIComponent($(value).val()) + "&";
		});
		
		$.each(selectedKeyword, function( index, value) {
			data += "selKeywords=" + encodeURIComponent(value.word) + "&";
		});
		
		var attachments = docElem.find("div[name=attachments]");
		if(attachments.length > 0) {
			$.each(attachments, function( index, value ) {
				data += "attachments=" + encodeURIComponent($(value).text()) + "&";
			});
		}
		return data;
	};
	
	$("#createShareLinkButton").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var docElem = $("#shareLinkDialog");
		var title = docElem.find("textarea[name=title]");
		if(title.val().length <= 0) {
			alert("A post must have a title");
			return null;
		}
		
		var url = docElem.find("#sharedLinkInput");
		if(url.val().length <= 0) {
			alert("A post must have a share link");
			return null;
		}
		
		var attachPhoto = docElem.find("#externalCovers#externalCovers img.selectedImage");
		if(attachPhoto == null || attachPhoto.length <= 0) {
			alert("A post must have at least one photo");
			return;
		}
		
		var circleSelection = docElem.find("#circleSelection input:checked");
		if(circleSelection.length <= 0) {
			alert("A post must contain a circle");
			return;
		}
		
		$("#postProgress").dialog("open");
		var imgBlock = docElem.find("#externalCovers .selectedImage");
		var url = imgBlock.attr("src");
		var genAttachmentData = function(dataUrl) {
			addUploadPhotoTask(docElem.find("#attachmentSelection"), 0, 'Photo', imgBlock.get(0).naturalWidth, imgBlock.get(0).naturalHeight, null, docElem.find("input[name=coverRedirectUrl]").val(), null, dataUrl);
			$.extend(true, uploadPhotoTask, uploadPhotoTaskTemp);
			uploadAll(function(succeeded) {
				if(!succeeded) {
					alert("Failed to upload file");
					$("#postProgress").append("Failed to upload file<br>");
					return;
				}
				
				var data = genShareLinkSpecParams(docElem);
				if(data == null) {
					$("#postProgress").dialog("close");
					return;
				}
				
				createShareLinkPost(data, function(suceeded, postId) {
				  $("#postProgress").append("Complete<br>");
					window.location.href = "./queryPost.action?postId=" + postId;
				});
			});
		};
		
		if(url.indexOf("http") == 0) {
			$.get("./CreatePost.action?getDataUrl&extUrl=" + encodeURIComponent(url), function(result){
				genAttachmentData(result);
			});
		}
		else if(url.indexOf("data") == 0) {
			genAttachmentData(result);
		}
	});
	
	$(".postRemove").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var sId = $(this).attr("sId");
		var ans = confirm("Are you sure you want to remove ["+sId+"] from trending?");
		if(ans == true) {
			var data = "postId=" + sId;
			$.post("queryPost.action?removeFromTrending", data, function(responseJson) {
				window.location.href = "./queryPost.action?postId=" + sId;
			}).fail(function(e) {
				alert("Failed to remove post from trending");
			});
		}
	});
	
	$(".postDelete").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var sId = $(this).attr("sId");
		var pId = $(this).attr("pId");
		var type = window.location.search.substr(1);
		type = type.substr(0, type.indexOf("&"));
		var data = "deleteId=" + sId;
		var thisParent = $(this).parent();
		$.post(location.pathname + "?delete", data, function(response) {
	         if(response!=null && response.length > 0 && response == "OK"){
	        	 if(pId === undefined) {
	        		 if(type == "clpost")
		        		 window.location.href = "./listUserPost.action?clpost";
		        	 else
		        		 window.location.href = "./listUserPost.action";
	        	 }
	        	 else {
	        		 thisParent.remove();
	        	 }
	         }
	         else
	        	 alert("Failed");
		}).fail(function(e) {
        	 alert("Failed");
         });
	});

	$(".postModify").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		var sId = $(this).attr("sId");
		var isCLPost = window.location.search.indexOf("clpost") > 0 ? true : false;
		if(isCLPost)
			window.location.href = "./ModifyPost.action?clpost&postId=" + sId;
		else
			window.location.href = "./ModifyPost.action?postId=" + sId;
	});
	
	var deleteComment = function($this, event) {
		var r = confirm("Confirm to delete this comment?");
		if(r == true) {
			var sId = $this.attr("sId");
			var cId = $this.attr("cId");
			var uId = $this.attr("uId");
			var data = "deleteCommentUsrId=" + uId + "&deleteCommentId=" + cId;
			$.post("queryPost.action?deleteComment", data, function(responseJson) {
				window.location.href = "./queryPost.action?postId=" + sId;
	         }).fail(function(e) {
	        	 alert("Failed");
	         });
		}
	};
	
	$(".commentDelete").click(function(event) {
		deleteComment($(this), event);
	});
	
	$(".lPostDiv").click(function(event) {
		if($(event.target).is('input'))
			return;
		else if($(event.target).is('a'))
			return;
		
		if($(this).attr("target") == "_blank")
			openInNewTab($(this).attr("href"));
		else
			window.location.href = $(this).attr("href");
	});
	
	$(window).scroll(function() {
        if ($(window).height() < $(window).scrollTop()) {
        	$(".goTop").show();
        }
        else {
        	$(".goTop").hide();
        }
    });
	
	$(".goTop").click(function() {
		$(document).scrollTop(0);
	});
	
	$("#importDoc").click(function() {
		$("#postProgress").dialog("open");
		$("#postProgress").append("Uploading document to create post<br>");
	});
	
	$(".listCommentAction").click(function() {
		var $this = $(this);
		var limit = 10;
		var offset = parseInt($this.attr("offset"));
		var tSize = parseInt($this.attr("tSize"));
		var data = "offset=" + offset;
		data += "&limit=" + limit;
		data += "&postId=" + $this.attr("pId");
		$.post("queryPost.action?listComment", data, function(responseJson) {
			 if(responseJson!=null){
				 var newCommentDiv = $($.parseHTML(responseJson)[1]);
				 var delCmtBtn = newCommentDiv.find(".commentDelete");
				 delCmtBtn.click(function(event) {
						deleteComment($(this), event);
					});
				 $($this.parent().parent().find("#postCommentsDiv")[0]).prepend(newCommentDiv);
				 offset += limit;
				 if(offset >= tSize) {
					 $this.hide();
				 }
				 else {
					 $this.attr("offset", offset);
					 $this.attr("value", "See " + (tSize - offset) + " more comments ...");
				 }
		     }
		 }).fail(function(e) {
			 alert("Failed");
		 });
	});
	
	$("input[name=selCircles]").each(function () {
		var $this = $(this);
		$this.change(function() {
			if(!$this.is(':checked')) {
				$this.prop('checked', false);
				return;
			}
				
			var $parent = $this.parent().parent();
			var selCircles = $("#selCircles:checked");
			$.each(selCircles, function( index, value ) {
				$(value).prop('checked', false);
			});
			$this.prop('checked', true);
		});
	});
	
	$("#sharedLinkInput").on('paste', function(event) {
		var $this = $(this);
		var parent = $this.parent().parent().parent();
		var url;
		if(window.clipboardData)
			url = window.clipboardData.getData('Text');
		else if(event.originalEvent.clipboardData)
			url = event.originalEvent.clipboardData.getData('Text');
		else {
			return;
		}
		var opts = {lines: 13, length: 20, width: 10, radius: 30, corners: 1, rotate: 0, direction: 1, color: '#000', speed: 1, trail: 60, shadow: false, hwaccel: false, className: 'spinner', zIndex: 2e9, top: '50%', left: '50%'};
		var spinner = new Spinner(opts).spin(parent[0]);
		$.get("./CreatePost.action?getMetaTagFromUrl&extUrl=" + encodeURIComponent(url), function( data ) {
			parent.find("textarea[name=title]").val(data["title"]);
			parent.find("input[name=coverRedirectUrl]").val(url);
			parent.find(".cke_contents iframe").contents().find("body").html(data["content"]);
			var imagesList =  data["images"];
			var externalCovers = $("#externalCovers");
			externalCovers.empty();
			for(var cIdx = 0; cIdx < imagesList.length; cIdx++) {
				var newCImg = $("<img style='max-height: 100px;padding-right: 5px;' src='" + imagesList[cIdx] + "'/>");
				newCImg.click(function(){
					$(this).parent().find(".selectedImage").removeClass("selectedImage");
					$(this).addClass("selectedImage");
				});
				newCImg.error(function(){
					var $thisNewCImg = $(this);
					$.get("./CreatePost.action?getDataUrl&extUrl=" + encodeURIComponent($thisNewCImg.attr("src")), function(result){
						$thisNewCImg.attr("src", result);
					});
				})
				
				externalCovers.append(newCImg);
			}
				
			spinner.stop();
		}).fail(function() {
			spinner.stop();
	    });
	});
	
	var postTagDivs = $(".postTagsDiv");
	if(postTagDivs.length > 0) {
		var apiUrl = "./CreatePost.action";
		var data = "loadKeyword";
		$.post(apiUrl, data, function(responseJson) {	
			var newKeywordInput = $("<input id='newKeyword' type='text' maxlength='20' placeholder='New Tag' style='width:60%;'>");
			var addKeywordBtn = $("<input type='button' style='background: #4780ae !important; color: #FFFFFF;width:40px;' value='Add'>");
			var smartTagDiv = postTagDivs.smartTag("en_US", null, "postTags", "addPostTagDiv", newKeywordInput, addKeywordBtn);
			smartTagDiv.onKeywordClick(function(clikedKeyword, keyword, isSelected) {
				if(isSelected) {
					selectedKeyword.push(keyword);
				}
				else {
					var idx = selectedKeyword.indexOf(keyword);
					if(idx != -1) {
						selectedKeyword.splice(idx, 1);
					}
				}
			});
			smartTagDiv.onAddingKeyword(function() {
				spinner.spin($("#displayDiv")[0]);
			});
			smartTagDiv.onAddedKeyword(function() {
				spinner.stop();
			});
			smartTagDiv.onAddKeywordError(function(error) {
				alert("Failed to add keyword");
				spinner.stop();
			});
			smartTagDiv.init(responseJson, []);
		}).fail(function(e) {  	
		});
	}

});

