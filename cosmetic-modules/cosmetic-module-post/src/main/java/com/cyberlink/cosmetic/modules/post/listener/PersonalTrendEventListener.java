package com.cyberlink.cosmetic.modules.post.listener;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.post.event.PersonalTrendEvent;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolInfo;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;
import com.cyberlink.cosmetic.modules.post.service.TrendingService;

public class PersonalTrendEventListener extends
        AbstractEventListener<PersonalTrendEvent> {

    private TrendingRepository trendingRepository;
    private TrendingService trendingService;
    
    public void setTrendingRepository(TrendingRepository trendingRepository) {
        this.trendingRepository = trendingRepository;
    }
    
    public void setTrendingService(TrendingService trendingService) {
        this.trendingService = trendingService;
    }
    
    private void loopAllType(PersonalTrendEvent event) {
        switch(event.getCmd()) {
        case md_pt : {
            TrendPoolInfo oSCInfo = trendingRepository.getTrendPoolInfo(TrendPoolType.SCat, 
                    event.getLoc(), event.getCk());
            if(oSCInfo == null)
                break;
            Double score = trendingRepository.getScore(TrendPoolType.SCat, event.getLoc(), event.getCk(), 
                    oSCInfo.getIdx(), event.getPi());
            trendingRepository.removeTrendPost(TrendPoolType.SCat, event.getLoc(), event.getCk(), 
                    oSCInfo.getIdx(), event.getPi());
            TrendPoolInfo oTCInfo = trendingRepository.getTrendPoolInfo(TrendPoolType.TCat, 
                    event.getLoc(), event.getCtid().toString());
            if(oTCInfo != null)
                trendingRepository.removeTrendPost(TrendPoolType.TCat, event.getLoc(), event.getCtid().toString(), 
                    oTCInfo.getIdx(), event.getPi());
            
            TrendPoolInfo nSGCInfo = trendingRepository.getTrendPoolInfo(TrendPoolType.SGenCat, 
                    event.getLoc(), event.getNck());
            if(nSGCInfo != null)
                trendingRepository.removeTrendPost(TrendPoolType.SGenCat, event.getLoc(), event.getNck(), 
                        nSGCInfo.getIdx(), event.getPi());
            
            if(score != null) {
                Map<Long, Double> val = new HashMap<Long, Double>();
                val.put(event.getPi(), score);
                
                TrendPoolInfo nSCInfo = trendingRepository.getTrendPoolInfo(TrendPoolType.SCat, 
                        event.getLoc(), event.getNck());
                trendingRepository.addTrendPost(TrendPoolType.SCat, event.getLoc(), event.getNck(), 
                        nSCInfo.getIdx(), val);
                
                TrendPoolInfo nTCInfo = trendingRepository.getTrendPoolInfo(TrendPoolType.TCat, 
                        event.getLoc(), event.getNctid().toString());
                if(nTCInfo != null)
                    trendingRepository.addTrendPost(TrendPoolType.TCat, event.getLoc(), event.getNctid().toString(), 
                            nTCInfo.getIdx(), val);
                
                Map<String, Map<Long, String>> allCircleMap = trendingService.getAllCircleNameMap();
                if(allCircleMap == null || !allCircleMap.containsKey(event.getLoc()))
                    break;
                Map<Long, String> circleTypeIds = allCircleMap.get(event.getLoc());
                if(circleTypeIds == null)
                    break;
                for(Long ctid : circleTypeIds.keySet()) {
                    if(ctid == null || ctid.equals(event.getNctid()))
                        continue;
                    TrendPoolInfo oSGCInfo = trendingRepository.getTrendPoolInfo(TrendPoolType.SGenCat, 
                            event.getLoc(), circleTypeIds.get(ctid));
                    if(oSGCInfo != null)
                        trendingRepository.addTrendPost(TrendPoolType.SGenCat, event.getLoc(), circleTypeIds.get(ctid), 
                                oSGCInfo.getIdx(), val);
                }
            }
            break;
        }
        case rm_pt : {
            for(TrendPoolType type : TrendPoolType.values()) {
                TrendPoolInfo info = trendingRepository.getTrendPoolInfo(type, event.getLoc(), event.getCk());
                if(info == null)
                    continue;
                String circleKey = type.getGroup() == 1 ? event.getCtid().toString() : event.getCk();
                trendingRepository.removeTrendPost(type, event.getLoc(), circleKey, 
                        info.getIdx(), event.getPi());
            }
        }   
        default:
            break;
        }
    }
    
    @Override
    public void onEvent(PersonalTrendEvent event) {
    if(!Constants.getPersonalTrendEnable())
        return;
    
    if(event == null)
        return;
    
    if(event.getPt() == null) {
        loopAllType(event);
        return;
    }
    
    TrendPoolType type = TrendPoolType.getFromShortForm(event.getPt());
    switch(event.getCmd()) {
    case ad_pt : {
        trendingRepository.addTrendPost(type, event.getLoc(), event.getCk(), event.getCidx(), event.getSvm());
        break;
    }
    case tr_pt : {
        trendingRepository.trimTrend(type, event.getLoc(), event.getCk(), event.getCidx(), event.getTsz());
        break;
    }
    case sw_pt : {
        trendingRepository.swapTrend(type, event.getLoc(), event.getCk(), event.getOidx(), event.getCidx());
        break;
    }
    case mv_cr : {
        TrendPoolInfo info = trendingRepository.getTrendPoolInfo(type, event.getLoc(), event.getCk());
        if(info == null || event.getNc() == null)
            break;
        info.setPoolCursor(event.getNc());
        trendingRepository.updateInfo(info);
        break;
    }
    case mr_pt : {
        trendingRepository.mergeTrend(type, event.getLoc(), event.getCk(), event.getOidx(), event.getSidx(), event.getCidx(), event.getNc());
        break;
    }
    default:
        break;
    }
    }

}
