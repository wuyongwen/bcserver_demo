package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.PostCurateKeyword;

public interface PostCurateKeywordDao extends GenericDao<PostCurateKeyword, Long>{
    
    PageResult<PostCurateKeyword> listByLocale(String locale, 
            Boolean withDefaultType, BlockLimit blockLimit);

    PostCurateKeyword findByKeyword(String locale, String keyword);
    
    List<PostCurateKeyword> listAllByLocale(String locale,
            Boolean withDefaultType);

    void updateFrequency(List<Long> keywordIds);
    
    Boolean batchCreate(List<PostCurateKeyword> list);

}
