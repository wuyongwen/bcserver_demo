package com.cyberlink.cosmetic.action.backend.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.PsTrendScheduleService;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.model.PsTrend;
import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool;
import com.cyberlink.cosmetic.modules.post.model.PsTrend.PsTrendKey;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool.PsTrendPoolKey;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService.ScanResultCallback;
import com.cyberlink.cosmetic.utils.CosmeticWorkQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PsTrendScheduleServiceImpl extends AbstractService 
    implements PsTrendScheduleService, InitializingBean {

    private PsTrendService psTrendService;
    private MailInappropPostCommentService mailInappropPostCommentService;
    private ObjectMapper objectMapper;
    private TransactionTemplate transactionTemplate;
    
    static private Boolean reRunning = false;
    static private Boolean genRunning = false;
    static private Boolean upRunning = false;
    static private Boolean enable = true;
    static private Boolean pause = false;
    static private CosmeticWorkQueue updateUsrGroupWorker;
    
    private String [] logReceivers;
    private Boolean isDev = "Dev".equalsIgnoreCase(Constants.getNotifyRegion());
    
	@Override
    public void start() {
        setPause(false);
    }
    
    @Override
    public void stop() {
        setPause(true);
    }

    @Override
    public Map<String, Object> getStatus() {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ReleaseRunning", getReRunning());
        results.put("GenrerateRunning", getGenRunning());
        results.put("UpdateGroupRunning", getUpRunning());
        results.put("Enable", getEnable());
        results.put("Pausing", getPause());
        return results;
    }

    @Override
    public void releaseFromTrendPool(final Date newDisplayDate, List<PsTrendGroup> groups, final Map<String, Object> info) {
        psTrendService.releaseFromTrendPool(groups, new ScanResultCallback<Map<PsTrendKey, Long>>() {

            @Override
            public void doWith(final Map<PsTrendKey, Long> r) {
                transactionTemplate.execute(new TransactionCallback<Boolean>() {

                    @Override
                    public Boolean doInTransaction(TransactionStatus status) {
                        Map<String, Object> batchInfo = new HashMap<String, Object>();
                        Boolean result = psTrendService.batchAddTrendPost(newDisplayDate, r, false, batchInfo);
                        if(info != null)
                            info.put("Batch" + info.size(), batchInfo);
                        return result;
                    }
                });
            }
            
        }, new ScanResultCallback<List<PsTrendGroup>>() {

            @Override
            public void doWith(final List<PsTrendGroup> r) {
                transactionTemplate.execute(new TransactionCallback<Boolean>() {

                    @Override
                    public Boolean doInTransaction(TransactionStatus status) {
                        psTrendService.updateTrendGroups(r);
                        return true;
                    }
                    
                });
            }
            
        });
    }

    @Override
    public void generateTrendPool(String locale, Date startTime, Date endTime, Date displayDate, final Map<String, Object> info) {
        final int bucketId = PsTrendPool.getBucketId(displayDate);
        final Date newDisplayDate = new Date();
        psTrendService.doWithBestPsTrend(locale, startTime, endTime, new ScanResultCallback<Map<String, Map<String, Date>>>() {

            @Override
            public void doWith(Map<String, Map<String, Date>> results) {
                for(String ctId : results.keySet()) {
                    Map<String, Date> pMap = results.get(ctId);
                    final List<PsTrendPool> toAddCatTrends = new ArrayList<PsTrendPool>();
                    for(String pId : pMap.keySet()) {
                        PsTrendPool tp = new PsTrendPool();
                        PsTrendPoolKey key = new PsTrendPoolKey();
                        key.setpId(Long.valueOf(pId));
                        key.setBucket(bucketId);
                        key.setCircleTypeId(Long.valueOf(ctId));
                        tp.setId(key);
                        tp.setDisplayTime(pMap.get(pId));
                        toAddCatTrends.add(tp);
                    }
                    transactionTemplate.execute(new TransactionCallback<Boolean>() {

                        @Override
                        public Boolean doInTransaction(TransactionStatus status) {
                            Map<String, Object> batchInfo = new HashMap<String, Object>();
                            Boolean result = psTrendService.batchCreateTrendPool(newDisplayDate, toAddCatTrends, batchInfo);
                            if(info != null)
                                info.put("Batch" + info.size(), batchInfo);
                            return result;
                        }
                        
                    });
                    
                }
            }
            
        });
        
    }
    
    @Override
    public void purgeExpiredTrendPool(Date displayDate) {
        psTrendService.purgeExpiredTrendPool(displayDate);
    }
    
    //@BackgroundJob(cronExpression = "0 0/10 * 1/1 * ? *")
    public void releaseOneSpecified() {
        if(!getEnable() || getReRunning())
            return;
        if(getPause())
            return;
        
        List<Integer> steps = new ArrayList<Integer>();
        Integer curStep = (int) Math.floor(Calendar.getInstance().get(Calendar.MINUTE) / 10) * 10;
        if(curStep % 20 == 0)
            steps.add(20);
        if(curStep % 30 == 0)
            steps.add(30);
        if(steps.size() <= 0)
            return;
        
        
        ProcessRecorder recoreder = new ProcessRecorder("releaseOneSpecified");
        setReRunning(true);
        Date newDisplayDate = new Date();
    	try{
    	    for(Integer step : steps) {
                List<PsTrendGroup> groupsList = psTrendService.findGroupsByStep(step);
                recoreder.lap(new HashMap<String, Object>(), "FindGroup_%d", step);
                Map<String, Object> info = new HashMap<String, Object>();
                releaseFromTrendPool(newDisplayDate, groupsList, info);
                recoreder.lap(info, "Release for interval [%d]", step);
    	    }
	    } catch (Exception e) {
	        Map<String, Object> stack = new HashMap<String, Object>();
	        stack.put("Stack", e.getStackTrace());
	        recoreder.lap(stack, "Error %s", e.getCause().getMessage());
		} finally {
		    recoreder.Commit();
			setReRunning(false);
		}
    }
	
    //@BackgroundJob(cronExpression = "0 15 0/1 1/1 * ? *")
    public void reGenerate() {
        if(!getEnable() || getGenRunning())
            return;
        if(getPause())
            return;
        
        ProcessRecorder recoreder = new ProcessRecorder("reGenerate");
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        try{
            Pair<Integer, Date> genInfo = getLocIdxToGenerate(day, hour);
            Date toPurge = isTimeToPurge(day, hour);
            if(genInfo != null) {
                setGenRunning(true);
                cal.add(Calendar.DATE, -60);
                Date startTime = cal.getTime();
                cal.add(Calendar.DATE, 60);
                Date endTime = cal.getTime();
                Date displayDate = genInfo.getValue();
                Map<String, Map<Long, Set<Long>>> groupMap = psTrendService.getPsTrendGroupMap();
                String[] locales = groupMap.keySet().toArray(new String[groupMap.keySet().size()]);
                if(genInfo.getKey() >= 0 && genInfo.getKey() < locales.length) {
                    Map<String, Object> info = new HashMap<String, Object>();
                    generateTrendPool(locales[genInfo.getKey()], startTime, endTime, displayDate, info);
                    recoreder.lap(info, "Complete create %s", locales[genInfo.getKey()]);
                }
            }
            else if(toPurge != null) {
                purgeExpiredTrendPool(toPurge);
                recoreder.lap(new HashMap<String, Object>(), "Complete delete bucket %s", toPurge.toString());
            }
        } catch (Exception e) {
            Map<String, Object> stack = new HashMap<String, Object>();
            stack.put("Stack", e.getStackTrace());
            recoreder.lap(stack, "Error %s", e.getCause().getMessage());
        } finally {
            recoreder.Commit();
            setGenRunning(false);
        }
    }
    
    @Override
    public void updateUsrGroupFromFile(File file) {
        updateUsrGroupWorker.execute(new SlitFileTask(file.getParent(), file.getName()));
    }
    
    private interface ReadCallback {
        void readed(String line);
    }
    
    private class ReadFileTask {
        protected void readThrough(String oriFile, ReadCallback callback) throws IOException {
            if(callback == null)
                return;
            
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                inputStream = new FileInputStream(oriFile);
                sc = new Scanner(inputStream, "UTF-8");
                while (sc.hasNextLine())
                    callback.readed(sc.nextLine());
                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }
        }
    }
    private class SlitFileTask extends ReadFileTask implements Runnable {
        final private String oriFile;
        final private String chunkedPath;
        
        public SlitFileTask(String filePath, String fileName) {
            this.oriFile = filePath + "/" + fileName;
            this.chunkedPath = filePath + "/chunk";
        }
        
        @Override
        public void run() {
            File chunckedDocDir = new File(chunkedPath);
            ProcessRecorder recorder = new ProcessRecorder("SlitFileTask");
            if (!chunckedDocDir.exists() && !chunckedDocDir.mkdirs()) {
                recorder.lap(new HashMap<String, Object>(), "Error : Create path %s failed.", chunkedPath);
                recorder.Commit();
                return;
            }
            
            try {
                split();
                if(!getUpRunning()) {
                    setUpRunning(true);
                    updateUsrGroupWorker.execute(new ProcessUpdateTrendGroup(0, new ProcessRecorder("ProcessUpdateTrendGroup_Split")));
                }
                
                recorder.lap(new HashMap<String, Object>(), "Complete %s.", chunkedPath);
                recorder.Commit();
            }
            catch(Exception e) {
                logger.error("", e);
                recorder.lap(new HashMap<String, Object>(), "Error : %s", e.getMessage());
                recorder.Commit();
            }
            
        }
        
        private class SplitCallback implements ReadCallback {
            public StringBuffer stringBuffer = new StringBuffer();
            public int i = 0;
            public int counter = 1;
            
            @Override
            public void readed(String line) {
                stringBuffer.append(line + "\n");
                i++;
                if(i >= 1000) {
                    String createdFilePath = saveChunked(counter, stringBuffer);
                    if(createdFilePath != null) {
                        counter++;
                    }
                    stringBuffer = new StringBuffer();
                    i = 0;
                }
            }
            
            public void flush() {
                if(i > 0) {
                    saveChunked(counter++, stringBuffer);
                }
            }
        }
        
        private void split() throws IOException {
            SplitCallback callback = new SplitCallback(); 
            readThrough(oriFile, callback);
            callback.flush();
        }
        
        private String saveChunked(int counter, StringBuffer pData) {
            String chunkedFileName = StringUtils.leftPad(String.valueOf(counter), 10, '0') + ".dat";
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(chunkedPath + "/" + chunkedFileName));
                out.write(pData.toString());
                return chunkedFileName;
            } catch (IOException e) {
                logger.error("", e);
                return null;
            }
            finally {
                if(out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                    
                }
            }
        }
        
    }
    
    private class ProcessUpdateTrendGroup extends ReadFileTask implements Runnable {
        private ProcessRecorder recorder;
        private int loop;
        private String chunkedPath;
        private String chunkedFileName;
        public ProcessUpdateTrendGroup(int loop, ProcessRecorder recorder) {
            this.recorder = recorder;
            this.loop = loop;
        }
        
        public ProcessUpdateTrendGroup(int loop, ProcessRecorder recorder, String chunkedPath) {
            this(loop, recorder);
            this.chunkedPath = chunkedPath;
        }
        
        private class ProcessCallback implements ReadCallback {
            final Map<String, String> inGroupMap;
            ProcessCallback() {
                inGroupMap = new HashMap<String, String>();
            }

            @Override
            public void readed(String line) {
                if(line == null || line.length() <= 0)
                    return;
                String [] toks = line.split("\t");
                if(toks.length != 2)
                    return;
                String uuid = toks[0];
                String [] cirGroupIds = toks[1].split(",");
                Long [] cirGroupLIds = new Long[cirGroupIds.length];   
                for (int i = 0; i < cirGroupIds.length; i++) {   
                    cirGroupLIds[i] = Long.parseLong(cirGroupIds[i]);   
                }
                Arrays.sort(cirGroupLIds);
                String groups = "";
                for (int i = 0; i < cirGroupLIds.length; i++) {   
                    groups += cirGroupLIds[i].toString() + ",";
                }
                if(groups.endsWith(","))
                    groups = groups.substring(0, groups.length() - 1);
                inGroupMap.put(uuid, groups);
                if(inGroupMap.size() >= 100)
                    flush();
            }
            
            public void flush() {
                try {
                    batchUpdateGroup(inGroupMap);
                    recorder.lap(new HashMap<String, Object>(), "flush_%d", loop);
                }
                catch(Exception e) {
                    logger.error("", e);
                    recorder.lap(new HashMap<String, Object>(), "error_%d", loop);
                }
                inGroupMap.clear();
                loop++;
            }
            
            private void batchUpdateGroup(final Map<String, String> group) {
                if(inGroupMap.size() <= 0)
                    return;
                
                transactionTemplate.execute(new TransactionCallback<Boolean>() {

                    @Override
                    public Boolean doInTransaction(
                            TransactionStatus status) {
                        return psTrendService.createOrUpdateUserGroup(group);
                    }
                    
                });
            }
        }
        
        @Override
        public void run() {
            if(chunkedPath == null) {
                File trendGroupWorkingDir = new File(Constants.getPostUpdateTrendGroupPath());
                File[] files = trendGroupWorkingDir.listFiles();
    
                Arrays.sort(files, new Comparator<File>(){
                    public int compare(File f1, File f2)
                    {
                        return f1.getName().compareTo(f2.getName());
                    } 
                });
                for(File f : files) {
                    if(!f.isDirectory())
                        continue;
                    chunkedPath = f.getPath() + "/chunk";
                    break;
                }
                if(chunkedPath == null) {
                    recorder.Commit();
                    setUpRunning(false);
                    return;
                }
            }
            
            if(chunkedFileName == null) {
                File trendGroupWorkingDir = new File(chunkedPath);
                File[] files = trendGroupWorkingDir.listFiles();
                for(File f : files) {
                    if(!f.isFile())
                        continue;
                    chunkedFileName = f.getName();
                    break;
                }
                if(chunkedFileName == null) {
                    try {
                        FileUtils.deleteDirectory(new File(chunkedPath.replace("/chunk", "")));
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                    recorder.lap(new HashMap<String, Object>(), "Complete");
                    recorder.Commit();
                    updateUsrGroupWorker.execute(new ProcessUpdateTrendGroup(0, new ProcessRecorder("ProcessUpdateTrendGroup_Self")));
                    return;
                }
            }
            
            try {
                ProcessCallback callback = new ProcessCallback();
                readThrough(chunkedPath + "/" + chunkedFileName, callback);
                callback.flush();
            } catch (IOException e) {
                logger.error("", e);
            }
            finally {
                try {
                    Files.delete(Paths.get(chunkedPath + "/" + chunkedFileName));
                    if(!isDev)
                        Thread.sleep(60000);
                } catch (IOException | InterruptedException e) {
                    logger.error("", e);
                }
                updateUsrGroupWorker.execute(new ProcessUpdateTrendGroup(loop, recorder, chunkedPath));
            }
        }
        
    }
    
    private Pair<Integer, Date> getLocIdxToGenerate(int day, int hour) {
        // Production server
        if(!isDev) {
            if(day == Calendar.SUNDAY) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 7);
                return Pair.of(hour, cal.getTime());
            }
            return null;
        }
        else { // Demo testing
            int gmt8 = hour + 8 >= 24 ? hour + 8 - 24 : hour + 8;
            if(gmt8 >= 3) {
                return Pair.of(gmt8 - 3, new Date());
            }
            return null;
        }
    }
    
    private Date isTimeToPurge(int day, int hour) {
        // Production server
        if(!isDev) {
            if(day == Calendar.WEDNESDAY && hour == 7) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -7);
                return cal.getTime();
            }
            return null;
        }
        else { // Demo testing
            int gmt8 = hour + 8 >= 24 ? hour + 8 - 24 : hour + 8;
            if(gmt8 == 2) {
                return new Date();
            }
            return null;
        }
    }
    
    private Boolean getReRunning() {
        Boolean r;
        synchronized(reRunning) {
            r = reRunning;
        }
        return r;
    }

    private void setReRunning(Boolean reRunning) {
        synchronized(reRunning) {
            PsTrendScheduleServiceImpl.reRunning = reRunning;
        }
    }
    
    private void setGenRunning(Boolean genRunning) {
        synchronized(genRunning) {
            PsTrendScheduleServiceImpl.genRunning = genRunning;
        }
    }

    private Boolean getGenRunning() {
        Boolean r;
        synchronized(genRunning) {
            r = genRunning;
        }
        return r;
    }
    
    private void setUpRunning(Boolean upRunning) {
        synchronized(upRunning) {
            PsTrendScheduleServiceImpl.upRunning = upRunning;
        }
    }
    
    private Boolean getUpRunning() {
        Boolean r;
        synchronized(upRunning) {
            r = upRunning;
        }
        return r;
    }
    
    public Boolean getEnable() {
        Boolean e;
        synchronized(enable) {
            e = enable;
        }
        return e;
    }

    public void setEnable(Boolean enable) {
        synchronized(enable) {
            PsTrendScheduleServiceImpl.enable = enable;
        }
    }

    public Boolean getPause() {
        Boolean p;
        synchronized(pause) {
            p = pause;
        }
        return p;
    }

    public void setPause(Boolean pause) {
        synchronized(pause) {
            PsTrendScheduleServiceImpl.pause = pause;
        }
    }
    
    public void setPsTrendService(PsTrendService psTrendService) {
        this.psTrendService = psTrendService;
    }

    public void setMailInappropPostCommentService(MailInappropPostCommentService mailInappropPostCommentService) {
        this.mailInappropPostCommentService = mailInappropPostCommentService;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    private class ProcessRecorder {
        private Map<String, Object> summary;
        private String subject;
        private long markTime;
        private long begin;
        final private String beginTime;
        
        public ProcessRecorder(String subject) {
            this.summary = new LinkedHashMap<String, Object>();
            this.begin = System.currentTimeMillis();
            this.markTime = this.begin;
            this.subject = subject;
            this.beginTime = (new Date()).toString();
        }
        
        private void SendMail(Object object) {
            subject += " - " + Constants.getWebsiteDomain();
            String content;
            try {
                content = objectMapper.writerWithView(Views.Public.class).writeValueAsString(object);
            } catch (JsonProcessingException e) {
                content = e.getMessage();
            }
            
            mailInappropPostCommentService.directSend(logReceivers, subject, content);
        }
        
        public void lap(Map<String, Object> msg, String key, Object...args) {
            msg.put("ProcessTime", String.valueOf(System.currentTimeMillis() - markTime) + " ms");
            summary.put(String.format(key, args), msg);
            markTime = System.currentTimeMillis();
        }
        
        public void Commit() {
            if(summary.size() <= 0)
                return;
            summary.put("Total Process", String.valueOf(System.currentTimeMillis() - begin) + " ms");
            summary.put("BeginTime", beginTime);
            SendMail(summary);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(isDev)
            logReceivers = new String[] {"Victor_Chew@PerfectCorp.com"};
        else
            logReceivers = new String[] {"Victor_Chew@PerfectCorp.com", "Frank_Chuang@PerfectCorp.com"};
        
        if(updateUsrGroupWorker == null) {
            updateUsrGroupWorker = new CosmeticWorkQueue(1, "update_trend_group");
            if(!getUpRunning()) {
                setUpRunning(true);
                updateUsrGroupWorker.execute(new ProcessUpdateTrendGroup(0, new ProcessRecorder("ProcessUpdateTrendGroup_Init")));
            }
        }
    }
}
