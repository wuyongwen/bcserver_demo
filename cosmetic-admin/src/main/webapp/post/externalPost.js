var toAddPostNewId = [];
var toAddLinkId = [];
var imgRequest = [];
var imgMap = new Map();

$(document).ready(function(){
			
	var opts = {lines: 13, length: 20, width: 10, radius: 30, corners: 1, rotate: 0, direction: 1, color: '#000', speed: 1, trail: 60, shadow: false, hwaccel: false, className: 'spinner', zIndex: 2e9, top: '50%', left: '50%'};
	var spinner = new Spinner(opts).spin($("#displayDiv")[0]);
	
	var enableHyperlink = function($link) {
		$link.attr("href", $link.attr("link"));
		$link.css("color", "#477cae");
	};
	
	var disableHyperlink = function($link) {
		$link.prop( "disabled", true);
		$link.removeAttr("href");
		$link.css("color", "black");
	};
	
	$("#articleTable").load(function() {
		var $iframe = $(this);
		$iframe.contents().find("input[type='checkbox'][name='checkIndexes']").each(function(){
			var $this = $(this);
			$this.prop( "disabled", false);
			if($.inArray($this.val(), toAddPostNewId) <= -1) {
				$this.prop('checked', false);
			}else
				$this.prop('checked', true);
			$this.change(function() {
				var $this1 = $(this);
				if(!$this1.is(':checked'))
					toAddPostNewId.splice($.inArray($this1.val(), toAddPostNewId),1);
				else
					toAddPostNewId.push($this1.val());
			});
		});
		$iframe.contents().find("input[type='checkbox'][name='checkall']").each(function(){
			var $this = $(this);
			$this.prop( "disabled", false);
			$this.change(function() {
				var $this1 = $(this);
				var checklist = $iframe.contents().find("input[type='checkbox'][name='checkIndexes']");
				for ( var i=0 ; i < checklist.length ; i++ ) {
					if(!$this1.is(':checked')) {
						var v = checklist[i].value;
						if (toAddPostNewId.indexOf(v) != -1)
							toAddPostNewId.splice(toAddPostNewId.indexOf(v),1);
					}
					else {
						if (toAddPostNewId.indexOf(checklist[i].value) == -1)
							toAddPostNewId.push(checklist[i].value);
					}
				}
			});
			
		});
		$iframe.contents().find("input[type='checkbox'][name='checkLinkIndexes']").each(function(){
			var $this = $(this);
			var $tr = $this.parents("tr");
			var $link = $tr.find("#externalLink");
			$this.prop( "disabled", false);
			if($.inArray($this.val(), toAddLinkId) <= -1) {
				enableHyperlink($link);
				$this.prop('checked', true);
			}else {
				disableHyperlink($link);
				$this.prop('checked', false);
			}
			$this.change(function() {
				var $this1 = $(this);
				var $tr1 = $this1.parents("tr");
				var $link1 = $tr1.find("#externalLink");
				if($this1.is(':checked')) {
					enableHyperlink($link1);
					toAddLinkId.splice($.inArray($this1.val(), toAddLinkId),1);
				}
				else {
					disableHyperlink($link1);
					toAddLinkId.push($this1.val());
				}
			});
		});
		$iframe.contents().find("input[type='checkbox'][name='checkLinkall']").each(function(){
			var $this = $(this);
			$this.prop( "disabled", false);
			$this.change(function() {
				var $this1 = $(this);
				var checklist = $iframe.contents().find("input[type='checkbox'][name='checkLinkIndexes']");
				for ( var i=0 ; i < checklist.length ; i++ ) {
					if($this1.is(':checked')) {
						var v = checklist[i].value;
						if (toAddLinkId.indexOf(v) != -1)
							toAddLinkId.splice(toAddLinkId.indexOf(v),1);
					}
					else {
						if (toAddLinkId.indexOf(checklist[i].value) == -1)
							toAddLinkId.push(checklist[i].value);
					}
				}
			});
			
		});
		// hittest region.
		$iframe.contents().find("[id='checkboxRegion']").each(function(){
			var $this = $(this);
			var $checkbox = $this.find("#checkIndexes");
			$this.find("#region").click(function() {
				$this.find("#checkIndexes").prop('checked', !$checkbox.is(':checked'));
				if (!$checkbox.is(':checked'))
					toAddPostNewId.splice($.inArray($checkbox.val(), toAddPostNewId),1);
				else
					toAddPostNewId.push($checkbox.val());
			});
		});
		
		$iframe.contents().find("[id='checkboxLinkRegion']").each(function(){
			var $this = $(this);
			var $checkbox = $this.find("#checkLinkIndexes");
			var $tr = $checkbox.parents("tr");
			var $link = $tr.find("#externalLink");
			$this.find("#region").click(function() {
				$this.find("#checkLinkIndexes").prop('checked', !$checkbox.is(':checked'));
				if ($checkbox.is(':checked')) {
					enableHyperlink($link);
					toAddLinkId.splice($.inArray($checkbox.val(), toAddLinkId),1);
				}
				else {
					disableHyperlink($link);
					toAddLinkId.push($checkbox.val());
				}
			});
		});
		
		$iframe.contents().find("img").each(function(){
			var $this = $(this);
			if ($this.attr("data-cropped") == "true")
				$this.hide();
			$this.load(function() {
				var $table = $iframe.contents().find("#row");
				var minHeight = $table.height() +  $table.position().top * 2 + $iframe.contents().find(".pagelinks").height() * 2;
				$("#displayDiv").height(minHeight + $("#pageSizeSel").height() + $("#nextStepDiv").height() + 10);
				$iframe.height(minHeight);
			});
			if (imgMap.has($this.attr("data-original"))) {
				if ($this.attr("data-cropped") == "false")
					$this.attr("src", imgMap.get($this.attr("data-original")));
				else
					$this.show();
			} else {
				var img = $.get("./CreatePost.action?getDataUrl&extUrl=" + encodeURIComponent($this.attr("data-original")), function(result){
					if ($this.attr("data-cropped") == "false")
						$this.attr("src", result);
					else
						$this.show();
					imgMap.set($this.attr("data-original"), result);
			    });
				imgRequest.push(img);
			}
		});
		
		$iframe.contents().find(".pagelinks a").click(function() {
			// terminate request
			if (imgRequest != null && imgRequest.length > 0) {
				for (var i=0 ; i < imgRequest.length ; i++) {
					imgRequest[i].abort();
				}
				imgRequest = [];
			}
			spinner.spin($("#displayDiv")[0]);
		});
		window.scrollTo(0, 0);
		spinner.stop();
	});
	
	$("#rescueProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Posting..."
	});
	
	$("#autoPostConfig").click(function() {
		var apiUrl = "./externalPost.action?config";
		var data = "indexs=" + toAddPostNewId.toString();
		var data2 = "linkIndexs=" + toAddLinkId.toString();
		window.location.href = apiUrl + "&" + data + "&" + data2 + "&isNext=true";
	});

	$("#pageSizeSel").change(function() {
		spinner.spin($("#displayDiv")[0]);
		$("#articleTable").attr("src", "./externalPost.action?getList&pageSize=" + $("#pageSizeSel option:selected").val());
	});
	
});

