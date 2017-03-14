CKEDITOR.plugins.add('youcammakeup',
{
	requires: 'menubutton',
    init: function (editor) {
    	var onClickFunc = function (hrefLink) {
    		return function() {
				var selection = editor.getSelection();
				var selectedText;
				
				if(editor.getSelection().getRanges().length > 0 && editor.getSelection().getRanges()[0].collapsed)
					selectedText = hrefLink;
				else {
					if (CKEDITOR.env.ie) {
						selection.unlock(true);
						selectedText = selection.getNative().createRange().text;
					} else {
						selectedText = selection.getNative();
					}
				}
				
    			editor.insertHtml("<a href=\""+ hrefLink + "\" target=\"_blank\">" + selectedText + "</a>");
    		};
    	}
    	var strings = [];
		strings.push(['toYMKAppStore', 'To Store', 'https://itunes.apple.com/us/app/youcam-makeup/id863844475']);
		strings.push(['toLooks', 'To Looks', 'ymk://extra/LOOKS']);
		strings.push(['toEyeShadow', 'To Eye Shadow', 'ymk://extra/EYE_SHADOW']);
		strings.push(['toEyeLiner', 'To Eye Liner', 'ymk://extra/EYE_LINES']);
		strings.push(['toEyeLash', 'To Eye Lash', 'ymk://extra/EYE_LASHES']);
		strings.push(['toMakeupTips', 'To Makeup Tips', 'ymk://makeup_tips']);
		
		var items = {};
		for (i = 0; i < strings.length; i++) {
			var item = strings[i];
			var hrefLink = item[2];
			items[item[0]] = {
                    label: item[1],
                    group: 'cyberlink_product_group',
                	onClick: onClickFunc(item[2])
				}
		}

		editor.addMenuGroup('cyberlink_product_group');
		editor.addMenuItems( items );
		editor.ui.add( 'youcammakeup', CKEDITOR.UI_MENUBUTTON, {
            label: 'Youcam Makeup',
            icon: this.path + '/images/youcammakeup.png',
            onMenu: function() {
            	var active = {};
                for ( var p in items )
                    active[ p ] = CKEDITOR.TRISTATE_OFF;

                return active;
            }
        } );
    }
})