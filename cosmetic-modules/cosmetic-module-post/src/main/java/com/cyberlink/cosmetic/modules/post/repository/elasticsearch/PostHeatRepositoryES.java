package com.cyberlink.cosmetic.modules.post.repository.elasticsearch;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.cyberlink.cosmetic.modules.post.repository.PostHeatRepository;
import com.cyberlink.cosmetic.modules.post.model.PostHeat;
import com.cyberlink.cosmetic.core.repository.HttpClient.EsRepositoryHttpClient;
import com.fasterxml.jackson.databind.JsonNode;

public class PostHeatRepositoryES extends EsRepositoryHttpClient<PostHeat>
    implements PostHeatRepository {
    
    @Override
    public EsResult<Boolean> updateLikeCount(String id, Integer updateBy) {
        return updateField(id, "ctx._source.likes += " + updateBy.toString());
    }
    
    @Override
    public EsResult<Boolean> updateCircleInCount(String id) {
        return updateField(id, "ctx._source.cirIns += 1");
    }
    
    @Override
    public EsResult<Map<Long, Map<String, Integer>>> findTopUser(String locale, Integer limit, Date begin, Date end) {
        EsResult<Map<Long, Map<String, Integer>>> result = new EsResult<Map<Long, Map<String, Integer>>>();
        String queryCmd = "{\"size\":0,\"query\":{\"filtered\":{\"query\":{\"match\":{\"loc\":\"%s\"}},\"filter\":{\"bool\":{\"must\":[{\"range\":{\"date\":{\"gte\":%d,\"lte\":%d,\"format\":\"epoch_millis\"}}}],\"must_not\":[]}}}},\"aggs\":{\"users\":{\"terms\":{\"field\":\"uid\",\"size\":%d,\"min_doc_count\":%d,\"order\":{\"total7score\":\"desc\"}},\"aggs\":{\"total7score\":{\"sum\":{\"script\":\"%d*doc['likes'].value+%d*doc['cirIns'].value\",\"lang\":\"expression\"}}}}}}";
        String getTopUrl = "_search";
        String data = String.format(queryCmd, locale, begin.getTime(), end.getTime(), limit, PostHeat.min7dPostCount, PostHeat.likeWeight, PostHeat.circleInWeight);
        result.response = doPost(getTopUrl, data);
        if(result.error != null)
            return result;
        
        Map<Long, Map<String, Integer>> resultMap = new LinkedHashMap<Long, Map<String, Integer>>();
        try {
            JsonNode resNode = objectMapper.readValue(result.response, JsonNode.class);
            Iterator<JsonNode> it = resNode.get("aggregations").get("users").get("buckets").iterator();
            while(it.hasNext()) {
                JsonNode buc =  it.next();
                JsonNode uid = buc.get("key");
                JsonNode pc7 = buc.get("doc_count");
                JsonNode s = buc.get("total7score").get("value");
                Map<String, Integer> m = new HashMap<String, Integer>();
                m.put("postCount", pc7.asInt());
                m.put("score", s.asInt());
                resultMap.put(Long.valueOf(uid.asText()), m);
            }
            result.result = resultMap;
        } catch (Exception e) {
            String err = e.getMessage();
            if(err == null)
                err = "Unknown Error";
            result.error = err;
        }
        return result;
    }

    @Override
    protected String index() {
        return "cosmetic";
    }

    @Override
    protected String type() {
        return "post-heat";
    }

    @Override
    protected Class<?> getEntityClass() {
        return PostHeat.class;
    }

}