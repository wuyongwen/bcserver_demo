package com.cyberlink.cosmetic.modules.post.event;

import java.util.Map;

import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalTrendEvent extends DurableEvent {
    
    private static final long serialVersionUID = -31012311743319519L;

    static public PersonalTrendEvent CreateAddEvent(TrendPoolType perType, String locale, String circleKey, 
            Long idx, Map<Long, Double> scoreValueMap) {
        return new PersonalTrendEvent(CommandType.ad_pt, perType, locale, circleKey, 
                idx, scoreValueMap, null, null, null, null, null, null, null, null);
    }
    
    static public PersonalTrendEvent CreateTrimEvent(TrendPoolType perType, String locale, String circleKey, 
            Long idx, Long maxSize) {
        return new PersonalTrendEvent(CommandType.tr_pt, perType, locale, circleKey, 
                idx, null, maxSize, null, null, null, null, null, null, null);
    }
    
    static public PersonalTrendEvent CreateSwapEvent(TrendPoolType perType, String locale, String circleKey, 
            Long idx, Long newIdx) {
        return new PersonalTrendEvent(CommandType.sw_pt, perType, locale, circleKey, 
                idx, null, null, newIdx, null, null, null, null, null, null); 
    }
    
    static public PersonalTrendEvent createMergeEvent(TrendPoolType perType, String locale, String circleKey, 
            Long idx1, Long idx2, Long toIdx, Long toCursor) {
        return new PersonalTrendEvent(CommandType.mr_pt, perType, locale, circleKey, 
                toIdx, null, null, idx1, toCursor, idx2, null, null, null, null);
    }
    
    static public  PersonalTrendEvent CreateUpdateCursorEvent(TrendPoolType perType, String locale, String circleKey, 
            Long newCursor) {
        return new PersonalTrendEvent(CommandType.mv_cr, perType, locale, circleKey, 
                null, null, null, null, newCursor, null, null, null, null, null);
    }
    
    static public PersonalTrendEvent CreateRemoveEvent(String locale, Long circleTypeId, String circleKey, 
            Long postId) {
        return new PersonalTrendEvent(CommandType.rm_pt, null, locale, circleKey, 
                null, null, null, null, null, null, null, postId, circleTypeId, null);
    }
    
    static public PersonalTrendEvent CreateModifyEvent(String locale, Long circleTypeId, Long newCircleTypeId, 
            String circleKey, String newCircleKey, Long postId) {
        return new PersonalTrendEvent(CommandType.md_pt, null, locale, circleKey, 
                null, null, null, null, null, null, newCircleKey, postId, circleTypeId, newCircleTypeId);
    }
    
    public enum CommandType {
        ad_pt, // add personal trend
        rm_pt, // remove personal trend
        md_pt, // modify personal trend
        tr_pt, // trim personal trend
        sw_pt, // swap personal trend      
        mv_cr, // move_cursor
        mr_pt; // merge two personal trend
    }

    private CommandType cmd;
    private String pt;
    private String loc;
    private String ck;
    private Map<Long, Double> svm;
    private Long cidx;
    private Long tsz;
    private Long oidx;
    private Long nc;
    private Long sidx;
    private String nck;
    private Long pi;
    private Long ctid;
    private Long nctid;
    
    public PersonalTrendEvent(CommandType cmd, TrendPoolType perType, String locale, String circleKey, 
            Long idx, Map<Long, Double> scoreValueMap, Long maxPooLSize, Long oldIdx, 
            Long newCursor, Long srcIdx, String newCircleKey, Long postId, Long circleTypeId,
            Long newCircleTypeId) {
        super(new Object());
        this.cmd = cmd;
        this.loc = locale;
        this.ck = circleKey == null ? "null" : circleKey.toString();
        if(perType != null)
            this.pt = perType.getShortForm();
        this.cidx = idx;
        this.svm = scoreValueMap;
        this.tsz = maxPooLSize;
        this.oidx = oldIdx;
        this.nc = newCursor;
        this.sidx = srcIdx;
        this.nck = newCircleKey == null ? "null" : newCircleKey.toString();
        this.pi = postId;
        this.ctid = circleTypeId;
        this.nctid = newCircleTypeId;
    }

    public PersonalTrendEvent() {
        super(new Object());
    }
    
    public CommandType getCmd() {
        return cmd;
    }

    public void setCmd(CommandType cmd) {
        this.cmd = cmd;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getCk() {
        return ck;
    }

    public void setCk(String ck) {
        this.ck = ck;
    }

    public Map<Long, Double> getSvm() {
        return svm;
    }

    public void setSvm(Map<Long, Double> svm) {
        this.svm = svm;
    }

    public Long getCidx() {
        return cidx;
    }

    public void setCidx(Long cidx) {
        this.cidx = cidx;
    }

    public Long getTsz() {
        return tsz;
    }

    public void setTsz(Long tsz) {
        this.tsz = tsz;
    }

    public Long getOidx() {
        return oidx;
    }

    public void setOidx(Long oidx) {
        this.oidx = oidx;
    }

    public Long getNc() {
        return nc;
    }

    public void setNc(Long nc) {
        this.nc = nc;
    }
    
    public Long getSidx() {
        return sidx;
    }

    public void setSidx(Long sidx) {
        this.sidx = sidx;
    }
    
    public String getNck() {
        return nck;
    }

    public void setNck(String nck) {
        this.nck = nck;
    }

    public Long getPi() {
        return pi;
    }

    public void setPi(Long pi) {
        this.pi = pi;
    }
    
    public Long getCtid() {
        return ctid;
    }

    public void setCtid(Long ctid) {
        this.ctid = ctid;
    }

    public Long getNctid() {
        return nctid;
    }

    public void setNctid(Long nctid) {
        this.nctid = nctid;
    }

}
