var toRescue = {};
$(document).ready(function(){	
	$.fn.singleAndDouble = function(singleClickFunc, doubleClickFunc) {
	  var timeOut = 50;
	  var timeoutID = 0;
	  var ignoreSingleClicks = false;
	  
	  this.on('click', function(event) {
	    if (!ignoreSingleClicks) {
	      clearTimeout(timeoutID);
	      
	      timeoutID = setTimeout(function() {
	        singleClickFunc(event);
	      }, timeOut);
	    }
	  });
	  
	  this.on('dblclick', function(event) {
	    clearTimeout(timeoutID);
	    ignoreSingleClicks = true;
	    
	    setTimeout(function() {
	      ignoreSingleClicks = false;
	    }, timeOut);
	    
	    doubleClickFunc(event);
	  });
	  
	};
		
	var disputePostMap = {};
	var opts = {lines: 13, length: 20, width: 10, radius: 30, corners: 1, rotate: 0, direction: 1, color: '#000', speed: 1, trail: 60, shadow: false, hwaccel: false, className: 'spinner', zIndex: 2e9, top: '50%', left: '50%'};
	var spinner = new Spinner(opts).spin($("#displayDiv")[0]);
	//var toRescue = {};
	var tableRow = $("#row");
	var selectedRegion = $("#selRegion").val();
	var selectedCircleTypeId = $("#selCircleTypeId").val();
	if(selectedCircleTypeId === undefined)
		selectedCircleTypeId = "0";
	var selectedCreatorType = $("#selCreatorType").val();
	if(selectedCreatorType === undefined)
		selectedCreatorType = "All";
	var poolType = "Qualified";
	var pageSize = $("#pageSizeSel option:selected").val();
	var totalSize = null;
	var curPageIdx = 0;
	var preloadSize = 5;
	var isWarnedLinePostId = null;
	var warnendDate = null;
	var lastHandled = null;
	var resultTotalSize = null;
	var selfieCirTypeId = null;
	var revived = false;
	var reviewed = false;
	var preSelectQuality = 0;
	var preSelectCircle = false;
	var enableSeparator = false;
	
	var nothingToDisplay = function() {
		$(".pagelinks").hide();
		tableRow.hide();
		spinner.stop();
		$("#messageLbl").text("Nothing to display");
	};
	
	var loadPostToTable = function(responseJson, pageIdx) {
		$("#jumpToInput").val(pageIdx + 1);
		window.scrollTo(0, 0);
		$(".postPhoto").attr("src", "");
		var trs = tableRow.find("tbody tr.postRow");
		var results = responseJson.results;
		if(totalSize == null)
			totalSize = Math.ceil(responseJson.totalSize / pageSize);
		resultTotalSize = responseJson.totalSize;
		if(results.length <= 0 && totalSize == curPageIdx) {
			if(curPageIdx < 0) {
				nothingToDisplay();
				return;
			}
			
			curPageIdx -= 1;
			loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, curPageIdx, pageSize, true);
			return;
		}
		
		var separator;
		if(enableSeparator) {
			separator = $("#separator");
			if(separator !== undefined) {
				separator.remove();
			}
			separator = $("<tr id='separator' style='background-color: red;Color:#ffffff'> \
				<td colspan='4' style='text-align: center;vertical-align: center;line-height:4px;'> \
					<b>Handled</b> \
				</td>\
			</tr>");
		}
		$.each(trs, function(index, tr){
			var trRow = $(tr);
			if(index < results.length){
				var row = results[index];
				//Image
				var attachmentImg = trRow.find("#attachmentImg");
				attachmentImg.attr("pId", row.postIdStr);
				if(row.attachments.files.length > 1)
					attachmentImg.attr("src", row.attachments.files[1].downloadUrl);
				else if(row.attachments.files.length > 0)
					attachmentImg.attr("src", row.attachments.files[0].downloadUrl);
				else {
					attachmentImg.attr("src", "");
				}
				//Look Type
				var lookTypeImg = trRow.find("#lookTypeImg");
				lookTypeImg.attr("pId", row.postIdStr);
				lookTypeImg.empty();
				lookTypeImg.hide();
				if(row.noticeIconUrls != null) {
					$.each(row.noticeIconUrls, function(i, url) {
						var img = $("<img class='asd' src='" + url + "' style='position: absolute; right:0px; top:" + i * 15 + "%; max-width:15%;width:auto; height:auto;vertical-align: top'><br>");
						lookTypeImg.append(img);
					});
					lookTypeImg.show();
				}
				if(row.extPostLink != null) {
					var img = $("<a href='" + row.extPostLink +"' target='_blank'><img class='asd' src='./../common/theme/backend/images/extPost.png' style='position: absolute; right:0px; bottom:0%; max-width:15%;width:auto; height:auto;vertical-align: top'></a>");
					lookTypeImg.append(img);
					lookTypeImg.show();
				}
				// Title
				var title = trRow.find("#title");
				title.text(row.title);
				// Post Id
				var postId = trRow.find("#postId");
				postId.text("(" + row.postIdStr + ")");
				// Content
				var circle = trRow.find("#circle");
				circle.text(row.descCirName);
				// Create time
				var twCreateTime = trRow.find("#twCreateTime");
				twCreateTime.text(row.twTime);
				// PhotoScore
				var photoScore = trRow.find("#photoScore");
				photoScore.text(row.score);
				var scoreDate = trRow.find("#scoreDate");
				scoreDate.text(new Date(row.processScoreDate));
				// Creator
				var postCreator = trRow.find("#postCreator");
				postCreator.text(row.creator.displayName + " (" + row.creator.userId + ")");
				postCreator.attr("createrId",row.creator.userId);
				if(row.creator.avatar == null){
					postCreator.attr("avatar","");
				}else{
					postCreator.attr("avatar",row.creator.avatar);
				}
				postCreator.attr("uDisplyName",row.creator.displayName);
				// Like count
				var likeCount = trRow.find("#likeCount");
				likeCount.text(row.likeCount + " likes");
				// CircleIn Count
				var circleInCount = trRow.find("#circleInCount");
				circleInCount.text(row.circleInCount + " circleIn");
				// Popularity
				var popularity = trRow.find("#popularity");
				popularity.text((3 * row.circleInCount) + row.likeCount);
				// Content
				var content = trRow.find("#content");
				content.val(row.content);
				// Action
				var checkBoxUndecided = trRow.find("#checkBoxUndecided");
				checkBoxUndecided.attr("name", "action" + row.postIdStr);
				checkBoxUndecided.val(row.postIdStr);
				var checkBoxCatTrendDis = trRow.find("#checkBoxCatTrendDis");
				checkBoxCatTrendDis.attr("name", "action" + row.postIdStr);
				checkBoxCatTrendDis.val(row.postIdStr);
				var checkBoxSelCir = trRow.find("#checkBoxSelCir");
				checkBoxSelCir.attr("name", "action" + row.postIdStr);
				checkBoxSelCir.val(row.postIdStr);
				var checkBoxCat = trRow.find("#checkBoxCat");
				checkBoxCat.attr("name", "action" + row.postIdStr);
				checkBoxCat.val(row.postIdStr);
				var checkBoxAbandon = trRow.find("#checkBoxAbandon");
				checkBoxAbandon.attr("name", "action" + row.postIdStr);
				checkBoxAbandon.val(row.postIdStr);
				var checkChangeKeyword = trRow.find("#checkChangeKeyword");
				checkChangeKeyword.attr("name", "action" + row.postIdStr);
				checkChangeKeyword.val(row.postIdStr);
				checkChangeKeyword.prop('checked', false);
				var checkDel = trRow.find("#checkDel");
				checkDel.attr("name", "action" + row.postIdStr);
				checkDel.val(row.postIdStr);
				checkDel.prop('checked', false);
				var ratingInput = trRow.find(".rating");
				ratingInput.val(row.postIdStr);
				
				trRow.find(".selected").each(function(i, e) {
					$(e).removeClass("selected");
				});
				trRow.find(".cirSelected").each(function(i, e) {
					$(e).removeClass("cirSelected");
				});
				updateMainKeyIcon(trRow, false);
				
				var acDiv = trRow.find(".availableCirDiv");
				acDiv.css("pointer-events", "auto");				
				var exRes;
				if(!revived || preSelectQuality > 0 || preSelectCircle) {
					exRes = getRescueTask(row.postIdStr);
					if(preSelectQuality > 0 && exRes.quality === undefined)
						exRes.quality = preSelectQuality.toString();
					if(preSelectCircle && exRes.descCirIds.length <= 0) {
						exRes.descCirIds.push(row.circleTypeIdStr);
						/*if(exRes.type == "UNDECIDED")
							exRes.type = "CAT";*/
					}
				}
				else
					exRes = toRescue[row.postIdStr];
				
				var selectedRating = ratingInput.find("#r1");
				if(exRes != null) {
					switch (exRes.type) {
					  case "CAT":
						  checkBoxCat.prop('checked', true);
						  break;
					  case "CAT_TREND":
						  checkBoxCatTrendDis.prop('checked', true);
						  break;
					  case "SEL_CIR":
						  checkBoxSelCir.prop('checked', true);
						  acDiv.css("pointer-events", "none");
						  break;
					  case "ABANDON":
						  checkBoxAbandon.prop('checked', true);
						  break;
					  case "CHANGE_KEYWORD":
						  checkChangeKeyword.prop('checked', true);
						  break;
					  case "REMOVE":
						  checkDel.prop('checked', true);
						  break;
					  case "UNDECIDED":
					  default:
						  checkBoxUndecided.prop('checked', true);
						  break;
					}
					if(exRes.descCirIds !== undefined) {
						$.each(exRes.descCirIds, function(i, v){
							var selCir = trRow.find("img[cid='" + v + "']");
							if(selCir.length > 0)
								selCir.addClass("cirSelected");
							else {
								selCir = trRow.find("input[cid='" + v + "']");
								if(selCir.length > 0)
									selCir.addClass("cirSelected");
							}
							if(i == 0)
								updateMainKeyIcon(selCir.parent(), true);
						});
					}
					if(exRes.keywords !== undefined) {
						$.each(exRes.keywords, function(i, v) {
							var selKW = trRow.find("[kid='" + v.id + "']");
							if(selKW !== undefined)
								selKW.addClass("selected");
						});
					}
					if(exRes.quality != undefined) {
						switch(exRes.quality) {
						case "1":
							selectedRating = ratingInput.find("#r1");
							break;
						case "2":
							selectedRating = ratingInput.find("#r2");
							break;
						case "3":
							selectedRating = ratingInput.find("#r3");
							break;
							default:
								break;
						}
					}
					var push = trRow.find(".push");
					if(exRes.skip != undefined) {
						if(exRes.skip == "true") {
							push.addClass("selected");
							push.attr("value", true);
						}
						else {
							push.removeClass("selected");
							push.attr("value", false);
						}
					}
				}
				else
					checkBoxUndecided.prop('checked', true);
				
				
				selectedRating.addClass("selected");
				var circlesDiv = trRow.find(".availableCircles");
				circlesDiv.attr("pId", row.postIdStr);
				var extCirDiv = trRow.find(".extCirDiv");
				extCirDiv.attr("pId", row.postIdStr);
				
				var postTags = trRow.find(".postTagsDiv");
				postTags.attr("pId", row.postIdStr);
				if(!revived) {
					if(lastHandled > row.processScoreDate && isWarnedLinePostId == null) {
						trRow.addClass("lastHandled");
						if(enableSeparator)
							separator.insertBefore(trRow);
						isWarnedLinePostId = row.postIdStr;
						warnendDate = row.createdTime;
					}
					else if(isWarnedLinePostId == row.postIdStr) {
						trRow.addClass("lastHandled");
						if(enableSeparator)
							separator.insertBefore(trRow);
					}
					else if(lastHandled > row.processScoreDate && row.createdTime > warnendDate) {
						trRow.addClass("lastHandled");
						if(enableSeparator)
							separator.insertBefore(trRow);
						isWarnedLinePostId = row.postIdStr;
						warnendDate = row.createdTime;
					}
					else
						trRow.removeClass("lastHandled");
				}
				trRow.show();
			}
			else {
				trRow.hide();
			}
		});
		if(responseJson.totalSize > 0)
			$("#messageLbl").text("");
		else {
			nothingToDisplay();
			return;
		}

		var displayPgIdx = curPageIdx + 1;
		$(".previousBtn").removeClass("not-active");
		$(".nextBtn").removeClass("not-active");
		if(displayPgIdx == 1)
			$(".previousBtn").addClass("not-active");
		if(displayPgIdx >= totalSize)
			$(".nextBtn").addClass("not-active");
		$(".selPageOffset").val(displayPgIdx);
		$(".pgOffsetLbl").text("/" + totalSize);
		var pgOffsetNums = $("#disputePgBar1");
		var pgOffsetBarMin = parseInt(pgOffsetNums.attr("minIdx"));
		var pgOffsetBarMax = parseInt(pgOffsetNums.attr("maxIdx"));
		var startIdx = pgOffsetBarMin;
		var pgOffsetNums1 = pgOffsetNums.find(".pgNum");
		if(displayPgIdx >= pgOffsetBarMax || displayPgIdx <= pgOffsetBarMin) {
			var pgCount = pgOffsetNums.attr("pgCount");
			startIdx = displayPgIdx - Math.ceil(pgCount/2) > 1 ? displayPgIdx - Math.ceil(pgCount/2) : 1;
		}
		$.each(pgOffsetNums1, function(key, value) {
			var pgOffsetNum = $(value);
			pgOffsetNum.removeClass("not-active");
			var pgIdx = startIdx + key;
			if(pgIdx > totalSize) {
				pgOffsetNum.hide();
				pgOffsetNum.next("span").hide();
				return;	
			}
			else if(pgIdx == displayPgIdx)
				pgOffsetNum.addClass("not-active");
			pgOffsetNum.attr("pgIdx", pgIdx);
			pgOffsetNum.attr("title", "Go to page " + pgIdx); 
			pgOffsetNum.text(pgIdx); 
			pgOffsetNum.show();
			if(pgIdx < totalSize)
				pgOffsetNum.next("span").show();
			else
				pgOffsetNum.next("span").hide();
		});
		var pgOffsetNums2 = $("#disputePgBar2 .pgNum");
		$.each(pgOffsetNums2, function(key, value) {
			var pgOffsetNum = $(value);
			pgOffsetNum.removeClass("not-active");
			var pgIdx = startIdx + key;
			if(pgIdx > totalSize) {
				pgOffsetNum.hide();
				pgOffsetNum.next("span").hide();
				return;	
			}
			else if(pgIdx == displayPgIdx)
				pgOffsetNum.addClass("not-active");
			pgOffsetNum.attr("pgIdx", pgIdx);
			pgOffsetNum.attr("title", "Go to page " + pgIdx); 
			pgOffsetNum.text(pgIdx); 
			pgOffsetNum.show();
			if(pgIdx < totalSize)
				pgOffsetNum.next("span").show();
			else
				pgOffsetNum.next("span").hide();
		});
		tableRow.show();
		$(".pagelinks").show();
		spinner.stop();
		preload();
	};
	
	var preload = function() {
		var preloaded = preloadSize - 1;
		var haldPreloadSize = Math.floor(preloaded/2);
		var maxPgOffset = totalSize - 1;
		var startIdx = curPageIdx - haldPreloadSize > 0 ? curPageIdx - haldPreloadSize : 0;
		var endIdx = startIdx + preloaded > maxPgOffset ? maxPgOffset : startIdx + preloaded;
		var toRemove = [];
		$.each(disputePostMap, function(key, value) {
			if(key > endIdx || key < startIdx)
				toRemove.push(key);
		});
		for(var k in toRemove)
			delete disputePostMap[toRemove[k]];
		
		for(var i = startIdx; i <= endIdx; i++) {
			if(disputePostMap[i] === undefined)
				loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, i, pageSize, false);
		}
	};
	
	var updateCurateInfoCount = function(curateCount) {
		$btnMore = $("#btnMore");
		var handleCountingDiv = $("#handleCountingDiv");
		handleCountingDiv.empty();
		$.each(curateCount, function(key, val) {
			if(typeof key !== 'string')
				return true;
			if(key == "Sepatare Date") {
				lastHandled = val;
				return true;
			}
			if(key.indexOf("Revived Count") < 0 && key.indexOf("Modified") < 0 
					&& key.indexOf("Retag Count") < 0 && key.indexOf("Pool Size"))
				return true;
			var msgKey = $("<label>");
			msgKey.text(key + " : ");
			var msgVal = $("<label>");
			msgVal.text(val);
			handleCountingDiv.append(msgKey);
			handleCountingDiv.append(msgVal);
			if(key=="Pool Size" || key == "Retag Count"){
				handleCountingDiv.append($btnMore);
				$btnMore.show();
			}
			handleCountingDiv.append("<br>");
		});
	};
	
	var updateMainKeyIcon = function(elm, add) {
		if(add) {
			if(elm.find("#mainKey").length > 0)
				return;
			var mainKeyIco = $("<img id='mainKey' src='./../common/theme/backend/images/mainType.png' style='position: absolute; top: -50; right: 0;'/>");
			elm.append(mainKeyIco);
			var $this = mainKeyIco;
			mainKeyIco.singleAndDouble(function(event) {
				event.preventDefault();
				$this.parent().find(".circle").trigger("click");
			},function(event) {
				event.preventDefault();
				$this.parent().find(".circle").trigger("dblclick");
			});
		}
		else
			elm.find("#mainKey").remove();
	};
	
	var loadLocaleInfo = function (selRegion, onCompleted, onFailed) {
		var apiUrl = "./DisputePostSystem.action";
		var data = "getLocaleInfo&selRegion=" + selRegion + "&poolType=" + poolType + "&selCreatorType=" + selectedCreatorType;
		$.post(apiUrl, data, function(responseJson) {
			var availableCirclesDivs = $(".availableCircles");
			availableCirclesDivs.each( function( idx, availableCirclesDiv ) {
				$(availableCirclesDiv).empty();
			});
			selfieCirTypeId = null;
			revived = responseJson.revived;
			reviewed = responseJson.reviewed;
			preSelectQuality = responseJson.preSelectQuality;
			preSelectCircle = responseJson.preSelectCircle;
			
			var mselect = false;
			if(availableCirclesDivs.length > 0) {
				var tmselect = $(availableCirclesDivs[0]).attr("mselect");
				if(tmselect !== undefined)
					mselect = tmselect == "true";
			}
			var clickCircleHandler = function($this, parentDiv, postRow, value) {
				var exRescue = getRescueTask(parentDiv.attr("pId"));
				if($this.hasClass("cirSelected")) {
					$this.removeClass("cirSelected");
					var index = exRescue.descCirIds.indexOf(value.circleTypeId);
					if (index > -1)
						exRescue.descCirIds.splice(index, 1);
				}
				else {
					if(!mselect) {
						var selecteds = postRow.find(".cirSelected");
						$.each(selecteds, function(i, v) {
							var $thisSelected = $(v);
							$thisSelected.removeClass("cirSelected");
							var index = exRescue.descCirIds.indexOf($thisSelected.attr("cId"));
							if (index > -1)
								exRescue.descCirIds.splice(index, 1);
						});
					}
					exRescue.descCirIds.push(value.circleTypeId);
					if(exRescue.type == "UNDECIDED") {
						if(revived && !mselect) {
							postRow.find("#checkChangeKeyword").trigger("click");
						}
						else {
							postRow.find("#checkBoxCat").trigger("click");
						}
					}
					$this.addClass("cirSelected");
				}
				if(mselect) {
					updateMainKeyIcon(parentDiv, false);
					updateMainKeyIcon(parentDiv.find("[cid='" + exRescue.descCirIds[0] + "']").parent(), true);
				}
			};
			$.each(responseJson.circles, function(key, value) {
				if(value.defaultType == 'SELFIE')
					selfieCirTypeId = value.circleTypeId;
				availableCirclesDivs.each( function( idx, availableCirclesDiv ) {
					var cirImg = $("<img class='circle' style='max-height: 50px;max-width: 50px;' />");
					cirImg.attr("src", value.iconUrl);
					cirImg.attr("cId", value.circleTypeId);
					if(!mselect) {
						cirImg.click(function(event) {
							var $this = $(this);
							var parentDiv = $this.parent().parent();
							var postRow = $this.closest(".postRow");
							clickCircleHandler($this, parentDiv, postRow, value);
						});
					}
					else {
						var $this = cirImg;
						cirImg.singleAndDouble(function(event) {
							var parentDiv = $this.parent().parent();
							var postRow = $this.closest(".postRow");
							clickCircleHandler($this, parentDiv, postRow, value);
						},function(event){
							$this.addClass("cirSelected");
							var parentDiv = $this.parent().parent();
							var exRescue = getRescueTask(parentDiv.attr("pId"));
							var index = exRescue.descCirIds.indexOf(value.circleTypeId);
							if (index > -1)
								exRescue.descCirIds.splice(index, 1);
							exRescue.descCirIds.unshift(value.circleTypeId);
							updateMainKeyIcon(parentDiv, false);
							updateMainKeyIcon(parentDiv.find("[cid='" + exRescue.descCirIds[0] + "']").parent(), true);
						});
					}
					var tr = $("<span style='position: relative; padding-left: 7px !important;padding-right: 7px !important; min-height: 51px;min-width: 51px;max-height: 51px;max-width: 51px;text-align: center !important;'>");
					tr.append(cirImg);
					$(availableCirclesDiv).append(tr);
					if((key + 1) % 9 == 0)
						$(availableCirclesDiv).append("<br>");
				});
			});	
			
			var availableExtCirclesDivs = $(".extCirDiv");
			availableExtCirclesDivs.each( function( idx, availableCirclesDiv ) {
				$(availableExtCirclesDivs).empty();
			});
			if(responseJson.extraCircles !== undefined) {
				$.each(responseJson.extraCircles, function(key, value) {
					availableExtCirclesDivs.each( function( idx, availableExtCirclesDiv ) {
						var cirName = $("<input type='button' style='border-radius: 25px; background: #C9E7E7;' />");
						cirName.attr("cId", value.circleTypeId);
						cirName.val(value.circleName);
						cirName.click(function() {
							var $this = $(this);
							var parentDiv = $this.parent();
							var postRow = $this.closest(".postRow");
							clickCircleHandler($this, parentDiv, postRow, value);
						});
						$(availableExtCirclesDiv).append(cirName);
					});
				});
			}
			if(selfieCirTypeId == null) {
				tableRow.find("input[type='radio'][act='checkSelfieCircle']").each(function(){
					$(this).hide();
				});
			}
			else {
				$(this).show();
			}
			
			var postTagDivs = $(".postTagsDiv");	
			var newKeywordInput = $("<input id='newKeyword' type='text' maxlength='20' placeholder='New Tag' style='width:60%;'>");
			var addKeywordBtn = $("<input type='button' style='background: #4780ae !important; color: #FFFFFF;width:40px;' value='Add'>");
			var smartTagDiv = postTagDivs.smartTag(selectedRegion, "fPostTags", "postTags", "addPostTagDiv", newKeywordInput, addKeywordBtn);
			smartTagDiv.onKeywordClick(function(clikedKeyword, keyword, isSelected) {
				var postTagDiv = clikedKeyword.closest(".postTagsDiv");
				var exRescue = getRescueTask(postTagDiv.attr("pId"));
				if(!isSelected) {
					var idx = exRescue.keywords.indexOf(keyword);
					if(idx != -1) {
						exRescue.keywords.splice(idx, 1);
					}
				}
				else {
					if(exRescue.keywords === undefined)
						exRescue.keywords = [];
					exRescue.keywords.push(keyword);
					if(exRescue.type == "UNDECIDED") {
						if(revived && !mselect) {
							clikedKeyword.closest(".postRow").find("#checkChangeKeyword").trigger("click");
						}
						else {
							clikedKeyword.closest(".postRow").find("#checkBoxCat").trigger("click");
						}
					}
				}
			});
			
			smartTagDiv.onAddingKeyword(function() {
				spinner.spin($("#displayDiv")[0]);
			});
			smartTagDiv.onAddedKeyword(function() {
				spinner.stop();
			});
			smartTagDiv.onAddKeywordError(function(error) {
				alert("Failed to add keyword");
				spinner.stop();
			});
			
			smartTagDiv.init(responseJson.keywords, []);
			updateCurateInfoCount(responseJson);
			
			$unCuratedSummary = $("#unCuratedSummaryTable");
			var data = responseJson.unCuratedSummary;
			if(data!=null && data.length>0){
				$.each(data, function(i, item) {
					if(i<4) {
						$unCuratedSummary.append("<tr><td>"+(data[i].locale).substr(3)+":</td><td>"+data[i].count+"</td><td  style='min-width: 230px;'>("+data[i].oldestPost+")</td>" +
												"<td>"+(data[i+5].locale).substr(3)+":</td><td>"+data[i+5].count+"</td><td>("+data[i+5].oldestPost+")</td></tr>");
					} else if(i==4){
						$unCuratedSummary.append("<tr><td>"+(data[i].locale).substr(3)+":</td><td>"+data[i].count+"</td><td>("+data[i].oldestPost+")</td>+" +
												"<td></td><td></td><td></td>");
						return;
					}
				});
			}
			
			onCompleted();
		}).fail(function(e) {  	
			onFailed();
		});
	}
	
	var loadDisputePost = function (selfRegion, selCircleTypeId, selectedCreatorType, offset, pageSize, showNow) {
		var responseJson = null;
		$.each(disputePostMap, function(key, value) {
			if(key == offset) {
				responseJson = value;
				return false;
			}
		});
		if(disputePostMap[offset] === undefined) {
			var apiUrl = "./DisputePostSystem.action";
			var data = "poolType=" + poolType + "&selCircleTypeId=" + selCircleTypeId + "&selCreatorType=" + selectedCreatorType + "&getDisputeJson&selRegion=" + selfRegion + "&offset=" + offset * pageSize + "&pageSize=" + pageSize;
			if(resultTotalSize != null)
				data += "&totalSize=" + resultTotalSize;
			$.post(apiUrl, data, function(responseJson) {
				disputePostMap[offset] = responseJson;
				if(showNow)
					loadPostToTable(responseJson, offset);
			}).fail(function(e) {  	
				$(".pagelinks").hide();
				tableRow.hide();
				spinner.stop();
				$("#messageLbl").text("Error");
			});
		}
		else {
			if(showNow)
				loadPostToTable(responseJson, offset);
		}
	};
	
	var getUrlParameter = function(sParam) {
	    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
	        sURLVariables = sPageURL.split('&'),
	        sParameterName,
	        i;

	    for (i = 0; i < sURLVariables.length; i++) {
	        sParameterName = sURLVariables[i].split('=');

	        if (sParameterName[0] === sParam) {
	            return sParameterName[1] === undefined ? true : sParameterName[1];
	        }
	    }
	};
	
	var tmpPoolType = getUrlParameter('poolType');
	if(tmpPoolType != null)
		poolType = tmpPoolType;
	
	loadLocaleInfo(selectedRegion, function() {
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, 0, pageSize, true);
	}, null);
	
	$(".previousBtn").click(function() {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		spinner.spin($("#displayDiv")[0]);
		if(curPageIdx > 0)
			curPageIdx--;
		else {
			spinner.stop();
			return;
		}
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, curPageIdx, pageSize, true);
	});
	$("#jumpToBtn").click(function() {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		spinner.spin($("#displayDiv")[0]);
		var jumpToVal = $("#jumpToInput").val();
		var jumpTo;
		if(jumpToVal === undefined)
			jumpTo = 0;
		else
			jumpTo = parseInt(jumpToVal);
		if(jumpTo > totalSize)
			jumpTo = totalSize;
		if(jumpTo < 0)
			jumpTo = 1;
		curPageIdx = jumpTo - 1;
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, curPageIdx, pageSize, true);
	});
	$(".nextBtn").click(function() {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		spinner.spin($("#displayDiv")[0]);
		if(curPageIdx < totalSize)
			curPageIdx++;
		else {
			spinner.stop();
			return;
		}
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, curPageIdx, pageSize, true);
	});
	
	$(".pgNum").click(function(event) {
		if(event.preventDefault) 
			event.preventDefault();
		else
			event.returnValue = false;
		spinner.spin($("#displayDiv")[0]);
		var selPageOffset = $(this).attr("pgIdx");
		curPageIdx = parseInt(selPageOffset);
		if(curPageIdx < 1){
			curPageIdx = 1;
		}	
		else if(curPageIdx > totalSize) {
			curPageIdx = totalSize
		}
		curPageIdx -= 1;
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, curPageIdx, pageSize, true);
	});
	
	$("#selRegion").change(function() {
		window.location.href = "./DisputePostSystem.action?poolType=" + poolType + "&selRegion=" + $("#selRegion").val() + "&selCircleTypeId=" + selectedCircleTypeId + "&pageSize=" + $("#pageSizeSel option:selected").val();
		/*spinner.spin($("#displayDiv")[0]);
		isWarnedLinePostId = null;
		warnendDate = null;
		selectedRegion = $("#selRegion").val();
		toRescue = {};
		disputePostMap = {};
		curPageIdx = 0;
		resultTotalSize = null;
		totalSize = null;
		loadLocaleInfo(selectedRegion, function() {
			loadDisputePost(selectedRegion, selectedCircleTypeId, 0, pageSize, true);
		}, null);*/
		
	});
	
	$("#pageSizeSel").change(function() {
		disputePostMap = {};
		isWarnedLinePostId = null;
		warnendDate = null;
		toRescue = {};
		spinner.spin($("#displayDiv")[0]);
		curPageIdx = 0;
		resultTotalSize = null;
		totalSize = null;
		pageSize = $("#pageSizeSel option:selected").val();
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, 0, pageSize, true);
	});
	
	$("#selCircleTypeId").change(function() {
		spinner.spin($("#displayDiv")[0]);
		isWarnedLinePostId = null;
		warnendDate = null;
		selectedCircleTypeId = $("#selCircleTypeId").val();
		toRescue = {};
		disputePostMap = {};
		curPageIdx = 0;
		resultTotalSize = null;
		totalSize = null;
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, 0, pageSize, true);		
	});
	
	$("#selCreatorType").change(function() {
		spinner.spin($("#displayDiv")[0]);
		isWarnedLinePostId = null;
		warnendDate = null;
		selectedCreatorType = $("#selCreatorType").val();
		toRescue = {};
		disputePostMap = {};
		curPageIdx = 0;
		resultTotalSize = null;
		totalSize = null;
		loadDisputePost(selectedRegion, selectedCircleTypeId, selectedCreatorType, 0, pageSize, true);
	});
	
	$(".selPageOffset").keypress(function(e){
		if(e.keyCode==13) {
			$(this).parent().find(".navSelPage").click();
		}
    });
	
	$(".postPhoto").load(function() {
		var width = this.naturalWidth;
		var height = this.naturalHeight;
		$(this).parent().parent().parent().find("#resolution").text("(" + width + " * " + height + ")");
	});
	
	tableRow.find("img#attachmentImg").each(function(){
		var $this = $(this);
		$this.dblclick(function() {
			window.open('./queryPost.action?clpost&postId=' + $this.attr("pId"));
		});
	});
	
	var getRescueTask = function(postId) {
		var exRescue = toRescue[postId];
		if(exRescue == null) {
			exRescue = new Object();
			exRescue.postId = postId;
			exRescue.type = "UNDECIDED";
			exRescue.descCirIds = [];
			toRescue[postId] = exRescue;
		}
		return exRescue;
	};
	
	tableRow.find("input[type='radio'][act='checkUndecided']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			exRescue.type = "UNDECIDED";
			acDiv.css("pointer-events", "auto");
		});
	});
	
	tableRow.find("input[type='radio'][act='checkCategory']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			exRescue.type = "CAT";
			exRescue.hiInAl = true;
			acDiv.css("pointer-events", "auto");
		});
	});
	
	tableRow.find("input[type='radio'][act='checkCategoryTrend']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			exRescue.type = "CAT_TREND";
			exRescue.hiInAl = false;
			acDiv.css("pointer-events", "auto");
		});
	});
	
	tableRow.find("input[type='radio'][act='checkSelfieCircle']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			exRescue.type = "SEL_CIR";
			exRescue.descCirIds.push(selfieCirTypeId);
			acDiv.find(".cirSelected").removeClass("cirSelected");
			acDiv.find("img[cid='"+ selfieCirTypeId +"']").addClass("cirSelected");
			acDiv.css("pointer-events", "none");
		});
	});
	
	tableRow.find("input[type='radio'][act='checkAbandon']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			exRescue.type = "ABANDON";
			acDiv.css("pointer-events", "auto");
		});
	});
	
	tableRow.find("input[type='radio'][act='checkChangeKeyword']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			if(this.checked)
				exRescue.type = "CHANGE_KEYWORD";
			else
				exRescue.type = "UNDECIDED";
		});
	});
	
	tableRow.find("input[type='radio'][act='checkDel']").each(function(){
		var $this = $(this);
		var acDiv = $this.parent().parent().parent().parent().find(".availableCirDiv");
		$this.change(function() {
			var exRescue = getRescueTask($this.val());
			if(this.checked)
				exRescue.type = "REMOVE";
			else
				exRescue.type = "UNDECIDED";
		});
	});
	
	$(".rate").click(function() {
		var $this = $(this);
		var parent = $this.parent();
		var exRescue = getRescueTask(parent.val());
		exRescue.quality = $this.attr("value");
		parent.find(".rate.selected").removeClass("selected");
		$this.addClass("selected");
		if(exRescue.type == "UNDECIDED")
			parent.parent().find("#checkBoxCat").trigger("click");
	});
	
	$(".push").click(function() {
		var $this = $(this);
		var parent = $this.parent();
		var exRescue = getRescueTask(parent.val());
		if($this.hasClass("selected")) {
			$this.removeClass("selected");
			$this.attr("value", false);
		}
		else {
			$this.addClass("selected");
			$this.attr("value", true);
			if(exRescue.type == "UNDECIDED")
				parent.parent().find("#checkBoxCat").trigger("click");
		}
		exRescue.skip = $this.attr("value");
	});
	
	$(".nonPropagate").click(function(event) {
		event.stopPropagation();
	});
	
	$(".clickableTd").click(function() {
		$(this).find("#checkChangeKeyword").trigger("click");
	});
	
	$("#rescue").click(function() {
		if (poolType == 'TrendingTest')
			return;
		
		var apiUrl = "./DisputePostSystem.action?rescue";
		var rescueArray = [];
		$.each(toRescue, function(key, value) {
			rescueArray.push(value);
		});
		var data = "rescueObjs=" + encodeURIComponent(JSON.stringify(rescueArray));
		data += "&selRegion=" + $("#selRegion").val();
		data += "&poolType=" + poolType;
		data += "&selCircleTypeId=" + selectedCircleTypeId;
		$.post(apiUrl, data, function(responseJson) {
			if(responseJson == "Completed")
				window.location.href = "./DisputePostSystem.action?poolType=" + poolType + "&selRegion=" + $("#selRegion").val() + "&selCircleTypeId=" + selectedCircleTypeId + "&selCreatorType=" + selectedCreatorType + "&pageSize=" + $("#pageSizeSel option:selected").val();  		
		}).fail(function(e) {
			alert("Error");
		});
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
	
	var createrId;
	var reporterId;
	$(".authorName").click(function() {
		var $this = $(this);
		var $dialog = $("#authorDialog");
		$dialog.find("#authorAvatar").attr("src", $this.attr("avatar"));
		$dialog.find("#authorName").text($this.attr("uDisplyName"));
		$dialog.find("#authorId").text($this.attr("createrId"));
		createrId = $this.attr("createrId");
		reporterId = $this.attr("reporterId");
		$dialog.dialog("open");
	});
	
	$("#reportUser").click(function() {
		var data = "reporterId=" + reporterId + "&targetId="+ createrId + "&selReason=GRAPHIC";
		$.post("../user/reportedUser.action?reportUser", data, function(responseJson) {
			alert("Report user success");
         }).fail(function(e) {
        	 alert("Report user failed " + e.status + " " + e.statusText);
         });
		$("#authorDialog").dialog('close');
	});
});

