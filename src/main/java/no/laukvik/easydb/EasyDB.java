package no.laukvik.easydb;

import no.laukvik.easydb.exception.NoEntityException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.*;

public class EasyDB {

    String url = "jdbc:derby:/Users/laukvik/Db/Derby;create=true";
    String user = "";
    String password = "";
    Connection connection;
    DatabaseType databaseType = DatabaseType.ApacheDerby;
    private final static String SQL_SELECT = "SELECT * FROM ?";

    public EasyDB() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseModel getModel(Class klass) {
        if (klass.isAnnotationPresent(DatabaseModel.class)) {
            return ((DatabaseModel) klass.getAnnotation(DatabaseModel.class));
        }
        throw new NoEntityException("Class is no valid entity: " + klass.getName());
    }

    public static String getTableName(Class klass) {
        return getModel(klass).table();
    }

    public void createModel(Class klass) throws SQLException {
        Statement st = getConnection().createStatement();
        st.executeUpdate(DDL.createTable(klass));
    }

    public void deleteModel(Class klass) throws SQLException {
        Statement st = getConnection().createStatement();
        st.executeUpdate(DDL.deleteTable(klass));
    }

    public Object getPrimaryKeyValue(Object object) {
        DatabaseModel model = getModel(object.getClass());
        HashMap<String, Object> map = Mapper.extractData(object);
        return map.get(model.id());
    }

    public Object createNew(Class klass) {
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new NoEntityException(e);
        } catch (IllegalAccessException e) {
            throw new NoEntityException(e);
        } catch (InvocationTargetException e) {
            throw new NoEntityException(e);
        } catch (NoSuchMethodException e) {
            throw new NoEntityException(e);
        }
    }

    private void populateRs(DatabaseModel model, Object object, HashMap<String, Field> map, ResultSet rs) throws SQLException {
        try {
            for (String columnName : map.keySet()) {
                Field f = map.get(columnName);
                Object v = f.get(object);
                boolean isAutoIncrement = model.auto() && model.id().equalsIgnoreCase(columnName);
                if (!isAutoIncrement) {
                    if (f.getType() == Integer.class) {
                        rs.updateInt(columnName, (Integer) v);
                    }
                    if (f.getType() == Double.class) {
                        rs.updateDouble(columnName, (Double) v);
                    }
                    if (f.getType() == Float.class) {
                        rs.updateFloat(columnName, (Float) v);
                    }
                    if (f.getType() == String.class) {
                        rs.updateString(columnName, (String) v);
                    }
                    if (f.getType() == Date.class) {
                        Date d = (Date) v;
                        rs.updateTimestamp(columnName, new java.sql.Timestamp(d.getTime()));
                    }
                    if (f.getType() == LocalDate.class) {
                        LocalDate d = (LocalDate) v;
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, d.getYear());
                        c.set(Calendar.MONTH, d.getMonthValue() - 1);
                        c.set(Calendar.DAY_OF_MONTH, d.getDayOfMonth());
                        rs.updateDate(columnName, new java.sql.Date(c.getTime().getTime()));
                    }
                    if (f.getType() == Boolean.class || f.getType() == boolean.class) {
                        rs.updateBoolean(columnName, (Boolean) v);
                    }
                    if (f.getType().isEnum()) {
                        Enum e = (Enum) v;
                        rs.updateString(columnName, e.name());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void add(Object object) throws SQLException {
        DatabaseModel model = getModel(object.getClass());
        String tableName = model.table();
        HashMap<String, Field> map = Mapper.extractFields(object.getClass());
        Field f = map.get(model.id());
        f.setAccessible(true);
        String sql = "SELECT * FROM " + tableName;
        PreparedStatement st = getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = st.executeQuery();
        rs.moveToInsertRow();
        populateRs(getModel(object.getClass()), object, map, rs);
        rs.insertRow();
        rs.last();
        Integer id = rs.getInt(model.id());
        try {
            f.set(object, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void update(Object instance) throws SQLException {
        DatabaseModel model = getModel(instance.getClass());
        String tableName = model.table();
        HashMap<String, Field> map = Mapper.extractFields(instance.getClass());
        String sql = "SELECT * FROM " + tableName + " WHERE ID=?";
        PreparedStatement st = getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        st.setObject(1, getPrimaryKeyValue(instance));
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            populateRs(getModel(instance.getClass()), instance, map, rs);
            rs.updateRow();
        }

    }

    public void remove(Object instance) throws SQLException {
        String tableName = getTableName(instance.getClass());
        String sql = "DELETE FROM " + tableName + " WHERE ID=?";
        PreparedStatement st = getConnection().prepareStatement(sql);
        st.setObject(1, getPrimaryKeyValue(instance));
        st.executeUpdate();
    }

    public void removeAll(Class klass) throws SQLException {
        String tableName = getTableName(klass);
        PreparedStatement st = getConnection().prepareStatement("DELETE FROM " + tableName);
        st.executeUpdate();
    }

    public Optional findById(Object id, Class instance) throws SQLException {
        String tableName = getTableName(instance);
        HashMap<String, Field> map = Mapper.extractFields(instance);
        String sql = "SELECT * FROM " + tableName + " WHERE ID=?";
        PreparedStatement st = getConnection().prepareStatement(sql);
        st.setObject(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            try {
                return Optional.of(populateObject(instance, rs, map));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private Object populateObject(Class k, ResultSet rs, HashMap<String, Field> map) throws SQLException, IllegalAccessException {
        Object o = createNew(k);
        for (String columnName : map.keySet()) {
            Field f = map.get(columnName);
            if (f.getType() == String.class) {
                f.set(o, rs.getString(columnName));
            }
            if (f.getType() == Integer.class) {
                f.set(o, rs.getInt(columnName));
            }
            if (f.getType() == Float.class) {
                f.set(o, rs.getFloat(columnName));
            }
            if (f.getType() == BigDecimal.class) {
                f.set(o, rs.getBigDecimal(columnName));
            }
            if (f.getType() == Boolean.class) {
                f.set(o, rs.getBoolean(columnName));
            }
            if (f.getType() == Date.class) {
                f.set(o, new Date(rs.getTimestamp(columnName).getTime()));
            }
            if (f.getType() == LocalDate.class) {
                f.set(o, mapLocalDate(rs.getDate(columnName)));
            }
            if (f.getType().isEnum()) {
                String s = rs.getString(columnName);
                Class klass = f.getType();
                f.set(o, Enum.valueOf(klass, s));
            }
        }
        return o;
    }

    public static LocalDate mapLocalDate(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public <K> List findAll(Class<K> k) throws SQLException {
        String tableName = getTableName(k);
        HashMap<String, Field> map = Mapper.extractFields(k);
        String sql = "SELECT * FROM " + tableName;
        PreparedStatement st = getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = st.executeQuery();
        List<K> list = new ArrayList<K>();
        while (rs.next()) {
            try {
                list.add((K) populateObject(k, rs, map));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public List findByQuery(Query query) throws SQLException {
        HashMap<String, Field> map = Mapper.extractFields(query.getQueryClass());
        PreparedStatement st = query.getPreparedStatement(getConnection());
        ResultSet rs = st.executeQuery();
        List list = new ArrayList();
        while (rs.next()) {
            try {
                list.add(populateObject(query.getQueryClass(), rs, map));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}