package com.cyberlink.cosmetic.modules.post.repository.elasticsearch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.repository.PsTrendHeatRepository;
import com.cyberlink.cosmetic.modules.post.model.PostHeat;
import com.cyberlink.cosmetic.modules.post.model.PsTrendHeat;
import com.cyberlink.cosmetic.core.repository.HttpClient.EsRepositoryHttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PsTrendHeatRepositoryES extends EsRepositoryHttpClient<PsTrendHeat>
    implements PsTrendHeatRepository {
    
    @Override
    public EsResult<Boolean> updateLikeCount(String id, Integer updateBy) {
        return updateField(id, "ctx._source.likes += " + updateBy.toString());
    }
    
    @Override
    public EsResult<Boolean> updateCircleInCount(String id) {
        return updateField(id, "ctx._source.cirIns += 1");
    }
    
    @Override
    public EsResult<Boolean> batchCreateOrUpdate(List<PsTrendHeat> ts) {
        EsResult<Boolean> result = new EsResult<Boolean>();
        result.result = true;
        if(ts == null || ts.size() <= 0)
            return result;
        String batchApi = "_bulk";
        String data = "";
        ObjectWriter notPrettyPrinter = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class);
        try {
            for(PsTrendHeat t : ts) {
                data += String.format("{\"update\":{\"_id\":\"%s\"}}\n", t.getId());
                String script = "if(ctx._source.containsKey(\\\"cirTypes\\\")) ctx._source.cirTypes = (ctx._source.cirTypes + cirTypes).unique(); else ctx._source.paymentInfos = paymentInfo; ctx._source.loc = loc; if(likes != null) ctx._source.likes = likes; if(cirIns != null) ctx._source.cirIns = cirIns;";
                String updateFormat = "{\"script\" : \"%s\", \"params\" : {\"cirTypes\" : %s, \"loc\": \"%s\", \"likes\" : %d, \"cirIns\":%d}, \"upsert\" : %s}\n";
                data += String.format(updateFormat, script, notPrettyPrinter.writeValueAsString(t.getCirTypes()), t.getLoc(), t.getLikes(), t.getCirIns(), notPrettyPrinter.writeValueAsString(t));
            }
            result.response = doPost(batchApi, data);
        } 
        catch (JsonProcessingException e) {
            result.result = false;
            result.error = e.getOriginalMessage();
        }
        
        return result;
    }

    @Override
    public void findTopTrend(List<String> circleTypeIds, Double topRatio, Date begin, Date end, LoopCallback<Map<String, Map<String, Date>>> callback) {   
        if(callback == null)
            return;
        
        for(String ctid : circleTypeIds) {
            String queryCmd = "{\"from\" : %d, \"size\": %d,\"sort\": [{\"_script\": {\"script\": \"%d*doc['likes'].value+%d*doc['cirIns'].value\",\"type\": \"number\",\"order\": \"desc\"}}],\"query\": {\"filtered\": {\"query\": {\"bool\": {\"must\": [{\"range\": {\"date\": {\"gte\": %d,\"lte\": %d,\"format\": \"epoch_millis\"}}}],\"must_not\": []}},\"filter\": {\"match\": {\"cirTypes\": {\"query\": \"%s\",\"type\": \"phrase\"}}}}}}";
            String getTopUrl = "_search";
            
            int from = 0, batchSize = 1000;
            Integer maxSize = null;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            do {
                String data = String.format(queryCmd, from, batchSize, PostHeat.likeWeight, PostHeat.circleInWeight, begin.getTime(), end.getTime(), ctid);
                Map<String, Map<String, Date>> resultMap = new LinkedHashMap<String, Map<String, Date>>();
                String response = doPost(getTopUrl, data);
                Map<String, Date> m = new LinkedHashMap<String, Date>();
                try {
                    JsonNode responseNode = objectMapper.readValue(response, JsonNode.class);
                    JsonNode resultNode = responseNode.get("hits"); 
                    if(resultNode == null) {
                        logger.error("Null hits");
                        break;
                    }
                    if(maxSize == null)
                        maxSize = (int) Math.ceil(resultNode.get("total").asInt() * topRatio);
                        
                    Iterator<JsonNode> it = resultNode.get("hits").iterator();
                    while(it.hasNext() && m.size() < maxSize - from) {
                        JsonNode trendPost =  it.next().get("_source");
                        JsonNode postId = trendPost.get("id");
                        JsonNode date = trendPost.get("date");
                        m.put(postId.asText(), format.parse(date.asText()));
                    }
                    resultMap.put(ctid, m);
                    callback.doWith(resultMap);
                } catch (Exception e) {
                    String err = e.getMessage();
                    logger.error(err);
                    break;
                }
                from += batchSize;
                if(from >= maxSize)
                    break;
            } while(true);
        }
    }

    @Override
    protected String index() {
        return "cosmetic";
    }

    @Override
    protected String type() {
        return "ps-trend-heat";
    }

    @Override
    protected Class<?> getEntityClass() {
        return PsTrendHeat.class;
    }

}