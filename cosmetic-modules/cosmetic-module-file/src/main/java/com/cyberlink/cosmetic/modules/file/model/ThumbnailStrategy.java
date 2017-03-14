package com.cyberlink.cosmetic.modules.file.model;

public enum ThumbnailStrategy {
    /** Maximum values of height and width given, aspect ratio preserved */
    Maximum,
    
    /** Minimum values of width and height given, aspect ratio preserved */
    Minimum,
    
    /** Width given, height automatically selected to preserve aspect ratio */
    FixedWidth,
    
    /** Height given, width automagically selected to preserve aspect ratio */
    FixedHeight,
    
    /** Width and height emphatically given, original aspect ratio ignored */ 
    Strict;
    
}
