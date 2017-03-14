package com.cyberlink.cosmetic.action.backend.user;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.utils.EncrUtil;




@UrlBinding("/user/autoCreateUser.action")
public class AutoCreateUserAction extends AbstractAction {
	@SpringBean("user.UserDao")
    private UserDao userDao;
	
	@SpringBean("user.AccountDao")
	private AccountDao accountDao;
	
	@SpringBean("user.MemberDao")
	private MemberDao memberDao;
	
	@SpringBean("file.fileService")
    private FileService fileService;
	
	@SpringBean("user.SessionDao")
    private SessionDao sessionDao;
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;

	@SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
	
	private final String password = "111111"; 
	private String failString = "";
	private String resultString = "";
	
	private FileBean avatarFile = null;
	private FileBean userFile = null;
	private String localeSel = "";
	private List<String> availableLocale = new ArrayList<String>(0);
	
	public void setAvatarFile(FileBean avatarFile) {
		this.avatarFile = avatarFile;
	}
	
	public void setUserFile(FileBean userFile) {
		this.userFile = userFile;
	}
	
	public List<String> getAvailableLocale() {
        return availableLocale;
    }
	
	public void setLocaleSel(String localeSel) {
		this.localeSel = localeSel;
	}
	
	public List<Map<String, String>> userList = new ArrayList<Map<String, String>>(0);
	
	private static final String PageRoute = "/user/autoCreateUser-route.jsp";
	
	@DefaultHandler
	public Resolution route() {
		boolean isLogin = false;
        Long userId = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
		
		availableLocale.clear();
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		return forward(PageRoute);
	}
	
	public Resolution autoCreateUser() {
		boolean isLogin = false;
        Long userId = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
		
		clearMessage();
		availableLocale.clear();
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		
		try {
			//parseUserList(userFile);
			parseUserListformXLS(userFile);
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1.getMessage(), e1);
			getContext().getRequest().setAttribute("errorMessage", "Invalid User File");
			return forward(PageRoute);
		} catch (NullPointerException | IOException e1) {
			logger.error(e1.getMessage(), e1);
			getContext().getRequest().setAttribute("errorMessage", "Invalid User File");
			return forward(PageRoute);
		}
		
		if (userList.isEmpty()) {
			getContext().getRequest().setAttribute("errorMessage", "User List is Empty");
			return forward(PageRoute);
		}
		
		try{
			uploadAvatar(avatarFile);
		} catch (NullPointerException | IOException e) {
			logger.error(e.getMessage(), e);
			getContext().getRequest().setAttribute("errorMessage", "Invalid Image File");
			getContext().getRequest().setAttribute("message", resultString);
			return forward(PageRoute);
		}
		
