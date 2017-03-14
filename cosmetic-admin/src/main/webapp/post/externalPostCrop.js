var cropData = null;
var selectedId = null;

$(document).ready(function() {
	$("#imageSelection").dialog({
		autoOpen : false,
		maxWidth : 1400,
		maxHeight : 800,
		minWidth : 1400,
		minHeight : 800,
		modal : true,
		closeOnEscape: false,
		title : "Image Selection"
	});
	
	$("#croppingArea").dialog({
		autoOpen : false,
		maxWidth : 900,
		maxHeight : 800,
		minWidth : 900,
		minHeight : 800,
		modal : true,
		closeOnEscape: false,
		title : "Crop Image"
	});
	
	$("#croppingSelection").dialog({
		autoOpen : false,
		maxWidth : 0,
		maxHeight : 0,
		minWidth : 0,
		minHeight : 0,
		modal : true,
		title : "Auto Detect"
	});
	
	$("#originalDiv").dialog({
		autoOpen : false,
		maxWidth : 900,
		maxHeight : 800,
		minWidth : 900,
		minHeight : 800,
		modal : true,
		closeOnEscape: false,
		title : "Original Image"
	});
	
	// image croppper
	var $imageCropper = $("#croppingArea img");
	var $imageSelector = $("#croppingSelection img");
	var selectorOptions = {
		zoomable : false,
		data: {x: 0, 
			y: 0, 
			width: 100, 
			height: 100
		}
	};
	var cropperOptions = {
		zoomable : false
	};
	$imageCropper.cropper(cropperOptions).on({
		"build.cropper" : function(e) {
			console.log("$imageCropper " + e.type);
		},
		"built.cropper" : function(e) {
			console.log("$imageCropper " + e.type);
		}
	});
	$imageSelector.cropper(selectorOptions).on({
		"build.cropper" : function(e) {
			console.log("$imageSelector " + e.type);
		},
		"built.cropper" : function(e) {
			console.log("$imageSelector " + e.type);
			
			if (cropData == null) {
				$("#imageSelection").find("#autoDetectionImg1").attr("src", $imageSelector.attr("src"));
				$("#imageSelection").find("#autoDetectionImg2").attr("src", $imageSelector.attr("src"));
				$("#imageSelection").find("#autoDetectionImg3").attr("src", $imageSelector.attr("src"));
				$("#imageSelection").find("#autoDetectionImg1").attr("croppedZone", "");
				$("#imageSelection").find("#autoDetectionImg2").attr("croppedZone", "");
				$("#imageSelection").find("#autoDetectionImg3").attr("croppedZone", "");
			} else {
				// Auto crop image
				cropData["height"] = $imageSelector.cropper("getImageData")["naturalHeight"];
				var naturalWidth = $imageSelector.cropper("getImageData")["naturalWidth"];
				var width = cropData["width"];
				var gain = (naturalWidth - width) / 3 ;
				
				$imageSelector.cropper("setData", cropData);
				var srcDataUrl1 =  $imageSelector.cropper("getDataURL");
				var croppedZone1 = JSON.stringify($imageSelector.cropper("getData")); 
				console.log(croppedZone1);
				$("#imageSelection").find("#autoDetectionImg1").attr("src", srcDataUrl1);
				$("#imageSelection").find("#autoDetectionImg1").attr("croppedZone", croppedZone1);
				
				cropData["x"] -= gain/2;
				cropData["width"] += gain;
				$imageSelector.cropper("setData", cropData);
				var srcDataUrl2 =  $imageSelector.cropper("getDataURL");
				var croppedZone2 = JSON.stringify($imageSelector.cropper("getData")); 
				console.log(croppedZone2);
				$("#imageSelection").find("#autoDetectionImg2").attr("src", srcDataUrl2);
				$("#imageSelection").find("#autoDetectionImg2").attr("croppedZone", croppedZone2);
				
				cropData["x"] -= gain/2;
				cropData["width"] += gain;
				$imageSelector.cropper("setData", cropData);
				var srcDataUrl3 =  $imageSelector.cropper("getDataURL");
				var croppedZone3 = JSON.stringify($imageSelector.cropper("getData")); 
				console.log(croppedZone3);
				$("#imageSelection").find("#autoDetectionImg3").attr("src", srcDataUrl3);
				$("#imageSelection").find("#autoDetectionImg3").attr("croppedZone", croppedZone3);
				
				cropData = null;
			}
		}
	});
	$imageSelector.cropper("replace", "./../common/theme/backend/images/ico_defualtImg.png");
	
	// spinner
	var opts = {
		lines : 13,
		length : 7,
		width : 7,
		radius : 30,
		corners : 1,
		rotate : 0,
		direction : 1,
		color : '#000',
		speed : 1,
		trail : 60,
		shadow : false,
		hwaccel : false,
		className : 'spinner',
		zIndex : 2e9,
		top : '50%',
		left : '50%'
	};
	var spinner = new Spinner(opts);
	var $targetImg = null;
	var $iframe = null;
	
	$("#articleTable").load(function() {
		$iframe = $(this);
		var $row = $iframe.contents().find("#row").first();
		$row.contents().find(".imgDiv").each(function() {
			$(this).click(function() {
				$(".selectedTd").css( "border-width", 0 );
				selectedId = null;
				$targetImg = $(this).find("img").first();
				
				var img = null;
				if (imgMap.has($targetImg.attr("data-original")))
					img = imgMap.get($targetImg.attr("data-original"));
				
				if (img != null) {
					$("#imageSelection").dialog("open");
					croppingSelectionStatus(false);
					// use extUrl to get face
					/*$.get("./photo-selection.action?getFace&extUrl=" + encodeURIComponent($targetImg.attr("data-original")), function(result){
						console.log(result);
						try {
							var jsonObj = result;
							var faceArray = JSON.parse(jsonObj["Face Rect"]);
							var left = null;
							var right = null;
							for (var i=0 ; i<faceArray.length ; i++) {
								if (i == 0) {
									left = faceArray[i]["left"];
									right = faceArray[i]["right"];
								} else {
									left = Math.min(left, faceArray[i]["left"]);
									right = Math.max(right, faceArray[i]["right"]);
								}
							}
							if (left == null || right == null)
								cropData = null;
							else
								cropData = {
									x: left,
									y: 0,
									width: right - left						
								};
							console.log("getFace: " + JSON.stringify(cropData));
						} catch(e) {
							cropData = null;
							console.log("[getFace error] " + e);
						}
						$imageSelector.cropper("replace", img);
						croppingSelectionStatus(true);
				    }).fail(function(e) {  	
				    	cropData = null;
				    	console.log("[getFace error] " + e.status + " (" + e.statusText + ")");
				    	$imageSelector.cropper("replace", img);
				    	croppingSelectionStatus(true);
					});*/
					
					// use dataUrl to get face
					var requestUrl = "./photo-selection.action?getFace";
					var srcDataUrl = img;
					var data = new FormData();
					data.append("dataUrl", srcDataUrl);
					jQuery.ajax({
						url : requestUrl,
						data : data,
						cache : false,
						mimeType : "multipart/form-data",
						contentType : false,
						processData : false,
						type : 'POST',
						success : function(result) {
							console.log(result);
							try {
								var jsonObj = JSON.parse(result);
								var faceArray = JSON.parse(jsonObj["Face Rect"]);
								var left = null;
								var right = null;
								for (var i=0 ; i<faceArray.length ; i++) {
									if (i == 0) {
										left = faceArray[i]["left"];
										right = faceArray[i]["right"];
									} else {
										left = Math.min(left, faceArray[i]["left"]);
										right = Math.max(right, faceArray[i]["right"]);
									}
								}
								if (left == null || right == null)
									cropData = null;
								else
									cropData = {
										x: left,
										y: 0,
										width: right - left						
									};
								console.log("getFace: " + JSON.stringify(cropData));
							} catch(e) {
								cropData = null;
								console.log("[getFace error] " + e);
							}
							$imageSelector.cropper("replace", img);
							croppingSelectionStatus(true);
						},
						error : function (jqXHR, textStatus, errorThrown) {
							cropData = null;
					    	console.log("[getFace error] " + e.status + " (" + e.statusText + ")");
					    	$imageSelector.cropper("replace", img);
					    	croppingSelectionStatus(true);
						}
					});
				} else {
					alert("image source lost.");
					$targetImg = null;
					$("#imageSelection").dialog("close");
				}
				
			});
		});
		
		$row.contents().find(".original-button").each(function() {
			var $this = $(this);
			$this.click(function() {
				var $tr = $this.parents("tr");
				$targetImg = $tr.find("img").first();
				
				var img = null;			
				if (imgMap.has($targetImg.attr("data-original")))
					img = imgMap.get($targetImg.attr("data-original"));
				if (img == null) {
					alert("image source lost.");
					return;
				}
				$("#originalDiv").dialog("open");
				$("#originalDiv img").attr("src", img);
			});
		});
		
		$row.contents().find(".edit-button").each(function() {
			var $this = $(this);
			$this.prop("disabled", false);
			$this.click(function() {
				$row.contents().find(".edit-button").prop("disabled", false);
				$row.contents().find(".cancel-button").prop("disabled", true);
				$row.contents().find(".save-button").prop("disabled", true);
				$this.prop("disabled", true);
				$this.parents(".editBar").find(".cancel-button").first().prop("disabled", false);
				$this.parents(".editBar").find(".save-button").first().prop("disabled", false);
				
				$row.contents().find(".titleEdit").hide();
				$row.contents().find(".titleLink").show();
				$row.contents().find(".contentEdit").hide();
				$row.contents().find(".contentDisplay").show();
				$this.parents("tr").find(".titleEdit").first().show();
				$this.parents("tr").find(".titleLink").first().hide();
				$this.parents("tr").find(".contentEdit").first().show();
				$this.parents("tr").find(".contentDisplay").first().hide();
				
				var text = $this.parents("tr").find(".titleLink a").text();
				$this.parents("tr").find(".titleEdit textarea").val(text);
				
				var description = $this.parents("tr").find(".contentDisplay textarea").val();
				$this.parents("tr").find(".contentEdit textarea").val(description);
			});
		});
		
		$row.contents().find(".cancel-button").each(function() {
			var $this = $(this);
			$this.prop("disabled", true);
			$this.click(function() {
				$this.prop("disabled", true);
				$this.parents(".editBar").find(".edit-button").first().prop("disabled", false);
				$this.parents(".editBar").find(".save-button").first().prop("disabled", true);
				$this.parents("tr").find(".titleEdit").first().hide();
				$this.parents("tr").find(".titleLink").first().show();
				$this.parents("tr").find(".contentEdit").first().hide();
				$this.parents("tr").find(".contentDisplay").first().show();
			});
		});
		
		$row.contents().find(".save-button").each(function() {
			var $this = $(this);
			$this.prop("disabled", true);
			$this.click(function() {
				var requestUrl = "./externalPost.action?editTitle";
				var text = $this.parents("tr").find(".titleEdit textarea").val();
				var description = $this.parents("tr").find(".contentEdit textarea").val();
				var editIndex = $this.attr("data-index");
				if (text.length <= 0) {
					alert("Please enter title");
					return;
				}
				$row.contents().find(".edit-button").prop("disabled", true);
				$row.contents().find(".cancel-button").prop("disabled", true);
				$row.contents().find(".save-button").prop("disabled", true);
				imageHttpRequestPause(true);
				
				var data = new FormData();
				data.append("title", text);
				data.append("content", description);
				data.append("croppedIdx", editIndex);
				jQuery.ajax({
					url : requestUrl,
					data : data,
					cache : false,
					mimeType : "multipart/form-data",
					contentType : false,
					processData : false,
					type : 'POST',
					success : function(result) {
						$this.parents("tr").find(".titleLink a").text(text);
						$this.parents("tr").find(".contentDisplay textarea").val(description);						
						
						$row.contents().find(".edit-button").prop("disabled", false);
						$this.parents("tr").find(".titleEdit").first().hide();
						$this.parents("tr").find(".titleLink").first().show();
						$this.parents("tr").find(".contentEdit").first().hide();
						$this.parents("tr").find(".contentDisplay").first().show();
						imageHttpRequestPause(false);
					},
					error : function (jqXHR, textStatus, errorThrown) {
						$row.contents().find(".edit-button").prop("disabled", false);
						$this.parents(".editBar").find(".edit-button").first().prop("disabled", true);
						$this.parents(".editBar").find(".cancel-button").first().prop("disabled", false);
						$this.prop("disabled", false);
						imageHttpRequestPause(false);
						
						alert("Failed to edit : " + errorThrown);
						return;
					}
				});
			});
		});
		
		$row.contents().find(".titleEdit").hide();
		$row.contents().find(".contentEdit").hide();
	});
	
	$("#doCroppedImage").click(function() {
		var img = null;
		if (imgMap.has($targetImg.attr("data-original")))
			img = imgMap.get($targetImg.attr("data-original"));
		if (img == null) {
			alert("image source lost.");
			return;
		}
			
		$imageCropper.cropper("reset", true).cropper("replace", img);
		$("#croppingArea").dialog("open");
	});
	
	$(".selectedTd").each(function() {
		var $this = $(this);
		$this.click(function() {
			$(".selectedTd").css( "border-width", 0 );
			$this.css( "border-width", 5 );
			selectedId = $this.find("img").first().attr("id");
		});
	});

	// save cropped img
	$("#confirmCroppedImage").click(function() {
		var requestUrl = "./externalPost.action?saveImage";
		var srcDataUrl = $imageCropper.cropper("getDataURL");
		var croppedZone = JSON.stringify($imageCropper.cropper("getData"));
		var idx = $targetImg.attr("data-index");
		var data = new FormData();
		data.append("croppedImg", srcDataUrl);
		data.append("croppedIdx", idx);
		data.append("croppedZone", croppedZone);
		//console.log("croppedZone: " + croppedZone);

		if (srcDataUrl) {
			// UI status
			croppingAreaStatus(false);

			jQuery.ajax({
				url : requestUrl,
				data : data,
				cache : false,
				mimeType : "multipart/form-data",
				contentType : false,
				processData : false,
				type : 'POST',
				success : function(result) {
					$targetImg.attr("src", srcDataUrl);
					$targetImg = null;
					$("#croppingArea").dialog("close");
					$("#imageSelection").dialog("close");
						
					croppingAreaStatus(true);
				},
				error : function (jqXHR, textStatus, errorThrown) {
					alert("Failed to save image : " + errorThrown);
					croppingAreaStatus(true);
					return;
				}
			});
		} 
		else {
			alert("Failed to save image : image source lost.");
			$targetImg = null;
			$("#croppingArea").dialog("close");
			$("#imageSelection").dialog("close");
		}

	});
	
	// save auto img
	$("#confirmSelectedImage").click(function() {
		if (selectedId == null) {
			alert("Please select one image.");
			return;
		}
		var requestUrl = "./externalPost.action?saveImage";
		var srcDataUrl = $("#" + selectedId).attr("src");
		var croppedZone = $("#" + selectedId).attr("croppedZone");
		var idx = $targetImg.attr("data-index");
		var data = new FormData();
		data.append("croppedImg", srcDataUrl);
		data.append("croppedIdx", idx);
		data.append("croppedZone", croppedZone);
		//console.log("croppedZone: " + croppedZone);
		
		if (croppedZone == null || croppedZone.length <= 0) {
			$("#imageSelection").dialog("close");
			return;
		}
		
		if (srcDataUrl) {
			// UI status
			croppingSelectionStatus(false);

			jQuery.ajax({
				url : requestUrl,
				data : data,
				cache : false,
				mimeType : "multipart/form-data",
				contentType : false,
				processData : false,
				type : 'POST',
				success : function(result) {
					$targetImg.attr("src", srcDataUrl);
					$targetImg = null;
					$("#imageSelection").dialog("close");
						
					croppingSelectionStatus(true);
				},
				error : function (jqXHR, textStatus, errorThrown) {
					alert("Failed to save image : " + errorThrown);
					croppingSelectionStatus(true);
					return;
				}
			});
		} 
		else {
			alert("Failed to save image : image source lost.");
			$targetImg = null;
			$("#imageSelection").dialog("close");
		}
	});
	
	// save ori img
	$("#confirmOriginalImage").click(function() {
		var requestUrl = "./externalPost.action?saveImage";
		var srcDataUrl = "";
		var croppedZone = "";
		var idx = $targetImg.attr("data-index");
		var data = new FormData();
		data.append("croppedImg", srcDataUrl);
		data.append("croppedIdx", idx);
		data.append("croppedZone", croppedZone);
		
		var img = null;			
		if (imgMap.has($targetImg.attr("data-original")))
			img = imgMap.get($targetImg.attr("data-original"));
		
		if (img != null) {
			originalDivStatus(false);
			jQuery.ajax({
				url : requestUrl,
				data : data,
				cache : false,
				mimeType : "multipart/form-data",
				contentType : false,
				processData : false,
				type : 'POST',
				success : function(result) {
					$targetImg.attr("src", img);
					$targetImg = null;
					$("#originalDiv").dialog("close");
					
					originalDivStatus(true);
				},
				error : function (jqXHR, textStatus, errorThrown) {
					alert("Failed to save image : " + errorThrown);
					originalDivStatus(true);
					return;
				}
			});
		}
		else {
			alert("Failed to save image : image source lost.");
			$targetImg = null;
			$("#originalDiv").dialog("close");
		}
	});
	
	var croppingSelectionStatus = function(bEnable) {
		if (bEnable) {
			$(".ui-dialog-titlebar-close").show();
			$("#doCroppedImage").prop("disabled", false);
			$("#confirmSelectedImage").prop("disabled", false);
			spinner.stop();
			imageHttpRequestPause(false);
		} else {
			$(".ui-dialog-titlebar-close").hide();
			$("#doCroppedImage").prop("disabled", true);
			$("#confirmSelectedImage").prop("disabled", true);
			spinner.spin($("#imageSelection")[0]);
			imageHttpRequestPause(true);
		}
	};
	
	var croppingAreaStatus = function(bEnable) {
		if (bEnable) {
			$(".ui-dialog-titlebar-close").show();
			$("#confirmCroppedImage").prop("disabled", false);
			spinner.stop();
			imageHttpRequestPause(false);
		} else {
			$(".ui-dialog-titlebar-close").hide();
			$("#confirmCroppedImage").prop("disabled", true);
			spinner.spin($("#croppingArea")[0]);
			imageHttpRequestPause(true);
		}
	};
	
	var originalDivStatus = function(bEnable) {
		if (bEnable) {
			$(".ui-dialog-titlebar-close").show();
			$("#confirmOriginalImage").prop("disabled", false);
			spinner.stop();
			imageHttpRequestPause(false);
		} else {
			$(".ui-dialog-titlebar-close").hide();
			$("#confirmOriginalImage").prop("disabled", true);
			spinner.spin($("#originalDiv")[0]);
			imageHttpRequestPause(true);
		}
	};
	
	var imageHttpRequestPause = function(bEnable) {
		if (bEnable) {
			if (imgRequest != null && imgRequest.length > 0) {
				for (var i=0 ; i < imgRequest.length ; i++) {
					imgRequest[i].abort();
				}
				imgRequest = [];
			}
		} else {
			$iframe.contents().find("img").each(function() {
				var $this = $(this);
				if (!imgMap.has($this.attr("data-original"))) {
					var request = $.get("./CreatePost.action?getDataUrl&extUrl=" + encodeURIComponent($this.attr("data-original")), function(result){
						$this.attr("src", result);
						imgMap.set($this.attr("data-original"), result);
				    });
					imgRequest.push(request);
				}
			});
		}
	};
});