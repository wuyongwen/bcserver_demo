package com.cyberlink.cosmetic.modules.post.result;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PostAttachments implements Serializable {

    private static final long serialVersionUID = 5079183592369788087L;

    @JsonView(Views.Public.class)
    public Set<PostFile> files = new HashSet<PostFile>(0);
}
