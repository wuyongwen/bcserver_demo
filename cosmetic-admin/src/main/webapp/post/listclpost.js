$(document).ready(function(){
	$("#clPostLocaleSel").change(function() {
		window.location.href = "./listUserPost.action?clpost&postType=" + $("#clPostUserTypeSel option:selected").val() + "&locale=" + $("#clPostLocaleSel option:selected").val() + "&postStatus=" + $("#postStatusSel option:selected").val();
	});
	
	$("#postStatusSel").change(function() {
		window.location.href = "./listUserPost.action?clpost&postType=" + $("#clPostUserTypeSel option:selected").val() + "&postStatus=" + $("#postStatusSel option:selected").val() + "&locale=" + $("#clPostLocaleSel option:selected").val();
	});
	
	$("#clPostUserTypeSel").change(function() {
		window.location.href = "./listUserPost.action?clpost&postType=" + $("#clPostUserTypeSel option:selected").val() + "&locale=" + $("#clPostLocaleSel option:selected").val() + "&postStatus=" + $("#postStatusSel option:selected").val();
	});
	
});