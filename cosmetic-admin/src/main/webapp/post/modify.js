var needVerifyMasterId = false;
var needUploadMasterUrl = false;

$(document).ready(function(){
	
	var contentEditor = $(".lPostContentDiv");
	var defaultPromoteScore = 9999;
	CKEDITOR.replace(contentEditor[0], contentEditor.height);
	
	var current_fs, next_fs, previous_fs;
	var left, opacity, scale;
	var animating;
	var exCircleTag = [];
	var selectedKeyword = [];
	
	$("label[name=exCircleTag]").each(function(key, value){
		exCircleTag.push(parseInt($(value).text()));
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
	
	var genMainPostSpecParams = function(docElem) {
		var data = "";
		
		var title = docElem.find("textarea[name=title]").val();
		if(title !== undefined && title.length > 0)
			data += "&title=" + encodeURIComponent(title);
		
		var promoteScore = docElem.find("#promoteScoreInput:checked");
		if(promoteScore.length > 0)
			data += "&promoteScore=" + $("#promoteOrder").val();
		
		var postType = docElem.find("#postType");
		if(postType.length > 0) 
			data += "&postType=" + encodeURIComponent(postType.val());
		
		var lookTypes = docElem.find("#lookTypesSelection");
		if(lookTypes.length > 0) {
			if(lookTypes.val() != null)
				data += "&lookTypeId=" + encodeURIComponent(lookTypes.val());
			else
				data += "&lookTypeId=-1";
		}
			
		var extLookUrlInput = docElem.find("#extLookUrlInput");
		if(extLookUrlInput.length > 0) {
			var extLookUrl = extLookUrlInput.val();
			if(extLookUrl.length > 0)
				data += "&extLookUrl=" + encodeURIComponent(extLookUrl);
			else
				data += "&extLookUrl=";
		}
		
		var extLookTagInput = docElem.find("#extLookTypeInput");
		if(extLookTagInput.length > 0)
			data += "&lookTag=" + encodeURIComponent(extLookTagInput.val());
		
		var extHoroscopeType = docElem.find("#extHoroscopeTypeInput");
		if(extHoroscopeType.length > 0) {
			data += "&horoscopeType=" + extHoroscopeType.val();
			
			var horoscopeMasterId = docElem.find("#masterId");
			if(horoscopeMasterId.length > 0) {
				var masterId = horoscopeMasterId.val();
				if(masterId.length >= 0)
					data += "&masterId=" + encodeURIComponent(masterId);
			}
			
			var horoscopeMasterDisplayName = docElem.find("#masterDisplayName");
			if(horoscopeMasterDisplayName.length > 0) {
				var masterDisplayName = horoscopeMasterDisplayName.val();
				if(masterDisplayName.length >= 0)
					data += "&masterDisplayName=" + encodeURIComponent(masterDisplayName);
			}
			
			var horoscopeMasterAvatarUrl = docElem.find("#masterAvatarUrl");
			if(horoscopeMasterAvatarUrl.length > 0) {
				var masterAvatarUrl = horoscopeMasterAvatarUrl.attr("src");
				if(masterAvatarUrl.length >= 0)
					data += "&masterAvatarUrl=" + encodeURIComponent(masterAvatarUrl);
			}
			
			var horoscopeMasterDescription = docElem.find("#masterDescription");
			if(horoscopeMasterDescription.length > 0) {
				var masterDescription = horoscopeMasterDescription.val();
				if(masterDescription.length >= 0)
					data += "&masterDescription=" + encodeURIComponent(masterDescription);
			}
			
			var horoscopeMasterExtLink = docElem.find("#masterExternalLink");
			if(horoscopeMasterExtLink.length > 0) {
				var masterExtLink = horoscopeMasterExtLink.val();
				if(masterExtLink.length >= 0)
					data += "&masterExtLink=" + encodeURIComponent(masterExtLink);
			}
		}
		
		var selCircles = docElem.find("#selCircles:checked");
		if(selCircles !== undefined && selCircles.length > 0) {
			$.each(selCircles, function( index, value ) {
				data += "&selCircles=" + encodeURIComponent($(value).val());
			});
		}
		
		if(selectedKeyword.length > 0) {
			$.each(selectedKeyword, function( index, value) {
				data += "&selKeywords=" + encodeURIComponent(value.word);
			});
		}
		else {
			data += "&selKeywords=undefined";
		}
		return data;
	};
	
	var genPostParams = function(element) {
		var data = "";	
		var attachments = element.find("div[name=attachments]");
		if(attachments.length > 0) {
			$.each(attachments, function( index, value ) {
				data += "&attachments=" + encodeURIComponent($(value).text());
			});
		}
		else {
			data += "&attachments=null";
		}

		var editor = element.find(".cke_contents iframe");
		var content = escapeHtml(editor.contents().find("body").html());
		if(content.length > 0) {
			var contentText = editor.contents().find("body").text().replace(/[\u200B-\u200D\uFEFF]/g, '');
			if(contentText.trim().length > 0) {
				data += "&content=" + encodeURIComponent(content);
			}
			else
				data += "&content=null";
		}
		else
			data += "&content=null";
			
		return data;
	};
	
	$("#postProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Posting..."
	});
	
	$("#promoteScoreInput").change(function() {
		if(this.checked)
			$("#promoteOrder").val(defaultPromoteScore).show();
		else
			$("#promoteOrder").hide();
	});
	
	$("#postModifyBtn").click(function() {
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
		
		var clickedTarget = $(this);
		
		$.extend(true, uploadPhotoTask, uploadPhotoTaskTemp);
		uploadAll(function(succeeded) {
			if(!succeeded) {
				alert("Failed to upload file");
				$("#postProgress").append("Failed to upload file<br>");
				return;
			}
			var postId = clickedTarget.attr("sId");
			var parentId = clickedTarget.attr("pId");
			var data = "postId=" + postId;
			var $document = $(document);
			data += genMainPostSpecParams($document);
			data += genPostParams($document);
			var isCLPost = window.location.search.indexOf("clpost") > 0 ? true : false;
			$.post("./ModifyPost.action?update", data, function(responseJson, status) {
		         if(responseJson!=null && responseJson.length > 0){
					if(responseJson == "You need to login"){
						$("#loginDialog").dialog("open");
						$("#postProgress").append("<br>---<br>");
						return;
					}
		        	 var redirectUrl = "./queryPost.action?"
	        		 if(isCLPost)
	        			 redirectUrl += "clpost&";
		        	 if(parentId === undefined)
		        		 window.location.href = redirectUrl + "postId=" + postId;
		        	 else
		        		 window.location.href = redirectUrl + "postId=" + parentId;
		         }
		    }).fail(function(e) {
		        alert("Failed to modify post");
		        $("#postProgress").append("Failed to upload file<br>");
		    });
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
	
	$(".next").click(function(){
		var gotMainPost = true;
		if($("#postContent textarea[name=title]").length <= 0)
			gotMainPost = false;
		
		var curFs = $(this).parent().parent();
		if(curFs.attr("id") == "postContent") {
			if(gotMainPost) {
				if($("#postContent textarea[name=title]").length <= 0) {
					alert("A post must contain a title");
					return;
				}
				if($("#circleSelection input:checked").length <= 0) {
					alert("A post must contain a circle");
					return;
				}
				if(needVerifyMasterId){
					$("#varifyHoroscopeMaster").css("border", "3px solid #900");
					$("#invalidMasterId").text("* Please click to verify the master id");
					return;
				}
			}
			var attchPhoto = $("#postContent #attachmentDisplay .attchPhoto");
			if(attchPhoto.length <= 0 || attchPhoto.attr("src").length <= 0) {
				alert("A post must at least contain a photo!");
				return;
			} else{
				$("#varifyHoroscopeMaster").css("border", "");
				$("#invalidMasterId").text("");
			}
		}
		
		if(animating) return false;
		
		var curFs = $(this).parent().parent();		
		animating = true;
		
		current_fs = $(this).parent().parent();
		next_fs = $(this).parent().parent().next();
		
		if(next_fs.attr("id") == "final") {
			var previewDiv = next_fs.find("div#postPreviewDiv");
			previewDiv.empty();
			
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
				var cover = $(document).find("#postContent #attachmentDisplay .attchPhoto");
				lPostContent.find(".lCover").attr("src", cover.attr("src"));
				
				var postCont = $(".cke_contents iframe").contents().find("body");
				if(postCont.html().length > 0) {
					var mainContent = escapeHtml($(document).find(".cke_contents iframe").contents().find("body").html());
					lPostContent.find(".lPostContentDiv").append(mainContent);
				}

				lPostDiv.append(lPostHeader);
				lPostDiv.append(lPostContent);
				lPostDiv.append(lLikeComment);
				previewDiv.append("<p>Cover :</p>");
				previewDiv.append(lPostDiv);
				previewDiv.append(lPostDiv);
				previewDiv.append("<br><br><br><br>");
			}

			var gotSubpost = false;
			var qPostDiv = $("<div class='qPostDiv'>");
			var lPostHeader1 = $("#prePostHeaderTemplate").clone();
			lPostHeader1.attr("id", "prePostHeader1");
			if(gotMainPost)
				lPostHeader1.find("textarea[name=title]").append(title.val());
			lPostHeader1.show();
			qPostDiv.append(lPostHeader1);
			qPostDiv.append("<h1 class='divider'>&nbsp;</h1>");

			var postContent = $(document);
			var editor = postContent.find(".cke_contents iframe");
			var content = escapeHtml(editor.contents().find("body").html());
			if(content.length > 0) {
				var contentText = editor.contents().find("body").text().replace(/[\u200B-\u200D\uFEFF]/g, '');
				if(contentText.trim().length > 0) {
					gotSubpost = true;
				}
			}
			
			var lPostContentIdx = $("#prePostContentTemplate").clone();
			lPostContentIdx.attr("id", "prePostContent" + 1);
			lPostContentIdx.show();
			
			var attachments = postContent.find("div#attachmentDisplay");
			var attachImg = attachments.find("img"); 
			lPostContentIdx.find(".llCoverBody").attr("class", "llPhotoBody");
			var lCoverTemplate = lPostContentIdx.find(".lCover");
			if(attachImg.length > 1)
			{
				gotSubpost = true;
				for(aIdx = 0; aIdx < attachImg.length; aIdx++) {
					var img = $(attachImg[aIdx]);
					if(img.attr("id") == "attachmentButton")
						continue;
					lCoverTemplate.clone().attr("class", "lPhoto").attr("src", img.attr("src")).insertAfter(lCoverTemplate);
				}
			}
			else {
				lPostContentIdx.find(".llCoverBody").css("max-height", "0px");
				lPostContentIdx.find(".llCoverBody").css("padding", "");
			}
			
			if(gotSubpost) {
				lPostContentIdx.find(".lPostContentDiv").append(content);
				qPostDiv.append(lPostContentIdx);
				qPostDiv.append("<h1 class='divider'>&nbsp;</h1>");
				
				qPostDiv.append("<div class='lPostInteractDiv'><input class='lLikeAction' type='button' value='Like'><input class='lCommentAction' type='button' value='Comment'></div>");
				previewDiv.append("<p>Posts :</p>");
				previewDiv.append(qPostDiv);
			}
			
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
			smartTagDiv.init(responseJson, preSelectedKeyword);
		}).fail(function(e) {  	
		});
	}
	
});

