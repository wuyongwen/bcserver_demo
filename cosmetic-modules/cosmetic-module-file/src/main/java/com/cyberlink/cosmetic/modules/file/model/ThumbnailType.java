package com.cyberlink.cosmetic.modules.file.model;

/**
 * The thumbnail dimension for different kinds of views
 */
public enum ThumbnailType {
    // (width, height, landscape strategy, portrait strategy, quality)
    /** List view */
    List(320, 320, ThumbnailStrategy.FixedWidth, ThumbnailStrategy.FixedWidth, 65),
    
    /** Detail view */
    Detail(800, 800, ThumbnailStrategy.FixedWidth, ThumbnailStrategy.FixedWidth, 65),
    
    /** Avatar view */
    Avatar(150, 150, ThumbnailStrategy.Strict, ThumbnailStrategy.Strict, 65),
    
    /** Keep the original size but reduce the quality to 65 */
    Quality65(null, null, null, null, 65);
    
    private final Integer width;
    private final Integer height;
    private final ThumbnailStrategy landscape;
    private final ThumbnailStrategy portrait;
    private final Integer quality;
    
    ThumbnailType(Integer width, Integer height, ThumbnailStrategy landscape, ThumbnailStrategy portrait, Integer quality) {
        this.width = width;
        this.height = height;
        this.landscape = landscape;
        this.portrait = portrait;
        this.quality = quality;
    }
    
    public Integer width() {
        return this.width;
    }
    
    public Integer height() {
        return this.height;
    }
    
    public ThumbnailStrategy landscape() {
        return this.landscape;
    }
    
    public ThumbnailStrategy portrait() {
        return this.portrait;
    }
    
    public Integer quality() {
        return this.quality;
    }
}
