package com.cyberlink.cosmetic.modules.file.model;

public enum FileType {
    // (isImage, isSupportResize, thumbnailTypes)
    Photo(true, true, Constants.imageThumbnails),
    BeforeLook(true, true, Constants.imageThumbnails),
    AfterLook(true, true, Constants.imageThumbnails),
    LookEffect(false, false, null),
    Avatar(true, true, Constants.avatarThumbnails),
    DefaultUserCover(true, true, Constants.imageThumbnails),
    DefaultCLCover(true, true, Constants.imageThumbnails),
    PostCover(true, true, Constants.imageThumbnails),
    PostCoverOri(true, true, Constants.imageThumbnails),
    Video(false, true, Constants.imageThumbnails),
    Raw(true, true, Constants.imageThumbnails);

    private final Boolean isImage;
    private final Boolean isSupportResize;
    private final ThumbnailType[] thumbnailTypes;

    FileType(Boolean isImage, Boolean isSupportResize, ThumbnailType[] thumbnailTypes) {
        this.isImage = isImage;
        this.isSupportResize = isSupportResize;
        this.thumbnailTypes = thumbnailTypes;
    }
    
    public Boolean isImage() {
        return this.isImage;
    }
    
    public Boolean getIsImage() {
        return this.isImage;
    }

    public Boolean isSupportResize() {
        return this.isSupportResize;
    }
    
    public ThumbnailType[] thumbnailTypes() {
        return this.thumbnailTypes;
    }
    

    private static class Constants {
        public static final ThumbnailType[] imageThumbnails = {ThumbnailType.List, ThumbnailType.Detail}; 
        public static final ThumbnailType[] avatarThumbnails = {ThumbnailType.Detail, ThumbnailType.Avatar};
    }
}