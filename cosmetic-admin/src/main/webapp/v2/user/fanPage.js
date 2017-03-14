var keywordCircleMap = {};
var postData = {};
var dataArray = [];
var x_dim = 0;
var last_x_dim = -1;
var simpleLock = false;
var checkboxArray = [[]];
var checkboxValue = [];
var checkAllValue = [];

$(document).ready(function() {
	
	$("#previewPostView").toggle(false);
	$("#fb_table").toggle(false);
	$("#prevBtn").toggle(false);
	$("input[name='optradio'][value='1']").prop("checked", true);
	$("#radio").toggle(false);
	if(document.getElementById("autoPost").value == "true"){
		$("input[id='autoPostCheck']").prop("checked", true);
	}
	
	$("#postProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        closeOnEscape: false,
        title: "Posting...",
        open: function(event, ui) { $(".ui-dialog-titlebar-close", ui.dialog | ui).hide();}
	});
	
	$("#searchProgress").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        closeOnEscape: false,
        title: "Pulling...",
        open: function(event, ui) { $(".ui-dialog-titlebar-close", ui.dialog | ui).hide();}
	});
	
	$('.datepick').each(function(){
		var opt={dateFormat: 'yy-mm-dd',
				showSecond: true,
				timeFormat: 'HH:mm:ss',
	            showTimezone: false
	           };
	    $(this).datetimepicker(opt);
	});
	
	$('#resettime').click(function(){
	    $("#sincetimepicker").val("");
		$("#untiltimepicker").val("");
	});
	
	$('#fanPageNameSel').change(function(){
		$("#sincetimepicker").val("");
		$("#untiltimepicker").val("");
	});
	
	$(".circleId_keyword").each(function(index, element) {
		var circleId = $(element).find("#key").val();
		var keyword = $(element).find("#value").val();
		keywordCircleMap[circleId] = keyword;
	});
	
	$("#saveBtn > a").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		
		changeInput();
		$(".circleId_circleName").each(function(index, element) {
			var circleId = $(element).find("#cId").val();
			if (typeof keywordCircleMap[circleId] == "undefined")
				keywordCircleMap[circleId] = "";
		});
		var postUrl = "create-fan-page-user.action?updateTagCircleMap";
		var data = "tagCircleMap=";
		data += encodeURIComponent(JSON.stringify(keywordCircleMap));

		$.post(postUrl, data, function(responseJson) {
			if (responseJson == "done")
				window.location.href = "./create-fan-page-user.action";
			else {
				alert(responseJson);
				simpleLock = false;
			}
		}).fail(function(e) {
		     alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		     simpleLock = false;
		});
	});
	
	$("#addBtn > a").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		
		var checkFieldErr = checkFieldComplete();
	    if(checkFieldErr != null) {
		     alert(checkFieldErr);
		     return;
	    }
		
		var fanPageLink = $("#fanPageLink").val();
		var data = "fanPageLink=" + fanPageLink;
		var postUrl = "create-fan-page-user.action?newFanPage";

		$.post(postUrl, data, function(responseJson) {
			if(responseJson != "done"){
				alert(responseJson);
				simpleLock = false;
			} else {
				window.location.href = "./create-fan-page-user.action";
			}
		}).fail(function(e) {
		     alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		     simpleLock = false;
		});
	});
	
	$("#cancelBtn > a").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		
		var fanPageId = $("#fanPageNameSel").val();
		if(fanPageId == null || fanPageId == ""){
			alert("Please add a facebook fanpage first!");
			simpleLock = false;
			return;
		}
		var data = "fanPageId=" + fanPageId;
		
		var postUrl = "create-fan-page-user.action?deleteFanPage";

		$.post(postUrl, data, function(responseJson) {
			window.location.href = "./create-fan-page-user.action";
		}).fail(function(e) {
		     alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		     simpleLock = false;
		});

	});
	
	$("#pullBtn > a").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		
		var fanPageId = $("#fanPageNameSel").val();
		if(fanPageId == null || fanPageId == ""){
			alert("Please add a facebook fanpage first!");
			simpleLock = false;
			return;
		}
		
		postData = {};
		dataArray = [];
		x_dim = 0;
		last_x_dim = -1;
		checkboxArray = [[]];
		checkboxValue = [];
		checkAllValue = [];
		$("#prevBtn").toggle(false);
		$("#nextBtn").toggle(true);
		$("#fb_table").toggle(false);
		$("#radio").toggle(false);
		
		var fanPageId = $("#fanPageNameSel").val();
		var sinceTime = $("#sincetimepicker").val();
		var untilTime = $("#untiltimepicker").val();
		var data = "fanPageId=" + fanPageId + "&sincetimepicker=" + sinceTime + "&untiltimepicker=" + untilTime + "&status=true";
		document.getElementById("dataFanPage").value = fanPageId;
		queryJsonFromServer(data);
	});
	
	$("#nextBtn").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		$("#nextBtn").toggle(false);
		
		// save checkbox status
		checkboxValue = [];
		var count = 0;
		$(".fb_list1 input:checkbox").each(function(){
			if($(this).prop("checked")){
				checkboxValue[count] = $(this).parent('span').attr('MainValue');
				count++;
			}
		});
		checkboxArray[x_dim] = [];
		checkboxArray[x_dim] = checkboxValue;
		checkAllValue[x_dim] = $("#checkAll").prop("checked");
		
		x_dim++;
		if(typeof dataArray[x_dim] == "undefined"){
			var fanPageId = $("#fanPageNameSel").val();
			var data = "fanPageId=" + fanPageId + "&status=true";
			if (typeof postData["next"] != "undefined")					
				data += "&nextUrl=" + encodeURIComponent(postData["next"]);
			queryJsonFromServer(data);
		} else {
			reDrawList(dataArray[x_dim]);
			simpleLock = false;
		}
	});
	
	$("#prevBtn").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		$("#nextBtn").toggle(true);
		
		// save checkbox status
		checkboxValue = [];
		var count = 0;
		$(".fb_list1 input:checkbox").each(function(){
			if($(this).prop("checked")){
				checkboxValue[count] = $(this).parent('span').attr('MainValue');
				count++;
			}
		});
		checkboxArray[x_dim] = checkboxValue;
		checkAllValue[x_dim] = $("#checkAll").prop("checked");
		
		x_dim--;
		reDrawList(dataArray[x_dim]);
		simpleLock = false;
	});
	
	$("#backBtn").click(function(event) {

		$("#previewPostView").toggle(false);
		$("#normalView").toggle(true);
        $(".page-header").html("Facebook Fanpage Link");
	});
	
	$("#confirmBtn > a").click(function(event) {
		if(simpleLock)
			return;
		simpleLock = true;
		
		var fanPageId = $("#fanPageNameSel").val();
		if(fanPageId == null || fanPageId == ""){
			alert("Please add a facebook fanpage first!");
			simpleLock = false;
			return;
		}
		
		// save checkbox status
		checkboxValue = [];
		var count = 0;
		$(".fb_list1 input:checkbox").each(function(){
			if($(this).prop("checked")){
				checkboxValue[count] = $(this).parent('span').attr('MainValue');
				count++;
			}
		});
		checkboxArray[x_dim] = [];
		checkboxArray[x_dim] = checkboxValue;
		
		var datas = [];
		for(var i = 0; i < checkboxArray.length; i++){
			for(var j = 0; j < checkboxArray[i].length; j++){
				var circleId = dataArray[i][checkboxArray[i][j]-1].circleId;
				if(circleId == null || circleId == "" || circleId == "null"){
					alert("You will not be able to create a post without any circle." +
							"Please refine your circle keyword mapping!");
					simpleLock = false;
					return;
				}
				var jsonData = {};
				var title = dataArray[i][checkboxArray[i][j]-1].title;
				var content = dataArray[i][checkboxArray[i][j]-1].content;
				var imgUrl = dataArray[i][checkboxArray[i][j]-1].imgUrl;
				var userId = dataArray[i][checkboxArray[i][j]-1].userId;
				var locale = dataArray[i][checkboxArray[i][j]-1].locale;
				var createdTime = dataArray[i][checkboxArray[i][j]-1].createdTime;
				jsonData["title"] = title;
				jsonData["content"] = content;
				jsonData["imgUrl"] = imgUrl;
				jsonData["circleId"] = circleId;
				jsonData["userId"] = userId;
				jsonData["locale"] = locale;
				jsonData["createdTime"] = createdTime;
				if(typeof dataArray[i][checkboxArray[i][j]-1].redirectUrl != "undefined"){
					var redirectUrl = dataArray[i][checkboxArray[i][j]-1].redirectUrl;
					jsonData["redirectUrl"] = encodeURIComponent(redirectUrl);
				}
				datas.push(jsonData);
			}
		}
		
		var requestUrl = "create-fan-page-user.action?postData";
		var data = new FormData();
		if (datas.length > 0 ) {
			var dataObj = {"data": datas};
			data.append("forwardData", JSON.stringify(dataObj));
		}
		data.append("fanPageId", document.getElementById("dataFanPage").value);
		if($("input[name='optradio']:checked").val() == 1)
			data.append("status", "Published");
		else
			data.append("status", "Drafted");
		if($("#autoPostCheck").prop("checked")){
			data.append("autoPost", "true");
		}
		
		$("#postProgress").dialog("open");
		jQuery.ajax({
			url : requestUrl,
			data : data,
			cache : false,
			mimeType : "multipart/form-data",
			contentType : false,
			processData : false,
			type : 'POST',
			success : function(result) {
				if(result == "null")
					window.location.href = "./create-fan-page-user.action";
				else {
					alert(result);
					simpleLock = false;
				}
				$("#postProgress").dialog("close");
			},
			error : function (jqXHR, textStatus, errorThrown) {
				alert("Failed : " + errorThrown + " " + textStatus);
				simpleLock = false;
				$("#postProgress").dialog("close");
			}
		});
	});
	
	var checkFieldComplete = function() {
		var fanPageLink = $("#fanPageLink").val();
		if(fanPageLink.length <= 0){
			simpleLock = false;
			return "Please input the Public Fan Page Link";
		}
		return null;
	};	
	
	var reDrawList = function(posts) {
		if (x_dim == last_x_dim)
			$("#nextBtn").toggle(false);
		else if(posts.length < 20)
			$("#nextBtn").toggle(false);
		else
			$("#nextBtn").toggle(true);
		
		if(x_dim <= 0)
			$("#prevBtn").toggle(false);
		else
			$("#prevBtn").toggle(true);
		
		for(var i = 0; i < 20; i++ ){
			var id = "post" + (i+1);
			document.getElementById(id).innerHTML = null;
		}
		
		for(var i = 0; i < posts.length; i++){
			var d = new Date(parseInt(posts[i].createdTime));
			
			var selection = '<div class="post_item1-2"> <select id="cSel" onchange="changeCircle(this);" MainValue="' + (i+1) + '">';
			$(".circleId_circleName").each(function(c_index, c_element) {
				var cId = $(c_element).find("#cId").val();
				var cName = $(c_element).find("#cName").val();
				if(cId == posts[i].circleId)
					selection += '<option value="'+ cId +'" selected>' + cName + '</option>';
				else
					selection += '<option value="'+ cId +'">' + cName + '</option>';
			});
			selection += '</select> </div>';
			
			var html = 
				'<div class="post_itemc" id="post_itemc"><p style"min-height:16px;display:block;"></p><span MainValue="' + (i+1) + '"><input style"margin:auto;" type="checkbox"></span></div>' +
				'<div class="post_item1">' + d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate() + '</div>' + 
				'<div class="post_item2"><a href="#" item_number="' + i + '">' +posts[i].title + '</a> </div>' +
				selection +
				'<div class="clear"></div>';
			
			var id = "post" + (i+1);
			document.getElementById(id).innerHTML = html;
		}
		
		var selectNum = 0;
		if (typeof checkAllValue[x_dim] == "undefined")
			$("#checkAll").prop("checked", false);
		else
			$("#checkAll").prop("checked", checkAllValue[x_dim]);
		
		if(typeof checkboxArray[x_dim]!= "undefined") {
			for(var i = 0; i < checkboxArray[x_dim].length; i++){
				$(".fb_list1 input:checkbox").each(function(){
					if($(this).parent('span').attr('MainValue') == checkboxArray[x_dim][i]){
						$(this).prop( "checked", true );
						selectNum++;
					}
				});
			}
		}
		
		$("#checkAll").change(function() {
			var checkAll = $(this);
	        $(".fb_list1 input:checkbox").each(function(){
	        	$(this).prop("checked", checkAll.is(':checked'));
			});
	        
	        if (checkAll.is(':checked'))
	        	selectNum = posts.length;
	        else
	        	selectNum = 0;
	    });
		
		$(".fb_list1 input:checkbox").change(function() {
			if ($(this).is(':checked'))
				selectNum++;
			else if (selectNum > 0)
				selectNum--;
				 
			if (selectNum == posts.length)
				$("#checkAll").prop("checked", true);
			else
				$("#checkAll").prop("checked", false); 
		 });
		
		$(".post_item2 > a").click(function(event) {
			var itemNumber = $(this).attr('item_number');
			$("#postPreviewTitle").html(posts[itemNumber].title);

			var post = $(".post").clone(true);
			$(".preview_ctn").empty();
				
			post.children("img").attr("src", posts[itemNumber].imgUrl);
			post.find(".previewContent").html(posts[itemNumber].content);
			if(typeof posts[itemNumber].redirectUrl == "undefined"){
				post.find(".previewRedirectUrl").attr("href", null);
				post.find(".previewRedirectUrl").html(null);
			} else {
				post.find(".previewRedirectUrl").attr("href", posts[itemNumber].redirectUrl);
				post.find(".previewRedirectUrl").html(posts[itemNumber].redirectUrl);
			}
		    $(".preview_ctn").append(post);
			post = $(".post").clone(true);

			$("#previewPostView").toggle(true);
			$("#normalView").toggle(false);
		    $(".page-header").html("Preview	post");
		});
	};
	
	function queryJsonFromServer(data) {
		
		$("#searchProgress").dialog("open");
		var postUrl = "create-fan-page-user.action?pullData";
		
		$.post(postUrl, data, function(responseJson) {
			try {
				postData = JSON.parse(responseJson);
				if (postData == null) {
					alert("no post left in this fan page.");
				} else {
					var sinceTime = postData["sinceTime"];
					if (sinceTime != null)
						$("#sincetimepicker").val(sinceTime);
					
					var errorMsg = postData["errorMsg"];
					if(errorMsg != null){
						alert(errorMsg);
					} else {
						var datas = postData["data"];
						if(datas != null && datas.length != 0){
							$("#fb_table").toggle(true);
							$("#radio").toggle(true);			
							reDrawList(datas);		
							dataArray.push(datas);
							simpleLock = false;
							$("#searchProgress").dialog("close");
							return;
						} else {
							alert("no post left in this fan page.");
						}
					}				
				}
			} catch(e) {
				alert(responseJson);
				console.log("[parse postData error] " + e);
			}
			x_dim--;
			last_x_dim = x_dim;
			simpleLock = false;
			$("#nextBtn").toggle(false);
			$("#searchProgress").dialog("close");
			return;
			
		}).fail(function(e) {
		     alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		     x_dim--;
		     last_x_dim = x_dim;
		     simpleLock = false;
		     $("#nextBtn").toggle(false);
		     $("#searchProgress").dialog("close");
		});
	}
});

function changeInput() {
	
	var e = document.getElementById("circleSel");
	var cId = e.options[e.selectedIndex].value;
	var cKeyWord = document.getElementById("add_hashtag").value;
	var last_cId = document.getElementById("last_circle").value;

	if (last_cId != "-----" && last_cId != "") {
		keywordCircleMap[last_cId] = cKeyWord;
	}

	document.getElementById("last_circle").value = cId;

	if (typeof keywordCircleMap[cId] !== "undefined")
		document.getElementById("add_hashtag").value = keywordCircleMap[cId];
	else
		document.getElementById("add_hashtag").value = "";
}

function changeCircle(element) {
	var cId = element.value;
	var idx = element.getAttribute("MainValue");
	dataArray[x_dim][idx - 1].circleId = cId;
}