package com.cyberlink.cosmetic.modules.notify.dao;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.notify.model.Notify;

public interface NotifyDao extends GenericDao<Notify, Long>{
	PageResult<Notify> findNotifyByType(Long userId, List<String> notifyType, Long offset, Long limit);
	Boolean checkUnReadNotifyWithType(Long userId, List<String> notifyType);
	Notify findNotifyGroup(Long receiverId, Long refId, String notifyType, String groupType);
	Notify findNotifyGroupByFirstCreated(Long receiverId, Long groupId, String notifyType, String groupType);
	Map<Long, Notify> findNotifyGroups(Set<Long> receiverId, Long refId, String notifyType, String groupType);
	Map<Long, Notify> findNotifyGroupsByFirstCreated(Set<Long> receiverId, Long groupId, String notifyType, String groupType);
	Notify findNewNotifyByType(Long receiverId, List<String> notifyType);
	int setIsReaded(Long receiverId, Long time, String type);
	Boolean findIsGrouped(Long receiverId, Long refId, Long senderId, String notifyType);
	void updateSenderAvatar(Long userId, Long avatarId);
	PageResult<Long> findRefIdByNotify(Notify notify, Long prevId, Long offset, Long limit);
	Long findPrevGroupId(Notify notify);
	
	List<Notify> findUnSendNotify(Long limit, String notifyType);
	List<Notify> findUnSendNotifyByUser(Long limit, String notifyType, Set<Long> userIds);
	void updateUserSendTarget(Long userId, String target, List<String> notifyTypes);
	Integer deleteOldNotify();
	void deleteFreeSampleNotify(Long refId);
	void realDelete();
	void updateByDeleteComment(Long postId, Long receiverId, Long senderId, String text);
	void updateByDeletePost(Long postId);
	void deleteById(Long notifyId);
	void batchInsert(List<Notify> list);
	Boolean batchDelete(List<Long> list);

}
