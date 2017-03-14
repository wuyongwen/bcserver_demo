$(document).ready(function(){
	
	$("#progressDialog").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false
	});
	
	var createTag = function(defaultTagId) {
		$("#progressDialog").append("Creating post default tag ...<br>");
		var nameMap = {};
		nameMap[$('#locale').find(":selected").val()] = $("#tagName").val();
		var data = "tagNameMap=" + JSON.stringify(nameMap);
		if(defaultTagId != null)
			data += "&defaultTagId=" + defaultTagId;
		data += "&isDeleted=" + ($("#isDeleted").find(":selected").val() == "true" ? "false" : "true");
		$.post("post-default-tag-manager.action?create", data, function(responseJson) {
			window.location.href = "./post-default-tag-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	
	};
	
	$("#create").click(function() {
		if($("#tagName").val().length <= 0) {
			alert("A look type must have a name!");
			return;
		}
		
		$("#progressDialog").dialog("open");
		createTag($("#defaultTagId").val());
	});
	
	$(".deleteTagBtn").click(function(event) {
		if (confirm("Delete Post Default Tag !!!!") == false) {
			event.preventDefault();
		    return false;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Deleting post default tag ...<br>");
		var data = "defaultTagId=" + $(this).attr("ild");
		$.post("post-default-tag-manager.action?delete", data, function(responseJson) {
			window.location.href = "./post-default-tag-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".showTagBtn").click(function(event) {
		if (confirm("Show Post Default Tag !!!!") == false) {
			event.preventDefault();
		    return false;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Showing post default tag ...<br>");
		var data = "defaultTagId=" + $(this).attr("ild");
		$.post("post-default-tag-manager.action?show", data, function(responseJson) {
			window.location.href = "./post-default-tag-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$(".hideTagBtn").click(function(event) {
		if (confirm("Hide Post Default Tag !!!!") == false) {
			event.preventDefault();
		    return false;
		}
		
		$("#progressDialog").dialog("open");
		$("#progressDialog").append("Hiding post default tag ...<br>");
		var data = "defaultTagId=" + $(this).attr("ild");
		$.post("post-default-tag-manager.action?hide", data, function(responseJson) {
			window.location.href = "./post-default-tag-manager.action";
         }).fail(function(e) {
        	 alert("Failed : " + e.status + " " + e.statusText)
         });
	});
	
	$("#newTag").click(function(){
		window.location.href = "./post-default-tag-manager.action?newTag";
	});
	
});

