package com.keeper.util.serializable;

/*
 * Created by @GoodforGod on 17.04.2017.
 */

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

/**
 * Default Comment
 */
public class StringArrayUserType implements UserType {

    private static final int[] SQL_TYPES = { Types.ARRAY };

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (String[]) this.deepCopy(value);
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null)
            return y == null;

        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {

        if (resultSet.wasNull())
            return null;
        if(resultSet.getArray(names[0]) == null)
            return new Integer[0];

        return resultSet.getArray(names[0]).getArray();
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        Connection connection = statement.getConnection();
        if (value == null)
            statement.setNull(index, SQL_TYPES[0]);
         else {
            String[] castObject = (String[]) value;
            Array array = connection.createArrayOf("varchar", castObject);
            statement.setArray(index, array);
        }
    }

    @Override
    public Object replace(Object original, Object target, Object owner)       throws HibernateException {
        return original;
    }

    @Override
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.ARRAY };
    }
}
