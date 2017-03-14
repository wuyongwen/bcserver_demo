package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.List;

public class PostAttachments implements Serializable {

    private static final long serialVersionUID = 5079183592369788087L;

    public List<PostFile> files = null;
    public List<PostLook> looks = null;
}
