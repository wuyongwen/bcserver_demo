package com.cyberlink.cosmetic.modules.post.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.modules.post.repository.TrendingCacheRepository;

public class TrendPool {

    public interface CommittedCallback {
        
        public void committing(final TrendPoolType type, final String locale, final String circleKey, 
                final Map<Long, Double> val);
        
        public void committed(final TrendPoolType type, final String locale, final String circleKey);

        public Long getMaxPoolSize(final TrendPoolType type, final String locale,
                final String circleKey, final Long currentPoolSize) ;
        
        public Long getScorePriority(final TrendPoolType type, final String locale, final String circleKey);
        
    }
    
    public interface BacthAdd {
        public void begin(final AddWorker worker);
    }
    
    public abstract class JobMap {
        public abstract void addTask(Long postId, String locale, Long circleTypeId, Long bonus,
                Boolean hideInAll, Long pop, Long timestamp);
    }
    
    public abstract class AddWorker {
        public abstract void add(Long postId, String locale, Long circleTypeId,
                Long bonus, Boolean hideInAll, Long pop, Long timestamp);
    }
    
    public class AddTrendWorker extends AddWorker {
        
        private List<TrendMap> jobMap = new ArrayList<TrendMap>();
        
        public AddTrendWorker(TrendingCacheRepository trendingCacheRepository,
                Map<String, Map<Long, String>> circleNameMap, List<TrendPoolType> mgrTypes) {
            for(TrendPoolType tt : mgrTypes) {
                switch(tt) {
                case TGen : {
                    jobMap.add(new TGTrendMap(trendingCacheRepository, circleNameMap));
                    break;
                }
                case TCat : {
                    jobMap.add(new TCTrendMap(trendingCacheRepository, circleNameMap));
                    break;
                }
                case SGen : {
                    jobMap.add(new SGTrendMap(trendingCacheRepository, circleNameMap));
                    break;
                }
                case SGenCat : {
                    jobMap.add(new SGCatTrendMap(trendingCacheRepository, circleNameMap));
                    break;
                }
                case SCat : {
                    jobMap.add(new SCTrendMap(trendingCacheRepository, circleNameMap));
                    break;
                }
                default:
                    break;
                }
            }
        }
        
        @Override
        public void add(Long postId, String locale, Long circleTypeId,
                Long bonus, Boolean hideInAll, Long pop, Long timestamp) {
            for(TrendMap jm : jobMap)
                jm.addTask(postId, locale, circleTypeId, bonus, hideInAll, pop, timestamp);
        }
        
        public void committed(final CommittedCallback ccb) {
            for(TrendMap jm : jobMap)
                jm.committed(ccb);
        }
        
        public void commitAll() {
            for(TrendMap jm : jobMap)
                jm.commitAll();
        }
        
        public void committing(final CommittedCallback ccb) {
            for(TrendMap jm : jobMap)
                jm.committing(ccb);
        }
        
        private abstract class TrendMap extends JobMap {
            protected Map<String, Map<Long, Map<Long, Double>>> tasks = new HashMap<String, Map<Long, Map<Long, Double>>>();
            protected Integer BATCH_UPDATE_SIZE = 50;
            private Set<String> committedKey = new HashSet<String>();
            final protected Map<String, Map<Long, String>> circleNameMap;
            final protected TrendPoolType tpType;
            protected TrendingCacheRepository trendingCacheRepository;
            
            public TrendMap(TrendPoolType tpType, Map<String, Map<Long, String>> circleNameMap) {
                this.circleNameMap = circleNameMap;
                this.tpType = tpType;
                tasks.put(null, new HashMap<Long, Map<Long, Double>>());
                for(String locale : circleNameMap.keySet()) {
                    if(!tasks.containsKey(locale))
                        tasks.put(locale, new HashMap<Long, Map<Long, Double>>());
                    Map<Long, Map<Long, Double>> cLoc = tasks.get(locale);
                    for(Long ctid : circleNameMap.get(locale).keySet()) {
                        if(cLoc.containsKey(ctid))
                            continue;
                        cLoc.put(ctid, new HashMap<Long, Double>());
                    }
                }
                for(Map<Long, Map<Long, Double>> k : tasks.values())
                    k.put(null, new HashMap<Long, Double>());
            }
            
