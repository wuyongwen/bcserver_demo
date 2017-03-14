var uploadCoverTask = null;
var uploadCoverOriTask = null;
var setUploadCoverTask = function(typeVal, widthVal, heightVal, imgDescVal, redirectUrlVal, srcDataVal) {
	uploadCoverTask = {type:typeVal, width:widthVal, height:heightVal, imgDesc:imgDescVal, redirectUrl:redirectUrlVal, srcData:srcDataVal};
};
var setUploadCoverOriTask = function(typeVal, widthVal, heightVal, imgDescVal, redirectUrlVal, srcDataVal) {
	uploadCoverOriTask = {type:typeVal, width:widthVal, height:heightVal, imgDesc:imgDescVal, redirectUrl:redirectUrlVal, srcData:srcDataVal};
};

var handleExternalImg = function(url) {
	$("#croppingArea").dialog("open");
	if(url.indexOf("http") == 0) {
		$.get("./CreatePost.action?getDataUrl&extUrl=" + encodeURIComponent(url), function(result){
			var imgView = $(".img-container img");
			var options = {
			          aspectRatio: 16 / 9,
			          data: {
			            x: 480,
			            y: 60,
			            width: 640,
			            height: 360
			          }
			        };
			imgView.cropper(options).cropper("replace", result);
			$(".img-container").show();
			$("#confirmCover").show();
		});
	}
	else if(url.indexOf("data") == 0) {
		var imgView = $(".img-container img");
		var options = {
		          aspectRatio: 16 / 9,
		          data: {
		            x: 480,
		            y: 60,
		            width: 640,
		            height: 360
		          }
		        };
		imgView.cropper(options).cropper("replace", url);
		$(".img-container").show();
		$("#confirmCover").show();
	}
};

$(function() {

	var fileInputType = "image/jpeg";
	$("#croppingArea").dialog({
		autoOpen: false,
		maxWidth:700,
	    maxHeight: 700,
	    minWidth: 700,
	    minHeight: 700,
	    modal: true,
	    title: "Select Cover..."
	});
	
  (function() {
    var $image = $(".img-container img"),
        $dataX = $("#dataX"),
        $dataY = $("#dataY"),
        $dataHeight = $("#dataHeight"),
        $dataWidth = $("#dataWidth"),
        options = {
          aspectRatio: 16 / 9,
          data: {
            x: 480,
            y: 60,
            width: 640,
            height: 360
          },
          done: function(data) {
            $dataX.val(Math.round(data.x));
            $dataY.val(Math.round(data.y));
            $dataHeight.val(Math.round(data.height));
            $dataWidth.val(Math.round(data.width));
          }
        };
    
    $(".img-container").hide();
    $("#confirmCover").hide();
    
    $image.cropper(options).on({
      "build.cropper": function(e) {
        console.log(e.type);
      },
      "built.cropper": function(e) {
        console.log(e.type);
      }
    });

    $(document).on("click", "[data-method]", function () {
      var data = $(this).data();

      if (data.method) {
        $image.cropper(data.method, data.option);
      }
    });

    var $inputCoverImage = $("#inputCoverImage");
    
    $("#confirmCover").click(function(e){
    	var srcDataUrl = $image.cropper("getDataURL", fileInputType);
    	var previewCover = $("#previewCover"); 
    	previewCover.attr("src", srcDataUrl);
    	setUploadCoverTask('PostCover', previewCover[0].naturalWidth, previewCover[0].naturalHeight, $("#coverDescription").val(), $("#coverRedirectUrl").val(), srcDataUrl);
    	var previewCoverOri = $("#previewOriCover"); 
    	previewCoverOri.attr("src", $("#coverOriImg").attr("src"));
    	setUploadCoverOriTask('PostCoverOri', previewCoverOri[0].naturalWidth, previewCoverOri[0].naturalHeight, "", "", previewCoverOri.attr("src"));
    	$("#croppingArea").dialog("close");
    });
    
    if (window.FileReader) {
      $inputCoverImage.change(function() {
        var fileReader = new FileReader(),
            files = this.files,
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
          $("#croppingArea").dialog("open");
          fileReader.readAsDataURL(file);
          fileReader.onload = function () {
            $image.cropper("reset", true).cropper("replace", this.result);
            $(".img-container").show();
            $("#confirmCover").show();
          };
        } else {
          alert("Please choose an image file.");
        }
      });
    } else {
      $inputCoverImage.addClass("hide");
    }

    $("[data-toggle='tooltip']").tooltip();
  }());

});
