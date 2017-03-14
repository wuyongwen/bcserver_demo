$(document).ready(function(){
	$("#postStatusSel").change(function() {
		var newUrl = "./listUserPost.action?postStatus=" + $("#postStatusSel option:selected").val();
		var creatorIdInput = $("#searchCreatorIdInput");
		if(creatorIdInput.length > 0)
			newUrl += "&searchCreatorId=" + creatorIdInput.val();
		window.location.href = newUrl
	});
	
	$("#searchByCreatorBtn").click(function() {
		window.location.href = "./listUserPost.action?postStatus=" + $("#postStatusSel option:selected").val() + "&searchCreatorId=" + $("#searchCreatorIdInput").val();
	});
});