            @Override
            public void addTask(Long postId, String locale, Long circleTypeId,
                    Long bonus, Boolean hideInAll, Long pop, Long timestamp) {
                Triple<String, Long, Double> toAddstoAddPost = toAdd(locale, circleTypeId, bonus, hideInAll,
                        pop, timestamp);
                if(toAddstoAddPost == null)
                    return;
                
                add(postId, toAddstoAddPost.getLeft(), toAddstoAddPost.getMiddle(), toAddstoAddPost.getRight());
            }
            
            private void add(Long postId, String locale, Long circleTypeId, Double score) {
                if(!tasks.containsKey(locale))
                    return;
                
                Map<Long, Map<Long, Double>> cloc = tasks.get(locale);
                if(!cloc.containsKey(circleTypeId))
                    return;
                Map<Long, Double> t = cloc.get(circleTypeId);
                t.put(postId, score);
                if(t.keySet().size() >= BATCH_UPDATE_SIZE) {
                    commit(locale, circleTypeId, t);
                    t.clear();
                }
            }
            
            public void commitAll() {
                for(String locale : tasks.keySet()) {
                    Map<Long, Map<Long, Double>> k = tasks.get(locale);
                    for(Long circleTypeId : k.keySet()) {
                        Map<Long, Double> t = k.get(circleTypeId);
                        if(t.keySet().size() <= 0)
                            continue;
                        commit(locale, circleTypeId, t);
                        t.clear();
                    }
                }
            }
            
            protected void addCommittedKey(String locale, String circleKey) {
                String key = locale == null ? "null:" : locale + ":";
                key += circleKey == null ? "null:" : circleKey;
                committedKey.add(key);
            }
            
            public void committing(final CommittedCallback ccb) {
                for(String s : committedKey) {
                    if(s == null)
                        return;
                    String [] toks = s.split(":");
                    if(toks.length < 2)
                        continue;
                    String locale = null;
                    if(toks[0].length() > 0)
                        locale = toks[0];
                    String circleKey = null;
                    if(toks[1].length() > 0)
                        circleKey = toks[1];
                    committing(locale, circleKey, ccb);
                }
            }
            
            public void committed(final CommittedCallback ccb) {
                for(String s : committedKey) {
                    if(s == null)
                        return;
                    String [] toks = s.split(":");
                    if(toks.length < 2)
                        continue;
                    String locale = null;
                    if(toks[0].length() > 0)
                        locale = toks[0];
                    String circleKey = null;
                    if(toks[1].length() > 0)
                        circleKey = toks[1];
                    trendingCacheRepository.deleteTrend(tpType, locale, circleKey);
                    ccb.committed(tpType, locale, circleKey);
                }
            }
            
            public abstract Triple<String, Long, Double> toAdd(final String locale, final Long circleTypeId, final Long bonus,
                    final Boolean hideInAll, final Long pop, final Long timestamp); 
            protected abstract void commit(final String locale, final Long circleTypeId, final Map<Long, Double> vals);
            
            protected abstract void committing(final String locale, final String circleKey, final CommittedCallback ccb);
        }
        
        private class TGTrendMap extends TrendMap {
            
            public TGTrendMap(TrendingCacheRepository trendingCacheRepository, 
                    Map<String, Map<Long, String>> circleNameMap) {
                this(TrendPoolType.TGen, trendingCacheRepository, circleNameMap);
            }

            protected TGTrendMap(TrendPoolType tpType, TrendingCacheRepository trendingCacheRepository, 
                    Map<String, Map<Long, String>> circleNameMap) {
                super(tpType, circleNameMap);
                this.trendingCacheRepository = trendingCacheRepository;
            }
            
