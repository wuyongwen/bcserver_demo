(function (factory) {
    factory(jQuery);
})(function ($) {

	"use strict";
	var availableKeyword = [],
	kwAutoCompleteList = [],
	toShowKeyword = [],
	toIconKeyword = [],
	preSelectList = [];
	var kwRegion, postTagDivs, fPostTagsClass, postTagsClass, addPostTagDiv, newKeywordInput, addKeywordBtn;
	var maxShowKW = 80;
	var keywordClicked = null, newKeywordAdding = null, newKeywordAdded = null, newKeywordAddError = null;
	
	var addKeyword = function(postTagDiv, container, fContainer, keyword, position) {
		var tagName;
		var cContainer;
		if(keyword.imgUrl === undefined) {
			tagName = $("<input type='button' style='border-radius: 25px; background: #D8D8D8;' />");
			cContainer = container;
		}
		else {
			tagName = $("<img style='max-height: 50px;max-width: 50px' />");
			tagName.attr("src", keyword.imgUrl);
			var td = $("<span style='padding-left: 7px !important; padding-right: 7px !important; min-height: 51px;min-width: 51px;max-height: 51px;max-width: 51px;text-align: center !important;'>");
			fContainer.append(td);
			cContainer = td; 
		}
		
		var children = cContainer.children();
		tagName.attr("kid", keyword.id);
		tagName.val(keyword.word);
		tagName.click(function() {
			var $this = $(this);
			var curIsSelected = $this.hasClass("selected");
			if(keywordClicked != null) {
				keywordClicked($this, keyword, !curIsSelected);
			}
			if(curIsSelected)
				$this.removeClass("selected");
			else
				$this.addClass("selected");
		});
		
		if(preSelectList.indexOf(keyword.word) >= 0)
			tagName.trigger("click");
		
		if(position == null) {
			cContainer.append(tagName);
		}
		else {
			var afterChild = children.get(position);
			if(afterChild == null)
				cContainer.append(tagName);
			else
				tagName.insertBefore(afterChild);	
		}
		
	};

	var updateKeyword = function(availableKeyword, toIconKeyword, toShowKeyword, kwAutoCompleteList) {	
		postTagDivs.each( function( idx, postTagDiv ) {
			var $postTagDiv = $(postTagDiv);
			var fContainer = $postTagDiv.find("." + fPostTagsClass);
			var container = $postTagDiv.find("." + postTagsClass);
			var aContainer = $postTagDiv.find("." + addPostTagDiv);
			fContainer.empty();
			container.empty();
			aContainer.empty();
			$.each(toShowKeyword, function(idx, keyword) {
				addKeyword($postTagDiv, container, fContainer, keyword, null);
			});
			$.each(toIconKeyword, function(idx, keyword) {
				addKeyword($postTagDiv, container, fContainer, keyword, null);
			});
			var nNewKeywordInput = newKeywordInput.clone();
			var nAddKeywordBtn = addKeywordBtn.clone();
			nNewKeywordInput.autocomplete({
				source: kwAutoCompleteList,
				select: function(e, ui) {
					e.preventDefault();
					nNewKeywordInput.val(ui.item.value);
					nAddKeywordBtn.trigger("click");
				}
			});
			nNewKeywordInput.on( "keydown", function(event) {
				if(event.keyCode == 13) {
					nNewKeywordInput.autocomplete( "close" );
					nAddKeywordBtn.trigger("click");
				}
			});
			nAddKeywordBtn.click(function() {
				var parent = $(this).parent();
				var newKeyword = nNewKeywordInput.val();
				if(newKeyword.length <= 0)
					return;
				
				var showAddedKeyword = function(newKeywordObj, needAdd, error) {
					if(error != null) {
						if(newKeywordAddError != null)
							newKeywordAddError(error);
						return;
					}
					
					if(needAdd) {
						addKeyword($postTagDiv, container, fContainer, newKeywordObj, 0);
					}
					var addNewKeyword = $postTagDiv.find("input[kid='" + newKeywordObj.id + "']");
					if(addNewKeyword !== undefined) {
						if(!addNewKeyword.hasClass("selected"))
							addNewKeyword.trigger("click");
						var slwp = addNewKeyword.parent();
						slwp.animate({scrollTop: addNewKeyword.position().top - slwp.position().top}, 'fast');
					}
					nNewKeywordInput.val("");
					if(newKeywordAdded != null)
						newKeywordAdded();
				};
				
				var existKeyword = null;
				availableKeyword.forEach(function(v) {
					if(v.word == newKeyword) {
						existKeyword = v;
						return false;
					}
				});
				if(existKeyword != null) {
					showAddedKeyword(existKeyword, $postTagDiv.find("input[kid='" + existKeyword.id + "']").length <= 0, null);
					return;
				}
				
				var apiUrl = "./DisputePostSystem.action";
				var data = "addKeyword&selRegion=" + kwRegion + "&keyword=" + encodeURIComponent(newKeyword);
				if(newKeywordAdding != null)
					newKeywordAdding();
				$.post(apiUrl, data, function(responseJson) {
					var createdKeywordId = responseJson.id;
					var createdKeyword = responseJson.keyword;
					var newKeywordObj = {"id":createdKeywordId, "word":createdKeyword};
					var isNewKeyword = true;
					$.each(availableKeyword, function(idx, val) {
						if(val.id != newKeywordObj.id)
							return true;
						isNewKeyword = false;
						return false;
					});
					if(isNewKeyword) {
						availableKeyword.push(newKeywordObj);
						kwAutoCompleteList.push(newKeywordObj.word);
						kwAutoCompleteList = kwAutoCompleteList.sort(function (a, b) {
							return a.localeCompare( b );
						});	
					}
					
					showAddedKeyword(newKeywordObj, isNewKeyword, null);
				}).fail(function(e) {  	
					showAddedKeyword(null, false, "Failed to add keyword");
				});
			});
			aContainer.append(nNewKeywordInput);
			aContainer.append(nAddKeywordBtn);
		});
	};

	// Constructor
	var SmartTag = function (inKwRegion, inPostTagDivs, inFPostTagsClass, inPostTagsClass, inAddPostTagDiv, inNewKeywordInput, inAddKeywordBtn) {
		kwRegion = inKwRegion;
		postTagDivs = inPostTagDivs;
		fPostTagsClass = inFPostTagsClass;
		postTagsClass = inPostTagsClass;
		addPostTagDiv = inAddPostTagDiv;
		newKeywordInput = inNewKeywordInput;
		addKeywordBtn = inAddKeywordBtn;
	};

	SmartTag.prototype = {
		constructor: SmartTag,

		init: function (keywordJson, inPreSelectList) {	
			availableKeyword = [];
			kwAutoCompleteList = [];
			toShowKeyword = [];
			toIconKeyword = [];
			if(inPreSelectList !== undefined)
				preSelectList = inPreSelectList;
			var preferKWList = [];
			$.each(keywordJson, function(key, value) {
				if(value.imgUrl === undefined) {
					kwAutoCompleteList.push(value.keyword);
					var toAdd = {"id":value.id,"word":value.keyword};
					availableKeyword.push(toAdd);
					if(preSelectList.indexOf(value.keyword) < 0) {
						if(toShowKeyword.length < maxShowKW - preSelectList.length)
							toShowKeyword.push(toAdd);
					}
					else
						preferKWList.push(toAdd);
				}
				else {
					var toAdd = {"id":value.id,"word":value.keyword, "imgUrl":value.imgUrl};
					toIconKeyword.push(toAdd);
					availableKeyword.push(toAdd);
				}
			});
			toShowKeyword = $.merge(toShowKeyword, preferKWList);
			toShowKeyword = toShowKeyword.sort(function (a, b) {
				return a.word.localeCompare( b.word );
			});
			kwAutoCompleteList = kwAutoCompleteList.sort(function (a, b) {
				return a.localeCompare( b );
			});
			updateKeyword(availableKeyword, toIconKeyword, toShowKeyword, kwAutoCompleteList);
		},
		setMaxShowKW : function(inMaxShowKW) {
			maxShowKW = inMaxShowKW;
		},
		onKeywordClick : function(onKeywordClick) {
			keywordClicked = onKeywordClick;
		},
		onAddingKeyword: function(newKeywordAdding) {
			newKeywordAdding = newKeywordAdding;
		},
		onAddedKeyword: function(newKeywordAdded) {
			newKeywordAdded = newKeywordAdded;
		},
		onAddKeywordError: function(newKeywordAddError) {
			newKeywordAddError = newKeywordAddError;
		}
	};
	$.fn.smartTag = function (locale, fPostTagsClass, postTagsClass, addPostTagDiv, newKeywordInput, addKeywordBtn) {
		return new SmartTag(locale, this, fPostTagsClass, postTagsClass, addPostTagDiv, newKeywordInput, addKeywordBtn);
	};
	$.fn.smartTag.Constructor = SmartTag;

});
