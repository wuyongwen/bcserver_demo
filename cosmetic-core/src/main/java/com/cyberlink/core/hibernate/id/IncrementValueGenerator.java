package com.cyberlink.core.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class IncrementValueGenerator implements IdentifierGenerator,
        Configurable {

    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
            CoreMessageLogger.class, IncrementValueGenerator.class.getName());

    @SuppressWarnings("rawtypes")
    private Class returnClass;
    private String sql;

    protected Long incrementValue = 1l;
    protected Integer[] initialValues = new Integer[] { 1, 2, 3, 4, 5, 6 };

    private IntegralDataTypeHolder previousValueHolder;

    public synchronized Serializable generate(SessionImplementor session,
            Object object) throws HibernateException {
        if (sql != null) {
            initializePreviousValueHolder(session);
        }
        Number n = previousValueHolder.makeValueThenAdd(incrementValue);
        return n.longValue() + getRandomInitialValue();
    }

    private Integer getRandomInitialValue() {
        final int random = RandomUtils.nextInt(initialValues.length);
        return initialValues[random];
    }

    public void configure(Type type, Properties params, Dialect dialect)
            throws MappingException {
        returnClass = type.getReturnedClass();

        ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params
                .get(PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER);

        String column = params.getProperty("column");
        if (column == null) {
            column = params.getProperty(PersistentIdentifierGenerator.PK);
        }
        column = dialect.quote(normalizer.normalizeIdentifierQuoting(column));

        String tableList = params.getProperty("tables");
        if (tableList == null) {
            tableList = params
                    .getProperty(PersistentIdentifierGenerator.TABLES);
        }
        String[] tables = StringHelper.split(", ", tableList);

        final String schema = dialect.quote(normalizer
                .normalizeIdentifierQuoting(params
                        .getProperty(PersistentIdentifierGenerator.SCHEMA)));
        final String catalog = dialect.quote(normalizer
                .normalizeIdentifierQuoting(params
                        .getProperty(PersistentIdentifierGenerator.CATALOG)));
        final String incrementValueStr = params.getProperty("incrementValue");
        if (StringUtils.isNotBlank(incrementValueStr)) {
            try {
                incrementValue = Long.parseLong(incrementValueStr);
            } catch (Exception e) {
                //
            }
        }

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < tables.length; i++) {
            final String tableName = dialect.quote(normalizer
                    .normalizeIdentifierQuoting(tables[i]));
            if (tables.length > 1) {
                buf.append("select max(").append(column)
                        .append(") as mx from ");
            }
            buf.append(Table.qualify(catalog, schema, tableName));
            if (i < tables.length - 1) {
                buf.append(" union ");
            }
        }
        if (tables.length > 1) {
            buf.insert(0, "( ").append(" ) ids_");
            column = "ids_.mx";
        }

        sql = "select max(" + column + ") from " + buf.toString();
    }

    private void initializePreviousValueHolder(SessionImplementor session) {
        previousValueHolder = IdentifierGeneratorHelper
                .getIntegralDataTypeHolder(returnClass);

        LOG.debugf("Fetching initial value: %s", sql);
        try {
            PreparedStatement st = session.getTransactionCoordinator()
                    .getJdbcCoordinator().getStatementPreparer()
                    .prepareStatement(sql);
            try {
                ResultSet rs = st.executeQuery();
                try {
                    if (rs.next()) {
                        final Long l = rs.getLong(1);
                        previousValueHolder
                                .initialize(((l / incrementValue) + 2)
                                        * incrementValue);

                    } else {
                        previousValueHolder.initialize(incrementValue);
                    }
                    sql = null;
                    if (LOG.isDebugEnabled()) {
                        LOG.debugf("First free id: %s",
                                previousValueHolder.makeValue());
                    }
                } finally {
                    rs.close();
                }
            } finally {
                st.close();
            }
        } catch (SQLException sqle) {
            throw session
                    .getFactory()
                    .getSQLExceptionHelper()
                    .convert(
                            sqle,
                            "could not fetch initial value for increment generator",
                            sql);
        }
    }
}