            @Override
            public Triple<String, Long, Double> toAdd(final String locale,
                    final Long circleTypeId, final Long bonus, final Boolean hideInAll, final Long pop, final Long timestamp) {
                if(!hideInAll)
                    return Triple.of(locale, (Long)null,  Double.valueOf(timestamp));
                return null;
            }

            @Override
            protected void commit(String locale, Long circleTypeId,
                    Map<Long, Double> vals) {
                String c = circleTypeId == null ? null : circleTypeId.toString();
                trendingCacheRepository.addTrendPost(tpType, locale, c, vals);
                addCommittedKey(locale, c);
            }

            @Override
            public void committing(final String locale, final String circleKey, final CommittedCallback ccb) {
                Long currentPoolSize = trendingCacheRepository.getTrendPoolSize(tpType, locale, circleKey);
                Long maxAllowPoolSize = ccb.getMaxPoolSize(tpType, locale, circleKey, currentPoolSize);
                if(maxAllowPoolSize == null)
                    return;
                if(currentPoolSize > maxAllowPoolSize) {
                    trendingCacheRepository.trimTrend(tpType, locale, circleKey, maxAllowPoolSize);
                    currentPoolSize = maxAllowPoolSize;
                }
                
                Long offset = 0L;
                Long limit = Long.valueOf(BATCH_UPDATE_SIZE);
                do {
                    Map<Long, Double> values = new HashMap<Long, Double>();
                    trendingCacheRepository.getTrendListWithScore(tpType, locale, circleKey, offset, 
                            limit, false, values);
                    ccb.committing(tpType, locale, circleKey, values);
                    if(offset >= currentPoolSize)
                        break;
                    offset += limit;
                } while(offset < currentPoolSize);
            }
        }
        
        private class TCTrendMap extends TGTrendMap {
            public TCTrendMap(TrendingCacheRepository trendingCacheRepository,
                    Map<String, Map<Long, String>> circleNameMap) {
                super(TrendPoolType.TCat, trendingCacheRepository, circleNameMap);
            }
            
            @Override
            public Triple<String, Long, Double> toAdd(final String locale, final Long circleTypeId, 
                    final Long bonus, final Boolean hideInAll, final Long pop, final Long timestamp) {
                if(circleTypeId == null)
                    return null;
                return Triple.of(locale, circleTypeId,  Double.valueOf(timestamp));
            }
        }
        
        private class SGTrendMap extends TrendMap  {
            
            public SGTrendMap(TrendingCacheRepository trendingCacheRepository, 
                    Map<String, Map<Long, String>> circleNameMap) {
                this(TrendPoolType.SGen, trendingCacheRepository, circleNameMap);
            }
            
            protected SGTrendMap(TrendPoolType tpType, TrendingCacheRepository trendingCacheRepository, 
                    Map<String, Map<Long, String>> circleNameMap) {
                super(tpType, circleNameMap);
                this.trendingCacheRepository = trendingCacheRepository;
            }
            
            @Override
            public Triple<String, Long, Double> toAdd(final String locale, final Long circleTypeId, 
                    final Long bonus, final Boolean hideInAll, final Long pop, final Long timestamp) {
                if(!bonus.equals(200L) || hideInAll)
                    return null;
                return Triple.of(locale, circleTypeId,  Double.valueOf(pop));
            }
            
            @Override
            protected void commit(final String locale, final Long circleTypeId,
                    final Map<Long, Double> vals) {
                trendingCacheRepository.addTrendPost(TrendPoolType.SGen, locale, null, vals);
                addCommittedKey(locale, null);                
            }
            
