package com.cyberlink.cosmetic.modules.post.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.stripes.util.Base64;

import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DocPostConverter extends WordToHtmlConverter {
    public class PostContent {
        public String content = "";
        public List<String> attachments = new ArrayList<String>();
    }
    
    public class MainPostContent extends PostContent{
        public String title = "";
    }
    
    public interface ImageHandler {
        String handleImage(String dataUrl, String metadata, boolean isCover);
    }
    
    public List<PostContent> posts = new ArrayList<PostContent>();
    public boolean lastProcessIsText = true;
    public int postIdx = 0;
    public boolean isCover = true;
    public ImageHandler imageHandler;
    
    public DocPostConverter(Document document) {
        super(document);
    }
    
    public List<PostContent> getPost() {
        return posts;
    }
    
    @Override
    protected void processParagraph( HWPFDocumentCore hwpfDocument, Element parentElement, int currentTableLevel, Paragraph paragraph, String bulletText )
    {
        if(posts.size() <= 0) {
            super.processParagraph(hwpfDocument, parentElement, currentTableLevel, paragraph, bulletText);
            return;
        }

        PostContent post = posts.get(postIdx);
        if(post.content.length() > 0)
            post.content += "<br>";
        
        super.processParagraph(hwpfDocument, parentElement, currentTableLevel, paragraph, bulletText);
    }
    
    @Override
    protected void processImageWithoutPicturesManager( Element currentBlock, boolean inlined, Picture picture ) {      
        InputStream in = new ByteArrayInputStream(picture.getContent());
        int width = 0;
        int heigth = 0;
        String mineType = picture.getMimeType();
        try {
            BufferedImage bImageFromConvert = ImageIO.read(in);
            width = bImageFromConvert.getWidth();
            heigth = bImageFromConvert.getHeight();           
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        String dataUrl = "data:" + mineType + ";base64," + Base64.encodeBytes(picture.getContent());
        String metadata = String.format("{\"width\":%d,\"height\":%d,\"redirectUrl\":\"\",\"imageDescription\":\"\"}", width, heigth);
        String attachment = imageHandler.handleImage(dataUrl, metadata, isCover);
        if(isCover)
            isCover = false;
        
        if(attachment.length() <= 0)
            return;
        
        if(lastProcessIsText) {
            lastProcessIsText = false;
            PostContent post = new PostContent();
            post.attachments.add(attachment);
            posts.add(post);
            postIdx++;
        }
        else {
            PostContent post = posts.get(postIdx);
            post.attachments.add(attachment);
        }
    }
    
    @Override
    protected boolean processCharacters(final HWPFDocumentCore wordDocument, final int currentTableLevel, final Range range, final Element block)
    {
        boolean result = super.processCharacters(wordDocument, currentTableLevel, range, block);
        String content = block.getTextContent();
        block.setTextContent("");
        
        if(content.length() <= 0)
            return result;
        
        if(posts.size() <= 0) {
            MainPostContent mainPost = new MainPostContent();
            mainPost.title = content;
            posts.add(mainPost);
            lastProcessIsText = false;
            return result;
        }
        else if(lastProcessIsText) {
            PostContent post = posts.get(postIdx);
            post.content += content;
        }
        else {
            lastProcessIsText = true;
            PostContent post = posts.get(postIdx);
            post.content += content;
        }
        
        return result;
    }
    
    @Override
    protected void processLineBreak( Element block, CharacterRun characterRun )
    {
        super.processLineBreak(block, characterRun);
        if(posts.size() <= 0) 
            return;
        PostContent post = posts.get(postIdx);
        post.content += "<br>";
    }
    
    @Override
    protected void processHyperlink( HWPFDocumentCore wordDocument, Element currentBlock, Range textRange, int currentTableLevel, String hyperlink )
    {
        if(posts.size() <= 0) {
            super.processHyperlink(wordDocument, currentBlock, textRange, currentTableLevel, hyperlink);
            return;
        }
        
        PostContent post = posts.get(postIdx);
        post.content += "<a href=\"" + hyperlink + "\">";
        super.processHyperlink(wordDocument, currentBlock, textRange, currentTableLevel, hyperlink);
        post.content += "</a>";
    }
}