		getContext().getRequest().setAttribute("errorMessage", failString);
		getContext().getRequest().setAttribute("message", resultString);
		return forward(PageRoute);
	}
	
	public void parseUserListformXLS(FileBean userFile) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(userFile.getInputStream());
		HSSFWorkbook wb = new HSSFWorkbook(fs);
	    HSSFSheet sheet = wb.getSheetAt(0);
	    HSSFRow row;
	    HSSFCell cell;
	    
	    int rows; // No of rows
	    rows = sheet.getPhysicalNumberOfRows();
	    
	    int cols = 0; // No of columns
	    int tmp = 0;
	    
	    for(int i = 0; i < 10 || i < rows; i++) {
	        row = sheet.getRow(i);
	        if(row != null) {
	            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
	            if(tmp > cols) cols = tmp;
	        }
	    }
	    String[] key = {"number", "displayName", "email", "description"};
	    
	    for(int r = 0; r < rows; r++) {
	        row = sheet.getRow(r);
	        if(row != null) {
	        	Map<String, String> userMap = new HashMap<String, String>();
	            for(int c = 0; c < cols; c++) {
	                cell = row.getCell((short)c);
	                if(cell != null && c < 4) {
	                    userMap.put(key[c], cell.getStringCellValue());
	                }
	            }
	            userMap.put("password", password);
				userMap.put("locale", localeSel);
				userMap.put("gender", "Unspecified");
				userList.add(userMap);
	        }
	    }
	}
	
	
	public boolean uploadAvatar(FileBean avatarFile) throws IOException, NullPointerException{	
		ZipInputStream zis = new ZipInputStream(avatarFile.getInputStream());
		ZipEntry entry;
		int idx = 0;
		int failCount = 0;
		if (userList.isEmpty())
			return false;
		
		while((entry = zis.getNextEntry()) != null) {
			if (idx >= userList.size()) 
				break;

			String mimeType;
            int pushbackLimit = 100;
			PushbackInputStream pushUrlStream = new PushbackInputStream(zis, pushbackLimit);
            byte [] firstBytes = new byte[pushbackLimit];
            pushUrlStream.read(firstBytes);
            pushUrlStream.unread(firstBytes);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
			mimeType = URLConnection.guessContentTypeFromStream(bais);
			if (mimeType == null)
				mimeType = URLConnection.guessContentTypeFromName(entry.getName());
            if (mimeType.startsWith("image/")) {
            	BufferedImage inputImage = ImageIO.read(pushUrlStream);
            	int width = 0;
                int heigth = 0;
                width = inputImage.getWidth();
                heigth = inputImage.getHeight();
                int centerX = width/2;
                int centerY = 0;
                int x = 0;
                int y = 0;
                int lenX = Math.min(width, heigth);
                int lenY = Math.min(width, heigth);
                
                x = centerX - lenX/2;
                	
                BufferedImage outputImage = inputImage.getSubimage(x, y, lenX, lenY);
                width = outputImage.getWidth();
                heigth = outputImage.getHeight();
                String imageType = mimeType.substring("image/".length());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( outputImage, imageType, baos);
                baos.flush();
                String dataUrl = "data:" + mimeType + ";base64," + Base64.encodeBase64String(baos.toByteArray()); 
                baos.close();
                String metadata = String.format("{\"width\":%d,\"height\":%d,\"redirectUrl\":\"\",\"imageDescription\":\"\"}", width, heigth);
                
                FileItem fileItem = null;
                try {
                	Account account = createUser(userList.get(idx));
                	if (account == null) {
                		failString += userList.get(idx).get("number") + ": account fail, ";
                		idx++;
                		failCount++;
                		continue;
                	}
                	
                    fileItem = fileService.createImageFile(account.getUserId(), dataUrl, metadata, FileType.Avatar);
                    if (fileItem == null) {
                    	failString += userList.get(idx).get("number") + ": upload image fail, ";
                    	idx++;
                    	failCount++;
                    	continue;
                    }
                    
                    Long avatarId = fileItem.getFile().getId();
                    updateUser(userList.get(idx), account.getUserId(), avatarId);
                    idx++;
                    
                } catch (InvalidMetadataException | InvalidFileTypeException | IOException e) {
                	logger.error(e.getMessage(), e);
                	failString += userList.get(idx).get("number") + ": unknown error, ";
                	idx++;
                	failCount++;                	
                	continue;
                }     	
            }
            resultString = String.format("Total Success: %d", Math.min(userList.size(), idx) - failCount);
		}
		zis.close();
		
		resultString = String.format("Total Success: %d", Math.min(userList.size(), idx) - failCount);
		return true;
	}
	
	public Account createUser(Map<String, String> userMap){
		String email = userMap.get("email");
		String password = userMap.get("password");
		String displayName = userMap.get("displayName");
		String locale = userMap.get("locale");
		Account account = null;
		
		account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
		if (account == null) {
			//LoginResult status = cyberlinkService.signUp(email, password, displayName, new Locale(locale.substring(0, 2),  locale.substring(3, 5)));
			//if (status.getStatus() == CyberLinkMemberStatus.WaitValidate && status.getContentMap() == null){
				//status = cyberlinkService.signInCyberlink(email, password);
			//}
			try {
				account = doSignIn(email, locale);
				Member member = createNewMember(
						PasswordHashUtil.createHash(password),
						EncrUtil.encrypt(password),
						account.getId(), null, locale);
    		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
    			return null;
    		}
		}
		return account;
	}
	
	public void updateUser(Map<String, String> userMap, Long userId, Long avatarId){
		User user = userDao.findById(userId);
		//The robot account don't need birthDay now
		/*try {
			DateFormat df = DateFormat.getDateInstance();
			Date date;
			date = df.parse(userMap.get("birthDay"));
			user.setBirthDay(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		if (userMap.get("gender").equals("Male")) {
			user.setGender(GenderType.Male);
		} else if(userMap.get("gender").equals("Female")) {
			user.setGender(GenderType.Female);
		} else{
			user.setGender(GenderType.Unspecified);
		}
		user.setDisplayName(userMap.get("displayName"));
		user.setDescription(userMap.get("description"));
		user.setAvatarId(avatarId);
    	Long [] ids = new Long[1];
        ids[0] = avatarId;
        List<FileItem> listResults = fileItemDao.findThumbnails(ids, ThumbnailType.Avatar);
        if(listResults.size() > 0)
        	user.setAvatarLink(listResults.get(0).getOriginalUrl());
        else
        	user.setAvatarLink(null);

		userDao.update(user);
	}
	
	public Account doSignIn(String email, String locale){
        Account	account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        
        User user = null;
        Long userId;
        if (account == null) {
        	account = new Account();
            account.setAccount(email);
            account.setEmail(email);
            account.setAccountSource(AccountSourceType.Email);
            account.setMailStatus(AccountMailStatus.INVALID);
        } 
        
        if (user == null) {
            user = createNewUser(locale);
            user.setAttribute("{}");
            userId = user.getId();
            account.setUserId(userId);
            account = accountDao.update(account);
        } 
        return account;
    }
	
	public User createNewUser(String locale) {
        User user = new User();
    	//user.setCoverFile(getDefaultCover());
        user.setUserType(UserType.Blogger);
    	user.setRegion(locale);
        user.setObjVersion(0);
        if (getContext() != null && getContext().getRequest() != null)
        	user.setIpAddress(getIpAddr(getContext().getRequest()));
        	//user.setIpAddress(getContext().getRequest().getRemoteAddr());

        user = userDao.create(user);
        return user;
    }
	
	public String getIpAddr(HttpServletRequest request) { 
    	String ip = request.getHeader("x-forwarded-for"); 
    	if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
    		int idx = ip.indexOf(',');
    		if (idx > -1) {
    			ip = ip.substring(0, idx);
    		}
    	} else {
    		ip = request.getHeader("Proxy-Client-IP");
    	}
    	
    	if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
    		ip = request.getHeader("WL-Proxy-Client-IP"); 
    	} 
    	if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
    		ip = request.getRemoteAddr(); 
    	} 
    	return ip; 
    }
	
	protected Member createNewMember(String pass, String encrStr, Long accountId, Long memberId, String locale) {
    	Member member = new Member();
    	member.setLocale(locale);
    	member.setAccountId(accountId);
    	if (false)
    		member.setActivateCode(UUID.randomUUID().toString());
    	else
    		member.setActivateCode(null);
    	member.setMemberId(memberId);
    	member.setPassword(pass);
    	member.setEncryption(encrStr);
    	return memberDao.create(member);
    }
	
	public void clearMessage() {
		getContext().getRequest().getAttribute("errorMessage");
		getContext().getRequest().getAttribute("message");
		
	}
}