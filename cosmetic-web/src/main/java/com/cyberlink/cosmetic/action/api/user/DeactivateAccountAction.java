package com.cyberlink.cosmetic.action.api.user;

import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

@UrlBinding("/api/user/deactivate-account.action")
public class DeactivateAccountAction extends AbstractAction {
	@SpringBean("user.MemberDao")
	private MemberDao memberDao;

	@SpringBean("user.AccountDao")
	protected AccountDao accountDao;

	@SpringBean("core.jdbcTemplate")
	private TransactionTemplate transactionTemplate;

	private Long memberId;

	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;
		
		if (memberId == null)
			return new ErrorResolution(ErrorDef.InvalidAccount);
		if (!memberDao.exists(memberId))
			return new ErrorResolution(ErrorDef.InvalidAccount);

		try {
			Boolean response = transactionTemplate
					.execute(new TransactionCallback<Boolean>() {
						@Override
						public Boolean doInTransaction(TransactionStatus status) {
							try {
								Member userMember = memberDao
										.findByMemberId(Long.valueOf(memberId));
								Account userAccount = userMember.getAccount();
								if (userAccount != null) {
									List<Session> sessionList = sessionDao
											.findByUserId(userAccount
													.getUserId());
									for (Session session : sessionList) {
										session.setStatus(SessionStatus.Invalied);
										sessionDao.update(session);
									}
									userAccount.setAccount(null);
									accountDao.update(userAccount);
								}
								userMember.setIsDeleted(Boolean.TRUE);
								userMember = memberDao.update(userMember);
								return true;
							} catch (Exception e) {
								return false;
							}
						}
					});
			if (!response)
				return new ErrorResolution(ErrorDef.InvalidAccount);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ErrorResolution(ErrorDef.ServerBusy);
		}

		return success();
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
}