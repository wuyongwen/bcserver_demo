package com.cyberlink.cosmetic.action.backend.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.sqs.model.Message;
import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.BounceAndComplainEmailService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.utils.amazon.SqsUtils;
import com.cyberlink.cosmetic.utils.amazon.SqsUtils.NotificationType;

public class BounceAndComplainEmailServiceImpl extends AbstractService implements BounceAndComplainEmailService {

	protected AccountDao accountDao;

	static final String CRONEXPRESSION = "0 30 * * * ? *";
	public static Boolean isDebug = Boolean.FALSE;
	public static Boolean isReady = Boolean.TRUE;
	public static Boolean isRunning = Boolean.FALSE;
	public static Boolean isRunAll = Boolean.FALSE;
	int handleBounceMailMaxCount = 1000;
	int handleComplainMailMaxCount = 1000;

	@Override
	public void start() {
		isReady = Boolean.TRUE;
	}

	@Override
	public void stop() {
		isReady = Boolean.FALSE;
	}

	@Override
	public void setIsDebugMode() {
		isDebug = Boolean.TRUE;
	}

	@Override
	public void setIsNotDebugMode() {
		isDebug = Boolean.FALSE;
	}

	@Override
	@BackgroundJob(cronExpression = CRONEXPRESSION)
	public void exec() {
		handleBounceAndComplainEmail();
	}

	@Override
	public String getStatus() {
		return "BounceAndComplainEmailService "
				+ (isReady != null && (isReady == Boolean.TRUE) ? "is ready" : "isn't ready") + " and "
				+ (isRunning != null && (isRunning == Boolean.TRUE) ? "is running" : "isn't running");
	}

	@Override
	public Boolean IsDebugMode() {
		return isDebug;
	}

	@Override
	public Boolean IsReady() {
		return isReady;
	}

	@Override
	public Boolean IsRunning() {
		return isRunning;
	}

	private void handleBounceAndComplainEmail() {
		if (!Constants.getIsHandleBounceMail() && !isDebug)
			return;
		isRunning = Boolean.TRUE;
		if (!isReady) {
			logger.info("BounceAndComplainEmailService isn't running");
			isRunning = Boolean.FALSE;
			return;
		} else
			logger.info("BounceAndComplainEmailService is running");
		try {
			// handleInvalidEmailBySQS();
			handleInvalidEmailByFolder();

			isRunning = Boolean.FALSE;
			logger.info("BounceAndComplainEmailService completed");
		} catch (Exception e) {
			isRunning = Boolean.FALSE;
			logger.info("BounceAndComplainEmailService is fail, message:" + e.getMessage());
		}
	}

	private void handleInvalidEmailByFolder() {
		List<String> emailList = loadInvailidMailAdressByFile();
		int offset = 0;
		int limit = 100;
		while (offset < emailList.size()) {
			List<String> emailSubList = emailList.subList(offset, Math.min(emailList.size(), offset + limit));
			accountDao.updateStatusByEmail(emailSubList, AccountMailStatus.INVALID);
			offset += limit;
		}
	}

	private List<String> loadInvailidMailAdressByFile() {
		List<String> mailList = new ArrayList<String>();
		String handleBounceMailPath = Constants.getHandleBounceMailPath();
		File rootFile = new File(handleBounceMailPath);
		if (!rootFile.exists()) {
			return mailList;
		}
		File[] mailFiles = rootFile.listFiles();
		if (mailFiles.length == 0)
			return mailList;
		FileReader fr = null;
		BufferedReader br = null;
		for (File mailFile : mailFiles) {
			if (mailFile.getName().split("-").length > 2) {
				continue;
			}
			try {
				fr = new FileReader(mailFile);
				br = new BufferedReader(fr);
				String line;
				while ((line = br.readLine()) != null) {
					if (!mailList.contains(line) && StringUtils.isNoneBlank(line))
						mailList.add(line);
				}
				br.close();
			} catch (Exception e) {
				logger.error(
						"BounceAndComplainEmailServiceImpl loadInvailidMailAdress fail. Message : " + e.getMessage());
			} finally {
				File newfile = new File(mailFile.getAbsolutePath() + "-Completed");
				mailFile.renameTo(newfile);
				try {
					br.close();
				} catch (IOException e) {
					logger.error("BounceAndComplainEmailServiceImpl loadInvailidMailAdress bufferedReader ocuur IOException. Message : " + e.getMessage());
				}
				try {
					fr.close();
				} catch (IOException e) {
					logger.error("BounceAndComplainEmailServiceImpl loadInvailidMailAdress fileReader ocuur IOException. Message : " + e.getMessage());
				}
			}
		}
		return mailList;
	}

