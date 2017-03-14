package com.cyberlink.cosmetic.modules.notify.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;

public interface NotifyService {
	void addFriendNotifyByType(String notifyType, Long senderId, Long refId, String content);
	void addFriendNotifyByType(String notifyType, Long senderId, Long refId, String content, String iconUrl);
	void addNotifyByType(String notifyType, Long reseiverId, Long senderId, Long refId, String content);
	void addNotifyByType(String notifyType, Long reseiverId, Long senderId, Long refId, String content, String iconUrl);
	void updateByDeleteComment(Long commentId);
	void updateNotifyDevice(Long userId, String apnsToken, String apnsType, String uuid, String app);
	void updateSenderAvatar(Long userId, Long avatarId);
	void sendEventNotify(List<EventUser> eventUserList, BrandEvent brandEvent);
	void sendSOWNotify(List<Long> userIds, String locale);
	void deleteOldNotify();
	void setNotifyIsRead(final Long receiverId,final Long time,final String type);
	
    Map<Integer, Boolean> getWorkerStatus();
    void wakeUpWorker();
    Integer getTaskCount();
    void clearAllTask();
    Long getDropCount();
    void setWriteEvent(Boolean bWrite);
    Boolean getWriteEvent();
    
    void doGroupYouNotify(Date checkTime);

}
