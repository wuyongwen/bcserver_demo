package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.MetaValue;
import org.hibernate.annotations.Where;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.look.model.Look;

@Entity
@DynamicUpdate
@Table(name = "BC_ATTACHMENT")
public class Attachment extends AbstractEntity<Long> {

    private static final long serialVersionUID = 7748198809268548786L;
    private Long postId;
    /*private String refType;
    private Long refId;*/
    private Object target;
    private Long downloadCount;
    private File attachmentFile;
    private AttachmentExtLink attachmentExtLink;
    
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", insertable=false, updatable=false)
    public File getAttachmentFile()
    {
        if(target == null || !(target instanceof File))
            return null;
        else
            return (File)target;
    }
    
    public void setAttachmentFile(File attachmentFile)
    {
        this.attachmentFile = attachmentFile;
    }
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", insertable=false, updatable=false)
    public AttachmentExtLink getAttachmentExtLink()
    {
        if(target == null || !(target instanceof AttachmentExtLink))
            return null;
        else
            return (AttachmentExtLink)target;
    }
    
    public void setAttachmentExtLink(AttachmentExtLink attachmentExtLink)
    {
        this.attachmentExtLink = attachmentExtLink;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "POST_ID")
    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    /*@Column(name = "REF_TYPE", length = 5)
    public String getRefType() {
        return this.refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    @Column(name = "REF_ID")
    public Long getRefId() {
        return this.refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }*/
    
    public void setTarget(Object target) {
        this.target = target;
    }
    
    @Any(metaColumn = @Column(name = "REF_TYPE"))
    @AnyMetaDef(idType = "long", metaType = "string", 
            metaValues = { 
             @MetaValue(targetEntity = File.class, value = "File"),
             @MetaValue(targetEntity = AttachmentExtLink.class, value = "Link"),
             @MetaValue(targetEntity = Look.class, value = "Look")
       })
    @JoinColumn(name="REF_ID")
    @Where(clause="ID != 0")
    public Object getTarget() {
        return target;
    }

    @Column(name = "DOWNLOAD_COUNT")
    public Long getDownloadCount() {
        return this.downloadCount;
    }

    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

}
