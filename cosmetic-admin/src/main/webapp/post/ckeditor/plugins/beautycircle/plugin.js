CKEDITOR.plugins.add('beautycircle',
{
	requires: 'menubutton',
    init: function (editor) {
    	var strings = [];
		strings.push(['Post', 'Post', 'Post']);
		strings.push(['User', 'User', 'User']);
		strings.push(['Product', 'Product', 'Product']);
		var thisPlugin = this;
		var tagType = 'Post';
		var requestUrl = "./CreatePost.action?listAttachablePost";
		var selectedPostId = {};
		var items = {
                post: {
                    label: 'Post',
                    group: 'cyberlink_product_group',
                	onClick: function() {
                		tagType = 'Post';
                		requestUrl = "./CreatePost.action?listAttachablePost";
						editor.execCommand('beautycircle_tag');
					},
                },
                user: {
                    label: 'User',
                    group: 'cyberlink_product_group',
                    onClick: function() {
                    	tagType = 'User';
                    	requestUrl = "../user/selectUser.action";
                    	editor.execCommand( 'beautycircle_tag');
					}
                }
            };

		editor.addMenuGroup('cyberlink_product_group');
		editor.addMenuItems( items );
		editor.ui.add( 'beautycircle', CKEDITOR.UI_MENUBUTTON, {
            label: 'Beauty Circle',
            icon: this.path + '/images/beautycircle.png',
            onMenu: function() {
            	var active = {};
                for ( var p in items )
                    active[ p ] = CKEDITOR.TRISTATE_OFF;

                return active;
            }
        } );
        editor.addCommand('beautycircle_tag', new CKEDITOR.dialogCommand('beautycircle_tag'));
        CKEDITOR.dialog.add('beautycircle_tag', function (editor) {    
        	if(tagType == 'Post')
        		requestUrl = "./CreatePost.action?listAttachablePost";
        	else if(tagType == 'User')
        		requestUrl = "../user/selectUser.action";
        	
        	return {
                title: 'Insert BeautyCircle',
                width: 400,
                height: 400,
                contents: [              
                    {
                        id: 'beautycircle',
                        label: 'beautycircle',
                        title: 'beautycircle',
                        elements:
                            [
	                            {
	            					type : 'html',
	            					html : '<div id="availableResult" style="height:400px;width:400px"><iframe id="availableSelection" style="overflow:scroll; height:400px;width:400px"></iframe></div>'
	            				}
                            ]
                    }
                    ],
                onShow: function () {
                	$("#availableSelection").load(function() {
                		var checkboxChoices = $(this).contents().find("input[name=checkboxChoices]");
                		checkboxChoices.change(function(){
   	   		        		if(this.checked) 
   	   		        			selectedPostId[$(this).val()] = $(this).attr('text');
   	   		        		else
   	   		        			delete selectedPostId[$(this).val()];
   	   		             });
                		checkboxChoices.each(function() {
      		        		if(selectedPostId.hasOwnProperty($(this).val()) > 0) {
      		        			$(this).prop( "checked", true );
      		        		}
      		        	});
                	});
                	$("#availableSelection").attr("src", requestUrl);
                },
                onOk: function () {   
                	var count = 0;
            		for (var i in selectedPostId)
    				{
            			if(count != 0)
            				editor.insertHtml(";");
            			editor.insertHtml("<a href=\"ybc://"+ tagType + "/" + i + "\" target=\"_blank\">" + selectedPostId[i] + "</a>");
            			count++;
    				}
            		selectedPostId = {};
                }
           };  
       });
    }
})