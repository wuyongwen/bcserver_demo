package com.cyberlink.cosmetic.action.backend.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.FreeSampleMailService;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventAttr;
import com.cyberlink.cosmetic.modules.event.model.EventType;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.mail.service.MailFreeSampleCustomerService;

public class FreeSampleMailServiceImpl extends AbstractService implements
		FreeSampleMailService {

	private BrandEventDao brandEventDao;
	private EventUserDao eventUserDao;
	private MailFreeSampleCustomerService mailFreeSampleCustomerService;

	static final int TIMERANGE = 24;
	static final String WARNING_MESSAGE_RECEIVER = "Roy_Lee@PerfectCorp.com";
	static final String CRONEXPRESSION = "0 0 3,15 * * ? *";

	static private Boolean isRunning = Boolean.TRUE;

	@Override
	public void start() {
		isRunning = Boolean.TRUE;
	}

	@Override
	public void stop() {
		isRunning = Boolean.FALSE;
	}

	@Override
	public String getStatus() {
		if (!isRunning)
			return "FreeSampleMailService isn't running";
		else
			return "FreeSampleMailService is running";
	}

	@Override
	@BackgroundJob(cronExpression = CRONEXPRESSION)
	public void exec() {
		if (!isRunning) {
			logger.info("FreeSampleMailService isn't running");
			return;
		} else
			logger.info("FreeSampleMailService is running");

		Long offset = Long.valueOf(0);
		Long limit = Long.valueOf(100);

		do {
			PageResult<BrandEvent> brandEvents = brandEventDao
					.findBrandEventByType(null, ServiceType
							.getSendCustomerType(), EventType
							.getSendCustomerType(),
							new BlockLimit(offset.intValue(), limit.intValue()));
			if (brandEvents.getResults().size() <= 0)
				break;

			for (BrandEvent event : brandEvents.getResults()) {
				if (event.getEventAttrJNode().getIsSent())
					continue;

				try {
					// check companySendDate
					Date sendTime = event.getEventAttrJNode()
							.getCompanySendDate();
					if (sendTime == null) {
						sendWarningMail(event, "CompanySendDate is null");
						continue;
					}
					String companyEmail = event.getEventAttrJNode()
							.getCompanyEmail();
					if (companyEmail == null || companyEmail.isEmpty()) {
						sendWarningMail(event, "CompanyEmail is null");
						continue;
					}

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.HOUR, +TIMERANGE);
					Date compareTime = cal.getTime();
					Long diff = (compareTime.getTime() - sendTime.getTime())
							/ (60 * 60 * 1000);

					if (diff >= 0 && diff < TIMERANGE) { // send mail
						Boolean bUpdate = Boolean.FALSE;
						File file = new File(Constants.getFreeSamplePath()
								+ String.format("/%s.xls", event.getId()
										.toString()));
						if (file.exists()) {
							mailFreeSampleCustomerService.send(event.getId());
							bUpdate = Boolean.TRUE;
						} else if (EventType.LimitProdNum.equals(event
								.getEventType())) {
							if (optputWinnerList(event)) {
								mailFreeSampleCustomerService.send(event
										.getId());
								bUpdate = Boolean.TRUE;
							} else
								sendWarningMail(event,
										"[LimitProdNum] output file fail");
						} else
							sendWarningMail(event,
									"[SelectUser] file not exists");

						if (bUpdate) {
							EventAttr attr = event.getEventAttrJNode();
							attr.setIsSent(Boolean.TRUE);
							event.setEventAttrJNode(attr);
							brandEventDao.update(event);
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					sendWarningMail(event, e.getMessage());

					// rollback isSent flag
					if (event.getEventAttrJNode().getIsSent()) {
						EventAttr attr = event.getEventAttrJNode();
						attr.setIsSent(Boolean.FALSE);
						event.setEventAttrJNode(attr);
						brandEventDao.update(event);
					}
					continue;
				}
			}

			offset += limit;
			if (offset > brandEvents.getTotalSize())
				break;
		} while (true);

	}

	private Boolean optputWinnerList(BrandEvent event) {
		if (event == null || event.getId() == null)
			return false;

		Long eventId = event.getId();
		ReceiveType receiveType = event.getReceiveType();
		WritableWorkbook workbook = null;
		File file = new File(Constants.getFreeSamplePath()
				+ String.format("/%s.xls", eventId.toString()));
		try {
			workbook = Workbook.createWorkbook(file);
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				workbook.close();
				if (file.exists())
					file.delete();
			} catch (Exception e1) {
				logger.error(e1.getMessage());
			}
			return false;
		}

		try {
			// optput winner list.
			WritableSheet sheet = workbook.createSheet("Winner List", 0);
			sheet.addCell(new Label(0, 0, "Id"));
			sheet.addCell(new Label(1, 0, "Display Name"));
			sheet.addCell(new Label(2, 0, "Real Name"));
			sheet.addCell(new Label(3, 0, "Birthday"));
			sheet.addCell(new Label(4, 0, "Phone"));
			sheet.addCell(new Label(5, 0, "Email"));
			if (ReceiveType.Home.equals(receiveType)) {
				sheet.addCell(new Label(6, 0, "User Address"));
			} else if (ReceiveType.Store.equals(receiveType)) {
				sheet.addCell(new Label(6, 0, "Store Location"));
				sheet.addCell(new Label(7, 0, "Store Name"));
				sheet.addCell(new Label(8, 0, "Store Address"));
			}

			int offset = 0;
			int limit = 100;
			do {
				PageResult<EventUser> eventUsers = eventUserDao
						.findEventUserByEventIdAndStatus(
								Arrays.asList(eventId), Arrays.asList(
										EventUserStatus.Selected,
										EventUserStatus.Redeemed),
								new BlockLimit(offset, limit));
				if (eventUsers.getResults().size() <= 0)
					break;

				// todo
				int num = offset;
				for (EventUser eventUser : eventUsers.getResults()) {
					num++;
					Long id = eventUser.getId();
					String displayName = eventUser.getDisplayName();
					String realName = eventUser.getName();
					String birthday = eventUser.getBirthDayString();
					String phone = eventUser.getPhone();
					String mail = eventUser.getMail();
					if (id != null)
						sheet.addCell(new Label(0, num, id.toString()));
					if (displayName != null)
						sheet.addCell(new Label(1, num, displayName));
					if (realName != null)
						sheet.addCell(new Label(2, num, realName));
					if (birthday != null)
						sheet.addCell(new Label(3, num, birthday));
					if (phone != null)
						sheet.addCell(new Label(4, num, phone));
					if (mail != null)
						sheet.addCell(new Label(5, num, mail));
					if (ReceiveType.Home.equals(receiveType)) {
						String userAddress = eventUser.getUserAddress();
						if (userAddress != null)
							sheet.addCell(new Label(6, num, userAddress));
					} else if (ReceiveType.Store.equals(receiveType)) {
						String storeLocation = eventUser.getStoreLocation();
						if (storeLocation != null)
							sheet.addCell(new Label(6, num, storeLocation));
						String storeName = eventUser.getStoreName();
						if (storeName != null)
							sheet.addCell(new Label(7, num, storeName));
						String storeAddress = eventUser.getStoreAddress();
						if (storeAddress != null)
							sheet.addCell(new Label(8, num, storeAddress));
					}
				}

				offset += limit;
				if (offset > eventUsers.getTotalSize())
					break;
			} while (true);
			workbook.write();
			workbook.close();
			return true;

		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				workbook.close();
				if (file.exists())
					file.delete();
			} catch (Exception e1) {
				logger.error(e1.getMessage());
			}
			return false;
		}
	}

	private void sendWarningMail(BrandEvent event, String content) {
		String subject = String.format(
				"Event ID: %s send mail to customer fail", event.getId()
						.toString())
				+ " - " + Constants.getWebsiteDomain();
		if (content == null)
			content = "NullPointerException";
		mailFreeSampleCustomerService.directSend(WARNING_MESSAGE_RECEIVER,
				subject, content);
	}

	public BrandEventDao getBrandEventDao() {
		return brandEventDao;
	}

	public void setBrandEventDao(BrandEventDao brandEventDao) {
		this.brandEventDao = brandEventDao;
	}

	public EventUserDao getEventUserDao() {
		return eventUserDao;
	}

	public void setEventUserDao(EventUserDao eventUserDao) {
		this.eventUserDao = eventUserDao;
	}

	public MailFreeSampleCustomerService getMailFreeSampleCustomerService() {
		return mailFreeSampleCustomerService;
	}

	public void setMailFreeSampleCustomerService(
			MailFreeSampleCustomerService mailFreeSampleCustomerService) {
		this.mailFreeSampleCustomerService = mailFreeSampleCustomerService;
	}

}