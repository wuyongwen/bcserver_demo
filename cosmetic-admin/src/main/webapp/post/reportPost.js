$(document).ready(function(){
	var toHandleReportedPost = {};
	var toHandleReportedComment = {};
	var postIds = [];
	var commentIds = [];
	var hasError = false;
	
	var requery = function() {
		var selectedTargetType = $("#selTargetType option:selected").val();
		var linkUrl = "./ReportedPost.action?selStatus=" + $("#reportStatus option:selected").val() + "&selRegion=" + $("#reportLocale option:selected").val() + "&searchAuthorId=" + $("#searchAuthorIdInput").val() + "&searchReportedId=" + $("#searchReportedIdInput").val();
		linkUrl += "&targetType=" + selectedTargetType;
		window.location.href = linkUrl;
	};
	
	$("#reportLocale").change(function() {
		requery();
	});
	
	$("#reportStatus").change(function() {
		requery();
	});
	
	var clickActButton = function($this) {
		var targetId = $this.val();
		if($this.attr("targetType") == "Post") {
			var toHPost = toHandleReportedPost[targetId];
			if(toHPost === undefined) {
				postIds.push(targetId);
				var toHPost = {};
				toHPost["targetId"] = targetId;
			}
			var act = $this.attr("act");
			if(act == "checkUndecided")
				delete toHandleReportedPost[targetId];
			else {
				toHPost["Result"] = act;
				toHandleReportedPost[targetId] = toHPost;
			}
		}
		else if($this.attr("targetType") == "Comment") {
			var toHComment = toHandleReportedComment[targetId];
			if(toHComment === undefined) {
				commentIds.push(targetId);
				var toHComment = {};
				toHComment["targetId"] = targetId;
			}
			var act = $this.attr("act");
			if(act == "checkUndecided")
				delete toHandleReportedComment[targetId];
			else {
				toHComment["Result"] = act;
				toHandleReportedComment[targetId] = toHComment;
			}
		}
	};
	
	$("table#row input[type='radio']").each(function(){
		var $this = $(this);
		$this.change(function() {
			clickActButton($this);
		});
	});
	
	var handleAllReportedComment = function(onComplete){
		if(commentIds.length <= 0){
			onComplete();
			return;
		}
			
		var curCommentId = commentIds.pop();
		var toHandleObj = toHandleReportedComment[curCommentId];
		if(toHandleObj === undefined) {
			handleAllReportedPost(onComplete);
			return;
		}
		$("#handleProgress").append("*Handling comment : " + curCommentId + " ... ");
		var data = "targetType=Comment&targetId="+ curCommentId + "&result=" + toHandleObj["Result"];
		$.post("ReportedPost.action?handleReported", data, function(responseJson) {
			$("#handleProgress").append("Done <br>");
			handleAllReportedComment(onComplete);
         }).fail(function(e) {
        	 $("#handleProgress").append("Failed : " + e.status + " " + e.statusText+ "<br>");
        	 handleAllReportedComment(onComplete);
        	 hasError = true;
         });
	};
	
	var handleAllReportedPost = function(onComplete) {
		if(postIds.length <= 0){
			onComplete();
			return;
		}
			
		var curPostId = postIds.pop();
		var toHandleObj = toHandleReportedPost[curPostId];
		if(toHandleObj === undefined) {
			handleAllReportedPost(onComplete);
			return;
		}
		$("#handleProgress").append("*Handling post : " + curPostId + " ... ");
		var data = "targetType=Post&targetId="+ curPostId + "&result=" + toHandleObj["Result"];
		$.post("ReportedPost.action?handleReported", data, function(responseJson) {
			if(responseJson.indexOf("failed to report contest post") != -1) {
				$("#handleProgress").append(responseJson+" <br>");
				hasError = true;
			}
			else
				$("#handleProgress").append("Done <br>");
			
			
			
			handleAllReportedPost(onComplete);
         }).fail(function(e) {
        	 $("#handleProgress").append("Failed : " + e.status + " " + e.statusText+ "<br>");
        	 handleAllReportedPost(onComplete);
        	 hasError = true;
         });
	};
	
	$("#handleProgress").dialog({
		autoOpen: false,
		maxWidth:600,
        maxHeight: 650,
        width: 600,
        height: 650,
        modal: true,
        title: "Share Link..."
	});
	
	$("#handleReported").click(function(){
		var done = function() {
			if(!hasError){
				alert("Complete");
				requery();
			}
			$("table#row input[type='radio']").prop('disabled', true);
		};
		
		var handleC = function() {
			handleAllReportedComment(done);
		};
		$("#handleProgress").dialog("open");
		handleAllReportedPost(handleC);
	});
	
	$("#authorDialog").dialog({
		autoOpen: false,
        maxHeight: 300,
        width: 600,
        height: 300,
        modal: true,
        draggable: false,
        title: "Author property"
	});
	
	
	$(".authorName").click(function() {
		var $this = $(this);
		var $dialog = $("#authorDialog");
		$dialog.find("#authorAvatar").attr("src", $this.attr("avatar"));
		$dialog.find("#authorName").text($this.text());
		$dialog.find("#authorEmail").text($this.attr("email"));
		$dialog.find("#authorId").text($this.attr("uid"));
		$dialog.find("#authorAccountSource").text($this.attr("accSource"));
		$dialog.find("#allReportedForOneAuthor").attr("href", "./ReportedPost.action?selStatus=" + $("#reportStatus option:selected").val() + "&selRegion=" + $("#reportLocale option:selected").val() + "&searchAuthorId=" + $this.attr("uid"));
		$dialog.find("#allReportingForOneAuthor").attr("href", "./ReportedPost.action?selStatus=" + $("#reportStatus option:selected").val() + "&selRegion=" + $("#reportLocale option:selected").val() + "&searchReportedId=" + $this.attr("uid"));
		$dialog.find("#allRelatedPostCommentForOneAuthor").attr("href", "./ReportedPost.action?getRelatedPostComment&selStatus=" + $("#reportStatus option:selected").val() + "&selRegion=" + $("#reportLocale option:selected").val() + "&searchAuthorId=" + $this.attr("uid"));
		$dialog.dialog("open");
	});
	
	$("#searchByAuthorBtn").click(function(e) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		requery();
	});
	
	$("#searchByReportedBtn").click(function(e) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		requery();
	});
	
	$("#undecidedAllBtn").click(function() {
		$("table#row input[type='radio'][act='checkUndecided']").each(function(){
			var $this = $(this);
			clickActButton($this);
			$this.prop('checked', true);
		});
	});
	
	$("#publishAllBtn").click(function() {
		$("table#row input[type='radio'][act='Published']").each(function(){
			var $this = $(this);
			clickActButton($this);
			$this.prop('checked', true);
		});
	});
	
	$("#bannedAllBtn").click(function() {
		$("table#row input[type='radio'][act='Banned']").each(function(){
			var $this = $(this);
			clickActButton($this);
			$this.prop('checked', true);
		});
	});
	
	$("table#row img[name='attachmentImg']").each(function(){
		var $this = $(this);
		$this.dblclick(function() {
			window.open('./queryPost.action?clpost&postId=' + $this.attr("pId"));
		});
	});
	
	$("#selTargetType").change(function() {
		requery();
	});
});