            @Override
            public void committing(final String locale, final String circleKey,
                    final CommittedCallback ccb) {
                Long currentPoolSize = trendingCacheRepository.getTrendPoolSize(tpType, locale, circleKey);
                Long maxAllowPoolSize = ccb.getMaxPoolSize(tpType, locale, circleKey, currentPoolSize);
                Long priorityScore = ccb.getScorePriority(tpType, locale, circleKey);
                if(maxAllowPoolSize == null)
                    return;
                if(currentPoolSize > maxAllowPoolSize) {
                    trendingCacheRepository.trimTrend(tpType, locale, circleKey, maxAllowPoolSize);
                    currentPoolSize = maxAllowPoolSize;
                }
                
                Long offset = 0L;
                Long limit = Long.valueOf(BATCH_UPDATE_SIZE);
                do {
                    Map<Long, Double> values = new HashMap<Long, Double>();
                    trendingCacheRepository.getTrendListWithScore(tpType, locale, circleKey, offset, 
                            limit, false, values);
                    Map<Long, Double> adjustedScore = adjustScore(priorityScore, values);
                    if(adjustedScore == null)
                        ccb.committing(tpType, locale, circleKey, values);
                    else
                        ccb.committing(tpType, locale, circleKey, adjustedScore);
                    if(offset >= currentPoolSize)
                        break;
                    offset += limit;
                } while(offset < currentPoolSize);
            }
            
            protected Map<Long, Double> adjustScore(Long curIdx, Map<Long, Double> vals) {
                if(curIdx == null)
                    return null;
                Map<Long, Double> adjustedVals= new HashMap<Long, Double>();
                for(Long id : vals.keySet()) {
                    Long s = vals.get(id).longValue();
                    s = s % 10000000000L;
                    s += (curIdx * 10000000000L);
                    adjustedVals.put(id, s.doubleValue());
                }   
                return adjustedVals;
            }
        }
        
        private class SGCatTrendMap extends SGTrendMap  {
            
            public SGCatTrendMap(TrendingCacheRepository trendingCacheRepository, 
                    Map<String, Map<Long, String>> circleNameMap) {
                super(TrendPoolType.SGenCat, trendingCacheRepository, circleNameMap);                
            }
            
            @Override
            protected void commit(final String locale, final Long circleTypeId,
                    final Map<Long, Double> vals) {
                if(!circleNameMap.containsKey(locale))
                    return;
                
                Map<Long, String> cTypeIdMap = circleNameMap.get(locale);
                for(Long ctid : cTypeIdMap.keySet()) {
                    if(ctid.equals(circleTypeId))
                        continue;
                    String ctg = cTypeIdMap.get(ctid);
                    trendingCacheRepository.addTrendPost(TrendPoolType.SGenCat, locale, ctg, vals);
                    addCommittedKey(locale, ctg);     
                }
            }
        }
        
        private class SCTrendMap extends SGTrendMap  {
            
            public SCTrendMap(TrendingCacheRepository trendingCacheRepository, 
                    Map<String, Map<Long, String>> circleNameMap) {
                super(TrendPoolType.SCat, trendingCacheRepository, circleNameMap);          
            }
            
            @Override
            protected void commit(final String locale, final Long circleTypeId,
                    final Map<Long, Double> vals) {
                if(!circleNameMap.containsKey(locale))
                    return;
                Map<Long, String> cTypeIdMap = circleNameMap.get(locale);
                if(!cTypeIdMap.containsKey(circleTypeId))
                    return;
                String circleTypeGroup = cTypeIdMap.get(circleTypeId);
                trendingCacheRepository.addTrendPost(TrendPoolType.SCat, locale, circleTypeGroup, vals);
                addCommittedKey(locale, circleTypeGroup);                 
            }
        }
    }
    
    private AddTrendWorker worker;
    
    public TrendPool(Map<String, Map<Long, String>> circleNameMap, List<TrendPoolType> mgrTypes) {
        if(mgrTypes == null)
            return;
        TrendingCacheRepository trendingCacheRepository = BeanLocator.getBean("post.trendingCacheRepository");
        worker = new AddTrendWorker(trendingCacheRepository, circleNameMap, mgrTypes);
    }
    
    public void bacthAddMergeTask(final BacthAdd add, final CommittedCallback ccb) {
        if(add == null || ccb == null)
            return;
        
        add.begin(worker);
        worker.commitAll();
        worker.committing(ccb);      
        worker.committed(ccb);
    }

}