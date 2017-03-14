
var uploadMasterAvatar = function(src, width, height) {
	$("#postProgress").append("Uploading horoscope master avatar...<br>");
	var fd = new FormData();
	fd.append("dataUrl", src);
	fd.append("metadata", "{\"width\":{0},\"height\":{1}}".format(width, height));
	fd.append("fileType", "Photo");
	$.ajax({
		url: "../file/upload-dataurl.action",
		data: fd,
		cache: false,
		mimeType: "multipart/form-data",
		processData: false,
		contentType: false,
		type: 'POST',
		success: function(data){
			if(data != null && data.length > 0){
				var jsonString = jQuery.parseJSON(data);
				var jsonObj = jQuery.parseJSON(jsonString);
				if(jsonObj.fileId == null) {
					$("#postProgress").append("Failed to upload horoscope master avatar.<br>");
				}
				var originalUrl = jsonObj.originalUrl;
				$masterAvatarUrl.prop("src", originalUrl);
			}
		},
		async: false
	});
}

var resetHoroscopeMaster = function(){
	$horoscopeMasterArea.hide();
	$horoscopeMaster.hide();
	
	$masterId.val("");
	$masterAvatarUrl.prop('src', "");
	$masterDisplayName.val("");
	$masterDescription.val("");
	$masterExternalLink.val("");
}

$(document).ready(function(){

	$extHoroscopeTypeInput = $("#extLookDiv #extHoroscopeTypeInput");
	$extLookTypeInput = $("#extLookDiv #extLookTypeInput");
	
	$horoscopeMasterArea = $("#horoscopeMasterArea");
	$horoscopeMaster = $("#horoscopeMaster");
	
	$masterId = $("#masterId");
	$masterAvatarInput = $("#masterAvatarInput");
	$masterAvatarUrl = $("#masterAvatarUrl");
	$masterDisplayName = $("#masterDisplayName");
	$masterDescription = $("#masterDescription");
	$masterExternalLink = $("#masterExternalLink");
	
	$extLookTypeInput.change(function(){
		if(this.value != ""){
			$extHoroscopeTypeInput.val("");
			$extHoroscopeTypeInput.prop('disabled', true);
			resetHoroscopeMaster();
			$postType.val("HOWTO").trigger("change");
		}
		else
			$extHoroscopeTypeInput.prop('disabled', false);
	});
	
	$extHoroscopeTypeInput.change(function(){
		if(this.value != ""){
			$extLookTypeInput.val("");
			$horoscopeMasterArea.show();
			$horoscopeMaster.show();
			$postType.val("HOROSCOPE_LOOK").trigger("change");
		}
		else
			resetHoroscopeMaster();
	});
	
	$("#varifyHoroscopeMaster").click(function(event) {
		$(this).css("border", "");
		$("#invalidMasterId").text("");
		
		if($masterId.length > 0) {
			var masterId = $masterId.val();
			$.post( "./CreatePost.action?verifyHoroscopeMaster&masterId="+masterId, function( data ) {
				if(data!=null && data.length > 0){
					if(data == "Invalid horoscope masterId"){
						$("#invalidMasterId").text("* "+data);
						needVerifyMasterId = true;
					}
					else{
						var jsonObj = jQuery.parseJSON(data);
						var displayName = jsonObj.displayName;
						var avatarUrl = jsonObj.avatarUrl;
						$masterAvatarUrl.prop('src', avatarUrl);
						$masterDisplayName.val(displayName);
						needVerifyMasterId = false;
					}
				}
			});
		}
	});
	
	$masterId.change(function(){
		if(this.value == "")
			needVerifyMasterId = false;
		else
			needVerifyMasterId = true;
	});
	
	if (window.FileReader) {
		$masterAvatarInput.change(function() {

			var fileReader = new FileReader();
			var files = this.files;
			if (!files.length) {
				return;
			}
	
			var file = files[0];
			if(file.size > 10000000) {
				alert("File size should not exceed 10MB.");
				return;
			}
			if (/^image\/\w+$/.test(file.type)) {
				fileReader.readAsDataURL(file);
				fileReader.onload = function () {
					$masterAvatarUrl.prop("src", this.result);
					needUploadMasterUrl = true;
				};
			} else {
				alert("Please choose an image file.");
			}
		});
	} else {
		$masterAvatarInput.addClass("hide");
	}

});