package com.cyberlink.core.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.cyberlink.core.model.BaseEntity;

public class BackgroundJobSessionInterceptor implements MethodInterceptor {
    private final SessionFactory sessionFactory;

    public BackgroundJobSessionInterceptor(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            final Session session = beforeInvocation();
            refreshHibernateObjects(session, invocation.getArguments());
            return invocation.proceed();
        } finally {
            afterInvocation();
        }
    }

    private void refreshHibernateObjects(final Session session,
            final Object[] arguments) {
        if (arguments != null) {
            for (final Object o : arguments) {
                if (o instanceof BaseEntity<?>) {
                    session.buildLockRequest(LockOptions.NONE).lock(o);
                }
            }
        }
    }

    private void afterInvocation() {
        final SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
                .unbindResource(sessionFactory);
        final Session s = sessionHolder.getSession();
        s.flush();
        SessionFactoryUtils.closeSession(s);
    }

    private Session beforeInvocation() {
        final Session session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        TransactionSynchronizationManager.bindResource(sessionFactory,
                new SessionHolder(session));
        return session;
    }

}
