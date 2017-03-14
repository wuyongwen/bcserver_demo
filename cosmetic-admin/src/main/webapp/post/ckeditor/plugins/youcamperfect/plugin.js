CKEDITOR.plugins.add('youcamperfect',
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
		strings.push(['toYCPAppStore', 'To Store', 'https://itunes.apple.com/us/app/youcam-perfect/id768469908']);
		strings.push(['toTemplate', 'To Template', 'ycp://extra/']);
		strings.push(['toCollage', 'To Collage', 'ycp://extra/collages']);
		strings.push(['toClassicCollage', 'To Classic Collage', 'ycp://extra/collages/classic']);
		strings.push(['toFrame', 'To Frame', 'ycp://extra/frames']);
		strings.push(['toScene', 'To Scene', 'ycp://extra/imagechefs']);
		strings.push(['toEffects', 'To Effects', 'ycp://extra/presets']);
		strings.push(['toTextBubble', 'To Text Bubble', 'ycp://extra/bubbles']);
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
		editor.ui.add( 'youcamperfect', CKEDITOR.UI_MENUBUTTON, {
            label: 'Youcam Perfect',
            icon: this.path + '/images/youcamperfect.png',
            onMenu: function() {
            	var active = {};
                for ( var p in items )
                    active[ p ] = CKEDITOR.TRISTATE_OFF;

                return active;
            }
        } );
    }
})