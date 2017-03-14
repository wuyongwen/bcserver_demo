package com.cyberlink.cosmetic.action.backend.product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.service.AutoCheckAndImportNewProductsService;
import com.cyberlink.utility.Tool;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/product/uploadProductInfoByJson.action")
public class UploadProductInfoByJsonAction extends AbstractAction{

	
	private String jsonStr;
	private String productRelatedInfo;
	private String locale;
	private String fileName;
	private FileBean newAttachment;
	
	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	 
	public void setFileBean(FileBean newAttachment) {
	    this.newAttachment = newAttachment;
	}
	
	public void setProductRelatedInfo(String productRelatedInfo) {
		this.productRelatedInfo = productRelatedInfo;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}

	@DefaultHandler
	public Resolution route() {
		AutoCheckAndImportNewProductsService autoCheckAndImportNewProductsService = BeanLocator.getBean("backend.AutoCheckAndImportNewProductsService");
		try {
			autoCheckAndImportNewProductsService.setUploadFileNow();
			FileWriter file;
			String filePath = Constants.getUploadJsonStringPath();
			File f = new File(filePath);
			if (!f.exists()) {
				f.mkdirs();
			}
			if(newAttachment != null && productRelatedInfo != null && locale != null){
				String productRelatedInfoPath = Tool.pathJoin(filePath, "_store", "_searchProductRelated");
				Tool.makeDir(productRelatedInfoPath);
				String productRelatedInfoFullPath = Tool.pathJoin(productRelatedInfoPath, String.format("%s.txt", locale));
				file = new FileWriter(productRelatedInfoFullPath);
				file.write(productRelatedInfo);
				file.flush();
				file.close();
				
				fileName = newAttachment.getFileName();
				File saveFile = new File(Tool.pathJoin(filePath, fileName));
				newAttachment.save(saveFile);
				newAttachment.delete();
				if(fileName.contains(".zip")){
					unZipFiles(filePath  + fileName,Tool.pathJoin(filePath, "_store", "_searchProduct"));
					Tool.delFile(Tool.pathJoin(filePath, fileName));
				}
			}else if (jsonStr!= null && !"".equals(fileName)){
				file = new FileWriter(filePath + fileName);
				file.write(jsonStr.toString());
				file.flush();
				file.close();
			}else{
				return new ErrorResolution(400, "Bad Request message:file content is null.");
			}
		} catch (Exception e) {
			return new ErrorResolution(400, "Bad Request message:" + e.getMessage());
		}finally{
			autoCheckAndImportNewProductsService.setNotUploadFileNow();
		}
		return new StreamingResolution("text/html", "Upload product infomation success :" + fileName);
    }
	
	/** 
     * Unzip to the specified directory
     * @param zipPath 
     * @param descDir  
     */  
    public static void unZipFiles(String zipPath,String descDir)throws IOException{  
        unZipFiles(new File(zipPath), descDir);  
    }  
    /** 
     * Unzip to the specified directory
     * @param zipFile 
     * @param descDir 
     */  
    @SuppressWarnings("rawtypes")  
    public static void unZipFiles(File zipFile,String descDir)throws IOException{  
    	File pathFile = new File(descDir);  
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }
        ZipFile zip = new ZipFile(zipFile);
        for(Enumeration entries = zip.entries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");
            outPath = outPath.replaceAll("\\\\", "/");            
            if(outPath.lastIndexOf('/') > 0){
	            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
	            if(!file.exists()){  
	                file.mkdirs();  
	            }  
            }
            if(new File(outPath).isDirectory()){  
                continue;  
            }
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
            }
        zip.close();
    }
}