package com.cyberlink.cosmetic.modules.file.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailStrategy;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.ImageService;

public class MagickImageServiceImpl extends AbstractService implements
        ImageService {
    private String command;
   
    public void setCommand(String command) {
        this.command = command;
    }
    
    public void thumbnail(FileItem sourceItem, Integer width, Integer height, FileItem newItem) {
        exec(getThumbnailCommand(sourceItem.getLocalFilePath(), width, height, ThumbnailStrategy.Strict, null, newItem.getLocalFilePath()));
    }
    
    public void thumbnail(FileItem sourceItem, ThumbnailType type, FileItem newItem) {
        Double aspectRatio = sourceItem.aspectRatio();
        ThumbnailStrategy strategy = type.landscape();
        ArrayList<String> extraParams = new ArrayList<String>();
        
        if (aspectRatio != null && aspectRatio < 1)
            strategy = type.portrait();
                
        if (newItem.getContentType().equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE)) {
            if (sourceItem.getContentType().equalsIgnoreCase(MediaType.IMAGE_GIF_VALUE) || sourceItem.getContentType().equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE)) {
                extraParams.add("-background");
                extraParams.add("white");
                extraParams.add("-flatten");
            }
            
            extraParams.add("-sampling-factor");
            extraParams.add("4:2:0");
            extraParams.add("-quality");
            extraParams.add(type.quality().toString());
        }
                
        exec(getThumbnailCommand(sourceItem.getLocalFilePath(), type.width(), type.height(), strategy, extraParams, newItem.getLocalFilePath()));
    }
    
    private final void exec(String[] exec) {
        try {
            logger.debug(StringUtils.join(exec, " "));
            final Process p = Runtime.getRuntime().exec(exec);
            p.waitFor();
        } catch (Exception e) {
            logger.error("Fail execute comment: " + StringUtils.join(exec, " "), e);
        }
    }
    
    private String[] getThumbnailCommand(String path, Integer width, Integer height, ThumbnailStrategy strategy, ArrayList<String> extraParam, String toPath) {
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(command);
        commands.add(path);
        commands.add("-strip");
        
        if (extraParam != null && extraParam.size() > 0)
            commands.addAll(extraParam);
        
        if (width != null && height != null) {
            commands.add("-thumbnail");
            commands.add(thumbnailStrategyCommand(width, height, strategy));
        }
        
        commands.add(toPath);
        return commands.toArray(new String[commands.size()]);
    }
    
    private String thumbnailStrategyCommand(Integer width, Integer height, ThumbnailStrategy strategy) {
        String command = "";
        switch(strategy) {
        case Minimum : {
            command = width.toString() + "x" + height.toString() + "^";
            break;
        }
        case FixedWidth : {
            command = width.toString();
            break;
        }
        case FixedHeight : {
            command = "x" + height.toString();
            break;
        }
        case Strict : {
            command = width.toString() + "x" + height.toString() + "!";
            break;
        }
        case Maximum :
            default: {
                command = width.toString() + "x" + height.toString();
                break;
            }
        }

        return command;
    }
    
    /**
     * appendImages 
     * @param combineImagePixel if direction is vertical,combineImagePixel is output image width.if direction is horizontal,combineImagePixel is output image height
     * @param direction vertical or horizontal
     * @param outputImageFullPath
     * @param sourceImageFullPaths
     */
    public void appendImages(Integer combineImagePixel,String direction,String outputImageFullPath,List<String> sourceImageFullPaths){
    	if(combineImagePixel == null || direction == null || outputImageFullPath == null || sourceImageFullPaths == null)
    		return;
    	final ArrayList<String> commands = new ArrayList<String>();
    	commands.add(command);
    	switch(direction){
		case "vertical":
	    	for(String sourceImagePath : sourceImageFullPaths){
	    		commands.add(sourceImagePath);
	    		commands.add("-resize");
	    		commands.add(thumbnailStrategyCommand(combineImagePixel,null,ThumbnailStrategy.FixedWidth));
	    	}
	    	commands.add("-append");
	    	commands.add(outputImageFullPath);
			break;
		case "horizontal":
	    	for(String sourceImagePath : sourceImageFullPaths){
	    		commands.add(sourceImagePath);
	    		commands.add("-resize");
	    		commands.add(thumbnailStrategyCommand(null,combineImagePixel,ThumbnailStrategy.FixedHeight));
	    	}
	    	commands.add("-append");
	    	commands.add(outputImageFullPath);
			break;
    	}
    	exec(commands.toArray(new String[commands.size()]));
    }
}