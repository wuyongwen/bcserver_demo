/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	
	// %REMOVE_START%
	// The configuration options below are needed when running CKEditor from source files.
	config.plugins = 'dialogui,panelbutton,colorbutton,listblock,richcombo,font,format,sourcearea,beautycircle,youcamperfect,youcammakeup,dialog,about,basicstyles,clipboard,button,toolbar,enterkey,entities,floatingspace,wysiwygarea,indent,indentlist,fakeobjects,link,list,undo';
	config.skin = 'moono';
	// %REMOVE_END%

	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config

	config.toolbar = [
        [ 'Link', 'Unlink'],
        [ 'Bold', 'Italic', 'Format', 'TextColor'],
        ['Source'],
        '/',
        [ 'beautycircle', 'youcamperfect', 'youcammakeup'],
    ];

	// The default plugins included in the basic setup define some buttons that
	// are not needed in a basic editor. They are removed here.
	config.removeButtons = 'Cut,Copy,Paste,Undo,Redo,Anchor,Underline,Strike,Subscript,Superscript,Font,FontSize,BGColor';

	// Dialog windows are also simplified.
	config.removeDialogTabs = 'link:advanced;link:target';
	config.autoParagraph = false;
	config.enterMode = CKEDITOR.ENTER_BR;
};
