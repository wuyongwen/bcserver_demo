
$(document).ready(function(){
	var toEdit = {};
	var addEditTask = function(row) {
		toEdit[row.find(".productIdDiv").text()] = row;
		var taskCount = Object.keys(toEdit).length;
		if(taskCount > 0) {
			var applyChangeBtn = $("#applyAllChange"); 
			applyChangeBtn.val("Apply change for " + Object.keys(toEdit).length + " product");
			applyChangeBtn.show();
		}
	};
	
	//	var toEdit = [];
	$(document).click(function(event){
		var targetId = $(event.target).attr("id");
	    if(targetId != "brandsSelDiv" && targetId !=  "selBrandId") {
	    	$("#brandsSelDiv").hide();
	    }
	    if(targetId != "productTypeRmvDiv" && targetId !=  "selTypeId") {
	    	$("#categorySelRDiv").hide();
	    }
	    if(targetId != "productTypeEditDiv" && targetId !=  "selTypeId"){
	    	$("#categorySelEDiv").hide();
	    }
	});
	
	$(window).scroll(function() {
		$("#brandsSelDiv").hide();
		$("#categorySelRDiv").hide();
		$("#categorySelEDiv").hide();
	});
	
	$(".brandEditDiv").click(function(event) {
		$("#categorySelEDiv").hide();
		$("#categorySelRDiv").hide();
		event.stopPropagation();
		var $this = $(this);
		var divPanel = $("#brandsSelDiv").css({position: 'fixed', top: event.clientY, left: event.clientX});
		var brandSel = $("#brandsSelDiv #selBrandId");
		var toSelBrand = brandSel.find("option[value='"+ $this.attr("value") + "']");
		toSelBrand.attr('selected', 'selected');
		brandSel.unbind("change").change(function(event) {
			var selBox = $(this);
			var selOpt = selBox.find(":selected");
			$this.text(selOpt.text());
			$this.attr("value", selOpt.val());
			addEditTask($this.parent().parent());
			divPanel.hide();
		});
		divPanel.show();
	});
	
	var genProductInfo = function (row) {
		var productId = row.find(".productIdDiv").text();
		var resultMap = {};
		var data = "productId=" + productId;
		data += "&brandId=" + row.find(".brandEditDiv").attr("value");
		data += "&onShelf=" + (row.find(".shelfDiv option:selected").attr("value") == "1" ? "true" : "false");
		var productTitle = row.find(".productTitleDiv").val().trim();
		if(productTitle.length <= 0) {
			resultMap["result"] = false;
			resultMap["error"] = "Product " + productId + " must have a valid title";
			return resultMap;
		}
		data += "&productTitle=" + encodeURIComponent(productTitle);
		var typeRList = row.find(".productTypeRmvDiv");
		var typeEList = row.find(".productTypeEditDiv");
		if(typeRList.length + typeEList.length <= 0) {
			resultMap["result"] = false;
			resultMap["error"] = "Product " + productId + " must have at least one category";
			return resultMap;
		}
			
		for(idx = 0; idx < typeEList.length; idx++) {
			data += "&newProdTypeId=" + $(typeEList[idx]).attr("value");
		}
		for(idx = 0; idx < typeRList.length; idx++) {
			data += "&newProdTypeId=" + $(typeRList[idx]).attr("value");
		}
		
		resultMap["result"] = true;
		resultMap["data"] = data;
		return resultMap;
	};
	
	var applyAllChange = function (onComplete) {
		var taskCount = Object.keys(toEdit).length;
		if(taskCount <= 0) {
			onComplete();
			return;
		}
		
		var onFailed = function(err) {
			$("#productEditProgress").append(err);
       	 	applyAllChange(onComplete);
		}
		
		var nextTaskKey = Object.keys(toEdit)[0];
		var changedRow = toEdit[nextTaskKey];
		delete toEdit[nextTaskKey];
		var dataMap = genProductInfo(changedRow);
		if(dataMap["result"] == false) {
			onFailed(dataMap["error"] + "<br>")
			return;
		}
		var productId = changedRow.find(".productIdDiv").text();
        $.post("./ProductManage.action?submitProductUpdates", dataMap["data"], function(responseJson) {
        	$("#productEditProgress").append("Update product : " + productId + " complete.<br>");
        	applyAllChange(onComplete);
         }).fail(function(e) {
        	 onFailed("Update product : " + productId + " failed.<br>");
         });
	};
	
	$("#applyAllChange").click(function(event) {
		var $dialog = $("#productEditProgress");
		$dialog.empty();
		$dialog.dialog("open");
		event.preventDefault();
		applyAllChange(function() {
			$("#productEditProgress").append("All complete.<br>");
   	     	$("#applyAllChange").hide();
		});
	});
	
	$("#productEditProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        title: "Updating..."
	});

	var modifyType = function($this, selBox) {
		var selOpt = selBox.find(":selected");
		$this.attr("value", selOpt.val());
		$this.text(selOpt.text());
		addEditTask($this.parent().parent());
	};
	
	$(".productTypeEditDiv").click(function(event) {
		$("#brandsSelDiv").hide();
		$("#categorySelRDiv").hide();
		event.stopPropagation();
		var $this = $(this);
		var divPanel = $("#categorySelEDiv").css({position: 'fixed', top: event.clientY, left: event.clientX});
		var typeSel = $("#categorySelEDiv #selTypeId");
		var toSelType = typeSel.find("option[value='"+ $this.attr("value") +"']");
		toSelType.attr('selected', 'selected');
		typeSel.unbind("change").change(function(event) {
			modifyType($this, $(this));
			divPanel.hide();
		});
		divPanel.show();
	});
	
	var removeType = function($this, selBox) {
		var selOpt = selBox.find(":selected");
		if(selOpt.val() == "remove") {
			var r = confirm("Delete this categorty from product?");
			if(r == true) {
				addEditTask($this.parent().parent());
				$this.remove();
			}
		}
		else {
			modifyType($this, selBox);
		}
	};
	
	var removeTypeClick = function(event, $this) {
		$("#brandsSelDiv").hide();
		$("#categorySelEDiv").hide();
		event.stopPropagation();
		var divPanel = $("#categorySelRDiv").css({position: 'fixed', top: event.clientY, left: event.clientX});
		var typeSel = $("#categorySelRDiv #selTypeId");
		var toSelType = typeSel.find("option[value='"+ $this.attr("value") +"']");
		toSelType.attr('selected', 'selected');
		typeSel.unbind("change").change(function(event) {
			removeType($this, $(this));
			divPanel.hide();
		});
		divPanel.show();
	};
	
	$(".productTypeRmvDiv").click(function(event) {
		removeTypeClick(event, $(this));
	});
	
	$(".addCategory").click(function(event) {
		$("#brandsSelDiv").hide();
		$("#categorySelRDiv").hide();
		event.stopPropagation();
		var $this = $(this);
		var divPanel = $("#categorySelEDiv").css({position: 'fixed', top: event.clientY, left: event.clientX});
		var typeSel = $("#categorySelEDiv #selTypeId");
		var toSelType = typeSel.find("option[value='empty']");
		toSelType.attr('selected', 'selected');
		typeSel.unbind("change").change(function(event) {
			var selBox = $(this);
			var selOpt = selBox.find(":selected");
			var newCategoryBox = $("<div class='productTypeRmvDiv' value='"+ selOpt.val() +"'>"+ selOpt.text() +"</div>");
			newCategoryBox.click(function(event) {
				removeTypeClick(event, $(this));
			});
			newCategoryBox.insertBefore($this);
			addEditTask($this.parent().parent());
			divPanel.hide();
		});
		divPanel.show();
	});
	
	$(".productTitleDiv").change(function(){
		var $this = $(this);
		if($this.val().trim().length <= 0) {
			alert("Not a valid title for a product");
			$this.val($this.attr("title"));
			return;
		}
		addEditTask($this.parent().parent());
	});
	
	$(".shelfDiv select").change(function(){
		addEditTask($(this).parent().parent().parent());
	});

});