	/*
	 * Use file replaced the SQS service,So the function did not use.
	 */
	@SuppressWarnings("unused")
	private void handleInvalidEmailBySQS() {
		SqsUtils sqsUtils = new SqsUtils();
		// record all bounce and complain email
		Set<String> blockedEmailSet = new HashSet<String>();

		Boolean isLoop = Boolean.TRUE;
		Boolean isDeleteMessage = Boolean.FALSE;
		List<Message> tempAllDeleteMessageList = new ArrayList<Message>();
		List<String> tempAllDeleteMessageIDList = new ArrayList<String>();
		// Handle bounce email
		int handleBounceCount = 0;
		do {
			try {
				isDeleteMessage = Boolean.FALSE;
				List<Message> bounceMessagesList = sqsUtils.receiveMessageFromSQS(NotificationType.BOUNCE);
				Map<String, String> emailMap = sqsUtils.getMailsFromMessages(bounceMessagesList,
						NotificationType.BOUNCE);
				// check has duplicate Message
				for (Message bounceMessage : bounceMessagesList) {
					if (tempAllDeleteMessageIDList.contains(bounceMessage.getMessageId())) {
						isDeleteMessage = Boolean.TRUE;
					} else {
						tempAllDeleteMessageList.add(bounceMessage);
						tempAllDeleteMessageIDList.add(bounceMessage.getMessageId());
						blockedEmailSet.add(emailMap.get(bounceMessage.getMessageId()));
					}
				}
				if (tempAllDeleteMessageList.size() > 200
						|| (bounceMessagesList.size() == 0 && tempAllDeleteMessageList.size() > 0))
					isDeleteMessage = Boolean.TRUE;

				if (isDeleteMessage.booleanValue()) {
					int offset = 0;
					int limit = 10;
					do {
						List<Message> subDeleteMessageList = tempAllDeleteMessageList.subList(offset,
								Math.min((offset + limit), tempAllDeleteMessageList.size()));
						sqsUtils.batchDeleteMessagesToSQS(NotificationType.BOUNCE, subDeleteMessageList);
						offset += limit;
						if (offset > tempAllDeleteMessageList.size())
							break;
					} while (true);
					tempAllDeleteMessageList = new ArrayList<Message>();
					tempAllDeleteMessageIDList = new ArrayList<String>();
					isDeleteMessage = Boolean.FALSE;
				}
				handleBounceCount += bounceMessagesList.size();
				// If bounce queue doesn't have email or handleBounceCount >
				// handleBounceMailMaxCount
				if (bounceMessagesList.size() == 0 || handleBounceCount > handleBounceMailMaxCount) {
					isLoop = Boolean.FALSE;
				}
			} catch (Exception e) {
				isLoop = false;
			}
		} while (isLoop);

		// Handle complain email
		isLoop = Boolean.TRUE;
		isDeleteMessage = Boolean.FALSE;
		tempAllDeleteMessageList = new ArrayList<Message>();
		tempAllDeleteMessageIDList = new ArrayList<String>();
		int handleComplainCount = 0;
		do {
			try {
				List<Message> complainMessagesList = sqsUtils.receiveMessageFromSQS(NotificationType.COMPLAIN);
				Map<String, String> emailMap = sqsUtils.getMailsFromMessages(complainMessagesList,
						NotificationType.COMPLAIN);
				// check has duplicate Message
				for (Message complainMessage : complainMessagesList) {
					if (tempAllDeleteMessageIDList.contains(complainMessage.getMessageId())) {
						isDeleteMessage = Boolean.TRUE;
					} else {
						tempAllDeleteMessageList.add(complainMessage);
						tempAllDeleteMessageIDList.add(complainMessage.getMessageId());
						blockedEmailSet.add(emailMap.get(complainMessage.getMessageId()));
					}
				}
				if (tempAllDeleteMessageList.size() > 200
						|| (complainMessagesList.size() == 0 && tempAllDeleteMessageList.size() > 0))
					isDeleteMessage = Boolean.TRUE;

				if (isDeleteMessage.booleanValue()) {
					int offset = 0;
					int limit = 10;
					do {
						List<Message> subDeleteMessageList = tempAllDeleteMessageList.subList(offset,
								Math.min((offset + limit), tempAllDeleteMessageList.size()));
						sqsUtils.batchDeleteMessagesToSQS(NotificationType.COMPLAIN, subDeleteMessageList);
						offset += limit;
						if (offset > tempAllDeleteMessageList.size()) {
							break;
						}
					} while (true);
					tempAllDeleteMessageList = new ArrayList<Message>();
					tempAllDeleteMessageIDList = new ArrayList<String>();
					isDeleteMessage = Boolean.FALSE;
				}
				handleComplainCount += complainMessagesList.size();
				// If complain queue doesn't have email or handleComplainCount >
				// handleComplainMailMaxCount
				if (complainMessagesList.size() == 0 || handleComplainCount > handleComplainMailMaxCount) {
					isLoop = Boolean.FALSE;
				}
			} catch (Exception e) {
				isLoop = false;
			}
		} while (isLoop);
		for (String blockedEmail : blockedEmailSet) {
			if (blockedEmail == null || blockedEmail == "")
				continue;
			List<Account> accountList = accountDao.findByEmail(blockedEmail);
			for (Account account : accountList) {
				account.setMailStatus(AccountMailStatus.INVALID);
				accountDao.update(account);
			}
		}
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
}
